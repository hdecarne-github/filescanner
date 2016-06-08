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
import java.nio.file.Path;

import de.carne.filescanner.core.transfer.ResultRenderer;
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
	public void render(ResultRenderer renderer) throws IOException, InterruptedException {
		renderer.setNormalMode().renderText("Compression");
		renderer.setOperatorMode().renderText(" = ");
		renderer.setValueMode().renderText(DeflateName.NAME);
	}

}
