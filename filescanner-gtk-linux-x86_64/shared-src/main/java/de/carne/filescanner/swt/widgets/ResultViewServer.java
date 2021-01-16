/*
 * Copyright (c) 2007-2021 Holger de Carne and contributors, All Rights Reserved.
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
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.util.ContentType;

import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.ModuleManifestInfos;
import de.carne.filescanner.engine.transfer.FileScannerResultRenderHandler;
import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.filescanner.platform.FileScannerPlatform;
import de.carne.filescanner.swt.resources.Images;
import de.carne.io.IOUtil;
import de.carne.util.Strings;
import de.carne.util.SystemProperties;
import de.carne.util.logging.Log;

class ResultViewServer {

	private static final Log LOG = new Log();

	private static final PortRange PORT_RANGE = PortRange
			.valueOf(SystemProperties.value(ResultViewServer.class, ".portRange", "50101:50199"));

	public static final ContentType CONTENT_TYPE_TEXT_HTML = ContentType.newContentType("text/html", "utf-8");
	public static final ContentType CONTENT_TYPE_TEXT_CSS = ContentType.newContentType("text/css", "utf-8");
	public static final ContentType CONTENT_TYPE_IMAGE_PNG = ContentType.newContentType("image/png");

	@SuppressWarnings("squid:S1075")
	public static final String PATH_TRANSPARENT_BACKGROUND = "/transparent_background.png";
	@SuppressWarnings("squid:S1075")
	public static final String PATH_STYLESHEET = "/result.css";

	@Nullable
	private static ResultViewServer instance = null;

	private final HttpServer server;
	private String cachedStylesheet;
	private final Set<ResultView> resultViews = new HashSet<>();

	private ResultViewServer(HttpServer server, String defaultDocument) {
		this.server = server;
		this.cachedStylesheet = buildStyleSheet(null, null, Collections.emptyMap());

		ServerConfiguration configuration = this.server.getServerConfiguration();

		configuration.addHttpHandler(textHandler(CONTENT_TYPE_TEXT_HTML, () -> defaultDocument));
		configuration.addHttpHandler(
				resourceHandler(CONTENT_TYPE_IMAGE_PNG, Images.get(Images.IMAGE_TRANSPARENT_BACKGROUND)),
				PATH_TRANSPARENT_BACKGROUND);
		configuration.addHttpHandler(textHandler(CONTENT_TYPE_TEXT_CSS, () -> this.cachedStylesheet), PATH_STYLESHEET);
	}

	public static synchronized ResultViewServer getInstance(ResultView resultView) throws IOException {
		ResultViewServer checkedInstance = instance;

		if (checkedInstance == null) {
			LOG.info("Starting server at localhost:{0}", PORT_RANGE);

			HttpServer server = HttpServer.createSimpleServer(null, "localhost", PORT_RANGE);

			server.start();

			instance = checkedInstance = new ResultViewServer(server, buildDefaultDocument(resultView));

			LOG.info("Server started at {0}", instance);
		}
		resultView.addDisposeListener(ResultViewServer::resultViewDisposed);
		checkedInstance.resultViews.add(resultView);
		return checkedInstance;
	}

	private static synchronized void resultViewDisposed(DisposeEvent event) {
		ResultViewServer checkedInstance = instance;

		if (checkedInstance != null) {
			checkedInstance.resultViews.remove(event.widget);

			ServerConfiguration configuration = checkedInstance.server.getServerConfiguration();

			for (HttpHandler handler : configuration.getHttpHandlersWithMapping().keySet()) {
				if (handler instanceof ResultViewContentHandler) {
					ResultViewContentHandler document = (ResultViewContentHandler) handler;

					if (document.resultView().equals(event.widget)) {
						checkedInstance.removeResult(document);
					}
				}
			}

			if (checkedInstance.resultViews.isEmpty()) {
				LOG.info("Stopping server at {0}...", instance);

				checkedInstance.server.shutdownNow();
				instance = null;
			}
		}
	}

	public void setStyle(FontData font, RGB background, Map<RenderStyle, RGB> styleColors) {
		this.cachedStylesheet = buildStyleSheet(font, background, styleColors);
		for (ResultView resultView : this.resultViews) {
			resultView.refresh();
		}
	}

	public ResultViewContentHandler addResult(ResultView resultView, FileScannerResult result,
			@Nullable FileScannerResultRenderHandler renderHandler) {
		URI documentUri = getServerUri(this.server).resolve("/").resolve(UUID.randomUUID().toString());

		LOG.info("Adding document handler {0}...", documentUri);

		ResultViewContentHandler document = new ResultViewContentHandler(documentUri, resultView, result,
				renderHandler);
		ServerConfiguration configuration = this.server.getServerConfiguration();

		configuration.addHttpHandler(document, documentUri.getPath());

		return document;
	}

	public void removeResult(ResultViewContentHandler document) {
		ServerConfiguration configuration = this.server.getServerConfiguration();

		configuration.removeHttpHandler(document);
	}

	public URI getDefaultUri() {
		return getServerUri(this.server);
	}

	@Override
	public String toString() {
		return getDefaultUri().toASCIIString();
	}

	private static HttpHandler resourceHandler(ContentType contentType, URL resource) {
		return new HttpHandler() {

			private byte @Nullable [] resourceBytes;

			@Override
			public void service(Request request, Response response) throws IOException {
				if (this.resourceBytes == null) {
					this.resourceBytes = IOUtil.readAllBytes(resource);
				}
				response.setContentType(contentType);
				response.getOutputStream().write(this.resourceBytes);
			}
		};
	}

	private static HttpHandler textHandler(ContentType contentType, Supplier<String> textSupplier) {
		return new HttpHandler() {

			@Override
			public void service(Request request, Response response) throws IOException {
				response.setContentType(contentType);
				response.getWriter().write(textSupplier.get());
			}
		};
	}

	private static String buildDefaultDocument(ResultView resultView) {
		String background = rgbString(new StringBuilder(), resultView.getBackground().getRGB()).toString();
		String foreground = rgbString(new StringBuilder(), resultView.getForeground().getRGB()).toString();
		ModuleManifestInfos infos = new ModuleManifestInfos();

		return ResultViewI18N.i18nTextDefaultResultViewHtml(background, foreground, Strings.encodeHtml(infos.name()),
				Strings.encodeHtml(infos.version()), Strings.encodeHtml(infos.build()));
	}

	private static String buildStyleSheet(@Nullable FontData font, @Nullable RGB background,
			Map<RenderStyle, RGB> styleColors) {
		StringBuilder stylesheet = new StringBuilder();

		stylesheet.append("body { white-space: pre; ");
		if (font != null) {
			stylesheet.append(" font-family:\"").append(font.getName()).append("\";");
			stylesheet.append(" font-style:");
			if ((font.getStyle() & SWT.ITALIC) != 0) {
				stylesheet.append("italic;");
			} else {
				stylesheet.append("normal;");
			}
			stylesheet.append(" font-weight:");
			if ((font.getStyle() & SWT.BOLD) != 0) {
				stylesheet.append("bold;");
			} else {
				stylesheet.append("normal;");
			}
			stylesheet.append(" font-size:").append(FileScannerPlatform.cssFontSize(font.getHeight())).append("pt;");
		}
		if (background != null) {
			stylesheet.append(" background-color: ");
			rgbString(stylesheet, background).append(";");
		}
		stylesheet.append(" }");
		stylesheet.append(" .indent { padding-left: 2em; }");
		stylesheet.append(" .transparent { background-image: url(\"").append(PATH_TRANSPARENT_BACKGROUND)
				.append("\"); }");
		stylesheet.append(" .wrap { white-space: normal; }");
		for (RenderStyle style : RenderStyle.values()) {
			RGB styleColor = styleColors.get(style);

			if (styleColor != null) {
				stylesheet.append(" .").append(style.shortName()).append(" { ");
				stylesheet.append("white-space: inherit; ");
				stylesheet.append("color: ");
				rgbString(stylesheet, styleColor).append(";");
				stylesheet.append("}");
			}
		}
		return stylesheet.toString();
	}

	private static StringBuilder rgbString(StringBuilder buffer, RGB rgb) {
		buffer.append("rgb(").append(rgb.red).append(",").append(rgb.green).append(",").append(rgb.blue).append(")");
		return buffer;
	}

	private static URI getServerUri(HttpServer server) {
		NetworkListener listener = Objects.requireNonNull(server.getListener("grizzly"));
		String uriString = (listener.isSecure() ? "https://" : "http://") + listener.getHost() + ":"
				+ listener.getPort();

		return URI.create(uriString);
	}

}
