/*
 * Copyright (c) 2007-2022 Holger de Carne and contributors, All Rights Reserved.
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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.dnd.Clipboard;

import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.transfer.FileScannerResultExportHandler;
import de.carne.filescanner.engine.transfer.TransferSource;
import de.carne.filescanner.engine.transfer.TransferType;
import de.carne.util.Late;

abstract class ClipboardTransferHandler {

	private static final Set<TransferType> TRANSFERABLE_TYPES = new HashSet<>();

	static {
		TRANSFERABLE_TYPES.add(TransferType.TEXT_PLAIN);
		TRANSFERABLE_TYPES.add(TransferType.TEXT_HTML);
		TRANSFERABLE_TYPES.add(TransferType.IMAGE_BMP);
		TRANSFERABLE_TYPES.add(TransferType.IMAGE_GIF);
		TRANSFERABLE_TYPES.add(TransferType.IMAGE_JPEG);
		TRANSFERABLE_TYPES.add(TransferType.IMAGE_PNG);
		TRANSFERABLE_TYPES.add(TransferType.IMAGE_TIFF);
		TRANSFERABLE_TYPES.add(TransferType.APPLICATION_PDF);
	}

	private final Late<ClipboardContent> contentHolder = new Late<>();
	private final boolean detachable;

	protected ClipboardTransferHandler(boolean detachable) {
		this.detachable = detachable;
	}

	public static boolean isTransferable(TransferType transferType) {
		return TRANSFERABLE_TYPES.contains(transferType);
	}

	public void prepareTransfer(ProgressCallback progress) throws IOException {
		TransferSource transferSource = getTransferSource();

		progress.setTotal(transferSource.size());

		TransferType transferType = transferSource.transferType();
		ClipboardContent content;

		switch (transferType) {
		case TEXT_PLAIN:
			content = this.contentHolder.set(ClipboardContent.textContent());
			break;
		case TEXT_HTML:
			content = this.contentHolder.set(ClipboardContent.htmlContent(this.detachable));
			break;
		case IMAGE_BMP:
		case IMAGE_GIF:
		case IMAGE_JPEG:
		case IMAGE_PNG:
		case IMAGE_TIFF:
			content = this.contentHolder.set(ClipboardContent.imageContent());
			break;
		case APPLICATION_PDF:
			content = this.contentHolder.set(ClipboardContent.fileContent(".pdf"));
			break;
		default:
			throw new IOException("Cannot copy transfer type: " + transferType);
		}
		content.prepareTransfer(progress, transferSource);
	}

	public ClipboardContentHolder transfer(Clipboard clipboard) {
		return this.contentHolder.get().transfer(clipboard);
	}

	protected abstract TransferSource getTransferSource() throws IOException;

	public static ClipboardTransferHandler defaultHandler(TransferSource transferSource) {
		return new ClipboardTransferHandler(false) {

			@Override
			protected TransferSource getTransferSource() throws IOException {
				return transferSource;
			}

		};
	}

	public static ClipboardTransferHandler exportHandler(FileScannerResultExportHandler exportHandler,
			FileScannerResult result) {
		return new ClipboardTransferHandler(true) {

			@Override
			protected TransferSource getTransferSource() throws IOException {
				return result.export(exportHandler);
			}

		};
	}

}
