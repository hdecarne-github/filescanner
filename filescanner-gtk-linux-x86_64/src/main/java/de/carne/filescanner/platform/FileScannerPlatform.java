/*
 * Copyright (c) 2007-2020 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.platform;

/**
 * Utility class providing platform dependent functions.
 */
public final class FileScannerPlatform {

	FileScannerPlatform() {
		// Prevent instantiation
	}

	/**
	 * Gets the CSS font size based upon the platform specific font height.
	 *
	 * @param height the font height to get the CSS font size for.
	 * @return the CSS font size.
	 */
	public static float cssFontSize(int height) {
		return height * 1.0f;
	}

}
