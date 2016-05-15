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

/**
 * {@code FileScannerResult} object of type
 * {@linkplain FileScannerResultType#SUB_FORMAT}.
 */
public class SubFormatFileScannerResult extends FileScannerResult {

	/**
	 * Construct {@code SubFormatFileScannerResult}.
	 *
	 * @param format The format represented by this result object.
	 * @param input The underlying input.
	 * @param start The result's start position.
	 */
	public SubFormatFileScannerResult(FileScannerInput input, long start, long end, FileScannerResult parent) {
		super(FileScannerResultType.SUB_FORMAT, input, start, end, parent);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerResult#getTitle()
	 */
	@Override
	public String getTitle() {
		return "???";
	}

}
