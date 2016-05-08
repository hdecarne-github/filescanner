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
package de.carne.filescanner.util;

/**
 * Utility class used for binary formatting.
 */
public final class Binary {

	private static final char[] BIT_MAP = new char[] { '0', '1' };

	/**
	 * Format a {@code byte} value.
	 *
	 * @param buffer The buffer to format into.
	 * @param b The value to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder format(StringBuilder buffer, byte b) {
		buffer.append(BIT_MAP[(b >> 7) & 0x1]);
		buffer.append(BIT_MAP[(b >> 6) & 0x1]);
		buffer.append(BIT_MAP[(b >> 5) & 0x1]);
		buffer.append(BIT_MAP[(b >> 4) & 0x1]);
		buffer.append(BIT_MAP[(b >> 3) & 0x1]);
		buffer.append(BIT_MAP[(b >> 2) & 0x1]);
		buffer.append(BIT_MAP[(b >> 1) & 0x1]);
		buffer.append(BIT_MAP[b & 0x1]);
		return buffer;
	}

}
