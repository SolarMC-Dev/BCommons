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

package me.bradleysteele.commons.resource.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.bradleysteele.commons.gson.StaticGson;
import me.bradleysteele.commons.resource.AbstractResource;
import me.bradleysteele.commons.resource.ResourceHandler;
import me.bradleysteele.commons.resource.ResourceReference;
import me.bradleysteele.commons.resource.ResourceSection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Bradley Steele
 */
public class ResourceJson extends AbstractResource {

    private JsonObject root = new JsonObject();

    public ResourceJson(File file, ResourceReference reference, ResourceHandler handler) {
        super(file, reference, handler);
    }

    public ResourceJson(ResourceJson resource, JsonObject root) {
        super(resource.getFile(), resource.getReference(), resource.getHandler());
        this.root = root;
    }

    @Override
    public JsonObject getConfiguration() {
        return root;
    }

    @Override
    public void setConfiguration(Object configuration) {
        if (configuration instanceof JsonObject) {
            this.root = (JsonObject) configuration;
        }
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public boolean contains(String path) {
        return root.has(path);
    }

    @Override
    public ResourceSection createSection(String name) {
        JsonObject section = new JsonObject();
        root.add(name, section);

        return new ResourceJson(this, section);
    }

    @Override
    public String getName() {
        return root.getAsString();
    }

    @Override
    public String getCurrentPath() {
        return root.toString();
    }

    @Override
    public ResourceSection getRoot() {
        return this;
    }

    @Override
    public ResourceSection getParent() {
        return this;
    }

    @Override
    public <T> T get(String path, Class<T> type, T def) {
        JsonElement element = root.get(path);

        if (element == null) {
            return def;
        }

        return StaticGson.getGson().fromJson(element, type);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return root.entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public String getString(String path, String def) {
        return contains(path) ? root.get(path).getAsString() : def;
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return contains(path) ? root.get(path).getAsBoolean() : def;
    }

    @Override
    public byte getByte(String path, byte def) {
        return contains(path) ? root.get(path).getAsByte() : def;
    }

    @Override
    public char getChar(String path, char def) {
        return contains(path) ? root.get(path).getAsCharacter() : def;
    }

    @Override
    public short getShort(String path, short def) {
        return contains(path) ? root.get(path).getAsShort() : def;
    }

    @Override
    public int getInt(String path, int def) {
        return contains(path) ? root.get(path).getAsInt() : def;
    }

    @Override
    public long getLong(String path, long def) {
        return contains(path) ? root.get(path).getAsLong() : def;
    }

    @Override
    public float getFloat(String path, float def) {
        return contains(path) ? root.get(path).getAsFloat() : def;
    }

    @Override
    public double getDouble(String path, double def) {
        return contains(path) ? root.get(path).getAsDouble() : def;
    }

    @Override
    public <T> List<T> getList(String path, Class<T> clazz) {
        return StaticGson.getGson().fromJson(root.getAsJsonArray(path), new TypeToken<ArrayList<T>>(){}.getType());
    }

    @Override
    public ResourceSection getSection(String path) {
        return new ResourceJson(this, root.getAsJsonObject(path));
    }

    @Override
    public void set(String path, Object object) {
        if (object instanceof ResourceJson) {
            set(path, ((ResourceJson) object).getConfiguration());
        } else if (object instanceof JsonObject) {
            root.add(path, (JsonObject) object);
        } else if (object == null) {
            root.add(path, JsonNull.INSTANCE);
        } else {
            root.add(path, StaticGson.getGson().toJsonTree(object));
        }
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
