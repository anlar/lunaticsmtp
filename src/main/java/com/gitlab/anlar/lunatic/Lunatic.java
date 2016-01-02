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

package com.gitlab.anlar.lunatic;

import com.beust.jcommander.JCommander;
import com.gitlab.anlar.lunatic.gui.LunaticApplication;

public class Lunatic {

    public static void main(String[] args) {
        Config config = Config.getInstance();
        JCommander commander = new JCommander(config, args);

        if (config.isHelp()) {
            commander.usage();
        } else {
            LunaticApplication.launch(LunaticApplication.class, args);
        }
    }
}
