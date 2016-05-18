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
import java.nio.ByteBuffer;

import de.carne.filescanner.core.FileScannerResultBuilder;

/**
 * Define basic data attributes.
 *
 * @param <T> The attribute' data type.
 */
public abstract class DataAttribute<T> extends FormatSpec {

	private final DataType dataType;
	private final String name;
	private T finalValue = null;
	private boolean bound = false;

	/**
	 * Construct {@code DataAttribute}.
	 *
	 * @param dataType The attribute's data type.
	 * @param name The attribute's name.
	 */
	protected DataAttribute(DataType dataType, String name) {
		assert dataType != null;
		assert name != null;

		this.dataType = dataType;
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.FormatSpec#matchSize()
	 */
	@Override
	public int matchSize() {
		return this.dataType.size();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.FormatSpec#matches(java.nio.ByteBuffer)
	 */
	@Override
	public boolean matches(ByteBuffer buffer) {
		T value = getValue(buffer);

		return (value != null && (this.finalValue == null || this.finalValue.equals(value)));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.FormatSpec#eval(de.carne.filescanner.
	 * core.FileScannerResultBuilder, long)
	 */
	@Override
	public long eval(FileScannerResultBuilder result, long position) throws IOException {
		if (this.bound) {
			ByteBuffer buffer = result.input().cachedRead(position, this.dataType.size(), result.order());

			DecodeContext.get().setAttribute(this, getValue(buffer));
		}
		return this.dataType.size();
	}

	/**
	 * Get the attribute's data type.
	 *
	 * @return The attribute's data type.
	 */
	public final DataType dataType() {
		return this.dataType;
	}

	/**
	 * Get the attribute's name.
	 *
	 * @return The attribute's name.
	 */
	public final String name() {
		return this.name;
	}

	/**
	 * Get the attribute's value type.
	 *
	 * @return The attribute's value type.
	 */
	public abstract Class<T> getValueType();

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
	public final DataAttribute<T> setFinalValue(T finalValue) {
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

	/**
	 * Mark this attribute as bound.
	 * <p>
	 * The values of bound attributes are written to the current context during
	 * the spec's evaluation.
	 * </p>
	 *
	 * @return The updated data attribute spec.
	 */
	public final DataAttribute<T> bind() {
		this.bound = true;
		return this;
	}

}
