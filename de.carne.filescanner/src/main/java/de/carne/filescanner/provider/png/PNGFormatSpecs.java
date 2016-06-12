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
import de.carne.filescanner.core.format.spec.U32Attribute;
import de.carne.filescanner.core.format.spec.U32Attributes;
import de.carne.filescanner.core.format.spec.U8ArrayAttribute;
import de.carne.filescanner.core.format.spec.VarArrayFormatSpec;
import de.carne.filescanner.core.transfer.FileResultExporter;

/**
 * PNG format structures.
 */
class PNGFormatSpecs {

	public static final String NAME_PNG = "PNG image";

	public static final String NAME_SIGNATURE = "Signature";

	public static final String NAME_CHUNK = "{0} Chunk";

	public static final PNGChunkTypeRenderer PNG_CHUNK_TYPE_SYMBOLS = new PNGChunkTypeRenderer();

	static {
		PNG_CHUNK_TYPE_SYMBOLS.addSymbol(0x49484452, "IHDR Image header");
		PNG_CHUNK_TYPE_SYMBOLS.addSymbol(0x49454e44, "IEND Image trailer");
	}

	public static final PNGChunkFlagRenderer PNG_CHUNK_FLAGS = new PNGChunkFlagRenderer();

	static {
		PNG_CHUNK_FLAGS.addFlagSymbol(0x20000000, "Ancillary");
		PNG_CHUNK_FLAGS.addFlagSymbol(0x00200000, "Private");
		PNG_CHUNK_FLAGS.addFlagSymbol(0x00002000, "Reserved");
		PNG_CHUNK_FLAGS.addFlagSymbol(0x00000020, "Safe-to-copy");
	}

	public static final PNGImageRenderHandler PNG_RENDER_HANDLER = new PNGImageRenderHandler();

	public static final FileResultExporter PNG_FILE_EXPORTER = FileResultExporter.simpleExporter(NAME_PNG, ".png",
			"*.png");

	public static final StructFormatSpec PNG_SIGNATURE;

	static {
		StructFormatSpec signature = new StructFormatSpec();

		signature.append(new U8ArrayAttribute("signature", 8)
				.addValidValue(new Byte[] { (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a }));
		signature.setResult(NAME_SIGNATURE);
		PNG_SIGNATURE = signature;
	}

	public static final U32Attribute CHUNK_LENGTH = new U32Attribute("Length", U32Attributes.DECIMAL_FORMAT);
	public static final U8ArrayAttribute CHUNK_DATA = new U8ArrayAttribute("Chunk Data", CHUNK_LENGTH);
	public static final U32Attribute CHUNK_CRC = new U32Attribute("CRC");

	public static final U32Attribute CHUNK_TYPE_GENERIC = new U32Attribute("Chunk Type");
	public static final U32Attribute CHUNK_TYPE_IEND = new U32Attribute(CHUNK_TYPE_GENERIC.name());

	public static final StructFormatSpec PNG_CHUNK_GENERIC;

	static {
		StructFormatSpec chunkGeneric = new StructFormatSpec();

		chunkGeneric.append(CHUNK_LENGTH.bind().addExtraRenderer(U32Attributes.BYTE_COUNT_COMMENT));
		chunkGeneric.append(
				CHUNK_TYPE_GENERIC.bind().addExtraRenderer(PNG_CHUNK_TYPE_SYMBOLS).addExtraRenderer(PNG_CHUNK_FLAGS));
		chunkGeneric.append(CHUNK_DATA);
		chunkGeneric.append(CHUNK_CRC);
		chunkGeneric.setResult(NAME_CHUNK, () -> PNGChunkTypeRenderer.formatChunkType(CHUNK_TYPE_GENERIC.get()));
		PNG_CHUNK_GENERIC = chunkGeneric;
	}

	public static final StructFormatSpec PNG_CHUNK_IEND;

	static {
		StructFormatSpec chunkIEND = new StructFormatSpec();

		chunkIEND.append(CHUNK_LENGTH);
		chunkIEND.append(CHUNK_TYPE_IEND.bind().addValidValue(0x49454e44).addExtraRenderer(PNG_CHUNK_TYPE_SYMBOLS)
				.addExtraRenderer(PNG_CHUNK_FLAGS));
		chunkIEND.append(CHUNK_DATA);
		chunkIEND.append(CHUNK_CRC);
		chunkIEND.setResult(NAME_CHUNK, () -> PNGChunkTypeRenderer.formatChunkType(CHUNK_TYPE_IEND.get()));
		PNG_CHUNK_IEND = chunkIEND;
	}

	public static final StructFormatSpec PNG;

	static {
		StructFormatSpec png = new StructFormatSpec();

		png.append(PNG_SIGNATURE);
		png.append(new VarArrayFormatSpec(PNG_CHUNK_GENERIC, PNG_CHUNK_IEND, 1));
		png.setResult(NAME_PNG);
		png.setResultRenderHandler(PNG_RENDER_HANDLER);
		png.addResultExporter(PNG_FILE_EXPORTER);
		PNG = png;
	}

}
