/*
 * I18N resource strings
 *
 * Generated on 05.06.2016 17:58:16
 */
package de.carne.filescanner.jfx.session;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
public final class I18N {

	/**
	 * The BUNDLE represented by this class.
	 */
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(I18N.class.getName());

	/**
	 * Format a resource string.
	 * @param key The resource key.
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String format(String key, Object... arguments) {
		String pattern = BUNDLE.getString(key);

		return (arguments.length > 0 ? MessageFormat.format(pattern, arguments) : pattern);
	}

	/**
	 * Resource key {@code STR_SCAN_STATUS_FINISHED}
	 * <p>
	 * Scan finished: {0} scanned ({1}&frasl;s)
	 * </p>
	 */
	public static final String STR_SCAN_STATUS_FINISHED = "STR_SCAN_STATUS_FINISHED";

	/**
	 * Resource string {@code STR_SCAN_STATUS_FINISHED}
	 * <p>
	 * Scan finished: {0} scanned ({1}&frasl;s)
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SCAN_STATUS_FINISHED(Object... arguments) {
		return format(STR_SCAN_STATUS_FINISHED, arguments);
	}

	/**
	 * Resource key {@code STR_SCAN_STATUS_CANCELLED}
	 * <p>
	 * Scan cancelled: {0} scanned ({1}&frasl;s)
	 * </p>
	 */
	public static final String STR_SCAN_STATUS_CANCELLED = "STR_SCAN_STATUS_CANCELLED";

	/**
	 * Resource string {@code STR_SCAN_STATUS_CANCELLED}
	 * <p>
	 * Scan cancelled: {0} scanned ({1}&frasl;s)
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SCAN_STATUS_CANCELLED(Object... arguments) {
		return format(STR_SCAN_STATUS_CANCELLED, arguments);
	}

	/**
	 * Resource key {@code STR_CLOSE_SESSION_ERROR}
	 * <p>
	 * An error occurred while closing the current scan session.
	 * </p>
	 */
	public static final String STR_CLOSE_SESSION_ERROR = "STR_CLOSE_SESSION_ERROR";

	/**
	 * Resource string {@code STR_CLOSE_SESSION_ERROR}
	 * <p>
	 * An error occurred while closing the current scan session.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CLOSE_SESSION_ERROR(Object... arguments) {
		return format(STR_CLOSE_SESSION_ERROR, arguments);
	}

	/**
	 * Resource key {@code STR_SCAN_STATUS_PROGRESS}
	 * <p>
	 * Scan in progress: {0} scanned ({1}&frasl;s)
	 * </p>
	 */
	public static final String STR_SCAN_STATUS_PROGRESS = "STR_SCAN_STATUS_PROGRESS";

	/**
	 * Resource string {@code STR_SCAN_STATUS_PROGRESS}
	 * <p>
	 * Scan in progress: {0} scanned ({1}&frasl;s)
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SCAN_STATUS_PROGRESS(Object... arguments) {
		return format(STR_SCAN_STATUS_PROGRESS, arguments);
	}

	/**
	 * Resource key {@code STR_OPEN_RENDERER_ERROR}
	 * <p>
	 * An error occurred while starting the result rendering.
	 * </p>
	 */
	public static final String STR_OPEN_RENDERER_ERROR = "STR_OPEN_RENDERER_ERROR";

	/**
	 * Resource string {@code STR_OPEN_RENDERER_ERROR}
	 * <p>
	 * An error occurred while starting the result rendering.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_OPEN_RENDERER_ERROR(Object... arguments) {
		return format(STR_OPEN_RENDERER_ERROR, arguments);
	}

	/**
	 * Resource key {@code STR_SYSTEM_STATUS}
	 * <p>
	 * Memory usage: {0} ({1}%}
	 * </p>
	 */
	public static final String STR_SYSTEM_STATUS = "STR_SYSTEM_STATUS";

	/**
	 * Resource string {@code STR_SYSTEM_STATUS}
	 * <p>
	 * Memory usage: {0} ({1}%}
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SYSTEM_STATUS(Object... arguments) {
		return format(STR_SYSTEM_STATUS, arguments);
	}

	/**
	 * Resource key {@code STR_SCAN_STATUS_START}
	 * <p>
	 * Scan started: {0} scanned ({1}&frasl;s)
	 * </p>
	 */
	public static final String STR_SCAN_STATUS_START = "STR_SCAN_STATUS_START";

	/**
	 * Resource string {@code STR_SCAN_STATUS_START}
	 * <p>
	 * Scan started: {0} scanned ({1}&frasl;s)
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SCAN_STATUS_START(Object... arguments) {
		return format(STR_SCAN_STATUS_START, arguments);
	}

	/**
	 * Resource key {@code STR_OPEN_FILE_ERROR}
	 * <p>
	 * An error occurred while opening the the file:<br/>''{0}''
	 * </p>
	 */
	public static final String STR_OPEN_FILE_ERROR = "STR_OPEN_FILE_ERROR";

	/**
	 * Resource string {@code STR_OPEN_FILE_ERROR}
	 * <p>
	 * An error occurred while opening the the file:<br/>''{0}''
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_OPEN_FILE_ERROR(Object... arguments) {
		return format(STR_OPEN_FILE_ERROR, arguments);
	}

	/**
	 * Resource key {@code STR_SESSION_TITLE}
	 * <p>
	 * FileScanner
	 * </p>
	 */
	public static final String STR_SESSION_TITLE = "STR_SESSION_TITLE";

