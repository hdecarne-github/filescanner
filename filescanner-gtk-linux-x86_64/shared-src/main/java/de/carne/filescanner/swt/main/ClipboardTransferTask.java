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

import java.util.concurrent.Callable;

import org.eclipse.jdt.annotation.Nullable;

import de.carne.filescanner.engine.FileScannerResult;

final class ClipboardTransferTask implements Callable<@Nullable Void> {

	private final ProgressCallback progress;
	private final FileScannerResult result;
	private final ClipboardTransferHandler handler;

	protected ClipboardTransferTask(ProgressCallback progress, FileScannerResult result,
			ClipboardTransferHandler handler) {
		this.progress = progress;
		this.result = result;
		this.handler = handler;
	}

	@Override
	public @Nullable Void call() throws Exception {
		try {
			this.handler.prepareTransfer(this.result);
		} finally {
			this.progress.done();
		}
		return null;
	}

}
