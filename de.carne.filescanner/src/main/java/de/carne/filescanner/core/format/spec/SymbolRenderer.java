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
import java.util.HashMap;

import de.carne.filescanner.spi.FileScannerResultRenderer;

/**
 * Map based attribute renderer (mapping values to symbols).
 *
 * @param <T> The attribute' data type.
 */
public class SymbolRenderer<T> extends AttributeRenderer<T> {

	private final HashMap<T, String> symbolMap = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.spec.AttributeRenderer#render(java.lang.
	 * Object, de.carne.filescanner.spi.FileScannerResultRenderer)
	 */
	@Override
	public void render(T value, FileScannerResultRenderer renderer) throws IOException, InterruptedException {
		String symbol = this.symbolMap.get(value);

		if (symbol != null) {
			renderer.setCommentMode().renderText(" // ").renderText(symbol);
		}
	}

	/**
	 * Add a symbol.
	 *
	 * @param value The value to map.
	 * @param symbol The symbol to map the value to.
	 * @return The updated renderer.
	 */
	public SymbolRenderer<T> addSymbol(T value, String symbol) {
		assert value != null;

		this.symbolMap.put(value, symbol);
		return this;
	}

}
