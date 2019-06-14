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

package me.bradleysteele.commons.util;

import me.bradleysteele.commons.util.logging.StaticLog;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

/**
 * @author Bradley Steele
 */
public final class SystemInfos {

    private static final SystemInfo system = new SystemInfo();

    private SystemInfos() {}

    public static SystemInfo getSystemInfo() {
        return system;
    }

    public static OperatingSystem getOperatingSystem() {
        try {
            return system.getOperatingSystem();
        } catch (Throwable e) {
            StaticLog.error("Failed to receive OS information.");
            return null;
        }
    }

    public static HardwareAbstractionLayer getHardware() {
        try {
            return system.getHardware();
        } catch (Throwable e) {
            StaticLog.error("Failed to receive hardware information.");
            return null;
        }
    }
}