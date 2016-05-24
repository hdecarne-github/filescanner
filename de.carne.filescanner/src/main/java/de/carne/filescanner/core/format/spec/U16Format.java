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
 * Number formats for a {@linkplain NumberAttributeType#U16} attribute.
 */
public abstract class U16Format extends NumberFormat<Short> {

	/**
	 * Decimal format.
	 */
	public static final U16Format DECIMAL = new U16Format() {

		@Override
		public String apply(Short t) {
			return java.text.NumberFormat.getNumberInstance().format(t);
		}

	};

	/**
	 * Hexadecimal format.
	 */
	public static final U16Format HEXADECIMAL = new U16Format() {

		@Override
		public String apply(Short t) {
			return Hexadecimal.formatL(new StringBuilder("0x"), t.shortValue()).toString();
		}

	};

}