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

import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import me.bradleysteele.commons.BPlugin;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Bradley Steele
 */
public class DefaultResourceProvider implements ResourceProvider {

    private final BPlugin plugin;
    private final File dataFolder;

    private Map<ResourceReference, Resource> cachedResources = Maps.newHashMap();
    private Map<String, ResourceHandler> resourceHandlers = Maps.newHashMap();

    public DefaultResourceProvider(BPlugin plugin) {
        this.plugin = plugin;
        dataFolder = plugin.getDataFolder();
    }

    @Override
    public Resource loadResource(ResourceReference reference) {
        File file = new File(dataFolder + reference.getSeparatorPathStart(), reference.getChild());

        try {
            Files.createParentDirs(file);

            if (!file.exists()) {
                plugin.getConsole().info("Loading resource defaults for &e" + reference.getSeparatorPathEnd() + reference.getChild() + "&r.");

                try (InputStream in = plugin.getResource(reference.getSeparatorPathEnd() + reference.getChild());
                     OutputStream out = new FileOutputStream(file)) {
                    ByteStreams.copy(in, out);
                }
            }
        } catch (IOException e) {
            plugin.getConsole().error("An IOException occurred when generating resource defaults for [&c" + reference.getPath() + "&r]:");
            plugin.getConsole().exception(e);
        } catch (NullPointerException e) {
            // plugin.getConsole().error("Failed to load resource defaults for: &e" + reference.getPath() + "&r.");
            // Temporarily ignored.
            // TODO: create way of loading a resource which is not stored in
            // TODO: the compiled jar.
        }

        Resource resource = getResourceHandler(reference.getExtension())
                .map(handler -> handler.load(this, reference))
                .orElse(null);

        cachedResources.put(reference, resource);
        return resource;
    }

    @Override
    public void loadResource(ResourceReference reference, ResourceLoadResultHandler resultHandler) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                resultHandler.onComplete(loadResource(reference));
            } catch (Exception e) {
                resultHandler.onFailure(e);
            }
        });
    }

    @Override
    public void saveResource(Resource resource) {
        getResourceHandler(resource.getReference().getExtension())
                .ifPresent(handler -> handler.save(resource));
    }

    @Override
    public void addResourceHandler(ResourceHandler loader) {
        loader.getExtensions()
                .stream()
                .filter(Objects::nonNull)
                .forEach(extension -> resourceHandlers.put(extension.toString(), loader));
    }

    @Override
    public Resource getResource(ResourceReference reference) {
        return cachedResources.containsKey(reference) ? cachedResources.get(reference) : loadResource(reference);
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    private Optional<ResourceHandler> getResourceHandler(String extension) {
        return resourceHandlers.values()
                .stream()
                .filter(handler -> handler.getExtensions().contains(extension))
                .findFirst();
    }
}