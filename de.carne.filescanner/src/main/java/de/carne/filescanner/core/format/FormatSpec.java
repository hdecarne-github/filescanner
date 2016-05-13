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

import java.nio.ByteBuffer;

/**
 * Base class for all format specifications.
 */
public abstract class FormatSpec {

	private final boolean result;

	/**
	 * Construct {@code Format}.
	 * 
	 * @param result Whether this spec defines a scan result or not.
	 */
	protected FormatSpec(boolean result) {
		this.result = result;
	}

	/**
	 * Check whether this spec defines a scan result or not.
	 * 
	 * @return {@code true} if this spec defines a scan result.
	 */
	public boolean isResult() {
		return this.result;
	}

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

}
