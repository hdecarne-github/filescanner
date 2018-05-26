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
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.ImageData;

import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.FileScannerResultExportHandler;
import de.carne.filescanner.engine.FileScannerResultExporter;
import de.carne.filescanner.engine.transfer.TransferType;
import de.carne.nio.compression.Check;
import de.carne.util.Late;

abstract class ClipboardTransferHandler {

	private static final Set<TransferType> TRANSFERABLE_TYPES = new HashSet<>();

	static {
		TRANSFERABLE_TYPES.add(TransferType.IMAGE_BMP);
		TRANSFERABLE_TYPES.add(TransferType.IMAGE_GIF);
		TRANSFERABLE_TYPES.add(TransferType.IMAGE_JPEG);
		TRANSFERABLE_TYPES.add(TransferType.IMAGE_PNG);
		TRANSFERABLE_TYPES.add(TransferType.IMAGE_TIFF);
	}

	public static boolean isTransferable(TransferType transferType) {
		return TRANSFERABLE_TYPES.contains(transferType);
	}

	public abstract void prepareTransfer(FileScannerResult result) throws IOException;

	public abstract void transfer(Clipboard clipboard);

	public static ClipboardTransfer defaultHandler(HtmlRenderServer renderServer) {
		return progress -> new ClipboardTransferHandler() {

			private final StringWriter htmlText = new StringWriter();
			private final StringWriter plainText = new StringWriter();

			@Override
			public void prepareTransfer(FileScannerResult result) throws IOException {
				HtmlResultDocument resultDocument = renderServer.createResultDocument(result, true);

				resultDocument.writeTo(this.htmlText, this.plainText);
			}

			@Override
			public void transfer(Clipboard clipboard) {
				clipboard.setContents(new Object[] { this.htmlText.toString(), this.plainText.toString() },
						new Transfer[] { HTMLTransfer.getInstance(), TextTransfer.getInstance(), });
			}

		};
	}

	public static ClipboardTransfer exportHandler(FileScannerResultExportHandler exportHandler) {
		return progress -> {
			ClipboardTransferHandler transferHandler;

			switch (exportHandler.transferType()) {
			case IMAGE_BMP:
			case IMAGE_GIF:
			case IMAGE_JPEG:
			case IMAGE_PNG:
			case IMAGE_TIFF:
				transferHandler = imageDataHandler(progress, exportHandler);
				break;
			default:
				transferHandler = Check.fail("Unexpected exporter type: %1$s", exportHandler.transferType());
			}
			return transferHandler;
		};
	}

	private static ClipboardTransferHandler imageDataHandler(ProgressCallback progress,
			FileScannerResultExporter exporter) {
		return new ClipboardTransferHandler() {

			private final Late<ImageData> imageDataHolder = new Late<>();

			@Override
			public void prepareTransfer(FileScannerResult result) throws IOException {
				try (PipedExporterInputStream pipe = new PipedExporterInputStream(progress, exporter)) {
					pipe.start(result);
					this.imageDataHolder.set(new ImageData(pipe));
				}
			}

			@Override
			public void transfer(Clipboard clipboard) {
				clipboard.setContents(new Object[] { this.imageDataHolder.get() },
						new Transfer[] { ImageTransfer.getInstance() });
			}

		};
	}

}
