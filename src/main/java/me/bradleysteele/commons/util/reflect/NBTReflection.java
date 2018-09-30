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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import me.bradleysteele.commons.itemstack.nbt.NBTCompound;
import me.bradleysteele.commons.util.logging.StaticLog;
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

    public static String getPackageVersion() {
        return version;
    }

    // NBTBase
    private static final Class<?> NBT_BASE = Reflection.getClass("net.minecraft.server." + version + ".NBTBase");

    // NBTTagCompound
    private static final Class<?> NBT_TAG_COMPOUND = Reflection.getClass("net.minecraft.server." + version + ".NBTTagCompound");

    private static final Method NTC_REMOVE = Reflection.getMethod(NBT_TAG_COMPOUND, "remove", String.class);
    private static final Method NTC_HAS_KEY = Reflection.getMethod(NBT_TAG_COMPOUND, "hasKey", String.class);
    private static final Method NTC_GET_COMPOUND = Reflection.getMethod(NBT_TAG_COMPOUND, "getCompound", String.class);
    private static final Method NTC_GET_STRING = Reflection.getMethod(NBT_TAG_COMPOUND, "getString", String.class);
    private static final Method NTC_GET_INT = Reflection.getMethod(NBT_TAG_COMPOUND, "getInt", String.class);
    private static final Method NTC_GET_DOUBLE = Reflection.getMethod(NBT_TAG_COMPOUND, "getDouble", String.class);
    private static final Method NTC_GET_BOOLEAN = Reflection.getMethod(NBT_TAG_COMPOUND, "getBoolean", String.class);

    private static final Method NTC_SET = Reflection.getMethod(NBT_TAG_COMPOUND, "set", String.class, NBT_BASE);
    private static final Method NTC_SET_STRING = Reflection.getMethod(NBT_TAG_COMPOUND, "setString", String.class, String.class);
    private static final Method NTC_SET_INT = Reflection.getMethod(NBT_TAG_COMPOUND, "setInt", String.class, int.class);
    private static final Method NTC_SET_DOUBLE = Reflection.getMethod(NBT_TAG_COMPOUND, "setDouble", String.class, double.class);
    private static final Method NTC_SET_BOOLEAN = Reflection.getMethod(NBT_TAG_COMPOUND, "setBoolean", String.class, boolean.class);

    // NMS ItemStack
    private static final Class<?> NMS_ITEM_STACK = Reflection.getClass("net.minecraft.server." + version + ".ItemStack");
    private static final Method NIS_GET_TAG = Reflection.getMethod(NMS_ITEM_STACK, "getTag");
    private static final Method NIS_SET_TAG = Reflection.getMethod(NMS_ITEM_STACK, "setTag", NBT_TAG_COMPOUND);

    // CraftItemStack
    private static final Class<?> CRAFT_ITEM_STACK = Reflection.getClass("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
    private static final Method CIS_AS_CRAFT_MIRROR = Reflection.getMethod(CRAFT_ITEM_STACK, "asCraftMirror", NMS_ITEM_STACK);
    private static final Method CIS_AS_NMS_COPY = Reflection.getMethod(CRAFT_ITEM_STACK, "asNMSCopy", ItemStack.class);


    private static Object newNBTTagCompound() {
        return Reflection.newInstance(NBT_TAG_COMPOUND);
    }

    public static ItemStack addNBTTagCompound(ItemStack stack, NBTCompound compound, String name) {
        return setValue(stack, compound, name, Reflection.newInstance(NBT_TAG_COMPOUND), NTC_SET);
    }

    public static boolean isCompound(ItemStack stack, NBTCompound compound) {
        Object root = getTag(getCraftItemStack(stack));

        if (root == null) {
            root = newNBTTagCompound();
        }

        return getToCompound(root, compound) != null;
    }

    // NBTTagCompound#remove
    public static ItemStack remove(ItemStack item, NBTCompound compound, String key) {
        Object craftStack = getCraftItemStack(item);

        if (craftStack == null || !isCompound(item, compound)) {
            return item;
        }

        Object rootTag = getTag(craftStack);
        Object workingTag = getToCompound(rootTag, compound);

        if (workingTag == null) {
            return item;
        }

        Reflection.invokeMethod(NTC_REMOVE, workingTag, key);
        return getItemStack(setNBTTag(rootTag, craftStack));
    }

    // NBTTagCompound#hasKey
    public static boolean hasKey(ItemStack stack, NBTCompound compound, String key) {
        Object craftStack = getCraftItemStack(stack);

        if (craftStack == null || !isCompound(stack, compound)) {
            return false;
        }

        Object workingTag = getToCompound(getTag(craftStack), compound);

        if (workingTag == null) {
            return false;
        }

        return Reflection.invokeMethod(NTC_HAS_KEY, workingTag, key);
    }

    // NBTTagCompound#c
    public static Set<String> getKeys(ItemStack stack, NBTCompound compound) {
        Object craftStack = getCraftItemStack(stack);

        if (craftStack == null || !isCompound(stack, compound)) {
            return null;
        }

        Object workingTag = getToCompound(getTag(craftStack), compound);

        if (workingTag == null) {
            return null;
        }

        return Reflection.invokeMethod("c", workingTag);
    }

    private static <T> T getValue(ItemStack itemStack, NBTCompound compound, String key, Method method, T def) {
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

    // NBTTagCompound#getString
    public static String getString(ItemStack stack, NBTCompound compound, String key) {
        return getValue(stack, compound, key, NTC_GET_STRING, null);
    }

    // NBTTagCompound#getInt
    public static int getInt(ItemStack stack, NBTCompound compound, String key) {
        return getValue(stack, compound, key, NTC_GET_INT, 0);
    }

    // NBTTagCompound#getDouble
    public static double getDouble(ItemStack stack, NBTCompound compound, String key) {
        return getValue(stack, compound, key, NTC_GET_DOUBLE, 0.0D);
    }

    // NBTTagCompound#getBoolean
    public static boolean getBoolean(ItemStack stack, NBTCompound compound, String key) {
        return getValue(stack, compound, key, NTC_GET_BOOLEAN, false);
    }

    public static <T> T getObject(ItemStack stack, NBTCompound compound, String key, Class<T> type) {
        String json = getString(stack, compound, key);

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

    // CraftItemStack#asNMSCopy
    private static Object getCraftItemStack(ItemStack stack) {
        return Reflection.invokeMethod(CIS_AS_NMS_COPY, CRAFT_ITEM_STACK, stack);
    }

    // CraftItemStack#asCraftMirror
    private static ItemStack getItemStack(Object craftStack) {
        return Reflection.invokeMethod(CIS_AS_CRAFT_MIRROR, CRAFT_ITEM_STACK, craftStack);
    }

    // ItemStack#getTag
    private static Object getTag(Object craftStack) {
        Object tag = Reflection.invokeMethod(NIS_GET_TAG, craftStack);
        return tag != null ? tag : newNBTTagCompound();
    }

    private static Object getSubNBTTagCompound(Object compound, String name) {
        return Reflection.invokeMethod(NTC_GET_COMPOUND, compound, name);
    }

    private static Object getToCompound(Object nbtTag, NBTCompound compound) {
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

    private static ItemStack setValue(ItemStack stack, NBTCompound compound, String key, Object value, Method method) {
        if (value == null) {
            return remove(stack, compound, key);
        }

        Object craftStack = getCraftItemStack(stack);

        if (craftStack == null) {
            return null;
        }

        Object rootTag = getTag(craftStack);

        if (rootTag == null) {
            rootTag = newNBTTagCompound();
        }

        if (!isCompound(stack, compound)) {
            return stack;
        }

        // Map key-value using provided setter
        Reflection.invokeMethod(method, getToCompound(rootTag, compound), key, value);

        craftStack = setNBTTag(rootTag, craftStack);
        return getItemStack(craftStack);
    }

    // NBTTagCompound#setString
    public static ItemStack setString(ItemStack stack, NBTCompound compound, String key, String value) {
        return setValue(stack, compound, key, value, NTC_SET_STRING);
    }

    // NBTTagCompound#setInt
    public static ItemStack setInt(ItemStack stack, NBTCompound compound, String key, int value) {
        return setValue(stack, compound, key, value, NTC_SET_INT);
    }

    // NBTTagCompound#setDouble
    public static ItemStack setDouble(ItemStack stack, NBTCompound compound, String key, double value) {
        return setValue(stack, compound, key, value, NTC_SET_DOUBLE);
    }

    // NBTTagCompound#setBoolean
    public static ItemStack setBoolean(ItemStack stack, NBTCompound compound, String key, boolean value) {
        return setValue(stack, compound, key, value, NTC_SET_BOOLEAN);
    }

    public static ItemStack setObject(ItemStack stack, NBTCompound compound, String key, Object value) {
        try {
            String json = gson.toJson(value);
            return setString(stack, compound, key, json);
        } catch (Exception e) {
            // Ignored
        }

        return stack;
    }

    // ItemStack#setTag
    private static Object setNBTTag(Object tag, Object item) {
        Reflection.invokeMethod(NIS_SET_TAG, item, tag);
        return item;
    }

    private static <T> T deserializeJson(String json, Class<T> type) throws JsonSyntaxException {
        if (json == null) {
            return null;
        }

        T object = gson.fromJson(json, type);
        return type.cast(object);
    }
}