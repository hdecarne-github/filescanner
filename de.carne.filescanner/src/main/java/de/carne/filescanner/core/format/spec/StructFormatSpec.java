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

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.format.ResultContext;
import de.carne.filescanner.spi.FileScannerResultRenderer;

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

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.FormatSpec#matchSize()
	 */
	@Override
	public int matchSize() {
		int matchSize = 0;

		for (FormatSpec spec : this.specs) {
			int specMatchSize = spec.matchSize();

			if (specMatchSize == 0) {
				break;
			}
			matchSize += specMatchSize;
		}
		return matchSize;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.FormatSpec#matches(java.nio.ByteBuffer)
	 */
	@Override
	public boolean matches(ByteBuffer buffer) {
		assert buffer != null;

		int matchCount = 0;

		for (FormatSpec spec : this.specs) {
			if (spec.matchSize() == 0 || !spec.matches(buffer)) {
				break;
			}
			matchCount++;
		}
		return matchCount > 0;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.FormatSpec#specDecode(de.carne.
	 * filescanner. core.FileScannerResultBuilder, long)
	 */
	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		assert result != null;
		assert position >= 0;

		long decoded = 0L;

		for (FormatSpec spec : this.specs) {
			long specPosition = position + decoded;

			if (spec.isResult()) {
				FileScannerResultBuilder specResult = result.addResult(spec.resultType(), specPosition, spec);

				decoded += ResultContext.setupAndDecode(spec, specResult);
			} else {
				decoded += spec.specDecode(result, specPosition);
			}
		}
		return decoded;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.FormatSpec#specRender(de.carne.
	 * filescanner.core.FileScannerResult, long,
	 * de.carne.filescanner.spi.FileScannerResultRenderer)
	 */
	@Override
	public long specRender(FileScannerResult result, long position, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException {
		assert result != null;
		assert result.start() <= position;
		assert position < result.end();
		assert renderer != null;

		long rendered = 0L;

		for (FormatSpec spec : this.specs) {
			if (!spec.isResult()) {
				long renderPosition = position + rendered;

				rendered += spec.specRender(result, renderPosition, renderer);
			}
		}
		if (rendered == 0L) {
			result.renderDefault(renderer);
		}
		return rendered;
	}

}
