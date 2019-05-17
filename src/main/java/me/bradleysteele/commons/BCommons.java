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

package me.bradleysteele.commons;

import me.bradleysteele.commons.hook.dependency.DependencyLoader;
import me.bradleysteele.commons.hook.dependency.MavenDependency;
import me.bradleysteele.commons.util.logging.ConsoleLogHandler;
import me.bradleysteele.commons.worker.WorkerBInventory;

import java.util.logging.Logger;

/**
 * @author Bradley Steele
 */
public class BCommons extends BPlugin {

    public BCommons() {
        this.register(DependencyLoader.class);

        DependencyLoader.get().load(
                // HttpClient
                new MavenDependency("org.apache.httpcomponents", "httpclient","4.5.6"),
                new MavenDependency("org.apache.httpcomponents", "httpcore", "4.4.10"),
                new MavenDependency("commons-logging", "commons-logging", "1.2"),
                new MavenDependency("commons-codec", "commons-codec", "1.10"),

                // Oshi
                new MavenDependency("com.github.oshi", "oshi-core", "3.13.2"),
                new MavenDependency("net.java.dev.jna", "jna", "5.3.1"),
                new MavenDependency("net.java.dev.jna", "jna-platform", "5.3.1"),
                new MavenDependency("org.slf4j", "slf4j-api", "1.7.25")
        );
    }

    @Override
    public void enable() {
        Logger.getLogger("BPlugin").addHandler(new ConsoleLogHandler());

        this.register(WorkerBInventory.class);
    }
}