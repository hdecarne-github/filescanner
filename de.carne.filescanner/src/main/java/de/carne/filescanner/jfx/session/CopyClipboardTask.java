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

import java.io.InputStream;
import java.util.List;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.transfer.ImageResultExporter;
import de.carne.filescanner.core.transfer.TextResultExporter;
import de.carne.util.logging.Log;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Task for clipboard copying in the background.
 */
class CopyClipboardTask extends Task<Void> {

	private static final Log LOG = new Log(CopyClipboardTask.class);

	private final FileScannerResult result;

	private TextResultExporter.Text textData = null;

	private InputStream imageData = null;

	CopyClipboardTask(FileScannerResult result) {
		this.result = result;
	}

	@Override
	protected Void call() throws Exception {
		List<TextResultExporter> textExporters = this.result.getExporters(TextResultExporter.class);

		if (!isCancelled() && textExporters.size() > 0) {
			this.textData = textExporters.get(0).export(this.result);
		}

		List<ImageResultExporter> imageExporters = this.result.getExporters(ImageResultExporter.class);

		if (!isCancelled() && imageExporters.size() > 0) {
			this.imageData = imageExporters.get(0).getStreamHandler(this.result).open();
		}
		if (!isCancelled() && this.textData == null && this.imageData == null) {
			// TODO
		}
		return null;
	}

	@Override
	protected void succeeded() {
		ClipboardContent content = new ClipboardContent();

		if (this.imageData != null) {
			LOG.info(null, "Copying image data from result ''{0}''", this.result.title());
			content.putImage(new Image(this.imageData));
		}
		if (this.textData != null) {
			if (this.textData.isPlainText()) {
				LOG.info(null, "Copying text/plain data from result ''{0}''", this.result.title());
				content.putString(this.textData.getPlainText());
			}
			if (this.textData.isRtfText()) {
				LOG.info(null, "Copying text/rtf data from result ''{0}''", this.result.title());
				content.putRtf(this.textData.getRtfText());
			}
		}
		Clipboard.getSystemClipboard().setContent(content);
	}

	@Override
	protected void cancelled() {
		LOG.info(null, "Cancelled copying data from result ''{0}''", this.result.title());
	}

	@Override
	protected void failed() {
		LOG.error(getException(), null, "Unable to copy data from result ''{0}''", this.result.title());
	}

}
