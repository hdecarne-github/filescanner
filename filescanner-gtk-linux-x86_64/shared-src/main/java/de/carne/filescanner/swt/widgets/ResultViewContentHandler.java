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
package de.carne.filescanner.swt.widgets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jdt.annotation.Nullable;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.ContentType;

import de.carne.boot.Exceptions;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.transfer.FileScannerResultRenderHandler;
import de.carne.filescanner.engine.transfer.RenderOption;
import de.carne.filescanner.engine.transfer.RenderOutput;
import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.filescanner.engine.transfer.Renderer;
import de.carne.filescanner.engine.transfer.TransferSource;
import de.carne.filescanner.engine.transfer.TransferType;
import de.carne.util.Strings;

class ResultViewContentHandler extends HttpHandler {

	public static final TransferSource NO_CONTENT = new TransferSource() {

		@Override
		public String name() {
			return "";
		}

		@Override
		public TransferType transferType() {
			return TransferType.TEXT_PLAIN;
		}

		@Override
		public long size() {
			return 0;
		}

		@Override
		public void transfer(WritableByteChannel target) throws IOException {
			// Nothing to do here
		}

		@Override
		public void transfer(OutputStream target) throws IOException {
			// Nothing to do here
		}

	};

	private final TransferSource defaultContentSource = new TransferSource() {

		@Override
		public String name() {
			return transferType().mimeType();
		}

		@Override
		public TransferType transferType() {
			return TransferType.TEXT_HTML;
		}

		@Override
		public long size() {
			return -1;
		}

		@Override
		public void transfer(WritableByteChannel target) throws IOException {
			try (Writer writer = Channels.newWriter(target, StandardCharsets.UTF_8.newEncoder(), -1)) {
				renderDefault(writer);
			}
		}

		@Override
		public void transfer(OutputStream target) throws IOException {
			try (Writer writer = new OutputStreamWriter(target, StandardCharsets.UTF_8)) {
				renderDefault(writer);
			}
		}

	};

	private final URI documentUri;
	private final ResultView resultView;
	private final FileScannerResult result;
	private final @Nullable FileScannerResultRenderHandler renderHandler;
	private final Map<String, TransferSource> mediaDataSources = new HashMap<>();
	private final Map<String, Long> hrefPositions = new HashMap<>();
	private final List<Long> renderOffsets = new ArrayList<>();
	private boolean renderOffsetsComplete = false;
	private long currentRenderOffset = 0l;
	private TransferSource contentSource = NO_CONTENT;

	ResultViewContentHandler(URI documentUri, ResultView resultView, FileScannerResult result,
			@Nullable FileScannerResultRenderHandler renderHandler) {
		this.documentUri = documentUri;
		this.resultView = resultView;
		this.result = result;
		this.renderHandler = renderHandler;
		this.renderOffsets.add(this.currentRenderOffset);
		this.resultView.updatePagination(buildPageLinks());
	}

	public URI documentUri() {
		return this.documentUri;
	}

	public URI documentPageUri(String page) {
		return this.documentUri.resolve(this.documentUri.getPath() + "/?" + REQUEST_PARAMETER_PAGE_ID + "=" + page);
	}

	public ResultView resultView() {
		return this.resultView;
	}

	public FileScannerResult result() {
		return this.result;
	}

	public TransferSource getContent() {
		return this.contentSource;
	}

	private static final String REQUEST_PARAMETER_MEDIA_DATA_ID = "mdid";
	private static final String REQUEST_PARAMETER_HREF_ID = "hrefid";
	private static final String REQUEST_PARAMETER_PAGE_ID = "pageid";

	@Override
	public void service(Request request, Response response) throws IOException {
		String mediaDataId = request.getParameter(REQUEST_PARAMETER_MEDIA_DATA_ID);
		String hrefId = request.getParameter(REQUEST_PARAMETER_HREF_ID);
		String pageId = request.getParameter(REQUEST_PARAMETER_PAGE_ID);

		if (mediaDataId != null) {
			serviceMediaData(response, mediaDataId);
		} else if (hrefId != null) {
			serviceNavigation(response, hrefId);
		} else {
			serviceDefault(response, pageId);
		}
	}

