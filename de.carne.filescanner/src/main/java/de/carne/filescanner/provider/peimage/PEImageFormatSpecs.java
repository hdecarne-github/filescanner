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
package de.carne.filescanner.provider.peimage;

import de.carne.filescanner.core.format.spec.StructFormatSpec;
import de.carne.filescanner.core.format.spec.U16Attribute;
import de.carne.filescanner.core.format.spec.U32Attribute;

/**
 * PE image format structures.
 */
class PEImageFormatSpecs {

	public static final String NAME_PE_IMAGE = "Portable Executable image";

	public static final String NAME_PE_HEADER = "PE header";

	public static final StructFormatSpec PE_HEADER;

	static {
		StructFormatSpec header = new StructFormatSpec();

		header.append(new U32Attribute("Magic").addValidValue(0x00004550));
		header.append(new U16Attribute("Machine"));
		header.append(new U16Attribute("NumberOfSections"));
		header.append(new U32Attribute("TimeDateStamp"));
		header.append(new U32Attribute("PointerToSymbolTable"));
		header.append(new U32Attribute("NumberOfSymbols"));
		header.append(new U16Attribute("SizeOfOptionalHeader"));
		header.append(new U16Attribute("Characteristics"));
		header.setResult(NAME_PE_HEADER);
		PE_HEADER = header;
	}

	public static final StructFormatSpec PE_IMAGE;

	static {
		StructFormatSpec image = new StructFormatSpec();

		image.append(PE_HEADER);
		image.setResult(NAME_PE_IMAGE);
		PE_IMAGE = image;
	}

}
