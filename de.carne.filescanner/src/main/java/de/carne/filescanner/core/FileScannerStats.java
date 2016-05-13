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

/**
 * FileScanner statistics.
 */
public interface FileScannerStats {

	/**
	 * Get the number of total scanned bytes.
	 * 
	 * @return The number of total scanned bytes.
	 */
	public long scanned();

	/**
	 * Get the number of elapsed milliseconds during the scan.
	 * 
	 * @return The number of elapsed milliseconds during the scan.
	 */
	public long elapsed();

	/**
	 * Get the current scan progress (0.0 to 1.0).
	 * 
	 * @return The current scan progress (0.0 to 1.0).
	 */
	public double progress();

}
