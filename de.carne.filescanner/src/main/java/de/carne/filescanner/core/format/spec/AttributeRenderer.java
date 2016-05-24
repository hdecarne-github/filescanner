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
package de.carne.filescanner.core.format.spec;

import java.io.IOException;

import de.carne.filescanner.spi.FileScannerResultRenderer;

/**
 * Base class for secondary renderer which can be added to an
 * {@linkplain Attribute} for additional render output.
 * 
 * @param <T> The attribute' data type.
 */
public abstract class AttributeRenderer<T> {

	/**
	 * Render the number attribute.
	 *
	 * @param value The value object to render.
	 * @param renderer The renderer to use.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public abstract void render(T value, FileScannerResultRenderer renderer) throws IOException, InterruptedException;

}
