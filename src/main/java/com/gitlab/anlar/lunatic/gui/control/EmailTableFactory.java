/*
 * LunaticSMTP
 * Copyright (C) 2017  Anton Larionov
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

package com.gitlab.anlar.lunatic.gui.control;

import com.gitlab.anlar.lunatic.dto.Email;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EmailTableFactory implements Callback<TableColumn<Email, Email>, TableCell<Email, Email>> {

    @Override
    public TableCell<Email, Email> call(TableColumn<Email, Email> param) {
        return new TableCell<Email, Email>() {

            @Override
            protected void updateItem(Email item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    boolean isToday = DateUtils.isSameDay(item.getDate(), new Date());

                    Label labelSubject = createSubject(item);
                    Label labelBody = createBody(item);
                    Label labelDate = createDate(item, isToday);

                    VBox emailBox = new VBox(labelSubject, labelBody);

                    GridPane box = new GridPane();
                    box.add(emailBox, 0, 0);
                    box.add(labelDate, 1, 0);
                    GridPane.setHgrow(emailBox, Priority.ALWAYS);

                    box.getColumnConstraints().add(new ColumnConstraints());
                    box.getColumnConstraints().add(new ColumnConstraints());

                    box.getColumnConstraints().get(1).setMinWidth(isToday ? 50 : 130);
                    box.getColumnConstraints().get(1).setHalignment(HPos.RIGHT);

                    setGraphic(box);
                } else {
                    setGraphic(null);
                }
            }

            private Label createSubject(Email email) {
                Label label = new Label(email.getSubject());

                label.setStyle("-fx-font-weight: bold");

                return label;
            }

            private Label createBody(Email email) {
                return new Label(getBodyPreview(email));
            }

            private String getBodyPreview(Email email) {
                if (StringUtils.isNotBlank(email.getBody())) {
                    String[] lines = email.getBody().trim().split("\\r?\\n", -1);
                    return lines[0];
                } else {
                    return null;
                }
            }

            private Label createDate(Email item, boolean isToday) {
                Label label = new Label();

                if (item.getDate() != null) {
                    if (isToday) {
                        label.setText(new SimpleDateFormat("HH:mm").format(item.getDate()));
                    } else {
                        label.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(item.getDate()));
                    }
                }

                return label;
            }
        };
    }
}
