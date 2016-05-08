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
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.prefs.Preferences;

import de.carne.filescanner.jfx.Images;
import de.carne.filescanner.jfx.control.FileView;
import de.carne.jfx.StageController;
import de.carne.jfx.aboutinfo.AboutInfoController;
import de.carne.jfx.messagebox.MessageBoxStyle;
import de.carne.util.Strings;
import de.carne.util.logging.Log;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Dialog controller for running a scanner session and display the scan results.
 */
public class SessionController extends StageController {

	private static final Log LOG = new Log(SessionController.class);

	private static final Preferences PREFERENCES = Preferences.systemNodeForPackage(SessionController.class);

	private static final String PREF_INITIAL_DIRECTORY = "initialDirectory";

	@FXML
	MenuBar systemMenuBar;

	@FXML
	FileView hexView;

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

	void closeSession() {
		try (FileChannel fileChannel = this.hexView.getFile()) {
			this.hexView.setFile(null);
		} catch (IOException e) {
			LOG.warning(e, I18N.BUNDLE, I18N.STR_CLOSE_SESSION_ERROR);
		}
		syncPreferences();
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
		controllerStage.showingProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (Boolean.FALSE.equals(newValue)) {
					closeSession();
				}
			}

		});
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
		LOG.debug(null, "Open file ''{0}''...", file);

		try {
			this.hexView.setFile(FileChannel.open(file.toPath(), StandardOpenOption.READ));
			recordInitialDirectoryPreference(file);
		} catch (IOException e) {
			showMessageBox(I18N.formatSTR_OPEN_FILE_ERROR(file), e, MessageBoxStyle.ICON_ERROR,
					MessageBoxStyle.BUTTON_OK);
		}
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
