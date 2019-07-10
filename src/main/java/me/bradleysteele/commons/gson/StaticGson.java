/*
 * Copyright 2019 Bradley Steele
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

package me.bradleysteele.commons.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import me.bradleysteele.commons.gson.adapter.ItemStackAdapter;
import me.bradleysteele.commons.gson.adapter.ItemStackArrayAdapter;
import me.bradleysteele.commons.gson.adapter.LocationAdapter;
import me.bradleysteele.commons.gson.adapter.UUIDAdapter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author Bradley Steele
 */
public final class StaticGson {

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeAdapter(ItemStack[].class, new ItemStackArrayAdapter())
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .registerTypeAdapter(UUID.class, new UUIDAdapter());

    private static Gson GSON = GSON_BUILDER.create();

    private static Gson GSON_PRETTY = GSON_BUILDER
            .setPrettyPrinting()
            .create();

    public static final JsonParser JSON_PARSER = new JsonParser();

    private StaticGson() {}

    // Raw Gson

    /**
     * Raw Gson object, does not include default type adapters.
     */
    private static final Gson RAW_GSON = new Gson();

    public static Gson getRawGson() {
        return RAW_GSON;
    }

    /**
     * Raw Gson object with pretty printing, does not include default
     * type adapters.
     */
    public static final Gson RAW_GSON_PRETTY = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static Gson getRawGsonPretty() {
        return RAW_GSON_PRETTY;
    }

    // BCommons Gson

    public static Gson getGson() {
        return GSON;
    }

    public static Gson getGsonPretty() {
        return GSON_PRETTY;
    }

    public static <T> void registerTypeAdapter(Class<T> clazz, GsonAdapter<T> adapter) {
        GSON_BUILDER.registerTypeAdapter(clazz, adapter);

        // Rebuild Gson
        GSON = GSON_BUILDER.create();
        // TODO rebuild pretty
    }

    // Parser

    public static JsonParser getJsonParser() {
        return JSON_PARSER;
    }
}