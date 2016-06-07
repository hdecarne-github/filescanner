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

import de.carne.filescanner.core.transfer.FileScannerResultRenderer;

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
		T foldedFlag = null;

		for (T flag : this) {
			if (this.symbolMap.containsKey(flag)) {
				if (foldedFlag != null) {
					renderFlag(foldedFlag, value, renderer);
					foldedFlag = null;
				}
				renderFlag(flag, value, renderer);
			} else {
				foldedFlag = (foldedFlag != null ? foldFlag(foldedFlag, flag) : flag);
			}
		}
		if (foldedFlag != null) {
			renderFlag(foldedFlag, value, renderer);
			foldedFlag = null;
		}
	}

	private void renderFlag(T flag, T value, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException {
		String symbol = this.symbolMap.get(flag);

		if (symbol != null) {
			renderer.renderBreak();
			renderer.setValueMode().renderText(formatFlag(flag, value));
			renderer.setCommentMode().renderText(" // ").renderText(symbol);
		} else if (testFlag(flag, value)) {
			renderer.renderBreak();
			renderer.setValueMode().renderText(formatFlag(flag, value));
		}
	}

	/**
	 * Test whether a single flag is set or not.
	 *
	 * @param flag The flag to test.
	 * @param value The current flag-set value to check.
	 * @return {@code true} if the flag is set.
	 */
	protected abstract boolean testFlag(T flag, T value);

	/**
	 * Fold two flags into one.
	 * 
	 * @param flag1 The 1st flag to fold.
	 * @param flag2 The 2nd flag to fold.
	 * @return The folded flag.
	 */
	protected abstract T foldFlag(T flag1, T flag2);

	/**
	 * Format a single flag.
	 *
	 * @param flag The flag to format.
	 * @param value The current flag-set value to format.
	 * @return The formatted flag.
	 */
	protected abstract String formatFlag(T flag, T value);

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
