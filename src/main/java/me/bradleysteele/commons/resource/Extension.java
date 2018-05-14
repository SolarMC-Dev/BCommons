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

package me.bradleysteele.commons.resource;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Bradley Steele
 */
public enum Extension {

    YML("yml", "yaml")

    ;


    private final List<String> extensions;


    /**
     * @param extensions array of extensions for the file type.
     */
    Extension(String... extensions) {
        this.extensions = Lists.newArrayList(extensions);
    }

    /**
     * @return all of the extensions.
     */
    public List<String> getExtensions() {
        return extensions;
    }

    /**
     * @return the first extension.
     */
    public String getExtension() {
        return extensions.iterator().next();
    }
}