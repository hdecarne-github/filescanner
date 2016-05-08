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

/**
 * Class used to describe the virtual layout of a
 * {@code VirtualScrollRegion.Scrollable}.
 */
public final class VirtualScrollLayout {

	/**
	 * Empty layout.
	 */
	public static final VirtualScrollLayout EMPTY = new VirtualScrollLayout(0.0, 0.0, 1.0, 1.0);

	private double width;

	private double height;

	private double hIncrement;

	private double vIncrement;

	/**
	 * Construct {@code VirtualScrollLayout}.
	 *
	 * @param width The total width to display.
	 * @param height The total height to display.
	 * @param hIncrement The horizontal increment to use.
	 * @param vIncrement The vertical increment to use.
	 */
	public VirtualScrollLayout(double width, double height, double hIncrement, double vIncrement) {
		this.width = width;
		this.height = height;
		this.hIncrement = hIncrement;
		this.vIncrement = vIncrement;
	}

	/**
	 * Get the total width to display.
	 *
	 * @return The total width to display.
	 */
	public double getWidth() {
		return this.width;
	}

	/**
	 * Get the total height to display.
	 *
	 * @return The total height to display.
	 */
	public double getHeight() {
		return this.height;
	}

	/**
	 * Get the horizontal increment to use.
	 *
	 * @return The horizontal increment to use.
	 */
	public double getHIncrement() {
		return this.hIncrement;
	}

	/**
	 * Get the vertical increment to use.
	 *
	 * @return The vertical increment to use.
	 */
	public double getVIncrement() {
		return this.vIncrement;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append(this.width);
		buffer.append(" x ");
		buffer.append(this.height);
		buffer.append(" - ");
		buffer.append(this.hIncrement);
		buffer.append(" x ");
		buffer.append(this.vIncrement);
		return buffer.toString();
	}

}
