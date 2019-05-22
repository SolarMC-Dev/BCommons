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

import me.bradleysteele.commons.itemstack.nbt.NBTItemStack;
import me.bradleysteele.commons.nms.NMSReflection;
import me.bradleysteele.commons.resource.ResourceSection;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Bradley Steele
 */
public final class ItemStacks {

    public static final Material PLAYER_HEAD;

    static {
        PLAYER_HEAD = NMSReflection.isLegacy() ? Material.matchMaterial("SKULL_ITEM") : Material.matchMaterial("PLAYER_HEAD");
    }

    private ItemStacks() {}

    /**
     * Returns an item stack builder with the provided material.
     * <p>
     * Returns a {@link SkullBuilder} if a {@link ItemStacks#PLAYER_HEAD}
     * is provided.
     *
     * @param material the stack material type.
     * @return item stack builder with the provided material type.
     *
     * @see ItemStackBuilder
     */
    public static ItemStackBuilder builder(Material material) {
        return material == PLAYER_HEAD ? new SkullBuilder() : new ItemStackBuilder(material);
    }

    /**
     * Returns a new object of the provided item stack builder,
     * with the same attributes.
     *
     * @param builder the builder to copy.
     * @return a copy of the provided item stack builder.
     *
     * @see ItemStackBuilder
     */
    public static ItemStackBuilder builder(ItemStackBuilder builder) {
        return new ItemStackBuilder(builder);
    }

    /**
     * Wraps the provided stack into an item stack builder.
     *
     * @param stack the stack to wrap into a builder.
     * @return wrapped item stack builder.
     *
     * @see ItemStackBuilder
     */
    public static ItemStackBuilder builder(ItemStack stack) {
        return stack.getType() == PLAYER_HEAD ? new SkullBuilder(stack) : new ItemStackBuilder(stack);
    }

    /**
     * @return item stack builder with type {@link Material#AIR}.
     *
     * @see ItemStackBuilder
     */
    public static ItemStackBuilder builder() {
        return builder(Material.AIR);
    }

    /**
     * @param owner the skull owner (player name).
     * @return skull builder with the provided owner.
     *
     * @see SkullBuilder
     */
    public static SkullBuilder skullBuilder(String owner) {
        return new SkullBuilder(owner);
    }

    /**
     * @return blank skull builder.
     *
     * @see SkullBuilder
     */
    public static SkullBuilder skullBuilder() {
        return skullBuilder(null);
    }

    /**
     * Wraps the provided stack into an {@link NBTItemStack}.
     *
     * @param stack the stack to convert.
     * @return wrapped NBT stack.
     *
     * @see NBTItemStack
     */
    public static NBTItemStack toNBTItemStack(ItemStack stack) {
        return new NBTItemStack(stack);
    }

    /**
     * @param section resource section to unwrap.
     * @return item stack builder with attributes provided by
     *         the resource section.
     *
     * @see ResourceSection
     * @see ItemStackBuilder
     */
    public static ItemStackBuilder toItemStackBuilder(ResourceSection section) {
        if (section == null) {
            return null;
        }

        int amount = section.getInt("amount", 1);

        return builder()
                .withMaterial(Material.matchMaterial(section.getString("material", "AIR")))
                .withAmount(amount < 1 ? 1 : amount > 64 ? 64 : amount)
                .withDurability(section.getShort("damage", (short) 0))
                .withDisplayNameColoured(section.getString("name"))
                .withLoreColoured(section.getStringList("lore"))
                .withUnbreakable(section.getBoolean("unbreakable"));
    }

    /**
     * Unwraps a {@link ResourceSection} into a {@link ItemStack}.
     *
     * @param section resource section to unwrap.
     * @return unwrapped item stack.
     *
     * @see ResourceSection
     */
    public static ItemStack toItemStack(ResourceSection section) {
        return toItemStackBuilder(section).build();
    }

    /**
     * @param player the player's name.
     * @return stack with the provided player name as
     *         the skull.
     *
     * @see SkullBuilder
     */
    public static ItemStack skullOf(String player) {
        return skullBuilder()
                .withOwner(player)
                .build();
    }

    public static ItemStack skullOf(OfflinePlayer player) {
        return skullOf(player.getName());
    }

    /**
     * @param url the skull's url.
     * @return stack with the provided url as the skull.
     *
     * @see SkullBuilder
     */
    public static ItemStack skullOfURL(String url) {
        return skullBuilder()
                .withURL(url)
                .build();
    }

    /**
     * @param stack the stack to test.
     * @return {@code true} if the stack is null or {@link Material#AIR}.
     */
    public static boolean isBlank(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR;
    }

    /**
     * @param stack the stack to test.
     * @return {@code true} if the stack is a player head.
     */
    public static boolean isPlayerHead(ItemStack stack) {
        return stack.getType() == PLAYER_HEAD && (!NMSReflection.isLegacy() || stack.getDurability() == 3);
    }

    public static String serializeItems(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            // Ignored
        }

        return null;
    }

    public static String serializeItem(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);
            dataOutput.close();

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            // Ignored
        }

        return null;
    }

    public static ItemStack[] deserializeItems(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (Exception e) {
            // Ignored
        }

        return null;
    }

    public static ItemStack deserializeItem(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();

            dataInput.close();
            return item;
        } catch (Exception e) {
            // Ignored
        }

        return null;
    }
}