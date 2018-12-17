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

import me.bradleysteele.commons.hook.dependency.Dependency;
import me.bradleysteele.commons.hook.dependency.DependencyLoader;
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
                new Dependency("oshi-core-3.4.0.jar", "https://cdn.bradleysteele.me/oshi-core-3.4.0"),
                new Dependency("jna-platform-4.2.2.jar", "https://cdn.bradleysteele.me/jna-platform-4.2.2"),
                new Dependency("jna-4.2.2.jar", "https://cdn.bradleysteele.me/jna-4.2.2"),
                new Dependency("slf4j-api-1.7.22.jar", "https://cdn.bradleysteele.me/slf4j-api-1.7.22")
        );
    }

    @Override
    public void enable() {
        Logger.getLogger("BPlugin").addHandler(new ConsoleLogHandler());

        this.register(WorkerBInventory.class);
    }
}