/*
 * Copyright (c) 2007-2020 Holger de Carne and contributors, All Rights Reserved.
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
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.ContentType;

import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.transfer.PlainTextRenderer;
import de.carne.filescanner.engine.transfer.RenderOption;
import de.carne.filescanner.engine.transfer.RenderOutput;
import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.filescanner.engine.transfer.Renderer;
import de.carne.filescanner.engine.transfer.TransferSource;
import de.carne.filescanner.engine.util.CombinedRenderer;
import de.carne.util.Strings;

class HtmlResultDocument extends HttpHandler {

	static final HtmlNavigation NO_NAVIGATION = (result, position) -> {
		throw new IOException();
	};

	private final URI serverUri;
	private final String documentPath;
	private final String stylesheetPath;
	private final HtmlNavigation navigation;
	private final FileScannerResult result;
	private final Map<String, TransferSource> mediaDataSources = new HashMap<>();
	private final Map<String, Long> hrefPositions = new HashMap<>();

	public HtmlResultDocument(URI serverUri, String documentPath, String stylesheetPath, HtmlNavigation navigation,
			FileScannerResult result) {
		this.serverUri = serverUri;
		this.documentPath = documentPath;
		this.stylesheetPath = stylesheetPath;
		this.navigation = navigation;
		this.result = result;
	}

	public FileScannerResult result() {
		return this.result;
	}

	public String documentUrl() {
		return this.serverUri.resolve(this.documentPath).toASCIIString();
	}

	public void writeTo(Writer htmlWriter) throws IOException {
		try (HtmlRenderer renderer = new HtmlRenderer(htmlWriter)) {
			RenderOutput.render(this.result, renderer);
		}
	}

	public void writeTo(Writer htmlWriter, Writer plainWriter) throws IOException {
		try (HtmlRenderer htmlRenderer = new HtmlRenderer(htmlWriter);
				PlainTextRenderer plainRenderer = new PlainTextRenderer(plainWriter);
				CombinedRenderer renderer = new CombinedRenderer(htmlRenderer, plainRenderer)) {
			RenderOutput.render(this.result, renderer);
		}
	}

	private static final String REQUEST_PARAMETER_MEDIA_SOURCE_ID = "msid";
	private static final String REQUEST_PARAMETER_HREF_ID = "hrefid";

	@Override
	public void service(@Nullable Request request, @Nullable Response response) throws Exception {
		if (request != null && response != null) {
			String mediaSourceId = request.getParameter(REQUEST_PARAMETER_MEDIA_SOURCE_ID);
			TransferSource mediaSource;
			String hrefId = request.getParameter(REQUEST_PARAMETER_HREF_ID);
			Long hrefPosition;

			if ((mediaSource = this.mediaDataSources.get(mediaSourceId)) != null) {
				response.setContentType(ContentType.newContentType(mediaSource.transferType().mimeType(), null));
				try (WritableByteChannel outputChannel = Channels.newChannel(response.getOutputStream())) {
					mediaSource.transfer(outputChannel);
				}
			} else if (!NO_NAVIGATION.equals(this.navigation)
					&& (hrefPosition = this.hrefPositions.get(hrefId)) != null) {
				this.navigation.navigateToPosition(this.result, hrefPosition);
			} else if (mediaSourceId == null && hrefId == null) {
				response.setContentType(HtmlResourceType.TEXT_HTML.contentType());
				writeTo(response.getWriter());
			} else {
				response.sendError(404);
			}
		}
	}

	@Override
	public String toString() {
		return this.serverUri.resolve(this.documentPath).toASCIIString();
	}

	String serverUri() {
		return this.serverUri.toASCIIString();
	}

	String stylesheetPath() {
		return this.stylesheetPath;
	}

	String createMediaDataPath(TransferSource source) {
		String mediaDataSourceId = Integer.toHexString(this.mediaDataSources.size() + 1);

		this.mediaDataSources.put(mediaDataSourceId, source);

		return this.documentPath + "/?" + REQUEST_PARAMETER_MEDIA_SOURCE_ID + "=" + mediaDataSourceId;
	}

	String createHrefPath(long position) {
		String hrefId = Integer.toHexString(this.hrefPositions.size() + 1);

		this.hrefPositions.put(hrefId, position);

		return this.documentPath + "/?" + REQUEST_PARAMETER_HREF_ID + "=" + hrefId;
	}

	private class HtmlRenderer implements Renderer {

		private final Writer writer;
		private int lastIndent;

		public HtmlRenderer(Writer writer) {
			this.writer = writer;
		}

		@Override
		public void close() throws IOException {
			this.writer.close();
		}

		@Override
		public void emitPrologue(Set<RenderOption> options) throws IOException {
			StringBuilder styles = new StringBuilder();

			addPrologueStyle(styles, options, RenderOption.TRANSPARENCY, "transparent");
			addPrologueStyle(styles, options, RenderOption.WRAP, "wrap");
			if (styles.length() > 0) {
				this.writer.write(HtmlRendererI18N.i18nPrologueExtended(serverUri(), stylesheetPath(), styles));
			} else {
				this.writer.write(HtmlRendererI18N.i18nPrologueDefault(serverUri(), stylesheetPath()));
			}
		}

		private void addPrologueStyle(StringBuilder styles, Set<RenderOption> options, RenderOption option,
				String style) {
			if (options.contains(option)) {
				if (styles.length() > 0) {
					styles.append(",");
				}
				styles.append(style);
			}
		}

		@Override
		public void emitText(int indent, RenderStyle style, String text, boolean lineBreak) throws IOException {
			applyIndent(indent);
			if (lineBreak) {
				this.writer.write(
						HtmlRendererI18N.i18nSimpleTextWithBreak(style.name().toLowerCase(), Strings.encodeHtml(text)));
			} else {
				this.writer.write(HtmlRendererI18N.i18nSimpleTextWithoutBreak(style.name().toLowerCase(),
						Strings.encodeHtml(text)));
			}
		}

		@Override
		public void emitText(int indent, RenderStyle style, String text, long href, boolean lineBreak)
				throws IOException {
			String hrefPath = createHrefPath(href);

			applyIndent(indent);
			if (lineBreak) {
				this.writer.write(HtmlRendererI18N.i18nHrefTextWithBreak(style.name().toLowerCase(),
						Strings.encodeHtml(text), hrefPath));
			} else {
				this.writer.write(HtmlRendererI18N.i18nHrefTextWithoutBreak(style.name().toLowerCase(),
						Strings.encodeHtml(text), hrefPath));
			}
		}

		@Override
		public void emitMediaData(int indent, RenderStyle style, TransferSource source, boolean lineBreak)
				throws IOException {
			String mediaDataSourcePath = createMediaDataPath(source);

			applyIndent(indent);
			if (source.transferType().isImage()) {
				if (lineBreak) {
					this.writer.write(HtmlRendererI18N.i18nSimpleImageWithoutBreak(style.name().toLowerCase(),
							mediaDataSourcePath, Strings.encodeHtml(source.name())));
				} else {
					this.writer.write(HtmlRendererI18N.i18nSimpleImageWithBreak(style.name().toLowerCase(),
							mediaDataSourcePath, Strings.encodeHtml(source.name())));
				}
			} else {
				if (lineBreak) {
					this.writer.write(HtmlRendererI18N.i18nSimpleMediaWithoutBreak(style.name().toLowerCase(),
							mediaDataSourcePath, Strings.encodeHtml(source.name())));
				} else {
					this.writer.write(HtmlRendererI18N.i18nSimpleMediaWithBreak(style.name().toLowerCase(),
							mediaDataSourcePath, Strings.encodeHtml(source.name())));
				}
			}
		}

		@Override
		public void emitMediaData(int indent, RenderStyle style, TransferSource source, long href, boolean lineBreak)
				throws IOException {
			String mediaDataSourcePath = createMediaDataPath(source);
			String hrefPath = createHrefPath(href);

			applyIndent(indent);
			if (source.transferType().isImage()) {
				if (lineBreak) {
					this.writer.write(HtmlRendererI18N.i18nHrefImageWithoutBreak(style.name().toLowerCase(),
							mediaDataSourcePath, Strings.encodeHtml(source.name()), hrefPath));
				} else {
					this.writer.write(HtmlRendererI18N.i18nHrefImageWithBreak(style.name().toLowerCase(),
							mediaDataSourcePath, Strings.encodeHtml(source.name()), hrefPath));
				}
			} else {
				if (lineBreak) {
					this.writer.write(HtmlRendererI18N.i18nHrefMediaWithoutBreak(style.name().toLowerCase(),
							mediaDataSourcePath, Strings.encodeHtml(source.name()), hrefPath));
				} else {
					this.writer.write(HtmlRendererI18N.i18nHrefMediaWithBreak(style.name().toLowerCase(),
							mediaDataSourcePath, Strings.encodeHtml(source.name()), hrefPath));
				}
			}
		}

		@Override
		public void emitEpilogue() throws IOException {
			applyIndent(0);
			this.writer.write(HtmlRendererI18N.i18nEpilogue());
		}

		private void applyIndent(int indent) throws IOException {
			if (indent >= 0) {
				while (this.lastIndent < indent) {
					this.writer.write(HtmlRendererI18N.i18nIndentIn());
					this.lastIndent++;
				}
				while (this.lastIndent > indent) {
					this.writer.write(HtmlRendererI18N.i18nIndentOut());
					this.lastIndent--;
				}
			}
		}

		@Override
		public String toString() {
			return Objects.toString(this.writer);
		}

	}

}
