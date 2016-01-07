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

import com.beust.jcommander.Parameter;

public class Config {
    private static final Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }

    @Parameter(names = {"-h", "--help"}, help = true, description = "Show short summary of options")
    private boolean help;

    @Parameter(names = {"-s", "--start"}, description = "Starts SMTP server at application launch")
    private boolean start = false;

    @Parameter(names = {"-n", "--no-gui"}, description = "Starts application without GUI (should be used with -s argument)")
    private boolean noGui = false;

    @Parameter(names = {"-p", "--port"}, description = "Specify port for SMTP server")
    private int port = 2527;

    @Parameter(names = {"-w", "--write"}, description = "Save incoming emails to disk")
    private boolean write = false;

    @Parameter(names = {"-d", "--directory"}, description = "Directory to save incoming messages")
    private String directory = "incoming";

    @Parameter(names = {"-j", "--jump-to-last"}, description = "Automatically select last received email in GUI")
    private boolean jumpToLast;

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isNoGui() {
        return noGui;
    }

    public void setNoGui(boolean noGui) {
        this.noGui = noGui;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public boolean isJumpToLast() {
        return jumpToLast;
    }

    public void setJumpToLast(boolean jumpToLast) {
        this.jumpToLast = jumpToLast;
    }
}
