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

import de.carne.filescanner.core.format.spec.StructFormatSpec;
import de.carne.filescanner.core.format.spec.U8ArrayAttribute;

/**
 * PNG format structures.
 */
class PNGFormatSpecs {

	public static final String NAME_PNG = "PNG image";

	public static final String NAME_SIGNATURE = "Signature";

	public static final StructFormatSpec PNG_SIGNATURE;

	static {
		StructFormatSpec signature = new StructFormatSpec();

		signature.append(new U8ArrayAttribute("signature", 8)
				.addValidValue(new Byte[] { (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a }));
		signature.setResult(NAME_SIGNATURE);
		PNG_SIGNATURE = signature;
	}

	public static final StructFormatSpec PNG;

	static {
		StructFormatSpec png = new StructFormatSpec();

		png.append(PNG_SIGNATURE);
		png.setResult(NAME_PNG);
		PNG = png;
	}

}
