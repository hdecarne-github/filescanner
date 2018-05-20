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
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;

import de.carne.boot.check.Check;
import de.carne.boot.check.Nullable;
import de.carne.boot.logging.Log;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.transfer.RenderOutput;
import de.carne.filescanner.swt.preferences.Config;
import de.carne.io.IOUtil;
import de.carne.swt.graphics.ResourceException;
import de.carne.util.SystemProperties;

class HtmlRenderService extends HttpHandler {

	private static final Log LOG = new Log();

	private static final PortRange HTTP_PORT_RANGE = PortRange
			.valueOf(SystemProperties.value(HtmlRenderService.class, ".portRange", "50101:50199"));

	private static final String RESOURCE_TRANSPARENT = "transparent.png";

	private final HttpServer httpServer;
	@Nullable
	private String cachedHeader = null;
	@Nullable
	private FileScannerResult result = null;
	private final List<HttpHandler> resultHandlers = new ArrayList<>();

	public HtmlRenderService() throws ResourceException {
		try {
			this.httpServer = startHttpServer();
		} catch (IOException e) {
			throw new ResourceException("Failed to start HTTP server", e);
		}
	}

	public synchronized void applyConfig(Config config) {
		this.cachedHeader = HtmlRenderer.prepareHeader(config);
	}

	public synchronized void clear() {
		this.result = null;

		ServerConfiguration configuration = this.httpServer.getServerConfiguration();

		for (HttpHandler handler : this.resultHandlers) {
			configuration.removeHttpHandler(handler);
		}
	}

	public synchronized String setResult(FileScannerResult result) {
		clear();

		this.result = result;

		URI resultHandlerUri = getHttpServerUri(this.httpServer).resolve("/" + UUID.randomUUID().toString());

		addResultHandler(this, resultHandlerUri);

		LOG.info("Created result renderer ''{0}''", resultHandlerUri);

		return resultHandlerUri.toASCIIString();
	}

	private void addResultHandler(HttpHandler handler, URI uri) {
		this.httpServer.getServerConfiguration().addHttpHandler(handler, uri.getPath());
		this.resultHandlers.add(handler);
	}

	@Override
	public void service(@Nullable Request request, @Nullable Response response) throws Exception {
		if (request != null && response != null) {
			FileScannerResult checkedResult = this.result;

			if (checkedResult != null) {
				HtmlRenderer htmlRenderer = new HtmlRenderer(response.getWriter(), Check.notNull(this.cachedHeader));

				RenderOutput.render(checkedResult, htmlRenderer);
			}
			response.finish();
		}
	}

	public void dispose() {
		LOG.info("Stopping local HTTP server at {0}...", getHttpServerUri(this.httpServer));

		this.httpServer.shutdownNow();
	}

	private static HttpServer startHttpServer() throws IOException {
		LOG.info("Starting local HTTP server at localhost:{0}", HTTP_PORT_RANGE);

		HttpServer httpServer = HttpServer.createSimpleServer(null, "localhost", HTTP_PORT_RANGE);

		NetworkListener listener = getHttpServerNetworkListener(httpServer);

		listener.getTransport().setIOStrategy(SameThreadIOStrategy.getInstance());

		ServerConfiguration configuration = httpServer.getServerConfiguration();

		configuration.addHttpHandler(new StaticResourceHandler(RESOURCE_TRANSPARENT), "/" + RESOURCE_TRANSPARENT);
		httpServer.start();

		LOG.info("Local HTTP server started at {0}", getHttpServerUri(httpServer));

		return httpServer;
	}

	private static NetworkListener getHttpServerNetworkListener(HttpServer httpServer) {
		return Check.notNull(httpServer.getListener("grizzly"));
	}

	private static URI getHttpServerUri(HttpServer httpServer) {
		NetworkListener listener = getHttpServerNetworkListener(httpServer);
		String uriString = (listener.isSecure() ? "https://" : "http://") + listener.getHost() + ":"
				+ listener.getPort();

		return URI.create(uriString);
	}

	private static class StaticResourceHandler extends HttpHandler {

		private final URL resourceUrl;

		StaticResourceHandler(String resource) {
			this(HtmlRenderService.class.getResource(resource));
		}

		StaticResourceHandler(URL resourceUrl) {
			this.resourceUrl = resourceUrl;
		}

		@Override
		public void service(@Nullable Request request, @Nullable Response response) throws Exception {
			if (request != null && response != null) {
				try (InputStream resourceStream = this.resourceUrl.openStream()) {
					IOUtil.copyStream(response.getOutputStream(), resourceStream);
				}
			}
		}

	}

}
