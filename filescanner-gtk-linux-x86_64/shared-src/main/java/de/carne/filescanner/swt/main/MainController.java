/*
 * Copyright (c) 2007-2020 Holger de Carne and contributors, All Rights Reserved.
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
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.Application;
import de.carne.boot.Exceptions;
import de.carne.filescanner.FileScannerMain;
import de.carne.filescanner.engine.FileScanner;
import de.carne.filescanner.engine.FileScannerProgress;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.FileScannerStatus;
import de.carne.filescanner.engine.Formats;
import de.carne.filescanner.swt.preferences.UserPreferences;

/**
 * Main window controller.
 */
class MainController implements FileScannerStatus {

	private final MainUI ui;
	@Nullable
	private SearchIndex searchIndex = null;
	@Nullable
	private FileScanner fileScanner = null;

	MainController(MainUI ui) {
		this.ui = ui;
	}

	FileScannerResult openAndScanFile(String file) throws IOException {
		closeScan();
		this.ui.resetSession(true);

		Path filePath = Paths.get(file);
		Formats formats = Formats.all();

		UserPreferences.get().getDisabledFormats().forEach(formats::disable);
		this.searchIndex = new SearchIndex();
		this.fileScanner = FileScanner.scan(filePath, formats.enabledFormats(), this);
		return this.fileScanner.result();
	}

	void close() {
		try {
			closeScan();
		} catch (IOException e) {
			Exceptions.ignore(e);
		}
	}

	private void closeScan() throws IOException {
		FileScanner oldFileScanner = this.fileScanner;
		SearchIndex oldSearchIndex = this.searchIndex;

		this.fileScanner = null;
		this.searchIndex = null;
		if (oldSearchIndex != null) {
			oldSearchIndex.close();
		}
		if (oldFileScanner != null) {
			oldFileScanner.close();
		}
	}

	void stopScan(boolean wait) {
		FileScanner checkedFileScanner = this.fileScanner;

		if (checkedFileScanner != null) {
			checkedFileScanner.stop(false, wait);
		}
		this.ui.sessionRunning(false);
	}

	FileScannerResult @Nullable [] searchNext(@Nullable FileScannerResult from, String query) throws IOException {
		SearchIndex checkedSearchIndex = this.searchIndex;
		FileScanner checkedFileScanner = this.fileScanner;
		FileScannerResult[] searchResult = null;

		if (checkedSearchIndex != null && checkedFileScanner != null) {
			byte[] resultKey = checkedSearchIndex.searchFoward(from, query);

			if (resultKey != null) {
				searchResult = checkedFileScanner.getResultPath(resultKey);
			}
		}
		return searchResult;
	}

	FileScannerResult @Nullable [] searchPrevious(@Nullable FileScannerResult from, String query) throws IOException {
		SearchIndex checkedSearchIndex = this.searchIndex;
		FileScanner checkedFileScanner = this.fileScanner;
		FileScannerResult[] searchResult = null;

		if (checkedSearchIndex != null && checkedFileScanner != null) {
			byte[] resultKey = checkedSearchIndex.searchBackward(from, query);

			if (resultKey != null) {
				searchResult = checkedFileScanner.getResultPath(resultKey);
			}
		}
		return searchResult;
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
			Objects.requireNonNull(this.searchIndex).seal();
			Application.getMain(FileScannerMain.class).runWait(() -> this.ui.sessionRunning(false));
		}
	}

	@Override
	public void scanProgress(FileScanner scanner, FileScannerProgress progress) {
		if (scanner.equals(this.fileScanner)) {
			long indexSize = Objects.requireNonNull(this.searchIndex).getIndexSize();

			Application.getMain(FileScannerMain.class).runWait(() -> this.ui.sessionProgress(progress, indexSize));
		}
	}

	@Override
	public void scanResult(FileScanner scanner, FileScannerResult result) {
		if (scanner.equals(this.fileScanner)) {
			Objects.requireNonNull(this.searchIndex).addResult(result);
			Application.getMain(FileScannerMain.class).runWait(() -> this.ui.sessionResult(result));
		}
	}

	@Override
	public void scanException(FileScanner scanner, Exception cause) {
		if (scanner.equals(this.fileScanner)) {
			Application.getMain(FileScannerMain.class).runWait(() -> this.ui.sessionException(cause));
		}
	}

}
