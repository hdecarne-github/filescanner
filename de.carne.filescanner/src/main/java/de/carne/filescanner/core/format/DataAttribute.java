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

import java.nio.ByteBuffer;

/**
 * Define basic data attributes.
 *
 * @param <T> The attribute' data type.
 */
public abstract class DataAttribute<T> extends FormatSpec {

	private final DataType dataType;
	private final String name;
	private T finalValue = null;

	/**
	 * Construct {@code DataAttribute}.
	 *
	 * @param dataType The attribute's data type.
	 * @param name The attribute's name.
	 */
	protected DataAttribute(DataType dataType, String name) {
		super(false);

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
	 * Get the attribute's value.
	 *
	 * @param buffer The buffer to get the value from.
	 * @return The attribute's value or {@code null} if the buffer's data is
	 *         insufficient.
	 */
	public abstract T getValue(ByteBuffer buffer);

}
