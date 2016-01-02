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

import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import java.net.InetAddress;

public class EmailServer {
    private static EmailServerListener listener = new EmailServerListener();
    private static SMTPServer smtpServer = null;

    public static void start(int port, InetAddress bindAddress) {
        smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(listener), new SMTPAuthHandlerFactory());
        smtpServer.setBindAddress(bindAddress);
        smtpServer.setPort(port);
        smtpServer.start();
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
