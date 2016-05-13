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

/**
 * Data attribute types.
 */
public enum DataType {

	/**
	 * Signed byte.
	 */
	I8(1, true),

	/**
	 * Unsigned byte.
	 */
	U8(1, false),

	/**
	 * Signed word.
	 */
	I16(2, true),

	/**
	 * Unsigned word.
	 */
	U16(2, false),

	/**
	 * Signed dword.
	 */
	I32(4, true),

	/**
	 * Unsigned dword.
	 */
	U32(4, false),

	/**
	 * Signed qword.
	 */
	I64(8, true),

	/**
	 * Unsigned qword.
	 */
	U64(8, false);

	private int size;

	private boolean signed;

	private DataType(int size, boolean signed) {
		this.size = size;
		this.signed = signed;
	}

	/**
	 * Get the data type's size.
	 * 
	 * @return The data type's size.
	 */
	public int size() {
		return this.size;
	}

	/**
	 * Get the data type's signed flag.
	 * 
	 * @return The data type's signed flag.
	 */
	public boolean signed() {
		return this.signed;
	}

}
