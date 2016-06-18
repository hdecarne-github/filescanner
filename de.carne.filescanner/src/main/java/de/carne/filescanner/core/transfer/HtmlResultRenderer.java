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
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import de.carne.ApplicationLoader;
import de.carne.filescanner.core.transfer.RendererStyle.FontInfo;
import de.carne.filescanner.util.Hexadecimal;
import de.carne.util.MRUList;

/**
 * Base class for all {@code FileScannerResultRenderer} implementations that
 * generate HTML output.
 */
public abstract class HtmlResultRenderer extends ResultRenderer {

	private static final String TRANSPARENCY_BACKGROUND_LOCATION = ApplicationLoader
			.getDirectURL(HtmlResultRenderer.class.getResource("transparency.png")).toExternalForm();

	static final HashMap<String, String> CSS_FONT_STYLES = new HashMap<>();

	static {
		CSS_FONT_STYLES.put("normal", "normal");
		CSS_FONT_STYLES.put("italic", "italic");
		CSS_FONT_STYLES.put("oblique", "oblique");
	}

	static final HashMap<String, String> CSS_FONT_WEIGHTS = new HashMap<>();

	static {
		CSS_FONT_WEIGHTS.put("bold", "bold");
	}

	private static class CSSFontInfo {

		private final String family;

		private final String style;

		private final String weight;

		private final String size;

		private CSSFontInfo(String family, String style, String weight, String size) {
			this.family = family;
			this.style = style;
			this.weight = weight;
			this.size = size;
		}

		public static CSSFontInfo fromFontInfo(FontInfo fontInfo) {
			StringTokenizer tokens = new StringTokenizer(fontInfo.name(), " ");
			StringBuilder family = new StringBuilder();
			String style = null;
			String weight = null;

			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();

				if (family.length() == 0) {
					family.append(token);
					continue;
				}
				if (style == null) {
					style = CSS_FONT_STYLES.get(token.toLowerCase());
					if (style != null) {
						continue;
					}
				}
				if (weight == null) {
					weight = CSS_FONT_WEIGHTS.get(token.toLowerCase());
					if (weight != null) {
						continue;
					}
				}
				if (style == null && weight == null) {
					family.append(' ').append(token);
				}
			}

			String size = String.format("%.0fpx", fontInfo.size());

			return new CSSFontInfo(family.toString(), style, weight, size);
		}

		public String family() {
			return this.family;
		}

		public boolean hasStyle() {
			return this.style != null;
		}

		public String style() {
			return this.style;
		}

		public boolean hasWeight() {
			return this.weight != null;
		}

		public String weight() {
			return this.weight;
		}

