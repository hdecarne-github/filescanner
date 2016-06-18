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

import de.carne.filescanner.core.format.spec.SectionSpec;
import de.carne.filescanner.core.format.spec.StructSpec;
import de.carne.filescanner.core.format.spec.U16ArrayAttribute;
import de.carne.filescanner.core.format.spec.U16Attribute;
import de.carne.filescanner.core.format.spec.U16Attributes;
import de.carne.filescanner.core.format.spec.U32Attribute;
import de.carne.filescanner.core.format.spec.U8ArrayAttribute;

/**
 * DOS image format structures.
 */
class DOSImageFormatSpecs {

	public static final String NAME_DOS_STUB = "DOS stub executable image";

	public static final String NAME_DOS_STUB_HEADER = "DOS stub header";

	public static final U32Attribute DOS_STUB_E_LFANEW = new U32Attribute("e_lfanew");

	public static final StructSpec DOS_STUB_HEADER;

	static {
		StructSpec dosStubHeader = new StructSpec();

		dosStubHeader.append(new U8ArrayAttribute("signature", 2).addValidValue(new Byte[] { 0x4D, 0x5A }));
		dosStubHeader.append(
				new U16Attribute("lastsize", U16Attributes.DECIMAL_FORMAT).addValidator(s -> 0 < s && s <= 512));
		dosStubHeader.append(new U16Attribute("nblocks", U16Attributes.DECIMAL_FORMAT).addValidator(s -> 0 < s));
		dosStubHeader.append(new U16Attribute("nreloc", U16Attributes.DECIMAL_FORMAT).addValidator(s -> s >= 0));
		dosStubHeader.append(new U16Attribute("hdrsize", U16Attributes.DECIMAL_FORMAT).addValidator(s -> s > 0));
		dosStubHeader.append(new U16Attribute("minalloc"));
		dosStubHeader.append(new U16Attribute("maxalloc"));
		dosStubHeader.append(new U16Attribute("ss"));
		dosStubHeader.append(new U16Attribute("sp"));
		dosStubHeader.append(new U16Attribute("checksum"));
		dosStubHeader.append(new U16Attribute("ip"));
		dosStubHeader.append(new U16Attribute("cs"));
		dosStubHeader.append(new U16Attribute("relocpos").addValidValue((short) 0x0040));
		dosStubHeader.append(new U16Attribute("noverlay", U16Attributes.DECIMAL_FORMAT));
		dosStubHeader.append(new U16ArrayAttribute("reserved1", 4));
		dosStubHeader.append(new U16Attribute("oem_id"));
		dosStubHeader.append(new U16Attribute("oem_info"));
		dosStubHeader.append(new U16ArrayAttribute("reserved2", 10));
		dosStubHeader.append(DOS_STUB_E_LFANEW.bind());
		dosStubHeader.setResult(NAME_DOS_STUB_HEADER);
		DOS_STUB_HEADER = dosStubHeader;
	}

	public static final SectionSpec IMAGE_DATA;

	static {
		SectionSpec imageData = new SectionSpec(() -> dosStubImageDataSize());

		imageData.setResult("Image data");
		IMAGE_DATA = imageData;
	}

	private static Long dosStubImageDataSize() {
		return DOS_STUB_E_LFANEW.get() - 64L;
	}

	public static final StructSpec DOS_STUB;

	static {
		StructSpec dosStub = new StructSpec();

		dosStub.declareAttributes(DOS_STUB_E_LFANEW);
		dosStub.append(DOS_STUB_HEADER);
		dosStub.append(IMAGE_DATA);
		dosStub.setResult(NAME_DOS_STUB);
		DOS_STUB = dosStub;
	}

}
