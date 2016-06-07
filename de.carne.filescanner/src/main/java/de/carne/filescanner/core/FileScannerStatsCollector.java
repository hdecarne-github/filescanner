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

	private static final long PROGRESS_INTERVAL = 1000000000l;

	private int scanCount = 0;

	private int finishedCount = 0;

	private long totalInputSize = 0L;

	private long scanned = 0L;

	private long startNanos = 0L;

	private long elapsedNanos = 0L;

	private long lastProgressNanos = 0L;

	public FileScannerStatsCollector() {
		// Nothing to do here
	}

	private FileScannerStatsCollector(FileScannerStatsCollector stats) {
		this.scanCount = stats.scanCount;
		this.finishedCount = stats.finishedCount;
		this.totalInputSize = stats.totalInputSize;
		this.scanned = stats.scanned;
		this.elapsedNanos = stats.elapsedNanos;
		if (stats.startNanos > 0) {
			this.elapsedNanos += System.nanoTime() - stats.startNanos;
		}
	}

	public synchronized FileScannerStatsCollector get() {
		return new FileScannerStatsCollector(this);
	}

	public synchronized FileScannerStatsCollector recordInput(FileScannerResult result) {
		this.scanCount++;
		this.totalInputSize += result.size();
		if (this.scanCount == 1) {
			this.startNanos = System.nanoTime();
		}
		return new FileScannerStatsCollector(this);
	}

	public synchronized FileScannerStatsCollector recordScanned(long scannedDelta, boolean finished) {
		if (finished) {
			this.finishedCount++;
		}
		this.scanned += scannedDelta;

		long currentNanos = System.nanoTime();
		boolean triggerProgress;

		if (finished || currentNanos - this.lastProgressNanos >= PROGRESS_INTERVAL) {
			this.lastProgressNanos = currentNanos;
			triggerProgress = true;
		} else {
			triggerProgress = false;
		}
		return (triggerProgress ? new FileScannerStatsCollector(this) : null);
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
		return this.elapsedNanos / 1000000;
	}

	@Override
	public double progress() {
		return (this.totalInputSize > 0 ? ((double) this.scanned) / this.totalInputSize : 0.0);
	}

}
