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

import com.gitlab.anlar.lunatic.server.auth.SMTPAuthHandlerFactory;
import com.gitlab.anlar.lunatic.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import java.net.BindException;
import java.util.Observer;

public class EmailServer {
    private static final Logger log = LoggerFactory.getLogger(EmailServer.class);

    private static EmailServerHandler listener = null;
    private static SMTPServer smtpServer = null;

    public static void initEmailWriter(SaverConfig config) {
        listener = new EmailServerHandler(config);
    }

    public static StartResult start(int port) {
        try {
            smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(listener), new SMTPAuthHandlerFactory());
            smtpServer.setPort(port);
            smtpServer.start();

            return new StartResult(true, null);
        } catch (Throwable e) {
            log.error("Failed to start SMTP server", e);

            if (e.getCause() instanceof BindException) {
                return new StartResult(false, String.format(Messages.get("%s, port: %s"), e.getCause().getMessage(), port));
            } else if (e.getCause() instanceof IllegalArgumentException && e.getMessage().contains("out of range")) {
                return new StartResult(false, String.format(Messages.get("server.error.outofrange"), port));
            } else {
                return new StartResult(false, e.getLocalizedMessage());
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

    public static void addObserver(Observer observer) {
        listener.addObserver(observer);
    }

    public static void clear() {
        listener.clearStorage();
    }
}
