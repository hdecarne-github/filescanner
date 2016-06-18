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
import de.carne.filescanner.core.transfer.ResultRenderer;

/**
 * This class defines render information for a specific data section within a
 * scanner result.
 *
 * @see DecodeContext#recordResultSection(RenderableData, long, long)
 * @see RenderContext#getResultSection(RenderableData)
 */
public final class ResultSection {

	private final RenderableData renderable;

	private final long start;

	private final long end;

	ResultSection(RenderableData renderable, long start, long end) {
		this.renderable = renderable;
		this.start = start;
		this.end = end;
	}

	/**
	 * Get the result section's {@linkplain RenderableData}.
	 *
	 * @return The result section's {@linkplain RenderableData}.
	 */
	public RenderableData renderable() {
		return this.renderable;
	}

	/**
	 * Get the result section's start position.
	 *
	 * @return The result section's start position.
	 */
	public long start() {
		return this.start;
	}

	/**
	 * Get the result section's end position.
	 *
	 * @return The result section's end position.
	 */
	public long end() {
		return this.end;
	}

	/**
	 * Render the result section.
	 *
	 * @param result The result object containing the data to render.
	 * @param renderer The renderer to use.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public void render(FileScannerResult result, ResultRenderer renderer) throws IOException, InterruptedException {
		this.renderable.renderData(result, this.start, this.end, renderer);
	}

}
