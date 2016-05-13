/*
 * Copyright (c) 2007-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.jfx.session;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import de.carne.filescanner.core.FileScanner;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerStats;
import de.carne.filescanner.core.FileScannerStatus;
import de.carne.filescanner.core.RootFileScannerInput;
import de.carne.filescanner.jfx.Images;
import de.carne.filescanner.jfx.control.FileView;
import de.carne.filescanner.jfx.control.FileViewType;
import de.carne.filescanner.jfx.control.PositionRange;
import de.carne.filescanner.jfx.session.ResultTreeItemFactory.ResultTreeItem;
import de.carne.jfx.StageController;
import de.carne.jfx.aboutinfo.AboutInfoController;
import de.carne.jfx.messagebox.MessageBoxStyle;
import de.carne.util.Strings;
import de.carne.util.logging.Log;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Dialog controller for running a scanner session and display the scan results.
 */
public class SessionController extends StageController {

	private static final Log LOG = new Log(SessionController.class);

	private static final Preferences PREFERENCES = Preferences.systemNodeForPackage(SessionController.class);

	private static final String PREF_FILE_VIEW_TYPE = "fileViewType";

	private static final String PREF_INITIAL_DIRECTORY = "initialDirectory";

	private final ResultTreeItemFactory resultItemFactory = new ResultTreeItemFactory();

	private FileScanner fileScanner = null;

	@FXML
	MenuBar systemMenuBar;

	@FXML
	RadioMenuItem binaryFileViewMenuItem;

	@FXML
	RadioMenuItem octalFileViewMenuItem;

	@FXML
	RadioMenuItem hexadecimalFileViewMenuItem;

	@FXML
	TreeView<FileScannerResult> resultsView;

	@FXML
	WebView resultDetailsView;

	@FXML
	FileView fileView;

	@FXML
	Button cancelScanButton;

	@FXML
	ProgressBar scanProgressBar;

	@FXML
	Label scanStatusMessage;

