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
package de.carne.filescanner.core.format;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.FileScannerResultType;
import de.carne.filescanner.spi.FileScannerInput;

/**
 * Base class for all format specifications.
 */
public abstract class FormatSpec {

	private Decodable decodable = null;

	/**
	 * Get this spec's match size.
	 * <p>
	 * The match size defines the number of bytes required by this spec to
	 * perform a data match.
	 * </p>
	 *
	 * @return This spec's match size or {@code 0} if matching is not supported.
	 */
	public int matchSize() {
		return 0;
	}

	/**
	 * Check whether a data chunk matches this spec.
	 *
	 * @param buffer The buffer containing the data to match.
	 * @return {@code true} if the data matches.
	 */
	public boolean matches(ByteBuffer buffer) {
		int matchSize = matchSize();
		boolean matches = matchSize != 0 && isSA(buffer, matchSize);

		if (matches) {
			buffer.position(buffer.position() + matchSize);
		}
		return matches;
	}

	/**
	 * Get this's specs result type.
	 *
	 * @return This's specs result type.
	 */
	public FileScannerResultType resultType() {
		return FileScannerResultType.FORMAT;
	}

	/**
	 * Process the input data and eval this spec.
	 *
	 * @param result The result builder object to decode into.
	 * @param position The position to start evaluating at.
	 * @return The number of evaluated bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract long eval(FileScannerResultBuilder result, long position) throws IOException;

	/**
	 * Set this spec's {@code Decodable} service.
	 *
	 * @param decodable The {@code Decodable} service to set.
	 * @return The updated format spec.
	 */
	public FormatSpec setDecodable(Decodable decodable) {
		this.decodable = decodable;
		return this;
	}

	/**
	 * Get this spec's {@code Decodable} service.
	 *
	 * @return his spec's {@code Decodable} service or {@code null} if this spec
	 *         does not support decoding.
	 */
	public Decodable getDecodable() {
		return this.decodable;
	}

	/**
	 * Check whether a buffer contains sufficient data.
	 *
	 * @param buffer The buffer to check.
	 * @param size The required number of data bytes.
	 * @return {@code true} if the buffer contains the required number of bytes
	 *         or more.
	 */
	protected static final boolean isSA(ByteBuffer buffer, int size) {
		return size <= buffer.remaining();
	}

	/**
	 * Ensure that a buffer contains sufficient data.
	 *
	 * @param buffer The buffer to check.
	 * @param size The required number of data bytes.
	 * @return The checked buffer.
	 * @throws EOFException if the buffer does not contain the required number
	 *         of bytes.
	 */
	protected static final ByteBuffer ensureSA(ByteBuffer buffer, int size) throws EOFException {
		if (!(size <= buffer.remaining())) {
			throw new EOFException("Insufficent buffer data: Requested " + size + ", got " + buffer.remaining());
		}
		return buffer;
	}

	/**
	 * Check whether an input contains sufficient data.
	 *
	 * @param input The input to check.
	 * @param position The input position to check with.
	 * @param size The required number of data bytes.
	 * @return {@code true} if the input contains the required number of bytes
	 *         or more.
	 * @throws IOException if an I/O error occurs.
	 */
	protected static final boolean isSA(FileScannerInput input, long position, long size)
			throws IOException {
		return (position + size) <= input.size();
	}

	/**
	 * Ensure that an input contains sufficient data.
	 *
	 * @param input The input to check.
	 * @param position The input position to check with.
	 * @param size The required number of data bytes.
	 * @throws EOFException if the input does not contain the required number of
	 *         bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	protected static final void ensureSA(FileScannerInput input, long position, long size)
			throws EOFException, IOException {
		long inputSize = input.size();

		if (!((position + size) <= inputSize)) {
			throw new EOFException("Insufficent input data: Requested " + size + ", got " + (inputSize - position));
		}
	}

}
