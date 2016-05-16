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
 * Define {@linkplain DataType#U16} data attribute.
 */
public class U32Attribute extends DataAttribute<Integer> {

	/**
	 * Construct {@code U32Attribute}.
	 *
	 * @param name The attribute's name.
	 */
	public U32Attribute(String name) {
		super(DataType.U32, name);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.DataAttribute#getValueType()
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
		return U32Values.get(buffer);
	}

	/**
	 * Make attribute final (with a specific value).
	 *
	 * @param finalValue The final value.
	 * @return The updated U32 attribute spec.
	 */
	public U32Attribute setFinalValue(int finalValue) {
		setFinalValue(Integer.valueOf(finalValue));
		return this;
	}

}