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
package de.carne.util;

/**
 * Utility class providing string related functions.
 */
public final class Strings {

	/**
	 * The current VM's newline string.
	 */
	public static final String NEWLINE = System.getProperty("line.separator");

	/**
	 * Check whether a string is empty (null or "").
	 *
	 * @param s The string to check.
	 * @return true, if the string is empty (null or "").
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	/**
	 * Check whether a string is not empty (not null and not "").
	 *
	 * @param s The string to check.
	 * @return true, if the string is not empty (not null and not "").
	 */
	public static boolean notEmpty(String s) {
		return s != null && s.length() > 0;
	}

	/**
	 * Perform a safe trim on a string that may be null.
	 *
	 * @param s The string to trim or null.
	 * @return The trimmed string or "" if the string is null.
	 */
	public static String safeTrim(String s) {
		return (s != null ? s.trim() : "");
	}

	/**
	 * Remove all occurrences of specific character from a string.
	 *
	 * @param s The string to remove the character from.
	 * @param c The character to remove.
	 * @return The processed string.
	 */
	public static String remove(String s, char c) {
		StringBuilder buffer = null;

		if (s != null && s.indexOf(c) >= 0) {
			int length = s.length();

			buffer = new StringBuilder(length);
			for (int charIndex = 0; charIndex < length; charIndex++) {
				char currentChar = s.charAt(charIndex);

				if (currentChar != c) {
					buffer.append(currentChar);
				}
			}
		}
		return (buffer != null ? buffer.toString() : s);
	}

}
