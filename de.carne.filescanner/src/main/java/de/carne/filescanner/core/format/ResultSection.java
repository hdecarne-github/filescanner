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

/**
 * This class defines render information for a specific data section within a
 * scanner result.
 *
 * @see DecodeContext#recordResultSection(long, RenderableData)
 * @see RenderContext#getResultSection(int)
 */
public final class ResultSection {

	private final long size;

	private final RenderableData renderable;

	ResultSection(long size, RenderableData renderable) {
		this.size = size;
		this.renderable = renderable;
	}

	/**
	 * Get the result section's size.
	 *
	 * @return The result section's size.
	 */
	public long size() {
		return this.size;
	}

	/**
	 * Get the result section's {@linkplain RenderableData}.
	 *
	 * @return The result section's {@linkplain RenderableData}.
	 */
	public RenderableData renderable() {
		return this.renderable;
	}

}
