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
package de.carne.filescanner.core.transfer;

import java.io.IOException;

import de.carne.filescanner.core.FileScannerResult;

/**
 * Base class for text exporter.
 */
public abstract class TextResultExporter extends ResultExporter {

	/**
	 * Default {@linkplain TextResultExporter} implementation which uses the
	 * result's render function to provide a textual representation of the
	 * result.
	 */
	public static final TextResultExporter RENDER_TEXT_EXPORTER = new TextResultExporter("Text") {

		@Override
		public Text export(FileScannerResult result, RendererStyle style) throws IOException, InterruptedException {
			StringPlainTextResultRenderer plainTextRenderer = new StringPlainTextResultRenderer();
			StringRichTextResultRenderer richTextRenderer = new StringRichTextResultRenderer();
			CombinedResultRenderer combinedRenderer = new CombinedResultRenderer(plainTextRenderer, richTextRenderer);

			richTextRenderer.setStyle(style);
			result.render(combinedRenderer);
			System.out.println(richTextRenderer.toString());
			return new Text(plainTextRenderer.toString(), richTextRenderer.toString());
		}

	};

	/**
	 * Result object holding the exported text.
	 * <p>
	 * The exported text can be available in more than one format.
	 * </p>
	 */
	public static final class Text {

		private final String plainText;

		private final String rtfText;

		Text(String plainText, String rtfText) {
			assert plainText != null || rtfText != null;

			this.plainText = plainText;
			this.rtfText = rtfText;
		}

		/**
		 * Check whether the exported text is available as plain text.
		 *
		 * @return {@code true} if the exported text is available as plain text.
		 */
		public boolean isPlainText() {
			return this.plainText != null;
		}

		/**
		 * Get the plain text representation of the exported data.
		 *
		 * @return The plain text representation of the exported data or
		 *         {@code null} if it is not available in plain text.
		 * @see #isPlainText()
		 */
		public String getPlainText() {
			return this.plainText;
		}

		/**
		 * Check whether the exported text is available as Rich Text.
		 *
		 * @return {@code true} if the exported text is available as Rich Text.
		 */
		public boolean isRtfText() {
			return this.rtfText != null;
		}

		/**
		 * Get the Rich Text representation of the exported data.
		 *
		 * @return The Rich Text representation of the exported data or
		 *         {@code null} if it is not available in plain text.
		 * @see #isRtfText()
		 */
		public String getRtfText() {
			return this.rtfText;
		}

	}

	/**
	 * Construct {@code TextResultExporter}.
	 *
	 * @param name The exporter name.
	 */
	protected TextResultExporter(String name) {
		super(name);
	}

	/**
	 * Export the submitted result object to one or more text formats.
	 *
	 * @param result The result object to export.
	 * @param style The style to use for rendering.
	 * @return The exported text (see {@linkplain Text}).
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the exporter thread was interrupted.
	 */
	public abstract Text export(FileScannerResult result, RendererStyle style) throws IOException, InterruptedException;

}
