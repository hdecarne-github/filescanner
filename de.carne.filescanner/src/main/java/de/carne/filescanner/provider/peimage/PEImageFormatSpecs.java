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

import java.nio.charset.StandardCharsets;

import de.carne.filescanner.core.format.spec.ConditionalSpec;
import de.carne.filescanner.core.format.spec.FixedArraySpec;
import de.carne.filescanner.core.format.spec.FixedStringAttribute;
import de.carne.filescanner.core.format.spec.FormatSpec;
import de.carne.filescanner.core.format.spec.StructSpec;
import de.carne.filescanner.core.format.spec.U16Attribute;
import de.carne.filescanner.core.format.spec.U16Attributes;
import de.carne.filescanner.core.format.spec.U16FlagRenderer;
import de.carne.filescanner.core.format.spec.U16SymbolRenderer;
import de.carne.filescanner.core.format.spec.U32Attribute;
import de.carne.filescanner.core.format.spec.U32Attributes;
import de.carne.filescanner.core.format.spec.U32FlagRenderer;
import de.carne.filescanner.core.format.spec.U8Attribute;
import de.carne.filescanner.core.format.spec.U8Attributes;

/**
 * PE image format structures.
 */
class PEImageFormatSpecs {

	public static final String NAME_PE_IMAGE = "Portable Executable image";

	public static final String NAME_PE_HEADER = "Portable Executable header";

	public static final String NAME_WIN32_HEADER = "Win32 optional header";

	public static final String NAME_PE_SECTION_HEADER = "Section header [{0}]";

	public static final String NAME_PE_SECTION_HEADERS = "Section headers";

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

	public static final U16SymbolRenderer PE_OPTIONAL_HEADER_MAGIC_SYMBOLS = new U16SymbolRenderer();

	static {
		PE_OPTIONAL_HEADER_MAGIC_SYMBOLS.addSymbol((short) 0x10b, "PE32 executable (Win32)");
		PE_OPTIONAL_HEADER_MAGIC_SYMBOLS.addSymbol((short) 0x107, "ROM image");
		PE_OPTIONAL_HEADER_MAGIC_SYMBOLS.addSymbol((short) 0x20b, "PE32+ executable (Win64)");
	}

	public static final U16SymbolRenderer PE_SUBSYSTEM_SYMBOLS = new U16SymbolRenderer();

	static {
		PE_SUBSYSTEM_SYMBOLS.addSymbol((short) 0, "IMAGE_SUBSYSTEM_UNKNOWN");
		PE_SUBSYSTEM_SYMBOLS.addSymbol((short) 1, "IMAGE_SUBSYSTEM_NATIVE");
		PE_SUBSYSTEM_SYMBOLS.addSymbol((short) 2, "IMAGE_SUBSYSTEM_WINDOWS_GUI");
		PE_SUBSYSTEM_SYMBOLS.addSymbol((short) 3, "IMAGE_SUBSYSTEM_WINDOWS_CUI");
		PE_SUBSYSTEM_SYMBOLS.addSymbol((short) 7, "IMAGE_SUBSYSTEM_POSIX_CUI");
		PE_SUBSYSTEM_SYMBOLS.addSymbol((short) 9, "IMAGE_SUBSYSTEM_WINDOWS_CE_GUI");
		PE_SUBSYSTEM_SYMBOLS.addSymbol((short) 10, "IMAGE_SUBSYSTEM_EFI_APPLICATION");
		PE_SUBSYSTEM_SYMBOLS.addSymbol((short) 11, "IMAGE_SUBSYSTEM_EFI_BOOT_SERVICE_DRIVER");
		PE_SUBSYSTEM_SYMBOLS.addSymbol((short) 12, "IMAGE_SUBSYSTEM_EFI_RUNTIME_DRIVER");
		PE_SUBSYSTEM_SYMBOLS.addSymbol((short) 13, "IMAGE_SUBSYSTEM_EFI_ROM");
		PE_SUBSYSTEM_SYMBOLS.addSymbol((short) 14, "IMAGE_SUBSYSTEM_XBOX");
	}

	public static final U16FlagRenderer PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS = new U16FlagRenderer();

