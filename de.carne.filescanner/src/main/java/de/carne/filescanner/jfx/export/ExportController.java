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
package de.carne.filescanner.jfx.export;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.transfer.FileResultExporter;
import de.carne.filescanner.jfx.ResultGraphics;
import de.carne.filescanner.util.Units;
import de.carne.jfx.StageController;
import de.carne.util.prefs.DirectoryPreference;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Dialog controller for result data exporting.
 */
public class ExportController extends StageController {

	private static final Preferences PREFERENCES = Preferences.systemNodeForPackage(ExportController.class);

	private static final DirectoryPreference PREF_INITIAL_DIRECTORY = new DirectoryPreference("initialDirectory");

	private FileScannerResult result = null;

	@FXML
	HBox resultDisplay;

	@FXML
	Label resultDescription;

	@FXML
	ChoiceBox<FileResultExporter> exporterSelection;

	@FXML
	TextField exportDestinationInput;

	@FXML
	ProgressBar exportProgress;

	@FXML
	void onChooseDestination(ActionEvent evt) {
		FileResultExporter exporter = this.exporterSelection.getValue();

		if (exporter != null) {
			FileChooser fileChooser = new FileChooser();

			fileChooser.getExtensionFilters().addAll(getFilters(exporter));
			fileChooser.setInitialDirectory(PREF_INITIAL_DIRECTORY.getAsFile(PREFERENCES));

			File file = fileChooser.showSaveDialog(getStage());

			if (file != null) {
				this.exportDestinationInput.setText(file.toString());
			}
		}
	}

	@FXML
	void onStart(ActionEvent evt) {

	}

	@FXML
	void onCancel(ActionEvent evt) {
		getStage().close();
	}

	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(I18N.formatSTR_EXPORT_TITLE());
		getStage().sizeToScene();
	}

	@Override
	protected Preferences getPreferences() {
		return PREFERENCES;
	}

	/**
	 * Begin the result export.
	 *
	 * @param resultParam The scanner result to export.
	 */
	public void beginExport(FileScannerResult resultParam) {
		assert resultParam != null;

		this.result = resultParam;
		this.resultDisplay.getChildren().add(0, ResultGraphics.get(this.result));
		this.resultDescription.setText(
				I18N.formatSTR_RESULT_DESCRIPTION(this.result.title(), Units.formatByteValue(this.result.size())));
		this.exporterSelection.getItems().addAll(this.result.getExporters(FileResultExporter.class));
		this.exporterSelection.getItems().add(FileResultExporter.RAW_EXPORTER);
		this.exporterSelection.getSelectionModel().select(0);
	}

	private static ExtensionFilter[] getFilters(FileResultExporter exporter) {
		ExtensionFilter exporterFilter = new ExtensionFilter(exporter.name(), exporter.filter());
		ExtensionFilter allFilesFilter = new ExtensionFilter(I18N.formatSTR_ALL_FILES_FILTER(), "*.*");

		return new ExtensionFilter[] { exporterFilter, allFilesFilter };
	}

}
