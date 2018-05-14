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

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * @author Bradley Steele
 */
public interface BInventory extends InventoryHolder {

    /**
     * @param event     the event which was fired upon clicking.
     * @param clicker   player who clicked the inventory.
     * @param stack     {@link ItemStack} that was clicked.
     */
    default void onClick(InventoryClickEvent event, Player clicker, ItemStack stack) {}

    /**
     * @param event  the event fired upon closing.
     * @param player the player who closed the inventory.
     */
    default void onClose(InventoryCloseEvent event, Player player) {}
}