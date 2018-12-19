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

package me.bradleysteele.commons.itemstack;

import com.google.common.collect.Lists;
import me.bradleysteele.commons.itemstack.nbt.NBTItemStack;
import me.bradleysteele.commons.util.Messages;
import me.bradleysteele.commons.util.reflect.Reflection;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * @author Bradley Steele
 */
public class ItemStackBuilder {

    // Stack
    private Material material;
    private int amount = 1;
    private short durability = 0;

    // Meta
    private String displayName;
    private List<String> lore = Lists.newArrayList();
    private boolean unbreakable = false;
    private List<ItemFlag> itemFlags = Lists.newArrayList();

    // NBT
    private final List<Applier> nbtAppliers = Lists.newArrayList();

    /**
     * @param material item's material.
     */
    protected ItemStackBuilder(Material material) {
        withMaterial(material);
    }

    /**
     * @param item the item to unpack.
     */
    protected ItemStackBuilder(ItemStack item) {
        this(item.getType());

        withAmount(item.getAmount());
        withDurability(item.getDurability());

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            withDisplayName(meta.getDisplayName());
            withLore(meta.hasLore() ? meta.getLore() : Lists.newArrayList());

            if (Reflection.hasMethod(ItemMeta.class, "setUnbreakable", boolean.class)) {
                withUnbreakable(meta.isUnbreakable());
            }
        }
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(material, amount, durability);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(lore);

            if (Reflection.hasMethod(ItemMeta.class, "setUnbreakable", boolean.class)) {
                meta.setUnbreakable(unbreakable);
            }

            meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));

            item.setItemMeta(meta);
        }

        // NBTs must be applied AFTER meta is applied.
        for (Applier applier : nbtAppliers) {
            item = applier.apply(item);
        }

        return item;
    }

    public ItemStackBuilder withMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemStackBuilder withAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStackBuilder withDurability(short durability) {
        this.durability = durability;
        return this;
    }

    public ItemStackBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemStackBuilder withDisplayNameColoured(String displayName) {
        this.displayName = Messages.colour(displayName);
        return this;
    }

    public ItemStackBuilder withLore(Iterable<? extends String> lore) {
        this.lore = Lists.newArrayList(lore);
        return this;
    }

    public ItemStackBuilder withLoreColoured(Iterable<? extends String> lore) {
        this.lore = Messages.colour(lore);
        return this;
    }

    public ItemStackBuilder withUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemStackBuilder withItemFlag(ItemFlag... flag) {
        itemFlags.addAll(Arrays.asList(flag));
        return this;
    }

    public ItemStackBuilder withNBTString(String key, String value) {
        nbtAppliers.add(item -> {
            NBTItemStack nbtItem = ItemStacks.toNBTItemStack(item);
            nbtItem.setString(key, value);
            return nbtItem.getItem();
        });

        return this;
    }

    public ItemStackBuilder withNBTInteger(String key, int value) {
        nbtAppliers.add(item -> {
            NBTItemStack nbtItem = ItemStacks.toNBTItemStack(item);
            nbtItem.setInteger(key, value);
            return nbtItem.getItem();
        });

        return this;
    }

    public ItemStackBuilder withNBTDouble(String key, double value) {
        nbtAppliers.add(item -> {
            NBTItemStack nbtItem = ItemStacks.toNBTItemStack(item);
            nbtItem.setDouble(key, value);
            return nbtItem.getItem();
        });

        return this;
    }

    public ItemStackBuilder withNBTBoolean(String key, boolean value) {
        nbtAppliers.add(item -> {
            NBTItemStack nbtItem = ItemStacks.toNBTItemStack(item);
            nbtItem.setBoolean(key, value);
            return nbtItem.getItem();
        });

        return this;
    }

    public ItemStackBuilder withNBTObject(String key, Object value) {
        nbtAppliers.add(item -> {
            NBTItemStack nbtItem = ItemStacks.toNBTItemStack(item);
            nbtItem.setObject(key, value);
            return nbtItem.getItem();
        });

        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public short getDurability() {
        return durability;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    protected List<Applier> getNbtAppliers() {
        return nbtAppliers;
    }

    interface Applier {

        /**
         * @return the {@link ItemStack} with the applied contents.
         */
        ItemStack apply(ItemStack item);
    }
}