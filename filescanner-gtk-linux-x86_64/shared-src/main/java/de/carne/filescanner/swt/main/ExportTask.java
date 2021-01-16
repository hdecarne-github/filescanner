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
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.transfer.FileScannerResultExportHandler;
import de.carne.filescanner.engine.transfer.TransferSource;
import de.carne.filescanner.swt.export.ExportOptions;
import de.carne.util.logging.Log;

class ExportTask implements Callable<Void> {

	private static final Log LOG = new Log();

	private final ProgressCallback progress;
	private final FileScannerResultExportHandler exportHandler;
	private final Path path;
	private final boolean overwrite;
	private final FileScannerResult result;

	public ExportTask(ProgressCallback progress, ExportOptions options, FileScannerResult result) {
		this.progress = progress;
		this.exportHandler = options.exportHandler();
		this.path = options.path();
		this.overwrite = options.overwrite();
		this.result = result;
	}

	@SuppressWarnings({ "null", "squid:S2637" })
	@Override
	public Void call() throws IOException {
		LOG.info("Exporting result ''{0}'' to path ''{1}''...", this.result.name(), this.path);

		try (OutputStream exportStream = new ProgressOutputStream(this.progress,
				Files.newOutputStream(this.path, getOpenOptions()))) {
			TransferSource transferSource = this.result.export(this.exportHandler);

			this.progress.setTotal(transferSource.size());

			transferSource.transfer(exportStream);
		} finally {
			this.progress.done();
		}

		LOG.info("Exporting to path ''{0}'' finished", this.path);

		return null;
	}

	private OpenOption[] getOpenOptions() {
		Set<OpenOption> openOptions = new HashSet<>();

		openOptions.add(StandardOpenOption.WRITE);
		if (this.overwrite) {
			openOptions.add(StandardOpenOption.CREATE);
			openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
		} else {
			openOptions.add(StandardOpenOption.CREATE_NEW);
		}
		return openOptions.toArray(new OpenOption[openOptions.size()]);
	}

}
