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

import java.util.List;

/**
 * The {@link ResourceHandler} interface is used for loading and saving
 * different types of files.
 *
 * @author Bradley Steele
 */
public interface ResourceHandler {

    /**
     * @param provider the resource provider.
     * @param reference resource's reference.
     * @return a new or existing resource if {@code fromCache} is true.
     */
    Resource load(ResourceProvider provider, ResourceReference reference);

    /**
     * @param resource the resource we're saving.
     */
    void save(Resource resource);

    /**
     * Extensions do not have a period ('{@literal .}') at the start.
     *
     * @return a list containing all possible extensions.
     */
    List<? extends CharSequence> getExtensions();

}