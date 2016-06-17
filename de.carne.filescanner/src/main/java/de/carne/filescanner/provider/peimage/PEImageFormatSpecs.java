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
import de.carne.filescanner.core.format.spec.U16Attributes;
import de.carne.filescanner.core.format.spec.U16FlagRenderer;
import de.carne.filescanner.core.format.spec.U16SymbolRenderer;
import de.carne.filescanner.core.format.spec.U32Attribute;
import de.carne.filescanner.core.format.spec.U32Attributes;

/**
 * PE image format structures.
 */
class PEImageFormatSpecs {

	public static final String NAME_PE_IMAGE = "Portable Executable image";

	public static final String NAME_PE_HEADER = "PE header";

	public static final U16SymbolRenderer PE_MACHINE_SYMBOLS = new U16SymbolRenderer();

	static {
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x0, "IMAGE_FILE_MACHINE_UNKNOWN");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x1d3, "IMAGE_FILE_MACHINE_AM33");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x8664, "IMAGE_FILE_MACHINE_AMD64");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x1c0, "IMAGE_FILE_MACHINE_ARM");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x1c4, "IMAGE_FILE_MACHINE_ARMNT");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0xebc, "IMAGE_FILE_MACHINE_EBC");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x14c, "IMAGE_FILE_MACHINE_I386");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x200, "IMAGE_FILE_MACHINE_IA64");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x9041, "IMAGE_FILE_MACHINE_M32R");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x266, "IMAGE_FILE_MACHINE_MIPS16");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x366, "IMAGE_FILE_MACHINE_MIPSFPU");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x466, "IMAGE_FILE_MACHINE_MIPSFPU16");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x1f0, "IMAGE_FILE_MACHINE_POWERPC");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x1f1, "IMAGE_FILE_MACHINE_POWERPCFP");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x166, "IMAGE_FILE_MACHINE_R4000");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x5032, "IMAGE_FILE_MACHINE_RISCV32");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x5064, "IMAGE_FILE_MACHINE_RISCV64");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x5128, "IMAGE_FILE_MACHINE_RISCV128");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x1a2, "IMAGE_FILE_MACHINE_SH3");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x1a3, "IMAGE_FILE_MACHINE_SH3DSP");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x1a6, "IMAGE_FILE_MACHINE_SH4");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x1a8, "IMAGE_FILE_MACHINE_SH5");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x1c2, "IMAGE_FILE_MACHINE_THUMB");
		PE_MACHINE_SYMBOLS.addSymbol((short) 0x169, "IMAGE_FILE_MACHINE_WCEMIPSV2");
	}

	public static final U16FlagRenderer PE_CHARACTERISTICS_FLAG_SYMBOLS = new U16FlagRenderer();

	static {
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0001, "IMAGE_FILE_RELOCS_STRIPPED");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0002, "IMAGE_FILE_EXECUTABLE_IMAGE");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0004, "IMAGE_FILE_LINE_NUMS_STRIPPED");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0008, "IMAGE_FILE_LOCAL_SYMS_STRIPPED");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0010, "IMAGE_FILE_AGGRESSIVE_WS_TRIM");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0020, "IMAGE_FILE_LARGE_ADDRESS_AWARE");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0040, "<reserved>");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0080, "IMAGE_FILE_BYTES_REVERSED_LO");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0100, "IMAGE_FILE_32BIT_MACHINE");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0200, "IMAGE_FILE_DEBUG_STRIPPED");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0400, "IMAGE_FILE_REMOVABLE_RUN_FROM_SWAP");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0800, "IMAGE_FILE_NET_RUN_FROM_SWAP");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x1000, "IMAGE_FILE_SYSTEM");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x2000, "IMAGE_FILE_DLL");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x4000, "IMAGE_FILE_UP_SYSTEM_ONLY");
		PE_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x8000, "IMAGE_FILE_BYTES_REVERSED_HI");
	}

	public static final U16Attribute SIZE_OF_OPTIONAL_HEADER = new U16Attribute("SizeOfOptionalHeader");

	public static final StructFormatSpec PE_HEADER;

	static {
		StructFormatSpec header = new StructFormatSpec();

		header.append(new U32Attribute("Magic").addValidValue(0x00004550));
		header.append(new U16Attribute("Machine").addValidValues(PE_MACHINE_SYMBOLS.getValues())
				.addExtraRenderer(PE_MACHINE_SYMBOLS));
		header.append(new U16Attribute("NumberOfSections", U16Attributes.DECIMAL_FORMAT));
		header.append(new U32Attribute("TimeDateStamp").addExtraRenderer(U32Attributes.CTIME_COMMENT));
		header.append(new U32Attribute("PointerToSymbolTable"));
		header.append(new U32Attribute("NumberOfSymbols", U32Attributes.DECIMAL_FORMAT));
		header.append(SIZE_OF_OPTIONAL_HEADER.addExtraRenderer(U16Attributes.BYTE_COUNT_COMMENT));
		header.append(new U16Attribute("Characteristics").addExtraRenderer(PE_CHARACTERISTICS_FLAG_SYMBOLS));
		header.setResult(NAME_PE_HEADER);
		PE_HEADER = header;
	}

	public static final StructFormatSpec PE_IMAGE;

	static {
		StructFormatSpec image = new StructFormatSpec();

		image.declareAttributes(SIZE_OF_OPTIONAL_HEADER);
		image.append(PE_HEADER);
		image.setResult(NAME_PE_IMAGE);
		PE_IMAGE = image;
	}

}