	@FXML
	void onNewSession(ActionEvent evt) {
		try {
			openRootStage(getClass()).getStage().show();
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	@FXML
	void onOpenFile(ActionEvent evt) {
		FileChooser fileChooser = new FileChooser();

		fileChooser.setInitialDirectory(getInitialDirectoryPreference());

		File file = fileChooser.showOpenDialog(getStage());

		if (file != null) {
			openFile(file);
		}
	}

	@FXML
	void onExit(ActionEvent evt) {
		getStage().close();
	}

	@FXML
	void onBinaryFileViewSelected(ActionEvent evt) {
		this.fileView.setViewType(FileViewType.BINARY);
		recordFileViewTypePrefence(FileViewType.BINARY);
	}

	@FXML
	void onOctalFileViewSelected(ActionEvent evt) {
		this.fileView.setViewType(FileViewType.OCTAL);
		recordFileViewTypePrefence(FileViewType.OCTAL);
	}

	@FXML
	void onHexadecimalFileViewSelected(ActionEvent evt) {
		this.fileView.setViewType(FileViewType.HEXADECIMAL_U);
		recordFileViewTypePrefence(FileViewType.HEXADECIMAL_U);
	}

	@FXML
	void onToggleLog(ActionEvent evt) {
		// TODO
	}

	@FXML
	void onAboutInfo(ActionEvent evt) {
		try {
			AboutInfoController aboutInfo = openStage(AboutInfoController.class);

			aboutInfo.setInfoIcon(Images.IMAGE_FILESCANNER48);
			aboutInfo.addInfo(I18N.formatSTR_ABOUT_TITLE1(), I18N.formatSTR_ABOUT_INFO1());
			aboutInfo.addInfo(I18N.formatSTR_ABOUT_TITLE2(), I18N.formatSTR_ABOUT_INFO2());
			aboutInfo.getStage().showAndWait();
		} catch (IOException e) {
			reportUnexpectedException(e);
		}
	}

	@FXML
	void onCancelScan(ActionEvent evt) {
		closeCurrentFileScanner(false);
		this.cancelScanButton.setDisable(true);
	}

	void closeSession() {
		closeCurrentFileScanner(true);
		syncPreferences();
	}

	void onResultItemSelected(TreeItem<FileScannerResult> resultItem) {
		if (resultItem != null) {
			FileScannerResult result = resultItem.getValue();

			this.fileView.setFile(new FileScannerInputAccess(result.input()));

			PositionRange selection = new PositionRange(result.start(), result.end());

			this.fileView.setPosition(selection.getStart());
			this.fileView.setSelection(selection);
		} else {
			this.fileView.setFile(null);
		}
	}

	void onFileScannerStart(FileScanner scanner, FileScannerStats stats) {
		if (scanner.equals(this.fileScanner)) {
			this.cancelScanButton.setDisable(false);
			updateScanStatusMessage(stats, I18N.STR_STATUS_START);
		}
	}

	void onFileScannerFinished(FileScanner scanner, FileScannerStats stats) {
		if (scanner.equals(this.fileScanner)) {
			this.cancelScanButton.setDisable(true);
			updateScanStatusMessage(stats, I18N.STR_STATUS_FINISHED);
		}
	}

	void onFileScannerCancelled(FileScanner scanner, FileScannerStats stats) {
		if (scanner.equals(this.fileScanner)) {
			this.cancelScanButton.setDisable(true);
			updateScanStatusMessage(stats, I18N.STR_STATUS_CANCELLED);
		}
	}

	void onFileScannerProgress(FileScanner scanner, FileScannerStats stats) {
		if (scanner.equals(this.fileScanner)) {
			updateScanStatusMessage(stats, I18N.STR_STATUS_PROGRESS);
		}
	}

	void onFileScannerResult(FileScanner scanner, FileScannerResult result) {
		if (scanner.equals(this.fileScanner)) {
			updateScanResults(result);
		}
	}

	void onFileScannerException(FileScanner scanner, Throwable e) {

	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		controllerStage.setTitle(I18N.formatSTR_SESSION_TITLE());
		controllerStage.getIcons().addAll(Images.IMAGE_FILESCANNER16, Images.IMAGE_FILESCANNER32,
				Images.IMAGE_FILESCANNER48);
		setupFileViewType(getFileViewTypePreference());
		this.cancelScanButton.setDisable(true);
		this.scanProgressBar.setProgress(0.0);
		this.scanStatusMessage.setText(I18N.formatSTR_STATUS_NONE());
		controllerStage.showingProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (Boolean.FALSE.equals(newValue)) {
					closeSession();
				}
			}

		});
		this.resultsView.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<FileScannerResult>>() {

					@Override
					public void changed(ObservableValue<? extends TreeItem<FileScannerResult>> observable,
							TreeItem<FileScannerResult> oldValue, TreeItem<FileScannerResult> newValue) {
						onResultItemSelected(newValue);
					}

				});
	}

	private void setupFileViewType(FileViewType fileViewType) {
		switch (fileViewType) {
		case BINARY:
			this.binaryFileViewMenuItem.setSelected(true);
			break;
		case OCTAL:
			this.octalFileViewMenuItem.setSelected(true);
			break;
		case HEXADECIMAL_U:
			this.hexadecimalFileViewMenuItem.setSelected(true);
			break;
		case HEXADECIMAL_L:
		default:
			throw new IllegalStateException("Unexpected file view type: " + fileViewType);
		}
		this.fileView.setViewType(fileViewType);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.jfx.StageController#getSystemMenuBar()
	 */
	@Override
	protected MenuBar getSystemMenuBar() {
		return this.systemMenuBar;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.jfx.StageController#getPreferences()
	 */
	@Override
	protected Preferences getPreferences() {
		return PREFERENCES;
	}

	/**
	 * Open file and start scanning it.
	 *
	 * @param fileName The file to open.
	 */
	public void openFile(String fileName) {
		assert fileName != null;

		openFile(new File(fileName));
	}

	private void openFile(File file) {
		closeCurrentFileScanner(true);

		LOG.debug(null, "Open file ''{0}''...", file);

		try {
			this.fileScanner = new FileScanner(new FileScannerStatusProxy(new FileScannerStatus() {

				@Override
				public void onScanStart(FileScanner scanner, FileScannerStats stats) {
					onFileScannerStart(scanner, stats);
				}

				@Override
				public void onScanFinished(FileScanner scanner, FileScannerStats stats) {
					onFileScannerFinished(scanner, stats);
				}

				@Override
				public void onScanCancelled(FileScanner scanner, FileScannerStats stats) {
					onFileScannerCancelled(scanner, stats);
				}

				@Override
				public void onScanProgress(FileScanner scanner, FileScannerStats stats) {
					onFileScannerProgress(scanner, stats);
				}

				@Override
				public void onScanResult(FileScanner scanner, FileScannerResult result) {
					onFileScannerResult(scanner, result);
				}

				@Override
				public void onScanException(FileScanner scanner, Throwable e) {
					onFileScannerException(scanner, e);
				}

			}));

			this.fileScanner.queueInput(RootFileScannerInput.open(file));
			recordInitialDirectoryPreference(file);
		} catch (IOException e) {
			showMessageBox(I18N.formatSTR_OPEN_FILE_ERROR(file), e, MessageBoxStyle.ICON_ERROR,
					MessageBoxStyle.BUTTON_OK);
		}
	}

	private void updateScanResults(FileScannerResult result) {
		FileScannerResult resultParent = result.parent();

		if (resultParent != null) {
			ResultTreeItem resultParentItem = this.resultItemFactory.get(resultParent);

			if (resultParentItem != null) {
				resultParentItem.syncChildren();
			}

		} else {
			TreeItem<FileScannerResult> rootItem = this.resultsView.getRoot();
			boolean selectResultItem = false;

			if (rootItem == null) {
				this.resultsView.setRoot(rootItem = new TreeItem<>(null));
				selectResultItem = true;
			}

			ResultTreeItem resultItem = this.resultItemFactory.create(result);

			rootItem.getChildren().add(resultItem);
			if (selectResultItem) {
				this.resultsView.getSelectionModel().select(resultItem);
				this.resultsView.requestFocus();
			}
		}
	}

	private void closeCurrentFileScanner(boolean clearResults) {
		if (this.fileScanner != null) {
			this.fileScanner.close();
			this.fileScanner = null;
		}
		if (clearResults) {
			this.resultsView.setRoot(null);
			this.resultItemFactory.clear();
		}
	}

	private void updateScanStatusMessage(FileScannerStats stats, String messageKey) {
		this.scanProgressBar.setProgress(stats.progress());

		double scanned = stats.scanned();
		double elapsed = stats.elapsed() / 1000.0;
		double bps = (elapsed >= 1.0 ? scanned / elapsed : 0.0);

		this.scanStatusMessage.setText(I18N.format(messageKey, formatByteValue(scanned), formatByteValue(bps)));
	}

	private FileViewType getFileViewTypePreference() {
		String fileViewTypePref = PREFERENCES.get(PREF_FILE_VIEW_TYPE, null);
		FileViewType fileViewType = FileViewType.HEXADECIMAL_U;

		if (Strings.notEmpty(fileViewTypePref)) {
			try {
				fileViewType = FileViewType.valueOf(fileViewTypePref);
			} catch (IllegalArgumentException e) {
				// ignore and use default value for file view type
			}
		}
		return fileViewType;
	}

	private void recordFileViewTypePrefence(FileViewType fileViewType) {
		PREFERENCES.put(PREF_FILE_VIEW_TYPE, fileViewType.toString());
	}

	private File getInitialDirectoryPreference() {
		String initialDirectoryPref = PREFERENCES.get(PREF_INITIAL_DIRECTORY, null);
		File initialDirectory = null;

		if (Strings.notEmpty(initialDirectoryPref)) {
			File initialDirectoryPrefFile = new File(initialDirectoryPref);

			if (initialDirectoryPrefFile.isDirectory()) {
				initialDirectory = initialDirectoryPrefFile;
			}
		}
		return initialDirectory;
	}

	private void recordInitialDirectoryPreference(File file) {
		PREFERENCES.put(PREF_INITIAL_DIRECTORY, file.getParent());
	}

	private String formatByteValue(double value) {
		String text;

		if (value > 1.0e24) {
			text = I18N.formatSTR_BYTE_UNIT24(value / 1.0e24);
		} else if (value > 1.0e21) {
			text = I18N.formatSTR_BYTE_UNIT21(value / 1.0e21);
		} else if (value > 1.0e18) {
			text = I18N.formatSTR_BYTE_UNIT18(value / 1.0e18);
		} else if (value > 1.0e15) {
			text = I18N.formatSTR_BYTE_UNIT15(value / 1.0e15);
		} else if (value > 1.0e12) {
			text = I18N.formatSTR_BYTE_UNIT12(value / 1.0e12);
		} else if (value > 1.0e9) {
			text = I18N.formatSTR_BYTE_UNIT9(value / 1.0e9);
		} else if (value > 1.0e6) {
			text = I18N.formatSTR_BYTE_UNIT6(value / 1.0e6);
		} else if (value > 1.0e3) {
			text = I18N.formatSTR_BYTE_UNIT3(value / 1.0e3);
		} else {
			text = I18N.formatSTR_BYTE_UNIT0(value);
		}
		return text;
	}

}
