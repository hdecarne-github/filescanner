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
package de.carne.filescanner.provider.zip;

import java.io.IOException;

import de.carne.filescanner.core.format.spec.U16SymbolRenderer;
import de.carne.filescanner.core.transfer.FileScannerResultRenderer;

/**
 * Custom renderer for ZIP version attribute.
 */
class ZIPVersionRenderer extends U16SymbolRenderer {

	@Override
	public void render(Short value, FileScannerResultRenderer renderer) throws IOException, InterruptedException {
		short shortValue = value.shortValue();
		short versionValue = (short) (shortValue & 0x00ff);
		short hostValue = (short) (shortValue & 0xff00);
		String symbol = getSymbol(hostValue);

		renderer.setCommentMode().renderText(" // ");
		renderer.renderText((versionValue / 10) + "." + (versionValue % 10));
		if (symbol != null) {
			renderer.renderText(" - ").renderText(symbol);
		}

	}

}
