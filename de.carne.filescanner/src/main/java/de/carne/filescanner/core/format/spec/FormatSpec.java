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

import de.carne.filescanner.core.FileScannerInput;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultBuilder;
import de.carne.filescanner.core.FileScannerResultType;
import de.carne.filescanner.core.format.Decodable;
import de.carne.filescanner.core.format.RenderableData;
import de.carne.filescanner.core.transfer.ResultRenderer;

/**
 * Base class for spec based format definitions.
 */
public abstract class FormatSpec implements Decodable, RenderableData {

	/**
	 * Interface for spec based custom result rendering.
	 */
	public interface RenderHandler {

		/**
		 * Render the scan result.
		 *
		 * @param spec The spec used to decode the result.
		 * @param result The result object to render.
		 * @param renderer The renderer to use.
		 * @throws IOException if an I/O error occurs.
		 * @throws InterruptedException if the render thread was interrupted.
		 */
		public void render(FormatSpec spec, FileScannerResult result, ResultRenderer renderer)
				throws IOException, InterruptedException;

	}

	/**
	 * Check whether this spec's data size is fixed or variable.
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
		return false;
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

	@Override
	public long decode(FileScannerResultBuilder result) throws IOException {
		assert isResult();

		return specDecode(result, result.start());
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
	public abstract void specRender(FileScannerResult result, long start, long end, ResultRenderer renderer)
			throws IOException, InterruptedException;

	@Override
	public void renderData(FileScannerResult result, long start, long end, ResultRenderer renderer)
			throws IOException, InterruptedException {
		specRender(result, start, end, renderer);
	}

	@Override
	public void render(FileScannerResult result, ResultRenderer renderer) throws IOException, InterruptedException {
		assert isResult();

		RenderHandler renderHandler = getResultRenderHandler();

		if (renderHandler != null) {
			renderHandler.render(this, result, renderer);
		} else {
			specRender(result, result.start(), result.end(), renderer);
		}
	}

	/**
	 * Check whether this spec represents a scan result or not.
	 *
	 * @return {@code true} if this spec has been set as a result.
	 */
	public boolean isResult() {
		return false;
	}

	/**
	 * Get this's specs result type.
	 *
	 * @return This's specs result type or {@code null} if this spec does not
	 *         represent a scan result.
	 */
	public FileScannerResultType resultType() {
		return null;
	}

	/**
	 * Get the custom render handler for result display.
	 *
	 * @return The handler or {@code null} if none has been set or if this spec
	 *         does not represent a scan result.
	 */
	public RenderHandler getResultRenderHandler() {
		return null;
	}

	/**
	 * Check whether a buffer contains sufficient data.
	 *
	 * @param buffer The buffer to check.
	 * @param size The required number of remaining bytes.
	 * @return {@code true} if the buffer contains the required number of bytes
	 *         or more.
	 */
	protected static final boolean isBufferSufficient(ByteBuffer buffer, int size) {
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
	protected static final boolean isInputSufficient(FileScannerInput input, long position, long size)
			throws IOException {
		assert input != null;
		assert position >= 0;
		assert size >= 0;

		return (position + size) <= input.size();
	}

}
