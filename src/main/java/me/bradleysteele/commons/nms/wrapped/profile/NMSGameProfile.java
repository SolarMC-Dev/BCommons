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

import me.bradleysteele.commons.nms.NMSObject;
import me.bradleysteele.commons.util.reflect.Reflection;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author Bradley Steele
 */
public class NMSGameProfile implements NMSObject {

    // Constructor
    private static final Class<?> CLASS_GAME_PROFILE = Reflection.getClass("com.mojang.authlib.GameProfile");
    private static final Class[] CONSTRUCTOR_PARAM_TYPES = new Class[] { UUID.class, String.class };

    private static final Method METHOD_GET_ID = Reflection.getMethod(CLASS_GAME_PROFILE, "getId");
    private static final Method METHOD_GET_NAME = Reflection.getMethod(CLASS_GAME_PROFILE, "getName");
    private static final Method METHOD_IS_COMPLETE = Reflection.getMethod(CLASS_GAME_PROFILE, "isComplete");
    private static final Method METHOD_GET_PROPERTIES = Reflection.getMethod(CLASS_GAME_PROFILE, "getProperties");

    public static NMSGameProfile fromNMSHandle(Object handle) {
        if (handle == null) {
            return null;
        }

        return new NMSGameProfile(handle);
    }

    private final Object handle;

    private NMSPropertyMap properties;

    private NMSGameProfile(Object handle) {
        this.handle = handle;
    }

    public NMSGameProfile(UUID uuid, String name) {
        this(Reflection.newInstance(CLASS_GAME_PROFILE, CONSTRUCTOR_PARAM_TYPES, uuid, name));
    }

    @Override
    public Object getNMSHandle() {
        return handle;
    }

    /**
     * @return profile owner's unique id.
     */
    public UUID getId() {
        return Reflection.invokeMethod(METHOD_GET_ID, handle);
    }

    /**
     * @return profile owner's name.
     */
    public String getName() {
        return Reflection.invokeMethod(METHOD_GET_NAME, handle);
    }

    /**
     * @return multimap of the property key and property object.
     */
    public NMSPropertyMap getProperties() {
        if (properties == null) {
            properties = NMSPropertyMap.fromNMSHandle(Reflection.invokeMethod(METHOD_GET_PROPERTIES, handle));
        }

        return properties;
    }

    /**
     * @return {@code true} if the uuid and name are valid.
     */
    public boolean isComplete() {
        return Reflection.invokeMethod(METHOD_IS_COMPLETE, handle);
    }
}
