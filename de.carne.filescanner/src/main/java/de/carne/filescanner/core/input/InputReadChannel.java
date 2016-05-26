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
package de.carne.filescanner.core.input;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import de.carne.filescanner.spi.FileScannerInput;

/**
 * Helper class providing {@linkplain ReadableByteChannel} access to an input.
 */
class InputReadChannel implements ReadableByteChannel {

	private final FileScannerInput input;

	private long readPosition;

	public InputReadChannel(FileScannerInput input, long readPosition) {
		this.input = input;
		this.readPosition = readPosition;
	}

	/*
	 * (non-Javadoc)
	 * @see java.nio.channels.Channel#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.nio.channels.Channel#close()
	 */
	@Override
	public void close() throws IOException {
		// Nothing to do here
	}

	/*
	 * (non-Javadoc)
	 * @see java.nio.channels.ReadableByteChannel#read(java.nio.ByteBuffer)
	 */
	@Override
	public synchronized int read(ByteBuffer dst) throws IOException {
		int read = this.input.read(dst, this.readPosition);

		if (read > 0) {
			this.readPosition += read;
		}
		return read;
	}

}
