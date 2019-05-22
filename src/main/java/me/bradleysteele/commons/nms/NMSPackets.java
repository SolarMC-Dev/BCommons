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
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Bradley Steele
 */
public final class NMSPackets {

    private NMSPackets() {}

    private static Method METHOD_PLAYER_CONNECTION_SEND_PACKET;

    /**
     * Sends packets to the provided player.
     *
     * @param player  packet receiver.
     * @param packets packets to send to the receiver.
     */
    public static void sendPacket(Player player, Object... packets) {
        if (METHOD_PLAYER_CONNECTION_SEND_PACKET == null) {
            METHOD_PLAYER_CONNECTION_SEND_PACKET = Reflection.getMethod(getPlayerConnection(), "sendPacket", getPacket());
        }

        Object connection = getPlayerConnection(player);

        for (Object packet : packets) {
            Reflection.invokeMethod(METHOD_PLAYER_CONNECTION_SEND_PACKET, connection, packet);
        }
    }

    public static Class<?> getPacket() {
        return NMSReflection.getNMSClass("Packet");
    }

    public static Class<?> getPlayerConnection() {
        return NMSReflection.getNMSClass("PlayerConnection");
    }

    private static Field FIELD_ENTITY_PLAYER_PLAYER_CONNECTION;

    /**
     * Returns the PlayerConnection object of the Player.
     *
     * @param player the player.
     * @return the player's PlayerConnection instance.
     */
    public static Object getPlayerConnection(Player player) {
        if (FIELD_ENTITY_PLAYER_PLAYER_CONNECTION == null) {
            FIELD_ENTITY_PLAYER_PLAYER_CONNECTION = Reflection.getField(NMSReflection.getEntityPlayer(), "playerConnection");
        }

        return Reflection.getFieldValue(FIELD_ENTITY_PLAYER_PLAYER_CONNECTION, NMSReflection.getEntityPlayer(player));
    }
}