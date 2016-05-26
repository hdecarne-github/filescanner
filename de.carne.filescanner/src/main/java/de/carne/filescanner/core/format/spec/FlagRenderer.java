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
 * Base class for flag-set rendering.
 *
 * @param <T> The attribute's value type.
 */
public abstract class FlagRenderer<T extends Number> extends AttributeRenderer<T> implements Iterable<T> {

	private final HashMap<T, String> symbolMap = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.spec.AttributeRenderer#render(java.lang.
	 * Object, de.carne.filescanner.spi.FileScannerResultRenderer)
	 */
	@Override
	public void render(T value, FileScannerResultRenderer renderer) throws IOException, InterruptedException {
		renderer.renderBreak();
		for (T flag : this) {
			String symbol = this.symbolMap.get(flag);
			boolean flagEnabled = testFlag(flag, value);

			if (symbol != null) {
				renderer.setValueMode().renderText(formatFlag(flag, value));
				renderer.setCommentMode().renderText(" // ").renderText(symbol);
				renderer.renderBreak();
			} else if (flagEnabled) {
				renderer.setValueMode().renderText(formatFlag(flag, value));
				renderer.renderBreak();
			}
		}
	}

	/**
	 * Format a single flag.
	 *
	 * @param flag The flag to format.
	 * @param value The current flag-set value to format.
	 * @return The formatted flag.
	 */
	protected abstract String formatFlag(T flag, T value);

	/**
	 * Test whether a single flag is set or not.
	 *
	 * @param flag The flag to test.
	 * @param value The current flag-set value to check.
	 * @return {@code true} if the flag is set.
	 */
	protected abstract boolean testFlag(T flag, T value);

	/**
	 * Add a flag symbol.
	 * 
	 * @param flag The flag to add the symbol for.
	 * @param symbol The symbol to add.
	 * @return The updated renderer.
	 */
	public FlagRenderer<T> addFlagSymbol(T flag, String symbol) {
		assert flag != null;
		assert symbol != null;

		this.symbolMap.put(flag, symbol);
		return this;
	}

}
