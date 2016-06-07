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

import java.util.Iterator;

/**
 * Renderer used for the display of {@linkplain NumberAttributeType#U16}
 * attribute based flag-sets.
 */
public class U16FlagRenderer extends FlagRenderer<Short> {

	private static final short MSB = 0b100000000000000;

	@Override
	public Iterator<Short> iterator() {
		return new Iterator<Short>() {

			private short nextFlag = MSB;

			@Override
			public boolean hasNext() {
				return this.nextFlag != 0;
			}

			@Override
			public Short next() {
				short currentFlag = this.nextFlag;

				this.nextFlag >>>= 1;
				return currentFlag;
			}

		};
	}

	@Override
	protected boolean testFlag(Short flag, Short value) {
		return (value.shortValue() & flag.shortValue()) != 0;
	}

	@Override
	protected Short foldFlag(Short flag1, Short flag2) {
		return Short.valueOf((short) (flag1.shortValue() | flag2.shortValue()));
	}

	@Override
	protected String formatFlag(Short flag, Short value) {
		StringBuilder formatBuffer = new StringBuilder();
		short shiftFlag = MSB;
		short flagValue = flag.shortValue();

		while (shiftFlag != 0) {
			if ((shiftFlag & flagValue) != 0) {
				formatBuffer.append((flagValue & value.shortValue()) != 0 ? '1' : '0');
			} else {
				formatBuffer.append('.');
			}
			shiftFlag >>>= 1;
		}
		return formatBuffer.toString();
	}

}
