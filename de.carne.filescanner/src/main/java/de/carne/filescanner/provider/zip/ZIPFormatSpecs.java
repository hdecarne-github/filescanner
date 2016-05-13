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

import de.carne.filescanner.core.format.StructFormatSpec;
import de.carne.filescanner.core.format.U16Attribute;
import de.carne.filescanner.core.format.U32Attribute;

/**
 * ZIP format structures.
 */
class ZIPFormatSpecs {

	public static final StructFormatSpec LOCAL_FILE_HEADER;

	static {
		StructFormatSpec lfh = new StructFormatSpec(true);

		lfh.append(new U32Attribute("local file header signature").setFinalValue(0x04034b50));
		lfh.append(new U16Attribute("version needed to extract"));
		lfh.append(new U16Attribute("general purpose bit flag"));
		lfh.append(new U16Attribute("compression method"));
		lfh.append(new U16Attribute("last mod file time"));
		lfh.append(new U16Attribute("last mod file date"));
		lfh.append(new U32Attribute("crc-32"));
		lfh.append(new U32Attribute("compressed size"));
		lfh.append(new U32Attribute("uncompressed size"));
		lfh.append(new U16Attribute("file name length"));
		lfh.append(new U16Attribute("extra field length"));
		LOCAL_FILE_HEADER = lfh;
	}

	public static final StructFormatSpec ZIP_FORMAT;

	static {
		StructFormatSpec zip = new StructFormatSpec(false);

		zip.append(LOCAL_FILE_HEADER);
		ZIP_FORMAT = zip;
	}

}
