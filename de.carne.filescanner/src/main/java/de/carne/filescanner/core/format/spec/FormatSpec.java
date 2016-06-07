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

import de.carne.filescanner.core.FileScannerInput;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.FileScannerResultType;
import de.carne.filescanner.core.format.Decodable;
import de.carne.filescanner.core.format.RenderableData;
import de.carne.filescanner.core.format.ResultContext;
import de.carne.filescanner.core.format.ResultSection;
import de.carne.filescanner.core.transfer.FileScannerResultRenderer;

/**
 * Base class for spec based format definitions.
 */
public abstract class FormatSpec implements Decodable, RenderableData {

	private final ArrayList<Attribute<?>> declaredAttributes = new ArrayList<>();

	private StringExpression resultTitleExpression = null;

	/**
	 * Interface for custom result rendering.
	 */
	public interface RenderHandler {

		/**
		 * Render the scanner result.
		 *
		 * @param spec The format spec used to decode the result.
		 * @param result The result object to render.
		 * @param renderer The renderer to use.
		 * @throws IOException if an I/O error occurs.
		 * @throws InterruptedException if the render thread was interrupted.
		 */
		public void render(FormatSpec spec, FileScannerResult result, FileScannerResultRenderer renderer)
				throws IOException, InterruptedException;

	}

	private RenderHandler resultRenderHandler = null;

	/**
	 * Whether this spec's data size is fixed or format dependent.
	 * <p>
	 * If this function returns {@code true} the {@linkplain #matchSize()}
	 * function can be used to get the actual size.
	 * </p>
	 *
	 * @return {@code true} if this spec's data size is fixed.
	 */
	public boolean isFixedSize() {
		return false;
	}

	/**
	 * Get this spec's match size.
	 * <p>
	 * The match size defines the number of bytes required by this spec to
	 * perform a data match.
	 * </p>
	 *
	 * @return This spec's match size or {@code 0} if matching is not supported.
	 */
	public int matchSize() {
		return 0;
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

		return (matchSize > 0 && isSA(buffer, matchSize) ? matchSize : 0);
	}

	/**
	 * Match a {@linkplain ByteBuffer} against this spec.
	 * <p>
	 * Matching will always fail if this spec's match size (as returned by
	 * {@linkplain #matchSize()}) is {@code 0} or exceeds the submitted buffer's
	 * remaining bytes.<br/>
	 * If this prerequisites are met the buffer data will be checked against the
	 * spec. If the buffer matches the spec the buffer's position will advance
	 * the match size. Otherwise the buffer's position is undefined.
	 * </p>
	 *
	 * @param buffer The buffer containing the data to match.
	 * @return {@code true} if the buffer's data matches.
	 */
	public boolean matches(ByteBuffer buffer) {
		int matchSize = checkedMatchSize(buffer);
		boolean matches = matchSize > 0;

		if (matches) {
			buffer.position(buffer.position() + matchSize);
		}
		return matches;
	}

	/**
	 * Get this's specs result type.
	 *
	 * @return This's specs result type.
	 */
	public FileScannerResultType resultType() {
		return FileScannerResultType.FORMAT;
	}

	/**
	 * Decode results by interpreting this spec.
	 *
	 * @param result The result builder to decode into.
	 * @param position The position to start decoding at.
	 * @return The number of decoded bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract long specDecode(FileScannerResultBuilder result, long position) throws IOException;

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.Decodable#decode(de.carne.filescanner.
	 * core.FileScannerResultBuilder)
	 */
	@Override
	public long decode(FileScannerResultBuilder result) throws IOException {
		ResultContext context = ResultContext.get();

		for (Attribute<?> declaredAttribute : this.declaredAttributes) {
			context.declareAttribute(declaredAttribute);
		}

		long decoded = specDecode(result, result.start());

		result.updateTitle(this.resultTitleExpression.decode());
		return decoded;
	}

