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
package de.carne.filescanner.core.transfer;

/**
 * Base class for all types of result exporters.
 */
public abstract class ResultExporter {

	private final String name;

	/**
	 * Construct {@code ResultExporter}.
	 *
	 * @param name The exporter name.
	 */
	protected ResultExporter(String name) {
		assert name != null;

		this.name = name;
	}

	/**
	 * Get the exporter name.
	 *
	 * @return The exporter name.
	 */
	public final String name() {
		return this.name;
	}

	@Override
	public String toString() {
		return name();
	}

}
