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
 * Renderer used for the display of {@linkplain NumberAttributeType#U8}
 * attribute based flag-sets.
 */
public class U8FlagRenderer extends FlagRenderer<Byte> {

	private static final byte MSB = (byte) 0b10000000;

	static byte shift(byte flag) {
		return (byte) ((flag & 0xff) >>> 1);
	}

	@Override
	public Iterator<Byte> iterator() {
		return new Iterator<Byte>() {

			private byte nextFlag = MSB;

			@Override
			public boolean hasNext() {
				return this.nextFlag != 0;
			}

			@Override
			public Byte next() {
				byte currentFlag = this.nextFlag;

				this.nextFlag = shift(this.nextFlag);
				return currentFlag;
			}

		};
	}

	@Override
	protected boolean testFlag(Byte flag, Byte value) {
		return (value.byteValue() & flag.byteValue()) != 0;
	}

	@Override
	protected Byte foldFlag(Byte flag1, Byte flag2) {
		return Byte.valueOf((byte) (flag1.byteValue() | flag2.byteValue()));
	}

	@Override
	protected String formatFlag(Byte flag, Byte value) {
		StringBuilder formatBuffer = new StringBuilder();
		byte shiftFlag = MSB;
		byte flagValue = flag.byteValue();

		while (shiftFlag != 0) {
			if ((shiftFlag & flagValue) != 0) {
				formatBuffer.append((flagValue & value.byteValue()) != 0 ? '1' : '0');
			} else {
				formatBuffer.append('.');
			}
			shiftFlag = shift(shiftFlag);
		}
		return formatBuffer.toString();
	}

}
