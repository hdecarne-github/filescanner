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
 * Utility class used for formatting of various Units.
 */
public final class Units {

	/**
	 * Format byte value using standard byte units (Byte, kB, MB, ...).
	 * 
	 * @param value The value to format.
	 * @return The formatted value including the corresponding unit string.
	 */
	public static String formatByteValue(long value) {
		return formatByteValue(new StringBuilder(), value).toString();
	}

	/**
	 * Format byte value using standard byte units (Byte, kB, MB, ...).
	 * 
	 * @param buffer The buffer to format into.
	 * @param value The value to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder formatByteValue(StringBuilder buffer, long value) {
		if (value > (1l << 40)) {
			buffer.append(Long.toString(value >>> 40)).append(" TB");
		} else if (value > (1l << 30)) {
			buffer.append(Long.toString(value >>> 30)).append(" GB");
		} else if (value > (1l << 20)) {
			buffer.append(Long.toString(value >>> 20)).append(" MB");
		} else if (value > (1l << 10)) {
			buffer.append(Long.toString(value >>> 10)).append(" kB");
		} else {
			buffer.append(Long.toString(value)).append(" Byte");
		}
		return buffer;
	}

}
