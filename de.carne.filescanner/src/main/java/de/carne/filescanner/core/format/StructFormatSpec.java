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
package de.carne.filescanner.core.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import de.carne.filescanner.core.FileScannerResultBuilder;

/**
 * Struct format spec: Define a consecutive list of format specs.
 */
public class StructFormatSpec extends FormatSpec {

	private ArrayList<FormatSpec> specs = new ArrayList<>();

	/**
	 * Append a format spec.
	 *
	 * @param spec The spec to append.
	 * @return The updated struct spec.
	 */
	public StructFormatSpec append(FormatSpec spec) {
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
		boolean matches = this.specs.size() > 0 && this.specs.get(0).matchSize() > 0;

		for (FormatSpec spec : this.specs) {
			if (spec.matchSize() == 0) {
				break;
			}
			if (!spec.matches(buffer)) {
				matches = false;
				break;
			}
		}
		return matches;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.FormatSpec#eval(de.carne.filescanner.
	 * core.FileScannerResultBuilder, long)
	 */
	@Override
	public long eval(FileScannerResultBuilder result, long position) throws IOException {
		long evaluated = 0l;

		for (FormatSpec spec : this.specs) {
			long specPosition = position + evaluated;
			Decodable specDecodable = spec.getDecodable();

			if (specDecodable != null) {
				FileScannerResultBuilder specResult = result.addResult(spec.resultType(), specPosition,
						specPosition + matchSize());

				evaluated += DecodeContext.setupContextAndDecode(specDecodable, specResult, specPosition);
			} else {
				evaluated += spec.eval(result, specPosition);
			}
		}
		return evaluated;
	}

	/**
	 * Set this spec's {@code Decodable} service to the default one.
	 *
	 * @return The updated struct spec.
	 */
	public StructFormatSpec setDecodable() {
		setDecodable(new Decodable() {

			@Override
			public long decode(FileScannerResultBuilder result, long position) throws IOException {
				return decodeStructSpec(result, position);
			}

		});
		return this;
	}

	long decodeStructSpec(FileScannerResultBuilder result, long position) throws IOException {
		long evaluated = eval(result, position);

		return evaluated;
	}

}
