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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.bradleysteele.commons.itemstack.nbt.NBTItemStack;
import me.bradleysteele.commons.util.Messages;
import me.bradleysteele.commons.util.reflect.Reflection;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Bradley Steele
 */
public class ItemStackBuilder {

    private static final boolean HAS_UNBREAKABLE = Reflection.hasMethod(ItemMeta.class, "setUnbreakable", boolean.class);

    // Stack
    private Material material;
    private int amount = 1;
    private short durability = 0;

    // Meta
    private String displayName;
    private List<String> lore = Lists.newArrayList();
    private boolean unbreakable = false;
    private final Set<ItemFlag> itemFlags = Sets.newHashSet();

    private final Map<Enchantment, Integer> enchantments = Maps.newHashMap();

    // NBT
    private final List<Applier> nbtAppliers = Lists.newArrayList();

    /**
     * @param material item's material.
     */
    protected ItemStackBuilder(Material material) {
        withMaterial(material);
    }

    /**
     * @param stack the stack to unpack.
     */
    protected ItemStackBuilder(ItemStack stack) {
        this(stack.getType());

        amount = stack.getAmount();
        durability = stack.getDurability();

        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();

            displayName = meta.getDisplayName();
            lore = meta.hasLore() ? meta.getLore() : Lists.newArrayList();
            unbreakable = meta.spigot().isUnbreakable();
            itemFlags.addAll(meta.getItemFlags());
        }

