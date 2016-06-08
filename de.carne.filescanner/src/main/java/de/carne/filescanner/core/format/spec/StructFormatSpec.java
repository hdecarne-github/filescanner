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
import java.util.ArrayList;

import de.carne.filescanner.core.DecodeStatusException;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.format.ResultContext;
import de.carne.filescanner.core.format.ResultSection;
import de.carne.filescanner.core.transfer.ResultRenderer;

/**
 * Struct format spec combining a consecutive list of format specs.
 */
public class StructFormatSpec extends FormatSpec {

	private final ArrayList<FormatSpec> specs = new ArrayList<>();

	/**
	 * Append a format spec.
	 *
	 * @param spec The spec to append.
	 * @return The updated struct spec.
	 */
	public StructFormatSpec append(FormatSpec spec) {
		assert spec != null;

		this.specs.add(spec);
		return this;
	}

	@Override
	public boolean isFixedSize() {
		boolean fixedSize = true;

		for (FormatSpec spec : this.specs) {
			if (!spec.isFixedSize()) {
				fixedSize = false;
				break;
			}
		}
		return fixedSize;
	}

	@Override
	public int matchSize() {
		int matchSize = 0;

		for (FormatSpec spec : this.specs) {
			int specMatchSize = spec.matchSize();

			if (specMatchSize == 0) {
				break;
			}
			matchSize += specMatchSize;
			if (!spec.isFixedSize()) {
				break;
			}
		}
		return matchSize;
	}

	@Override
	public boolean matches(ByteBuffer buffer) {
		boolean matches = false;

		for (FormatSpec spec : this.specs) {
			if (spec.matchSize() == 0) {
				break;
			}
			matches = spec.matches(buffer);
			if (!matches) {
				break;
			}
			if (!spec.isFixedSize()) {
				break;
			}
		}
		return matches;
	}

	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		long decoded = 0L;

		for (FormatSpec spec : this.specs) {
			long specPosition = position + decoded;
			long specDecoded;

			if (spec.isResult()) {
				FileScannerResultBuilder specResult = result.addResult(spec.resultType(), specPosition, spec);

				specDecoded = ResultContext.setupAndDecode(spec, specResult);
				result.updateDecodeStatus(specResult.decodeStatus());
			} else {
				specDecoded = spec.specDecode(result, specPosition);
			}

			DecodeStatusException decodeStatus = result.decodeStatus();

			if (decodeStatus != null && decodeStatus.isFatal()) {
				break;
			}
			recordResultSection(result, specDecoded, spec);
			decoded += specDecoded;
		}
		return decoded;
	}

	@Override
	public void specRender(FileScannerResult result, long start, long end, ResultRenderer renderer)
			throws IOException, InterruptedException {
		long renderPosition = start;
		int sectionIndex = 0;

		for (FormatSpec spec : this.specs) {
			ResultSection section = getResultSectionSize(result, sectionIndex);

			if (section == null) {
				break;
			}

			long nextRenderPosition = renderPosition + section.size();

			assert nextRenderPosition <= end;

			if (!spec.isResult()) {
				spec.specRender(result, renderPosition, nextRenderPosition, renderer);
			}
			renderPosition = nextRenderPosition;
			sectionIndex++;
		}
		if (!renderer.hasOutput()) {
			result.renderDefault(renderer);
		}
		result.renderDecodeStatus(renderer);
		renderer.renderBreakOrClose(isResult());
	}

}