	private synchronized void serviceMediaData(Response response, String mediaDataId) throws IOException {
		TransferSource mediaSource = this.mediaDataSources.get(mediaDataId);

		if (mediaSource != null) {
			if (!mediaSource.transferType().isImage()) {
				this.contentSource = mediaSource;
			}
			response.setContentType(ContentType.newContentType(mediaSource.transferType().mimeType(), null));
			mediaSource.transfer(response.getOutputStream());
			this.resultView.getDisplay().syncExec(() -> this.resultView.updatePagination(buildPageLinks()));
		} else {
			this.contentSource = NO_CONTENT;
			response.sendError(404);
		}
	}

	private synchronized void serviceNavigation(Response response, String hrefId) throws IOException {
		this.contentSource = NO_CONTENT;

		Long hrefPosition = this.hrefPositions.get(hrefId);

		if (hrefPosition != null) {
			AtomicReference<String> toUrl = new AtomicReference<>();

			this.resultView.getDisplay()
					.syncExec(() -> toUrl.set(this.resultView.navigateTo(this.result, hrefPosition)));
			response.sendRedirect(toUrl.get());
		} else {
			response.sendError(404);
		}
	}

	private synchronized void serviceDefault(Response response, @Nullable String pageId) throws IOException {
		this.contentSource = this.defaultContentSource;

		int pageIndex = 0;

		if (pageId != null) {
			try {
				pageIndex = Integer.parseInt(pageId);
			} catch (NumberFormatException e) {
				Exceptions.ignore(e);
			}
			pageIndex = Math.min(Math.max(pageIndex, 1), this.renderOffsets.size()) - 1;
		}
		this.currentRenderOffset = this.renderOffsets.get(pageIndex);
		response.setContentType(ResultViewServer.CONTENT_TYPE_TEXT_HTML);
		try (HtmlRenderer renderer = new HtmlRenderer(response.getWriter(), true)) {
			this.mediaDataSources.clear();
			this.hrefPositions.clear();

			long decoded = RenderOutput.render(this.result, renderer, this.renderHandler, this.currentRenderOffset);
			long nextRenderOffset = this.currentRenderOffset + decoded;

			if ((pageIndex + 1) == this.renderOffsets.size()) {
				if (nextRenderOffset < this.result.size()) {
					this.renderOffsets.add(nextRenderOffset);
				} else {
					this.renderOffsetsComplete = true;
				}
			}
		}
		this.resultView.getDisplay().syncExec(() -> this.resultView.updatePagination(buildPageLinks()));
	}

	@Override
	public String toString() {
		return this.documentUri.toASCIIString();
	}

	synchronized void renderDefault(Writer writer) throws IOException {
		try (HtmlRenderer renderer = new HtmlRenderer(writer, false)) {
			RenderOutput.render(this.result, renderer, this.renderHandler, 0);
		}
	}

	String registerMediaDataPath(int mediaDataIndex, TransferSource source) {
		String mediaDataId = Integer.toString(mediaDataIndex);

		this.mediaDataSources.put(mediaDataId, source);
		return "?" + REQUEST_PARAMETER_MEDIA_DATA_ID + "=" + mediaDataId;
	}

	String getMediaDataPath(int mediaDataIndex) {
		return getMediaDataPath(Integer.toString(mediaDataIndex));
	}

	private String getMediaDataPath(String mediaDataId) {
		return "?" + REQUEST_PARAMETER_MEDIA_DATA_ID + "=" + mediaDataId;
	}

	String registerHrefPath(int hrefIndex, long position) {
		String hrefId = Integer.toString(hrefIndex);

		this.hrefPositions.put(hrefId, position);
		return "?" + REQUEST_PARAMETER_HREF_ID + "=" + hrefId;
	}

	String getHrefPath(int hrefIndex) {
		return getHrefPath(Integer.toString(hrefIndex));
	}

	private String getHrefPath(String hrefId) {
		return "?" + REQUEST_PARAMETER_HREF_ID + "=" + hrefId;
	}

	String getPagePath(int pageIndex) {
		String pageId = Integer.toString(pageIndex + 1);

		return "?" + REQUEST_PARAMETER_PAGE_ID + "=" + pageId;
	}

