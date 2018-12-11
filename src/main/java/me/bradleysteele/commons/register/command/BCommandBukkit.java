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

import me.bradleysteele.commons.util.logging.StaticLog;
import me.bradleysteele.commons.util.reflect.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Bradley Steele
 */
public class BCommandBukkit extends Command {

    private static final Field COMMAND_MAP = Reflection.getField(Bukkit.getServer().getClass(), "commandMap");

    public static SimpleCommandMap getCommandMap() {
        return Reflection.getFieldValue(COMMAND_MAP, Bukkit.getServer());
    }

    private final BCommand command;

    protected BCommandBukkit(BCommand command) {
        super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());

        this.command = command;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        try {
            command.executeCalled(sender, args);
        } catch (Exception e) {
            StaticLog.error("An exception occurred when executing the command &c" + label + "&r:");
            StaticLog.exception(e);
            return false;
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> result = command.tabCalled(sender, alias, args);

        if (result == null) {
            // Fallback to builtin
            result = super.tabComplete(sender, alias, args);
        }

        return result;
    }

    /**
     * @return the internal command.
     */
    public BCommand getCommand() {
        return command;
    }
}