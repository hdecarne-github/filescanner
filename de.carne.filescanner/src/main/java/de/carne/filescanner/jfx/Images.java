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
package de.carne.filescanner.jfx;

import java.io.IOException;
import java.io.InputStream;

import javafx.scene.image.Image;

/**
 * Utility class for accessing Image resources.
 */
public final class Images {

	/**
	 * Success icon (16x16)
	 */
	public static final Image IMAGE_SUCCESS16 = getImage(Images.class, "iconSuccess16.png");

	/**
	 * Success icon (32x32)
	 */
	public static final Image IMAGE_SUCCESS32 = getImage(Images.class, "iconSuccess32.png");

	/**
	 * Info icon (16x16)
	 */
	public static final Image IMAGE_INFO16 = getImage(Images.class, "iconInfo16.png");

	/**
	 * Info icon (32x32)
	 */
	public static final Image IMAGE_INFO32 = getImage(Images.class, "iconInfo32.png");

	/**
	 * Warning icon (16x16)
	 */
	public static final Image IMAGE_WARNING16 = getImage(Images.class, "iconWarning16.png");

	/**
	 * Warning icon (32x32)
	 */
	public static final Image IMAGE_WARNING32 = getImage(Images.class, "iconWarning32.png");

	/**
	 * Error icon (16x16)
	 */
	public static final Image IMAGE_ERROR16 = getImage(Images.class, "iconError16.png");

	/**
	 * Error icon (32x32)
	 */
	public static final Image IMAGE_ERROR32 = getImage(Images.class, "iconError32.png");

	/**
	 * Notice icon (16x16)
	 */
	public static final Image IMAGE_NOTICE16 = getImage(Images.class, "iconNotice16.png");

	/**
	 * Debug icon (16x16)
	 */
	public static final Image IMAGE_DEBUG16 = getImage(Images.class, "iconDebug16.png");

	/**
	 * Help icon (16x16)
	 */
	public static final Image IMAGE_HELP16 = getImage(Images.class, "iconHelp16.png");

	/**
	 * Help icon (32x32)
	 */
	public static final Image IMAGE_HELP32 = getImage(Images.class, "iconHelp32.png");

	/**
	 * Question icon (16x16)
	 */
	public static final Image IMAGE_QUESTION16 = getImage(Images.class, "iconQuestion16.png");

	/**
	 * Question icon (32x32)
	 */
	public static final Image IMAGE_QUESTION32 = getImage(Images.class, "iconQuestion32.png");

	/**
	 * FileScanner icon (16x16)
	 */
	public static final Image IMAGE_FILESCANNER16 = getImage(Images.class, "iconFileScanner16.png");

	/**
	 * FileScanner icon (32x32)
	 */
	public static final Image IMAGE_FILESCANNER32 = getImage(Images.class, "iconFileScanner32.png");

	/**
	 * FileScanner icon (48x48)
	 */
	public static final Image IMAGE_FILESCANNER48 = getImage(Images.class, "iconFileScanner48.png");

	/**
	 * Input result icon (16x16)
	 */
	public static final Image IMAGE_INPUT_RESULT16 = getImage(Images.class, "iconInputResult16.png");

	/**
	 * Format result icon (16x16)
	 */
	public static final Image IMAGE_FORMAT_RESULT16 = getImage(Images.class, "iconFormatResult16.png");

	/**
	 * Encoded input result icon (16x16)
	 */
	public static final Image IMAGE_ENCODED_INPUT_RESULT16 = getImage(Images.class, "iconEncodedInputResult16.png");

	/**
	 * FileScanner icon (128x128)
	 */
	public static final Image IMAGE_FILESCANNER128 = getImage(Images.class, "iconFileScanner128.png");

	private static Image getImage(Class<?> resourceClass, String resourceName) {
		Image image;

		try (InputStream imageStream = resourceClass.getResourceAsStream(resourceName)) {
			if (imageStream == null) {
				throw new IOException("Unable to access resource: " + resourceName);
			}
			image = new Image(imageStream);
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		return image;
	}

}
