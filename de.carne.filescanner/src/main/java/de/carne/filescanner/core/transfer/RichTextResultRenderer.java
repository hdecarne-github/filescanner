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
import java.util.HashMap;
import java.util.StringTokenizer;

import de.carne.filescanner.core.transfer.RendererStyle.FontInfo;

/**
 * Base class for all {@code FileScannerResultRenderer} implementations that
 * generate Rich Text output.
 */
public abstract class RichTextResultRenderer extends ResultRenderer {

	static final HashMap<String, String> RTF_FORMATS = new HashMap<>();

	static {
		RTF_FORMATS.put("normal", "\\plain ");
		RTF_FORMATS.put("italic", "\\i ");
		RTF_FORMATS.put("oblique", "");
		RTF_FORMATS.put("bold", "\\b ");
	}

	private static class RTFFontInfo {

		private final String family;

		private final String formats;

		private RTFFontInfo(String family, String formats) {
			this.family = family;
			this.formats = formats;
		}

		public static RTFFontInfo fromFontInfo(FontInfo fontInfo) {
			StringTokenizer tokens = new StringTokenizer(fontInfo.name(), " ");
			StringBuilder family = new StringBuilder();
			StringBuilder formats = new StringBuilder();

			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();

				if (family.length() == 0) {
					family.append(token);
					continue;
				}

				String format = RTF_FORMATS.get(token.toLowerCase());

				if (format != null) {
					formats.append(format);
				} else {
					family.append(' ').append(token);
				}
			}
			formats.append(String.format("\\fs%.0f ", fontInfo.size() * 2.0));
			return new RTFFontInfo(family.toString(), formats.toString());
		}

		public String family() {
			return this.family;
		}

		public String formats() {
			return this.formats;
		}

	}

	@Override
	protected void writePrologue() throws IOException, InterruptedException {
		write("{\\rtf1\\ansi");

		String encoding = System.getProperty("file.encoding", "").toLowerCase();

		if (encoding.startsWith("cp") || encoding.startsWith("ms")) {
			write("\\ansicpg", encoding.substring(2));
		}
		write("\\deff0{\\fonttbl{\\f0 ");

		RendererStyle style = getStyle();
		RTFFontInfo fontInfo = RTFFontInfo.fromFontInfo(style.getFontInfo());

		write(fontInfo.family());
		write(";}}\n{\\colortbl\n");
		for (Mode mode : Mode.values()) {
			int color = style.getColor(mode);

			write("\\red", Integer.toString((color >>> 16) & 0xff));
			write("\\green", Integer.toString((color >>> 8) & 0xff));
			write("\\blue", Integer.toString(color & 0xff), ";\n");
		}
		write("}\n\\f0 ", fontInfo.formats());
	}

	@Override
	protected void writeEpilogue() throws IOException, InterruptedException {
		write("\n}\n");
	}

	@Override
	protected void writeBreak() throws IOException, InterruptedException {
		write("\n\\par ");
	}

	@Override
	protected void writeText(Mode mode, String text) throws IOException, InterruptedException {
		write("{\\cf", Integer.toString(mode.ordinal()), " ");

		StringBuilder buffer = new StringBuilder();
		int textLength = text.length();

		for (int textIndex = 0; textIndex < textLength; textIndex++) {
			char textChar = text.charAt(textIndex);

			if (textChar == '\\' || textChar == '{' || textChar == '}') {
				buffer.append('\\').append(textChar);
			} else if (textChar > 0xff) {
				buffer.append("\\u").append(textChar & 0xffff);
			} else {
				buffer.append(textChar);
			}
		}
		write(buffer.toString(), "}");
	}

	@Override
	protected void writeImage(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException {
		// Nothing to do here
	}

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
