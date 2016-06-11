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
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.carne.filescanner.core.format.ResultContext;
import de.carne.filescanner.core.transfer.ResultExporter;
import de.carne.filescanner.core.transfer.ResultRenderer;
import de.carne.filescanner.util.Hexadecimal;
import de.carne.filescanner.util.Units;
import de.carne.util.Exceptions;

/**
 * A {@code FileScanner} result object.
 * <p>
 * Results are of different types ({@link FileScannerResultType}) and ordered
 * hierarchically. The resulting structure is:
 *
 * <pre>
 *  Input               	An input
 *  |
 *  +-Format            	An identified format/file structure
 *    |
 *    +-Format   	   		A format/file structure can contain others.
 *      |
 *      +-Encoded (Input)	An encoded data stream (optional)
 *        |
 *        +-Input       	The input data representing the decoded data stream
 *          |
 *          +-...       	Goes on recursively
 * </pre>
 * </p>
 */
public abstract class FileScannerResult {

	/**
	 * Standard {@linkplain Comparator} for comparing results according to their
	 * position.
	 * <p>
	 * The compared results must be associates with the same parent result.
	 * </p>
	 */
	private static final Comparator<FileScannerResult> POSITION_COMPARATOR = new Comparator<FileScannerResult>() {

		@Override
		public int compare(FileScannerResult o1, FileScannerResult o2) {
			return compareResultPositions(o1, o2);
		}

	};

	static int compareResultPositions(FileScannerResult o1, FileScannerResult o2) {
		assert Objects.equals(o1.parent(), o2.parent());

		return Long.signum(o1.start - o2.start);
	}

	private final FileScannerResultType type;

	private final FileScannerInput input;

	private final ByteOrder order;

	private final long start;

	private final ResultContext context = new ResultContext() {

		@Override
		protected ResultContext parent() {
			FileScannerResult parentResult = FileScannerResult.this.parent();

			return (parentResult != null ? parentResult.context() : null);
		}

	};

	private final ArrayList<FileScannerResult> children = new ArrayList<>();

	private final ArrayList<? extends ResultExporter> exporters = new ArrayList<>();

	private Object data;

	FileScannerResult(FileScannerResultType type, FileScannerInput input, ByteOrder order, long start) {
		assert type != null;
		assert input != null;
		assert order != null;
		assert start >= 0;

		this.type = type;
		this.input = input;
		this.order = order;
		this.start = start;
	}

	synchronized void addChild(FileScannerResult child) {
		this.children.add(child);
		this.children.sort(POSITION_COMPARATOR);
	}

	synchronized void setChildren(List<FileScannerResult> children) {
		this.children.addAll(children);
	}

	synchronized long getChildrenEnd() {
		int childrenCount = this.children.size();

		return (childrenCount > 0 ? this.children.get(childrenCount - 1).end() : this.start);
	}

	/**
	 * Get the result type.
	 *
	 * @return The result type.
	 */
	public final FileScannerResultType type() {
		return this.type;
	}

	/**
	 * Get the result object's underlying input object.
	 *
	 * @return The result object's underlying input object.
	 */
	public final FileScannerInput input() {
		return this.input;
	}

	/**
	 * Get the result object's byte order.
	 *
	 * @return The result object's byte order.
	 */
	public final ByteOrder order() {
		return this.order;
	}

	/**
	 * Get the result's start position within the input object.
	 *
	 * @return The result's start position within the input object.
	 */
	public final long start() {
		return this.start;
	}

	/**
	 * Get the result's end position within the input object.
	 *
	 * @return The result's end position within the input object.
	 */
	public abstract long end();

	/**
	 * Get the result's size.
	 *
	 * @return The result's size.
	 */
	public final long size() {
		return this.end() - this.start;
	}

	/**
	 * Get the result's context.
	 *
	 * @return The result's context.
	 */
	public final ResultContext context() {
		return this.context;
	}

	/**
	 * Get the result's decode status.
	 *
	 * @return {@code null} if the decoding was successful or the decode status
	 *         exception otherwise.
	 */
	public abstract DecodeStatusException decodeStatus();

	/**
	 * Get the parent result.
	 *
	 * @return The parent result or {@code null} if this is a root result.
	 */
	public abstract FileScannerResult parent();

	/**
	 * Get the result's children count.
	 *
	 * @return The result's children count.
	 */
	public final synchronized int childrenCount() {
		return this.children.size();
	}

	/**
	 * Get the result's children.
	 * <p>
	 * The returned children are ordered according to their position.
	 * </p>
	 *
	 * @return The result's children.
	 */
	public final synchronized List<FileScannerResult> children() {
		return new ArrayList<>(this.children);
	}

	/**
	 * Get the result's title.
	 * <p>
	 * The title can be used to display the result.
	 * </p>
	 *
	 * @return The result's title.
	 */
	public abstract String title();

