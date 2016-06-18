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
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.FileScannerResultType;
import de.carne.filescanner.core.transfer.ResultRenderer;

/**
 * Conditional format spec that applies a specific spec depending on the current
 * context.
 */
public class ConditionalSpec extends FormatSpec {

	private final boolean mandatory;

	private final Supplier<FormatSpec> specLambda;

	/**
	 * Construct {@code ConditionalSpec}.
	 *
	 * @param mandatory Controls whether the absence of a spec is considered an
	 *        error or not.
	 * @param specLambda The expression providing the format spec to use.
	 */
	public ConditionalSpec(boolean mandatory, Supplier<FormatSpec> specLambda) {
		assert specLambda != null;

		this.mandatory = mandatory;
		this.specLambda = specLambda;
	}

	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		FormatSpec spec = this.specLambda.get();
		long decoded = 0L;

		if (spec != null) {
			decoded = spec.specDecode(result, position);
		} else if (this.mandatory) {
			result.updateDecodeStatus(DecodeStatusException.fatal(DecodeStatusException.STATUS_NO_DATA_DECODED));
		}
		return decoded;
	}

	@Override
	public long decode(FileScannerResultBuilder result) throws IOException {
		FormatSpec spec = this.specLambda.get();
		long decoded = 0L;

		if (spec != null) {
			decoded = spec.decode(result);
		} else if (this.mandatory) {
			result.updateDecodeStatus(DecodeStatusException.fatal(DecodeStatusException.STATUS_NO_DATA_DECODED));
		}
		return decoded;
	}

	@Override
	public void specRender(FileScannerResult result, long start, long end, ResultRenderer renderer)
			throws IOException, InterruptedException {
		FormatSpec spec = this.specLambda.get();

		if (spec != null) {
			spec.specRender(result, start, end, renderer);
		}
	}

	@Override
	public boolean isResult() {
		FormatSpec spec = this.specLambda.get();

		return (spec != null ? spec.isResult() : false);
	}

	@Override
	public FileScannerResultType resultType() {
		FormatSpec spec = this.specLambda.get();

		return (spec != null ? spec.resultType() : null);
	}

	@Override
	public RenderHandler getResultRenderHandler() {
		FormatSpec spec = this.specLambda.get();

		return (spec != null ? spec.getResultRenderHandler() : null);
	}

}
