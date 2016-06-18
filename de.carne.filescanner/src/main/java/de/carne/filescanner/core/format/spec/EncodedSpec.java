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
import java.util.function.Supplier;

import de.carne.filescanner.core.DecodeStatusException;
import de.carne.filescanner.core.FileScannerInput;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.FileScannerResultType;
import de.carne.filescanner.core.input.DecodeCache;
import de.carne.filescanner.core.input.DecodeParams;
import de.carne.filescanner.core.transfer.ResultRenderer;
import de.carne.nio.compression.spi.Decoder;
import de.carne.util.logging.Log;

/**
 * Encoded format spec defining a section of encoded data.
 */
public class EncodedSpec extends FormatSpec {

	private static final Log LOG = new Log(EncodedSpec.class);

	private final ValueExpression<DecodeParams> decodeParamsExpression;

	private EncodedSpec(ValueExpression<DecodeParams> factoryExpression) {
		this.decodeParamsExpression = factoryExpression;
	}

	/**
	 * Construct {@code EncodedSpec}.
	 *
	 * @param decodeParams The decode parameters to use for decoding.
	 */
	public EncodedSpec(DecodeParams decodeParams) {
		this(new ValueExpression<>(decodeParams));
	}

	/**
	 * Construct {@code EncodedSpec}.
	 *
	 * @param decodeParamsLambda The expression providing the decode parameters
	 *        to use for decoding.
	 */
	public EncodedSpec(Supplier<DecodeParams> decodeParamsLambda) {
		this(new ValueExpression<>(decodeParamsLambda));
	}

	@SuppressWarnings("resource")
	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		DecodeParams decodeParams = this.decodeParamsExpression.decode();
		long decoded = 0L;

		if (decodeParams != null) {
			Decoder decoder = decodeParams.newDecoder();
			long encodedSize = decodeParams.getEncodedSize();
			FileScannerInput decodedInput;
			Exception decodeStatus;

			if (decoder != null) {
				DecodeCache.Input cachedInput = result.input().scanner().decodeCache().decodeInput(result.input(),
						position, decoder, decodeParams.getDecodedPath());

				decoded = Math.max(decoder.totalIn(), encodedSize);
				decodedInput = cachedInput;
				decodeStatus = cachedInput.decodeStatus();
			} else {
				decoded = Math.max(0L, encodedSize);
				decodedInput = result.input().slice(position, position + decoded, decodeParams.getDecodedPath());
				decodeStatus = null;
			}
			if (encodedSize >= 0 && decoded > encodedSize) {
				LOG.warning(null, "Decoding exceeded the specified encoded size; {0} addional bytes read",
						decoded - encodedSize);
			}
			result.updateTitle(decodeParams.getEncodedName());
			result.addInput(decodedInput);
			result.updateDecodeStatus(DecodeStatusException.fromException(decodeStatus, encodedSize < 0));
		}
		return decoded;
	}

	@Override
	public void specRender(FileScannerResult result, long start, long end, ResultRenderer renderer)
			throws IOException, InterruptedException {
		DecodeParams decodeParams = this.decodeParamsExpression.decode();

		if (decodeParams != null) {
			decodeParams.render(renderer);
		}
		if (isResult()) {
			result.renderDecodeStatus(renderer);
		}
		renderer.renderBreakOrClose(isResult());
	}

	@Override
	public boolean isResult() {
		return true;
	}

	@Override
	public FileScannerResultType resultType() {
		return FileScannerResultType.ENCODED_INPUT;
	}

}
