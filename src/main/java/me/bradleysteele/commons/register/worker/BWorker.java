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

package me.bradleysteele.commons.register.worker;

import me.bradleysteele.commons.BPlugin;
import me.bradleysteele.commons.register.Registrable;
import me.bradleysteele.commons.util.logging.StaticLog;
import me.bradleysteele.commons.util.reflect.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

/**
 * The {@link BWorker} class is a modular task handler,
 * designed to handle both Bukkit Events and run tasks.
 *
 * @author Bradley Steele
 */
public class BWorker implements Registrable, Listener, Runnable {

    private static final boolean HAS_IS_CANCELLED;

    static {
        // Check for 1.8
        HAS_IS_CANCELLED = Reflection.hasMethod(BukkitTask.class, "isCancelled");
    }

    protected BPlugin plugin;

    private long delay;
    private long period;
    private boolean sync;

    private BukkitTask task;

    @Override // Registrable
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        setRunning(true);

        plugin.getConsole().info(String.format("Registered worker: &a%s&r.", plugin.getLoggableName(this)));
    }

    @Override // Registrable
    public void unregister() {
        // Check the plugin is not disabling
        if (plugin.isEnabled()) {
            // Unregister EventHandlers
            HandlerList.unregisterAll(this);

            setRunning(false);
        }
    }

    @Override // Runnable
    public void run() {}

    /**
     * @return the ticks to wait until scheduling the task.
     */
    public long getDelay() {
        return delay;
    }

    /**
     * @return the ticks to wait before repeating the task.
     */
    public long getPeriod() {
        return period;
    }

    /**
     * @return if the task is running on the main thread.
     */
    public boolean isSync() {
        return sync;
    }

    /**
     * @return the active task or {@code null} if it is not active.
     */
    public BukkitTask getTask() {
        return task;
    }

    /**
     * @return whether the task is running.
     */
    public boolean isRunning() {
        return task != null && (!HAS_IS_CANCELLED || !task.isCancelled());
    }

    /**
     * Runs or cancels the worker task.
     *
     * @param run whether we should run the task or not.
     */
    public void setRunning(boolean run) {
        // The plugin must be enabled in order for us to run tasks.
        if (!plugin.isEnabled()) {
            StaticLog.error(String.format("Attempted to alter task &c%s &rwhile its parent is disabled.", plugin.getLoggableName(this)));
            return;
        }

        if (run) {
            // Check that we have a valid wait period.
            if (period <= 0) {
                return;
            }

            // Check that the task isn't already running.
            if (isRunning()) {
                if (task != null) {
                    StaticLog.error("Attempted to run task while it is already active. Active Id: &e" + task.getTaskId() + "&r.");
                }

                return;
            }

            if (sync) {
                task = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period);
            } else {
                task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period);
            }
        } else if (task != null) {
            task.cancel();
        }
    }

    /**
     * @param delay the ticks to wait until scheduling the task.
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * @param period the ticks to wait before repeating the task.
     */
    public void setPeriod(long period) {
        this.period = period;
    }

    /**
     * @param sync if the task is running on the main thread.
     */
    public void setSync(boolean sync) {
        this.sync = sync;
    }
}