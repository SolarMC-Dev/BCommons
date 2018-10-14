/*
 * Copyright 2018 Bradley Steele
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.bradleysteele.commons.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Bradley Steele
 */
public class OfflinePlayers {

    private static final JSONParser parser = new JSONParser();

    private static final String ENDPOINT_SESSIONSERVER = "https://sessionserver.mojang.com/session/minecraft/profile/%s";
    private static final String ENDPOINT_PROFILES_NAMES = "https://api.mojang.com/user/profiles/%s/names";
    private static final String ENDPOINT_PROFILES = "https://api.mojang.com/users/profiles/minecraft/%s";

    private static final LoadingCache<String, UUID> uuidCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(new CacheLoader<String, UUID>() {

                @Override
                public UUID load(String name) throws Exception {
                    JSONObject object = (JSONObject) JSONValue.parseWithException(getResponseAsString(String.format(ENDPOINT_PROFILES, name)));
                    return UUID.fromString(object.get("id").toString().replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})","$1-$2-$3-$4-$5"));
                }
            });

    private static final LoadingCache<UUID, String> nameCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(new CacheLoader<UUID, String>() {

                   @Override
                   public String load(UUID uuid) throws Exception {
                       // Dashes have to be removed from the UUID when passing it
                       // through Mojang API URLs.
                       JSONArray array = (JSONArray) JSONValue.parseWithException(getResponseAsString(String.format(ENDPOINT_PROFILES_NAMES, uuid.toString().replace("-", ""))));

                       // Get the last object (current name) in the array.
                       JSONObject object = (JSONObject) JSONValue.parseWithException(array.get(array.size() - 1).toString());
                       return object.get("name").toString();
                   }
               });

    public static UUID getUUID(String name) {
        try {
            return uuidCache.get(name);
        } catch (ExecutionException e) {
            // Ignored
        }

        return null;
    }

    /**
     * Attempts to retrieve the latest name associated with the
     * provided unique id. Results are cached for 10 minutes.
     * <p>
     * Fallback will be returned if {@code nameCache#get} fails to
     * execute (api down, invalid uuid, etc).
     *
     * @param uuid     player's unique id.
     * @param fallback fallback name to return if retrieving failed.
     * @return the players name or {@param fallback} if it could
     *         not be retrieved.
     */
    public static String getName(UUID uuid, String fallback) {
        String name = Players.getOfflinePlayer(uuid).getName();

        // If the player has never joined the server, they won't have
        // a userdata file, and null returned.
        if (name != null) {
            return name;
        }

        try {
            return nameCache.get(uuid);
        } catch (ExecutionException e) {
            // Ignored
        }

        return fallback;
    }

    public static String getName(UUID uuid) {
        return getName(uuid, null);
    }

    public static String getSkinURL(UUID uuid) {
        try {
            JSONObject object = (JSONObject) JSONValue.parseWithException(IOUtils.toString(new URL(String.format(ENDPOINT_SESSIONSERVER, uuid.toString().replace("-", "")))));
            JSONArray array = (JSONArray) object.get("properties");
            JSONObject properties = (JSONObject) array.get(array.size() - 1);

            // texture
            JSONObject value = (JSONObject) parser.parse(new String(Base64.decodeBase64(properties.get("value").toString().getBytes())));
            JSONObject textures = (JSONObject) value.get("textures");
            JSONObject skin = (JSONObject) textures.get("SKIN");

            return skin.get("url").toString();
        } catch (ParseException | IOException e) {
            // Ignored
        }

        return null;
    }

    private static String getResponseAsString(URL url) throws IOException {
        return IOUtils.toString(url);
    }

    private static String getResponseAsString(String url) throws IOException {
        return getResponseAsString(new URL(url));
    }
}