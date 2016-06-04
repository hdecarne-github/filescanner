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

import java.text.NumberFormat;

/**
 * Utility class for measuring the elapsed time.
 */
public final class Nanos {

	private final long startNanos;

	/**
	 * Construct {@code Nanos}.
	 */
	public Nanos() {
		this.startNanos = System.nanoTime();
	}

	/**
	 * Get the elapsed nanoseconds since object creation.
	 *
	 * @return The elapsed nanoseconds since object creation.
	 */
	public long elapsed() {
		return System.nanoTime() - this.startNanos;
	}

	/**
	 * Get the elapsed milliseconds since object creation.
	 *
	 * @return The elapsed milliseconds since object creation.
	 */
	public long elapsedMillis() {
		return (System.nanoTime() - this.startNanos) / 1000L;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(elapsed());
	}

	/**
	 * Format nanoseconds value.
	 *
	 * @param nanos the value to format.
	 * @return The formatted value.
	 */
	public static String toString(long nanos) {
		return NumberFormat.getNumberInstance().format(nanos) + " ns";
	}

	/**
	 * Format the elapsed milliseconds.
	 *
	 * @return The formatted milliseconds.
	 */
	public String toMillisString() {
		return toMillisString(elapsedMillis());
	}

	/**
	 * Format milliseconds value.
	 *
	 * @param millis the value to format.
	 * @return The formatted value.
	 */
	public static String toMillisString(long millis) {
		return NumberFormat.getNumberInstance().format(millis) + " ms";
	}

}
