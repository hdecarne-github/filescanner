/*
 * Copyright (c) 2007-2021 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.swt.main;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.Application;
import de.carne.filescanner.engine.transfer.TransferSource;
import de.carne.filescanner.swt.FileScannerMain;
import de.carne.util.Exceptions;

class PipedTransferSource extends PipedInputStream {

	private final ProgressCallback progress;
	private final TransferSource transferSource;
	private volatile boolean pipeReady = false;
	@Nullable
	private Exception exception = null;

	PipedTransferSource(ProgressCallback progress, TransferSource transferSource) {
		this.progress = progress;
		this.transferSource = transferSource;
		Application.getMain(FileScannerMain.class).cachedThreadPool().execute(this::runPipe);
		waitForPipeReady();
	}

	private void waitForPipeReady() {
		synchronized (this.transferSource) {
			try {
				while (!this.pipeReady) {
					this.transferSource.wait(1000);
				}
			} catch (InterruptedException e) {
				Exceptions.ignore(e);
				Thread.currentThread().interrupt();
			}
		}
	}

	private void signalPipeReady() {
		synchronized (this.transferSource) {
			this.pipeReady = true;
			this.transferSource.notifyAll();
		}
	}

	private void runPipe() {
		try (OutputStream target = new ProgressOutputStream(this.progress, new PipedOutputStream(this))) {
			signalPipeReady();
			this.transferSource.transfer(target);
		} catch (Exception e) {
			this.exception = e;
		}
	}

	@Override
	public void close() throws IOException {
		Exception checkedException = this.exception;

		if (checkedException != null) {
			try {
				super.close();
			} catch (IOException e) {
				checkedException.addSuppressed(e);
			}
			if (checkedException instanceof IOException) {
				throw (IOException) checkedException;
			}
			throw new IOException(checkedException);
		}
		super.close();
	}

}
