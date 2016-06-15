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
package de.carne.jfx;

import de.carne.util.Platform;
import de.carne.util.Strings;

/**
 * Utility class providing workarounds for known quirks in the Java FX runtime.
 */
public final class FXQuirks {

	/**
	 * Fix double line breaks on paste issue on windows platform.
	 *
	 * @param s The string to copy to the clipboard.
	 * @return The fixed string.
	 */
	public static String fxClipboardString(String s) {
		if (Platform.IS_WINDOWS) {
			return Strings.remove(s, '\r');
		}
		return s;
	}

}
