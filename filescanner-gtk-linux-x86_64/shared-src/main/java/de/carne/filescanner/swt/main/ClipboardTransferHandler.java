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
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.FileScannerResultExportHandler;
import de.carne.filescanner.engine.transfer.RenderOutput;
import de.carne.filescanner.engine.transfer.SimpleTextRenderer;
import de.carne.filescanner.engine.transfer.TransferType;
import de.carne.filescanner.engine.util.CombinedRenderer;
import de.carne.filescanner.swt.preferences.Config;
import de.carne.nio.compression.Check;

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

	public static ClipboardTransferHandler defaultHandler(Config config) {
		return new ClipboardTransferHandler() {

			private final StringWriter htmlText = new StringWriter();
			private final StringWriter simpleText = new StringWriter();

			@Override
			public void prepareTransfer(FileScannerResult result) throws IOException {
				HtmlRenderer htmlTextRenderer = new HtmlRenderer(this.htmlText, config);
				SimpleTextRenderer simpleTextRenderer = new SimpleTextRenderer(this.simpleText);

				RenderOutput.render(result, new CombinedRenderer(htmlTextRenderer, simpleTextRenderer));
			}

			@Override
			public void transfer(Clipboard clipboard) {
				clipboard.setContents(new Object[] { this.htmlText.toString(), this.simpleText.toString() },
						new Transfer[] { HTMLTransfer.getInstance(), TextTransfer.getInstance(), });
			}

		};
	}

	public static ClipboardTransferHandler exportHandler(FileScannerResultExportHandler exportHandler) {
		ClipboardTransferHandler transferHandler;

		switch (exportHandler.transferType()) {
		case IMAGE_BMP:
		case IMAGE_GIF:
		case IMAGE_JPEG:
		case IMAGE_PNG:
		case IMAGE_TIFF:
			transferHandler = imageDataHandler(exportHandler);
			break;
		default:
			transferHandler = Check.fail("Unexpected exporter type: %1$s", exportHandler.transferType());
		}
		return transferHandler;
	}

	private static ClipboardTransferHandler imageDataHandler(FileScannerResultExportHandler exportHandler) {
		return Check.fail("Not yet implemented");
	}

}
