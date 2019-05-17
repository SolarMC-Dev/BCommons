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
import me.bradleysteele.commons.resource.ResourceSection;
import me.bradleysteele.commons.util.reflect.NBTReflection;
import org.bukkit.Material;
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
        PLAYER_HEAD = NBTReflection.isLegacy() ? Material.matchMaterial("SKULL_ITEM") : Material.matchMaterial("PLAYER_HEAD");
    }

    private ItemStacks() {}

    public static ItemStackBuilder builder(Material material) {
        return NBTReflection.isLegacy() && material == PLAYER_HEAD ? new SkullBuilder() : new ItemStackBuilder(material);
    }

    public static ItemStackBuilder builder(ItemStackBuilder builder) {
        return new ItemStackBuilder(builder);
    }

    public static ItemStackBuilder builder(ItemStack item) {
        return new ItemStackBuilder(item);
    }

    public static ItemStackBuilder builder() {
        return builder(Material.AIR);
    }

    public static SkullBuilder skullBuilder(String owner) {
        return new SkullBuilder(owner);
    }

    public static SkullBuilder skullBuilder() {
        return skullBuilder(null);
    }

    public static NBTItemStack toNBTItemStack(ItemStack item) {
        return new NBTItemStack(item);
    }

    /**
     * Unwraps a {@link ResourceSection} into a {@link ItemStack}.
     *
     * @param section resource section to unwrap.
     * @return unwrapped item stack.
     */
    public static ItemStack toItemStack(ResourceSection section) {
        return toItemStackBuilder(section).build();
    }

    public static ItemStackBuilder toItemStackBuilder(ResourceSection section) {
        if (section == null) {
            return null;
        }

        int amount = section.getInt("amount", 1);

        return builder(Material.matchMaterial(section.getString("material", "AIR")))
                .withAmount(amount < 1 ? 1 : amount > 64 ? 64 : amount)
                .withDurability(section.getShort("damage", (short) 0))
                .withDisplayNameColoured(section.getString("name"))
                .withLoreColoured(section.getStringList("lore"))
                .withUnbreakable(section.getBoolean("unbreakable"));
    }

    public static ItemStack skullOf(String player) {
        return skullBuilder()
                .withOwner(player)
                .build();
    }

    public static ItemStack skullOfURL(String url) {
        return skullBuilder()
                .withURL(url)
                .build();
    }

    public static boolean isBlank(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR;
    }

    public static boolean isPlayerHead(ItemStack stack) {
        return stack.getType() == PLAYER_HEAD && (!NBTReflection.isLegacy() || stack.getDurability() == 3);
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