	/**
	 * Resource string {@code STR_SESSION_TITLE}
	 * <p>
	 * FileScanner
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SESSION_TITLE(Object... arguments) {
		return format(STR_SESSION_TITLE, arguments);
	}

	/**
	 * Resource key {@code STR_SCAN_STATUS_NONE}
	 * <p>
	 * 
	 * </p>
	 */
	public static final String STR_SCAN_STATUS_NONE = "STR_SCAN_STATUS_NONE";

	/**
	 * Resource string {@code STR_SCAN_STATUS_NONE}
	 * <p>
	 * 
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SCAN_STATUS_NONE(Object... arguments) {
		return format(STR_SCAN_STATUS_NONE, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUT_TITLE2}
	 * <p>
	 * Additional Copyrights
	 * </p>
	 */
	public static final String STR_ABOUT_TITLE2 = "STR_ABOUT_TITLE2";

	/**
	 * Resource string {@code STR_ABOUT_TITLE2}
	 * <p>
	 * Additional Copyrights
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUT_TITLE2(Object... arguments) {
		return format(STR_ABOUT_TITLE2, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUT_TITLE1}
	 * <p>
	 * Copyright FileScanner
	 * </p>
	 */
	public static final String STR_ABOUT_TITLE1 = "STR_ABOUT_TITLE1";

	/**
	 * Resource string {@code STR_ABOUT_TITLE1}
	 * <p>
	 * Copyright FileScanner
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUT_TITLE1(Object... arguments) {
		return format(STR_ABOUT_TITLE1, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUT_INFO2}
	 * <p>
	 * The Farm-Fresh icons (http:&frasl;&frasl;www.fatcow.com&frasl;free-icons) are<br/>© Copyright 2009-2014 FatCow Web Hosting. All rights reserved.<br/>http:&frasl;&frasl;www.fatcow.com<br/>These icons are licensed under a Creative Commons Attribution 3.0 License.<br/>http:&frasl;&frasl;creativecommons.org&frasl;licenses&frasl;by&frasl;3.0&frasl;us&frasl;.<br/><br/>The Eclipse icons are subject to the Eclipse Public License<br/>(http:&frasl;&frasl;www.eclipse.org&frasl;legal&frasl;epl-v10.html").<br/><br/>The application icon is © Copyright 2009-2016 MazeNL77<br/>(http:&frasl;&frasl;mazenl77.deviantart.com).<br/><br/>See the license file for further details.
	 * </p>
	 */
	public static final String STR_ABOUT_INFO2 = "STR_ABOUT_INFO2";

	/**
	 * Resource string {@code STR_ABOUT_INFO2}
	 * <p>
	 * The Farm-Fresh icons (http:&frasl;&frasl;www.fatcow.com&frasl;free-icons) are<br/>© Copyright 2009-2014 FatCow Web Hosting. All rights reserved.<br/>http:&frasl;&frasl;www.fatcow.com<br/>These icons are licensed under a Creative Commons Attribution 3.0 License.<br/>http:&frasl;&frasl;creativecommons.org&frasl;licenses&frasl;by&frasl;3.0&frasl;us&frasl;.<br/><br/>The Eclipse icons are subject to the Eclipse Public License<br/>(http:&frasl;&frasl;www.eclipse.org&frasl;legal&frasl;epl-v10.html").<br/><br/>The application icon is © Copyright 2009-2016 MazeNL77<br/>(http:&frasl;&frasl;mazenl77.deviantart.com).<br/><br/>See the license file for further details.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUT_INFO2(Object... arguments) {
		return format(STR_ABOUT_INFO2, arguments);
	}

	/**
	 * Resource key {@code STR_ABOUT_INFO1}
	 * <p>
	 * Copyright © 2007-2016 Holger de Carne and contributors,<br/>All Rights Reserved.<br/><br/>This program is free software: you can redistribute it and&frasl;or modify<br/>it under the terms of the GNU General Public License as published by<br/>the Free Software Foundation, either version 3 of the License, or<br/>(at your option) any later version.<br/><br/>This program is distributed in the hope that it will be useful,<br/>but WITHOUT ANY WARRANTY; without even the implied warranty of<br/>MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br/>GNU General Public License for more details.<br/><br/>You should have received a copy of the GNU General Public License<br/>along with this program.  If not, see http:&frasl;&frasl;www.gnu.org&frasl;licenses.
	 * </p>
	 */
	public static final String STR_ABOUT_INFO1 = "STR_ABOUT_INFO1";

	/**
	 * Resource string {@code STR_ABOUT_INFO1}
	 * <p>
	 * Copyright © 2007-2016 Holger de Carne and contributors,<br/>All Rights Reserved.<br/><br/>This program is free software: you can redistribute it and&frasl;or modify<br/>it under the terms of the GNU General Public License as published by<br/>the Free Software Foundation, either version 3 of the License, or<br/>(at your option) any later version.<br/><br/>This program is distributed in the hope that it will be useful,<br/>but WITHOUT ANY WARRANTY; without even the implied warranty of<br/>MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br/>GNU General Public License for more details.<br/><br/>You should have received a copy of the GNU General Public License<br/>along with this program.  If not, see http:&frasl;&frasl;www.gnu.org&frasl;licenses.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ABOUT_INFO1(Object... arguments) {
		return format(STR_ABOUT_INFO1, arguments);
	}

}
