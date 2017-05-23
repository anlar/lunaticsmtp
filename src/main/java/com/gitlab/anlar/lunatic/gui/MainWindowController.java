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
import com.gitlab.anlar.lunatic.dto.EmailPart;
import com.gitlab.anlar.lunatic.server.EmailServer;
import com.gitlab.anlar.lunatic.server.Event;
import com.gitlab.anlar.lunatic.server.SaverConfig;
import com.gitlab.anlar.lunatic.server.StartResult;
import com.gitlab.anlar.lunatic.util.Messages;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Header;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(MainWindowController.class);

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
    public TabPane emailScreenTabPane;

    @FXML
    public GridPane emailHeader;

    @FXML
    public Label emailCcLabel;

    @FXML
    public TextField emailFrom;
    @FXML
    public TextField emailTo;
    @FXML
    public TextField emailCc;
    @FXML
    public TextField emailSubject;
    @FXML
    public TextField emailDate;
    @FXML
    public ComboBox<EmailPart> emailPart;

    @FXML
    public Button viewButton;

    @FXML
    private WebView emailText;
    @FXML
    private TextArea sourceText;
    @FXML
    private CustomTextField tableFilter;
    @FXML
    private TableView<Email> messagesTable;

    @FXML
    private TextArea serverLog;

    @FXML
    private ObservableList<Email> messages;
    private FilteredList<Email> filteredMessages;

    @FXML
    private ObservableList<EmailPart> parts;
    @FXML
    private ObservableList<Header> headers;

    private Desktop desktop;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Config config = Config.getInstance();

        createLogPanelAppender();

        initListeners(config);
        initTable();
        initTableFilter();
        initControlPanel(config);
        initEmailViewer();
        initEmailButtons();

        loadSavedEmails(config);

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
        EmailServer.clear();
    }

    @FXML
    private void handleDirButton(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(dirButton.getScene().getWindow());
        if (file != null) {
            dirField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void handleViewButton(ActionEvent event) {
        if (desktop != null) {
            Email email = messagesTable.getSelectionModel().getSelectedItem();

            try {
                Path file = Files.createTempFile("lunaticsmtp-", ".eml");
                file.toFile().deleteOnExit();

                try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                    writer.write(email.getContent());
                }

                EventQueue.invokeLater(() -> {
                    try {
                        this.desktop.browse(file.toUri());
                    } catch (IOException e) {
                        log.error("Failed to open file {}", file.toString(), e);
                    }
                });
            } catch (IOException e) {
                log.error("Failed to create temp file", e);
            }
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
        appender.setLayout(new PatternLayout("%d{HH:mm:ss.SSS} - %m%n"));

        org.apache.log4j.Logger.getRootLogger().addAppender(appender);
    }

    private void initListeners(Config config) {
        EmailServer.init(new SaverConfig() {
            @Override
            public boolean isActive() {
                return saveDirCheck.isSelected();
            }

            @Override
            public String getDirectory() {
                return dirField.getText();
            }
        }, config.isWrite(), config.getDirectory());

        tableFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isNotBlank(newValue)) {
                filteredMessages.setPredicate(email -> StringUtils.containsIgnoreCase(email.getSubject(), newValue));
            } else {
                filteredMessages.setPredicate(null);
            }
        });

        messagesTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (messagesTable.getSelectionModel().getSelectedItem() != null) {
                emailScreenTabPane.getSelectionModel().select(1);

                emailFrom.setText(newValue.getFrom());
                emailTo.setText(newValue.getTo());
                emailCc.setText(newValue.getCc());
                emailSubject.setText(newValue.getSubject());
                emailDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(newValue.getDate()));

                boolean hasCc = StringUtils.isNotBlank(newValue.getCc());

                emailCc.setVisible(hasCc);
                emailCc.setManaged(hasCc);
                emailCcLabel.setVisible(hasCc);
                emailCcLabel.setManaged(hasCc);

                sourceText.setText(newValue.getContent());

                parts.clear();
                parts.addAll(newValue.getParts());

                headers.clear();
                headers.addAll(newValue.getHeaders());

                emailPart.getSelectionModel().selectFirst();
            } else {
                emailScreenTabPane.getSelectionModel().select(0);

                sourceText.setText(null);
            }
        });

        emailPart.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (messagesTable.getSelectionModel().getSelectedItem() != null) {
                if (newValue != null && (newValue.getType() == EmailPart.Type.text || newValue.getType() == EmailPart.Type.html)) {
                    emailText.getEngine().loadContent(newValue.getContent(), newValue.getType() == EmailPart.Type.text ? "text/plain" : "text/html");
                } else {
                    emailText.getEngine().loadContent("");
                }
            } else {
                emailText.getEngine().loadContent("");
            }
        });

        EmailServer.addObserver((o, arg) -> Platform.runLater(() -> {
            Event event = (Event) arg;

            switch (event.getType()) {
                case incoming:
                    messages.add(event.getEmail());
                    updateMessagesCount();

                    if (config.isJumpToLast()) {
                        messagesTable.getSelectionModel().selectLast();
                    }
                    break;
                case clear:
                    messages.clear();
                    updateMessagesCount();
                    serverLog.clear();
                    break;
            }
        }));
    }

    private void initTable() {
        messages = FXCollections.observableArrayList();
        filteredMessages = new FilteredList<>(messages);
        messagesTable.setItems(filteredMessages);
    }

    private void initTableFilter() {
        // we can't create clearableTextField via FXML so we need to init it manually
        // see: https://bitbucket.org/controlsfx/controlsfx/issues/330
        try {
            Method m = TextFields.class.getDeclaredMethod("setupClearButtonField", TextField.class, ObjectProperty.class);
            m.setAccessible(true);
            m.invoke(null, tableFilter, tableFilter.rightProperty());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Failed to init table filter field", e);
        }
    }

    private void initControlPanel(Config config) {
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

    private void initEmailViewer() {
        emailHeader.getChildren().forEach(node -> {
            int index = GridPane.getRowIndex(node);
            GridPane.setMargin(node, new Insets(index > 0 ? 5 : 0, 0, 0, 0));
        });
    }

    private void initEmailButtons() {
        if (Desktop.isDesktopSupported()) {
            this.desktop = Desktop.getDesktop();

            if (!this.desktop.isSupported(Desktop.Action.BROWSE)) {
                this.desktop = null;
            }
        }

        if (this.desktop == null) {
            viewButton.setDisable(true);
        }
    }

    private void loadSavedEmails(Config config) {
        messages.addAll(EmailServer.getEmails());
        updateMessagesCount();

        if (config.isJumpToLast()) {
            messagesTable.getSelectionModel().selectLast();

            if (messages.size() > 0) {
                messagesTable.scrollTo(messages.size() - 1);
            }
        }
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
