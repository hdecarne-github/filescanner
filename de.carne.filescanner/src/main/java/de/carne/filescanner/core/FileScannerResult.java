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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.carne.filescanner.spi.FileScannerInput;
import de.carne.filescanner.spi.FileScannerResultRenderer;
import de.carne.filescanner.util.Hexadecimal;

/**
 * A {@code FileScanner} result object.
 * <p>
 * Results are of different types ({@link FileScannerResultType}) and ordered
 * hierarchically. The resulting pattern is:
 *
 * <pre>
 *  Input               An input
 *  |
 *  +-Format            An identified file format
 *    |
 *    +-Sub-format      A sub-format used by the file format (optional)
 *      |
 *      +-Encoded       An encoded data stream (optional)
 *        |
 *        +-Input       The input data representing the decoded data stream
 *          |
 *          +-...       Goes on recursively
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
	private static final Comparator<FileScannerResult> COMPARATOR = new Comparator<FileScannerResult>() {

		@Override
		public int compare(FileScannerResult o1, FileScannerResult o2) {
			assert Objects.equals(o1.parent(), o2.parent());

			return Long.signum(o1.start() - o2.start());
		}

	};

	private final FileScannerResultType type;

	private final FileScannerInput input;

	private final long start;

	private long end;

	private FileScannerResult parent = null;

	private final ArrayList<FileScannerResult> children = new ArrayList<>();

	FileScannerResult(FileScannerResult parent, FileScannerResultType type, FileScannerInput input, long start,
			long end) {
		assert type != null;
		assert input != null;
		assert start <= end;

		this.type = type;
		this.input = input;
		this.start = start;
		this.end = end;
		if (parent != null) {
			parent.addChild(this);
		}
	}

	synchronized void addChild(FileScannerResult child) {
		child.parent = this;
		this.children.add(child);
		this.children.sort(COMPARATOR);
		this.end = Math.max(this.end, this.children.get(this.children.size() - 1).end);
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
	public synchronized final long end() {
		long realEnd = this.end;
		int childrenCount = this.children.size();

		if (this.type != FileScannerResultType.INPUT && childrenCount > 0) {
			realEnd = this.children.get(childrenCount - 1).end();
		}
		return realEnd;
	}

	/**
	 * Get the result's size.
	 *
	 * @return The result's size.
	 */
	public final long size() {
		return this.end - this.start;
	}

	/**
	 * Get the parent result.
	 *
	 * @return The parent result or {@code null} if this is a root result.
	 */
	public final FileScannerResult parent() {
		return this.parent;
	}

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
	public abstract String getTitle();

	/**
	 * Render the result for user display.
	 * <p>
	 * The default implementation simply displays the result's position and
	 * size.
	 * </p>
	 *
	 * @param renderer The renderer to use.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if he render thread is interrupted.
	 */
	public void render(FileScannerResultRenderer renderer) throws IOException, InterruptedException {
		renderer.setNormalMode().renderText("start");
		renderer.setOperatorMode().renderText(" = ");
		renderer.setValueMode().renderText(Hexadecimal.formatL(new StringBuilder(), start()).toString());
		renderer.renderBreak();
		renderer.setNormalMode().renderText("end");
		renderer.setOperatorMode().renderText(" = ");
		renderer.setValueMode().renderText(Hexadecimal.formatL(new StringBuilder(), end()).toString());
		renderer.renderBreak();
		renderer.setNormalMode().renderText("size");
		renderer.setOperatorMode().renderText(" = ");
		renderer.setValueMode().renderText(Hexadecimal.formatL(new StringBuilder(), size()).toString());
		renderer.close();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getTitle();
	}

}
