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

import de.carne.filescanner.core.DecodeStatusException;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.format.ResultContext;
import de.carne.filescanner.spi.FileScannerResultRenderer;

/**
 * Variable array of format specs.
 */
public class VarArrayFormatSpec extends FormatSpec {

	private final FormatSpec spec;

	private final int minOccurrence;

	private final int maxOccurrence;

	/**
	 * Construct {@code VarArrayFormatSpec}.
	 *
	 * @param spec The spec defining the array elements.
	 * @param minOccurrence The minimum occurrence of the spec.
	 */
	public VarArrayFormatSpec(FormatSpec spec, int minOccurrence) {
		this(spec, minOccurrence, 0);
	}

	/**
	 * Construct {@code VarArrayFormatSpec}.
	 *
	 * @param spec The spec defining the array elements.
	 * @param minOccurrence The minimum occurrence of the spec.
	 * @param maxOccurrence The maximum occurrence of the space or {@code 0} if
	 *        undefined.
	 */
	public VarArrayFormatSpec(FormatSpec spec, int minOccurrence, int maxOccurrence) {
		assert spec != null;
		assert spec.matchSize() > 0;
		assert minOccurrence >= 0;
		assert maxOccurrence == 0 || maxOccurrence >= minOccurrence;

		this.spec = spec;
		this.minOccurrence = minOccurrence;
		this.maxOccurrence = maxOccurrence;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.spec.FormatSpec#isFixedSize()
	 */
	@Override
	public boolean isFixedSize() {
		return this.minOccurrence > 0 && this.minOccurrence == this.maxOccurrence && this.spec.isFixedSize();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.spec.FormatSpec#matchSize()
	 */
	@Override
	public int matchSize() {
		return (this.spec.isFixedSize() ? this.minOccurrence : 1) * this.spec.matchSize();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.spec.FormatSpec#matches(java.nio.
	 * ByteBuffer)
	 */
	@Override
	public boolean matches(ByteBuffer buffer) {
		boolean matches = this.spec.matches(buffer);

		if (matches && this.spec.isFixedSize()) {
			for (int occurrence = 2; matches && occurrence <= this.minOccurrence; occurrence++) {
				matches = this.spec.matches(buffer);
			}
		}
		return matches;
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
				result.updateDecodeStatus(specResult.decodeStatus());
			} else {
				specDecoded = this.spec.specDecode(result, specPosition);
			}
			if (specDecoded == 0L) {
				result.updateDecodeStatus(DecodeStatusException.fatal("No data decoded"));
			}

			DecodeStatusException decodeStatus = result.decodeStatus();

			if (decodeStatus != null && decodeStatus.isFatal()) {
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
	 * filescanner.core.FileScannerResult, long, long,
	 * de.carne.filescanner.spi.FileScannerResultRenderer)
	 */
	@Override
	public void specRender(FileScannerResult result, long start, long end, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException {
		result.renderDefault(renderer);
		renderer.renderBreakOrClose(isResult());
	}

}
