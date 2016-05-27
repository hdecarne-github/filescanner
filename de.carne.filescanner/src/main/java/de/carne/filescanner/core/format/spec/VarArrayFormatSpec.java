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
package de.carne.filescanner.core.format.spec;

import java.io.IOException;
import java.nio.ByteBuffer;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.format.ResultContext;
import de.carne.filescanner.spi.FileScannerResultRenderer;

/**
 * Variable array of format specs.
 */
public class VarArrayFormatSpec extends FormatSpec {

	private final FormatSpec spec;

	private final boolean matchOnce;

	/**
	 * Construct {@code VarArrayFormatSpec}.
	 *
	 * @param spec The spec defining the array elements.
	 * @param matchOnce Whether the minimum size of the array is {@code 0} (
	 *        {@code false}) or {@code 1} ({@code true}).
	 */
	public VarArrayFormatSpec(FormatSpec spec, boolean matchOnce) {
		assert spec != null;
		assert spec.matchSize() > 0;

		this.spec = spec;
		this.matchOnce = matchOnce;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.spec.FormatSpec#matchSize()
	 */
	@Override
	public int matchSize() {
		return (this.matchOnce ? this.spec.matchSize() : 0);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.spec.FormatSpec#matches(java.nio.
	 * ByteBuffer)
	 */
	@Override
	public boolean matches(ByteBuffer buffer) {
		return this.spec.matches(buffer);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.spec.FormatSpec#specDecode(de.carne.
	 * filescanner.core.FileScannerResultBuilder, long)
	 */
	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		long decoded = 0;

		while (true) {
			long specPosition = position + decoded;
			ByteBuffer matchBuffer = result.cachedRead(specPosition, this.spec.matchSize());

			if (!this.spec.matches(matchBuffer)) {
				break;
			}

			long specDecoded;

			if (this.spec.isResult()) {
				FileScannerResultBuilder specResult = result.addResult(this.spec.resultType(), specPosition, this.spec);

				specDecoded = ResultContext.setupAndDecode(this.spec, specResult);
			} else {
				specDecoded = this.spec.specDecode(result, specPosition);
			}
			if (specDecoded == 0L) {
				break;
			}
			decoded += specDecoded;
		}
		return decoded;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.spec.FormatSpec#specRender(de.carne.
	 * filescanner.core.FileScannerResult, long,
	 * de.carne.filescanner.spi.FileScannerResultRenderer)
	 */
	@Override
	public long specRender(FileScannerResult result, long position, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return result.size();
	}

}
