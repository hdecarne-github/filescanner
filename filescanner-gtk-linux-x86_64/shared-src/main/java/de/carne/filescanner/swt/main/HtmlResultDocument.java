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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.ContentType;

import de.carne.boot.Exceptions;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.transfer.PlainTextRenderer;
import de.carne.filescanner.engine.transfer.RenderOption;
import de.carne.filescanner.engine.transfer.RenderOutput;
import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.filescanner.engine.transfer.Renderer;
import de.carne.filescanner.engine.transfer.TransferSource;
import de.carne.filescanner.engine.transfer.TransferType;
import de.carne.filescanner.engine.util.CombinedRenderer;
import de.carne.util.Strings;

class HtmlResultDocument extends HttpHandler {

	private final URI serverUri;
	private final String documentPath;
	private final String stylesheetPath;
	private final FileScannerResult result;
	private final @Nullable HtmlNavigation navigation;
	private final Map<String, TransferSource> mediaDataSources = new HashMap<>();
	private final Map<String, Long> hrefPositions = new HashMap<>();
	private final List<Long> pageOffsets = new ArrayList<>();
	private long currentPageOffset = 0l;

	HtmlResultDocument(URI serverUri, String documentPath, String stylesheetPath, FileScannerResult result,
			@Nullable HtmlNavigation navigation) {
		this.serverUri = serverUri;
		this.documentPath = documentPath;
		this.stylesheetPath = stylesheetPath;
		this.result = result;
		this.navigation = navigation;
		this.pageOffsets.add(this.currentPageOffset);
	}

	public FileScannerResult result() {
		return this.result;
	}

	public String documentUrl() {
		return this.serverUri.resolve(this.documentPath).toASCIIString();
	}

	public synchronized void writeTo(Writer htmlWriter, Writer plainWriter) throws IOException {
		try (HtmlRenderer htmlRenderer = new HtmlRenderer(htmlWriter);
				PlainTextRenderer plainRenderer = new PlainTextRenderer(plainWriter);
				CombinedRenderer renderer = new CombinedRenderer(htmlRenderer, plainRenderer)) {
			this.mediaDataSources.clear();
			this.hrefPositions.clear();
			RenderOutput.render(this.result, renderer);
		}
	}

	private static final String REQUEST_PARAMETER_MEDIA_SOURCE_ID = "msid";
	private static final String REQUEST_PARAMETER_HREF_ID = "hrefid";
	private static final String REQUEST_PARAMETER_PAGE_ID = "pageid";

	@Override
	public void service(@Nullable Request request, @Nullable Response response) throws Exception {
		if (request != null && response != null) {
			String mediaSourceId = request.getParameter(REQUEST_PARAMETER_MEDIA_SOURCE_ID);
			String hrefId = request.getParameter(REQUEST_PARAMETER_HREF_ID);
			String pageId = request.getParameter(REQUEST_PARAMETER_PAGE_ID);

			if (mediaSourceId != null) {
				serviceMediaSource(mediaSourceId, response);
			} else if (hrefId != null) {
				serviceNavigation(hrefId, response);
			} else {
				serviceHtml(pageId, response);
			}
		}
	}

	private synchronized void serviceMediaSource(String mediaSourceId, Response response) throws IOException {
		TransferSource mediaSource = this.mediaDataSources.get(mediaSourceId);

		if (mediaSource != null) {
			response.setContentType(ContentType.newContentType(mediaSource.transferType().mimeType(), null));
			try (WritableByteChannel outputChannel = Channels.newChannel(response.getOutputStream())) {
				mediaSource.transfer(outputChannel);
			}
		} else {
			response.sendError(404);
		}
	}

	private synchronized void serviceNavigation(String hrefId, Response response) throws IOException {
		Long hrefPosition = this.hrefPositions.get(hrefId);

		if (hrefPosition != null && this.navigation != null) {
			this.navigation.navigateToPosition(this.result, hrefPosition);
		} else {
			response.sendError(404);
		}
	}

	private synchronized void serviceHtml(@Nullable String pageId, Response response) throws IOException {
		int pageIndex = 0;

		if (pageId != null) {
			try {
				pageIndex = Integer.parseInt(pageId);
			} catch (NumberFormatException e) {
				Exceptions.ignore(e);
			}
			pageIndex = Math.min(Math.max(pageIndex, 1), this.pageOffsets.size()) - 1;
		}
		this.currentPageOffset = this.pageOffsets.get(pageIndex);
		response.setContentType(HtmlResourceType.TEXT_HTML.contentType());
		try (HtmlRenderer renderer = new HtmlRenderer(response.getWriter())) {
			this.mediaDataSources.clear();
			this.hrefPositions.clear();
			RenderOutput.render(this.result, renderer);
		}
	}

	@Override
	public String toString() {
		return documentUri();
	}

	String documentUri() {
		return this.serverUri.resolve(this.documentPath).toASCIIString();
	}

