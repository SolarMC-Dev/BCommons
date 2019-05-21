/*
 * Copyright 2019 Bradley Steele
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

package me.bradleysteele.commons.hook;

import me.bradleysteele.commons.BPlugin;
import me.bradleysteele.commons.register.Registrable;
import org.bukkit.Bukkit;

/**
 * @author Bradley Steele
 */
public abstract class PluginHook implements Registrable, Hook {

    private BPlugin plugin;

    private boolean hooked;
    private boolean late;

    @Override
    public void register() {
        if (isLate()) {
            // Waits for plugins to finish enabling before hooking.
            Bukkit.getScheduler().runTask(plugin, this::hook0);
        } else {
            hook0();
        }
    }

    @Override
    public void unregister() {
        if (isHooked()) {
            try {
                this.unhook();
            } catch (Exception e) {
                plugin.getConsole().error("Failed to unhook &e%s&r:", this.getClass().getSimpleName());
                plugin.getConsole().exception(e);
            }
        }
    }

    @Override
    public void unhook() {}

    @Override
    public boolean isHooked() {
        return hooked;
    }

    private void hook0() {
        try {
            this.hook();
            setHooked(true);
        } catch (Exception e) {
            // Ignored
            setHooked(false);
        }
    }

    /**
     * @return {@code true} if the hook is late.
     */
    public boolean isLate() {
        return late;
    }

    /**
     * @param late if the hook is late.
     */
    public void setLate(boolean late) {
        this.late = late;
    }

    /**
     * @param hooked if the plugin is hooked.
     */
    public void setHooked(boolean hooked) {
        this.hooked = hooked;
    }
}
