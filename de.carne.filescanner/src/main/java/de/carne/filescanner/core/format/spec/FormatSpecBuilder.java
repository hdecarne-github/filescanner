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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.function.Supplier;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.FileScannerResultType;
import de.carne.filescanner.core.format.DecodeContext;
import de.carne.filescanner.core.format.ResultSection;
import de.carne.filescanner.core.transfer.ResultExporter;

/**
 * Base class for building spec based format definitions.
 */
public abstract class FormatSpecBuilder extends FormatSpec {

	private final ArrayList<Attribute<?>> declaredAttributes = new ArrayList<>();

	private StringExpression resultTitleExpression = null;

	private RenderHandler resultRenderHandler = null;

	private final ArrayList<ResultExporter> resultExporters = new ArrayList<>();

	@Override
	public boolean matches(ByteBuffer buffer) {
		int matchSize = checkedMatchSize(buffer);
		boolean matches = matchSize > 0;

		if (matches) {
			buffer.position(buffer.position() + matchSize);
		}
		return matches;
	}

	/**
	 * This function checks whether the prerequisites assumed by
	 * {@linkplain #matches(ByteBuffer)} are met.
	 *
	 * @param buffer The buffer to check.
	 * @return The match size as returned by {@linkplain #matchSize()} or
	 *         {@code 0} if the match size is {@code 0} or the buffer remaining
	 *         bytes are insufficient.
	 */
	protected final int checkedMatchSize(ByteBuffer buffer) {
		int matchSize = matchSize();

		return (matchSize > 0 && isBufferSufficient(buffer, matchSize) ? matchSize : 0);
	}

	@Override
	public long decode(FileScannerResultBuilder result) throws IOException {
		DecodeContext context = DecodeContext.getDecodeContext();

		for (Attribute<?> declaredAttribute : this.declaredAttributes) {
			context.declareAttribute(declaredAttribute);
		}

		long decoded = specDecode(result, result.start());

		if (isResult()) {
			result.updateTitle(this.resultTitleExpression.decode());
		}
		result.addExporters(this.resultExporters);
		return decoded;
	}

	/**
	 * Declare attributes to make them accessible in this spec's result scope.
	 *
	 * @param attributes The attributes to declare.
	 * @return The updated format spec.
	 */
	public final FormatSpecBuilder declareAttributes(Attribute<?>... attributes) {
		this.declaredAttributes.ensureCapacity(this.declaredAttributes.size() + attributes.length);
		for (Attribute<?> attribute : attributes) {
			this.declaredAttributes.add(attribute);
		}
		return this;
	}

	/**
	 * Mark this spec as a scan result.
	 * <p>
	 * Marking spec as a result instructs the caller to decode this spec into
	 * it's own result object.
	 * </p>
	 *
	 * @param title The title to use for the result object.
	 * @return The updated format spec.
	 */
	public final FormatSpecBuilder setResult(String title) {
		this.resultTitleExpression = new StringExpression(title);
		return this;
	}

	/**
	 * Mark this spec as a scan result.
	 * <p>
	 * Marking spec as a result instructs the caller to decode this spec into
	 * it's own result object.
	 * </p>
	 *
	 * @param titleLambda The string expression providing the title to use for
	 *        the result object.
	 * @return The updated format spec.
	 */
	public final FormatSpecBuilder setResult(Supplier<String> titleLambda) {
		this.resultTitleExpression = new StringExpression(titleLambda);
		return this;
	}

	/**
	 * Mark this spec as a scan result.
	 * <p>
	 * Marking spec as a result instructs the caller to decode this spec into
	 * it's own result object.
	 * </p>
	 *
	 * @param pattern The {@linkplain MessageFormat} format pattern for the
	 *        result object's title.
	 * @param titleLambda The string expression providing the format param to
	 *        use during title formatting.
	 * @return The updated format spec.
	 */
	public final FormatSpecBuilder setResult(String pattern, Supplier<String> titleLambda) {
		this.resultTitleExpression = new StringExpression(pattern, titleLambda);
		return this;
	}

	@Override
	public boolean isResult() {
		return this.resultTitleExpression != null;
	}

	@Override
	public FileScannerResultType resultType() {
		return FileScannerResultType.FORMAT;
	}

	/**
	 * Set a custom render handler for result display.
	 *
	 * @param handler The handler to use for result rendering.
	 * @return The updated format spec.
	 */
	public final FormatSpecBuilder setResultRenderHandler(RenderHandler handler) {
		this.resultRenderHandler = handler;
		return this;
	}

	@Override
	public RenderHandler getResultRenderHandler() {
		return this.resultRenderHandler;
	}

	/**
	 * Add an exporter for result export.
	 *
	 * @param exporter The exporter to add.
	 */
	public final void addResultExporter(ResultExporter exporter) {
		assert exporter != null;

		this.resultExporters.add(exporter);
	}

	/**
	 * Record a result section during decoding.
	 *
	 * @param result The corresponding result object.
	 * @param position The result section's position.
	 * @param size The result section's size.
	 * @param spec The result section's spec.
	 */
	protected final void recordResultSection(FileScannerResultBuilder result, long position, long size,
			FormatSpec spec) {
		assert result != null;
		assert result.start() <= position;

		result.decodeContext().recordResultSection(position, size, spec);
	}

	/**
	 * Get a previously recorded result section.
	 *
	 * @param result The corresponding result object.
	 * @param position The position of the result section to retrieve.
	 * @return The result section object or {@code null} if the submitted index
	 *         has not been recorded.
	 */
	protected final ResultSection getResultSectionSize(FileScannerResult result, long position) {
		assert result != null;

		return result.renderContext().getResultSection(position);
	}

	@Override
	public String toString() {
		return "FormatSpec " + (this.resultTitleExpression != null ? this.resultTitleExpression : "<anonymous>");
	}

}
