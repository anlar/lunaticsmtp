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

package com.gitlab.anlar.lunatic.server.auth;

import org.subethamail.smtp.AuthenticationHandler;

public class SMTPAuthHandler implements AuthenticationHandler {
    private static final String USER_IDENTITY = "User";
    private static final String PROMPT_USERNAME = "334 VXNlcm5hbWU6"; // VXNlcm5hbWU6 is base64 for "Username:"
    private static final String PROMPT_PASSWORD = "334 UGFzc3dvcmQ6"; // UGFzc3dvcmQ6 is base64 for "Password:"

    private int pass = 0;

    @Override
    public String auth(String clientInput) {
        String prompt;

        if (++pass == 1) {
            prompt = SMTPAuthHandler.PROMPT_USERNAME;
        } else if (pass == 2) {
            prompt = SMTPAuthHandler.PROMPT_PASSWORD;
        } else {
            pass = 0;
            prompt = null;
        }
        return prompt;
    }

    @Override
    public Object getIdentity() {
        return SMTPAuthHandler.USER_IDENTITY;
    }
}
