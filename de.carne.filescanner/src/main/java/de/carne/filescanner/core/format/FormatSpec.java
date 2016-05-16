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

import java.io.IOException;
import java.nio.ByteBuffer;

import de.carne.filescanner.core.FileScannerResult;

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
		boolean matches = 0 < matchSize && matchSize <= buffer.remaining();

		if (matches) {
			buffer.position(buffer.position() + matchSize);
		}
		return matches;
	}

	/**
	 * Process the input data and eval this spec.
	 *
	 * @param result The result object of the current decode step.
	 * @param position The position to start evaluating at.
	 * @return The number of evaluated bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract long eval(FileScannerResult result, long position) throws IOException;

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

}
