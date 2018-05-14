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

import org.bukkit.ChatColor;

import java.util.logging.Level;

/**
 * @author Bradley Steele
 */
public enum ConsoleLevel {

    /** Primarily used for debugging purposes, should never be in public builds. */
    DEBUG(0, "Debug"),

    /** Used when wanting to provide general information. */
    INFO(10, "Info"),

    /**
     * Alerts the console of a fault, for example: an incorrect value
     * in a configuration file. This level should not be used when
     * the program crashes or unable to complete the executed task.
     */
    WARN(25, "Warn", ChatColor.YELLOW),

    /**
     * Used only for exceptions, as to easily point them out.
     */
    EXCEPTION(50, "Exception", ChatColor.DARK_RED),

    /**
     * Only used for when a task is unable to proceed. This is usually used
     * when an invalid input or exception is handled, but does not have a
     * safe fallback.
     */
    ERROR(100, "Error", ChatColor.DARK_RED);

    /**
     * Translate a {@link Level} to {@link ConsoleLevel}.
     *
     * @param level log's level.
     * @return console log level.
     *
     * @see java.util.logging.Level
     */
    public static ConsoleLevel from(Level level) {
        return level == Level.INFO ? INFO
                : level == Level.WARNING ? WARN
                : level == Level.SEVERE ? ERROR
                : DEBUG;
    }

    private final int level;
    private final String tag;
    private final ChatColor tagColour;

    /**
     * @param level     severity of the log.
     * @param tag       string tag used as an output prefix.
     * @param tagColour colour of the tag.
     */
    ConsoleLevel(int level, String tag, ChatColor tagColour) {
        this.level = level;
        this.tag = tag;
        this.tagColour = tagColour;
    }

    ConsoleLevel(int level, String tag) {
        this(level, tag, ChatColor.GREEN);
    }

    /**
     * @return log's severity level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return log's string tag, can contain spaces.
     */
    public String getTag() {
        return tag;
    }

    /**
     * @return the tag's colour.
     */
    public ChatColor getTagColour() {
        return tagColour;
    }
}