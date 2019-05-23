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
import java.security.PublicKey;

/**
 * @author Bradley Steele
 */
public class NMSProperty implements NMSObject {

    private static final Class<?> CLASS_PROPERTY = Reflection.getClass("com.mojang.authlib.properties.Property");
    private static final Class[] CONSTRUCTOR_PARAM_TYPES = new Class[] { String.class, String.class, String.class };

    private static final Method METHOD_GET_NAME = Reflection.getMethod(CLASS_PROPERTY, "getName");
    private static final Method METHOD_GET_VALUE = Reflection.getMethod(CLASS_PROPERTY, "getValue");
    private static final Method METHOD_GET_SIGNATURE = Reflection.getMethod(CLASS_PROPERTY, "getSignature");
    private static final Method METHOD_HAS_SIGNATURE = Reflection.getMethod(CLASS_PROPERTY, "hasSignature");
    private static final Method METHOD_IS_SIGNATURE_VALID = Reflection.getMethod(CLASS_PROPERTY, "isSignatureValid", PublicKey.class);

    public static NMSProperty fromNMSHandle(Object handle) {
        if (handle == null) {
            return null;
        }

        return new NMSProperty(handle);
    }

    private final Object handle;

    private NMSProperty(Object handle) {
        this.handle = handle;
    }

    public NMSProperty(String name, String value, String signature) {
        this(Reflection.newInstance(CLASS_PROPERTY, CONSTRUCTOR_PARAM_TYPES, name, value, signature));
    }

    public NMSProperty(String name, String value) {
        this(name, value, null);
    }

    @Override
    public Object getNMSHandle() {
        return handle;
    }

    /**
     * @return the property name.
     */
    public String getName() {
        return Reflection.invokeMethod(METHOD_GET_NAME, handle);
    }

    /**
     * @return the property value.
     */
    public String getValue() {
        return Reflection.invokeMethod(METHOD_GET_VALUE, handle);
    }

    /**
     * @return the property signature.
     */
    public String getSignature() {
        return Reflection.invokeMethod(METHOD_GET_SIGNATURE, handle);
    }

    /**
     * @return {@code true} if the signature is not {@code null}.
     */
    public boolean hasSignature() {
        return Reflection.invokeMethod(METHOD_HAS_SIGNATURE, handle);
    }

    /**
     * @param publicKey key to test.
     * @return {@code true} if the signature is valid.
     */
    public boolean isSignatureValid(PublicKey publicKey) {
        return Reflection.invokeMethod(METHOD_IS_SIGNATURE_VALID, handle, publicKey);
    }
}
