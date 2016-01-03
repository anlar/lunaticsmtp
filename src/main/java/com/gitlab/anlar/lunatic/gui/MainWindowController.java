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
import com.gitlab.anlar.lunatic.dto.Email;
import com.gitlab.anlar.lunatic.server.EmailServer;
import com.gitlab.anlar.lunatic.server.StartResult;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private Logger log = LoggerFactory.getLogger(getClass());

    @FXML
    private TextField portField;
    @FXML
    private Button startButton;
    @FXML
    private Label messagesField;

    @FXML
    private WebView emailText;
    @FXML
    private TextArea rawText;
    @FXML
    private TableView messagesTable;

    @FXML
    private TextArea serverLog;

    @FXML
    private ObservableList<Email> messages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Config config = Config.getInstance();

        createLogPanelAppender();

        initListeners();
        initElements(config);

        if (config.isStart()) {
            // launch server in separate thread so it won't block main window appearance with warning dialog
            // in case if error will occur during it's start
            Platform.runLater(this::startServer);
        }
    }

    @FXML
    private void handleStartButton(ActionEvent event) {
        if (EmailServer.isRunning()) {
            stopServer();
        } else {
            startServer();
        }
    }

    @FXML
    private void handleClearButton(ActionEvent event) {
        messages.clear();
        updateMessagesCount();
    }

    private void createLogPanelAppender() {
        WriterAppender appender = new WriterAppender() {
            @Override
            public void append(LoggingEvent event) {
                Platform.runLater(() -> serverLog.appendText(layout.format(event)));
            }
        };
        appender.setName("GuiLogger");
        appender.setLayout(new PatternLayout("%d{HH:mm:ss.SSS} %-5p [%c{1}]: %m%n"));
        appender.setThreshold(Level.DEBUG);

        org.apache.log4j.Logger.getRootLogger().addAppender(appender);
    }

    private void initListeners() {
        //noinspection unchecked
        messagesTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (messagesTable.getSelectionModel().getSelectedItem() != null) {
                emailText.getEngine().loadContent(((Email) newValue).getBody());
                rawText.setText(((Email) newValue).getContent());
            } else {
                emailText.getEngine().loadContent("");
                rawText.setText(null);

            }
        });

        EmailServer.getListener().addObserver((o, arg) -> Platform.runLater(() -> {
            if (messages.isEmpty()) {
                messages.add((Email) arg);
                messagesTable.getSelectionModel().select(0);
            } else {
                messages.add((Email) arg);
            }

            updateMessagesCount();
        }));
    }

    private void initElements(Config config) {
        portField.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().replaceAll("[^0-9.,]", ""));
            return change;
        }));

        portField.setText(String.valueOf(config.getPort()));
        messagesField.setText("0");
        setStartButtonText(false);
    }

    private void startServer() {
        StartResult result = EmailServer.start(Integer.parseInt(portField.getText()), null);

        if (result.isSuccessful()) {
            portField.setDisable(true);
            setStartButtonText(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("LunaticSMTP error");
            alert.setHeaderText("Failed to start SMTP server");
            alert.setContentText(String.format(
                    "%s\nYou may check application logs for additional details", result.getMessage()));

            alert.showAndWait();
        }
    }

    private void stopServer() {
        EmailServer.stop();
        portField.setDisable(false);
        setStartButtonText(false);
    }

    private void setStartButtonText(boolean isRunning) {
        startButton.setText(isRunning ? "Stop" : "Start");
    }

    private void updateMessagesCount() {
        messagesField.setText(String.valueOf(messages.size()));
    }
}
