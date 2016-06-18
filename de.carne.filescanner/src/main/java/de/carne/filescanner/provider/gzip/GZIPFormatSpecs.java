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

import de.carne.filescanner.core.format.spec.StructFormatSpec;
import de.carne.filescanner.core.format.spec.U32Attribute;
import de.carne.filescanner.core.format.spec.U32Attributes;
import de.carne.filescanner.core.format.spec.U8ArrayAttribute;
import de.carne.filescanner.core.format.spec.U8Attribute;
import de.carne.filescanner.core.input.DecodeParams;
import de.carne.filescanner.core.input.DeflateDecodeParams;

/**
 * gzip format structures.
 */
class GZIPFormatSpecs {

	public static final String NAME_GZIP = "gzip data set";

	public static final String NAME_GZIP_HEADER = "gzip header";

	public static final String NAME_GZIP_TRAILER = "gzip trailer";

	public static final StructFormatSpec GZIP_HEADER;

	static {
		StructFormatSpec header = new StructFormatSpec();

		header.append(new U8ArrayAttribute("ID", 2).addValidValue(new Byte[] { 0x1f, (byte) 0x8b }));
		header.append(new U8Attribute("CM").addValidValue((byte) 8));
		header.append(new U8Attribute("FLG"));
		header.append(new U32Attribute("MTIME").addExtraRenderer(U32Attributes.CTIME_COMMENT));
		header.append(new U8Attribute("XFL"));
		header.append(new U8Attribute("OS"));
		header.setResult(NAME_GZIP_HEADER);
		GZIP_HEADER = header;
	}

	public static final StructFormatSpec GZIP_TRAILER;

	static {
		StructFormatSpec dd = new StructFormatSpec();

		dd.append(new U32Attribute("local file header signature").addValidValue(0x08074b50));
		dd.append(new U32Attribute("crc-32"));
		dd.setResult(NAME_GZIP_TRAILER);
		GZIP_TRAILER = dd;
	}

	public static final StructFormatSpec GZIP;

	static {
		StructFormatSpec zip = new StructFormatSpec();

		zip.append(GZIP_HEADER);
		zip.append(GZIP_TRAILER);
		zip.setResult(NAME_GZIP);
		GZIP = zip;
	}

	private static DecodeParams getInputDecodeParams() {
		return new DeflateDecodeParams(0L, Paths.get("."));
	}

}
