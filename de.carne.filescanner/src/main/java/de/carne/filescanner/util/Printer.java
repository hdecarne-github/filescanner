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

import java.nio.CharBuffer;

/**
 * Utility class used for formatting character based data.
 */
public final class Printer {

	private static final char[] CHAR_MAP = new char[] {

			// 0x00 - 0x0f
			'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',

			// 0x10 - 0x1f
			'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',

			// 0x20 - 0x2f
			' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',

			// 0x30 - 0x3f
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?',

			// 0x40 - 0x4f
			'@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',

			// 0x50 - 0x5f
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_',

			// 0x60 - 0x6f
			'`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',

			// 0x70 - 0x7f
			'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\0',

			// 0x80 - 0x8f
			'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',

			// 0x90 - 0x9f
			'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',

			// 0xa0 - 0xaf
			'\u00a0', '\u00a1', '\u00a2', '\u00a3', '\u00a4', '\u00a5', '\u00a6', '\u00a7', '\u00a8', '\u00a9',
			'\u00aa', '\u00ab', '\u00ac', '\u00ad', '\u00ae', '\u00af',

			// 0xb0 - 0xbf
			'\u00b0', '\u00b1', '\u00b2', '\u00b3', '\u00b4', '\u00b5', '\u00b6', '\u00b7', '\u00b8', '\u00b9',
			'\u00ba', '\u00bb', '\u00bc', '\u00bd', '\u00be', '\u00bf',

			// 0xc0 - 0xcf
			'\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7', '\u00c8', '\u00c9',
			'\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf',

			// 0xd0 - 0xdf
			'\u00d0', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7', '\u00d8', '\u00d9',
			'\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df',

			// 0xe0 - 0xef
			'\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7', '\u00e8', '\u00e9',
			'\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef',

			// 0xf0 - 0xff
			'\u00f0', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7', '\u00f8', '\u00f9',
			'\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u00ff'

	};

	/**
	 * Format {@code byte} value.
	 *
	 * @param buffer The buffer to format into.
	 * @param b The value to format.
	 * @param nonPrintable The character to use for non-printable values.
	 * @return The updated buffer.
	 */
	public static StringBuilder format(StringBuilder buffer, byte b, char nonPrintable) {
		char mapped = CHAR_MAP[b & 0xff];

		buffer.append(mapped != '\0' ? mapped : nonPrintable);
		return buffer;
	}

	/**
	 * Format {@code char} value.
	 *
	 * @param buffer The buffer to format into.
	 * @param c The value to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder format(StringBuilder buffer, char c) {
		switch (c) {
		case '\0':
			buffer.append("\0");
			break;
		case '\b':
			buffer.append("\b");
			break;
		case '\t':
			buffer.append("\t");
			break;
		case '\n':
			buffer.append("\n");
			break;
		case '\r':
			buffer.append("\r");
			break;
		case '\\':
			buffer.append("\\\\");
			break;
		case '"':
			buffer.append("\"");
			break;
		default:
			buffer.append(isPrintable(c) ? c : Hexadecimal.formatL(new StringBuilder("\\u"), (short) c).toString());
		}
		return buffer;
	}

	private static boolean isPrintable(int codePoint) {
		return (((1 << Character.CONTROL) | (1 << Character.FORMAT) | (1 << Character.PRIVATE_USE)
				| (1 << Character.SURROGATE)) & Character.getType(codePoint)) == 0;
	}

	/**
	 * Format {@code CharBuffer} content.
	 *
	 * @param buffer The buffer to format into.
	 * @param charBuffer The values to format.
	 * @return The updated buffer.
	 */
	public static StringBuilder format(StringBuilder buffer, CharBuffer charBuffer) {
		while (charBuffer.hasRemaining()) {
			format(buffer, charBuffer.get());
		}
		return buffer;
	}

}
