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

import java.nio.ByteOrder;

import de.carne.filescanner.core.format.FormatSpec;
import de.carne.filescanner.spi.Format;

/**
 * ZIP file format decoder:
 * <a href="https://en.wikipedia.org/wiki/Zip_(file_format)">https://en.
 * wikipedia.org/wiki/Zip_(file_format)</a>.
 */
public class ZIPFormat extends Format {

	private static final String NAME = "ZIP archive";

	/**
	 * Construct {@code ZIPFormat}.
	 */
	public ZIPFormat() {
		super(NAME, ByteOrder.LITTLE_ENDIAN);
		registerHeaderSpec(ZIPFormatSpecs.LOCAL_FILE_HEADER);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.spi.Format#getFormatSpec()
	 */
	@Override
	protected FormatSpec getFormatSpec() {
		return ZIPFormatSpecs.ZIP_FORMAT;
	}

}
