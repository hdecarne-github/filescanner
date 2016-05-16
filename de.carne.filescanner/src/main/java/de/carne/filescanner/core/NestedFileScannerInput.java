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
import java.nio.ByteBuffer;
import java.nio.file.Path;

import de.carne.filescanner.spi.FileScannerInput;

/**
 *
 */
class NestedFileScannerInput extends FileScannerInput {

	/**
	 * @param scanner
	 * @param path
	 */
	public NestedFileScannerInput(FileScanner scanner, Path path) {
		super(scanner, path);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.spi.FileScannerInput#size()
	 */
	@Override
	public long size() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.spi.FileScannerInput#read(java.nio.ByteBuffer,
	 * long)
	 */
	@Override
	public int read(ByteBuffer dst, long position) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.spi.FileScannerInput#close0()
	 */
	@Override
	protected void close0() throws Exception {
		// Nothing to do here
	}

}
