/*
 * I18N resource strings
 *
 * Generated on 14.05.2016 00:23:52
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
	 * Resource key {@code STR_BYTE_UNIT12}
	 * <p>
	 * {0} TB
	 * </p>
	 */
	public static final String STR_BYTE_UNIT12 = "STR_BYTE_UNIT12";

	/**
	 * Resource string {@code STR_BYTE_UNIT12}
	 * <p>
	 * {0} TB
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_BYTE_UNIT12(Object... arguments) {
		return format(STR_BYTE_UNIT12, arguments);
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
	 * Resource key {@code STR_ABOUT_INFO2}
	 * <p>
	 * This program makes use of the Farm-Fresh icon set (http:&frasl;&frasl;www.fatcow.com&frasl;free-icons) .<br/>© Copyright 2009-2014 FatCow Web Hosting. All rights reserved.<br/>http:&frasl;&frasl;www.fatcow.com<br/><br/>These icons are licensed under a Creative Commons Attribution 3.0 License.<br/>http:&frasl;&frasl;creativecommons.org&frasl;licenses&frasl;by&frasl;3.0&frasl;us&frasl;.
	 * </p>
	 */
	public static final String STR_ABOUT_INFO2 = "STR_ABOUT_INFO2";

	/**
	 * Resource string {@code STR_ABOUT_INFO2}
	 * <p>
	 * This program makes use of the Farm-Fresh icon set (http:&frasl;&frasl;www.fatcow.com&frasl;free-icons) .<br/>© Copyright 2009-2014 FatCow Web Hosting. All rights reserved.<br/>http:&frasl;&frasl;www.fatcow.com<br/><br/>These icons are licensed under a Creative Commons Attribution 3.0 License.<br/>http:&frasl;&frasl;creativecommons.org&frasl;licenses&frasl;by&frasl;3.0&frasl;us&frasl;.
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

	/**
	 * Resource key {@code STR_STATUS_PROGRESS}
	 * <p>
	 * Scan in progress: {0} scanned ({1}&frasl;s)
	 * </p>
	 */
	public static final String STR_STATUS_PROGRESS = "STR_STATUS_PROGRESS";

	/**
	 * Resource string {@code STR_STATUS_PROGRESS}
	 * <p>
	 * Scan in progress: {0} scanned ({1}&frasl;s)
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_STATUS_PROGRESS(Object... arguments) {
		return format(STR_STATUS_PROGRESS, arguments);
	}

	/**
	 * Resource key {@code STR_STATUS_FINISHED}
	 * <p>
	 * Scan finished: {0} scanned ({1}&frasl;s)
	 * </p>
	 */
	public static final String STR_STATUS_FINISHED = "STR_STATUS_FINISHED";

	/**
	 * Resource string {@code STR_STATUS_FINISHED}
	 * <p>
	 * Scan finished: {0} scanned ({1}&frasl;s)
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_STATUS_FINISHED(Object... arguments) {
		return format(STR_STATUS_FINISHED, arguments);
	}

	/**
	 * Resource key {@code STR_STATUS_NONE}
	 * <p>
	 * 
	 * </p>
	 */
	public static final String STR_STATUS_NONE = "STR_STATUS_NONE";

	/**
	 * Resource string {@code STR_STATUS_NONE}
	 * <p>
	 * 
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_STATUS_NONE(Object... arguments) {
		return format(STR_STATUS_NONE, arguments);
	}

	/**
	 * Resource key {@code STR_BYTE_UNIT24}
	 * <p>
	 * {0} YB
	 * </p>
	 */
	public static final String STR_BYTE_UNIT24 = "STR_BYTE_UNIT24";

	/**
	 * Resource string {@code STR_BYTE_UNIT24}
	 * <p>
	 * {0} YB
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_BYTE_UNIT24(Object... arguments) {
		return format(STR_BYTE_UNIT24, arguments);
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
	 * Resource key {@code STR_BYTE_UNIT9}
	 * <p>
	 * {0} GB
	 * </p>
	 */
	public static final String STR_BYTE_UNIT9 = "STR_BYTE_UNIT9";

	/**
	 * Resource string {@code STR_BYTE_UNIT9}
	 * <p>
	 * {0} GB
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_BYTE_UNIT9(Object... arguments) {
		return format(STR_BYTE_UNIT9, arguments);
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
	 * Resource key {@code STR_BYTE_UNIT21}
	 * <p>
	 * {0} ZB
	 * </p>
	 */
	public static final String STR_BYTE_UNIT21 = "STR_BYTE_UNIT21";

	/**
	 * Resource string {@code STR_BYTE_UNIT21}
	 * <p>
	 * {0} ZB
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_BYTE_UNIT21(Object... arguments) {
		return format(STR_BYTE_UNIT21, arguments);
	}

	/**
	 * Resource key {@code STR_BYTE_UNIT6}
	 * <p>
	 * {0} MB
	 * </p>
	 */
	public static final String STR_BYTE_UNIT6 = "STR_BYTE_UNIT6";

	/**
	 * Resource string {@code STR_BYTE_UNIT6}
	 * <p>
	 * {0} MB
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_BYTE_UNIT6(Object... arguments) {
		return format(STR_BYTE_UNIT6, arguments);
	}

	/**
	 * Resource key {@code STR_BYTE_UNIT3}
	 * <p>
	 * {0} kB
	 * </p>
	 */
	public static final String STR_BYTE_UNIT3 = "STR_BYTE_UNIT3";

	/**
	 * Resource string {@code STR_BYTE_UNIT3}
	 * <p>
	 * {0} kB
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_BYTE_UNIT3(Object... arguments) {
		return format(STR_BYTE_UNIT3, arguments);
	}

	/**
	 * Resource key {@code STR_STATUS_CANCELLED}
	 * <p>
	 * Scan cancelled: {0} scanned ({1}&frasl;s)
	 * </p>
	 */
	public static final String STR_STATUS_CANCELLED = "STR_STATUS_CANCELLED";

	/**
	 * Resource string {@code STR_STATUS_CANCELLED}
	 * <p>
	 * Scan cancelled: {0} scanned ({1}&frasl;s)
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_STATUS_CANCELLED(Object... arguments) {
		return format(STR_STATUS_CANCELLED, arguments);
	}

	/**
	 * Resource key {@code STR_BYTE_UNIT0}
	 * <p>
	 * {0} Byte
	 * </p>
	 */
	public static final String STR_BYTE_UNIT0 = "STR_BYTE_UNIT0";

	/**
	 * Resource string {@code STR_BYTE_UNIT0}
	 * <p>
	 * {0} Byte
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_BYTE_UNIT0(Object... arguments) {
		return format(STR_BYTE_UNIT0, arguments);
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
	 * Resource key {@code STR_BYTE_UNIT18}
	 * <p>
	 * {0} EB
	 * </p>
	 */
	public static final String STR_BYTE_UNIT18 = "STR_BYTE_UNIT18";

	/**
	 * Resource string {@code STR_BYTE_UNIT18}
	 * <p>
	 * {0} EB
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_BYTE_UNIT18(Object... arguments) {
		return format(STR_BYTE_UNIT18, arguments);
	}

	/**
	 * Resource key {@code STR_STATUS_START}
	 * <p>
	 * Scan started: {0} scanned ({1}&frasl;s)
	 * </p>
	 */
	public static final String STR_STATUS_START = "STR_STATUS_START";

	/**
	 * Resource string {@code STR_STATUS_START}
	 * <p>
	 * Scan started: {0} scanned ({1}&frasl;s)
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_STATUS_START(Object... arguments) {
		return format(STR_STATUS_START, arguments);
	}

	/**
	 * Resource key {@code STR_BYTE_UNIT15}
	 * <p>
	 * {0} PB
	 * </p>
	 */
	public static final String STR_BYTE_UNIT15 = "STR_BYTE_UNIT15";

	/**
	 * Resource string {@code STR_BYTE_UNIT15}
	 * <p>
	 * {0} PB
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_BYTE_UNIT15(Object... arguments) {
		return format(STR_BYTE_UNIT15, arguments);
	}

}
