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

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;
import java.util.prefs.Preferences;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultType;
import de.carne.filescanner.core.transfer.FileResultExporter;
import de.carne.filescanner.core.transfer.StreamHandler;
import de.carne.filescanner.jfx.ResultGraphics;
import de.carne.filescanner.util.Units;
import de.carne.jfx.StageController;
import de.carne.jfx.messagebox.MessageBoxStyle;
import de.carne.util.Strings;
import de.carne.util.prefs.DirectoryPreference;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

	private Future<?> exportFuture = null;

	@FXML
	HBox resultDisplay;

	@FXML
	Label resultDescription;

	@FXML
	ChoiceBox<FileResultExporter> exporterSelection;

	@FXML
	TextField exportDestinationInput;

	@FXML
	Button chooseExportDestinationButton;

	@FXML
	ProgressBar exportProgress;

	@FXML
	Button startButton;

	@FXML
	void onChooseExportDestination(ActionEvent evt) {
		FileResultExporter exporter = this.exporterSelection.getValue();

		if (exporter != null) {
			FileChooser fileChooser = new FileChooser();

			fileChooser.getExtensionFilters().addAll(getExporterFilters(exporter));

			String input = this.exportDestinationInput.getText();
			File initialDirectory = null;
			String initialFileName = null;

			if (Strings.notEmpty(input)) {
				try {
					Path inputPath = Paths.get(input);
					Path inputPathParent = inputPath.getParent();
					Path inputPathFileName = inputPath.getFileName();

					if (inputPathParent != null) {
						initialDirectory = inputPathParent.toFile();
					}
					if (inputPathFileName != null) {
						initialFileName = inputPathFileName.toString();
					}
				} catch (InvalidPathException e) {
					// ignore
				}
			}

			fileChooser.setInitialDirectory(initialDirectory);
			fileChooser.setInitialFileName(initialFileName);

			File file = fileChooser.showSaveDialog(getStage());

			if (file != null) {
				this.exportDestinationInput.setText(file.toString());
			}
		}
	}

	@FXML
	void onStart(ActionEvent evt) {
		Path exportFilePath = validateExportDestination();

		if (exportFilePath != null) {
			StreamHandler exporterStreamHandler = this.exporterSelection.getValue().getStreamHandler(this.result);
			Task<Void> exportTask = new Task<Void>() {

				private final Path filePath = exportFilePath;

				private final StreamHandler streamHandler = exporterStreamHandler;

				@Override
				protected Void call() throws Exception {
					byte[] buffer = new byte[4096];
					long total = this.streamHandler.size();
					long progress = 0L;

					try (InputStream in = this.streamHandler.open();
							OutputStream out = Files.newOutputStream(this.filePath, StandardOpenOption.CREATE)) {
						while (!isCancelled()) {
							int read = in.read(buffer);

							if (read > 0) {
								out.write(buffer, 0, read);
								progress += read;
								if (total >= 0) {
									updateProgress(progress, total);
								}
							} else if (progress < total) {
								throw new EOFException();
							} else {
								break;
							}
						}
					}
					return null;
				}

				@Override
				protected void succeeded() {
					exportTaskSucceeded(this.filePath);
				}

				@Override
				protected void cancelled() {
					exportTaskCancelled();
				}

				@Override
				protected void failed() {
					exportTaskFailed(this.filePath, getException());
				}

			};

			beginExport(exportTask, exporterStreamHandler.size() < 0L);
			this.exportFuture = getExecutorService().submit(exportTask);
		}
	}

	@FXML
	void onCancel(ActionEvent evt) {
		if (this.exportFuture != null) {
			this.exportFuture.cancel(false);
		} else {
			getStage().close();
		}
	}

	@FXML
	void onExporterSelectionChanged(FileResultExporter exporter) {
		updateExportDestinationInput(exporter);
	}

	@Override
	protected boolean canClose() {
		return this.exportFuture == null;
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
		this.exporterSelection.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<FileResultExporter>() {

					@Override
					public void changed(ObservableValue<? extends FileResultExporter> observable,
							FileResultExporter oldValue, FileResultExporter newValue) {
						onExporterSelectionChanged(newValue);
					}

				});
		this.exporterSelection.getItems().addAll(this.result.getExporters(FileResultExporter.class));
		this.exporterSelection.getItems().add(FileResultExporter.BIN_EXPORTER);
		this.exporterSelection.getSelectionModel().select(0);
	}

	private void updateExportDestinationInput(FileResultExporter exporter) {
		String currentInput = this.exportDestinationInput.getText();
		Path currentInputPath = null;

		if (Strings.notEmpty(currentInput)) {
			try {
				currentInputPath = Paths.get(currentInput);
			} catch (InvalidPathException e) {
				// ignore
			}
		}

		Path inputPath;

		if (currentInputPath != null) {
			inputPath = exporter.getDefaultExportFilePath(currentInputPath);
		} else {
			inputPath = exporter.getDefaultExportFilePath(PREF_INITIAL_DIRECTORY.getAsPath(PREFERENCES),
					getResultFileName());
		}
		this.exportDestinationInput.setText(inputPath.toString());
	}

	private String getResultFileName() {
		return (this.result.type() == FileScannerResultType.INPUT ? this.result.title() : null);
	}

	private Path validateExportDestination() {
		String input = this.exportDestinationInput.getText();
		Exception invalidInputException = null;
		Path inputPath = null;

		if (Strings.notEmpty(input)) {
			try {
				inputPath = Paths.get(input);
			} catch (InvalidPathException e) {
				invalidInputException = e;
			}
		}
		if (inputPath == null) {
			showMessageBox(I18N.formatSTR_INVALID_FILE_INPUT(input), invalidInputException,
					MessageBoxStyle.ICON_WARNING, MessageBoxStyle.BUTTON_OK);
		}
		return inputPath;
	}

	private void beginExport(Task<Void> task, boolean indeterminate) {
		this.exporterSelection.setDisable(true);
		this.exportDestinationInput.setDisable(true);
		this.chooseExportDestinationButton.setDisable(true);
		this.startButton.setDisable(true);
		this.exportProgress.setProgress(indeterminate ? -1.0 : 0.0);
		this.exportProgress.progressProperty().bind(task.progressProperty());
	}

	private void endExport() {
		this.exportProgress.progressProperty().unbind();
		if (this.exportProgress.getProgress() < 0.0) {
			this.exportProgress.setProgress(0.0);
		}
		this.exporterSelection.setDisable(false);
		this.exportDestinationInput.setDisable(false);
		this.chooseExportDestinationButton.setDisable(false);
		this.startButton.setDisable(false);
		this.exportFuture = null;
	}

	void exportTaskSucceeded(Path exportFilePath) {
		endExport();
		PREF_INITIAL_DIRECTORY.setFromFile(PREFERENCES, exportFilePath);
		getStage().close();
	}

	void exportTaskCancelled() {
		endExport();
	}

	void exportTaskFailed(Path exportFilePath, Throwable e) {
		endExport();
		showMessageBox(I18N.formatSTR_EXPORT_ERROR(exportFilePath), e, MessageBoxStyle.ICON_ERROR,
				MessageBoxStyle.BUTTON_OK);
	}

	private static ExtensionFilter[] getExporterFilters(FileResultExporter exporter) {
		ExtensionFilter exporterFilter = new ExtensionFilter(exporter.name(), exporter.extensionFilters());
		ExtensionFilter allFilesFilter = new ExtensionFilter(I18N.formatSTR_ALL_FILES_FILTER(), "*.*");

		return new ExtensionFilter[] { exporterFilter, allFilesFilter };
	}

}
