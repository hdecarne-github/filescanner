/*
 * Copyright (c) 2007-2022 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.swt.resources;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

/**
 * Image resources.
 */
public final class Images {

	private Images() {
		// Prevent instantiation
	}

	/**
	 * fslogo16.png
	 */
	public static final String IMAGE_FSLOGO16 = "fslogo16.png";
	/**
	 * fslogo32.png
	 */
	public static final String IMAGE_FSLOGO32 = "fslogo32.png";
	/**
	 * fslogo48.png
	 */
	public static final String IMAGE_FSLOGO48 = "fslogo48.png";
	/**
	 * fslogo128.png
	 */
	public static final String IMAGE_FSLOGO128 = "fslogo128.png";
	/**
	 * fslogo{16,32,48,128}.png
	 */
	public static final Iterable<String> IMAGES_FSLOGO = Arrays.asList(IMAGE_FSLOGO16, IMAGE_FSLOGO32, IMAGE_FSLOGO48,
			IMAGE_FSLOGO128);
	/**
	 * open_file16.png
	 */
	public static final String IMAGE_OPEN_FILE16 = "open_file16.png";
	/**
	 * print_object16.png
	 */
	public static final String IMAGE_PRINT_OBJECT16 = "print_object16.png";
	/**
	 * print_object_disabled16.png
	 */
	public static final String IMAGE_PRINT_OBJECT_DISABLED16 = "print_object_disabled16.png";
	/**
	 * export_object16.png
	 */
	public static final String IMAGE_EXPORT_OBJECT16 = "export_object16.png";
	/**
	 * export_object_disabled16.png
	 */
	public static final String IMAGE_EXPORT_OBJECT_DISABLED16 = "export_object_disabled16.png";
	/**
	 * copy_object16.png
	 */
	public static final String IMAGE_COPY_OBJECT16 = "copy_object16.png";
	/**
	 * copy_object_disabled16.png
	 */
	public static final String IMAGE_COPY_OBJECT_DISABLED16 = "copy_object_disabled16.png";
	/**
	 * copy_default16.png
	 */
	public static final String IMAGE_COPY_DEFAULT16 = "copy_default16.png";
	/**
	 * view_object16.png
	 */
	public static final String IMAGE_VIEW_OBJECT16 = "view_object16.png";
	/**
	 * view_object_disabled16.png
	 */
	public static final String IMAGE_VIEW_OBJECT_DISABLED16 = "view_object_disabled16.png";
	/**
	 * view_default16.png
	 */
	public static final String IMAGE_VIEW_DEFAULT16 = "view_default16.png";
	/**
	 * goto_next16.png
	 */
	public static final String IMAGE_GOTO_NEXT16 = "goto_next16.png";
	/**
	 * goto_previous16.png
	 */
	public static final String IMAGE_GOTO_PREVIOUS16 = "goto_previous16.png";
	/**
	 * goto_end16.png
	 */
	public static final String IMAGE_GOTO_END16 = "goto_end16.png";
	/**
	 * goto_start16.png
	 */
	public static final String IMAGE_GOTO_START16 = "goto_start16.png";
	/**
	 * result_input16.png
	 */
	public static final String IMAGE_RESULT_INPUT16 = "result_input16.png";
	/**
	 * result_format16.png
	 */
	public static final String IMAGE_RESULT_FORMAT16 = "result_format16.png";
	/**
	 * result_encoded_input16.png
	 */
	public static final String IMAGE_RESULT_ENCODED_INPUT16 = "result_encoded_input16.png";
	/**
	 * stop16.png
	 */
	public static final String IMAGE_STOP16 = "stop16.png";
	/**
	 * stop_disabled16.png
	 */
	public static final String IMAGE_STOP_DISABLED16 = "stop_disabled16.png";
	/**
	 * trash16.png
	 */
	public static final String IMAGE_TRASH16 = "trash16.png";
	/**
	 * log_notice16.png
	 */
	public static final String IMAGE_LOG_NOTICE16 = "log_notice16.png";
	/**
	 * log_error16.png
	 */
	public static final String IMAGE_LOG_ERROR16 = "log_error16.png";
	/**
	 * log_warning16.png
	 */
	public static final String IMAGE_LOG_WARNING16 = "log_warning16.png";
	/**
	 * log_info16.png
	 */
	public static final String IMAGE_LOG_INFO16 = "log_info16.png";
	/**
	 * log_debug16.png
	 */
	public static final String IMAGE_LOG_DEBUG16 = "log_debug16.png";
	/**
	 * log_trace16.png
	 */
	public static final String IMAGE_LOG_TRACE16 = "log_trace16.png";
	/**
	 * transparent_background.png
	 */
	public static final String IMAGE_TRANSPARENT_BACKGROUND = "transparent_background.png";

	/**
	 * Gets the image resource {@linkplain URL}.
	 *
	 * @param image the image to access.
	 * @return the image resource {@linkplain URL}.
	 */
	public static URL get(String image) {
		return Objects.requireNonNull(Images.class.getResource(image));
	}

}
