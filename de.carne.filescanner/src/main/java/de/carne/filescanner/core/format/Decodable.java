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
package de.carne.filescanner.core.format;

import java.io.IOException;

import de.carne.filescanner.core.FileScannerResultBuilder;

/**
 * This interface defines functions used to decode and render a format and it's
 * nested structures.
 */
public interface Decodable extends Renderable {

	/**
	 * Decode scanner result.
	 *
	 * @param result The result builder to decode into.
	 * @return The number of decoded bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public long decode(FileScannerResultBuilder result) throws IOException;

}