        enchantments.putAll(stack.getEnchantments());
    }

    protected ItemStackBuilder(ItemStackBuilder builder) {
        this(builder.getMaterial());

        amount = builder.amount;
        durability = builder.durability;

        displayName = builder.displayName;
        lore = Lists.newArrayList(builder.lore);
        unbreakable = builder.unbreakable;
        itemFlags.addAll(Lists.newArrayList(builder.itemFlags));
        enchantments.putAll(Maps.newHashMap(builder.enchantments));

        nbtAppliers.addAll(Lists.newArrayList(builder.nbtAppliers));
    }

    /**
     * @return builds the {@link ItemStackBuilder}'s data into a valid {@link ItemStack}.
     */
    public ItemStack build() {
        ItemStack stack = new ItemStack(material, amount, durability);
        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(lore);

            if (HAS_UNBREAKABLE) {
                meta.setUnbreakable(unbreakable);
            } else {
                meta.spigot().setUnbreakable(unbreakable);
            }

            meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
            stack.setItemMeta(meta);
        }

        stack.addUnsafeEnchantments(enchantments);

        // NBTs must be applied AFTER meta is applied.
        for (Applier applier : nbtAppliers) {
            stack = applier.apply(stack);
        }

        return stack;
    }

    /**
     * @return a copy of this {@link ItemStackBuilder}.
     */
    public ItemStackBuilder deepClone() {
        return new ItemStackBuilder(this);
    }

    /**
     * @param material the stack type.
     * @return this item stack builder.
     *
     * @see Material
     */
    public ItemStackBuilder withMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * @param amount the stack amount.
     * @return this item stack builder.
     */
    public ItemStackBuilder withAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * @param durability the stack data value.
     * @return this item stack builder.
     */
    public ItemStackBuilder withDurability(short durability) {
        this.durability = durability;
        return this;
    }

    /**
     * @param displayName the stack display name.
     * @return this item stack builder.
     */
    public ItemStackBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Colours and sets the item stack display name.
     *
     * @param displayName the stack display name.
     * @return this item stack builder.
     *
     * @see Messages#colour(String)
     */
    public ItemStackBuilder withDisplayNameColoured(String displayName) {
        this.displayName = Messages.colour(displayName);
        return this;
    }

    /**
     * @param lore the stack lore.
     * @return this item stack builder.
     */
    public ItemStackBuilder withLore(Iterable<? extends String> lore) {
        this.lore = Lists.newArrayList(lore);
        return this;
    }

    /**
     * Colours and sets the item stack lore.
     *
     * @param lore the stack lore.
     * @return this item stack builder.
     *
     * @see Messages#colour(Iterable)
     */
    public ItemStackBuilder withLoreColoured(Iterable<? extends String> lore) {
        this.lore = Messages.colour(lore);
        return this;
    }

    /**
     * @param unbreakable if the stack is unbreakable.
     * @return this item stack builder.
     */
    public ItemStackBuilder withUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    /**
     * @param flag the stack item flags.
     * @return this item stack builder.
     *
     * @see ItemFlag
     */
    public ItemStackBuilder withItemFlag(ItemFlag... flag) {
        itemFlags.addAll(Arrays.asList(flag));
        return this;
    }

    /**
     * Adds, and overrides existing enchantment type, an enchantment to
     * the stack.
     *
     * @param enchantment the enchantment type.
     * @param level       the enchantment level.
     * @return this item stack builder.
     *
     * @see Enchantment
     */
    public ItemStackBuilder withEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    /**
     * @param enchantments map of enchantments and their levels.
     * @return this item stack.
     *
     * @see Enchantment
     */
    public ItemStackBuilder withEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
        return this;
    }

    /**
     * @param key   the NBT key.
     * @param value the NBT string value.
     * @return this item stack builder.
     *
     * @see NBTItemStack
     */
    public ItemStackBuilder withNBTString(String key, String value) {
        nbtAppliers.add(item -> {
            NBTItemStack nbtItem = ItemStacks.toNBTItemStack(item);
            nbtItem.setString(key, value);
            return nbtItem.getItem();
        });

        return this;
    }

    /**
     * @param key   the NBT key.
     * @param value the NBT integer value.
     * @return this item stack builder.
     *
     * @see NBTItemStack
     */
    public ItemStackBuilder withNBTInteger(String key, int value) {
        nbtAppliers.add(item -> {
            NBTItemStack nbtItem = ItemStacks.toNBTItemStack(item);
            nbtItem.setInteger(key, value);
            return nbtItem.getItem();
        });

        return this;
    }

    /**
     * @param key   the NBT key.
     * @param value the NBT double value.
     * @return this item stack builder.
     *
     * @see NBTItemStack
     */
    public ItemStackBuilder withNBTDouble(String key, double value) {
        nbtAppliers.add(item -> {
            NBTItemStack nbtItem = ItemStacks.toNBTItemStack(item);
            nbtItem.setDouble(key, value);
            return nbtItem.getItem();
        });

        return this;
    }

    /**
     * @param key   the NBT key.
     * @param value the NBT boolean value.
     * @return this item stack builder.
     *
     * @see NBTItemStack
     */
    public ItemStackBuilder withNBTBoolean(String key, boolean value) {
        nbtAppliers.add(item -> {
            NBTItemStack nbtItem = ItemStacks.toNBTItemStack(item);
            nbtItem.setBoolean(key, value);
            return nbtItem.getItem();
        });

        return this;
    }

    /**
     * @param key   the NBT key.
     * @param value the NBT value.
     * @return this item stack builder.
     *
     * @see NBTItemStack
     */
    public ItemStackBuilder withNBTObject(String key, Object value) {
        nbtAppliers.add(item -> {
            NBTItemStack nbtItem = ItemStacks.toNBTItemStack(item);
            nbtItem.setObject(key, value);
            return nbtItem.getItem();
        });

        return this;
    }

    /**
     * @return stack type.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * @return stack amount.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @return stack data value.
     */
    public short getDurability() {
        return durability;
    }

    /**
     * @return stack display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return stack lore.
     */
    public List<String> getLore() {
        return lore;
    }

    /**
     * @return {@code true} if the stack is unbreakable.
     */
    public boolean isUnbreakable() {
        return unbreakable;
    }

    /**
     * @return set of item flags applied to the stack.
     *
     * @see ItemFlag
     */
    public Set<ItemFlag> getItemFlags() {
        return itemFlags;
    }

    /**
     * @return map of enchantments and their levels.
     */
    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    protected List<Applier> getNBTAppliers() {
        return nbtAppliers;
    }

    interface Applier {

        /**
         * @return the {@link ItemStack} with the applied contents.
         */
        ItemStack apply(ItemStack item);
    }
}