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
import com.gitlab.anlar.lunatic.server.SaverConfig;
import com.gitlab.anlar.lunatic.server.StartResult;
import com.gitlab.anlar.lunatic.util.Messages;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private TextField portField;
    @FXML
    private Button startButton;
    @FXML
    private Label messagesField;

    @FXML
    private TextField dirField;
    @FXML
    private Button dirButton;
    @FXML
    private CheckBox saveDirCheck;

    @FXML
    private WebView emailText;
    @FXML
    private TextArea rawText;
    @FXML
    private TableView<Email> messagesTable;

    @FXML
    private TextArea serverLog;

    @FXML
    private ObservableList<Email> messages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Config config = Config.getInstance();

        createLogPanelAppender();

        initListeners(config);
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
        serverLog.clear();
    }

    @FXML
    private void handleDirButton(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(dirButton.getScene().getWindow());
        if (file != null) {
            dirField.setText(file.getAbsolutePath());
        }
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

    private void initListeners(Config config) {
        EmailServer.initEmailWriter(new SaverConfig() {
            @Override
            public boolean isActive() {
                return saveDirCheck.isSelected();
            }

            @Override
            public String getDirectory() {
                return dirField.getText();
            }
        });

        messagesTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (messagesTable.getSelectionModel().getSelectedItem() != null) {
                emailText.getEngine().loadContent(newValue.getBody(), newValue.getBodyType());
                rawText.setText(newValue.getContent());
            } else {
                emailText.getEngine().loadContent("");
                rawText.setText(null);

            }
        });

        EmailServer.addObserver((o, arg) -> Platform.runLater(() -> {
            messages.add((Email) arg);
            updateMessagesCount();

            if (config.isJumpToLast()) {
                messagesTable.getSelectionModel().selectLast();
            }
        }));
    }

    private void initElements(Config config) {
        portField.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().replaceAll("[^0-9.,]", ""));
            return change;
        }));

        if (SystemUtils.IS_OS_UNIX) {
            // change web-cache dir
            // from: $HOME/.com.gitlab.anlar.lunatic.gui.LunaticApplication/webview
            // to:   $HOME/.cache/lunatic-smtp/webview/
            this.emailText.getEngine().setUserDataDirectory(new File(
                    SystemUtils.USER_HOME + File.separator + ".cache" + File.separator + "lunatic-smtp",
                    "webview"));
        }

        portField.setText(String.valueOf(config.getPort()));
        messagesField.setText("0");
        setStartButtonText(false);

        dirField.setText(config.getDirectory());
        saveDirCheck.setSelected(config.isWrite());
    }

    private void startServer() {
        StartResult result = EmailServer.start(Integer.parseInt(portField.getText()));

        if (result.isSuccessful()) {
            portField.setDisable(true);
            setStartButtonText(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(Messages.get("gui.error.start.title"));
            alert.setHeaderText(Messages.get("gui.error.start.header"));
            alert.setContentText(String.format("%s\n" + Messages.get("gui.error.start.body"), result.getMessage()));

            // expand default width to make sure that second content line fill fit there
            alert.setResizable(true);
            alert.getDialogPane().setPrefWidth(500);

            alert.showAndWait();
        }
    }

    private void stopServer() {
        EmailServer.stop();
        portField.setDisable(false);
        setStartButtonText(false);
    }

    private void setStartButtonText(boolean isRunning) {
        startButton.setText(isRunning ? Messages.get("gui.button.stop") : Messages.get("gui.button.start"));
    }

    private void updateMessagesCount() {
        messagesField.setText(String.valueOf(messages.size()));
    }
}
