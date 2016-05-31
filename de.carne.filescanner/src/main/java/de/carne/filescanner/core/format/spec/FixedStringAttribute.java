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

import de.carne.filescanner.core.DecodeStatusException;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.spi.FileScannerResultRenderer;

/**
 * Defines an fixed-length string attribute.
 * <p>
 * The actual length of the string has to be defined statically or via a Lambda
 * expression.
 * </p>
 */
public class FixedStringAttribute extends StringAttribute {

	private final NumberExpression<?> sizeExpression;

	private FixedStringAttribute(String name, Charset charset, NumberExpression<?> sizeExpression) {
		super(name, charset);

		assert sizeExpression != null;

		this.sizeExpression = sizeExpression;
	}

	/**
	 * Construct {@code AStringAttribute}.
	 *
	 * @param name The attribute's name.
	 * @param charset The charset to use for string decoding.
	 * @param size The static string data size.
	 */
	public FixedStringAttribute(String name, Charset charset, Number size) {
		this(name, charset, new NumberExpression<>(size));
	}

	/**
	 * Construct {@code AStringAttribute}.
	 *
	 * @param name The attribute's name.
	 * @param charset The charset to use for string decoding.
	 * @param sizeLambda The expression providing the string data size.
	 */
	public FixedStringAttribute(String name, Charset charset, Supplier<? extends Number> sizeLambda) {
		this(name, charset, new NumberExpression<>(sizeLambda));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.spec.FormatSpec#isFixedSize()
	 */
	@Override
	public boolean isFixedSize() {
		Number sizeValue = this.sizeExpression.beforeDecode();

		return sizeValue != null && sizeValue.longValue() < MAX_MATCH_SIZE;
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
		long stringSize = this.sizeExpression.decode().longValue();
		long decoded = 0L;

		if (!isSA(result.input(), position, stringSize)) {
			result.updateDecodeStatus(DecodeStatusException.fatal("Unexpected end of data"));
		} else {
			String value = decodeString(result, position, stringSize).toString();

			if (!validateValue(value)) {
				result.updateDecodeStatus(DecodeStatusException.fatal("Invalid data"));
			} else {
				decoded = stringSize;
				if (isBound()) {
					bindValue(value);
				}
			}
		}
		return decoded;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.spec.FormatSpec#specRender(de.carne.
	 * filescanner.core.FileScannerResult, long, long,
	 * de.carne.filescanner.spi.FileScannerResultRenderer)
	 */
	@Override
	public void specRender(FileScannerResult result, long start, long end, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException {
		renderer.setNormalMode().renderText(name());
		renderer.setOperatorMode().renderText(" = ");
		renderString(result, start, this.sizeExpression.decode().longValue(), renderer);
		renderer.renderBreakOrClose(isResult());
	}

}
