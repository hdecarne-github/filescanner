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

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.spi.FileScannerResultRenderer;
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
	 * The maximum size to use for matching and actual data decoding.
	 */
	public static final int MAX_MATCH_SIZE = 1024;

	private final NumberAttributeType type;

	private final NumberFormat<T> format;

	private final NumberExpression<?> sizeExpression;

	private NumberArrayAttribute(NumberAttributeType type, String name, NumberFormat<T> format,
			NumberExpression<?> sizeExpression) {
		super(name);
		assert type != null;
		assert format != null;
		assert sizeExpression != null;

		this.type = type;
		this.format = format;
		this.sizeExpression = sizeExpression;
	}

	/**
	 * Construct {@code NumberArrayAttribute}.
	 *
	 * @param type The array attribute element's type.
	 * @param name The array attribute's name.
	 * @param format The array attribute's primary format.
	 * @param size The static array size.
	 */
	protected NumberArrayAttribute(NumberAttributeType type, String name, NumberFormat<T> format, Number size) {
		this(type, name, format, new NumberExpression<>(size));
	}

	/**
	 * Construct {@code NumberArrayAttribute}.
	 *
	 * @param type The array attribute element's type.
	 * @param name The array attribute's name.
	 * @param format The array attribute's primary format.
	 * @param sizeLambda The expression providing the array size.
	 */
	protected NumberArrayAttribute(NumberAttributeType type, String name, NumberFormat<T> format,
			Supplier<? extends Number> sizeLambda) {
		this(type, name, format, new NumberExpression<>(sizeLambda));
	}

	/**
	 * Get the array attribute element's date type.
	 *
	 * @return The array attribute element's date type.
	 */
	public final NumberAttributeType type() {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.spec.FormatSpec#isFixedSize()
	 */
	@Override
	public boolean isFixedSize() {
		Number sizeValue = this.sizeExpression.beforeDecode();

		return sizeValue != null && (sizeValue.longValue() * this.type.size()) < MAX_MATCH_SIZE;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.spec.FormatSpec#matchSize()
	 */
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

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.spec.FormatSpec#specDecode(de.carne.
	 * filescanner.core.FileScannerResultBuilder, long)
	 */
	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		return this.sizeExpression.decode().longValue() * this.type.size();
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

		StringBuilder formatBuffer = new StringBuilder();

		formatBuffer.append("[");

		long totalSize = end - start;
		int readSize = (int) Math.min(totalSize, MAX_MATCH_SIZE);
		int readCount = readSize / this.type.size();
		ByteBuffer readBuffer = result.cachedRead(start, readSize);

		for (int elementIndex = 0; elementIndex < readCount; elementIndex++) {
			T elementValue = getElementValue(readBuffer);

			if (elementValue == null) {
				break;
			}
			if (elementIndex > 0) {
				formatBuffer.append(", ");
			}
			formatBuffer.append(this.format.apply(elementValue));
		}
		if (readSize < totalSize) {
			formatBuffer.append(", \u2026");
		}
		formatBuffer.append("]");
		renderer.setValueMode();
		renderer.renderText(formatBuffer.toString());
		if (readSize < totalSize) {
			formatBuffer.setLength(0);
			formatBuffer.append(" // remaining ");
			formatBuffer.append(Units.formatByteValue(totalSize - MAX_MATCH_SIZE));
			formatBuffer.append(" omitted");
			renderer.setCommentMode().renderText(formatBuffer.toString());
		}
		renderer.renderBreakOrClose(isResult());
	}

	/**
	 * Get the a single attribute array element's value.
	 *
	 * @param buffer The buffer to get the value from.
	 * @return The attribute array element's value or {@code null} if the
	 *         buffer's data is insufficient.
	 */
	public abstract T getElementValue(ByteBuffer buffer);

}
