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
import java.net.URL;
import java.util.Set;

import de.carne.filescanner.util.Hexadecimal;

/**
 * Base class for all {@code FileScannerResultRenderer} implementations that
 * generate HTML output.
 */
public abstract class HtmlResultRenderer extends FileScannerResultRenderer {

	private final String styleSheetLocation;

	/**
	 * Construct {@code HtmlResultRenderer}.
	 *
	 * @param styleSheetLocation Optional reference to a style sheet.
	 */
	protected HtmlResultRenderer(String styleSheetLocation) {
		this.styleSheetLocation = styleSheetLocation;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writePreamble(java.
	 * util.Set)
	 */
	@Override
	protected void writePreamble(Set<Feature> features) throws IOException, InterruptedException {
		write("<!DOCTYPE HTML>\n<html>\n<head>\n<meta charset=\"utf-8\">\n");
		if (this.styleSheetLocation != null) {
			write("<link rel=\"stylesheet\" href=\"" + this.styleSheetLocation + "\"");
		}

		String bodyStyle = (features.contains(Feature.TRANSPARENCY) ? " class=\"transparent\"" : "");

		write("</head>\n<body" + bodyStyle + ">\n");
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.spi.FileScannerResultRenderer#writeEpilogue()
	 */
	@Override
	protected void writeEpilogue() throws IOException, InterruptedException {
		write("</body>\n</html>\n");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeBeginMode(de.
	 * carne.filescanner.spi.FileScannerResultRenderer.Mode)
	 */
	@Override
	protected void writeBeginMode(Mode mode) throws IOException, InterruptedException {
		write("<span class=\"", mode.name().toLowerCase(), "\">");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeEndMode(de.carne.
	 * filescanner.spi.FileScannerResultRenderer.Mode)
	 */
	@Override
	protected void writeEndMode(Mode mode) throws IOException, InterruptedException {
		write("</span>");
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.spi.FileScannerResultRenderer#writeBreak()
	 */
	@Override
	protected void writeBreak() throws IOException, InterruptedException {
		write("<br/>\n");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeText(de.carne.
	 * filescanner.spi.FileScannerResultRenderer.Mode, java.lang.String)
	 */
	@Override
	protected void writeText(Mode mode, String text) throws IOException, InterruptedException {
		write(text);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeRefText(de.carne.
	 * filescanner.spi.FileScannerResultRenderer.Mode, java.lang.String, long)
	 */
	@Override
	protected void writeRefText(Mode mode, String text, long position) throws IOException, InterruptedException {
		write("<a href=\"#", Hexadecimal.formatL(position), "\">", text, "</a>");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeImage(de.carne.
	 * filescanner.spi.FileScannerResultRenderer.Mode,
	 * de.carne.filescanner.spi.FileScannerResultRenderer.StreamHandler)
	 */
	@Override
	protected void writeImage(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException {
		write("<img src=\"", registerStreamHandler(streamHandler).toExternalForm(), "\"/>");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeRefImage(de.carne
	 * .filescanner.spi.FileScannerResultRenderer.Mode,
	 * de.carne.filescanner.spi.FileScannerResultRenderer.StreamHandler, long)
	 */
	@Override
	protected void writeRefImage(Mode mode, StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		write("<a href=\"#", Hexadecimal.formatL(position), "\"><img src=\"",
				registerStreamHandler(streamHandler).toExternalForm(), "\"/></a>");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeVideo(de.carne.
	 * filescanner.spi.FileScannerResultRenderer.Mode,
	 * de.carne.filescanner.spi.FileScannerResultRenderer.StreamHandler)
	 */
	@Override
	protected void writeVideo(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException {
		write("<video src=\"", registerStreamHandler(streamHandler).toExternalForm(), "\"/>");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeRefVideo(de.carne
	 * .filescanner.spi.FileScannerResultRenderer.Mode,
	 * de.carne.filescanner.spi.FileScannerResultRenderer.StreamHandler, long)
	 */
	@Override
	protected void writeRefVideo(Mode mode, StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		write("<a href=\"#", Hexadecimal.formatL(position), "\"><video src=\"",
				registerStreamHandler(streamHandler).toExternalForm(), "\"/></a>");
	}

	/**
	 * Register a {@linkplain StreamHandler} an make it accessible via an
	 * {@linkplain URL}.
	 *
	 * @param streamHandler The {@linkplain StreamHandler} to register.
	 * @return The registered {@linkplain URL}.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected abstract URL registerStreamHandler(StreamHandler streamHandler) throws IOException, InterruptedException;

	/**
	 * Write one or more text artifacts.
	 *
	 * @param artifacts The text artifacts to write.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected abstract void write(String... artifacts) throws IOException, InterruptedException;

}
