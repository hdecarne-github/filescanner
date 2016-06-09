/*
 * Copyright (c) 2014-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.jfx.preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;

import de.carne.filescanner.core.FileScannerPreferences;
import de.carne.filescanner.core.transfer.RendererStyle;
import de.carne.filescanner.core.transfer.RendererStylePreferences;
import de.carne.filescanner.core.transfer.ResultRenderer.Mode;
import de.carne.filescanner.jfx.Images;
import de.carne.filescanner.spi.Format;
import de.carne.jfx.StageController;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Dialog controller for preference editing.
 */
public class PreferencesController extends StageController {

	@FXML
	ColorPicker normalColorSelection;

	@FXML
	ColorPicker valueColorSelection;

	@FXML
	ColorPicker commentColorSelection;

	@FXML
	ColorPicker keywordColorSelection;

	@FXML
	ColorPicker operatorColorSelection;

	@FXML
	ColorPicker labelColorSelection;

	@FXML
	ColorPicker errorColorSelection;

	@FXML
	ListView<EnabledFormatModel> enabledFormatsList;

	@FXML
	void onApply(ActionEvent evt) {
		saveFormatPreferences();
		saveStylePreferences();
		try {
			RendererStylePreferences.sync();
			FileScannerPreferences.sync();
		} catch (BackingStoreException e) {
			reportUnexpectedException(e);
		}
	}

	@FXML
	void onSave(ActionEvent evt) {
		saveFormatPreferences();
		saveStylePreferences();
		try {
			RendererStylePreferences.sync();
			FileScannerPreferences.sync();
			getStage().close();
		} catch (BackingStoreException e) {
			reportUnexpectedException(e);
		}
	}

	@FXML
	void onCancel(ActionEvent evt) {
		getStage().close();
	}

	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.getIcons().addAll(Images.IMAGE_FILESCANNER16, Images.IMAGE_FILESCANNER32);
		controllerStage.setTitle(I18N.formatSTR_PREFERENCES_TITLE());
		this.enabledFormatsList.setCellFactory(
				CheckBoxListCell.forListView(new Callback<EnabledFormatModel, ObservableValue<Boolean>>() {

					@Override
					public ObservableValue<Boolean> call(EnabledFormatModel param) {
						return param.enabledProperty();
					}

				}));
		loadStylePrefrences();
		loadFormatPreferences();
		getStage().sizeToScene();
	}

	private void loadStylePrefrences() {
		RendererStyle style = RendererStylePreferences.getDefaultStyle();

		this.normalColorSelection.setValue(intToColor(style.getColor(Mode.NORMAL)));
		this.valueColorSelection.setValue(intToColor(style.getColor(Mode.VALUE)));
		this.commentColorSelection.setValue(intToColor(style.getColor(Mode.COMMENT)));
		this.keywordColorSelection.setValue(intToColor(style.getColor(Mode.KEYWORD)));
		this.operatorColorSelection.setValue(intToColor(style.getColor(Mode.OPERATOR)));
		this.labelColorSelection.setValue(intToColor(style.getColor(Mode.LABEL)));
		this.errorColorSelection.setValue(intToColor(style.getColor(Mode.ERROR)));
	}

	private static Color intToColor(int i) {
		return Color.rgb((i >>> 16) & 0xff, (i >>> 8) & 0xff, i & 0xff);
	}

	private void saveStylePreferences() {
		RendererStyle style = new RendererStyle();

		style.setColor(Mode.NORMAL, colorToInt(this.normalColorSelection.getValue()));
		style.setColor(Mode.VALUE, colorToInt(this.valueColorSelection.getValue()));
		style.setColor(Mode.COMMENT, colorToInt(this.commentColorSelection.getValue()));
		style.setColor(Mode.KEYWORD, colorToInt(this.keywordColorSelection.getValue()));
		style.setColor(Mode.OPERATOR, colorToInt(this.operatorColorSelection.getValue()));
		style.setColor(Mode.LABEL, colorToInt(this.labelColorSelection.getValue()));
		style.setColor(Mode.ERROR, colorToInt(this.errorColorSelection.getValue()));
	}

	private static int colorToInt(Color color) {
		return ((int) (color.getRed() * 255) << 16) | ((int) (color.getGreen() * 255) << 8)
				| (int) (color.getBlue() * 255);
	}

	private void loadFormatPreferences() {
		ArrayList<Format> formats = new ArrayList<>(Format.getFormats());

		formats.sort(new Comparator<Format>() {

			@Override
			public int compare(Format o1, Format o2) {
				return o1.name().compareTo(o2.name());
			}

		});

		Set<Format> enabledFormats = FileScannerPreferences.getEnabledFormats();

		for (Format format : formats) {
			boolean formatEnabled = enabledFormats.contains(format);

			this.enabledFormatsList.getItems().add(new EnabledFormatModel(format, formatEnabled));
		}
	}

	private void saveFormatPreferences() {
		HashSet<Format> enabledFormats = new HashSet<>();

		for (EnabledFormatModel enabledFormatEntry : this.enabledFormatsList.getItems()) {
			if (enabledFormatEntry.enabledProperty().get()) {
				enabledFormats.add(enabledFormatEntry.formatProperty().get());
			}
		}
		FileScannerPreferences.setEnabledFormats(enabledFormats);
	}

}