	private String buildPageLinks() {
		StringBuilder pageLinks = new StringBuilder();
		int renderOffsetsCount = this.renderOffsets.size();

		if (renderOffsetsCount > 1) {
			int pageLink = 1;

			for (Long renderOffset : this.renderOffsets) {
				if (renderOffset.longValue() != this.currentRenderOffset) {
					pageLinks.append(" <a>" + pageLink + "</a>");
				} else {
					pageLinks.append(" " + pageLink + "");
				}
				pageLink++;
			}
			if (!this.renderOffsetsComplete) {
				pageLinks.append(" \u2026");
			}
		}
		return pageLinks.toString();
	}

	private class HtmlRenderer implements Renderer {

		private final Writer writer;
		private final boolean registerPaths;
		private int nextMediaDataIndex = 0;
		private int nextHrefIndex = 0;
		private int currentIndent = 0;
		private @Nullable RenderStyle currentStyle = null;

		HtmlRenderer(Writer writer, boolean registerPaths) {
			this.writer = writer;
			this.registerPaths = registerPaths;
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
				this.writer.write(
						ResultViewI18N.i18nPrologueExtended(documentUri(), ResultViewServer.PATH_STYLESHEET, styles));
			} else {
				this.writer.write(ResultViewI18N.i18nPrologueDefault(documentUri(), ResultViewServer.PATH_STYLESHEET));
			}
		}

		private void addPrologueStyle(StringBuilder styles, Set<RenderOption> options, RenderOption option,
				String optionStyle) {
			if (options.contains(option)) {
				if (styles.length() > 0) {
					styles.append(",");
				}
				styles.append(optionStyle);
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
			String hrefPath = (this.registerPaths ? registerHrefPath(this.nextHrefIndex, href)
					: getHrefPath(this.nextHrefIndex));

			this.nextHrefIndex++;
			applyIndentAndStyle(indent, style);
			this.writer.write(ResultViewI18N.i18nHref(hrefPath, Strings.encodeHtml(text)));
			if (lineBreak) {
				emitBreak();
			}
		}

		@Override
		public void emitMediaData(int indent, RenderStyle style, TransferSource source, boolean lineBreak)
				throws IOException {
			String mediaDataPath = (this.registerPaths ? registerMediaDataPath(this.nextMediaDataIndex, source)
					: getMediaDataPath(this.nextMediaDataIndex));

			this.nextMediaDataIndex++;
			applyIndentAndStyle(indent, style);

			TransferType transferType = source.transferType();

			if (transferType.isImage()) {
				this.writer.write(ResultViewI18N.i18nImg(mediaDataPath, Strings.encodeHtml(transferType.mimeType())));
			} else {
				this.writer.write(ResultViewI18N.i18nMedia(mediaDataPath, Strings.encodeHtml(transferType.mimeType())));
			}
			if (lineBreak) {
				emitBreak();
			}
		}

		@Override
		public void emitEpilogue() throws IOException {
			applyIndentAndStyle(0, null);
			this.writer.write(ResultViewI18N.i18nEpilogue());
		}

		private void applyIndentAndStyle(int indent, @Nullable RenderStyle style) throws IOException {
			if (indent >= 0 && this.currentIndent != indent) {
				if (this.currentStyle != null) {
					this.writer.write(ResultViewI18N.i18nStyleEnd());
				}
				while (this.currentIndent < indent) {
					this.writer.write(ResultViewI18N.i18nIndentIn());
					this.currentIndent++;
				}
				while (this.currentIndent > indent) {
					this.writer.write(ResultViewI18N.i18nIndentOut());
					this.currentIndent--;
				}
				if (style != null) {
					this.writer.write(ResultViewI18N.i18nStyleStart(style.shortName()));
				}
			} else if (this.currentStyle != style) {
				if (this.currentStyle != null) {
					this.writer.write(ResultViewI18N.i18nStyleEnd());
				}
				if (style != null) {
					this.writer.write(ResultViewI18N.i18nStyleStart(style.shortName()));
				}
			}
			this.currentStyle = style;
		}

		private void emitBreak() throws IOException {
			if (this.currentStyle != null) {
				this.writer.write(ResultViewI18N.i18nStyleEnd());
				this.currentStyle = null;
			}
			this.writer.write(ResultViewI18N.i18nBreak());
		}

	}

}
