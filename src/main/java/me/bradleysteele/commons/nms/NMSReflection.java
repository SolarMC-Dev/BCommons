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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bradley Steele
 */
public final class NMSReflection {

    // Reflection cache
    private static final ClassCache NMS_CLASS_CACHE;
    private static final ClassCache CB_CLASS_CACHE = new ClassCache(String.format("org.bukkit.craftbukkit.%s", getPackageVersion()));

    static {
        if (isOldPackageStructure()) {
            NMS_CLASS_CACHE = new ClassCache(String.format("net.minecraft.server.%s", getPackageVersion()));
        } else {
            NMS_CLASS_CACHE = new ClassCache("net.minecraft");
        }
    }


    private NMSReflection() {}

    /**
     * @return {@code true} if the server is legacy.
     */
    public static boolean isLegacy() {
        // < 1.13 = "legacy" minecraft
        return getPackageVersionFloat() < 1.13f;
    }

    public static boolean isOldPackageStructure() {
        // >= 1.17 introduces new nsm package structure
        return getPackageVersionFloat() < 1.17f;
    }

    private static String PACKAGE_VERSION;

    /**
     * Returns the server package version, example: v1_8_R3
     *
     * @return minecraft server package version.
     */
    public static String getPackageVersion() {
        if (PACKAGE_VERSION == null) {
            // [0] - net       | org
            // [1] - minecraft | bukkit
            // [2] - server    | craftbukkit
            // [3] - VERSION
            PACKAGE_VERSION = Bukkit.getServer().getClass()
                    .getPackage()
                    .getName()
                    .split("\\.")[3];
        }

        return PACKAGE_VERSION;
    }

    private static Float PACKAGE_VERSION_FLOAT;

    /**
     * @return minecraft server package version.
     */
    public static float getPackageVersionFloat() {
        if (PACKAGE_VERSION_FLOAT == null) {
            Pattern pattern = Pattern.compile("[v]?(1_[0-9]+)");
            Matcher matcher = pattern.matcher(getPackageVersion());

            if (!matcher.find()) {
                throw new RuntimeException(String.format("failed to match package version from '%s'", getPackageVersion()));
            }

            PACKAGE_VERSION_FLOAT = Float.parseFloat(
                    // 1_17 -> 1.17
                    matcher.group(1).replace("_", ".")
            );
        }

        return PACKAGE_VERSION_FLOAT;
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

    // Common

    /**
     * @return nms EntityPlayer class.
     */
    public static Class<?> getEntityPlayer() {
        return isOldPackageStructure()
                ? getNMSClass("EntityPlayer")
                : getNMSClass("server.level.EntityPlayer");
    }

    private static Method METHOD_CRAFT_PLAYER_GET_HANDLE;

    /**
     * Returns the result of invoking the getHandle method on the
     * provided player object.
     *
     * @param player the craft player.
     * @return the player's handle.
     *
     * @see #getEntityPlayer()
     */
    public static Object getEntityPlayer(Player player) {
        if (METHOD_CRAFT_PLAYER_GET_HANDLE == null) {
            METHOD_CRAFT_PLAYER_GET_HANDLE = Reflection.getMethod(getCraftPlayer(), "getHandle");
        }

        return Reflection.invokeMethod(METHOD_CRAFT_PLAYER_GET_HANDLE, player);
    }

    /**
     * @return craft bukkit CraftPlayer class.
     */
    public static Class<?> getCraftPlayer() {
        return getCBClass("entity.CraftPlayer");
    }
}