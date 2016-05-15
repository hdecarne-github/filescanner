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
package de.carne.filescanner.spi;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import de.carne.filescanner.core.FileScanner;
import de.carne.util.logging.Log;

/**
 * Base class for all kinds of scanner inputs.
 */
public abstract class FileScannerInput implements Closeable {

	private static final Log LOG = new Log(FileScannerInput.class);

	private final FileScanner scanner;

	private final Path path;

	/**
	 * Construct {@code FileScannerInput}.
	 *
	 * @param scanner The {@code FileScanner} scanning this input.
	 * @param path The input's path.
	 */
	public FileScannerInput(FileScanner scanner, Path path) {
		assert scanner != null;
		assert path != null;

		this.scanner = scanner;
		this.path = path;
	}

	/**
	 * Get the {@code FileScanner} scanning this input.
	 *
	 * @return The {@code FileScanner} scanning this input.
	 */
	public FileScanner scanner() {
		return this.scanner;
	}

	/**
	 * Get the input's path.
	 *
	 * @return The input's path.
	 */
	public Path path() {
		return this.path;
	}

	/**
	 * Get the input's size.
	 *
	 * @return The input's size.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract long size() throws IOException;

	/**
	 * Read data from input.
	 *
	 * @param dst The {@code ByteBuffer} to read into.
	 * @param position The position to read from.
	 * @return The number of read bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract int read(ByteBuffer dst, long position) throws IOException;

	/**
	 * Start scanning this input.
	 *
	 * @see FileScanner#queueInput(FileScannerInput)
	 * @throws IOException if an I/O error occurs.
	 */
	public void startScan() throws IOException {
		this.scanner.queueInput(this);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public final void close() {
		try {
			close0();
		} catch (Exception e) {
			LOG.warning(e, null, "An error occurred while closing input: ''{0}''", this.path);
		}
	}

	/**
	 * Function responsible for the actual closing operation.
	 *
	 * @throws Exception if an error occurs.
	 */
	protected abstract void close0() throws Exception;

	private static class FileChannelInput extends FileScannerInput {

		private final FileChannel file;

		FileChannelInput(FileScanner scanner, Path path, FileChannel file) {
			super(scanner, path);
			this.file = file;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.filescanner.spi.FileScannerInput#size()
		 */
		@Override
		public long size() throws IOException {
			return this.file.size();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * de.carne.filescanner.spi.FileScannerInput#read(java.nio.ByteBuffer,
		 * long)
		 */
		@Override
		public int read(ByteBuffer dst, long position) throws IOException {
			return this.file.read(dst, position);
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.filescanner.spi.FileScannerInput#close0()
		 */
		@Override
		protected void close0() throws Exception {
			this.file.close();
		}

	}

	/**
	 * Open file based {@code FileScannerInput}.
	 *
	 * @param scanner The {@code FileScanner} to open the input for.
	 * @param path The file path to open.
	 * @return The opened input.
	 * @throws IOException if an I/O error occurs.
	 */
	public static FileScannerInput open(FileScanner scanner, Path path) throws IOException {
		assert path != null;

		return new FileChannelInput(scanner, path, FileChannel.open(path, StandardOpenOption.READ));
	}

	/**
	 * Open file based {@code FileScannerInput}.
	 *
	 * @param scanner The {@code FileScanner} to open the input for.
	 * @param fileName The file name to open.
	 * @return The opened input.
	 * @throws IOException if an I/O error occurs.
	 */
	public static FileScannerInput open(FileScanner scanner, String fileName) throws IOException {
		assert fileName != null;

		return open(scanner, Paths.get(fileName));
	}

	/**
	 * Open file based {@code FileScannerInput}.
	 *
	 * @param scanner The {@code FileScanner} to open the input for.
	 * @param file The file to open.
	 * @return The opened input.
	 * @throws IOException if an I/O error occurs.
	 */
	public static FileScannerInput open(FileScanner scanner, File file) throws IOException {
		assert file != null;

		return open(scanner, file.toPath());
	}

}
