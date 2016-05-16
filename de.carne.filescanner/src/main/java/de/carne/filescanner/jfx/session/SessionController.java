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
import java.net.URL;
import java.util.prefs.Preferences;

import de.carne.filescanner.core.FileScanner;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerStats;
import de.carne.filescanner.core.FileScannerStatus;
import de.carne.filescanner.core.transfer.HtmlResultRendererURLHandler;
import de.carne.filescanner.jfx.Images;
import de.carne.filescanner.jfx.control.FileView;
import de.carne.filescanner.jfx.control.FileViewType;
import de.carne.filescanner.jfx.control.PositionRange;
import de.carne.filescanner.jfx.session.ResultTreeItemFactory.ResultTreeItem;
import de.carne.filescanner.spi.FileScannerInput;
import de.carne.filescanner.util.Units;
import de.carne.jfx.StageController;
import de.carne.jfx.aboutinfo.AboutInfoController;
import de.carne.jfx.logview.LogViewTriggerProperty;
import de.carne.jfx.messagebox.MessageBoxStyle;
import de.carne.util.Strings;
import de.carne.util.logging.Log;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
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

	private static final URL RESULT_VIEW_STYLE_URL = SessionController.class.getResource("ResultView.css");

	private static final URL EMPTY_RESULT_VIEW_DOC = SessionController.class.getResource("EmptyResultView.html");

	private FileScanner fileScanner = null;

	private final ResultTreeItemFactory resultItemFactory = new ResultTreeItemFactory();

	private URL resultViewURL = null;

	private final LogViewTriggerProperty logViewTriggerProperty = new LogViewTriggerProperty(this);

	@FXML
	MenuBar systemMenuBar;

	@FXML
	RadioMenuItem binaryFileViewMenuItem;

	@FXML
	RadioMenuItem octalFileViewMenuItem;

	@FXML
	RadioMenuItem hexadecimalFileViewMenuItem;

	@FXML
	CheckMenuItem toggleLogMenuItem;

	@FXML
	TreeView<FileScannerResult> resultsView;

	@FXML
	WebView resultView;

	@FXML
	FileView fileView;

	@FXML
	ImageView scanStatusIcon;

	@FXML
	Button cancelScanButton;

	@FXML
	ProgressBar scanProgressBar;

	@FXML
	Label scanStatusMessage;

	@FXML
	Label sysStatusMessage;

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
		closeCurrentFileScanner();
		this.cancelScanButton.setDisable(true);
	}

	void closeSession() {
		closeCurrentFileScanner();
		clearResults();
		syncPreferences();
	}

	void onResultItemSelected(TreeItem<FileScannerResult> resultItem) {
		if (resultItem != null) {
			FileScannerResult result = resultItem.getValue();

			this.fileView.setFile(new FileScannerInputAccess(result.input()));

			PositionRange selection = new PositionRange(result.start(), result.end());

			this.fileView.setPosition(selection.getStart());
			this.fileView.setSelection(selection);

			if (this.resultViewURL != null) {
				HtmlResultRendererURLHandler.close(this.resultViewURL);
				this.resultViewURL = null;
			}
			try {
				this.resultViewURL = HtmlResultRendererURLHandler.open(result);
				this.resultView.getEngine().load(this.resultViewURL.toExternalForm());
			} catch (IOException e) {
				LOG.error(e, I18N.BUNDLE, I18N.STR_OPEN_RENDERER_ERROR);
			}
		} else {
			this.fileView.setFile(null);
			this.resultView.getEngine().load(EMPTY_RESULT_VIEW_DOC.toExternalForm());
		}
	}

	void onFileScannerStart(FileScanner scanner, FileScannerStats stats) {
		if (scanner.equals(this.fileScanner)) {
			this.scanStatusIcon.setImage(Images.IMAGE_SUCCESS16);
			this.cancelScanButton.setDisable(false);
			updateScanStatusMessage(stats, I18N.STR_SCAN_STATUS_START);
		}
	}

	void onFileScannerFinished(FileScanner scanner, FileScannerStats stats) {
		if (scanner.equals(this.fileScanner)) {
			this.cancelScanButton.setDisable(true);
			updateScanStatusMessage(stats, I18N.STR_SCAN_STATUS_FINISHED);
		}
	}

	void onFileScannerCancelled(FileScanner scanner, FileScannerStats stats) {
		if (scanner.equals(this.fileScanner)) {
			this.cancelScanButton.setDisable(true);
			updateScanStatusMessage(stats, I18N.STR_SCAN_STATUS_CANCELLED);
		}
	}

	void onFileScannerProgress(FileScanner scanner, FileScannerStats stats) {
		if (scanner.equals(this.fileScanner)) {
			updateScanStatusMessage(stats, I18N.STR_SCAN_STATUS_PROGRESS);
			updateSystemStatusMessage();
		}
	}

	void onFileScannerResult(FileScanner scanner, FileScannerResult result) {
		if (scanner.equals(this.fileScanner)) {
			updateScanResults(result);
		}
	}

	void onFileScannerException(FileScanner scanner, Throwable e) {
		this.scanStatusIcon.setImage(Images.IMAGE_WARNING16);
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
		this.toggleLogMenuItem.selectedProperty().bindBidirectional(this.logViewTriggerProperty);
		this.resultView.getEngine().setUserStyleSheetLocation(RESULT_VIEW_STYLE_URL.toExternalForm());
		this.resultView.getEngine().load(EMPTY_RESULT_VIEW_DOC.toExternalForm());
		this.cancelScanButton.setDisable(true);
		this.scanProgressBar.setProgress(0.0);
		this.scanStatusMessage.setText(I18N.formatSTR_SCAN_STATUS_NONE());
		updateSystemStatusMessage();
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
		closeCurrentFileScanner();
		clearResults();

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

			FileScannerInput.open(this.fileScanner, file).startScan();

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

	private void closeCurrentFileScanner() {
		if (this.fileScanner != null) {
			this.fileScanner.close();
			this.fileScanner = null;
		}
	}

	private void clearResults() {
		this.resultView.getEngine().load(EMPTY_RESULT_VIEW_DOC.toExternalForm());
		if (this.resultViewURL != null) {
			HtmlResultRendererURLHandler.close(this.resultViewURL);
			this.resultViewURL = null;
		}
		this.resultsView.setRoot(null);
		this.resultItemFactory.clear();
	}

	private void updateScanStatusMessage(FileScannerStats stats, String messageKey) {
		this.scanProgressBar.setProgress(stats.progress());

		long scanned = stats.scanned();
		long elapsedSeconds = stats.elapsed() / 1000;
		long bps = (elapsedSeconds >= 1l ? scanned / elapsedSeconds : 0l);

		this.scanStatusMessage
				.setText(I18N.format(messageKey, Units.formatByteValue(scanned), Units.formatByteValue(bps)));
	}

	private void updateSystemStatusMessage() {
		Runtime rt = Runtime.getRuntime();
		long freeMemory = rt.freeMemory();
		long totalMemory = rt.totalMemory();
		long usedMemory = totalMemory - freeMemory;
		long maxMemory = rt.maxMemory();
		long usageRatio = (usedMemory / (maxMemory * 100));

		this.sysStatusMessage.setText(I18N.formatSTR_SYS_STATUS(Units.formatByteValue(usedMemory),
				Units.formatByteValue(maxMemory), usageRatio));
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

}
