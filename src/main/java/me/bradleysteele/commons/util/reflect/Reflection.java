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

package me.bradleysteele.commons.util.reflect;

import me.bradleysteele.commons.util.logging.StaticLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * The {@link Reflection} class contains reflection utility methods which are all
 * thread safe. In most cases of an exception thrown, it is logged and {@code null} is
 * returned.
 *
 * @author Bradley Steele
 * @version 1.0
 */
public final class Reflection {

    // Suppresses default constructor.
    private Reflection() {}

    /**
     * Attempts to create an instance of the given class.
     *
     * @param clazz class that we're creating an instance of.
     * @param <T>   class type.
     * @return an initialised class object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) {
        T instance = null;

        try {
            instance = (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            StaticLog.exception(e);
        }

        return instance;
    }

    // Getters: Class

    /**
     * @param name the fully qualified name of the desired class.
     * @return the {@code Class} object for the class with the specified name.
     */
    public static Class<?> getClass(String name) {
        Class<?> clazz = null;

        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            StaticLog.exception(e);
        }

        return clazz;
    }


    // Getters: Field

    /**
     * @param clazz class to check.
     * @param name  field name.
     * @return whether the class contains a field with the specified name.
     */
    public static boolean hasField(Class<?> clazz, String name) {
        return Arrays.stream(clazz.getDeclaredFields())
                .anyMatch(field -> field.getName().equals(name));
    }

    /**
     * Gets the field in the class and allows accessibility before returning.
     *
     * @param clazz class containing the field.
     * @param name  field name.
     * @return the field.
     */
    public static Field getField(Class<?> clazz, String name) {
        Field field = null;
        Class<?> c = clazz;

        while (c != null && c != Object.class) {

            if (hasField(c, name)) {
                try {
                    field = c.getDeclaredField(name);
                    setAccessible(field, true);
                } catch (NoSuchFieldException | NullPointerException | SecurityException e) {
                    /* Ignored */
                }

                break;
            }

            c = c.getSuperclass();
        }

        return field;
    }

    /**
     * @param field  the field we're modifying.
     * @param object the object we want.
     * @param <T>    object type.
     * @return value of the object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field field, Object object) {
        setAccessible(field, true);
        T value = null;

        try {
            value = (T) field.get(object);
        } catch (IllegalAccessException e) {
            StaticLog.exception(e);
        }

        return value;
    }

    /**
     * @param clazz  class containing the field.
     * @param name   field name.
     * @param object the object we want.
     * @param <T>    object type.
     * @return value of the object.
     */
    public static <T> T getFieldValue(Class<?> clazz, String name, Object object) {
        return getFieldValue(getField(clazz, name), object);
    }


    // Getters: Method

    // Prevent us from creating a new empty class/object every time the method is called.
    private static final Class<?>[] CLASSES = new Class<?>[] { };
    private static final Object[] OBJECTS = new Object[] { };

    /**
     * @param clazz          class containing the method.
     * @param name           method name.
     * @param parameterTypes method parameter types.
     * @return whether the class contains the method.
     */
    public static boolean hasMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        return getMethod(clazz, name, parameterTypes) != null;
    }

    /**
     * @param clazz class containing the method.
     * @param name  method name.
     * @return whether the class contains the method.
     */
    public static boolean hasMethod(Class<?> clazz, String name) {
        return hasMethod(clazz, name, CLASSES);
    }

    /**
     * By passing an Object argument rather than a Class, we're able to determine
     * if the methods have been implemented.
     * <p>
     * If the super class has a method, but it has not been implemented in the
     * object provided then {@code false} is returned.
     *
     * @param object         the object containing the method.
     * @param name           method name.
     * @param parameterTypes method parameter types.
     * @return whether the class contains the method.
     */
    public static boolean hasMethod(Object object, String name, Class<?>... parameterTypes) {
        Class<?> clazz = object.getClass();
        return getMethod(clazz, name, parameterTypes).getDeclaringClass().equals(clazz);
    }

    /**
     * @param object         the object containing the method.
     * @param name           method name.
     * @return whether the class contains the method.
     */
    public static boolean hasMethod(Object object, String name) {
        return hasMethod(object, name, CLASSES);
    }

    /**
     * @param clazz          class containing the method.
     * @param name           method name.
     * @param parameterTypes method parameter types.
     * @return method with the same name and parameter types.
     */
    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        Method method = null;

        try {
            method = clazz.getMethod(name, parameterTypes);
            setAccessible(method, true);
        } catch (NoSuchMethodException | NullPointerException | SecurityException  e) {
            /* Ignored */
        }

        return method;
    }

    /**
     * @param clazz class containing the method.
     * @param name  method name.
     * @return method with the same name with no parameters.
     */
    public static Method getMethod(Class<?> clazz, String name) {
        return getMethod(clazz, name, CLASSES);
    }

    /**
     * @param method to invoke.
     * @param object instance containing the method.
     * @param args   method arguments.
     * @param <T>    return type.
     * @return result from invoking the method.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Method method, Object object, Object... args) {
        T result = null;

        try {
            result = (T) method.invoke(object, args);
        } catch (Exception e) {
            StaticLog.exception(e);
        }

        return result;
    }

    /**
     * @param method to invoke.
     * @param object instance containing the method.
     * @param <T>    return type.
     * @return result from invoking the method.
     */
    public static <T> T invokeMethod(Method method, Object object) {
        return invokeMethod(method, object, OBJECTS);
    }

    /**
     * @param methodName method's name to invoke.
     * @param object     instance containing the method.
     * @param args       method arguments.
     * @param <T>        return type.
     * @return result from invoking the method.
     */
    public static <T> T invokeMethod(String methodName, Object object, Object... args) {
        return invokeMethod(getMethod(object.getClass(), methodName), object, args);
    }


    // Getters: Singleton

    /**
     * @param clazz class to check.
     * @return if the class is a singleton.
     */
    public static boolean isSingleton(Class<?> clazz) {
        return hasMethod(clazz, "getInstance") || hasMethod(clazz, "get");
    }

    /**
     * @param clazz class to get the instance from.
     * @param <T>   instance type.
     * @return the singleton's instance.
     */
    public static <T> T getSingleton(Class<?> clazz) {
        if (!isSingleton(clazz)) {
            return null;
        }

        Method getInstanceMethod = getMethod(clazz, "getInstance");

        if (getInstanceMethod == null) {
            getInstanceMethod = getMethod(clazz, "get");
        }

        return invokeMethod(getInstanceMethod, null);
    }

    /**
     * Sets the accessibility of a field. If access is allowed, the final modifier will be removed.
     *
     * @param field      the field to modify.
     * @param accessible whether the field should be accessible.
     */
    public static void setAccessible(Field field, boolean accessible) {
        try {
            field.setAccessible(accessible);

            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(accessible);

            if (accessible) {
                modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            }
        } catch (Exception e) {
            StaticLog.exception(e);
        }
    }

    /**
     * @param method     the method to modify.
     * @param accessible whether the method should be accessible.
     */
    public static void setAccessible(Method method, boolean accessible) {
        try {
            method.setAccessible(accessible);
        } catch (SecurityException e) {
            StaticLog.exception(e);
        }
    }

    /**
     * @param field  the field to modify.
     * @param object the object containing the field.
     * @param value  new value for the field.
     */
    public static void setFieldValue(Field field, Object object, Object value) {
        setAccessible(field, true);

        try {
            field.set(object, value);
        } catch (Exception e) {
            StaticLog.exception(e);
        }
    }

    /**
     * @param fieldName name of the field.
     * @param object    the object containing the field.
     * @param value     new value for the field.
     */
    public static void setFieldValue(String fieldName, Object object, Object value) {
        setFieldValue(getField(object.getClass(), fieldName), object, value);
    }
}