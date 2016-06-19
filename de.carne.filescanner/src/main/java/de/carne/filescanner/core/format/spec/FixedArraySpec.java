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
import java.util.function.Supplier;

import de.carne.filescanner.core.DecodeStatusException;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.format.DecodeContext;
import de.carne.filescanner.core.transfer.ResultRenderer;

/**
 * Fixed-length array of format specs.
 * <p>
 * The actual length of the array has to be defined statically or via a Lambda
 * expression.
 * </p>
 */
public class FixedArraySpec extends FormatSpecBuilder {

	/**
	 * The maximum size to use for data matching.
	 */
	public static final int MAX_MATCH_SIZE = 1024;

	private final FormatSpec spec;

	private final NumberExpression<?> lengthExpression;

	private FixedArraySpec(FormatSpec spec, NumberExpression<?> lengthExpression) {
		assert spec != null;

		this.spec = spec;
		this.lengthExpression = lengthExpression;
	}

	/**
	 * Construct {@code FixedArraySpec}.
	 *
	 * @param spec The spec defining the array elements.
	 * @param length The static array length.
	 */
	public FixedArraySpec(FormatSpec spec, Number length) {
		this(spec, new NumberExpression<>(length));
	}

	/**
	 * Construct {@code FixedArraySpec}.
	 *
	 * @param spec The spec defining the array elements.
	 * @param lengthLambda The expression providing the array length.
	 */
	public FixedArraySpec(FormatSpec spec, Supplier<? extends Number> lengthLambda) {
		this(spec, new NumberExpression<>(lengthLambda));
	}

	@Override
	public boolean isFixedSize() {
		Number lengthValue = this.lengthExpression.beforeDecode();

		return lengthValue != null && this.spec.isFixedSize()
				&& (lengthValue.longValue() * this.spec.matchSize() < MAX_MATCH_SIZE);
	}

	@Override
	public int matchSize() {
		int length = this.lengthExpression.beforeDecode().intValue();

		return length * this.spec.matchSize();
	}

	@Override
	public boolean matches(ByteBuffer buffer) {
		int length = this.lengthExpression.beforeDecode().intValue();
		boolean matches = true;

		for (int index = 0; index < length; index++) {
			matches = this.spec.matches(buffer);
			if (!matches) {
				break;
			}
		}
		return matches;
	}

	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		long remaining = this.lengthExpression.decode().longValue();
		long decoded = 0;

		while (remaining > 0) {
			long specPosition = position + decoded;
			long specDecoded;

			if (this.spec.isResult()) {
				FileScannerResultBuilder specResult = result.addResult(this.spec.resultType(), specPosition, this.spec);

				specDecoded = DecodeContext.setupAndDecode(this.spec, specResult);
				result.updateDecodeStatus(specResult.decodeStatus());
			} else {
				specDecoded = this.spec.specDecode(result, specPosition);
			}
			DecodeStatusException decodeStatus = result.decodeStatus();

			if (decodeStatus != null && decodeStatus.isFatal()) {
				break;
			}
			decoded += specDecoded;
			remaining--;
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
