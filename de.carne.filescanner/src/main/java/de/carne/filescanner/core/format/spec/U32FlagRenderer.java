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
 * Renderer used for the display of {@linkplain NumberAttributeType#U32}
 * attribute based flag-sets.
 */
public class U32FlagRenderer extends FlagRenderer<Integer> {

	private static final int MSB = 0b1000000000000000000000000000000;

	static int shift(int flag) {
		return flag >>> 1;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			private int nextFlag = MSB;

			@Override
			public boolean hasNext() {
				return this.nextFlag != 0;
			}

			@Override
			public Integer next() {
				int currentFlag = this.nextFlag;

				this.nextFlag = shift(this.nextFlag);
				return currentFlag;
			}

		};
	}

	@Override
	protected boolean testFlag(Integer flag, Integer value) {
		return (value.intValue() & flag.intValue()) != 0;
	}

	@Override
	protected Integer foldFlag(Integer flag1, Integer flag2) {
		return Integer.valueOf(flag1.intValue() | flag2.intValue());
	}

	@Override
	protected String formatFlag(Integer flag, Integer value) {
		StringBuilder formatBuffer = new StringBuilder();
		int shiftFlag = MSB;
		int flagValue = flag.intValue();

		while (shiftFlag != 0) {
			if ((shiftFlag & flagValue) != 0) {
				formatBuffer.append((flagValue & value.intValue()) != 0 ? '1' : '0');
			} else {
				formatBuffer.append('.');
			}
			shiftFlag = shift(shiftFlag);
		}
		return formatBuffer.toString();
	}

}
