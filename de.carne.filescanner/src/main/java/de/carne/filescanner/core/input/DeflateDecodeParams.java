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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import de.carne.filescanner.core.transfer.FileScannerResultRenderer;
import de.carne.nio.compression.deflate.DeflateDecoder;
import de.carne.nio.compression.deflate.DeflateMode;
import de.carne.nio.compression.spi.Decoder;

/**
 * Deflate decoder parameter object.
 */
public class DeflateDecodeParams extends DecodeParams {

	private final HashSet<DeflateMode> modes = new HashSet<>();

	/**
	 * Construct {@code DeflateDecodeParams}.
	 *
	 * @param encodedSize The number of encoded bytes or {@code -1} if
	 *        undefined.
	 * @param decodedPath The decoded path.
	 * @param modes The {@linkplain DeflateMode}s to use.
	 */
	public DeflateDecodeParams(long encodedSize, Path decodedPath, DeflateMode... modes) {
		super("Deflate encoded data", encodedSize, decodedPath);
		for (DeflateMode mode : modes) {
			this.modes.add(mode);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.input.DecodeParams#newDecoder()
	 */
	@Override
	public Decoder newDecoder() {
		return new DeflateDecoder(this.modes);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.input.DecodeParams#render(de.carne.filescanner.
	 * spi.FileScannerResultRenderer)
	 */
	@Override
	public void render(FileScannerResultRenderer renderer) throws IOException, InterruptedException {
		super.render(renderer);

		ArrayList<DeflateMode> modeList = new ArrayList<>(this.modes);

		Collections.sort(modeList);
		for (DeflateMode mode : modeList) {
			renderer.renderBreak();
			renderer.setNormalMode().renderText("Compression mode");
			renderer.setOperatorMode().renderText(" = ");
			renderer.setValueMode().renderText(mode.name());
		}
	}

}
