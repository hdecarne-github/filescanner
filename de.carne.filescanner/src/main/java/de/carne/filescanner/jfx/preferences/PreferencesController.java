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
import de.carne.filescanner.jfx.Images;
import de.carne.filescanner.spi.Format;
import de.carne.jfx.StageController;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Dialog controller for preference editing.
 */
public class PreferencesController extends StageController {

	@FXML
	ListView<EnabledFormatModel> enabledFormatsList;

	@FXML
	void onSave(ActionEvent evt) {
		saveFormatPreferences();
		try {
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

	/*
	 * (non-Javadoc)
	 * @see de.carne.jfx.StageController#getModality()
	 */
	@Override
	protected Modality getModality() {
		return Modality.APPLICATION_MODAL;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.certmgr.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
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
		loadFormatPreferences();
		getStage().sizeToScene();
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
