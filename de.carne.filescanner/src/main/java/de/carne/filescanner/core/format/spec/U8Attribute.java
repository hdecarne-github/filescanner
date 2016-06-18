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
 * Defines a {@linkplain NumberAttributeType#U8} attribute.
 */
public class U8Attribute extends NumberAttribute<Byte> {

	/**
	 * Construct {@code U8Attribute}.
	 *
	 * @param name The attribute's name.
	 */
	public U8Attribute(String name) {
		this(name, U8Attributes.HEXADECIMAL_FORMAT);
	}

	/**
	 * Construct {@code U8Attribute}.
	 *
	 * @param name The attribute's name.
	 * @param format The attribute's primary format.
	 */
	public U8Attribute(String name, NumberFormat<Byte> format) {
		super(NumberAttributeType.U8, name, format);
	}

	@Override
	public Class<Byte> getValueType() {
		return Byte.class;
	}

	@Override
	public Byte getValue(ByteBuffer buffer) {
		return (isBufferSufficient(buffer, type().size()) ? Byte.valueOf(buffer.get()) : null);
	}

}
