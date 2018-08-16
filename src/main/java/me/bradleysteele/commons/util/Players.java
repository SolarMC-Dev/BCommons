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

package me.bradleysteele.commons.util;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Bradley Steele
 */
public final class Players {

    private Players() {}

    public static String apostrophiseName(String name) {
        if (name.endsWith("s") || name.endsWith("z")) {
            return name + "'";
        }

        return name + "'s";
    }

    public static String apostrophiseName(Player player) {
        return apostrophiseName(player.getName());
    }

    public static String apostrophiseName(OfflinePlayer player) {
        return apostrophiseName(player.getName());
    }

    public static String apostrophiseName(CommandSender sender) {
        return apostrophiseName(sender.getName());
    }

    /**
     * Colours then sends the message to the player.
     *
     * @param receiver  who is receiving the message.
     * @param message   what we're sending.
     */
    public static void sendMessage(CommandSender receiver, String message) {
        if (isOnline(receiver)) {
            receiver.sendMessage(Messages.colour(message));
        }
    }

    /**
     * @param receiver  who is receiving the message.
     * @param messages  what we're sending.
     */
    public static void sendMessage(CommandSender receiver, String... messages) {
        sendMessage(receiver, Arrays.asList(messages));
    }

    /**
     * @param receiver who is receiving the message.
     * @param messages what we're sending.
     */
    public static void sendMessage(CommandSender receiver, Iterable<? extends String> messages) {
        messages.forEach(message -> sendMessage(receiver, message));
    }

    /**
     * @param receivers who is receiving the message.
     * @param message   what we're sending.
     */
    public static void sendMessage(Iterable<? extends CommandSender> receivers, String message) {
        receivers.forEach(receiver -> sendMessage(receiver, message));
    }

    /**
     * @param receivers who is receiving the message.
     * @param messages  what we're sending.
     */
    public static void sendMessage(Iterable<? extends CommandSender> receivers, Iterable<? extends String> messages) {
        receivers.forEach(receiver -> sendMessage(receiver, messages));
    }

    /**
     * @param player    the player we're sending the message to.
     * @param component the component to send.
     */
    public static void sendMessage(OfflinePlayer player, BaseComponent component) {
        if (isOnline(player)) {
            Bukkit.getPlayer(player.getUniqueId()).spigot().sendMessage(component);
        }
    }

    /**
     * @param players  iterable of player's to send the message to.
     * @param component what we're sending.
     */
    public static void sendMessage(Iterable<? extends OfflinePlayer> players, BaseComponent component) {
        players.forEach(player -> sendMessage(player, component));
    }

    /**
     * @param player   the player we're teleporting.
     * @param location the location to teleport the player to.
     */
    public static void teleport(Player player, Location location) {
        player.teleport(location);
    }

    /**
     * @param players  iterable of players to teleport.
     * @param location the location to teleport the players to.
     */
    public static void teleport(Iterable<? extends Player> players, Location location) {
        players.forEach(player -> teleport(player, location));
    }

    /**
     *
     * @param players  array of players to teleport.
     * @param location the location to teleport the players to.
     */
    public static void teleport(Player[] players, Location location) {
        teleport(Arrays.asList(players), location);
    }

    /**
     * @param location the location to teleport the players to.
     * @param players  array of players to teleport.
     */
    public static void teleport(Location location, Player... players) {
        teleport(players, location);
    }

    /**
     * @param player the player we're checking.
     * @return if the player is online.
     */
    public static boolean isOnline(Player player) {
        return player != null && player.isOnline();
    }

    /**
     * @param player the player we're checking.
     * @return if the player is online.
     */
    public static boolean isOnline(OfflinePlayer player) {
        return player != null && player.isOnline();
    }

    /**
     * @param sender command sender to check.
     * @return if the command sender is online.
     */
    public static boolean isOnline(CommandSender sender) {
        return !(sender instanceof OfflinePlayer) || isOnline((OfflinePlayer) sender);
    }

    /**
     * @param s comma-separated player names.
     * @return list of players.
     */
    public static List<Player> getPlayersFromCSV(String s) {
        return getPlayers(s.replace(" ", ""), ",");
    }

    /**
     * @param s         string of player names.
     * @param separator the separator between each player's name.
     * @return list of players.
     */
    public static List<Player> getPlayers(String s, String separator) {
        return (s.equals("*") || s.equals("**")) ? getOnlinePlayers()
                : Arrays.stream(s.split(separator))
                .map(Players::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * @return a list of online players.
     */
    public static List<Player> getOnlinePlayers() {
        return Lists.newArrayList(Bukkit.getOnlinePlayers());
    }

    /**
     * @param s exact or part of the player's name.
     * @return the player, or null if no player is online.
     */
    public static Player getPlayer(String s) {
        Player player = Bukkit.getPlayerExact(s);

        if (player == null) {
            player = Bukkit.getPlayer(s);
        }

        return player;
    }

    /**
     * @param uuid unique id of the player.
     * @return the player or {@code null} if the player is not online.
     */
    public static Player getPlayer(UUID uuid) {
        return uuid != null ? Bukkit.getPlayer(uuid) : null;
    }

    /**
     * @param sender the sender to cast.
     * @return player casted from the provided sender or {@code null} if
     *         the sender was not a player.
     */
    public static Player getPlayer(CommandSender sender) {
        return sender instanceof Player ? (Player) sender : null;
    }

    /**
     * @param s player's name.
     * @return the player, or null if no player is online.
     */
    public static OfflinePlayer getOfflinePlayer(String s) {
        OfflinePlayer player = Bukkit.getPlayer(s);

        if (player == null) {
            player = Bukkit.getOfflinePlayer(s);
        }

        if (player == null) {
            player = Bukkit.getOfflinePlayer(UUID.fromString(s));
        }

        return player;
    }

    /**
     * @param uuid player's uuid.
     * @return the player matching the uuid.
     */
    public static OfflinePlayer getOfflinePlayer(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid);
    }
}