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

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import de.carne.boot.check.Nullable;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.transfer.RenderOption;
import de.carne.filescanner.engine.transfer.RenderOutput;
import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.filescanner.engine.transfer.Renderer;
import de.carne.filescanner.engine.transfer.SimpleTextRenderer;
import de.carne.filescanner.engine.util.CombinedRenderer;
import de.carne.filescanner.engine.util.EmitCounter;

class HtmlResultDocument extends HttpHandler {

	private final String documentUrl;
	private final FileScannerResult result;
	private final String stylesheetUrl;

	public HtmlResultDocument(String documentUrl, FileScannerResult result, String stylesheetUrl) {
		this.documentUrl = documentUrl;
		this.result = result;
		this.stylesheetUrl = stylesheetUrl;
	}

	public FileScannerResult result() {
		return this.result;
	}

	public String documentUrl() {
		return this.documentUrl;
	}

	public void writeTo(Writer htmlWriter) throws IOException {
		try (HtmlRenderer renderer = new HtmlRenderer(htmlWriter, this.stylesheetUrl)) {
			RenderOutput.render(this.result, renderer);
		}
	}

	public void writeTo(Writer htmlWriter, Writer plainWriter) throws IOException {
		try (CombinedRenderer renderer = new CombinedRenderer(new HtmlRenderer(htmlWriter, this.stylesheetUrl),
				new SimpleTextRenderer(plainWriter))) {
			RenderOutput.render(this.result, renderer);
		}
	}

	@Override
	public void service(@Nullable Request request, @Nullable Response response) throws Exception {
		if (request != null && response != null) {
			response.setContentType(HtmlResourceType.TEXT_HTML.contentType());
			writeTo(response.getWriter());
		}
	}

	static class HtmlRenderer implements Renderer {

		private final Writer writer;
		private final String stylesheetUrl;

		public HtmlRenderer(Writer writer, String stylesheetUrl) {
			this.writer = writer;
			this.stylesheetUrl = stylesheetUrl;
		}

		@Override
		public void close() throws IOException {
			this.writer.close();
		}

		@Override
		public void emitPrologue(Set<RenderOption> options) throws IOException {
			this.writer.write("<!DOCTYPE HTML><html><head><meta charset=\"utf-8\">"
					+ "<head><link rel=\"stylesheet\" type=\"text/css\" href=\"");
			this.writer.write(this.stylesheetUrl);
			if (options.contains(RenderOption.TRANSPARENCY)) {
				this.writer.write("\"></head><body class=\"transparent\">");
			} else {
				this.writer.write("\"></head><body>");
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

}
