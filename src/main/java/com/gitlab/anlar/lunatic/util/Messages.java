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

package com.gitlab.anlar.lunatic.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final Messages instance = new Messages();

    private final ResourceBundle resources;

    private Messages() {
        resources = ResourceBundle.getBundle("i18n/messages", Locale.getDefault());
    }

    public static ResourceBundle getResources() {
        return instance.resources;
    }

    public static String get(String key) {
        try {
            return instance.resources.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
