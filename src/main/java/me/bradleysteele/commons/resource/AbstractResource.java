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

/**
 * @author Bradley Steele
 */
public abstract class AbstractResource implements Resource {

    private final File file;
    private final ResourceReference reference;
    private final ResourceHandler handler;

    /**
     * @param file      a <b>valid</b> file location pointing to the resource.
     * @param reference the resource's reference.
     * @param handler   the handler which loaded this resource.
     */
    public AbstractResource(File file, ResourceReference reference, ResourceHandler handler) {
        this.file = file;
        this.reference = reference;
        this.handler = handler;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public ResourceReference getReference() {
        return reference;
    }

    @Override
    public ResourceHandler getHandler() {
        return handler;
    }

    /**
     * @return the loaded configuration file of the resource, or null if it had not been loaded correctly.
     */
    public abstract Object getConfiguration();

    /**
     * @param configuration the configuration to set.
     */
    public abstract void setConfiguration(Object configuration);
}