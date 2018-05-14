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

/**
 * @author Bradley Steele
 */
public class NBTItemStack extends NBTCompound {

    private ItemStack itemStack;

    public NBTItemStack(ItemStack itemStack) {
        super(null, null);

        this.itemStack = itemStack.clone();
    }

    @Override
    public ItemStack getItem() {
        return itemStack;
    }

    @Override
    protected void setItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}