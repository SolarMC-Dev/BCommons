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

package me.bradleysteele.commons.register.command;

import com.google.common.collect.Lists;
import me.bradleysteele.commons.BPlugin;
import me.bradleysteele.commons.register.Registrable;
import me.bradleysteele.commons.util.Players;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * @author Bradley Steele
 */
public abstract class BCommand implements Registrable, BCommandExecutor {

    protected BPlugin plugin;

    private String name;
    private String description;
    private String usage;
    private String permission;
    private List<String> permissionDenyMessage;
    private boolean allowConsole;
    private List<String> aliases = Lists.newArrayList();
    private List<BCommand> children = Lists.newArrayList();

    private BCommandExecutor executor = this;
    private BCommand parent;

    @Override
    public final void register() {
        // Update children which were set in the constructor, as
        // the plugin is injected after the constructor.
        children.forEach(child -> {
            child.plugin = plugin;
            child.parent = this;
        });

        if (isRoot()) {
            if (BCommandBukkit.getCommandMap().register(plugin.getName(), new BCommandBukkit(this))) {
                plugin.getConsole().info("Registered command: &a" + getName() + " &rwith &e" + children.size() + " &rchildren.");
            } else {
                plugin.getConsole().error("Failed to register command: &c" + getName() + "&r.");
            }
        }

        children.forEach(BCommand::register);
    }

    final void called(CommandSender sender, String[] args) {
        if (!children.isEmpty() && args.length > 0) {
            for (BCommand child : children) {
                if (child.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(args[0]))) {
                    List<String> list = Lists.newArrayList(args);
                    list.remove(0);

                    child.called(sender, list.toArray(new String[0]));
                    return;
                }
            }
        }

        if (sender instanceof ConsoleCommandSender && !isAllowConsole()) {
            plugin.getConsole().error("&c" + getName() + " &rdoes not have console support.");
            return;
        }

        if (permission != null && !sender.hasPermission(permission)) {
            if (permissionDenyMessage != null && !permissionDenyMessage.isEmpty() && (sender instanceof Player)) {
                Players.sendMessage(Players.getPlayer(sender), permissionDenyMessage);
            }

            return;
        }

        // All tests past, execute.
        executor.execute(sender, args);
    }

    /**
     * If the {@link BCommand#name} is not present, the first alias will be returned, or
     * {@code null} if no aliases are set.
     *
     * @return command's name.
     */
    public String getName() {
        return name != null ? name : aliases.size() > 0 ? aliases.get(0) : null;
    }

    /**
     * The commands description describes what the command does, not how to use it.
     *
     * @return command's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return command's usage.
     */
    public String getUsage() {
        return usage != null ? usage : "/" + name;
    }

    /**
     * @return command's permission or {@code null} if none.
     */
    public String getPermission() {
        return permission;
    }

    /**
     * @return message to send to the sender if they do not have permission to
     *         execute the command.
     */
    public List<String> getPermissionDenyMessage() {
        return permissionDenyMessage;
    }

    /**
     * @return if the console is allowed to execeute the command.
     */
    public boolean isAllowConsole() {
        return allowConsole;
    }

    /**
     * @return a list of aliases, including the command's "main" executor.
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * @return list of child commands, can be empty.
     */
    public List<BCommand> getChildren() {
        return children;
    }

    /**
     * @return the command's executor.
     */
    public BCommandExecutor getExecutor() {
        return executor;
    }

    /**
     * @param parent the command's parent.
     */
    public void setParent(BCommand parent) {
        this.parent = parent;
    }

    /**
     * @return {@code true} if the command has no parent.
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * @param name command's name, usually main or common executor.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description should not contain how to use the command.
     *
     * @param description command's description, describing what the command does.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The usage should not contain what the command does.
     *
     * @param usage command's usage, how to use it.
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * @param permission the permission required to execute the command, or null
     *                   if no permission is required.
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * @param permissionDenyMessage the messages to send to the sender if they do not have permission.
     */
    public void setPermissionDenyMessage(List<String> permissionDenyMessage) {
        this.permissionDenyMessage = permissionDenyMessage;
    }

    /**
     * @param allowConsole if the console is allowed to execute the command.
     */
    public void setAllowConsole(boolean allowConsole) {
        this.allowConsole = allowConsole;
    }

    /**
     * @param aliases new list of aliases.
     */
    public void setAliases(List<String> aliases) {
        this.aliases.clear();
        aliases.forEach(this::addAlias);
    }

    /**
     * @param aliases new list of aliases.
     */
    public void setAliases(String... aliases) {
        setAliases(Arrays.asList(aliases));
    }

    /**
     * Checks to see whether the provided alias is already present in the list,
     * if so it will not add it - this is to avoid duplicate and redundant aliases.
     *
     * @param alias the alias to add.
     */
    public void addAlias(String alias) {
        if (!aliases.contains(alias)) {
            aliases.add(alias);
        }
    }

    /**
     * @param children new list of children.
     */
    public void setChildren(List<BCommand> children) {
        this.children.clear();
        children.forEach(this::addChild);
    }

    /**
     * @param children new list of children.
     */
    public void setChildren(BCommand... children) {
        setChildren(Arrays.asList(children));
    }

    /**
     * @param child the child command to add.
     */
    public void addChild(BCommand child) {
        if (!children.contains(child)) {
            child.plugin = plugin;
            child.parent = parent;
            children.add(child);
        }
    }

    /**
     * @param executor the command's executor.
     */
    public void setExecutor(BCommandExecutor executor) {
        this.executor = executor;
    }
}