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
package de.carne.filescanner.swt.export;

import java.nio.file.Path;

import de.carne.filescanner.engine.transfer.FileScannerResultExportHandler;

/**
 * This class holds the export options selected during an {@linkplain ExportDialog} invocation.
 */
public final class ExportOptions {

	private final FileScannerResultExportHandler exportHandler;
	private final Path path;
	private final boolean overwrite;

	ExportOptions(FileScannerResultExportHandler exportHandler, Path path, boolean overwrite) {
		this.exportHandler = exportHandler;
		this.path = path;
		this.overwrite = overwrite;
	}

	/**
	 * Gets the selected {@linkplain FileScannerResultExportHandler}.
	 *
	 * @return the selected {@linkplain FileScannerResultExportHandler}.
	 */
	public FileScannerResultExportHandler exportHandler() {
		return this.exportHandler;
	}

	/**
	 * Gets the selected export file path.
	 *
	 * @return the selected export file path.
	 */
	public Path path() {
		return this.path;
	}

	/**
	 * Gets the overwrite confirmation.
	 *
	 * @return the overwrite confirmation.
	 */
	public boolean overwrite() {
		return this.overwrite;
	}

}
