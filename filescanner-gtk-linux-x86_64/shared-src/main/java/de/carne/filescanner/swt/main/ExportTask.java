/*
 * Copyright (c) 2007-2019 Holger de Carne and contributors, All Rights Reserved.
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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.boot.logging.Log;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.transfer.ExportTarget;
import de.carne.filescanner.engine.transfer.FileScannerResultExporter;
import de.carne.filescanner.swt.export.ExportOptions;

class ExportTask implements Callable<@Nullable Void>, ExportTarget {

	private static final Log LOG = new Log();

	private final ProgressCallback progress;
	private final FileScannerResult result;
	private final FileScannerResultExporter exportHandler;
	private final Path path;
	private final boolean overwrite;
	@Nullable
	private WritableByteChannel channel = null;

	public ExportTask(ProgressCallback progress, FileScannerResult result, ExportOptions options) {
		this.progress = progress;
		this.result = result;
		this.exportHandler = options.exportHandler();
		this.path = options.path();
		this.overwrite = options.overwrite();
	}

	@Override
	public @Nullable Void call() throws Exception {
		LOG.info("Exporting result ''{0}'' to path ''{1}''...", this.result.name(), this.path);

		try {
			this.result.export(this, this.exportHandler);
		} finally {
			this.progress.done();
			close();
		}

		LOG.info("Exporting to path ''{0}'' finished", this.path);

		return null;
	}

	@SuppressWarnings("resource")
	@Override
	public int write(@Nullable ByteBuffer src) throws IOException {
		int written = 0;

		if (src != null) {
			WritableByteChannel checkedChannel = this.channel;

			if (checkedChannel == null) {
				LOG.info("Creating export target ''{0}''...", this.path);

				Set<OpenOption> openOptions = new HashSet<>();

				openOptions.add(StandardOpenOption.WRITE);
				if (this.overwrite) {
					openOptions.add(StandardOpenOption.CREATE);
					openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
				} else {
					openOptions.add(StandardOpenOption.CREATE_NEW);
				}
				checkedChannel = this.channel = FileChannel.open(this.path, openOptions);
			}
			written = checkedChannel.write(src);
			this.progress.addProgress(written);
		}
		return written;
	}

	@Override
	public synchronized boolean isOpen() {
		return this.channel != null;
	}

	@Override
	public synchronized void close() throws IOException {
		LOG.info("Closing export target ''{0}''...", this.path);

		WritableByteChannel checkedChannel = this.channel;

		if (checkedChannel != null) {
			this.channel = null;
			checkedChannel.close();
		}
	}

	@Override
	public void setSize(long size) throws IOException {
		this.progress.setTotal(size);
	}

}
