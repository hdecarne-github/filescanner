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
 * Utility class providing {@code DataType#U32} related functions.
 */
public final class U32Values {

	/**
	 * Get value from buffer.
	 * 
	 * @param buffer The buffer the get the value from.
	 * @return The retrieved value or null if the buffer has insufficient data.
	 */
	public static Integer get(ByteBuffer buffer) {
		return (DataType.U32.size() <= buffer.remaining() ? Integer.valueOf(buffer.getInt()) : null);
	}

	/**
	 * Get value from buffer at specific index.
	 * 
	 * @param buffer The buffer the get the value from.
	 * @param index The buffer index to get the value from.
	 * @return The retrieved value or null if the buffer has insufficient data.
	 */
	public static Integer get(ByteBuffer buffer, int index) {
		return (index + DataType.U32.size() <= buffer.capacity() ? Integer.valueOf(buffer.getInt(index)) : null);
	}

}
