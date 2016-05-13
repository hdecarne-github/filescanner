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
package de.carne.filescanner.jfx.session;

import java.util.concurrent.CountDownLatch;

import de.carne.filescanner.core.FileScanner;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerStats;
import de.carne.filescanner.core.FileScannerStatus;
import javafx.application.Platform;

/**
 * Proxy class for interface {@code FileScannerStatus} making sure that all
 * calls are delegated on JavaFX Application Thread.
 */
class FileScannerStatusProxy implements FileScannerStatus {

	private FileScannerStatus status;

	FileScannerStatusProxy(FileScannerStatus status) {
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerStatus#onScanStart(de.carne.
	 * filescanner.core.FileScanner, de.carne.filescanner.core.FileScannerStats)
	 */
	@Override
	public void onScanStart(FileScanner scanner, FileScannerStats stats) {
		if (Platform.isFxApplicationThread()) {
			this.status.onScanStart(scanner, stats);
		} else {
			CountDownLatch latch = new CountDownLatch(1);

			Platform.runLater(new Runnable() {

				private final FileScanner scannerParam = scanner;
				private final FileScannerStats statsParam = stats;

				@Override
				public void run() {
					onScanStart(this.scannerParam, this.statsParam);
					latch.countDown();
				}

			});
			latchAwait(latch);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerStatus#onScanFinished(de.carne.
	 * filescanner.core.FileScanner, de.carne.filescanner.core.FileScannerStats)
	 */
	@Override
	public void onScanFinished(FileScanner scanner, FileScannerStats stats) {
		if (Platform.isFxApplicationThread()) {
			this.status.onScanFinished(scanner, stats);
		} else {
			CountDownLatch latch = new CountDownLatch(1);

			Platform.runLater(new Runnable() {

				private final FileScanner scannerParam = scanner;
				private final FileScannerStats statsParam = stats;

				@Override
				public void run() {
					onScanFinished(this.scannerParam, this.statsParam);
					latch.countDown();
				}

			});
			latchAwait(latch);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.FileScannerStatus#onScanCancelled(de.carne.
	 * filescanner.core.FileScanner, de.carne.filescanner.core.FileScannerStats)
	 */
	@Override
	public void onScanCancelled(FileScanner scanner, FileScannerStats stats) {
		if (Platform.isFxApplicationThread()) {
			this.status.onScanCancelled(scanner, stats);
		} else {
			CountDownLatch latch = new CountDownLatch(1);

			Platform.runLater(new Runnable() {

				private final FileScanner scannerParam = scanner;
				private final FileScannerStats statsParam = stats;

				@Override
				public void run() {
					onScanCancelled(this.scannerParam, this.statsParam);
					latch.countDown();
				}

			});
			latchAwait(latch);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerStatus#onScanProgress(de.carne.
	 * filescanner.core.FileScanner, de.carne.filescanner.core.FileScannerStats)
	 */
	@Override
	public void onScanProgress(FileScanner scanner, FileScannerStats stats) {
		if (Platform.isFxApplicationThread()) {
			this.status.onScanProgress(scanner, stats);
		} else {
			CountDownLatch latch = new CountDownLatch(1);

			Platform.runLater(new Runnable() {

				private final FileScanner scannerParam = scanner;
				private final FileScannerStats statsParam = stats;

				@Override
				public void run() {
					onScanProgress(this.scannerParam, this.statsParam);
					latch.countDown();
				}

			});
			latchAwait(latch);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerStatus#onScanResult(de.carne.
	 * filescanner.core.FileScanner,
	 * de.carne.filescanner.core.FileScannerResult)
	 */
	@Override
	public void onScanResult(FileScanner scanner, FileScannerResult result) {
		if (Platform.isFxApplicationThread()) {
			this.status.onScanResult(scanner, result);
		} else {
			CountDownLatch latch = new CountDownLatch(1);

			Platform.runLater(new Runnable() {

				private final FileScanner scannerParam = scanner;
				private final FileScannerResult resultParam = result;

				@Override
				public void run() {
					onScanResult(this.scannerParam, this.resultParam);
					latch.countDown();
				}

			});
			latchAwait(latch);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.FileScannerStatus#onScanException(de.carne.
	 * filescanner.core.FileScanner, java.lang.Throwable)
	 */
	@Override
	public void onScanException(FileScanner scanner, Throwable e) {
		if (Platform.isFxApplicationThread()) {
			this.status.onScanException(scanner, e);
		} else {
			CountDownLatch latch = new CountDownLatch(1);

			Platform.runLater(new Runnable() {

				private final FileScanner scannerParam = scanner;
				private final Throwable eParam = e;

				@Override
				public void run() {
					onScanException(this.scannerParam, this.eParam);
					latch.countDown();
				}

			});
			latchAwait(latch);
		}
	}

	private void latchAwait(CountDownLatch latch) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
