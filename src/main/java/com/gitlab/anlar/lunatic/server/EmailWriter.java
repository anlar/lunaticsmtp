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

package com.gitlab.anlar.lunatic.server;

import com.gitlab.anlar.lunatic.dto.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class EmailWriter implements Observer {
    public interface Config {
        boolean isActive();

        String getDirectory();
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Config config;

    public EmailWriter(Config config) {
        this.config = config;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (config.isActive()) {
            Email email = (Email) arg;

            Path dir = Paths.get(config.getDirectory());
            Path file = Paths.get(config.getDirectory(), getFileName(email));

            try {
                Files.createDirectories(dir);
                try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                    writer.write(email.getContent());
                }
                log.info("Saved email to '{}'", file.toAbsolutePath());
            } catch (IOException e) {
                log.error("Failed to save email to '{}'", file.toAbsolutePath(), e);
            }
        }
    }

    private String getFileName(Email email) {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_").format(email.getDate())
                + UUID.randomUUID().toString().substring(0, 8)
                + ".eml";
    }
}
