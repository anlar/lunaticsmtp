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

public class EmailServerHandler extends Observable implements SimpleMessageListener {

    private static final String TYPE_TEXT = "text/plain";
    private static final String TYPE_HTML = "text/html";

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
        String body = null;
        String bodyType = null;

        // single-part email
        if (content instanceof String) {
            if (isPlainText(message.getContentType())) {
                body = (String) content;
                bodyType = TYPE_TEXT;
            } else if (isHtml(message.getContentType())) {
                body = (String) content;
                bodyType = TYPE_HTML;
            }
        // multi-part email
        } else if (content instanceof Multipart) {
            Multipart mp = (Multipart) content;
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bp = mp.getBodyPart(i);

                if (isPlainText(bp.getContentType())) {
                    body = (String) bp.getContent();
                    bodyType = TYPE_TEXT;
                    break;
                } else if (isHtml(bp.getContentType())) {
                    body = (String) bp.getContent();
                    bodyType = TYPE_HTML;
                    break;
                }
            }
        }

        return new Email(rawContent, new Date(), subject, from, recipient, body, bodyType);
    }

    private boolean isPlainText(String value) {
        return isSuitableContentType(value, TYPE_TEXT);
    }

    private boolean isHtml(String value) {
        return isSuitableContentType(value, TYPE_HTML);
    }

    private boolean isSuitableContentType(String value, String type) {
        return Pattern.compile(Pattern.quote(type), Pattern.CASE_INSENSITIVE).matcher(value).find();
    }
}
