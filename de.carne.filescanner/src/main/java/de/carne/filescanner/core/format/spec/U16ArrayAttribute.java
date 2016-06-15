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
import java.util.function.Supplier;

/**
 * Defines a {@linkplain NumberAttributeType#U16} array attribute.
 */
public class U16ArrayAttribute extends NumberArrayAttribute<Short> {

	/**
	 * Construct {@code U16ArrayAttribute}.
	 *
	 * @param name The array attribute's name.
	 * @param size The static array size.
	 */
	public U16ArrayAttribute(String name, Number size) {
		super(NumberAttributeType.U16, name, size, U16Attributes.HEXADECIMAL_FORMAT);
	}

	/**
	 * Construct {@code U16ArrayAttribute}.
	 *
	 * @param name The array attribute's name.
	 * @param sizeLambda The expression providing the array size.
	 */
	public U16ArrayAttribute(String name, Supplier<? extends Number> sizeLambda) {
		super(NumberAttributeType.U16, name, sizeLambda, U16Attributes.HEXADECIMAL_FORMAT);
	}

	@Override
	public Class<Short[]> getValueType() {
		return Short[].class;
	}

	@Override
	public Short[] getValues(ByteBuffer buffer, int size) {
		Short[] values = null;

		if (isSA(buffer, size * type().size())) {
			values = new Short[size];
			for (int valueIndex = 0; valueIndex < size; valueIndex++) {
				values[valueIndex] = buffer.getShort();
			}
		}
		return values;
	}

}
