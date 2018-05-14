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

package me.bradleysteele.commons.nbt;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import me.bradleysteele.commons.util.logging.StaticLog;
import me.bradleysteele.commons.util.reflect.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.Stack;

/**
 * @author Bradley Steele
 */
public final class NBTReflection {

    private static final Gson gson = new Gson();
    private static final String version;

    static {
        version = Bukkit.getServer().getClass().getPackage().getName()
                .replace(".", ",")
                .split(",")[3];
    }


    // nms/CraftBukkit Classes
    private static final Class<?> CRAFT_ITEM_STACK = Reflection.getClass("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
    private static final Class<?> NBT_BASE = Reflection.getClass("net.minecraft.server." + version + ".NBTBase");
    private static final Class<?> NBT_TAG_COMPOUND = Reflection.getClass("net.minecraft.server." + version + ".NBTTagCompound");


    // NBTTagCompound Methods

    /** NBTTagCompound#getString(String key) */
    private static final Method NTC_GET_STRING = Reflection.getMethod(NBT_TAG_COMPOUND, "getString", String.class);

    /** NBTTagCompound#getInt(String key) */
    private static final Method NTC_GET_INT = Reflection.getMethod(NBT_TAG_COMPOUND, "getInt", String.class);

    /** NBTTagCompound#getDouble(String key) */
    private static final Method NTC_GET_DOUBLE = Reflection.getMethod(NBT_TAG_COMPOUND, "getDouble", String.class);

    /** NBTTagCompound#getBoolean(String key) */
    private static final Method NTC_GET_BOOLEAN = Reflection.getMethod(NBT_TAG_COMPOUND, "getBoolean", String.class);


    public static boolean isCompound(ItemStack itemStack, NBTCompound compound){
        Object root = getTag(getCraftItemStack(itemStack));

        if (root == null) {
            root = getNewNBTTagCompound();
        }

        return (getToCompound(root, compound)) != null;
    }

    public static boolean hasKey(ItemStack itemStack, NBTCompound compound, String key) {
        Object nmsItem = getCraftItemStack(itemStack);

        if (nmsItem == null || !isCompound(itemStack, compound)) {
            return false;
        }

        Object rootTag = getTag(nmsItem);
        Object workingTag = getToCompound(rootTag, compound);

        if (workingTag == null) {
            return false;
        }

        Method method = Reflection.getMethod(workingTag.getClass(), "hasKey", String.class);
        return Reflection.invokeMethod(method, workingTag, key);
    }

    public static Set<String> getKeys(ItemStack itemStack, NBTCompound compound) {
        Object nmsItem = getCraftItemStack(itemStack);

        if (nmsItem == null || !isCompound(itemStack, compound)) {
            return null;
        }

        Object rootTag = getTag(nmsItem);
        Object workingTag = getToCompound(rootTag, compound);

        if (workingTag == null) {
            return null;
        }

        return Reflection.invokeMethod("c", workingTag);
    }

    public static String getString(ItemStack itemStack, NBTCompound compound, String key) {
        return getKey(itemStack, compound, key, NTC_GET_STRING, null);
    }

    public static int getInt(ItemStack itemStack, NBTCompound compound, String key) {
        return getKey(itemStack, compound, key, NTC_GET_INT, 0);
    }

    public static double getDouble(ItemStack itemStack, NBTCompound compound, String key) {
        return getKey(itemStack, compound, key, NTC_GET_DOUBLE, 0.0D);
    }

    public static boolean getBoolean(ItemStack itemStack, NBTCompound compound, String key) {
        return getKey(itemStack, compound, key, NTC_GET_BOOLEAN, false);
    }

    public static <T> T getObject(ItemStack itemStack, NBTCompound compound, String key, Class<T> type) {
        String json = getString(itemStack, compound, key);

        if (json == null) {
            return null;
        }

        try {
            return deserializeJson(json, type);
        } catch (JsonSyntaxException e) {
            StaticLog.error("Failed to deserialize NBT Json object:");
            StaticLog.exception(e);
        }

        return null;
    }

    private static Object getNewNBTTagCompound() {
        return Reflection.newInstance(NBT_TAG_COMPOUND);
    }

    private static Object getCraftItemStack(ItemStack original) {
        return Reflection.invokeMethod(Reflection.getMethod(CRAFT_ITEM_STACK, "asNMSCopy", ItemStack.class), CRAFT_ITEM_STACK, original);
    }

    private static ItemStack getItemStack(Object item) {
        return Reflection.invokeMethod(Reflection.getMethod(CRAFT_ITEM_STACK, "asCraftMirror", item.getClass()), CRAFT_ITEM_STACK, item);
    }

    private static Object getTag(Object craftItemStack) {
        Object tag = Reflection.invokeMethod(Reflection.getMethod(craftItemStack.getClass(), "getTag"), craftItemStack);
        return tag != null ? tag : getNewNBTTagCompound();
    }

    private static Object getSubNBTTagCompound(Object compound, String name) {
        Method method = Reflection.getMethod(compound.getClass(), "getCompound", String.class);
        return Reflection.invokeMethod(method, compound, name);
    }

    public static ItemStack addNBTTagCompound(ItemStack item, NBTCompound comp, String name) {
        if (name == null) {
            return remove(item, comp, null);
        }

        Object nmsItem = getCraftItemStack(item);

        if (nmsItem == null) {
            return null;
        }

        Object nbtTag = getTag(nmsItem);

        if (nbtTag == null) {
            nbtTag = getNewNBTTagCompound();
        }

        if (!isCompound(item, comp)) {
            return item;
        }

        Object workingTag = getToCompound(nbtTag, comp);

        try {
            Method method = workingTag.getClass().getMethod("set", String.class, NBT_BASE);
            method.invoke(workingTag, name, NBT_TAG_COMPOUND.newInstance());
            nmsItem = setNBTTag(nbtTag, nmsItem);

            return getItemStack(nmsItem);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return item;
    }

    private static <T> T getKey(ItemStack itemStack, NBTCompound compound, String key, Method method, T def) {
        Object nmsItem = getCraftItemStack(itemStack);

        if (nmsItem == null || !isCompound(itemStack, compound)) {
            return def;
        }

        Object rootTag = getTag(nmsItem);
        Object tag = getToCompound(rootTag, compound);

        if (tag == null) {
            return def;
        }

        return Reflection.invokeMethod(method, tag, key);
    }

    public static ItemStack remove(ItemStack item, NBTCompound comp, String key) {
        Object nmsItem = getCraftItemStack(item);

        if (nmsItem == null || !isCompound(item, comp)) {
            return item;
        }

        Object rootTag = getTag(nmsItem);
        Object workingTag = getToCompound(rootTag, comp);

        if (workingTag == null) {
            return item;
        }

        Method method = Reflection.getMethod(workingTag.getClass(), "remove", String.class);
        Reflection.invokeMethod(method, workingTag, key);

        nmsItem = setNBTTag(rootTag, nmsItem);
        return getItemStack(nmsItem);
    }

    public static ItemStack setString(ItemStack item, NBTCompound comp, String key, String text) {
        if (text == null) {
            return remove(item, comp, key);
        }

        Object nmsItem = getCraftItemStack(item);

        if (nmsItem == null) {
            return null;
        }

        Object rootNbtTag = getTag(nmsItem);

        if (rootNbtTag == null) {
            rootNbtTag = getNewNBTTagCompound();
        }

        if (!isCompound(item, comp)) {
            return item;
        }

        Object workingTag = getToCompound(rootNbtTag, comp);

        try {
            Method method = workingTag.getClass().getMethod("setString", String.class, String.class);
            method.invoke(workingTag, key, text);
            nmsItem = setNBTTag(rootNbtTag, nmsItem);

            return getItemStack(nmsItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public static ItemStack setInt(ItemStack item, NBTCompound comp, String key, Integer i) {
        if (i == null) {
            return remove(item, comp, key);
        }

        Object nmsItem = getCraftItemStack(item);

        if (nmsItem == null) {
            return null;
        }

        Object rootTag = getTag(nmsItem);

        if (rootTag == null) {
            rootTag = getNewNBTTagCompound();
        }

        if (!isCompound(item, comp)) {
            return item;
        }

        Object workingTag = getToCompound(rootTag, comp);
        Method method;

        try {
            method = workingTag.getClass().getMethod("setInt", String.class, int.class);
            method.invoke(workingTag, key, i);
            nmsItem = setNBTTag(rootTag, nmsItem);

            return getItemStack(nmsItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public static ItemStack setDouble(ItemStack item, NBTCompound comp, String key, Double d) {
        if (d == null) {
            return remove(item, comp, key);
        }

        Object nmsItem = getCraftItemStack(item);

        if (nmsItem == null) {
            return null;
        }

        Object rootTag = getTag(nmsItem);

        if (rootTag == null) {
            rootTag = getNewNBTTagCompound();
        }

        if (!isCompound(item, comp)) {
            return item;
        }

        Object workingTag = getToCompound(rootTag, comp);
        Method method;

        try {
            method = workingTag.getClass().getMethod("setDouble", String.class, double.class);
            method.invoke(workingTag, key, d);
            nmsItem = setNBTTag(rootTag, nmsItem);

            return getItemStack(nmsItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public static ItemStack setBoolean(ItemStack item, NBTCompound comp, String key, Boolean d) {
        if (d == null) {
            return remove(item, comp, key);
        }

        Object nmsItem = getCraftItemStack(item);

        if (nmsItem == null) {
            return null;
        }

        Object rootTag = getTag(nmsItem);

        if (rootTag == null) {
            rootTag = getNewNBTTagCompound();
        }

        if (!isCompound(item, comp)) {
            return item;
        }

        Object workingTag = getToCompound(rootTag, comp);
        Method method;

        try {
            method = workingTag.getClass().getMethod("setBoolean", String.class, boolean.class);
            method.invoke(workingTag, key, d);
            nmsItem = setNBTTag(rootTag, nmsItem);

            return getItemStack(nmsItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public static ItemStack setObject(ItemStack item, NBTCompound comp, String key, Object value) {
        try {
            String json = gson.toJson(value);
            return setString(item, comp, key, json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static Object setNBTTag(Object tag, Object item) {
        Method method = Reflection.getMethod(item.getClass(), "setTag", tag.getClass());
        Reflection.invokeMethod(method, item, tag);
        return item;
    }

    private static <T> T deserializeJson(String json, Class<T> type) throws JsonSyntaxException {
        if (json == null) {
            return null;
        }

        T obj = gson.fromJson(json, type);

        return type.cast(obj);
    }

    private static Object getToCompound(Object nbtTag, NBTCompound compound){
        Stack<String> structure = new Stack<>();

        while (compound.getParent() != null) {
            structure.add(compound.getName());
            compound = compound.getParent();
        }

        while (!structure.isEmpty()) {
            nbtTag = getSubNBTTagCompound(nbtTag, structure.pop());

            if (nbtTag == null) {
                return null;
            }
        }

        return nbtTag;
    }
}