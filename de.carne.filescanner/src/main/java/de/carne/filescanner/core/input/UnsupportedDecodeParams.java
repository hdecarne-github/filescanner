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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

import de.carne.nio.compression.spi.Decoder;

/**
 * Decoder parameter object for an unsupported encodings.
 */
public class UnsupportedDecodeParams extends DecodeParams {

	/**
	 * Construct {@code UnsupportedDecodeParams}.
	 *
	 * @param encodedSize The number of encoded bytes.
	 * @param decodedPath The decoded path.
	 * @param name The name of the unsupported decoder.
	 */
	public UnsupportedDecodeParams(long encodedSize, Path decodedPath, String name) {
		super(name + " encoded data", encodedSize, decodedPath);
	}

	@Override
	public Decoder newDecoder() {
		return new Decoder() {

			@Override
			public int decode(ByteBuffer dst, ReadableByteChannel src) throws IOException {
				return -1;
			}

			@Override
			public String name() {
				return "Unsupported encoding";
			}

		};
	}

}
