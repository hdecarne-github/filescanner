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
 * Utility class providing an abstract interface for byte formatting.
 */
public abstract class ByteFormatter {

	/**
	 * Binary byte formatter.
	 */
	public static final ByteFormatter BINARY = new ByteFormatter() {

		@Override
		public StringBuilder format(StringBuilder buffer, byte b) {
			return Binary.format(buffer, b);
		}

	};

	/**
	 * Octal byte formatter.
	 */
	public static final ByteFormatter OCTAL = new ByteFormatter() {

		@Override
		public StringBuilder format(StringBuilder buffer, byte b) {
			return Octal.format(buffer, b);
		}

	};

	/**
	 * Hexadecimal byte formatter (lower case).
	 */
	public static final ByteFormatter HEXADECIMAL_L = new ByteFormatter() {

		@Override
		public StringBuilder format(StringBuilder buffer, byte b) {
			return Hexadecimal.formatL(buffer, b);
		}

	};

	/**
	 * Hexadecimal byte formatter (upper case).
	 */
	public static final ByteFormatter HEXADECIMAL_U = new ByteFormatter() {

		@Override
		public StringBuilder format(StringBuilder buffer, byte b) {
			return Hexadecimal.formatU(buffer, b);
		}

	};

	/**
	 * Format a {@code byte} value.
	 *
	 * @param buffer The buffer to format into.
	 * @param b The value to format.
	 * @return The updated buffer.
	 */
	public abstract StringBuilder format(StringBuilder buffer, byte b);

}
