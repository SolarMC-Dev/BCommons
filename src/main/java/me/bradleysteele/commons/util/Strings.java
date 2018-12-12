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

/**
 * @author Bradley Steele
 */
public final class Strings {

    private Strings() {}

    /**
     * Retrieves the last string split in the array of matches of the
     * provided regular expression.
     *
     * @param str   string to split.
     * @param regex delimiting regular expression.
     * @param limit the result threshold.
     * @return last string split.
     *
     * @see String#split(String)
     */
    public static String splitRetrieveLast(String str, String regex, int limit) {
        String[] parts = str.split(regex, limit);

        if (parts.length == 0) {
            return str;
        }

        return parts[parts.length - 1];
    }

    /**
     * Retrieves the last string split in the array of matches of the
     * provided regular expression.
     *
     * @param str   string to split.
     * @param regex delimiting regular expression.
     * @return last string split.
     *
     * @see String#split(String)
     */
    public static String splitRetrieveLast(String str, String regex) {
        return splitRetrieveLast(str, regex, 0);
    }
}