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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.carne.filescanner.core.transfer.FileScannerResultView;

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

	private final FileScannerResultType type;

	private final FileScannerInput input;

	private final long start;

	private long end;

	private FileScannerResult parent;

	private final ArrayList<FileScannerResult> children = new ArrayList<>();

	FileScannerResult(FileScannerResultType type, FileScannerInput input, long start, long end,
			FileScannerResult parent) {
		assert type != null;
		assert input != null;
		assert start < end;

		this.type = type;
		this.input = input;
		this.start = start;
		this.end = end;
		this.parent = parent;
		if (this.parent != null) {
			this.parent.addChild(this);
		}
	}

	synchronized void addChild(FileScannerResult child) {
		child.parent = this;
		this.children.add(child);
		this.children.sort(new Comparator<FileScannerResult>() {

			@Override
			public int compare(FileScannerResult o1, FileScannerResult o2) {
				return childCompare(o1, o2);
			}

		});
		this.end = Math.max(this.end, this.children.get(this.children.size() - 1).end);
	}

	static int childCompare(FileScannerResult o1, FileScannerResult o2) {
		return Long.signum(o1.start != o2.start ? o1.start - o2.start : o1.end - o2.end);
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
	public final long end() {
		return this.end;
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
	 * Get the result's data view.
	 * <p>
	 * The data view provides a detailed representation of the result's data.
	 * </p>
	 *
	 * @return The result's data view.
	 */
	public abstract FileScannerResultView getView();

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getTitle();
	}

}
