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

package me.bradleysteele.commons.nms.wrapped.profile;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.Multimap;
import me.bradleysteele.commons.nms.NMSHandle;
import me.bradleysteele.commons.nms.util.MultimapCollector;
import me.bradleysteele.commons.util.reflect.Reflection;

import java.lang.reflect.Method;
import java.util.AbstractMap;

/**
 * @author Bradley Steele
 */
public class NMSPropertyMap extends ForwardingMultimap<String, NMSProperty> implements NMSHandle<Multimap<String, Object>> {

    private static final Class<Multimap<String, Object>> CLASS_PROPERTY_MAP = Reflection.getClass("com.mojang.authlib.properties.PropertyMap");

    private static final Method METHOD_DELEGATE = Reflection.getMethod(CLASS_PROPERTY_MAP, "delegate");

    public static NMSPropertyMap fromNMSHandle(Multimap<String, Object> handle) {
        if (handle == null) {
            return null;
        }

        return new NMSPropertyMap(handle);
    }

    private final Multimap<String, Object> handle;

    private NMSPropertyMap(Multimap<String, Object> handle) {
        this.handle = handle;
    }

    public NMSPropertyMap() {
        this(Reflection.newInstance(CLASS_PROPERTY_MAP));
    }

    @Override
    public Multimap<String, Object> getNMSHandle() {
        return handle;
    }

    @Override
    protected Multimap<String, NMSProperty> delegate() {
        Multimap<String, Object> internal = Reflection.invokeMethod(METHOD_DELEGATE, handle);

        // Convert String-Object multimap to String-NMSProperty map
        return internal.entries().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), NMSProperty.fromNMSHandle(entry.getValue())))
                .collect(MultimapCollector.toLinkedHashMultimap());
    }

    @Override
    public boolean put(String key, NMSProperty value) {
        handle.put(key, value.getNMSHandle());
        return super.put(key, value);
    }

    @Override
    public void clear() {
        handle.clear();
        super.clear();
    }
}