		public String size() {
			return this.size;
		}

	}

	private static class CSSColor {

		private final String color;

		private CSSColor(String color) {
			this.color = color;
		}

		public static CSSColor fromInt(int i) {
			return new CSSColor("rgb(" + ((i >>> 16) & 0xff) + "," + ((i >>> 8) & 0xff) + "," + (i & 0xff) + ")");
		}

		public String color() {
			return this.color;
		}

	}

	private static String preparePrologue(RendererStyle style) {
		StringBuilder buffer = new StringBuilder();

		buffer.append("<!DOCTYPE HTML>\n<html>\n<head>\n<meta charset=\"utf-8\">\n<style>\nbody {\n");
		CSSFontInfo cssFontInfo = CSSFontInfo.fromFontInfo(style.getFontInfo());

		buffer.append("font-family: ").append(cssFontInfo.family()).append(";\n");
		if (cssFontInfo.hasStyle()) {
			buffer.append("font-style: ").append(cssFontInfo.style()).append(";\n");
		}
		if (cssFontInfo.hasWeight()) {
			buffer.append("font-weight: ").append(cssFontInfo.weight()).append(";\n");
		}
		buffer.append("font-size: ");
		buffer.append(cssFontInfo.size());
		buffer.append(";\nwhite-space: nowrap;\n}\n.transparent {\nbackground-image: url(\"");
		buffer.append(TRANSPARENCY_BACKGROUND_LOCATION);
		buffer.append("\");\n}\n.normal {\ncolor: ");
		buffer.append(CSSColor.fromInt(style.getColor(Mode.NORMAL)).color());
		buffer.append(";\n}\n.value {\ncolor: ");
		buffer.append(CSSColor.fromInt(style.getColor(Mode.VALUE)).color());
		buffer.append(";\n}\n.comment {\ncolor: ");
		buffer.append(CSSColor.fromInt(style.getColor(Mode.COMMENT)).color());
		buffer.append(";\n}\n.keyword {\ncolor: ");
		buffer.append(CSSColor.fromInt(style.getColor(Mode.KEYWORD)).color());
		buffer.append(";\n}\n.operator {\ncolor: ");
		buffer.append(CSSColor.fromInt(style.getColor(Mode.OPERATOR)).color());
		buffer.append(";\n}\n.label {\ncolor: ");
		buffer.append(CSSColor.fromInt(style.getColor(Mode.LABEL)).color());
		buffer.append(";\n}\n.error {\ncolor: ");
		buffer.append(CSSColor.fromInt(style.getColor(Mode.ERROR)).color());
		buffer.append(";\n}\n</style>\n</head>\n");
		return buffer.toString();
	}

	private static final MRUList<RendererStyle, String> PROLOGUE_CACHE = new MRUList<>(1);

	@Override
	protected void writePrologue() throws IOException, InterruptedException {
		RendererStyle style = getStyle();
		String prologue = PROLOGUE_CACHE.use(style);

		if (prologue == null) {
			prologue = preparePrologue(style);
			PROLOGUE_CACHE.use(style, prologue);
		}
		write(prologue);
		write("</head>\n<body");

		Set<Feature> features = getFeatures();

		if (features.contains(Feature.TRANSPARENCY)) {
			write(" class=\"transparent\"");
		}
		write(">\n");
	}

	@Override
	protected void writeEpilogue() throws IOException, InterruptedException {
		write("</body>\n</html>\n");
	}

	@Override
	protected void writeBeginMode(Mode mode) throws IOException, InterruptedException {
		write("<span class=\"", mode.name().toLowerCase(), "\">");
	}

	@Override
	protected void writeEndMode(Mode mode) throws IOException, InterruptedException {
		write("</span>");
	}

	@Override
	protected void writeBreak() throws IOException, InterruptedException {
		write("<br/>\n");
	}

	@Override
	protected void writeText(Mode mode, String text) throws IOException, InterruptedException {
		write(htmlEncode(text));
	}

	@Override
	protected void writeRefText(Mode mode, String text, long position) throws IOException, InterruptedException {
		write("<a href=\"#", Hexadecimal.formatL(position), "\">", htmlEncode(text), "</a>");
	}

	@Override
	protected void writeImage(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException {
		write("<img src=\"", registerStreamHandler(streamHandler).toExternalForm(), "\"/>");
	}

	@Override
	protected void writeRefImage(Mode mode, StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		write("<a href=\"#", Hexadecimal.formatL(position), "\"><img src=\"",
				registerStreamHandler(streamHandler).toExternalForm(), "\"/></a>");
	}

	@Override
	protected void writeVideo(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException {
		write("<video src=\"", registerStreamHandler(streamHandler).toExternalForm(), "\"/>");
	}

	@Override
	protected void writeRefVideo(Mode mode, StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		write("<a href=\"#", Hexadecimal.formatL(position), "\"><video src=\"",
				registerStreamHandler(streamHandler).toExternalForm(), "\"/></a>");
	}

	private String htmlEncode(String decoded) {
		StringBuilder encoded = new StringBuilder(decoded.length());

		decoded.chars().forEachOrdered(c -> encodeCodePoint(encoded, c));
		return encoded.toString();
	}

	private static void encodeCodePoint(StringBuilder encoded, int c) {
		switch (c) {
		case '&':
			encoded.append("&amp;");
			break;
		case '"':
			encoded.append("&quot;");
			break;
		case '\'':
			encoded.append("&#039;");
			break;
		case '<':
			encoded.append("&lt;");
			break;
		case '>':
			encoded.append("&gt;");
			break;
		default:
			encoded.append((char) c);
		}
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
