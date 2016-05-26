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

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class providing DOS related functions.
 */
public final class Dos {

	/**
	 * Convert DOS time value to a Java time value (in milliseconds).
	 *
	 * @param time The DOS time value to convert.
	 * @return The converted value.
	 */
	public static long dosTimeToMillis(short time) {
		int second = (time & 0x001f) * 2;
		int minute = (time >>> 5) & 0x001f;
		int hour = (time >>> 11) & 0x000f;
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		return calendar.getTimeInMillis();
	}

	/**
	 * Convert DOS time value to a Java {@linkplain Date} object.
	 *
	 * @param time The DOS time value to convert.
	 * @return The converted value.
	 */
	public static Date dosTimeToDate(short time) {
		return new Date(dosTimeToMillis(time));
	}

	/**
	 * Convert DOS date value to a Java date value (in milliseconds).
	 *
	 * @param date The DOS date value to convert.
	 * @return The converted value.
	 */
	public static long dosDateToMillis(short date) {
		int day = date & 0x001f;
		int month = ((date >>> 5) & 0x0007) - 1;
		int year = ((date >>> 9) & 0x007f) + 1980;
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		return calendar.getTimeInMillis();
	}

	/**
	 * Convert DOS date value to a Java date object.
	 *
	 * @param date The DOS date value to convert.
	 * @return The converted value.
	 */
	public static Date dosDateToDate(short date) {
		return new Date(dosDateToMillis(date));
	}

}
