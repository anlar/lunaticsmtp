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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class EmailTableValueFactory implements Callback<TableColumn.CellDataFeatures<Email, Email>, ObservableValue<Email>> {

    @Override
    public ObservableValue<Email> call(TableColumn.CellDataFeatures<Email, Email> param) {
        if (param.getValue() != null) {
            return new ReadOnlyObjectWrapper<>(param.getValue());
        }

        return null;
    }
}
