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

package me.bradleysteele.commons.resource;

import java.util.List;
import java.util.Set;

/**
 * @author Bradley Steele
 */
public interface ResourceSection extends ResourceItem {

    // Logic

    boolean isRoot();

    boolean contains(String path);

    ResourceSection createSection(String name);

    // Getters

    String getName();

    String getCurrentPath();

    ResourceSection getRoot();

    ResourceSection getParent();

    Object get(String path, Class<?> type, Object def);

    Set<String> getKeys(boolean deep);

    String getString(String path, String def);

    boolean getBoolean(String path, boolean def);

    byte getByte(String path, byte def);

    char getChar(String path, char def);

    short getShort(String path, short def);

    int getInt(String path, int def);

    long getLong(String path, long def);

    float getFloat(String path, float def);

    double getDouble(String path, double def);

    <T> List<T> getList(String path, Class<T> clazz);

    ResourceSection getSection(String path);

    // Setters

    void set(String path, Object object);

    // Defaults

    default Object get(String path, Class<?> type) {
        return get(path, type, null);
    }

    default Set<String> getKeys() {
        return getKeys(false);
    }

    default String getString(String path) {
        return getString(path, null);
    }

    default boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    default byte getByte(String path) {
        return getByte(path, (byte) 0);
    }

    default char getChar(String path) {
        return getChar(path, Character.MIN_VALUE);
    }

    default short getShort(String path) {
        return getShort(path, (short) 0);
    }

    default int getInt(String path) {
        return getInt(path, 0);
    }

    default long getLong(String path) {
        return getLong(path, 0L);
    }

    default float getFloat(String path) {
        return getFloat(path, 0.0F);
    }

    default double getDouble(String path) {
        return getDouble(path, 0.0D);
    }

    default List<String> getStringList(String path) {
        return getList(path, String.class);
    }

    default ResourceSection getOrCreateSection(String section) {
        return contains(section) ? getSection(section) : createSection(section);
    }
}