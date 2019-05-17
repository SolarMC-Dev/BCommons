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

package me.bradleysteele.commons.nms;

import com.google.common.collect.Maps;
import me.bradleysteele.commons.util.reflect.Reflection;

import java.util.Map;

/**
 * @author Bradley Steele
 */
public class ClassCache {

    private final Map<String, Class<?>> cache = Maps.newHashMap();
    private final String prefix;

    public ClassCache(String prefix) {
        if (!prefix.isEmpty()) {
            prefix += ".";
        }

        this.prefix = prefix + "%s";
    }

    public ClassCache() {
        this("");
    }

    /**
     * @param name      simple class name.
     * @param fromCache if the cached value should be returned.
     * @return the class or {@code null} if it does not exist.
     *
     * @see Class#getSimpleName()
     */
    public Class<?> getAndCache(String name, boolean fromCache) {
        name = String.format(prefix, name);

        if (fromCache && isCached(name)) {
            return cache.get(name);
        }

        Class<?> clazz = Reflection.getClass(name);

        if (clazz != null) {
            cache.put(name, clazz);
        }

        return clazz;
    }

    /**
     * @param name simple class name.
     * @return the class or {@code null} if it does not exist.
     *
     * @see Class#getSimpleName()
     */
    public Class<?> getAndCache(String name) {
        return getAndCache(name, true);
    }

    /**
     * @param name simple class name.
     * @return {@code true} if the class is cached.
     */
    public boolean isCached(String name) {
        return cache.containsKey(name);
    }
}