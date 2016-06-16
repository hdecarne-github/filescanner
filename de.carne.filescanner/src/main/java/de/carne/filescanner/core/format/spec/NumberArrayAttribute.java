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
import java.nio.ByteBuffer;
import java.util.function.Supplier;

import de.carne.filescanner.core.DecodeStatusException;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.transfer.ResultRenderer;
import de.carne.filescanner.util.Units;

/**
 * This class defines Number based attribute arrays of fixed size.
 * <p>
 * The actual length of the array has to be defined statically or via a Lambda
 * expression.
 * </p>
 *
 * @param <T> The attribute's type.
 */
public abstract class NumberArrayAttribute<T extends Number> extends Attribute<T[]> {

	/**
	 * The maximum size to use for data matching.
	 */
	public static final int MAX_MATCH_SIZE = 1024;

	/**
	 * The maximum size to use for data rendering.
	 */
	public static final int MAX_RENDER_SIZE = 16;

	private final NumberAttributeType type;

	private final NumberExpression<?> sizeExpression;

	private NumberFormat<T> format;

	private NumberArrayAttribute(NumberAttributeType type, String name, NumberExpression<?> sizeExpression,
			NumberFormat<T> format) {
		super(name);
		assert type != null;
		assert sizeExpression != null;
		assert format != null;

		this.type = type;
		this.sizeExpression = sizeExpression;
		this.format = format;
	}

	/**
	 * Construct {@code NumberArrayAttribute}.
	 *
	 * @param type The array element's type.
	 * @param name The array attribute's name.
	 * @param size The static array size.
	 * @param format The array element's primary format.
	 */
	protected NumberArrayAttribute(NumberAttributeType type, String name, Number size, NumberFormat<T> format) {
		this(type, name, new NumberExpression<>(size), format);
	}

	/**
	 * Construct {@code NumberArrayAttribute}.
	 *
	 * @param type The array element's type.
	 * @param name The array attribute's name.
	 * @param sizeLambda The expression providing the array size.
	 * @param format The array element's primary format.
	 */
	protected NumberArrayAttribute(NumberAttributeType type, String name, Supplier<? extends Number> sizeLambda,
			NumberFormat<T> format) {
		this(type, name, new NumberExpression<>(sizeLambda), format);
	}

	/**
	 * Get the array element's date type.
	 *
	 * @return The array element's date type.
	 */
	public final NumberAttributeType type() {
		return this.type;
	}

	/**
	 * Get the array element's format.
	 *
	 * @return The array element's format.
	 */
	public final NumberFormat<T> format() {
		return this.format;
	}

	/**
	 * Set the array element's format.
	 *
	 * @param format The format to set.
	 * @return The updated array attribute.
	 */
	public final NumberArrayAttribute<T> setFormat(NumberFormat<T> format) {
		assert format != null;

		this.format = format;
		return this;
	}

	@Override
	public boolean isFixedSize() {
		Number sizeValue = this.sizeExpression.beforeDecode();

		return sizeValue != null && (sizeValue.longValue() * this.type.size()) < MAX_MATCH_SIZE;
	}

	@Override
	public int matchSize() {
		Number sizeValue = this.sizeExpression.beforeDecode();
		int matchSize = 0;

		if (sizeValue != null) {
			long sizeLong = sizeValue.longValue() * this.type.size();

			if (sizeLong < MAX_MATCH_SIZE) {
				matchSize = sizeValue.intValue() * this.type.size();
			}
		}
		return matchSize;
	}

	@Override
	public boolean matches(ByteBuffer buffer) {
		Number sizeValue = this.sizeExpression.beforeDecode();
		boolean matches = false;

		if (sizeValue != null) {
			long sizeLong = sizeValue.longValue() * this.type.size();

			if (sizeLong < MAX_MATCH_SIZE) {
				int size = sizeValue.intValue();

				if (isSA(buffer, size * this.type.size())) {
					if (hasValidators()) {
						T[] values = getValues(buffer, size);

						matches = values != null && validateValue(values);
					} else {
						matches = true;
					}
				}
			}
		}
		return matches;
	}

	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		long totalSize = this.sizeExpression.decode().longValue() * this.type.size();
		long decoded = 0L;

		if (!isSA(result.input(), position, totalSize)) {
			result.updateDecodeStatus(DecodeStatusException.fatal(DecodeStatusException.STATUS_UNEXPECTED_EOD));
		} else {
			decoded = totalSize;
		}
		return decoded;
	}

	@Override
	public void specRender(FileScannerResult result, long start, long end, ResultRenderer renderer)
			throws IOException, InterruptedException {
		renderer.setNormalMode().renderText(name());
		renderer.setOperatorMode().renderText(" = ");
		renderer.setNormalMode().renderText("{");

		StringBuilder formatBuffer = new StringBuilder();

		long totalSize = end - start;
		int readSize = (int) Math.min(totalSize, MAX_RENDER_SIZE);
		int readCount = readSize / this.type.size();
		ByteBuffer readBuffer = result.cachedRead(start, readSize);
		T[] elements = getValues(readBuffer, readCount);

		if (elements != null) {
			boolean firstElement = true;

			for (T elementValue : elements) {
				if (!firstElement) {
					formatBuffer.append(", ");
				} else {
					firstElement = false;
				}
				formatBuffer.append(this.format.apply(elementValue));
			}
		}
		if (readSize < totalSize) {
			formatBuffer.append(", \u2026");
		}
		renderer.setValueMode().renderText(formatBuffer.toString());
		renderer.setNormalMode().renderText(" }");
		if (readSize < totalSize) {
			formatBuffer.setLength(0);
			formatBuffer.append(" // remaining ");
			formatBuffer.append(Units.formatByteValue(totalSize - MAX_RENDER_SIZE));
			formatBuffer.append(" omitted");
			renderer.setCommentMode().renderText(formatBuffer.toString());
		}
		renderer.renderBreakOrClose(isResult());
	}

	/**
	 * Get the array values.
	 *
	 * @param buffer The buffer to get the values from.
	 * @param size The number of values to get.
	 * @return The attribute array values or {@code null} if the buffer's data
	 *         is insufficient.
	 */
	public abstract T[] getValues(ByteBuffer buffer, int size);

}
