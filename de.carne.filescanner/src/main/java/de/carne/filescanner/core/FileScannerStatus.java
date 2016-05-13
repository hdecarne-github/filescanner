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
 * Callback interface receiving status notifications from the scanner.
 */
public interface FileScannerStatus {

	/**
	 * Called on scan start.
	 *
	 * @param scanner The notifying scanner.
	 * @param stats The current scanner stats.
	 */
	public void onScanStart(FileScanner scanner, FileScannerStats stats);

	/**
	 * Called when the scanner has finished.
	 *
	 * @param scanner The notifying scanner.
	 * @param stats The current scanner stats.
	 */
	public void onScanFinished(FileScanner scanner, FileScannerStats stats);

	/**
	 * Called whenever cancel is invoked on the scanner.
	 *
	 * @param scanner The notifying scanner.
	 * @param stats The current scanner stats.
	 */
	public void onScanCancelled(FileScanner scanner, FileScannerStats stats);

	/**
	 * Periodically called during scan to report scan progress.
	 *
	 * @param scanner The notifying scanner.
	 * @param stats The current scanner stats.
	 */
	public void onScanProgress(FileScanner scanner, FileScannerStats stats);

	/**
	 * Called every time a new scan result is available.
	 *
	 * @param scanner The notifying scanner.
	 * @param result The new scan result.
	 */
	public void onScanResult(FileScanner scanner, FileScannerResult result);

	/**
	 * Called whenever an exception is encountered while scanning.
	 *
	 * @param scanner The notifying scanner.
	 * @param e The encountered exception.
	 */
	public void onScanException(FileScanner scanner, Throwable e);

}
