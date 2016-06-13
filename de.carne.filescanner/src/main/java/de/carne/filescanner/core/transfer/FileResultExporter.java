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
package de.carne.filescanner.core.transfer;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.carne.filescanner.core.FileScannerResult;

/**
 * Base class for file exporter.
 */
public abstract class FileResultExporter extends ResultExporter {

	private static final String DEFAULT_EXPORT_NAME = "Export";

	/**
	 * Default exporter for simply writing a scanner result to a file.
	 */
	public static final FileResultExporter BIN_EXPORTER = defaultFileExporter(I18N.formatSTR_RAW_EXPORT_NAME(), ".bin",
			"*.bin");

	private final String defaultExtension;

	private final String[] extensionFilters;

	/**
	 * Construct {@code FileResultExporter}.
	 *
	 * @param name The exporter name.
	 * @param defaultExtension The export file's default extension (without any
	 *        wildcards).
	 * @param extensionFilters The extension filters supported by the export
	 *        file type.
	 */
	protected FileResultExporter(String name, String defaultExtension, String... extensionFilters) {
		super(name);

		assert defaultExtension != null;
		assert extensionFilters.length > 0;

		this.defaultExtension = defaultExtension;
		this.extensionFilters = extensionFilters;
	}

	/**
	 * Create simple exporter performing a pass-through export of a scanner
	 * result.
	 * 
	 * @param name The exporter name.
	 * @param defaultExtension The export file's default extension (without any
	 *        wildcards).
	 * @param extensionFilters The extension filters supported by the export
	 *        file type.
	 * @return The created exporter.
	 */
	public static FileResultExporter defaultFileExporter(String name, String defaultExtension, String... extensionFilters) {
		return new FileResultExporter(name, defaultExtension, extensionFilters) {

			@Override
			public StreamHandler getExportStreamHandler(FileScannerResult result) {
				MappingStreamHandler streamHandler = new MappingStreamHandler();

				streamHandler.mapResult(result);
				return streamHandler;
			}

		};
	}

	/**
	 * Get the extension filters supported by the export file type.
	 *
	 * @return The extension filters supported by the export file type.
	 */
	public final String[] extensionFilters() {
		return this.extensionFilters;
	}

	/**
	 * Get the {@linkplain StreamHandler} providing access to the export data.
	 *
	 * @param result The result object to export.
	 * @return The {@linkplain StreamHandler} providing access to the export
	 *         data.
	 */
	public abstract StreamHandler getExportStreamHandler(FileScannerResult result);

	/**
	 * Get the default export file path for the exporter.
	 *
	 * @param file An optional file path to derive the default export file path
	 *        from.
	 * @return The generated default export file path.
	 */
	public Path getDefaultExportFilePath(Path file) {
		Path directory = null;
		Path namePath = null;

		if (file != null) {
			directory = file.getParent();
			namePath = file.getFileName();
		}
		return getDefaultExportFilePath(directory, (namePath != null ? namePath.toString() : null));
	}

	/**
	 * Get the default export file path for the exporter.
	 *
	 * @param directory An optional directory path to derive the default export
	 *        file path from.
	 * @param name An optional file name to derive the default export file path
	 *        from.
	 * @return The generated default export file path.
	 */
	public Path getDefaultExportFilePath(Path directory, String name) {
		Path exportDirectory = (directory != null ? directory : getDefaultExportDirectory());
		String exportName;

		if (name != null) {
			int extensionIndex = name.lastIndexOf('.');

			if (extensionIndex >= 0) {
				exportName = name.substring(0, extensionIndex) + this.defaultExtension;
			} else {
				exportName = name + this.defaultExtension;
			}
		} else {
			exportName = DEFAULT_EXPORT_NAME + this.defaultExtension;
		}
		return exportDirectory.resolve(exportName);
	}

	private static Path getDefaultExportDirectory() {
		return Paths.get("").toAbsolutePath();
	}

}
