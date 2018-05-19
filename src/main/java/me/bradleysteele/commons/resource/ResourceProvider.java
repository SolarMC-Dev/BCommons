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

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Responsible for handling the loading and saving of resources apart
 * of a specific plugin.
 *
 * @author Bradley Steele
 */
public interface ResourceProvider {

    /**
     * @param reference resource's reference containing the file path and extension.
     * @return a loaded resource.
     */
    Resource loadResource(ResourceReference reference);

    /**
     * Used for asynchronously loading resources without the need of surrounding the method
     * in a runnable.
     *
     * @param reference     resource's reference containing the file path and extension.
     * @param resultHandler result handler.
     */
    void loadResource(ResourceReference reference, ResourceLoadResultHandler resultHandler);

    /**
     * Saves a resource using the appropriate handlers.
     *
     * @param resource the resource to be saved.
     */
    void saveResource(Resource resource);

    /**
     * @param handler the resource handler to add.
     */
    void addResourceHandler(ResourceHandler handler);

    /**
     * @param reference the resource's reference.
     * @return the resource.
     */
    Resource getResource(ResourceReference reference);

    /**
     * @return the plugin's base data folder.
     */
    File getDataFolder();

}