	static {
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0001, "<reserved>");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0002, "<reserved>");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0004, "<reserved>");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0008, "<reserved>");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0020, "IMAGE_DLLCHARACTERISTICS_HIGH_ENTROPY_VA");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0040, "IMAGE_DLLCHARACTERISTICS_DYNAMIC_BASE");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0080, "IMAGE_DLLCHARACTERISTICS_FORCE_INTEGRITY");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0100, "IMAGE_DLLCHARACTERISTICS_NX_COMPAT");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0200, "IMAGE_DLLCHARACTERISTICS_NO_ISOLATION");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0400, "IMAGE_DLLCHARACTERISTICS_NO_SEH");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x0800, "IMAGE_DLLCHARACTERISTICS_NO_BIND");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x1000, "IMAGE_DLLCHARACTERISTICS_APPCONTAINER");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x2000, "IMAGE_DLLCHARACTERISTICS_WDM_DRIVER");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x4000, "IMAGE_DLLCHARACTERISTICS_GUARD_CF");
		PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol((short) 0x8000,
				"IMAGE_DLLCHARACTERISTICS_TERMINAL_SERVER_AWARE");
	}

	public static final U32FlagRenderer PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS = new U32FlagRenderer();

	static {
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000001, "<reserved>");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000002, "<reserved>");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000004, "<reserved>");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000008, "IMAGE_SCN_TYPE_NO_PAD");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000010, "<reserved>");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000020, "IMAGE_SCN_CNT_CODE");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000040, "IMAGE_SCN_CNT_INITIALIZED_DATA");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000080, "IMAGE_SCN_CNT_UNINITIALIZED_DATA");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000100, "IMAGE_SCN_LNK_OTHER");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000200, "IMAGE_SCN_LNK_INFO");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000400, "<reserved>");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00000800, "IMAGE_SCN_LNK_REMOVE");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00001000, "IMAGE_SCN_LNK_COMDAT");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00008000, "IMAGE_SCN_GPREL");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00020000, "IMAGE_SCN_MEM_PURGEABLE");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00020000, "IMAGE_SCN_MEM_16BIT");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00040000, "IMAGE_SCN_MEM_LOCKED");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00080000, "IMAGE_SCN_MEM_PRELOAD");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00100000, "IMAGE_SCN_ALIGN_1BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00200000, "IMAGE_SCN_ALIGN_2BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00300000, "IMAGE_SCN_ALIGN_4BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00400000, "IMAGE_SCN_ALIGN_8BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00500000, "IMAGE_SCN_ALIGN_16BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00600000, "IMAGE_SCN_ALIGN_32BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00700000, "IMAGE_SCN_ALIGN_64BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00800000, "IMAGE_SCN_ALIGN_128BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00900000, "IMAGE_SCN_ALIGN_256BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00A00000, "IMAGE_SCN_ALIGN_512BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00B00000, "IMAGE_SCN_ALIGN_1024BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00C00000, "IMAGE_SCN_ALIGN_2048BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00D00000, "IMAGE_SCN_ALIGN_4096BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x00E00000, "IMAGE_SCN_ALIGN_8192BYTES");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x01000000, "IMAGE_SCN_LNK_NRELOC_OVFL");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x02000000, "IMAGE_SCN_MEM_DISCARDABLE");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x04000000, "IMAGE_SCN_MEM_NOT_CACHED");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x08000000, "IMAGE_SCN_MEM_NOT_PAGED");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x10000000, "IMAGE_SCN_MEM_SHARED");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x20000000, "IMAGE_SCN_MEM_EXECUTE");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x40000000, "IMAGE_SCN_MEM_READ");
		PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS.addFlagSymbol(0x80000000, "IMAGE_SCN_MEM_WRITE");
	}

	public static final U16Attribute NUMBER_OF_SECTIONS = new U16Attribute("NumberOfSections");

	public static final U16Attribute SIZE_OF_OPTIONAL_HEADER = new U16Attribute("SizeOfOptionalHeader");

	public static final StructSpec PE_HEADER;

	static {
		StructSpec header = new StructSpec();

		header.append(new U32Attribute("Magic").addValidValue(0x00004550));
		header.append(new U16Attribute("Machine").addValidValues(PE_MACHINE_SYMBOLS.getValues())
				.addExtraRenderer(PE_MACHINE_SYMBOLS));
		header.append(NUMBER_OF_SECTIONS.setFormat(U16Attributes.DECIMAL_FORMAT).bind());
		header.append(new U32Attribute("TimeDateStamp").addExtraRenderer(U32Attributes.CTIME_COMMENT));
		header.append(new U32Attribute("PointerToSymbolTable"));
		header.append(new U32Attribute("NumberOfSymbols", U32Attributes.DECIMAL_FORMAT));
		header.append(SIZE_OF_OPTIONAL_HEADER.bind().addExtraRenderer(U16Attributes.BYTE_COUNT_COMMENT));
		header.append(new U16Attribute("Characteristics").addExtraRenderer(PE_CHARACTERISTICS_FLAG_SYMBOLS));
		header.setResult(NAME_PE_HEADER);
		PE_HEADER = header;
	}

	public static final StructSpec EXPORT_TABLE_ENTRY;

	static {
		StructSpec exportTable = new StructSpec();

		exportTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_EXPORT].VirtualAddress"));
		exportTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_EXPORT].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		EXPORT_TABLE_ENTRY = exportTable;
	}

	public static final StructSpec IMPORT_TABLE_ENTRY;

	static {
		StructSpec importTable = new StructSpec();

		importTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_IMPORT].VirtualAddress"));
		importTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_IMPORT].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		IMPORT_TABLE_ENTRY = importTable;
	}

	public static final StructSpec RESOURCE_TABLE_ENTRY;

	static {
		StructSpec resourceTable = new StructSpec();

		resourceTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_RESOURCE].VirtualAddress"));
		resourceTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_RESOURCE].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		RESOURCE_TABLE_ENTRY = resourceTable;
	}

	public static final StructSpec EXCEPTION_TABLE_ENTRY;

	static {
		StructSpec exceptionTable = new StructSpec();

		exceptionTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_EXCEPTION].VirtualAddress"));
		exceptionTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_EXCEPTION].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		EXCEPTION_TABLE_ENTRY = exceptionTable;
	}

	public static final StructSpec CERTIFICATE_TABLE_ENTRY;

	static {
		StructSpec certificateTable = new StructSpec();

		certificateTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_SECURITY].VirtualAddress"));
		certificateTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_SECURITY].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		CERTIFICATE_TABLE_ENTRY = certificateTable;
	}

	public static final StructSpec BASE_RELOCATION_TABLE_ENTRY;

	static {
		StructSpec baseRelocationTable = new StructSpec();

		baseRelocationTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_BASERELOC].VirtualAddress"));
		baseRelocationTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_BASERELOC].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		BASE_RELOCATION_TABLE_ENTRY = baseRelocationTable;
	}

	public static final StructSpec DEBUG_ENTRY;

	static {
		StructSpec debug = new StructSpec();

		debug.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_DEBUG].VirtualAddress"));
		debug.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_DEBUG].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		DEBUG_ENTRY = debug;
	}

	public static final StructSpec ARCHITECTURE_ENTRY;

	static {
		StructSpec architecture = new StructSpec();

		architecture.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_ARCHITECTURE].VirtualAddress"));
		architecture.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_ARCHITECTURE].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		ARCHITECTURE_ENTRY = architecture;
	}

	public static final StructSpec GLOBAL_PTR_ENTRY;

	static {
		StructSpec globalPtr = new StructSpec();

		globalPtr.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_GLOBALPTR].VirtualAddress"));
		globalPtr.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_GLOBALPTR].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		GLOBAL_PTR_ENTRY = globalPtr;
	}

	public static final StructSpec TLS_TABLE_ENTRY;

	static {
		StructSpec tlsTable = new StructSpec();

		tlsTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_TLS].VirtualAddress"));
		tlsTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_TLS].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		TLS_TABLE_ENTRY = tlsTable;
	}

	public static final StructSpec LOAD_CONFIG_TABLE_ENTRY;

	static {
		StructSpec loadConfigTable = new StructSpec();

		loadConfigTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_LOAD_CONFIG].VirtualAddress"));
		loadConfigTable.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_LOAD_CONFIG].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		LOAD_CONFIG_TABLE_ENTRY = loadConfigTable;
	}

	public static final StructSpec BOUND_IMPORT_ENTRY;

	static {
		StructSpec boundImport = new StructSpec();

		boundImport.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_BOUND_IMPORT].VirtualAddress"));
		boundImport.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_BOUND_IMPORT].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		BOUND_IMPORT_ENTRY = boundImport;
	}

	public static final StructSpec IAT_ENTRY;

	static {
		StructSpec iat = new StructSpec();

		iat.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_IAT].VirtualAddress"));
		iat.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_IAT].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		IAT_ENTRY = iat;
	}

	public static final StructSpec DELAY_IMPORT_DESCRIPTOR_ENTRY;

	static {
		StructSpec delayImportDescriptor = new StructSpec();

		delayImportDescriptor
				.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_DELAY_IMPORT].VirtualAddress"));
		delayImportDescriptor.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_DELAY_IMPORT].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		DELAY_IMPORT_DESCRIPTOR_ENTRY = delayImportDescriptor;
	}

	public static final StructSpec CLR_RUNTIME_HEADER_ENTRY;

	static {
		StructSpec clrRuntimeHeader = new StructSpec();

		clrRuntimeHeader.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_COM_DESCRIPTOR].VirtualAddress"));
		clrRuntimeHeader.append(new U32Attribute("DataDirectory[IMAGE_DIRECTORY_ENTRY_COM_DESCRIPTOR].Size")
				.addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		CLR_RUNTIME_HEADER_ENTRY = clrRuntimeHeader;
	}

	public static final StructSpec RESERVED15_ENTRY;

	static {
		StructSpec clrRuntimeHeader = new StructSpec();

		clrRuntimeHeader.append(new U32Attribute("DataDirectory[15].VirtualAddress"));
		clrRuntimeHeader
				.append(new U32Attribute("DataDirectory[15].Size").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		RESERVED15_ENTRY = clrRuntimeHeader;
	}

	public static final U32Attribute NUMBER_OF_RVA_AND_SIZES = new U32Attribute("NumberOfRvaAndSizes");

	public static final StructSpec WIN32_HEADER;

	static {
		StructSpec win32Header = new StructSpec();

		win32Header.append(new U16Attribute("Magic").addValidValues(PE_OPTIONAL_HEADER_MAGIC_SYMBOLS.getValues())
				.addExtraRenderer(PE_OPTIONAL_HEADER_MAGIC_SYMBOLS));
		win32Header.append(new U8Attribute("MajorLinkerVersion", U8Attributes.DECIMAL_FORMAT));
		win32Header.append(new U8Attribute("MinorLinkerVersion", U8Attributes.DECIMAL_FORMAT));
		win32Header.append(new U32Attribute("SizeOfCode").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		win32Header
				.append(new U32Attribute("SizeOfInitializedData").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		win32Header
				.append(new U32Attribute("SizeOfUninitializedData").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		win32Header.append(new U32Attribute("AddressOfEntryPoint"));
		win32Header.append(new U32Attribute("BaseOfCode"));
		win32Header.append(new U32Attribute("BaseOfData"));
		win32Header.append(new U32Attribute("ImageBase"));
		win32Header.append(new U32Attribute("SectionAlignment"));
		win32Header.append(new U32Attribute("FileAlignment"));
		win32Header.append(new U16Attribute("MajorOperatingSystemVersion", U16Attributes.DECIMAL_FORMAT));
		win32Header.append(new U16Attribute("MinorOperatingSystemVersion", U16Attributes.DECIMAL_FORMAT));
		win32Header.append(new U16Attribute("MajorImageVersion", U16Attributes.DECIMAL_FORMAT));
		win32Header.append(new U16Attribute("MinorImageVersion", U16Attributes.DECIMAL_FORMAT));
		win32Header.append(new U16Attribute("MajorSubsystemVersion", U16Attributes.DECIMAL_FORMAT));
		win32Header.append(new U16Attribute("MinorSubsystemVersion", U16Attributes.DECIMAL_FORMAT));
		win32Header.append(new U32Attribute("Win32VersionValue", U32Attributes.DECIMAL_FORMAT));
		win32Header.append(new U32Attribute("SizeOfImage").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		win32Header.append(new U32Attribute("SizeOfHeaders").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		win32Header.append(new U32Attribute("CheckSum"));
		win32Header.append(new U16Attribute("Subsystem").addExtraRenderer(PE_SUBSYSTEM_SYMBOLS));
		win32Header
				.append(new U16Attribute("DllCharacteristics").addExtraRenderer(PE_DLL_CHARACTERISTICS_FLAG_SYMBOLS));
		win32Header.append(new U32Attribute("SizeOfStackReserve").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		win32Header.append(new U32Attribute("SizeOfStackCommit").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		win32Header.append(new U32Attribute("SizeOfHeapReserve").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		win32Header.append(new U32Attribute("SizeOfHeapCommit").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		win32Header.append(new U32Attribute("LoaderFlags"));
		win32Header.append(NUMBER_OF_RVA_AND_SIZES.setFormat(U32Attributes.DECIMAL_FORMAT).bind());
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(0, EXPORT_TABLE_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(1, IMPORT_TABLE_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(2, RESOURCE_TABLE_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(3, EXCEPTION_TABLE_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(4, CERTIFICATE_TABLE_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(5, BASE_RELOCATION_TABLE_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(6, DEBUG_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(7, ARCHITECTURE_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(8, GLOBAL_PTR_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(9, TLS_TABLE_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(10, LOAD_CONFIG_TABLE_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(11, BOUND_IMPORT_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(12, IAT_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(13, DELAY_IMPORT_DESCRIPTOR_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(14, CLR_RUNTIME_HEADER_ENTRY)));
		win32Header.append(new ConditionalSpec(false, () -> dataDirectoryEntrySpec(15, RESERVED15_ENTRY)));
		win32Header.setResult(NAME_WIN32_HEADER);
		WIN32_HEADER = win32Header;
	}

	private static FormatSpec dataDirectoryEntrySpec(int index, FormatSpec spec) {
		return (index < NUMBER_OF_RVA_AND_SIZES.get().intValue() ? spec : null);
	}

	public static final FixedStringAttribute SECTION_NAME = new FixedStringAttribute("Name", StandardCharsets.UTF_8, 8);

	public static final StructSpec PE_SECTION_HEADER;

	static {
		StructSpec sectionHeader = new StructSpec();

		sectionHeader.append(SECTION_NAME.bind());
		sectionHeader.append(new U32Attribute("VirtualSize").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		sectionHeader.append(new U32Attribute("VirtualAddress"));
		sectionHeader.append(new U32Attribute("SizeOfRawData").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		sectionHeader.append(new U32Attribute("PointerToRawData"));
		sectionHeader.append(new U32Attribute("PointerToRelocations"));
		sectionHeader.append(new U32Attribute("PointerToLinenumbers"));
		sectionHeader.append(new U16Attribute("NumberOfRelocations", U16Attributes.DECIMAL_FORMAT));
		sectionHeader.append(new U16Attribute("NumberOfLinenumbers", U16Attributes.DECIMAL_FORMAT));
		sectionHeader
				.append(new U32Attribute("Characteristics").addExtraRenderer(PE_SECIION_CHARACTERISTICS_FLAG_SYMBOLS));
		sectionHeader.setResult(NAME_PE_SECTION_HEADER, SECTION_NAME);
		PE_SECTION_HEADER = sectionHeader;
	}

	public static final FixedArraySpec PE_SECTION_HEADERS;

	static {
		FixedArraySpec sectionHeaders = new FixedArraySpec(PE_SECTION_HEADER, NUMBER_OF_SECTIONS);

		sectionHeaders.setResult(NAME_PE_SECTION_HEADERS);
		PE_SECTION_HEADERS = sectionHeaders;
	}

	public static final StructSpec PE_IMAGE;

	static {
		StructSpec image = new StructSpec();

		image.declareAttributes(NUMBER_OF_SECTIONS, SIZE_OF_OPTIONAL_HEADER);
		image.append(PE_HEADER);
		image.append(new ConditionalSpec(true, () -> getOptionalHeaderSpec()));
		image.append(PE_SECTION_HEADERS);
		image.setResult(NAME_PE_IMAGE);
		PE_IMAGE = image;
	}

	private static FormatSpec getOptionalHeaderSpec() {
		FormatSpec spec;

		switch (SIZE_OF_OPTIONAL_HEADER.get()) {
		case 0x00E0:
			spec = WIN32_HEADER;
			break;
		default:
			spec = null;
		}
		return spec;
	}

}
