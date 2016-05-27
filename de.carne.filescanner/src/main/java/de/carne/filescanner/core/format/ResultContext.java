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
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.format.spec.Attribute;
import de.carne.filescanner.spi.FileScannerResultRenderer;
import de.carne.util.logging.Log;

/**
 * This class keeps track of all context specific information of a scanner
 * result.
 */
public abstract class ResultContext {

	private static final Log LOG = new Log(ResultContext.class);

	private static final ThreadLocal<ResultContext> RESULT_CONTEXT = new ThreadLocal<>();

	private final HashMap<Attribute<?>, Object> contextAttributes = new HashMap<>();

	/**
	 * Get the parent context.
	 *
	 * @return The parent context or {@code null} if there is none.
	 */
	protected abstract ResultContext parent();

	/**
	 * Add all result scoped attributes from another context.
	 *
	 * @param context The context containing the result scoped attributes to
	 *        add.
	 */
	public void addResultAttributes(ResultContext context) {
		this.contextAttributes.putAll(context.contextAttributes);
	}

	/**
	 * Declare a context attribute.
	 *
	 * @param attribute The attribute to declare.
	 */
	public <T> void declareAttribute(Attribute<T> attribute) {
		assert attribute != null;

		this.contextAttributes.put(attribute, null);
	}

	/**
	 * Set a context attribute value.
	 *
	 * @param attribute The attribute to set.
	 * @param value The attribute value to set.
	 */
	public <T> void setAttribute(Attribute<T> attribute, T value) {
		assert attribute != null;

		ResultContext currentContext = this;

		while (currentContext != null && !currentContext.contextAttributes.containsKey(attribute)) {
			currentContext = currentContext.parent();
		}
		if (currentContext == null) {
			LOG.debug(null, "Declare context attribute ''{0}'' = {1}", attribute.name(), value);
			currentContext = this;
		} else {
			LOG.debug(null, "Set context attribute ''{0}'' = {1}", attribute.name(), value);
		}
		currentContext.contextAttributes.put(attribute, value);
	}

	/**
	 * Get a context attribute value.
	 *
	 * @param attribute The attribute to get.
	 * @return The set attribute value or {@code null} if none has been set.
	 */
	public <T> T getAttribute(Attribute<T> attribute) {
		assert attribute != null;

		ResultContext currentContext = this;
		Object value = null;

		while (value == null && currentContext != null) {
			value = currentContext.contextAttributes.get(attribute);
			currentContext = currentContext.parent();
		}
		return attribute.getValueType().cast(value);
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
	 * Activate a context and render a result.
	 *
	 * @param renderable The {@linkplain Renderable} to use for rendering.
	 * @param result The result to render and which's context should be
	 *        activate.
	 * @param renderer The renderer to use.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 * @see Decodable#render(FileScannerResult, FileScannerResultRenderer)
	 */
	public static void setupAndRender(Renderable renderable, FileScannerResult result,
			FileScannerResultRenderer renderer) throws IOException, InterruptedException {
		assert renderable != null;
		assert result != null;
		assert renderer != null;

		ResultContext parentContext = RESULT_CONTEXT.get();

		try {
			RESULT_CONTEXT.set(result.context());
			renderable.render(result, renderer);
		} finally {
			if (parentContext != null) {
				RESULT_CONTEXT.set(parentContext);
			} else {
				RESULT_CONTEXT.remove();
			}
		}
	}

	/**
	 * Get the currently active {@code ResultContext}.
	 *
	 * @return The currently active {@code ResultContext}.
	 */
	public static ResultContext get() {
		ResultContext context = RESULT_CONTEXT.get();

		if (context == null) {
			throw new IllegalStateException("No decode context set");
		}
		return context;
	}

}
