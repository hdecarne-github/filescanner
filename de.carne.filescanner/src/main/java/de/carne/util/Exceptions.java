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
 * Utility class providing {@code Exception} related functions.
 */
public final class Exceptions {

	/**
	 * Get the exception message.
	 * <p>
	 * This function implements a best effort approach to get the exception
	 * message.
	 * </p>
	 *
	 * @param e The exception to get the message from (maybe {@code null}).
	 * @return The exception message or {@code null} if the submitted exception
	 *         is {@code null}.
	 */
	public static String toMessage(Throwable e) {
		String message;

		if (e == null) {
			message = null;
		} else if ((message = e.getLocalizedMessage()) != null) {
			// Nothing to do here
		} else if ((message = e.getMessage()) != null) {
			// Nothing to do here
		} else {
			message = e.getClass().getName();
		}
		return message;
	}

}
