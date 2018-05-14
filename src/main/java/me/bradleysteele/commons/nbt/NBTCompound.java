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

import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * @author Bradley Steele
 */
public class NBTCompound {

    private final NBTCompound parent;
    private final String name;

    protected NBTCompound(NBTCompound parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return parent.getItem();
    }

    public NBTCompound getParent() {
        return parent;
    }

    public boolean hasKey(String key) {
        return NBTReflection.hasKey(getItem(), this, key);
    }

    public NBTCompound getCompound(String name) {
        NBTCompound next = new NBTCompound(this, name);

        if (NBTReflection.isCompound(getItem(), next)) {
            return next;
        }

        return null;
    }

    public <T> T getObject(String key, Class<T> type) {
        return NBTReflection.getObject(getItem(), this, key, type);
    }

    public String getString(String key) {
        return NBTReflection.getString(getItem(), this, key);
    }

    public Integer getInteger(String key) {
        return NBTReflection.getInt(getItem(), this, key);
    }

    public double getDouble(String key) {
        return NBTReflection.getDouble(getItem(), this, key);
    }

    public boolean getBoolean(String key) {
        return NBTReflection.getBoolean(getItem(), this, key);
    }

    public Set<String> getKeys() {
        return NBTReflection.getKeys(getItem(), this);
    }

    public NBTCompound addCompound(String name) {
        setItem(NBTReflection.addNBTTagCompound(getItem(), this, name));
        return getCompound(name);
    }

    protected void setItem(ItemStack item) {
        parent.setItem(item);
    }

    public void setString(String key, String value) {
        setItem(NBTReflection.setString(getItem(), this, key, value));
    }

    public void setInteger(String key, int value) {
        setItem(NBTReflection.setInt(getItem(), this, key, value));
    }

    public void setDouble(String key, double value) {
        setItem(NBTReflection.setDouble(getItem(), this, key, value));
    }

    public void setBoolean(String key, boolean value) {
        setItem(NBTReflection.setBoolean(getItem(), this, key, value));
    }

    public void setObject(String key, Object value) {
        setItem(NBTReflection.setObject(getItem(), this, key, value));
    }

    public void removeKey(String key) {
        setItem(NBTReflection.remove(getItem(), this, key));
    }
}