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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * @author Bradley Steele
 */
public class SkullBuilder extends ItemStackBuilder {

    private String owner;

    protected SkullBuilder(String owner) {
        super(new ItemStack(Material.SKULL_ITEM, 1, (short) 3));

        this.owner = owner;
    }

    protected SkullBuilder() {
        this(null);
    }

    @Override
    public ItemStack build() {
        ItemStack item = super.build();
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        if (owner != null) {
            meta.setOwner(owner);
        }

        item.setItemMeta(meta);

        // NBTs must be applied AFTER meta is applied.
        for (Applier applier : this.getNbtAppliers()) {
            item = applier.apply(item);
        }

        return item;
    }

    public SkullBuilder withOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public SkullBuilder withOwner(OfflinePlayer player) {
        this.owner = player.getName();
        return this;
    }

    public SkullBuilder withOwner(UUID uuid) {
        this.owner = Bukkit.getOfflinePlayer(uuid).getName();
        return this;
    }
}