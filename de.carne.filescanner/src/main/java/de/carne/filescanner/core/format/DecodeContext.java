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

/**
 * This class is used to keeps track of all context specific information
 * recorded during decoding.
 */
public abstract class DecodeContext extends RenderContext {

	private static final ThreadLocal<DecodeContext> DECODE_CONTEXT = new ThreadLocal<>();

	/**
	 * Declare a context attribute.
	 *
	 * @param attribute The attribute to declare.
	 */
	public final <T> void declareAttribute(ResultAttribute<T> attribute) {
		contextDeclareAttribute(attribute);
	}

	/**
	 * Set a context attribute value.
	 *
	 * @param attribute The attribute to set.
	 * @param value The attribute value to set.
	 */
	public final <T> void setAttribute(ResultAttribute<T> attribute, T value) {
		contextSetAttribute(attribute, value);
	}

	/**
	 * Record a result section for later rendering.
	 *
	 * @param renderable The {@linkplain RenderableData} to use for rendering.
	 * @param start The result section's start position.
	 * @param end The result section's end position.
	 */
	public final void recordResultSection(RenderableData renderable, long start, long end) {
		contextRecordResultSection(renderable, start, end);
	}

	/**
	 * Activate a context and start decode a result.
	 *
	 * @param decodable The {@linkplain Decodable} to use for decoding.
	 * @param result The result builder to decode into and which's context
	 *        should be activate.
	 * @return The number of decoded bytes.
	 * @throws IOException if an I/O error occurs.
	 * @see Decodable#decode(FileScannerResultBuilder)
	 */
	public static long setupAndDecode(Decodable decodable, FileScannerResultBuilder result) throws IOException {
		assert decodable != null;
		assert result != null;

		DecodeContext parentContext = DECODE_CONTEXT.get();
		long decoded;

		try {
			DECODE_CONTEXT.set(result.decodeContext());
			decoded = decodable.decode(result);
			result.updateEnd(result.start() + decoded);
		} finally {
			if (parentContext != null) {
				DECODE_CONTEXT.set(parentContext);
			} else {
				DECODE_CONTEXT.remove();
			}
		}
		return decoded;
	}

	/**
	 * Get the currently active {@code DecodeContext}.
	 *
	 * @return The currently active {@code DecodeContext}.
	 */
	public static DecodeContext getDecodeContext() {
		DecodeContext context = DECODE_CONTEXT.get();

		if (context == null) {
			throw new IllegalStateException("No decode context set");
		}
		return context;
	}

	static RenderContext getDecodeOrRenderContext() {
		RenderContext context = DECODE_CONTEXT.get();

		if (context == null) {
			context = RenderContext.getDecodeOrRenderContext();
		}
		return context;
	}

}
