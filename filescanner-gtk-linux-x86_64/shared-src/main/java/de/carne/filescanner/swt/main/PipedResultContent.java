/*
 * Copyright (c) 2007-2020 Holger de Carne and contributors, All Rights Reserved.
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
import java.io.PipedReader;
import java.io.PipedWriter;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.Application;
import de.carne.boot.Exceptions;
import de.carne.filescanner.FileScannerMain;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.transfer.PlainTextRenderer;
import de.carne.filescanner.engine.transfer.RenderOutput;
import de.carne.filescanner.engine.transfer.RenderStyle;

class PipedResultContent extends PipedReader {

	private final FileScannerResult result;
	private volatile boolean pipeReady = false;
	@Nullable
	private IOException exception = null;

	PipedResultContent(FileScannerResult result) {
		this.result = result;
		Application.getMain(FileScannerMain.class).cachedThreadPool().execute(this::runPipe);
		waitForPipeReady();
	}

	private void waitForPipeReady() {
		synchronized (this.result) {
			try {
				do {
					this.result.wait(1000);
				} while (!this.pipeReady);
			} catch (InterruptedException e) {
				Exceptions.ignore(e);
				Thread.currentThread().interrupt();
			}
		}
	}

	private void signalPipeReady() {
		synchronized (this.result) {
			this.pipeReady = true;
			this.result.notifyAll();
		}
	}

	private void runPipe() {
		try (PlainTextRenderer renderer = new PlainTextRenderer(new PipedWriter(this))) {
			signalPipeReady();
			renderer.emitText(0, RenderStyle.NORMAL, this.result.name(), true);
			RenderOutput.render(this.result, renderer, 0);
		} catch (IOException e) {
			this.exception = e;
		}
	}

	@Override
	public void close() throws IOException {
		IOException checkedException = this.exception;

		if (checkedException != null) {
			try {
				super.close();
			} catch (IOException e) {
				checkedException.addSuppressed(e);
			}
			throw checkedException;
		}
		super.close();
	}

}
