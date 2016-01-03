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

package com.gitlab.anlar.lunatic.gui.control;

import com.gitlab.anlar.lunatic.dto.Email;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;

public class DatePropertyValueFactory extends PropertyValueFactory {

    private final String format;

    public DatePropertyValueFactory(@NamedArg("property") String property, @NamedArg("format") String format) {
        super(property);
        this.format = format;
    }

    @Override
    public ObservableValue call(TableColumn.CellDataFeatures param) {
        if (getProperty() != null && !getProperty().isEmpty() && param.getValue() != null) {
            Email email = (Email) param.getValue();
            if (email.getDate() != null) {
                String value = new SimpleDateFormat(format).format(email.getDate());
                return new ReadOnlyObjectWrapper<>(value);
            }
        }

        return null;
    }
}
