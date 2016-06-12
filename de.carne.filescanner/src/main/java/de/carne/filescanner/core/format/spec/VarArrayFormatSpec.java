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
import de.carne.filescanner.core.format.DecodeContext;
import de.carne.filescanner.core.transfer.ResultRenderer;

/**
 * Variable array of format specs.
 */
public class VarArrayFormatSpec extends FormatSpec {

	private final FormatSpec spec;

	private final FormatSpec stopSpec;

	private final int minOccurrence;

	private final int maxOccurrence;

	/**
	 * Construct {@code VarArrayFormatSpec}.
	 *
	 * @param spec The spec defining the array elements.
	 * @param stopSpec The optional spec indicating the last entry in the array.
	 * @param minOccurrence The minimum occurrence of the spec.
	 */
	public VarArrayFormatSpec(FormatSpec spec, FormatSpec stopSpec, int minOccurrence) {
		this(spec, stopSpec, minOccurrence, 0);
	}

	/**
	 * Construct {@code VarArrayFormatSpec}.
	 *
	 * @param spec The spec defining the array elements.
	 * @param stopSpec The optional spec indicating the last entry in the array.
	 * @param minOccurrence The minimum occurrence of the spec.
	 * @param maxOccurrence The maximum occurrence of the space or {@code 0} if
	 *        undefined.
	 */
	public VarArrayFormatSpec(FormatSpec spec, FormatSpec stopSpec, int minOccurrence, int maxOccurrence) {
		assert spec != null;
		assert spec.matchSize() > 0;
		assert stopSpec == null || stopSpec.matchSize() <= spec.matchSize();
		assert minOccurrence >= 0;
		assert maxOccurrence == 0 || maxOccurrence >= minOccurrence;

		this.spec = spec;
		this.stopSpec = stopSpec;
		this.minOccurrence = minOccurrence;
		this.maxOccurrence = maxOccurrence;
	}

	@Override
	public boolean isFixedSize() {
		return this.minOccurrence > 0 && this.minOccurrence == this.maxOccurrence && this.spec.isFixedSize();
	}

	@Override
	public int matchSize() {
		return (this.spec.isFixedSize() ? this.minOccurrence : 1) * this.spec.matchSize();
	}

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

	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		long decoded = 0;

		while (true) {
			long specPosition = position + decoded;
			ByteBuffer matchBuffer = result.cachedRead(specPosition, this.spec.matchSize());
			FormatSpec matchingSpec;

			if (this.stopSpec != null && this.stopSpec.matches(matchBuffer)) {
				matchingSpec = this.stopSpec;
			} else if (this.spec.matches(matchBuffer)) {
				matchingSpec = this.spec;
			} else {
				break;
			}

			long specDecoded;

			if (matchingSpec.isResult()) {
				FileScannerResultBuilder specResult = result.addResult(matchingSpec.resultType(), specPosition,
						matchingSpec);

				specDecoded = DecodeContext.setupAndDecode(matchingSpec, specResult);
				result.updateDecodeStatus(specResult.decodeStatus());
			} else {
				specDecoded = matchingSpec.specDecode(result, specPosition);
			}
			if (specDecoded == 0L) {
				result.updateDecodeStatus(DecodeStatusException.fatal("No data decoded"));
			}

			DecodeStatusException decodeStatus = result.decodeStatus();

			if (decodeStatus != null && decodeStatus.isFatal()) {
				break;
			}
			decoded += specDecoded;
			if (matchingSpec == this.stopSpec) {
				break;
			}
		}
		return decoded;
	}

	@Override
	public void specRender(FileScannerResult result, long start, long end, ResultRenderer renderer)
			throws IOException, InterruptedException {
		result.renderDefault(renderer);
		renderer.renderBreakOrClose(isResult());
	}

}
