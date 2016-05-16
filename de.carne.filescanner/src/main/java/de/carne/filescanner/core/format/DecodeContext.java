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
import java.util.HashMap;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.util.logging.Log;

/**
 * This class keeps track of all context specific information need by an
 * {@linkplain Decodable}.
 */
public class DecodeContext {

	private static final Log LOG = new Log(DecodeContext.class);

	private static final ThreadLocal<DecodeContext> CONTEXT = new ThreadLocal<>();

	private final HashMap<DataAttribute<?>, Object> context = new HashMap<>();

	private final DecodeContext parent;

	private DecodeContext(DecodeContext parent) {
		this.parent = parent;
	}

	/**
	 * Set context attribute.
	 *
	 * @param attribute The attribute to set.
	 * @param value The attribute value to set.
	 */
	public <T> void setAttribute(DataAttribute<T> attribute, T value) {
		DecodeContext currentContext = this;

		while (currentContext != null && !currentContext.context.containsKey(attribute)) {
			currentContext = currentContext.parent;
		}
		if (currentContext == null) {
			LOG.debug(null, "New context attribute ''{0}'' = {1}", attribute.name(), value);
			currentContext = this;
		}
		currentContext.context.put(attribute, value);
	}

	/**
	 * Get context attribute.
	 *
	 * @param attribute The attribute to get.
	 * @return The set attribute value or {@code null} if none has been set.
	 */
	public <T> T getAttribute(DataAttribute<T> attribute) {
		DecodeContext currentContext = this;
		Object value = null;

		while (value == null && currentContext != null) {
			value = this.context.get(attribute);
			currentContext = currentContext.parent;
		}
		return attribute.getValueType().cast(value);
	}

	/**
	 * Setup a new {@code DecodeContext} and start decoding.
	 *
	 * @param decodable The {@linkplain Decodable} to use for decoding.
	 * @param result The result object to decode.
	 * @param position The position to start decoding at.
	 * @return The number of decoded bytes.
	 * @throws IOException if an I/O error occurs.
	 * @see Decodable#decode(FileScannerResult, long)
	 */
	public static long setupContextAndDecode(Decodable decodable, FileScannerResult result, long position)
			throws IOException {
		DecodeContext parentContext = CONTEXT.get();
		DecodeContext context = new DecodeContext(parentContext);
		long decoded;

		try {
			CONTEXT.set(context);
			decoded = decodable.decode(result, position);
		} finally {
			if (parentContext != null) {
				CONTEXT.set(parentContext);
			} else {
				CONTEXT.remove();
			}
		}
		return decoded;
	}

	/**
	 * Get the current {@code DecodeContext}.
	 *
	 * @return The current {@code DecodeContext}.
	 */
	public static DecodeContext get() {
		DecodeContext context = CONTEXT.get();

		if (context == null) {
			throw new IllegalStateException("No decode context set");
		}
		return context;
	}

}
