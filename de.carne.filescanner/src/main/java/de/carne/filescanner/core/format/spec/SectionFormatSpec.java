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

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.transfer.ResultRenderer;

/**
 * Format spec for defining simple format sections.
 */
public class SectionFormatSpec extends FormatSpec {

	private final NumberExpression<?> sizeExpression;

	/**
	 * Construct {@code SectionFormatSpec}.
	 *
	 * @param size The static section size.
	 */
	public SectionFormatSpec(Number size) {
		assert size != null;

		this.sizeExpression = new NumberExpression<>(size);
	}

	/**
	 * Construct {@code SectionFormatSpec}.
	 *
	 * @param sizeLambda The expression providing the section size.
	 */
	public SectionFormatSpec(Supplier<? extends Number> sizeLambda) {
		assert sizeLambda != null;

		this.sizeExpression = new NumberExpression<>(sizeLambda);
	}

	@Override
	public long specDecode(FileScannerResultBuilder result, long position) throws IOException {
		return this.sizeExpression.decode().longValue();
	}

	@Override
	public void specRender(FileScannerResult result, long start, long end, ResultRenderer renderer)
			throws IOException, InterruptedException {
		result.renderDefault(renderer);
		renderer.renderBreakOrClose(isResult());
	}

}
