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

import de.carne.filescanner.core.DecodeStatusException;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.transfer.ResultRenderer;

/**
 * This class defines Number based attributes of fixed size.
 *
 * @param <T> The attribute's type.
 */
public abstract class NumberAttribute<T extends Number> extends Attribute<T> {

	private final NumberAttributeType type;

	private NumberFormat<T> format;

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

	/**
	 * Get the attribute's type.
	 *
	 * @return The attribute's type.
	 */
	public final NumberAttributeType type() {
		return this.type;
	}

	/**
	 * Get the attribute's format.
	 *
	 * @return The attribute's format.
	 */
	public final NumberFormat<T> format() {
		return this.format;
	}

	/**
	 * Set the attribute's format.
	 *
	 * @param format The format to set.
	 * @return The updated attribute.
	 */
	public final NumberAttribute<T> setFormat(NumberFormat<T> format) {
		assert format != null;

		this.format = format;
		return this;
	}

	@Override
	public boolean isFixedSize() {
		return true;
	}

	@Override
	public int matchSize() {
		return this.type.size();
	}

	@Override
	public boolean matches(ByteBuffer buffer) {
		T value = getValue(buffer);

		return (value != null && validateValue(value));
	}

	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		T value = getValue(result.input().cachedRead(position, this.type.size(), result.order()));
		long decoded = 0L;

		if (value == null) {
			result.updateDecodeStatus(DecodeStatusException.fatal(DecodeStatusException.STATUS_UNEXPECTED_EOD));
		} else if (!validateValue(value)) {
			result.updateDecodeStatus(DecodeStatusException.fatal(DecodeStatusException.STATUS_INVALID_DATA));
		} else {
			decoded = this.type.size();
			if (isBound()) {
				bindValue(value);
			}
		}
		return decoded;
	}

	@Override
	public void specRender(FileScannerResult result, long start, long end, ResultRenderer renderer)
			throws IOException, InterruptedException {
		int typeSize = this.type.size();
		ByteBuffer buffer = result.cachedRead(start, typeSize);
		T value = getValue(buffer);

		if (value != null) {
			renderer.setNormalMode().renderText(name());
			renderer.setOperatorMode().renderText(" = ");
			renderer.setValueMode().renderText(this.format.apply(value));
			for (AttributeRenderer<T> extraRenderer : getExtraRenderer()) {
				extraRenderer.render(value, renderer);
			}
		}
		renderer.renderBreakOrClose(isResult());
	}

	/**
	 * Get the attribute's value.
	 *
	 * @param buffer The buffer to get the value from.
	 * @return The attribute's value or {@code null} if the buffer's data is
	 *         insufficient.
	 */
	public abstract T getValue(ByteBuffer buffer);

}
