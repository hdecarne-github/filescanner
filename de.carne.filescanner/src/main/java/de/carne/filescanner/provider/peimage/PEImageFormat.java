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

import java.nio.ByteOrder;

import de.carne.filescanner.core.format.Decodable;
import de.carne.filescanner.spi.Format;

/**
 * <a href="https://en.wikipedia.org/wiki/Portable_Executable">Portable
 * Executable</a> file format decoder.
 */
public class PEImageFormat extends Format {

	/**
	 * Construct {@code PEImageFormat}.
	 */
	public PEImageFormat() {
		super(PEImageFormatSpecs.NAME_PE_IMAGE, ByteOrder.LITTLE_ENDIAN);
		registerHeaderSpec(PEImageFormatSpecs.PE_HEADER);
	}

	@Override
	public Decodable decodable() {
		return PEImageFormatSpecs.PE_IMAGE;
	}

}
