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

import de.carne.filescanner.spi.FileScannerResultRenderer;
import de.carne.util.Strings;

/**
 * Base class for all {@code FileScannerResultRenderer} implementations that
 * generate simple text output.
 */
public abstract class TextResultRenderer extends FileScannerResultRenderer {

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.spi.FileScannerResultRenderer#writeBreak()
	 */
	@Override
	protected void writeBreak() throws IOException, InterruptedException {
		write(Strings.NEWLINE);
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
	 * de.carne.filescanner.spi.FileScannerResultRenderer#writeImage(de.carne.
	 * filescanner.spi.FileScannerResultRenderer.Mode,
	 * de.carne.filescanner.spi.FileScannerResultRenderer.StreamHandler)
	 */
	@Override
	protected void writeImage(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException {
		// Nothing to do here
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
		// Nothing to do here
	}

	/**
	 * Write one or more text artifacts.
	 *
	 * @param artifacts The text artifacts to write.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected abstract void write(String... artifacts) throws IOException, InterruptedException;

}
