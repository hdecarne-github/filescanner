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

import java.nio.ByteOrder;

import de.carne.filescanner.spi.FileScannerInput;
import de.carne.filescanner.spi.Format;

/**
 * This class is used to build up the scan result hierarchy for a single format.
 * <p>
 * Result objects stored in the buffer are not required to be complete from the
 * beginning. Furthermore several of the later immutable attributes can still be
 * changed while the result object is still in the buffer.
 * </p>
 */
public final class FileScannerResultBuilder extends FileScannerResult {

	private long end;

	private final FileScannerResultBuilder parent;

	private final ByteOrder order;

	private String title;

	/**
	 * Construct {@code FileScannerResultBuilder}.
	 *
	 * @param format The format to decode.
	 * @param input The result object's input.
	 * @param start The result object's start position.
	 */
	public FileScannerResultBuilder(Format format, FileScannerInput input, long start) {
		this(null, FileScannerResultType.FORMAT, input, start, start, format.order(), format.name());
	}

	private FileScannerResultBuilder(FileScannerResultBuilder parent, FileScannerResultType type,
			FileScannerInput input, long start, long end, ByteOrder order, String title) {
		super(type, input, start);
		this.end = end;
		this.parent = parent;
		if (this.parent != null) {
			this.parent.addChild(this);
		}
		this.order = order;
		this.title = title;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerResult#end()
	 */
	@Override
	public long end() {
		return Math.max(this.end, getChildrenEnd());
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerResult#parent()
	 */
	@Override
	public FileScannerResult parent() {
		return this.parent;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerResult#title()
	 */
	@Override
	public String title() {
		return this.title;
	}

	/**
	 * Set the result object's title.
	 *
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		assert title != null;

		this.title = title;
	}

	/**
	 * Add a new result to the builder.
	 *
	 * @param type The result type to build up.
	 * @param resultStart The result object's start position.
	 * @return The builder for the added result object.
	 */
	public FileScannerResultBuilder addResult(FileScannerResultType type, long resultStart) {
		return addResult(type, resultStart, resultStart);
	}

	/**
	 * Add a new result to the builder.
	 *
	 * @param type The result type to build up.
	 * @param resultStart The result object's start position.
	 * @param resultEnd The result object's end position.
	 * @return The builder for the added result object.
	 */
	public FileScannerResultBuilder addResult(FileScannerResultType type, long resultStart, long resultEnd) {
		assert type() != FileScannerResultType.INPUT;
		assert type != null;
		assert start() <= resultStart;
		assert resultStart <= resultEnd;

		return new FileScannerResultBuilder(this, type, input(), resultStart, resultEnd, this.order, "");
	}

	/**
	 * Adds all result object's collected in the buffer to the scan results.
	 * <p>
	 * During this step also all identified nested input data streams are
	 * submitted for scanning.
	 * </p>
	 *
	 * @param result The result object to add the buffered results to.
	 * @return The decoded result object.
	 */
	public FileScannerResult toResult(FileScannerResult result) {
		FileScannerResult decoded = null;

		if (size() > 0) {
			decoded = new SimpleFileScannerResult(result, type(), input(), start(), end(), this.title);
			for (FileScannerResult child : children()) {
				FileScannerResultBuilder childBuilder = ((FileScannerResultBuilder) child);

				childBuilder.toResult(decoded);
			}
		}
		return decoded;
	}

}
