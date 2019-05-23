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

package me.bradleysteele.commons.nms.util;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.Map.Entry;

/**
 * The {@link MultimapCollector} class is a collector for the
 * {@link Multimap} map class.
 * <p>
 * Source: https://blog.jayway.com/2014/09/29/java-8-collector-for-gauvas-linkedhashmultimap/
 *
 * @author Johan Haleby
 * @author Bradley Steele
 */
public final class MultimapCollector {

    private MultimapCollector() {}

    public static <K, V, A extends Multimap<K, V>> Collector<Entry<K, V>, A, A> toMultimap(Supplier<A> supplier) {
        return Collector.of(supplier, (acc, entry) -> acc.put(entry.getKey(), entry.getValue()), (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        });
    }

    public static <K, V> Collector<Entry<K, V>, LinkedHashMultimap<K, V>, LinkedHashMultimap<K, V>> toLinkedHashMultimap() {
        return toMultimap(LinkedHashMultimap::create);
    }
}