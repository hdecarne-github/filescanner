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
 * Helper class for recording the scan statistics reported via the
 * {@code FileScannerStats} interface.
 */
final class FileScannerStatsCollector implements FileScannerStats {

	private int scanCount = 0;

	private int finishedCount = 0;

	private long totalInputSize = 0l;

	private long scanned = 0l;

	private long startTime = 0l;

	private long elapsed = 0l;

	public FileScannerStatsCollector() {
		// Nothing to do here
	}

	private FileScannerStatsCollector(FileScannerStatsCollector stats) {
		this.scanCount = stats.scanCount;
		this.finishedCount = stats.finishedCount;
		this.totalInputSize = stats.totalInputSize;
		this.scanned = stats.scanned;
		this.elapsed = stats.elapsed;
		if (stats.startTime > 0) {
			this.elapsed += System.currentTimeMillis() - stats.startTime;
		}
	}

	public synchronized FileScannerStatsCollector recordInput(InputFileScannerResult result) {
		this.scanCount++;
		this.totalInputSize += result.size();
		if (this.scanCount == 1) {
			this.startTime = System.currentTimeMillis();
		}
		return new FileScannerStatsCollector(this);
	}

	public synchronized FileScannerStatsCollector recordScanned(long scannedDelta, boolean finished) {
		if (finished) {
			this.finishedCount++;
		}
		this.scanned += scannedDelta;
		return new FileScannerStatsCollector(this);
	}

	public int scanCount() {
		return this.scanCount;
	}

	public int finishedCount() {
		return this.finishedCount;
	}

	@Override
	public long scanned() {
		return this.scanned;
	}

	@Override
	public long elapsed() {
		return this.elapsed;
	}

	@Override
	public double progress() {
		return (this.totalInputSize > 0 ? ((double) this.scanned) / this.totalInputSize : 0.0);
	}

}
