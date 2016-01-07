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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PercentageTableColumn<S, T> extends TableColumn<S, T> {

    private final IntegerProperty percentageWidth = new SimpleIntegerProperty(100);

    public PercentageTableColumn() {
        tableViewProperty().addListener(new ChangeListener<TableView<S>>() {

            @Override
            public void changed(ObservableValue<? extends TableView<S>> observable, TableView<S> oldValue, TableView<S> newValue) {
                if (prefWidthProperty().isBound()) {
                    prefWidthProperty().unbind();
                }

                double coefficient = percentageWidth.get() * 1.0 / 100;
                prefWidthProperty().bind(newValue.widthProperty().multiply(coefficient));
            }
        });
    }

    public int getPercentageWidth() {
        return percentageWidth.get();
    }

    public IntegerProperty percentageWidthProperty() {
        return percentageWidth;
    }

    public void setPercentageWidth(int percentageWidth) {
        if (percentageWidth >= 0 && percentageWidth <= 100) {
            this.percentageWidthProperty().set(percentageWidth);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Percentage width should be between 0 and 100, value: %s", percentageWidth));
        }
    }
}
