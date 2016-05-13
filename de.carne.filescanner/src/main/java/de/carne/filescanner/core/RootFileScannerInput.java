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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * File based {@code FileScannerInput} implementation.
 */
public class RootFileScannerInput extends FileScannerInput {

	private FileChannel file;

	private RootFileScannerInput(Path path, FileChannel file) {
		super(path);
		this.file = file;
	}

	/**
	 * Open {@code RootFileScannerInput}.
	 *
	 * @param path The file path to open.
	 * @return The opened input.
	 * @throws IOException if an I/O error occurs.
	 */
	public static RootFileScannerInput open(Path path) throws IOException {
		assert path != null;

		return new RootFileScannerInput(path, FileChannel.open(path, StandardOpenOption.READ));
	}

	/**
	 * Open {@code RootFileScannerInput}.
	 *
	 * @param fileName The file name to open.
	 * @return The opened input.
	 * @throws IOException if an I/O error occurs.
	 */
	public static RootFileScannerInput open(String fileName) throws IOException {
		assert fileName != null;

		return open(Paths.get(fileName));
	}

	/**
	 * Open {@code RootFileScannerInput}.
	 *
	 * @param file The file to open.
	 * @return The opened input.
	 * @throws IOException if an I/O error occurs.
	 */
	public static RootFileScannerInput open(File file) throws IOException {
		assert file != null;

		return open(file.toPath());
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerInput#size()
	 */
	@Override
	public long size() throws IOException {
		return this.file.size();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerInput#read(java.nio.ByteBuffer,
	 * long)
	 */
	@Override
	public int read(ByteBuffer dst, long position) throws IOException {
		return this.file.read(dst, position);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerInput#close0()
	 */
	@Override
	protected void close0() throws Exception {
		this.file.close();
	}

}