	String stylesheetPath() {
		return this.stylesheetPath;
	}

	String createMediaDataPath(TransferSource source) {
		String mediaDataSourceId = Integer.toString(this.mediaDataSources.size() + 1);

		this.mediaDataSources.put(mediaDataSourceId, source);
		return "?" + REQUEST_PARAMETER_MEDIA_SOURCE_ID + "=" + mediaDataSourceId;
	}

	String createHrefPath(long position) {
		String hrefId = Integer.toString(this.hrefPositions.size() + 1);

		this.hrefPositions.put(hrefId, position);
		return "?" + REQUEST_PARAMETER_HREF_ID + "=" + hrefId;
	}

	String getPagePath(int pageIndex) {
		String pageId = Integer.toString(pageIndex + 1);

		return "?" + REQUEST_PARAMETER_PAGE_ID + "=" + pageId;
	}

	private class HtmlRenderer implements Renderer {

		private final Writer writer;
		private int currentIndent;
		private @Nullable RenderStyle currentStyle = null;

		HtmlRenderer(Writer writer) {
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
				this.writer.write(HtmlRendererI18N.i18nPrologueExtended(documentUri(), stylesheetPath(), styles));
			} else {
				this.writer.write(HtmlRendererI18N.i18nPrologueDefault(documentUri(), stylesheetPath()));
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
			applyIndentAndStyle(indent, style);
			this.writer.write(Strings.encodeHtml(text));
			if (lineBreak) {
				emitBreak();
			}
		}

		@Override
		public void emitText(int indent, RenderStyle style, String text, long href, boolean lineBreak)
				throws IOException {
			String hrefPath = createHrefPath(href);

			applyIndentAndStyle(indent, style);
			this.writer.write(HtmlRendererI18N.i18nHref(hrefPath, Strings.encodeHtml(text)));
			if (lineBreak) {
				emitBreak();
			}
		}

		@Override
		public void emitMediaData(int indent, RenderStyle style, TransferSource source, boolean lineBreak)
				throws IOException {
			String mediaDataSourcePath = createMediaDataPath(source);

			applyIndentAndStyle(indent, style);

			TransferType transferType = source.transferType();

			if (transferType.isImage()) {
				this.writer.write(
						HtmlRendererI18N.i18nImg(mediaDataSourcePath, Strings.encodeHtml(transferType.mimeType())));
			} else {
				this.writer.write(
						HtmlRendererI18N.i18nMedia(mediaDataSourcePath, Strings.encodeHtml(transferType.mimeType())));
			}
			if (lineBreak) {
				emitBreak();
			}
		}

		@Override
		public void emitMediaData(int indent, RenderStyle style, TransferSource source, long href, boolean lineBreak)
				throws IOException {
			String mediaDataSourcePath = createMediaDataPath(source);
			String hrefPath = createHrefPath(href);

			applyIndentAndStyle(indent, style);

			TransferType transferType = source.transferType();

			if (transferType.isImage()) {
				this.writer.write(HtmlRendererI18N.i18nHrefImg(hrefPath, mediaDataSourcePath,
						Strings.encodeHtml(transferType.mimeType())));
			} else {
				this.writer.write(HtmlRendererI18N.i18nHrefMedia(hrefPath, mediaDataSourcePath,
						Strings.encodeHtml(transferType.mimeType())));
			}
			if (lineBreak) {
				emitBreak();
			}
		}

		@Override
		public void emitEpilogue() throws IOException {
			applyIndentAndStyle(0, null);
			this.writer.write(HtmlRendererI18N.i18nEpilogue());
		}

		private void applyIndentAndStyle(int indent, @Nullable RenderStyle style) throws IOException {
			if (indent >= 0 && this.currentIndent != indent) {
				if (this.currentStyle != null) {
					this.writer.write(HtmlRendererI18N.i18nStyleEnd());
				}
				while (this.currentIndent < indent) {
					this.writer.write(HtmlRendererI18N.i18nIndentIn());
					this.currentIndent++;
				}
				while (this.currentIndent > indent) {
					this.writer.write(HtmlRendererI18N.i18nIndentOut());
					this.currentIndent--;
				}
				if (style != null) {
					this.writer.write(HtmlRendererI18N.i18nStyleStart(style.shortName()));
				}
			} else if (this.currentStyle != style) {
				if (this.currentStyle != null) {
					this.writer.write(HtmlRendererI18N.i18nStyleEnd());
				}
				if (style != null) {
					this.writer.write(HtmlRendererI18N.i18nStyleStart(style.shortName()));
				}
			}
			this.currentStyle = style;
		}

		private void emitBreak() throws IOException {
			if (this.currentStyle != null) {
				this.writer.write(HtmlRendererI18N.i18nStyleEnd());
				this.currentStyle = null;
			}
			this.writer.write(HtmlRendererI18N.i18nBreak());
		}

		@Override
		public String toString() {
			return Objects.toString(this.writer);
		}

	}

}
