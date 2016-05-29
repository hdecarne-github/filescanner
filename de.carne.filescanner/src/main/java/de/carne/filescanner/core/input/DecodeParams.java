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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import de.carne.filescanner.spi.FileScannerResultRenderer;
import de.carne.nio.compression.deflate.DeflateDecoder;
import de.carne.nio.compression.deflate.DeflateMode;
import de.carne.nio.compression.deflate.DeflateName;
import de.carne.nio.compression.spi.Decoder;

/**
 * Parameter class defining the {@linkplain Decoder} as well as all other
 * necessary parameters for input decoding.
 */
public abstract class DecodeParams {

	private final String encodedName;

	private final long encodedSize;

	private final Path decodedPath;

	DecodeParams(String encodedName, long encodedSize, Path decodedPath) {
		assert encodedName != null;
		assert decodedPath != null;

		this.encodedName = encodedName;
		this.encodedSize = encodedSize;
		this.decodedPath = decodedPath;
	}

	/**
	 * Create {@code null} decoder parameter object.
	 * <p>
	 * This parameter object represents the pass-through decoder for non-encoded
	 * stored data.
	 * </p>
	 *
	 * @param encodedSize The number of encoded bytes or {@code -1} if
	 *        undefined.
	 * @param decodedPath The decoded path.
	 * @return The created factory.
	 */
	public static DecodeParams newNullDecoderFactory(long encodedSize, Path decodedPath) {
		return new DecodeParams("Stored data", encodedSize, decodedPath) {

			@Override
			public Decoder newDecoder() {
				return null;
			}

			@Override
			public void render(FileScannerResultRenderer renderer) throws IOException, InterruptedException {
				// Nothing to do here
			}

		};
	}

	/**
	 * Create decoder parameter object for an unsupported encodings.
	 *
	 * @param encodedSize The number of encoded bytes or {@code -1} if
	 *        undefined.
	 * @param decodedPath The decoded path.
	 * @param name The name of the unsupported decoder.
	 * @return The created factory.
	 */
	public static DecodeParams newUnsupportedDecoderFactory(long encodedSize, Path decodedPath, String name) {
		return new DecodeParams("Unsupported encoded data", encodedSize, decodedPath) {

			private final String decoderName = name;

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

			@Override
			public void render(FileScannerResultRenderer renderer) throws IOException, InterruptedException {
				renderer.setNormalMode().renderText("Compression");
				renderer.setOperatorMode().renderText(" = ");
				renderer.setValueMode().renderText(this.decoderName);
			}

		};
	}

	/**
	 * Create Deflate decoder parameter object.
	 *
	 * @param encodedSize The number of encoded bytes or {@code -1} if
	 *        undefined.
	 * @param decodedPath The decoded path.
	 * @param modes The {@linkplain DeflateMode}s to use.
	 * @return The created factory.
	 */
	public static DecodeParams newDeflateDecoderFactory(long encodedSize, Path decodedPath, DeflateMode... modes) {

		HashSet<DeflateMode> modeSet = new HashSet<>();

		for (DeflateMode mode : modes) {
			modeSet.add(mode);
		}
		return new DecodeParams("Deflate encoded data", encodedSize, decodedPath) {

			@Override
			public Decoder newDecoder() {
				return new DeflateDecoder(modeSet);
			}

			@Override
			public void render(FileScannerResultRenderer renderer) throws IOException, InterruptedException {
				renderer.setNormalMode().renderText("Compression");
				renderer.setOperatorMode().renderText(" = ");
				renderer.setValueMode().renderText(DeflateName.NAME);

				ArrayList<DeflateMode> modeList = new ArrayList<>(modeSet);

				Collections.sort(modeList);
				for (DeflateMode mode : modeList) {
					renderer.renderBreak();
					renderer.setNormalMode().renderText("Compression mode");
					renderer.setOperatorMode().renderText(" = ");
					renderer.setValueMode().renderText(mode.name());
				}
			}

		};
	}

	/**
	 * Get the encoded data name.
	 *
	 * @return The encoded data name.
	 */
	public final String getEncodedName() {
		return this.encodedName;
	}

	/**
	 * Get the number of encoded bytes.
	 *
	 * @return The number of encoded bytes or {@code -1} if undefined.
	 */
	public final long getEncodedSize() {
		return this.encodedSize;
	}

	/**
	 * Get the decoded path.
	 *
	 * @return The decoded path.
	 */
	public final Path getDecodedPath() {
		return this.decodedPath;
	}

	/**
	 * Create a new {@linkplain Decoder} instance.
	 *
	 * @return The new {@linkplain Decoder} instance.
	 */
	public abstract Decoder newDecoder();

	/**
	 * Render decoder information.
	 *
	 * @param renderer The renderer to use.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public abstract void render(FileScannerResultRenderer renderer) throws IOException, InterruptedException;

}
