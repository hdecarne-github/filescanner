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
package de.carne.filescanner.provider.gzip;

import java.nio.file.Paths;

import de.carne.filescanner.core.format.spec.EncodedSpec;
import de.carne.filescanner.core.format.spec.StructSpec;
import de.carne.filescanner.core.format.spec.U32Attribute;
import de.carne.filescanner.core.format.spec.U32Attributes;
import de.carne.filescanner.core.format.spec.U8ArrayAttribute;
import de.carne.filescanner.core.format.spec.U8Attribute;
import de.carne.filescanner.core.format.spec.U8FlagRenderer;
import de.carne.filescanner.core.format.spec.U8SymbolRenderer;
import de.carne.filescanner.core.input.DecodeParams;
import de.carne.filescanner.core.input.DeflateDecodeParams;

/**
 * gzip format structures.
 */
class GZIPFormatSpecs {

	public static final String NAME_GZIP = "gzip data set";

	public static final String NAME_GZIP_HEADER = "gzip header";

	public static final String NAME_GZIP_TRAILER = "gzip trailer";

	public static final U8FlagRenderer FLG_FLAG_SYMBOLS = new U8FlagRenderer();

	static {
		FLG_FLAG_SYMBOLS.addFlagSymbol((byte) 0x01, "FTEXT");
		FLG_FLAG_SYMBOLS.addFlagSymbol((byte) 0x02, "FHCRC");
		FLG_FLAG_SYMBOLS.addFlagSymbol((byte) 0x04, "FEXTRA");
		FLG_FLAG_SYMBOLS.addFlagSymbol((byte) 0x08, "FNAME");
		FLG_FLAG_SYMBOLS.addFlagSymbol((byte) 0x10, "FCOMMENT");
		FLG_FLAG_SYMBOLS.addFlagSymbol((byte) 0x20, "<reserved>");
		FLG_FLAG_SYMBOLS.addFlagSymbol((byte) 0x40, "<reserved>");
		FLG_FLAG_SYMBOLS.addFlagSymbol((byte) 0x80, "<reserved>");
	}

	public static final U8SymbolRenderer OS_SYMBOLS = new U8SymbolRenderer();

	static {
		OS_SYMBOLS.addSymbol((byte) 0, "FAT filesystem (MS-DOS, OS/2, NT/Win32)");
		OS_SYMBOLS.addSymbol((byte) 1, "Amiga");
		OS_SYMBOLS.addSymbol((byte) 2, "VMS (or OpenVMS)");
		OS_SYMBOLS.addSymbol((byte) 3, "Unix");
		OS_SYMBOLS.addSymbol((byte) 4, "VM/CMS");
		OS_SYMBOLS.addSymbol((byte) 5, "Atari TOS");
		OS_SYMBOLS.addSymbol((byte) 6, "HPFS filesystem (OS/2, NT)");
		OS_SYMBOLS.addSymbol((byte) 7, "Macintosh");
		OS_SYMBOLS.addSymbol((byte) 8, "Z-System");
		OS_SYMBOLS.addSymbol((byte) 9, "CP/M");
		OS_SYMBOLS.addSymbol((byte) 10, "TOPS-20");
		OS_SYMBOLS.addSymbol((byte) 11, "NTFS filesystem (NT)");
		OS_SYMBOLS.addSymbol((byte) 12, "QDOS");
		OS_SYMBOLS.addSymbol((byte) 13, "Acorn RISCOS");
		OS_SYMBOLS.addSymbol((byte) 255, "unknown");
	}

	public static final StructSpec GZIP_HEADER;

	static {
		StructSpec header = new StructSpec();

		header.append(new U8ArrayAttribute("ID", 2).addValidValue(new Byte[] { 0x1f, (byte) 0x8b }));
		header.append(new U8Attribute("CM").addValidValue((byte) 8));
		header.append(new U8Attribute("FLG").addExtraRenderer(FLG_FLAG_SYMBOLS));
		header.append(new U32Attribute("MTIME").addExtraRenderer(U32Attributes.CTIME_COMMENT));
		header.append(new U8Attribute("XFL"));
		header.append(new U8Attribute("OS").addValidValues(OS_SYMBOLS.getValues()).addExtraRenderer(OS_SYMBOLS));
		header.setResult(NAME_GZIP_HEADER);
		GZIP_HEADER = header;
	}

	public static final StructSpec GZIP_TRAILER;

	static {
		StructSpec trailer = new StructSpec();

		trailer.append(new U32Attribute("CRC32"));
		trailer.append(new U32Attribute("ISIZE").addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		trailer.setResult(NAME_GZIP_TRAILER);
		GZIP_TRAILER = trailer;
	}

	public static final StructSpec GZIP;

	static {
		StructSpec gzip = new StructSpec();

		gzip.append(GZIP_HEADER);
		gzip.append(new EncodedSpec(() -> getInputDecodeParams()));
		gzip.append(GZIP_TRAILER);
		gzip.setResult(NAME_GZIP);
		GZIP = gzip;
	}

	private static DecodeParams getInputDecodeParams() {
		return new DeflateDecodeParams(0L, Paths.get("."));
	}

}
