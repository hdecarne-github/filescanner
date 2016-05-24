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

import java.nio.charset.StandardCharsets;

import de.carne.filescanner.core.format.spec.AStringAttribute;
import de.carne.filescanner.core.format.spec.StructFormatSpec;
import de.carne.filescanner.core.format.spec.SymbolRenderer;
import de.carne.filescanner.core.format.spec.U16Attribute;
import de.carne.filescanner.core.format.spec.U32Attribute;

/**
 * ZIP format structures.
 */
class ZIPFormatSpecs {

	public static final String NAME_ZIP = "ZIP archive";

	public static final String NAME_ZIP_ENTRY = "ZIP entry [{0}]";

	public static final String NAME_ZIP_LFH = "Local file header";

	public static final U16Attribute LFH_COMPRESSION_METHOD = new U16Attribute("compression method");

	public static final SymbolRenderer<Short> ZIP_COMPRESSION_METHOD_SYMBOLS = new SymbolRenderer<>();

	static {
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 0, "Stored (no compression)");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 1, "Shrunk");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 2, "Reduced with compression factor 1");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 3, "Reduced with compression factor 2");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 4, "Reduced with compression factor 3");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 5, "Reduced with compression factor 4");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 6, "Imploded");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 7, "Reserved");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 8, "Deflated");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 9, "Enhanced Deflating");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 10, "IBM TERSE (old)");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 11, "Reserved");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 12, "BZIP2");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 13, "Reserved");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 14, "LZMA (EFS)");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 15, "Reserved");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 16, "Reserved");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 17, "Reserved");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 18, "IBM TERSE (new)");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 19, "IBM LZ77");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 97, "WavPack");
		ZIP_COMPRESSION_METHOD_SYMBOLS.addSymbol((short) 98, "PPMd");
		LFH_COMPRESSION_METHOD.addExtraRenderer(ZIP_COMPRESSION_METHOD_SYMBOLS);
	}

	public static final U16Attribute LFH_FILE_NAME_LENGTH = new U16Attribute("file name length");

	public static final U16Attribute LFH_EXTRA_FIELD_LENGTH = new U16Attribute("extra field length");

	public static final AStringAttribute LFH_FILE_NAME = new AStringAttribute("file name", StandardCharsets.UTF_8,
			LFH_FILE_NAME_LENGTH);

	public static final StructFormatSpec ZIP_LFH;

	static {
		StructFormatSpec lfh = new StructFormatSpec();

		lfh.append(new U32Attribute("local file header signature").setFinalValue(0x04034b50));
		lfh.append(new U16Attribute("version needed to extract"));
		lfh.append(new U16Attribute("general purpose bit flag"));
		lfh.append(LFH_COMPRESSION_METHOD.bind());
		lfh.append(new U16Attribute("last mod file time"));
		lfh.append(new U16Attribute("last mod file date"));
		lfh.append(new U32Attribute("crc-32"));
		lfh.append(new U32Attribute("compressed size"));
		lfh.append(new U32Attribute("uncompressed size"));
		lfh.append(LFH_FILE_NAME_LENGTH.bind(true));
		lfh.append(LFH_EXTRA_FIELD_LENGTH.bind(true));
		lfh.append(LFH_FILE_NAME.bind());
		lfh.setResult(NAME_ZIP_LFH);
		ZIP_LFH = lfh;
	}

	public static final StructFormatSpec ZIP_ENTRY;

	static {
		StructFormatSpec zipEntry = new StructFormatSpec();

		zipEntry.append(ZIP_LFH);
		zipEntry.declareAttribute(LFH_COMPRESSION_METHOD).declareAttribute(LFH_FILE_NAME);
		zipEntry.setResult(NAME_ZIP_ENTRY, LFH_FILE_NAME);
		ZIP_ENTRY = zipEntry;
	}

	public static final StructFormatSpec ZIP;

	static {
		StructFormatSpec zip = new StructFormatSpec();

		zip.append(ZIP_ENTRY);
		zip.setResult(NAME_ZIP);
		ZIP = zip;
	}

}
