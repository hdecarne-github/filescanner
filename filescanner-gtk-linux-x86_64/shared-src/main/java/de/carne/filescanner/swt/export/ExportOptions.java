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
package de.carne.filescanner.swt.export;

import java.nio.file.Path;

import de.carne.filescanner.engine.FileScannerResultExporter;

/**
 * This class holds the export options selected during an {@linkplain ExportDialog} invocation.
 */
public final class ExportOptions {

	private final FileScannerResultExporter exporter;
	private final Path path;

	ExportOptions(FileScannerResultExporter exporter, Path path) {
		this.exporter = exporter;
		this.path = path;
	}

	/**
	 * The selected {@linkplain FileScannerResultExporter}.
	 *
	 * @return the selected {@linkplain FileScannerResultExporter}.
	 */
	public FileScannerResultExporter exporter() {
		return this.exporter;
	}

	/**
	 * The selected export file path.
	 *
	 * @return the selected export file path.
	 */
	public Path path() {
		return this.path;
	}

}
