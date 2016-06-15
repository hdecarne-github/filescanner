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
package de.carne.filescanner.provider.dosimage;

import de.carne.filescanner.core.format.spec.StructFormatSpec;
import de.carne.filescanner.core.format.spec.U16Attribute;

/**
 * DOS executable image format structures.
 */
class DOSImageFormatSpecs {

	public static final String NAME_DOS_MZ_EXECUTABLE = "DOS MZ executable";

	public static final String NAME_EXE_HEADER = "EXE header";

	public static final String NAME_OEM_HEADER = "OEM header";

	public static final StructFormatSpec EXE_HEADER;

	static {
		StructFormatSpec header = new StructFormatSpec();

		header.append(new U16Attribute("MAGIC").addValidValue((short) 0x5A4D));
		header.append(new U16Attribute("CBLP").addValidator(s -> 0 < s && s <= 512));
		header.append(new U16Attribute("CP").addValidator(s -> 0 < s));
		header.append(new U16Attribute("CRLC").addValidator(s -> 0 <= s));
		header.append(new U16Attribute("CPARHDR").addValidator(s -> 0 <= s));
		header.append(new U16Attribute("MINALLOC").addValidator(s -> 0 <= s));
		header.append(new U16Attribute("MAXALLOC"));
		header.append(new U16Attribute("SS"));
		header.append(new U16Attribute("SP"));
		header.append(new U16Attribute("CSUM"));
		header.append(new U16Attribute("IP"));
		header.append(new U16Attribute("CS"));
		header.append(new U16Attribute("LFARLC").addValidator(s -> 28 <= s));
		header.append(new U16Attribute("OVNO"));
		header.setResult(NAME_EXE_HEADER);
		EXE_HEADER = header;
	}

	public static final StructFormatSpec DOS_MZ_EXECUTABLE;

	static {
		StructFormatSpec executable = new StructFormatSpec();

		executable.append(EXE_HEADER);
		executable.setResult(NAME_DOS_MZ_EXECUTABLE);
		DOS_MZ_EXECUTABLE = executable;
	}

}
