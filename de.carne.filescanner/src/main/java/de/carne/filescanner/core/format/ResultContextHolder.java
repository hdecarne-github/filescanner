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
package de.carne.filescanner.core.format;

import java.io.IOException;

import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.ResultContext;

/**
 * This class make the current result context available during the decode and
 * render phase.
 */
public class ResultContextHolder {

	private static final ThreadLocal<ResultContext> RESULT_CONTEXT = new ThreadLocal<>();

	/**
	 * Setup the context and start decoding.
	 *
	 * @param decodable The {@linkplain Decodable} to use for decoding.
	 * @param result The result builder object to decode into and for which to
	 *        update the context.
	 * @return The number of decoded bytes.
	 * @throws IOException if an I/O error occurs.
	 * @see Decodable#decode(FileScannerResultBuilder)
	 */
	public static long setupAndDecode(Decodable decodable, FileScannerResultBuilder result) throws IOException {
		assert decodable != null;
		assert result != null;

		ResultContext parentContext = RESULT_CONTEXT.get();
		long decoded;

		try {
			RESULT_CONTEXT.set(result.context());
			decoded = decodable.decode(result);
			result.updateEnd(result.start() + decoded);
		} finally {
			if (parentContext != null) {
				RESULT_CONTEXT.set(parentContext);
			} else {
				RESULT_CONTEXT.remove();
			}
		}
		return decoded;
	}

	/**
	 * Get the current {@code ResultContext}.
	 *
	 * @return The current {@code ResultContext}.
	 */
	public static ResultContext get() {
		ResultContext context = RESULT_CONTEXT.get();

		if (context == null) {
			throw new IllegalStateException("No decode context set");
		}
		return context;
	}

}
