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

package me.bradleysteele.commons.nms;

import me.bradleysteele.commons.util.reflect.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * @author Bradley Steele
 */
public final class NMSReflection {

    /**
     * Splits at third period to retrieve the nms package version.
     * <p>
     * Split indexes:
     * 0 - "net"
     * 1 - "minecraft"
     * 2 - "server"
     * 3 - _version_
     */
    private static final String PACKAGE_VERSION = Bukkit.getServer().getClass().getPackage().getName()
            .split("\\.")[3];

    private static boolean LEGACY = false;

    // Package formats
    private static final String NMS_FORMAT = "net.minecraft.server.%s";
    private static final String CB_FORMAT = "org.bukkit.craftbukkit.%s";

    private static final ClassCache NMS_CLASS_CACHE = new ClassCache(String.format(NMS_FORMAT, PACKAGE_VERSION));
    private static final ClassCache CB_CLASS_CACHE = new ClassCache(String.format(CB_FORMAT, PACKAGE_VERSION));


    // Reflection

    public static final Class<?> CLASS_CRAFT_PLAYER;
    public static final Class<?> CLASS_ENTITY_PLAYER;

    private static final Method METHOD_CRAFT_PLAYER_GET_HANDLE;

    static {
        try {
            Class.forName("org.bukkit.GameRule");
        } catch (ClassNotFoundException e) {
            LEGACY = true;
        }

        CLASS_CRAFT_PLAYER = getCBClass("entity.CraftPlayer");
        CLASS_ENTITY_PLAYER = getNMSClass("EntityPlayer");

        METHOD_CRAFT_PLAYER_GET_HANDLE = Reflection.getMethod(CLASS_CRAFT_PLAYER, "getHandle");
    }

    private NMSReflection() {}

    /**
     * @return {@code true} if the server is legacy.
     */
    public static boolean isLegacy() {
        return LEGACY;
    }

    /**
     * Returns the server package version, example: v1_8_R3
     *
     * @return minecraft server package version.
     */
    public static String getPackageVersion() {
        return PACKAGE_VERSION;
    }

    /**
     * @param name nms simple class name.
     * @return the class or {@code null} if it does not exist.
     */
    public static Class<?> getNMSClass(String name) {
        return NMS_CLASS_CACHE.getAndCache(name);
    }

    /**
     * @param name craft bukkit simple class name.
     * @return the class or {@code null} if it does not exist.
     */
    public static Class<?> getCBClass(String name) {
        return CB_CLASS_CACHE.getAndCache(name);
    }

    // Util

    /**
     * Returns the result of invoking the getHandle method on the
     * provided player object.
     *
     * @param player the craft player.
     * @return the player's handle.
     */
    public static Object getEntityPlayer(Player player) {
        return Reflection.invokeMethod(METHOD_CRAFT_PLAYER_GET_HANDLE, player);
    }
}