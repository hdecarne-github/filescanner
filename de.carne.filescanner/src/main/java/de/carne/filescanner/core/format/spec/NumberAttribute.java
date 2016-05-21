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

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.spi.FileScannerResultRenderer;

/**
 * This class defines Number based attributes of fixed size.
 *
 * @param <T> The attribute's type.
 */
public abstract class NumberAttribute<T extends Number> extends Attribute<T> {

	private final NumberAttributeType type;

	private final NumberFormat<T> format;

	private T finalValue = null;

	/**
	 * Construct {@code NumberAttribute}.
	 *
	 * @param type The attribute's type.
	 * @param name The attribute's name.
	 * @param format The attribute's primary format.
	 */
	protected NumberAttribute(NumberAttributeType type, String name, NumberFormat<T> format) {
		super(name);
		assert type != null;
		assert format != null;

		this.type = type;
		this.format = format;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.FormatSpec#matchSize()
	 */
	@Override
	public int matchSize() {
		return this.type.size();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.FormatSpec#matches(java.nio.ByteBuffer)
	 */
	@Override
	public boolean matches(ByteBuffer buffer) {
		assert buffer != null;

		T value = getValue(buffer);

		return (value != null && (this.finalValue == null || this.finalValue.equals(value)));
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.FormatSpec#specDecode(de.carne.
	 * filescanner. core.FileScannerResultBuilder, long)
	 */
	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		int typeSize = this.type.size();

		if (isBound()) {
			ByteBuffer buffer = ensureSA(result.input().cachedRead(position, typeSize, result.order()), typeSize);

			bindValue(getValue(buffer));
		}
		return typeSize;
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
		int typeSize = this.type.size();
		ByteBuffer buffer = ensureSA(result.cachedRead(position, typeSize), typeSize);
		T value = getValue(buffer);

		renderer.setNormalMode().renderText(name());
		renderer.setOperatorMode().renderText(" = ");
		renderer.setValueMode().renderText(this.format.apply(value));
		renderer.renderBreakOrClose(isResult());
		return typeSize;
	}

	/**
	 * Get the attribute's value.
	 *
	 * @param buffer The buffer to get the value from.
	 * @return The attribute's value or {@code null} if the buffer's data is
	 *         insufficient.
	 */
	public abstract T getValue(ByteBuffer buffer);

	/**
	 * Make attribute final (with a specific value).
	 *
	 * @param finalValue The final value.
	 * @return The updated data attribute spec.
	 */
	public final NumberAttribute<T> setFinalValue(T finalValue) {
		this.finalValue = finalValue;
		return this;
	}

	/**
	 * Get the attribute's final value.
	 *
	 * @return The attribute's final value or {@code null} if the attribute is
	 *         not final.
	 */
	public final T getFinalValue() {
		return this.finalValue;
	}

}
