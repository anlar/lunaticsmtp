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

import com.gitlab.anlar.lunatic.Config;
import com.gitlab.anlar.lunatic.dto.Email;
import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

public class ServerTest {
    private int serverPort = Config.getInstance().getPort();

    @Test
    public void startStop() {
        initNullEmailWriter();
        startServer(serverPort);
        stopServer();
    }

    @Test
    public void receiveSinglePartEmail() throws MessagingException {
        initNullEmailWriter();
        startServer(serverPort);

        String from = "from@mail.test";
        String to = "to@mail.test";
        String subject = "Single-part subject";
        String body = "Single-part body";

        EmailServer.addObserver((o, arg) -> {
            Event event = (Event) arg;
            TestCase.assertEquals(Event.Type.incoming, event.getType());
            checkEmail(from, to, subject, body, event.getEmail());
        });

        sendSinglePartEmail(from, to, subject, body);
        stopServer();
    }

    @Test
    public void receiveMultiPartEmail() throws MessagingException {
        initNullEmailWriter();
        startServer(serverPort);

        String from = "from@mail.test";
        String to = "to@mail.test";
        String subject = "Multi-part subject";
        String body = "Multi-part body";

        EmailServer.addObserver((o, arg) -> {
            Event event = (Event) arg;
            TestCase.assertEquals(Event.Type.incoming, event.getType());
            checkEmail(from, to, subject, body, event.getEmail());
        });

        sendMultiPartEmail(from, to, subject, body);
        stopServer();
    }

    // Email creation/sending

    private void sendSinglePartEmail(String from, String to, String subject, String body) throws MessagingException {
        Properties props = createEmailProps(serverPort);
        Session session = Session.getInstance(props);

        Message msg = createBaseMessage(from, to, subject, session);
        msg.setText(body);

        Transport.send(msg);
    }

    private void sendMultiPartEmail(String from, String to, String subject, String body) throws MessagingException {
        Properties props = createEmailProps(serverPort);
        Session session = Session.getInstance(props);

        Message msg = createBaseMessage(from, to, subject, session);

        MimeBodyPart p1 = new MimeBodyPart();
        p1.setText(body);

        MimeBodyPart p2 = new MimeBodyPart();
        p2.setText("Second part");

        Multipart mp = new MimeMultipart();
        mp.addBodyPart(p1);
        mp.addBodyPart(p2);

        msg.setContent(mp);

        Transport.send(msg);
    }

    private Properties createEmailProps(int port) {
        Properties props = new Properties();

        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", port);
        props.put("mail.debug", "true");

        return props;
    }

    private Message createBaseMessage(String from, String to, String subject, Session session) throws MessagingException {
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(to)});
        msg.setSentDate(new Date());
        msg.setSubject(subject);

        return msg;
    }

    private void checkEmail(String from, String to, String subject, String body, Email email) {
        TestCase.assertNotNull(email);
        TestCase.assertEquals(from, email.getFrom());
        TestCase.assertEquals(to, email.getTo());
        TestCase.assertEquals(subject, email.getSubject());
        TestCase.assertEquals(StringUtils.trim(body), StringUtils.trim(email.getBody()));
    }

    // Server control

    private void startServer(int port) {
        StartResult result = EmailServer.start(port);
        TestCase.assertEquals("Failed to startServer server", true, result.isSuccessful());
    }

    private void stopServer() {
        EmailServer.stop();
        TestCase.assertEquals("Failed to stopServer server", false, EmailServer.isRunning());
    }

    private void initNullEmailWriter() {
        EmailServer.initEmailWriter(new SaverConfig() {
            @Override
            public boolean isActive() {
                return false;
            }

            @Override
            public String getDirectory() {
                return null;
            }
        });
    }
}
