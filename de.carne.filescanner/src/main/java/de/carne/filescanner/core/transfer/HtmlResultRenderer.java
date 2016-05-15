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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import de.carne.filescanner.spi.FileScannerResultRenderer;

/**
 * {@code FileScannerResultRenderer} implementation generating HTML output.
 */
class HtmlResultRenderer extends FileScannerResultRenderer {

	private final HtmlResultRendererURLHandler urlHandler;

	private final OutputStreamWriter out;

	public HtmlResultRenderer(HtmlResultRendererURLHandler urlHandler, OutputStream out) {
		this.urlHandler = urlHandler;
		this.out = new OutputStreamWriter(out, StandardCharsets.UTF_8);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.spi.FileScannerResultRenderer#writePreamble()
	 */
	@Override
	protected void writePreamble() throws IOException {
		this.out.write("<!DOCTYPE HTML>\n<html>\n<head>\n<meta charset=\"utf-8\">\n</head>\n<body>\n");
		this.out.flush();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.spi.FileScannerResultRenderer#writeEpilogue()
	 */
	@Override
	protected void writeEpilogue() throws IOException {
		this.out.write("</body>\n</html>\n");
		this.out.flush();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeBeginMode(de.
	 * carne.filescanner.spi.FileScannerResultRenderer.Mode)
	 */
	@Override
	protected void writeBeginMode(Mode mode) throws IOException {
		this.out.write("<span class=\"");
		this.out.write(mode.name().toLowerCase());
		this.out.write("\">");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeEndMode(de.carne.
	 * filescanner.spi.FileScannerResultRenderer.Mode)
	 */
	@Override
	protected void writeEndMode(Mode mode) throws IOException {
		this.out.write("</span>");
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.spi.FileScannerResultRenderer#writeBreak()
	 */
	@Override
	protected void writeBreak() throws IOException {
		this.out.write("<br/>\n");
		this.out.flush();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeText(de.carne.
	 * filescanner.spi.FileScannerResultRenderer.Mode, java.lang.String)
	 */
	@Override
	protected void writeText(Mode mode, String text) throws IOException {
		this.out.write(text);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeRefText(de.carne.
	 * filescanner.spi.FileScannerResultRenderer.Mode, java.lang.String, long)
	 */
	@Override
	protected void writeRefText(Mode mode, String text, long position) throws IOException {
		// TODO Auto-generated method stub
		super.writeRefText(mode, text, position);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeImage(de.carne.
	 * filescanner.spi.FileScannerResultRenderer.Mode,
	 * de.carne.filescanner.spi.FileScannerResultRenderer.StreamHandler)
	 */
	@Override
	protected void writeImage(Mode mode, StreamHandler streamHandler) throws IOException {
		this.out.write("<img src=\"");
		this.out.write(this.urlHandler.openStream(streamHandler).toExternalForm());
		this.out.write("\"/>");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeRefImage(de.carne
	 * .filescanner.spi.FileScannerResultRenderer.Mode,
	 * de.carne.filescanner.spi.FileScannerResultRenderer.StreamHandler, long)
	 */
	@Override
	protected void writeRefImage(Mode mode, StreamHandler streamHandler, long position) throws IOException {
		// TODO Auto-generated method stub
		super.writeRefImage(mode, streamHandler, position);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeVideo(de.carne.
	 * filescanner.spi.FileScannerResultRenderer.Mode,
	 * de.carne.filescanner.spi.FileScannerResultRenderer.StreamHandler)
	 */
	@Override
	protected void writeVideo(Mode mode, StreamHandler streamHandler) throws IOException {
		this.out.write("<video src=\"");
		this.out.write(this.urlHandler.openStream(streamHandler).toExternalForm());
		this.out.write("\"/>");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeRefVideo(de.carne
	 * .filescanner.spi.FileScannerResultRenderer.Mode,
	 * de.carne.filescanner.spi.FileScannerResultRenderer.StreamHandler, long)
	 */
	@Override
	protected void writeRefVideo(Mode mode, StreamHandler streamHandler, long position) throws IOException {
		// TODO Auto-generated method stub
		super.writeRefVideo(mode, streamHandler, position);
	}

}
