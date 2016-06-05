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
package de.carne.filescanner.provider.png;

import de.carne.filescanner.core.format.spec.U32SymbolRenderer;
import de.carne.filescanner.util.Printer;

/**
 * Custom renderer for PNG chunk types.
 */
class PNGChunkTypeRenderer extends U32SymbolRenderer {

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.spec.SymbolRenderer#getSymbol(java.lang.
	 * Object)
	 */
	@Override
	public String getSymbol(Integer value) {
		String symbol = super.getSymbol(value);

		if (symbol == null) {
			symbol = formatChunkType(value);
		}
		return symbol;
	}

	public static String formatChunkType(Integer typeValue) {
		StringBuilder buffer = new StringBuilder();

		if (typeValue != null) {
			int type = typeValue.intValue();

			Printer.format(buffer, (byte) (type >>> 24));
			Printer.format(buffer, (byte) ((type >>> 16) & 0xff));
			Printer.format(buffer, (byte) ((type >>> 8) & 0xff));
			Printer.format(buffer, (byte) (type & 0xff));
		} else {
			buffer.append("????");
		}
		return buffer.toString();
	}

}
