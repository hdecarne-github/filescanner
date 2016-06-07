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

import java.io.IOException;
import java.nio.ByteBuffer;

import de.carne.filescanner.core.FileScannerInput;
import de.carne.filescanner.jfx.control.FileAccess;

/**
 * Helper class providing {@FileAccess} functions on {@code FileScannerInput}.
 */
class FileScannerInputAccess implements FileAccess {

	private FileScannerInput input;

	FileScannerInputAccess(FileScannerInput input) {
		this.input = input;
	}

	@Override
	public long size() throws IOException {
		return this.input.size();
	}

	@Override
	public int read(ByteBuffer dst, long position) throws IOException {
		return this.input.read(dst, position);
	}

}
