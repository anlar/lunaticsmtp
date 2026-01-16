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

import com.gitlab.anlar.lunatic.server.EmailServer;
import com.gitlab.anlar.lunatic.util.Messages;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import junit.framework.TestCase;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;

public class GuiTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        Parent sceneRoot = getRootNode();
        Scene scene = new Scene(sceneRoot, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void startAndStop() {
        clickOn("#startButton");
        TestCase.assertTrue("Server hasn't started after start button activation", EmailServer.isRunning());
        clickOn("#startButton");
        TestCase.assertFalse("Server hasn't stopped after stop button activation", EmailServer.isRunning());
    }

    private Parent getRootNode() {
        try {
            return FXMLLoader.load(getClass().getClassLoader().getResource("gui/main_window.fxml"), Messages.getResources());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
