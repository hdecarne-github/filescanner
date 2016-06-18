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

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.format.spec.Attribute;
import de.carne.filescanner.core.transfer.ResultRenderer;

/**
 * This class provides access to the context information recorded during
 * decoding.
 *
 * @see DecodeContext
 */
public abstract class RenderContext extends ResultContext {

	private static final ThreadLocal<RenderContext> RENDER_CONTEXT = new ThreadLocal<>();

	/**
	 * Get a context attribute value.
	 *
	 * @param attribute The attribute to get.
	 * @return The set attribute value or {@code null} if none has been set.
	 */
	public final <T> T getAttribute(Attribute<T> attribute) {
		return contextGetAttribute(attribute);
	}

	/**
	 * Get a previously recorded result section.
	 *
	 * @param renderable The {@linkplain RenderableData} to retrieve the result
	 *        section for.
	 * @return The result section object or {@code null} if the submitted index
	 *         has not been recorded.
	 * @see DecodeContext#recordResultSection(RenderableData, long, long)
	 */
	public final ResultSection getResultSection(RenderableData renderable) {
		return contextGetResultSection(renderable);
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
	 * @see Decodable#render(FileScannerResult, ResultRenderer)
	 */
	public static void setupAndRender(Renderable renderable, FileScannerResult result, ResultRenderer renderer)
			throws IOException, InterruptedException {
		assert renderable != null;
		assert result != null;
		assert renderer != null;

		RenderContext parentContext = RENDER_CONTEXT.get();

		try {
			RENDER_CONTEXT.set(result.renderContext());
			renderable.render(result, renderer);
		} finally {
			if (parentContext != null) {
				RENDER_CONTEXT.set(parentContext);
			} else {
				RENDER_CONTEXT.remove();
			}
		}
	}

	/**
	 * Get the currently active {@code RenderContext}.
	 *
	 * @return The currently active {@code RenderContext}.
	 */
	public static RenderContext getRenderContext() {
		return DecodeContext.getDecodeOrRenderContext();
	}

	static RenderContext getDecodeOrRenderContext() {
		RenderContext context = RENDER_CONTEXT.get();

		if (context == null) {
			throw new IllegalStateException("No render context set");
		}
		return context;
	}

}
