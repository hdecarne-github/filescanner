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
package de.carne.filescanner.core.input;

import java.nio.file.Path;

import de.carne.nio.compression.spi.Decoder;

/**
 * Null decoder parameter object.
 * <p>
 * This parameter object represents the pass-through decoder for data stored
 * without any compression/encoding.
 * </p>
 */
public class NullDecodeParams extends DecodeParams {

	/**
	 * Construct {@code NullDecoderParams}.
	 *
	 * @param encodedSize The number of encoded bytes.
	 * @param decodedPath The decoded path.
	 */
	public NullDecodeParams(long encodedSize, Path decodedPath) {
		super("Stored data", encodedSize, decodedPath);

		assert encodedSize >= 0;
	}

	@Override
	public Decoder newDecoder() {
		return null;
	}

}
