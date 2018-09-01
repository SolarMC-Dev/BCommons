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
import me.bradleysteele.commons.util.Preconditions;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The {@link BStore} class maps keys to values of a specific type using the
 * {@link Map} interface. Keys do not have a specific type. {@link String}
 * type keys will be converted to lowercase using
 * {@link String#toLowerCase(Locale)} before stored.
 * <p>
 * A map cannot contain duplicate keys; each key can map to at most one value.
 * <p>
 * Both keys and values cannot be {@code null} and are checked with the
 * {@link Preconditions} class before insertion.
 *
 * @param <T> the store type.
 *
 * @author Bradley Steele
 * @see java.util.Map
 * @see java.util.HashMap
 */
public class BStore<T> implements Registrable {

    private static final Random random = new Random();
    private static final Locale LOCALE = Locale.ENGLISH;

    protected BPlugin plugin;
    private Map<Object, T> store = Maps.newHashMap();


    @Override
    public void register() {}

    /**
     * @param key the key to check.
     * @return if the key is present in the store.
     */
    public boolean exists(Object key) {
        return key != null && store.containsKey(key);
    }

    /**
     * @param key the key to check.
     * @return if the key is present in the store.
     */
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

    @SafeVarargs
    public final T retrieveRandom(T... exclude) {
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

    /**
     * @param key   key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     */
    public void store(Object key, T value) {
        store.put(key, value);
    }

    /**
     * Converts the provided key to lowercase before storing in order to
     * keep the store organised.
     *
     * @param key   key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @throws NullPointerException if either parameters are {@code null}.
     */
    public void store(String key, T value) {
        Preconditions.nonNull(key, "key cannot be null.");
        Preconditions.nonNull(value, "value cannot be null, use Store#drop to remove values.");

        store.put(key.toLowerCase(), value);
    }

    /**
     * Copies all of the mappings from the provided map to
     * this store.
     *
     * @param map mappings to be stored in this map.
     */
    public void storeAll(Map<Object, ? extends T> map) {
        store.putAll(map);
    }

    /**
     * @param key key with which the specified value is to be remove.
     *
     * @throws NullPointerException if the provided key is {@code null}.
     */
    public void drop(Object key) {
        Preconditions.nonNull(key);

        store.remove(key);
    }

    /**
     * @param key key with which the specified value is to be remove.
     */
    public void drop(String key) {
        Preconditions.nonNull(key);
        drop((Object) key.toLowerCase(LOCALE));
    }
}