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
import java.nio.charset.Charset;
import java.util.function.Supplier;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.spi.FileScannerResultRenderer;

/**
 * Defines an array-like string attribute.
 * <p>
 * The actual size of the string has be defined statically or via a Lambda
 * expression.
 * </p>
 */
public class AStringAttribute extends StringAttribute {

	private final NumberExpression<?> sizeExpression;

	private AStringAttribute(String name, Charset charset, NumberExpression<?> sizeExpression) {
		super(name, charset);

		assert sizeExpression != null;

		this.sizeExpression = sizeExpression;
	}

	/**
	 * Construct {@code AStringAttribute}.
	 *
	 * @param name The attribute's name.
	 * @param charset The charset to use for string decoding.
	 * @param staticSize The static string data size.
	 */
	public AStringAttribute(String name, Charset charset, Number staticSize) {
		this(name, charset, new NumberExpression<>(staticSize));
	}

	/**
	 * Construct {@code AStringAttribute}.
	 *
	 * @param name The attribute's name.
	 * @param charset The charset to use for string decoding.
	 * @param sizeLambda The expression providing the string data size.
	 */
	public AStringAttribute(String name, Charset charset, Supplier<? extends Number> sizeLambda) {
		this(name, charset, new NumberExpression<>(sizeLambda));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.FormatSpec#matchSize()
	 */
	@Override
	public int matchSize() {
		Number sizeValue = this.sizeExpression.beforeDecode();
		int matchSize = 0;

		if (sizeValue != null) {
			long sizeValueLong = sizeValue.longValue();

			if (sizeValueLong < MAX_MATCH_SIZE) {
				matchSize = sizeValue.intValue();
			}
		}
		return matchSize;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.FormatSpec#specDecode(de.carne.
	 * filescanner. core.FileScannerResultBuilder, long)
	 */
	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		long decoded = this.sizeExpression.afterDecode().longValue();

		if (isBound()) {
			bindValue(decodeString(result, position, decoded).toString());
		}
		return decoded;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.spec.FormatSpec#specRender(de.carne.
	 * filescanner.core.FileScannerResult, long,
	 * de.carne.filescanner.spi.FileScannerResultRenderer)
	 */
	@Override
	public long specRender(FileScannerResult result, long position, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException {
		long rendered = this.sizeExpression.afterDecode().longValue();

		renderer.setNormalMode().renderText(name());
		renderer.setOperatorMode().renderText(" = ");
		renderString(result, position, rendered, renderer);
		renderer.renderBreakOrClose(isResult());
		return rendered;
	}

}
