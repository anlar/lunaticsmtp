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
import com.beust.jcommander.Parameters;

@Parameters(resourceBundle = "i18n/messages")
public class Config {
    public enum TrayMode {
        none,
        enable,
        minimize
    }

    private static final Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }

    @Parameter(names = {"-h", "--help"}, help = true, descriptionKey = "cli.help")
    private boolean help;

    @Parameter(names = {"-v", "--version"}, descriptionKey = "cli.version")
    private boolean version;

    @Parameter(names = {"-s", "--start"}, descriptionKey = "cli.start")
    private boolean start = false;

    @Parameter(names = {"-n", "--no-gui"}, descriptionKey = "cli.nogui")
    private boolean noGui = false;

    @Parameter(names = {"-p", "--port"}, descriptionKey = "cli.port")
    private int port = 2525;

    @Parameter(names = {"-w", "--write"}, descriptionKey = "cli.write")
    private boolean write = false;

    @Parameter(names = {"-c", "--cleanup"}, descriptionKey = "cli.cleanup")
    private boolean cleanup = false;

    @Parameter(names = {"-d", "--directory"}, descriptionKey = "cli.directory")
    private String directory = "incoming";

    @Parameter(names = {"-j", "--jump-to-last"}, descriptionKey = "cli.jump")
    private boolean jumpToLast;

    @Parameter(names = {"-t", "--tray-mode"}, descriptionKey = "cli.tray")
    private TrayMode trayMode = TrayMode.none;

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
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

    public boolean isCleanup() {
        return cleanup;
    }

    public void setCleanup(boolean cleanup) {
        this.cleanup = cleanup;
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

    public TrayMode getTrayMode() {
        return trayMode;
    }

    public void setTrayMode(TrayMode trayMode) {
        this.trayMode = trayMode;
    }
}
