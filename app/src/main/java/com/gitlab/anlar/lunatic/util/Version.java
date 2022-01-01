/*
 * LunaticSMTP
 * Copyright (C) 2016  Anton Larionov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gitlab.anlar.lunatic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {
    private static final Version instance = new Version();

    private static final Logger log = LoggerFactory.getLogger(Version.class);

    private String version = "DEVEL";
    private String gitShortRevision = "DEVEL";

    public Version() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("version.properties")) {
            if (stream != null) {
                Properties prop = new Properties();
                prop.load(stream);

                version = prop.getProperty("version");
                gitShortRevision = prop.getProperty("gitShortRevision");
            }
        } catch (IOException e) {
            log.error("Failed to read version.properties file", e);
        }
    }

    public static String getVersion() {
        return instance.version;
    }

    public static String getGitShortRevision() {
        return instance.gitShortRevision;
    }
}
