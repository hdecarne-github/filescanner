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

import de.carne.filescanner.core.FileScannerResult;

/**
 * Base class for image exporter.
 */
public abstract class ImageResultExporter extends FileResultExporter {

	/**
	 * Construct {@code ImageResultExporter}.
	 *
	 * @param name The exporter name.
	 * @param defaultExtension The export file's default extension (without any
	 *        wildcards).
	 * @param extensionFilters The extension filters supported by the export
	 *        file type.
	 */
	protected ImageResultExporter(String name, String defaultExtension, String... extensionFilters) {
		super(name, defaultExtension, extensionFilters);
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
	public static ImageResultExporter defaultImageExporter(String name, String defaultExtension,
			String... extensionFilters) {
		return new ImageResultExporter(name, defaultExtension, extensionFilters) {

			@Override
			public StreamHandler getExportStreamHandler(FileScannerResult result) {
				MappingStreamHandler streamHandler = new MappingStreamHandler();

				streamHandler.mapResult(result);
				return streamHandler;
			}

		};
	}

}
