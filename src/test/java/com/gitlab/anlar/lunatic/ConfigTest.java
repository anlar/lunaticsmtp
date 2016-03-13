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
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ConfigTest {

    @Test
    public void createInstance() {
        Config config = Config.getInstance();
        TestCase.assertNotNull(config);
    }

    @Test
    public void configShortOptions() {
        String[] args = new String[]{"-d", "dir", "-h", "-j", "-n", "-p", "1234", "-s", "-t", "none", "-w"};
        configOptions(args);
    }

    @Test
    public void configLongOptions() {
        String[] args = new String[]{
                "--directory", "dir", "--help", "--jump-to-last", "--no-gui",
                "--port", "1234", "--start", "--tray-mode", "none", "--write"};
        configOptions(args);
    }

    private void configOptions(String[] args) {
        Config config = Config.getInstance();
        JCommander commander = Lunatic.createCommander(config, args);
        TestCase.assertNotNull(commander);
    }

    @Test
    public void printHelp() {
        Config config = Config.getInstance();
        JCommander commander = Lunatic.createCommander(config, new String[]{});

        // suppress output stream so help output won't be shown
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                // do nothing
            }
        }));

        Lunatic.printHelp(commander);
    }
}
