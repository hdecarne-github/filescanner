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

import java.io.IOException;

import de.carne.filescanner.spi.FileScannerInput;

/**
 * {@code FileScannerResult} object of type
 * {@linkplain FileScannerResultType#INPUT}.
 */
class InputFileScannerResult extends FileScannerResult {

	private final long end;

	private final FileScannerResult parent;

	InputFileScannerResult(FileScannerResult parent, FileScannerInput input) throws IOException {
		super(FileScannerResultType.INPUT, input, 0l);

		this.end = input.size();
		this.parent = parent;
		if (this.parent != null) {
			this.parent.addChild(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerResult#end()
	 */
	@Override
	public long end() {
		return this.end;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerResult#parent()
	 */
	@Override
	public FileScannerResult parent() {
		return this.parent;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerResult#title()
	 */
	@Override
	public String title() {
		return input().path().getFileName().toString();
	}

}