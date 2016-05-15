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
package de.carne.filescanner.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import de.carne.filescanner.core.format.FormatSpec;
import de.carne.filescanner.spi.FileScannerInput;
import de.carne.filescanner.spi.Format;

/**
 * Helper class used to perform the first match of the known formats to the
 * scanner input.
 */
final class FormatMatcher {

	private final ByteBuffer matchBuffer;

	public FormatMatcher() {
		this.matchBuffer = ByteBuffer.allocateDirect(maxHeaderSize());
	}

	public List<Format> matchFormats(FileScannerInput input, long position) throws IOException {
		this.matchBuffer.clear();
		input.read(this.matchBuffer, position);
		this.matchBuffer.flip();

		Collection<Format> formats = Format.getFormats();
		ArrayList<Format> trailerMatches = new ArrayList<>(formats.size());
		ArrayList<Format> headerMatches = new ArrayList<>(formats.size());
		ArrayList<Format> inputPathMatches = new ArrayList<>(formats.size());
		ArrayList<Format> unknownMatches = new ArrayList<>(formats.size());

		for (Format format : Format.getFormats()) {
			// Record whether this format is an explicit mismatch
			// We consider a format a mismatch if it defines header order
			// trailer specs and none of them matches.
			boolean mismatch = false;
			boolean match = false;

			// Only on position 0 we perform trailer matching
			if (position == 0) {
				for (FormatSpec trailerSpec : format.trailerSpecs()) {
					mismatch = true;
					this.matchBuffer.rewind();

					int matchSize = trailerSpec.matchSize();

					if (0 < matchSize && matchSize <= this.matchBuffer.remaining()) {
						this.matchBuffer.order(format.order());
						this.matchBuffer.position(this.matchBuffer.remaining() - matchSize);
						if (trailerSpec.matches(this.matchBuffer)) {
							trailerMatches.add(format);
							match = true;
							break;
						}
					}
				}
			}
			if (!match) {
				for (FormatSpec headerSpec : format.headerSpecs()) {
					mismatch = true;
					this.matchBuffer.rewind();

					int matchSize = headerSpec.matchSize();

					if (0 < matchSize && matchSize <= this.matchBuffer.remaining()) {
						this.matchBuffer.order(format.order());
						if (headerSpec.matches(this.matchBuffer)) {
							headerMatches.add(format);
							match = true;
							break;
						}
					}
				}
			}
			// Only on position 0 (and for not yet evaluated formats) we perform
			// input name matching
			if (position == 0l && !match && !mismatch) {
				String inputName = input.path().toString();

				for (Pattern inputNamePattern : format.inputNamePatterns()) {
					if (inputNamePattern.matcher(inputName).matches()) {
						inputPathMatches.add(format);
						match = true;
						break;
					}
				}
			}
			if (!match && !mismatch) {
				unknownMatches.add(format);
			}
		}

		ArrayList<Format> matches = new ArrayList<>(formats.size());

		matches.addAll(trailerMatches);
		matches.addAll(matches.size(), headerMatches);
		matches.addAll(matches.size(), inputPathMatches);
		matches.addAll(matches.size(), unknownMatches);
		return matches;
	}

	private static int maxHeaderSize() {
		int maxHeaderSize = 0;

		for (Format format : Format.getFormats()) {
			for (FormatSpec headerSpec : format.headerSpecs()) {
				maxHeaderSize = Math.max(maxHeaderSize, headerSpec.matchSize());
			}
			for (FormatSpec trailerSpec : format.trailerSpecs()) {
				maxHeaderSize = Math.max(maxHeaderSize, trailerSpec.matchSize());
			}
		}
		return maxHeaderSize;
	}

}
