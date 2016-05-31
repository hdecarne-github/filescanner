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
package de.carne.filescanner.core;

import java.io.IOException;

/**
 * Exception class used for recording the decoding status of a scanner result
 * object in case of failures or unexpected data.
 * <p>
 * The fatal attribute indicates whether decoding can continue ({@code false})
 * or not ({@code true}). The nested attribute indicates whether the decode
 * status was set by the current result ({@code false}) or by one of it's
 * children ({@code true}).
 * </p>
 */
public class DecodeStatusException extends IOException {

	private static final long serialVersionUID = 1L;

	private final boolean fatal;

	private final boolean nested;

	private DecodeStatusException(boolean fatal, boolean nested, String message, Throwable cause) {
		super((message != null ? message : (cause != null ? cause.getMessage() : "Unknown")), cause);
		this.fatal = fatal;
		this.nested = nested;
	}

	/**
	 * Check whether decoding can continue ({@code false}) or not ({@code true}
	 * ).
	 *
	 * @return {@code false} if the status is non-fatal and decoding can
	 *         continue.
	 */
	public boolean isFatal() {
		return this.fatal;
	}

	/**
	 * Check whether this status was set the be current result ({@code false})
	 * or one of it's children.
	 *
	 * @return {@code false} if the status was set by the current result.
	 */
	public boolean isNested() {
		return this.nested;
	}

	/**
	 * Construct a fatal decode status.
	 *
	 * @param message The status message.
	 * @return The constructed decode status.
	 */
	public static DecodeStatusException fatal(String message) {
		assert message != null;

		return new DecodeStatusException(true, false, message, null);
	}

	/**
	 * Construct a fatal decode status.
	 *
	 * @param cause The status cause.
	 * @return The constructed decode status.
	 */
	public static DecodeStatusException fatal(Exception cause) {
		assert cause != null;

		DecodeStatusException decodeStatus;

		if (cause instanceof DecodeStatusException) {
			DecodeStatusException decodeStatusCause = (DecodeStatusException) cause;

			if (decodeStatusCause.fatal && decodeStatusCause.nested) {
				decodeStatus = decodeStatusCause;
			} else {
				decodeStatus = new DecodeStatusException(true, true, null, cause.getCause());
			}
		} else {
			decodeStatus = new DecodeStatusException(true, false, null, cause);
		}
		return decodeStatus;
	}

	/**
	 * Construct a non-fatal decode status.
	 *
	 * @param message The status message.
	 * @return The constructed decode status.
	 */
	public static DecodeStatusException status(String message) {
		assert message != null;

		return new DecodeStatusException(false, false, message, null);
	}

	/**
	 * Construct a non-fatal decode status.
	 *
	 * @param cause The status cause.
	 * @return The constructed decode status.
	 */
	public static DecodeStatusException status(Exception cause) {
		assert cause != null;

		DecodeStatusException decodeStatus = null;

		if (cause instanceof DecodeStatusException) {
			DecodeStatusException decodeStatusCause = (DecodeStatusException) cause;

			if (!decodeStatusCause.fatal && decodeStatusCause.nested) {
				decodeStatus = decodeStatusCause;
			} else {
				decodeStatus = new DecodeStatusException(false, true, null, cause.getCause());
			}
		} else {
			decodeStatus = new DecodeStatusException(false, false, null, cause);
		}
		return decodeStatus;
	}

	/**
	 * Conditionally construct a decode status exception.
	 * 
	 * @param cause The exception status. If {@code null} no decode status
	 *        exception will be created.
	 * @param fatal Whether the status is fatal or non-fatal.
	 * @return The constructed decode status exception or {@code null} if the
	 *         submitted exception status is {@code null}.
	 */
	public static DecodeStatusException fromException(Exception cause, boolean fatal) {
		return (cause != null ? (fatal ? fatal(cause) : status(cause)) : null);
	}

}
