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
package de.carne.filescanner.core.format.spec;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.transfer.FileScannerResultRenderer;
import de.carne.filescanner.util.Printer;
import de.carne.filescanner.util.Units;

/**
 * This class defines string based attributes of variable size.
 */
public abstract class StringAttribute extends Attribute<String> {

	/**
	 * The maximum size to use for string matching, decoding and rendering.
	 */
	public static final int MAX_MATCH_SIZE = 1024;

	private final Charset charset;

	/**
	 * Construct {@code StringAttribute}.
	 *
	 * @param name The attribute's name.
	 * @param charset The charset to use for string decoding.
	 */
	public StringAttribute(String name, Charset charset) {
		super(name);

		assert charset != null;

		this.charset = charset;
	}

	@Override
	public Class<String> getValueType() {
		return String.class;
	}

	/**
	 * Decode the actual string data.
	 * <p>
	 * Maximum {@linkplain #MAX_MATCH_SIZE} number of bytes will be decoded.
	 * </p>
	 *
	 * @param result The result object to read from.
	 * @param position The position to start decoding at.
	 * @param size The byte size of the string to decode.
	 * @return The decoded string data.
	 * @throws IOException if an I/O error occurs.
	 */
	protected CharBuffer decodeString(FileScannerResult result, long position, long size) throws IOException {
		int readSize = (int) Math.min(size, MAX_MATCH_SIZE);
		ByteBuffer buffer = result.cachedRead(position, readSize);

		buffer.limit(buffer.position() + Math.min(readSize, buffer.remaining()));
		return this.charset.decode(buffer);
	}

	/**
	 * Render the actual string data.
	 * <p>
	 * This function takes care of the necessary decoding and furthermore also
	 * adds a comment in case the string data is truncated due to it's size.
	 * </p>
	 *
	 * @param result The result object to read the string data from.
	 * @param position The position to start decoding the string data from at.
	 * @param size The byte size of the string to decode.
	 * @param renderer The renderer to use.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected void renderString(FileScannerResult result, long position, long size, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException {
		StringBuilder buffer = new StringBuilder("\"");

		Printer.format(buffer, decodeString(result, position, size));

		if (size <= MAX_MATCH_SIZE) {
			buffer.append("\"");
			renderer.setValueMode().renderText(buffer.toString());
		} else {
			buffer.append("\u2026\"");
			renderer.setValueMode().renderText(buffer.toString());
			buffer.setLength(0);
			buffer.append(" // remaining ");
			buffer.append(Units.formatByteValue(size - MAX_MATCH_SIZE));
			buffer.append(" omitted");
			renderer.setCommentMode().renderText(buffer.toString());
		}
	}

}
