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
import org.subethamail.smtp.AuthenticationHandlerFactory;

import java.util.ArrayList;
import java.util.List;

public class SMTPAuthHandlerFactory implements AuthenticationHandlerFactory {

    @Override
    public AuthenticationHandler create() {
        return new SMTPAuthHandler();
    }

    @Override
    public List<String> getAuthenticationMechanisms() {
        List<String> result = new ArrayList<String>();
        result.add("LOGIN");
        return result;
    }
}
