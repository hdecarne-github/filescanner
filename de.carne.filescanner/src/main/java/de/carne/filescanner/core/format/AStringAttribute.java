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
import java.nio.charset.Charset;
import java.util.function.Supplier;

import de.carne.filescanner.core.FileScannerResultBuilder;

/**
 *
 */
public class AStringAttribute extends StringAttribute {

	private final ValueExpression<Short> sizeExpression;

	private AStringAttribute(String name, Charset charset, ValueExpression<Short> sizeExpression) {
		super(name, charset);

		assert sizeExpression != null;

		this.sizeExpression = sizeExpression;
	}

	public AStringAttribute(String name, Charset charset, Short staticSize) {
		this(name, charset, new ValueExpression<>(staticSize, Short.valueOf((short) 0)));
	}

	public AStringAttribute(String name, Charset charset, Supplier<Short> dynamicSize) {
		this(name, charset, new ValueExpression<>(dynamicSize, Short.valueOf((short) 0)));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.FormatSpec#matchSize()
	 */
	@Override
	public int matchSize() {
		return this.sizeExpression.beforeEval();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.FormatSpec#eval(de.carne.filescanner.
	 * core.FileScannerResultBuilder, long)
	 */
	@Override
	public long eval(FileScannerResultBuilder result, long position) throws IOException {
		return this.sizeExpression.afterEval();
	}

}
