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

import me.bradleysteele.commons.util.Strings;
import me.bradleysteele.commons.util.logging.StaticLog;

/**
 * An unloaded resource which references to both the
 * resource's path and extension.
 *
 * @author Bradley Steele
 */
public class ResourceReference implements ResourceItem {

    private final String parent;
    private final String child;
    private final String extension;

    public ResourceReference(String parent, String child, String extension) {
        if (extension.startsWith(".")) {
            StaticLog.warn("Resource reference extension [&e" + extension + "&r] starts with a period! Resource"
                    + " extensions should not include periods, example: [&eyml&r].");
        }

        this.parent = parent;
        this.child = child;
        this.extension = extension;
    }

    public ResourceReference(String child, String extension) {
        this("", child, extension);
    }

    public ResourceReference(String parent, String child, Extension extension) {
        this(parent, child, extension.getExtension());
    }

    public ResourceReference(String child, Extension extension) {
        this(child, extension.getExtension());
    }

    public ResourceReference(String child) {
        this(child, Extension.from(Strings.splitRetrieveLast(child, "\\.")));
    }

    @Override
    public String toString() {
        return "ResourceReference[Parent:" + parent + ",Child:" + child + ",Extension:" + extension +"]";
    }

    public String getPath() {
        return parent + (hasParent() ? "/" : "") + child;
    }

    public String getSeparatorPathStart() {
        return hasParent() ? "/" + parent : "";
    }

    public String getSeparatorPathEnd() {
        return hasParent() ? parent + "/" : "";
    }

    public String getParent() {
        return parent;
    }

    public String getChild() {
        return child;
    }

    /**
     * Returns the resource's extension, for example: <i>/plugins/BExample/config.yml</i>
     * will return <i>yml</i>. The period before the extension should not be included.
     * This is dependant on the resource's reference.
     *
     * @return the resource's extension.
     */
    public String getExtension() {
        return extension;
    }

    public boolean hasParent() {
        return parent != null && !parent.equals("");
    }
}
