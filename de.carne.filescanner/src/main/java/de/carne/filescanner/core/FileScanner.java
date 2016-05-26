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

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import de.carne.filescanner.core.input.DecodeCache;
import de.carne.filescanner.spi.FileScannerInput;
import de.carne.filescanner.spi.Format;
import de.carne.util.logging.Log;

/**
 * The actual scan engine.
 */
public final class FileScanner implements Closeable {

	private static final Log LOG = new Log(FileScanner.class);

	private static final String PROPERTY_PACKAGE = FileScanner.class.getPackage().getName();

	private static final String THREAD_COUNT_PROPERTY = PROPERTY_PACKAGE + ".threadCount";

	private static final int THREAD_COUNT;

	static {
		String threadCountProperty = System.getProperty(THREAD_COUNT_PROPERTY);
		int threadCount = 0;

		if (threadCountProperty != null) {
			try {
				threadCount = Integer.parseInt(threadCountProperty);
			} catch (NumberFormatException e) {
				// ignore
			}
			if (threadCount < 0) {
				LOG.warning(null, "Ignoring invalid {0} value: ''{1}''", THREAD_COUNT_PROPERTY, threadCountProperty);
			}
		}
		if (threadCount <= 0) {
			threadCount = Runtime.getRuntime().availableProcessors();
		}

		LOG.notice(null, "Using {0} scan threads", threadCount);

		THREAD_COUNT = threadCount;
	}

	private static final long PROGRESS_INTERVAL = 1000000000l;

	private final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

	private final DecodeCache decodeCache = new DecodeCache();

	private final FileScannerStatus status;

	private final FileScannerStatsCollector stats = new FileScannerStatsCollector();

	private final AtomicInteger scannerCount = new AtomicInteger(0);

	/**
	 * Construct {@code FileScanner}.
	 *
	 * @param status The callback to receive file scanner results.
	 */
	public FileScanner(FileScannerStatus status) {
		assert status != null;

		this.status = status;
	}

	/**
	 * Get this scanner's decode cache.
	 *
	 * @return This scanner's decode cache.
	 */
	public final DecodeCache decodeCache() {
		return this.decodeCache;
	}

	/**
	 * Submit an input for scanning in the background.
	 *
	 * @param input The input to scan.
	 * @throws IOException if an I/O error occurs accessing the input.
	 */
	public void queueInput(FileScannerInput input) throws IOException {
		assert input != null;
		assert this.equals(input.scanner());

		InputFileScannerResult result = new InputFileScannerResult(null, input);

		this.status.onScanResult(this, result);

		Scanner scanner = new Scanner(result);

		this.scannerCount.incrementAndGet();
		this.threadPool.submit(scanner);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() {
		this.threadPool.shutdownNow();

		FileScannerStatsCollector currentStats = this.stats.recordScanned(0, false);

		if (this.scannerCount.get() != currentStats.finishedCount()) {
			LOG.notice(null, "Closing still running scan; pending results will be ignored");
			this.status.onScanCancelled(this, currentStats);
		}
		this.decodeCache.close();
	}

	private class Scanner implements Callable<Scanner> {

		private InputFileScannerResult result;

		public Scanner(InputFileScannerResult result) {
			this.result = result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Scanner call() throws Exception {
			scanInput(this.result);
			return this;
		}

	}

	@SuppressWarnings("resource")
	void scanInput(InputFileScannerResult inputResult) {
		FileScannerStatsCollector currentStats = this.stats.recordInput(inputResult);

		if (currentStats.scanCount() == 1) {
			this.status.onScanStart(this, currentStats);
		}

		FileScannerInput input = inputResult.input();

		LOG.notice(null, "Scanning input ''{0}''...", input.path());

		long scanPosition = 0L;
		long inputSize = inputResult.size();
		long lastProgressNanos = System.nanoTime();
		long lastProgressPosition = scanPosition;
		FormatMatcher formatMatcher = new FormatMatcher();

		try {
			while (scanPosition < inputSize) {
				// Let pending interrupts occur
				if (Thread.interrupted()) {
					LOG.notice(null, "Interrupting scan of input ''{0}''", input.path());
					throw new InterruptedException();
				}

				// Report progress (from time to time)
				long currentNanos = System.nanoTime();

				if ((currentNanos - lastProgressNanos) > PROGRESS_INTERVAL) {
					long scannedDelta = scanPosition - lastProgressPosition;

					currentStats = this.stats.recordScanned(scannedDelta, false);
					this.status.onScanProgress(this, currentStats);
					lastProgressNanos = currentNanos;
					lastProgressPosition = scanPosition;
				}

				// Do the actual decoding
				FileScannerResult decoded = decodeInput(inputResult, scanPosition, formatMatcher);

				if (decoded != null) {
					scanPosition += decoded.size();
					this.status.onScanResult(this, decoded);
				} else {
					scanPosition += 1l;
				}
			}
		} catch (InterruptedException e) {
			// nothing to do here
		} catch (Exception e) {
			this.status.onScanException(this, e);
		}
		// Report reached progress end possible completion
		currentStats = this.stats.recordScanned(scanPosition - lastProgressPosition, true);
		this.status.onScanProgress(this, currentStats);
		if (this.scannerCount.get() == currentStats.finishedCount()) {
			this.status.onScanFinished(this, currentStats);
		}
	}

	@SuppressWarnings("resource")
	private FileScannerResult decodeInput(InputFileScannerResult inputResult, long position,
			FormatMatcher formatMatcher) throws IOException {
		FileScannerInput input = inputResult.input();
		List<Format> formats = formatMatcher.matchFormats(input, position);
		FileScannerResult decoded = null;

		for (Format format : formats) {
			decoded = format.decodeInput(inputResult, position);
			if (decoded != null) {
				break;
			}
		}
		return decoded;
	}

}
