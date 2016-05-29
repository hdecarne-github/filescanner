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

import java.nio.ByteBuffer;

/**
 * Defines a {@linkplain NumberAttributeType#U16} attribute.
 */
public class U32Attribute extends NumberAttribute<Integer> {

	/**
	 * Construct {@code U32Attribute}.
	 *
	 * @param name The attribute's name.
	 */
	public U32Attribute(String name) {
		this(name, U32Attributes.HEXADECIMAL_FORMAT);
	}

	/**
	 * Construct {@code U32Attribute}.
	 *
	 * @param name The attribute's name.
	 * @param format The attribute's primary format.
	 */
	public U32Attribute(String name, NumberFormat<Integer> format) {
		super(NumberAttributeType.U32, name, format);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.ResultAttribute#getValueType()
	 */
	@Override
	public Class<Integer> getValueType() {
		return Integer.class;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.DataAttribute#getValue(java.nio.
	 * ByteBuffer)
	 */
	@Override
	public Integer getValue(ByteBuffer buffer) {
		return (isSA(buffer, type().size()) ? Integer.valueOf(buffer.getInt()) : null);
	}

}
