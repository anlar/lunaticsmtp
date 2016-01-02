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
import org.subethamail.smtp.helper.SimpleMessageListener;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Observable;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

public class EmailServerListener extends Observable implements SimpleMessageListener {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean accept(String from, String recipient) {
        return true;
    }

    @Override
    public void deliver(String from, String recipient, InputStream data) throws IOException {
        try {
            Email email = parseMessage(from, recipient, data);

            this.setChanged();
            this.notifyObservers(email);
        } catch (MessagingException e) {
            // do nothing
        }
    }

    private Email parseMessage(String from, String recipient, InputStream data) throws MessagingException, IOException {
        String rawContent = new Scanner(data, "UTF-8").useDelimiter("\\A").next();

        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(rawContent.getBytes(StandardCharsets.UTF_8)));
        String subject = message.getSubject();

        Object content = message.getContent();
        StringBuilder body = new StringBuilder();
        if (content instanceof Multipart) {
            Multipart mp = (Multipart) content;
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bp = mp.getBodyPart(i);
                if (Pattern.compile(Pattern.quote("text/plain"), Pattern.CASE_INSENSITIVE).matcher(bp.getContentType()).find()) {
                    body.append(bp.getContent());
                } else {
                    // todo: html
                }
            }
        }

        return new Email(rawContent, new Date(), subject, from, recipient, body.toString());
    }
}
