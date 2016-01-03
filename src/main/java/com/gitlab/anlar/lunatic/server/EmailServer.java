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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import java.net.BindException;
import java.net.InetAddress;

public class EmailServer {
    private static Logger log = LoggerFactory.getLogger(EmailServer.class);

    private static EmailServerListener listener = new EmailServerListener();
    private static SMTPServer smtpServer = null;

    public static StartResult start(int port, InetAddress bindAddress) {
        try {
            smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(listener), new SMTPAuthHandlerFactory());
            smtpServer.setBindAddress(bindAddress);
            smtpServer.setPort(port);
            smtpServer.start();

            return new StartResult(true);
        } catch (Throwable e) {
            log.error("Failed to start SMTP server", e);

            if (e.getCause() instanceof BindException) {
                return new StartResult(false, String.format("%s, port: %s", e.getCause().getMessage(), port));
            } else if (e.getCause() instanceof IllegalArgumentException && e.getMessage().contains("out of range")) {
                return new StartResult(false, String.format("Port out of range: %s", port));
            } else {
                return new StartResult(false, e.getMessage());
            }
        }
    }

    public static void stop() {
        if (smtpServer != null && smtpServer.isRunning()) {
            smtpServer.stop();
        }
    }

    public static boolean isRunning() {
        return smtpServer != null && smtpServer.isRunning();
    }

    public static EmailServerListener getListener() {
        return listener;
    }
}
