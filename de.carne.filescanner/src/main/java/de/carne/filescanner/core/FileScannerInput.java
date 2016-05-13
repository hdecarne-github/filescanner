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

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import de.carne.util.logging.Log;

/**
 * Abstract base class for all kinds of scanner input.
 */
public abstract class FileScannerInput implements Closeable {

	private static final Log LOG = new Log(FileScannerInput.class);

	private Path path;

	/**
	 * Construct {@code FileScannerInput}.
	 *
	 * @param path The input's path.
	 */
	protected FileScannerInput(Path path) {
		assert path != null;

		this.path = path;
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

}
