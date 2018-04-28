/*
 * Copyright (c) 2007-2018 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.swt.main;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.carne.boot.Application;
import de.carne.boot.Exceptions;
import de.carne.boot.check.Nullable;
import de.carne.boot.logging.Log;
import de.carne.filescanner.FileScannerMain;
import de.carne.filescanner.engine.FileScanner;
import de.carne.filescanner.engine.FileScannerProgress;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.FileScannerStatus;
import de.carne.filescanner.engine.Formats;

/**
 * Main window controller.
 */
class MainController implements FileScannerStatus {

	private static final Log LOG = new Log();

	private final MainUI ui;
	@Nullable
	private FileScanner fileScanner = null;

	MainController(MainUI ui) {
		this.ui = ui;
	}

	FileScannerResult openFile(String file) throws IOException {
		if (this.fileScanner != null) {
			FileScanner oldFileScanner = this.fileScanner;

			this.fileScanner = null;
			oldFileScanner.close();
		}
		this.ui.resetSession(true);

		Path filePath = Paths.get(file);

		this.fileScanner = FileScanner.scan(filePath, Formats.all().enabledFormats(), this);
		return this.fileScanner.result();
	}

	void close() {
		if (this.fileScanner != null) {
			try {
				this.fileScanner.close();
			} catch (IOException e) {
				Exceptions.ignore(e);
			}
		}
	}

	void onCopyObjectSelected() {
		LOG.info("onCopyObjectSelected");
	}

	void onExportObjectSelected() {
		LOG.info("onExportObjectSelected");
	}

	void onGotoNextSelected() {
		LOG.info("onGotoNextSelected");
	}

	void onGotoPreviousSelected() {
		LOG.info("onGotoPreviousSelected");
	}

	void onStopScanSelected() {
		LOG.info("onStopScanSelected");
	}

	@Override
	public void scanStarted(FileScanner scanner) {
		if (scanner.equals(this.fileScanner)) {
			Application.getMain(FileScannerMain.class).runWait(() -> this.ui.sessionRunning(true));
		}
	}

	@Override
	public void scanFinished(FileScanner scanner) {
		if (scanner.equals(this.fileScanner)) {
			Application.getMain(FileScannerMain.class).runWait(() -> this.ui.sessionRunning(false));
		}
	}

	@Override
	public void scanProgress(FileScanner scanner, FileScannerProgress progress) {
		if (scanner.equals(this.fileScanner)) {
			Application.getMain(FileScannerMain.class).runWait(() -> this.ui.sessionProgress(progress));
		}
	}

	@Override
	public void scanResult(FileScanner scanner, FileScannerResult result) {
		if (scanner.equals(this.fileScanner)) {
			Application.getMain(FileScannerMain.class).runWait(() -> this.ui.sessionResult(result));
		}
	}

	@Override
	public void scanException(FileScanner scanner, Exception cause) {
		if (scanner.equals(this.fileScanner)) {
			// TODO Auto-generated method stub
		}
	}

}
