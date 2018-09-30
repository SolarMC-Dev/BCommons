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
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Bradley Steele
 */
public class OfflinePlayers {

    private static final LoadingCache<UUID, String> nameCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build(new CacheLoader<UUID, String>() {

                       @Override
                       public String load(UUID uuid) throws Exception {
                           // Dashes have to be removed from the UUID when passing it
                           // through Mojang API URLs.
                           String url = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";
                           JSONArray array = (JSONArray) JSONValue.parseWithException(IOUtils.toString(new URL(url)));

                           // Get the last object (current name) in the array.
                           JSONObject object = (JSONObject) JSONValue.parseWithException(array.get(array.size() - 1).toString());
                           return object.get("name").toString();
                       }
                   }
            );

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
}