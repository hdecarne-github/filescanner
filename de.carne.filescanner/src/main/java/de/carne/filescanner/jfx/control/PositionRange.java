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
package de.carne.filescanner.jfx.control;

import de.carne.filescanner.util.Hex;

/**
 * Class used to represent a position range within a file (or any other
 * continuous data range).
 */
public final class PositionRange {

	private long start;

	private long end;

	/**
	 * Construct {@code PositionRange}.
	 */
	public PositionRange() {
		this(0l, 0l);
	}

	/**
	 * Construct {@code PositionRange}.
	 * 
	 * @param range The initial range (may be {@code null}).
	 */
	public PositionRange(PositionRange range) {
		this((range != null ? range.start : 0l), (range != null ? range.end : 0l));
	}

	/**
	 * Construct {@code PositionRange}.
	 *
	 * @param start The start position.
	 * @param end The end position.
	 */
	public PositionRange(long start, long end) {
		this.start = start;
		this.end = end;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("[");
		Hex.formatL(buffer, this.start);
		buffer.append(" - ");
		Hex.formatL(buffer, this.end);
		buffer.append("[");
		return buffer.toString();
	}

}