	/**
	 * Get the result's exporters.
	 *
	 * @param exporterClass The type of exporters to return.
	 * @return The available exporters.
	 */
	public synchronized <T extends ResultExporter> List<T> getExporters(Class<T> exporterClass) {
		ArrayList<T> matchingExporters = new ArrayList<>();

		for (ResultExporter exporter : this.exporters) {
			if (exporterClass.isInstance(exporter)) {
				matchingExporters.add(exporterClass.cast(exporter));
			}
		}
		return matchingExporters;
	}

	/**
	 * Set application data for this result.
	 *
	 * @param data The data to set.
	 */
	public synchronized final <T> void setData(T data) {
		this.data = data;
	}

	/**
	 * Get previously set application data.
	 *
	 * @param dataType The application data type.
	 * @return The application data.
	 * @see #setData(Object)
	 */
	public synchronized final <T> T getData(Class<T> dataType) {
		return dataType.cast(this.data);
	}

	/**
	 * Map a position to the nearest result.
	 *
	 * @param position The position to map.
	 * @return The mapped result or {@code null} if the position is not located
	 *         within this result.
	 */
	public synchronized FileScannerResult mapPosition(long position) {
		FileScannerResult mappedResult = null;

		if (this.start <= position && position < end()) {
			mappedResult = this;
			if (this.type != FileScannerResultType.ENCODED_INPUT) {
				int startIndex = 0;
				int endIndex = this.children.size();

				while (true) {
					if (startIndex == endIndex) {
						break;
					}

					int medianIndex = startIndex + (endIndex - startIndex) / 2;
					FileScannerResult medianChild = this.children.get(medianIndex);
					long medianChildStart = medianChild.start;
					long medianChildEnd = medianChild.end();

					if (medianChildStart <= position && position < medianChildEnd) {
						mappedResult = medianChild.mapPosition(position);
						break;
					} else if (position < medianChildStart) {
						endIndex = medianIndex;
					} else {
						startIndex = medianIndex + 1;
					}
				}
			}
		}
		return mappedResult;
	}

	/**
	 * Render the result for user display.
	 *
	 * @param renderer The renderer to use.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if he render thread is interrupted.
	 */
	public void render(ResultRenderer renderer) throws IOException, InterruptedException {
		renderDefault(renderer);
		renderer.close();
	}

	/**
	 * Render the current decode status (if not {@code null}).
	 *
	 * @param renderer The renderer to use.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if he render thread is interrupted.
	 */
	public void renderDecodeStatus(ResultRenderer renderer) throws IOException, InterruptedException {
		DecodeStatusException decodeStatus = decodeStatus();

		if (decodeStatus != null && !decodeStatus.isNested()) {
			if (renderer.hasOutput()) {
				renderer.renderBreak();
			}
			renderer.setLabelMode().renderText("--- Decoding failure ---").renderBreak();
			renderer.setErrorMode().renderText(Exceptions.toMessage(decodeStatus));
		}
	}

	/**
	 * Render the result for user display using the default rendering.
	 * <p>
	 * The default entering simply displays the result attributes (position,
	 * size, ...).
	 * </p>
	 *
	 * @param renderer The renderer to use.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if he render thread is interrupted.
	 */
	public void renderDefault(ResultRenderer renderer) throws IOException, InterruptedException {
		if (renderer.hasOutput()) {
			renderer.renderBreak();
		}
		renderer.setNormalMode().renderText("start");
		renderer.setOperatorMode().renderText(" = ");
		renderer.setValueMode().renderText(Hexadecimal.formatL(new StringBuilder("0x"), start()).toString());
		renderer.renderBreak();
		renderer.setNormalMode().renderText("end");
		renderer.setOperatorMode().renderText(" = ");
		renderer.setValueMode().renderText(Hexadecimal.formatL(new StringBuilder("0x"), end()).toString());
		renderer.renderBreak();
		renderer.setNormalMode().renderText("size");
		renderer.setOperatorMode().renderText(" = ");
		renderer.setValueMode().renderText(Units.formatByteValue(size()));
		renderDecodeStatus(renderer);
	}

	/**
	 * Read and cache data from the result's input.
	 *
	 * @param position The position to read from.
	 * @param size The number of bytes to read.
	 * @return A byte buffer containing the read bytes. If the input end was
	 *         reached during the read operation the buffer may contain less
	 *         bytes than requested.
	 * @throws IOException if an I/O error occurs.
	 * @see FileScannerInput#cachedRead(long, int, ByteOrder)
	 */
	public ByteBuffer cachedRead(long position, int size) throws IOException {
		return this.input.cachedRead(position, size, this.order);
	}

	@Override
	public String toString() {
		return title();
	}

}
