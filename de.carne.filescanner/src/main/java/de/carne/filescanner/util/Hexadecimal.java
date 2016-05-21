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
 * Utility class used for hexadecimal formatting.
 */
public final class Hexadecimal {

	private static final char[] LOWER_NIBBLE_MAP = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
			'b', 'c', 'd', 'e', 'f' };

	private static final char[] UPPER_NIBBLE_MAP = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
			'B', 'C', 'D', 'E', 'F' };

	/**
	 * Format a {@code byte} value using lower case characters.
	 *
	 * @param buffer The buffer to format into.
	 * @param b The value to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder formatL(StringBuilder buffer, byte b) {
		buffer.append(LOWER_NIBBLE_MAP[(b >> 4) & 0xf]);
		buffer.append(LOWER_NIBBLE_MAP[b & 0xf]);
		return buffer;
	}

	/**
	 * Format a {@code byte} value using upper case characters.
	 *
	 * @param buffer The buffer to format into.
	 * @param b The value to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder formatU(StringBuilder buffer, byte b) {
		buffer.append(UPPER_NIBBLE_MAP[(b >> 4) & 0xf]);
		buffer.append(UPPER_NIBBLE_MAP[b & 0xf]);
		return buffer;
	}

	/**
	 * Format a {@code short} value using lower case characters.
	 *
	 * @param buffer The buffer to format into.
	 * @param s The value to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder formatL(StringBuilder buffer, short s) {
		buffer.append(LOWER_NIBBLE_MAP[(s >> 12) & 0xf]);
		buffer.append(LOWER_NIBBLE_MAP[(s >> 8) & 0xf]);
		buffer.append(LOWER_NIBBLE_MAP[(s >> 4) & 0xf]);
		buffer.append(LOWER_NIBBLE_MAP[s & 0xf]);
		return buffer;
	}

	/**
	 * Format a {@code short} value using lower case characters.
	 *
	 * @param s The value to format.
	 * @return The formatted value.
	 */
	public static String formatL(short s) {
		return formatL(new StringBuilder(), s).toString();
	}

	/**
	 * Format a {@code short} value using upper case characters.
	 *
	 * @param buffer The buffer to format into.
	 * @param s The value to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder formatU(StringBuilder buffer, short s) {
		buffer.append(UPPER_NIBBLE_MAP[(s >> 12) & 0xf]);
		buffer.append(UPPER_NIBBLE_MAP[(s >> 8) & 0xf]);
		buffer.append(UPPER_NIBBLE_MAP[(s >> 4) & 0xf]);
		buffer.append(UPPER_NIBBLE_MAP[s & 0xf]);
		return buffer;
	}

	/**
	 * Format a {@code short} value using upper case characters.
	 *
	 * @param s The value to format.
	 * @return The formatted value.
	 */
	public static String formatU(short s) {
		return formatU(new StringBuilder(), s).toString();
	}

	/**
	 * Format a {@code int} value using lower case characters.
	 *
	 * @param buffer The buffer to format into.
	 * @param i The value to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder formatL(StringBuilder buffer, int i) {
		buffer.append(LOWER_NIBBLE_MAP[(i >> 28) & 0xf]);
		buffer.append(LOWER_NIBBLE_MAP[(i >> 24) & 0xf]);
		buffer.append(LOWER_NIBBLE_MAP[(i >> 20) & 0xf]);
		buffer.append(LOWER_NIBBLE_MAP[(i >> 16) & 0xf]);
		buffer.append(LOWER_NIBBLE_MAP[(i >> 12) & 0xf]);
		buffer.append(LOWER_NIBBLE_MAP[(i >> 8) & 0xf]);
		buffer.append(LOWER_NIBBLE_MAP[(i >> 4) & 0xf]);
		buffer.append(LOWER_NIBBLE_MAP[i & 0xf]);
		return buffer;
	}

	/**
	 * Format a {@code int} value using lower case characters.
	 *
	 * @param i The value to format.
	 * @return The formatted value.
	 */
	public static String formatL(int i) {
		return formatL(new StringBuilder(), i).toString();
	}

	/**
	 * Format a {@code int} value using upper case characters.
	 *
	 * @param buffer The buffer to format into.
	 * @param i The value to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder formatU(StringBuilder buffer, int i) {
		buffer.append(UPPER_NIBBLE_MAP[(i >> 28) & 0xf]);
		buffer.append(UPPER_NIBBLE_MAP[(i >> 24) & 0xf]);
		buffer.append(UPPER_NIBBLE_MAP[(i >> 20) & 0xf]);
		buffer.append(UPPER_NIBBLE_MAP[(i >> 16) & 0xf]);
		buffer.append(UPPER_NIBBLE_MAP[(i >> 12) & 0xf]);
		buffer.append(UPPER_NIBBLE_MAP[(i >> 8) & 0xf]);
		buffer.append(UPPER_NIBBLE_MAP[(i >> 4) & 0xf]);
		buffer.append(UPPER_NIBBLE_MAP[i & 0xf]);
		return buffer;
	}

	/**
	 * Format a {@code int} value using upper case characters.
	 *
	 * @param i The value to format.
	 * @return The formatted value.
	 */
	public static String formatU(int i) {
		return formatU(new StringBuilder(), i).toString();
	}

	/**
	 * Format a {@code long} value using lower case characters.
	 *
	 * @param buffer The buffer to format into.
	 * @param l The value to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder formatL(StringBuilder buffer, long l) {
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 60) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 56) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 52) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 48) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 44) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 40) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 36) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 32) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 28) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 24) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 20) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 16) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 12) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 8) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) ((l >>> 4) & 0xfl)]);
		buffer.append(LOWER_NIBBLE_MAP[(int) (l & 0xfl)]);
		return buffer;
	}

	/**
	 * Format a {@code long} value using upper case characters.
	 *
	 * @param buffer The buffer to format into.
	 * @param l The value to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder formatU(StringBuilder buffer, long l) {
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 60) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 56) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 52) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 48) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 44) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 40) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 36) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 32) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 28) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 24) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 20) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 16) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 12) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 8) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) ((l >>> 4) & 0xfl)]);
		buffer.append(UPPER_NIBBLE_MAP[(int) (l & 0xfl)]);
		return buffer;
	}

}
