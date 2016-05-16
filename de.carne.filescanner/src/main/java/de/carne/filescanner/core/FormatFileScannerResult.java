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
package de.carne.filescanner.core;

import de.carne.filescanner.spi.FileScannerInput;
import de.carne.filescanner.spi.Format;

/**
 * {@code FileScannerResult} object of type
 * {@linkplain FileScannerResultType#FORMAT}.
 */
public class FormatFileScannerResult extends FileScannerResult {

	private final Format format;

	/**
	 * Construct {@code FormatFileScannerResult}.
	 *
	 * @param format The format represented by this result object.
	 * @param input The underlying input.
	 * @param start The result's start position.
	 */
	public FormatFileScannerResult(Format format, FileScannerInput input, long start) {
		super(null, FileScannerResultType.FORMAT, input, start, start);

		assert format != null;

		this.format = format;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerResult#getTitle()
	 */
	@Override
	public String getTitle() {
		return this.format.name();
	}

}
