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
 * This class defines basic format spec attributes.
 * <p>
 * Basic attributes are of the form &lt;name&gt; = &lt;value&gt; where value is
 * a reasonable simple type. Basic attributes can be bound to the decode context
 * and hence evaluated by a format spec.
 * </p>
 *
 * @param <T> The attribute' data type.
 */
public abstract class Attribute<T> extends FormatSpec {

	private final String name;
	private boolean bound = false;

	/**
	 * Construct {@code Attribute}.
	 *
	 * @param name The attribute's name.
	 */
	protected Attribute(String name) {
		assert name != null;

		this.name = name;
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
	 * Mark this attribute as bound.
	 * <p>
	 * The values of bound attributes are written to the current context during
	 * the spec's evaluation.
	 * </p>
	 *
	 * @return The updated data attribute spec.
	 */
	public final Attribute<T> bind() {
		this.bound = true;
		return this;
	}

	/**
	 * Check whether this attribute is bound.
	 *
	 * @return [@code true} if the attribute is bound.
	 */
	protected final boolean isBound() {
		return this.bound;
	}

	/**
	 * Bind the attribute value.
	 *
	 * @param value The value to bind.
	 */
	protected final void bindValue(T value) {
		DecodeContext.get().setAttribute(this, value);
	}

}
