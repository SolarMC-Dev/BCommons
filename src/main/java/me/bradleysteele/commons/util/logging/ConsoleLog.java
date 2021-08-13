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

package me.bradleysteele.commons.util.logging;

import me.bradleysteele.commons.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.slf4j.LoggerFactory; // Solar

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * @author Bradley Steele
 */
public class ConsoleLog {

    private String logFormat = "[BCommons] [{bcommons_log_level}]: {bcommons_log_message}";

    /**
     * @param message the message to log.
     * @param args    arguments referenced by the format specifiers
     *                in the format string.
     */
    public void log(String message, Object... args) {
        LoggerFactory.getLogger(getClass()).info(String.format(message, args));
    }

    /**
     * @param messages the messages to log.
     */
    public void log(String... messages) {
        Arrays.asList(messages).forEach(this::log);
    }

    /**
     * @param messages the messages to log.
     */
    public void log(Iterable<? extends String> messages) {
        messages.forEach(this::log);
    }

    /**
     * @param level   severity of the log.
     * @param message the message to log.
     * @param args    arguments referenced by the format specifiers
     *                in the format string.
     */
    public void log(ConsoleLevel level, String message, Object... args) {
        log(Messages.colour(logFormat
                .replace("{bcommons_log_level}", level.getTagColour() + level.getTag() + ChatColor.RESET)
                .replace("{bcommons_log_message}", message)), args);
    }

    /**
     * @param message the message to debug.
     * @param args    arguments referenced by the format specifiers
     *                in the format string.
     */
    public void debug(String message, Object... args) {
        log(ConsoleLevel.DEBUG, message, args);
    }

    /**
     * @param message the message to inform.
     * @param args    arguments referenced by the format specifiers
     *                in the format string.
     */
    public void info(String message, Object... args) {
        log(ConsoleLevel.INFO, message, args);
    }

    /**
     * @param message the message to warn.
     * @param args    arguments referenced by the format specifiers
     *                in the format string.
     */
    public void warn(String message, Object... args) {
        log(ConsoleLevel.WARN, message, args);
    }

    /**
     * @param throwable the throwable to output.
     */
    public void exception(Throwable throwable) {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));

        Arrays.asList(writer.toString()
                .split(System.lineSeparator()))
                .forEach(line -> log(ConsoleLevel.EXCEPTION, line));
    }

    /**
     * @param message the message to error.
     * @param args    arguments referenced by the format specifiers
     *                in the format string.
     */
    public void error(String message, Object... args) {
        log(ConsoleLevel.ERROR, message, args);
    }

    public String getFormat() {
        return logFormat;
    }

    public void setFormat(String logFormat) {
        this.logFormat = logFormat;
    }
}
