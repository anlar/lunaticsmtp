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

package com.gitlab.anlar.lunatic.gui;

import com.gitlab.anlar.lunatic.Config;
import com.gitlab.anlar.lunatic.server.EmailServer;
import com.gitlab.anlar.lunatic.util.Messages;
import com.gitlab.anlar.lunatic.util.Version;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class LunaticApplication extends Application {

    private TrayIcon trayIcon;

    @Override
    public void start(Stage stage) throws Exception {
        Config.TrayMode trayMode = Config.getInstance().getTrayMode();

        //noinspection ConstantConditions
        Parent root = FXMLLoader.load(
                getClass().getClassLoader().getResource("gui/main_window.fxml"), Messages.getResources());

        Scene scene = new Scene(root, 1024, 768);

        //noinspection ConstantConditions
        scene.getStylesheets().add(getClass().getClassLoader().getResource("gui/main.css").toExternalForm());

        stage.setTitle("LunaticSMTP " + Version.getVersion());
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));
        stage.setScene(scene);

        if (!Config.TrayMode.minimize.equals(trayMode)) {
            stage.show();
        }

        if (SystemTray.isSupported() && !Config.TrayMode.none.equals(trayMode)) {
            initTray(stage);
        }
    }

    @Override
    public void stop() throws Exception {
        EmailServer.stop();

        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
        }
    }

    private void initTray(Stage stage) throws IOException, AWTException {
        // set to false so app won't exit when it's main stage is hidden
        Platform.setImplicitExit(false);

        SystemTray tray = SystemTray.getSystemTray();

        BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("icon.png"));

        PopupMenu popup = new PopupMenu();
        MenuItem item = new MenuItem(Messages.get("gui.tray.exit"));
        popup.add(item);

        item.addActionListener(e -> Platform.exit());

        trayIcon = new TrayIcon(image, null, popup);
        trayIcon.setImageAutoSize(true);

        trayIcon.addActionListener(e -> {
            if (stage.isShowing()) {
                Platform.runLater(stage::hide);
            } else {
                Platform.runLater(stage::show);
            }
        });

        tray.add(trayIcon);
    }
}
