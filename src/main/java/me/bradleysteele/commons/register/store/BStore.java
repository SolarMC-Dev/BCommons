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

package me.bradleysteele.commons.register.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.bradleysteele.commons.BPlugin;
import me.bradleysteele.commons.register.Registrable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @param <T> store type.
 *
 * @author Bradley Steele
 */
public class BStore<T> implements Registrable {

    private static final Random random = new Random();

    protected BPlugin plugin;
    private Map<Object, T> store = Maps.newHashMap();

    @Override
    public void register() {

    }

    public boolean exists(Object key) {
        return key != null && store.containsKey(key);
    }

    public boolean exists(String key) {
        return key != null && store.containsKey(key.toLowerCase());
    }

    public T retrieve(Object key) {
        return key != null ? store.get(key) : null;
    }

    public T retrieve(String key) {
        return key != null ? store.get(key.toLowerCase()) : null;
    }

    public T retrieveRandom(Collection<T> exclude) {
        List<T> values = Lists.newArrayList(store.values());
        values.removeAll(exclude);

        return values.get(random.nextInt(values.size()));
    }

    public T retrieveRandom(T... exclude) {
        return retrieveRandom(Arrays.asList(exclude));
    }

    public T retrieveRandom(Predicate<T> predicate) {
        List<T> values = Lists.newArrayList(store.values())
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());

        if (values.size() <= 0) {
            return null;
        }

        return values.get(random.nextInt(values.size()));
    }

    public Map<Object, T> retrieveAll() {
        return store;
    }

    public Collection<T> retrieveValues() {
        return store.values();
    }

    public Set<Object> retrieveKeySet() {
        return store.keySet();
    }

    public void store(Object key, T value) {
        store.put(key, value);
    }

    public void store(String key, T value) {
        if (key != null) {
            store.put(key.toLowerCase(), value);
        }
    }

    public void drop(Object key) {
        store.remove(key);
    }

    public void drop(String key) {
        if (key != null) {
            store.remove(key.toLowerCase());
        }
    }
}