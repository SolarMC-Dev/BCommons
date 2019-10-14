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

package me.bradleysteele.commons.inventory;

import me.bradleysteele.commons.util.reflect.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Bradley Steele
 */
public final class Inventories {

    private static final Method METHOD_INVENTORY_GET_TITLE = Reflection.getMethod(Inventory.class, "getTitle");
    private static final Method METHOD_INVENTORY_GET_STORAGE_CONTENTS = Reflection.getMethod(Inventory.class, "getStorageContents");

    private Inventories() {}

    /**
     * @param inventory the inventory to clone.
     * @param holder    new inventory holder.
     * @return new inventory with a cloned contents of the provided inventory.
     */
    public static Inventory clone(Inventory inventory, InventoryHolder holder) {
        if (METHOD_INVENTORY_GET_TITLE == null) {
            throw new IllegalArgumentException("Inventory#getTitle() is not available in this version");
        }

        return clone(inventory, holder, Reflection.invokeMethod(METHOD_INVENTORY_GET_TITLE, inventory));
    }

    /**
     * @param inventory the inventory to clone.
     * @param holder    new inventory holder.
     * @param title     the inventory title.
     * @return new inventory with a cloned contents of the provided inventory.
     */
    public static Inventory clone(Inventory inventory, InventoryHolder holder, String title) {
        int size = inventory.getSize();

        // 1.14 #getSize() includes armour, shield & off-hand, causing issues
        // as its not a multiple of 9.
        if (inventory.getType() == InventoryType.PLAYER) {
            size = 36;
        }

        Inventory inv = Bukkit.createInventory(holder, size, title);

        if (METHOD_INVENTORY_GET_STORAGE_CONTENTS != null) {
            inv.setContents(inventory.getStorageContents().clone());
        } else {
            inv.setContents(inventory.getContents().clone());
        }

        return inv;
    }

    /**
     * Checks to see if an item stack can fit inside an inventory,
     * includes stacks which aren't at their max stack size.
     *
     * @param inventory inventory to check.
     * @param stacks    item stacks to check.
     * @return {@code true} if the item stack can fit in the inventory.
     */
    public static boolean fits(Inventory inventory, ItemStack... stacks) {
        return clone(inventory, null, "_").addItem(stacks).isEmpty();
    }

    /**
     * @param inventory the inventory to put the items in.
     * @param stack     item stack to clone.
     * @param slots     array of slots to clone the item stack into.
     */
    public static void setItem(Inventory inventory, ItemStack stack, int... slots) {
        Arrays.stream(slots).forEach(slot -> inventory.setItem(slot, stack.clone()));
    }

    /**
     * @param inventory the inventory to put the items in.
     * @param stack     item stack to clone.
     * @param row       row of the inventory to fill.
     */
    public static void setRow(Inventory inventory, ItemStack stack, int row) {
        int rowStart = row * 9;
        int rowEnd = rowStart + 8;

        for (int slot = rowStart; slot <= rowEnd; slot++) {
            inventory.setItem(slot, stack.clone());
        }
    }
}