/*
 * Copyright 2019 Bradley Steele
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

/**
 * @author Bradley Steele
 */
public class MavenDependency extends Dependency {

    private static final String NAME = "%s-%s.jar";
    private static final String DOWNLOAD_URL = "https://search.maven.org/remotecontent?filepath=%s/%s/%s/%s";

    public MavenDependency(String pckg, String name, String version) {
        super(String.format(NAME, name, version), String.format(DOWNLOAD_URL, pckg.replace(".", "/"), name, version, String.format(NAME, name, version)));
    }
}