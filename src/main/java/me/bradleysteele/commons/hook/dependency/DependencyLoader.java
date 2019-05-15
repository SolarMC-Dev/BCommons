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

package me.bradleysteele.commons.hook.dependency;

import me.bradleysteele.commons.BPlugin;
import me.bradleysteele.commons.register.Registrable;
import me.bradleysteele.commons.util.logging.StaticLog;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * @author Bradley Steele
 */
public final class DependencyLoader implements Registrable {

    private static final DependencyLoader instance = new DependencyLoader();

    public static DependencyLoader get() {
        return instance;
    }

    private BPlugin plugin;
    private File folder;

    @Override
    public void register() {
        folder = new File(plugin.getDataFolder().getParent() + "/.bdependencies");

        try {
            if (!folder.exists()) {
                Files.createDirectories(folder.toPath());

                if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
                    Files.setAttribute(folder.toPath(), "dos:hidden", true);
                }
            }
        } catch (IOException e) {
            StaticLog.error("Failed to load dependency folder:");
            StaticLog.exception(e);
        } catch (Exception e) {
            // Ignored
        }
    }

    /**
     * @return the folder holding the dependencies.
     */
    public File getFolder() {
        return folder;
    }

    /**
     * @param dependency the dependency to load
     */
    public void load(Dependency dependency) {
        URL url;

        try {
            url = new URL(dependency.getDownloadURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        File file = new File(folder, dependency.getFileName());

        try {
            if (!file.exists()) {
                FileUtils.copyURLToFile(url, file);
            }
        } catch (IOException e) {
            StaticLog.error(String.format("Failed to load dependency &c%s&r:", dependency.getFileName()));
            StaticLog.exception(e);
        }
    }

    public void load(Dependency... dependencies) {
        Arrays.stream(dependencies).forEach(this::load);
    }

    public void load(String fileName, String downloadURL) {
        load(new Dependency(fileName, downloadURL));
    }
}