/*
 * Copyright (c) 2007-2018 Holger de Carne and contributors, All Rights Reserved.
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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;

import de.carne.boot.check.Nullable;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.FileScannerResultExporter;
import de.carne.filescanner.engine.transfer.ExportTarget;
import de.carne.io.IOUtil;
import de.carne.nio.compression.Check;

class PipedExporterInputStream extends PipedInputStream {

	private final ProgressCallback progress;
	private final FileScannerResultExporter exporter;
	@Nullable
	private IOException ioException = null;

	public PipedExporterInputStream(ProgressCallback progress, FileScannerResultExporter exporter) {
		this.progress = progress;
		this.exporter = exporter;
	}

	public void start(FileScannerResult result) {
		Thread exportThread = new Thread(() -> export0(result));

		exportThread.start();
	}

	private void export0(FileScannerResult result) {
		try (PipedOutputStream pipe = new PipedOutputStream(this)) {
			result.export(new PipedExportTarget(pipe), this.exporter);
		} catch (IOException e) {
			this.ioException = e;
		} catch (RuntimeException e) {
			this.ioException = new IOException(e);
		}
	}

	@Override
	public void close() throws IOException {
		super.close();
		if (this.ioException != null) {
			throw this.ioException;
		}
	}

	void setSize0(long size) {
		this.progress.setTotal(size);
	}

	private class PipedExportTarget implements ExportTarget {

		private final PipedOutputStream pipe;
		private boolean open = true;

		public PipedExportTarget(PipedOutputStream pipe) {
			this.pipe = pipe;
		}

		@Override
		public int write(@Nullable ByteBuffer src) throws IOException {
			return IOUtil.copyBuffer(this.pipe, Check.notNull(src));
		}

		@Override
		public synchronized boolean isOpen() {
			return this.open;
		}

		@Override
		public synchronized void close() throws IOException {
			this.open = false;
		}

		@Override
		public void setSize(long size) throws IOException {
			setSize0(size);
		}

	}

}
