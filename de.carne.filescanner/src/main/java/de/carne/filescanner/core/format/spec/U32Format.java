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

import de.carne.filescanner.util.Hexadecimal;

/**
 * Number formats for a {@linkplain NumberAttributeType#U32} attribute.
 */
public abstract class U32Format extends NumberFormat<Integer> {

	/**
	 * Decimal format.
	 */
	public static final U32Format DECIMAL = new U32Format() {

		@Override
		public String apply(Integer t) {
			return java.text.NumberFormat.getNumberInstance().format(t);
		}

	};

	/**
	 * Hexadecimal format.
	 */
	public static final U32Format HEXADECIMAL = new U32Format() {

		@Override
		public String apply(Integer t) {
			return Hexadecimal.formatL(new StringBuilder("0x"), t.intValue()).toString();
		}

	};

}
