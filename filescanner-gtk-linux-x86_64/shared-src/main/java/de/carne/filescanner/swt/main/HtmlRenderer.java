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
import java.io.Writer;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import de.carne.filescanner.engine.transfer.RenderOption;
import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.filescanner.engine.transfer.Renderer;
import de.carne.filescanner.engine.util.EmitCounter;
import de.carne.filescanner.swt.preferences.Config;

class HtmlRenderer implements Renderer {

	private final Writer writer;
	private final String header;

	public HtmlRenderer(Writer writer, Config config) {
		this(writer, prepareHeader(config));
	}

	public HtmlRenderer(Writer writer, String header) {
		this.writer = writer;
		this.header = header;
	}

	public static String prepareHeader(Config config) {
		StringBuilder header = new StringBuilder();

		header.append("<!DOCTYPE HTML><html><head><meta charset=\"utf-8\"><style>");
		header.append("body { ");
		cssFont(header, config.getResultViewFont());
		header.append(" }");
		header.append(" .transparent { background-image: url(\"/").append(RESOURCE_TRANSPARENT).append("\"); }");
		for (RenderStyle style : RenderStyle.values()) {
			header.append(" .").append(style.name().toLowerCase()).append(" {");
			cssColor(header, config.getResultViewColor(style));
			header.append("}");
		}
		header.append(" </style></head>");
		return header.toString();
	}

	private static final String RESOURCE_TRANSPARENT = "transparent.png";

	private static void cssFont(StringBuilder css, FontData font) {
		css.append("font-family:\"").append(font.getName()).append("\";");
		css.append("font-style:");
		if ((font.getStyle() & SWT.ITALIC) != 0) {
			css.append("italic;");
		} else {
			css.append("normal;");
		}
		css.append("font-weight:");
		if ((font.getStyle() & SWT.BOLD) != 0) {
			css.append("bold;");
		} else {
			css.append("normal;");
		}
		css.append("font-size:").append(font.getHeight()).append("pt;");
	}

	private static void cssColor(StringBuilder css, RGB rgb) {
		css.append("color:rgb(").append(rgb.red).append(",").append(rgb.green).append(",").append(rgb.blue)
				.append(");");
	}

	@Override
	public void close() throws IOException {
		this.writer.close();
	}

	@Override
	public void emitPrologue(Set<RenderOption> options) throws IOException {
		this.writer.write(this.header);
		if (options.contains(RenderOption.TRANSPARENCY)) {
			this.writer.write("<body class=\"transparent\">");
		} else {
			this.writer.write("<body>");
		}
	}

	@Override
	public int emitText(RenderStyle style, String text, boolean lineBreak) throws IOException {
		EmitCounter counter = new EmitCounter();

		this.writer.write(counter.count("<span class=\""));
		this.writer.write(counter.count(style.name().toLowerCase()));
		this.writer.write(counter.count("\">"));
		this.writer.write(counter.count(text));
		this.writer.write(counter.count("</span>"));
		if (lineBreak) {
			this.writer.write(counter.count("<br>"));
		}
		return counter.value();
	}

	@Override
	public void emitEpilouge() throws IOException {
		this.writer.write("</body></html>");
	}

	@Override
	public String toString() {
		return this.writer.toString();
	}

}