	/**
	 * Render previously decoded data by interpreting this spec.
	 *
	 * @param result The result object containing the data to render.
	 * @param start The start position of the data to render.
	 * @param end The end position of the data to render.
	 * @param renderer The renderer to use.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public abstract void specRender(FileScannerResult result, long start, long end, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException;

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.RenderableData#renderData(de.carne.
	 * filescanner.core.FileScannerResult, long, long,
	 * de.carne.filescanner.spi.FileScannerResultRenderer)
	 */
	@Override
	public void renderData(FileScannerResult result, long start, long end, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException {
		assert result.start() <= start;
		assert start <= end;
		assert end <= result.end();

		specRender(result, start, end, renderer);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.Renderable#render(de.carne.filescanner.
	 * core.FileScannerResult,
	 * de.carne.filescanner.spi.FileScannerResultRenderer)
	 */
	@Override
	public void render(FileScannerResult result, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException {
		if (this.resultRenderHandler != null) {
			this.resultRenderHandler.render(this, result, renderer);
		} else {
			specRender(result, result.start(), result.end(), renderer);
		}
	}

	/**
	 * Declare an attribute to make it accessible in this spec's result scope.
	 *
	 * @param attribute The attribute to declare.
	 * @return The updated format spec.
	 */
	public final FormatSpec declareAttribute(Attribute<?> attribute) {
		assert attribute != null;

		this.declaredAttributes.add(attribute);
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
	public final FormatSpec setResult(String title) {
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
	public final FormatSpec setResult(Supplier<String> titleLambda) {
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
	public final FormatSpec setResult(String pattern, Supplier<String> titleLambda) {
		this.resultTitleExpression = new StringExpression(pattern, titleLambda);
		return this;
	}

	/**
	 * Check whether this spec has been set as result.
	 *
	 * @return {@code true} if this spec has been set as a result.
	 */
	public final boolean isResult() {
		return this.resultTitleExpression != null;
	}

	/**
	 * Set a custom render handler for result display.
	 *
	 * @param handler The handler to use for result rendering.
	 * @return The updated format spec.
	 */
	public final FormatSpec setResultRenderHandler(RenderHandler handler) {
		this.resultRenderHandler = handler;
		return this;
	}

	/**
	 * Get the custom render handler for result display.
	 *
	 * @return The handler or {@code null} if none has been set.
	 */
	public final RenderHandler getResultRenderHandler() {
		return this.resultRenderHandler;
	}

	/**
	 * Record a result section during decoding.
	 *
	 * @param result The corresponding result object.
	 * @param position The result section's position.
	 * @param size The result section's size.
	 * @param spec The result section's spec.
	 */
	protected final void recordResultSection(FileScannerResult result, long size, FormatSpec spec) {
		assert size >= 0;
		assert spec != null;

		result.context().recordResultSection(size, spec);
	}

	/**
	 * Get a previously recorded result section.
	 *
	 * @param result The corresponding result object.
	 * @param index The index of the result section to retrieve.
	 * @return The result section object or {@code null} if the submitted index
	 *         has not been recorded.
	 */
	protected final ResultSection getResultSectionSize(FileScannerResult result, int index) {
		assert result != null;

		return result.context().getResultSection(index);
	}

	/**
	 * Check whether a buffer contains sufficient data.
	 *
	 * @param buffer The buffer to check.
	 * @param size The required number of remaining bytes.
	 * @return {@code true} if the buffer contains the required number of bytes
	 *         or more.
	 */
	protected static final boolean isSA(ByteBuffer buffer, int size) {
		assert buffer != null;
		assert size >= 0;

		return size <= buffer.remaining();
	}

	/**
	 * Check whether an input contains sufficient data.
	 *
	 * @param input The input to check.
	 * @param position The input position to check with.
	 * @param size The required number of remaining bytes.
	 * @return {@code true} if the input contains the required number of bytes
	 *         or more.
	 * @throws IOException if an I/O error occurs.
	 */
	protected static final boolean isSA(FileScannerInput input, long position, long size) throws IOException {
		assert input != null;
		assert position >= 0;
		assert size >= 0;

		return (position + size) <= input.size();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FormatSpec " + (this.resultTitleExpression != null ? this.resultTitleExpression : "<anonymous>");
	}

}
