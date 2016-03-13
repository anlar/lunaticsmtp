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
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class EmailServerHandler extends Observable implements SimpleMessageListener {
    private static final Logger log = LoggerFactory.getLogger(EmailServerHandler.class);

    private static final String TYPE_TEXT = "text/plain";
    private static final String TYPE_HTML = "text/html";

    private SaverConfig config;

    private Lock storageLock;
    private List<Email> storage;

    public EmailServerHandler(SaverConfig config) {
        this.config = config;

        this.storageLock = new ReentrantLock();
        this.storage = new LinkedList<>();
    }

    @Override
    public boolean accept(String from, String recipient) {
        return true;
    }

    @Override
    public void deliver(String from, String recipient, InputStream data) throws IOException {
        try {
            Email email = parseMessage(from, recipient, data);
            String filePath = save(email);
            email.setFilePath(filePath);

            putIntoStorage(email);
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

    private String save(Email email) {
        if (config.isActive()) {
            Path dir = Paths.get(config.getDirectory());
            Path file = Paths.get(config.getDirectory(), getFileName(email));

            try {
                Files.createDirectories(dir);
                try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                    writer.write(email.getContent());
                }
                log.info("Saved email to '{}'", file.toAbsolutePath());
                return file.toAbsolutePath().toString();
            } catch (IOException e) {
                log.error("Failed to save email to '{}'", file.toAbsolutePath(), e);
            }
        }

        return null;
    }

    private String getFileName(Email email) {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_").format(email.getDate())
                + UUID.randomUUID().toString().substring(0, 8)
                + ".eml";
    }

    // Storage related

    private void putIntoStorage(Email email) {
        storageLock.lock();
        try {
            storage.add(email);
        } finally {
            storageLock.unlock();
        }

        this.setChanged();
        this.notifyObservers(new Event(Event.Type.incoming, email));
    }

    protected void clearStorage() {
        storageLock.lock();
        try {
            storage.stream().filter(email -> email.getFilePath() != null).forEach(email -> {
                try {
                    Files.deleteIfExists(Paths.get(email.getFilePath()));
                    log.info("Deleted email file '{}'", email.getFilePath());
                } catch (IOException e) {
                    log.warn("Failed to delete email file '{}'", email.getFilePath(), e);
                }
            });

            storage.clear();
        } finally {
            storageLock.unlock();
        }

        this.setChanged();
        this.notifyObservers(new Event(Event.Type.clear));
    }
}
