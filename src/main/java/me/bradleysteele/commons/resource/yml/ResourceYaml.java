/*
 * Copyright 2017 Bradley Steele
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

package me.bradleysteele.commons.resource.yml;

import me.bradleysteele.commons.resource.AbstractResource;
import me.bradleysteele.commons.resource.ResourceHandler;
import me.bradleysteele.commons.resource.ResourceReference;
import me.bradleysteele.commons.resource.ResourceSection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Bradley Steele
 */
public class ResourceYaml extends AbstractResource {

    private ConfigurationSection root;

    public ResourceYaml(File file, ResourceReference reference, ResourceHandler handler) {
        super(file, reference, handler);
    }

    public ResourceYaml(ResourceYaml resource, ConfigurationSection root) {
        super(resource.getFile(), resource.getReference(), resource.getHandler());
        this.root = root;
    }

    @Override
    public boolean isRoot() {
        return root.getParent() == null;
    }

    @Override
    public boolean contains(String path) {
        return root.contains(path);
    }

    @Override
    public ResourceSection createSection(String name) {
        if (contains(name)) {
            return getSection(name);
        }

        return new ResourceYaml(this, root.createSection(name));
    }

    @Override
    public String getName() {
        return root.getName();
    }

    @Override
    public String getCurrentPath() {
        return root.getCurrentPath();
    }

    @Override
    public ResourceSection getRoot() {
        return new ResourceYaml(this, root.getRoot());
    }

    @Override
    public ResourceSection getParent() {
        return new ResourceYaml(this, root.getParent());
    }

    @Override
    public ConfigurationSection getConfiguration() {
        return root;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return root.getKeys(deep);
    }

    @Override
    public <T> T get(String path, Class<T> type, T def) {
        Object ret = root.get(path);

        if (ret == null || !ret.getClass().isInstance(type)) {
            return def; // default
        }

        return type.cast(ret);
    }

    @Override
    public String getString(String path, String def) {
        return root.getString(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return root.getBoolean(path, def);
    }

    @Override
    public byte getByte(String path, byte def) {
        return (byte) root.getInt(path, def);
    }

    @Override
    public char getChar(String path, char def) {
        try {
            return getString(path, Character.toString(def)).charAt(0);
        } catch (StringIndexOutOfBoundsException | NullPointerException e) {
            /* Ignored */
        }

        return def;
    }

    @Override
    public short getShort(String path, short def) {
        return (short) root.getInt(path, def);
    }

    @Override
    public int getInt(String path, int def) {
        return root.getInt(path, def);
    }

    @Override
    public long getLong(String path, long def) {
        return root.getLong(path, def);
    }

    @Override
    public float getFloat(String path, float def) {
        return (float) root.getDouble(path, def);
    }

    @Override
    public double getDouble(String path, double def) {
        return root.getDouble(path, def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getList(String path, Class<T> clazz) {
        return (List<T>) root.getList(path, new ArrayList<T>());
    }

    @Override
    public ResourceSection getSection(String path) {
        if (!contains(path)) {
            return null;
        }

        return new ResourceYaml(this, root.getConfigurationSection(path));
    }

    @Override
    public void set(String path, Object object) {
        root.set(path, object);
    }

    @Override
    public void setConfiguration(Object configuration) {
        if (configuration instanceof ConfigurationSection) {
            this.root = (ConfigurationSection) configuration;
        }
    }

    public ConfigurationSection createSection(String path, Map<?, ?> keyValues) {
        return root.createSection(path, keyValues);
    }

    public YamlConfiguration getRootConfigurationSection() {
        return (YamlConfiguration) root;
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return root.getConfigurationSection(path);
    }
}