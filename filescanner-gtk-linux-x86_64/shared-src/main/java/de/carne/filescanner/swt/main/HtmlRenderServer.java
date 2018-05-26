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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;

import de.carne.boot.check.Check;
import de.carne.boot.check.Nullable;
import de.carne.boot.logging.Log;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.swt.preferences.Config;
import de.carne.swt.graphics.ResourceException;
import de.carne.util.SystemProperties;

class HtmlRenderServer {

	private static final Log LOG = new Log();

	private static final PortRange HTTP_PORT_RANGE = PortRange
			.valueOf(SystemProperties.value(HtmlRenderServer.class, ".portRange", "50101:50199"));

	private static final String TRANSPARENT_BACKGROUND_RESOURCE = "transparent.png";
	private static final String TRANSPARENT_BACKGROUND_URL = "/" + TRANSPARENT_BACKGROUND_RESOURCE;

	private final HttpServer httpServer;
	@Nullable
	private HttpHandler stylesheetHandler;
	@Nullable
	private String stylesheetUrl;
	private final List<HttpHandler> persistentSessionHandlers = new ArrayList<>();
	private final List<HttpHandler> transientSessionHandlers = new ArrayList<>();

	public HtmlRenderServer(Config config) throws ResourceException {
		try {
			this.httpServer = startHttpServer();
		} catch (IOException e) {
			throw new ResourceException("Failed to start HTTP server", e);
		}
		applyConfig(config);
	}

	public synchronized void applyConfig(Config config) {
		ServerConfiguration configuration = this.httpServer.getServerConfiguration();
		HttpHandler checkedStylesheetHandler = this.stylesheetHandler;

		if (checkedStylesheetHandler != null) {
			configuration.removeHttpHandler(checkedStylesheetHandler);
		}
		this.stylesheetUrl = "/result.css";
		this.stylesheetHandler = new HtmlResultStylesheetResource(config, TRANSPARENT_BACKGROUND_URL);
		configuration.addHttpHandler(this.stylesheetHandler, this.stylesheetUrl);
	}

	public synchronized void clearSession() {
		ServerConfiguration configuration = this.httpServer.getServerConfiguration();

		for (HttpHandler sessionHandler : this.transientSessionHandlers) {
			configuration.removeHttpHandler(sessionHandler);
		}
		for (HttpHandler sessionHandler : this.persistentSessionHandlers) {
			configuration.removeHttpHandler(sessionHandler);
		}
		this.persistentSessionHandlers.clear();
	}

	public synchronized HtmlResultDocument createResultDocument(FileScannerResult result, boolean persistent) {
		ServerConfiguration configuration = this.httpServer.getServerConfiguration();

		for (HttpHandler sessionHandler : this.transientSessionHandlers) {
			configuration.removeHttpHandler(sessionHandler);
		}

		URI resultDocumentUri = getHttpServerUri(this.httpServer).resolve("/" + UUID.randomUUID().toString());
		HtmlResultDocument resultDocumentHandler = new HtmlResultDocument(resultDocumentUri.toASCIIString(), result,
				Check.notNull(this.stylesheetUrl));

		configuration.addHttpHandler(resultDocumentHandler, resultDocumentUri.getPath());
		if (persistent) {
			this.persistentSessionHandlers.add(resultDocumentHandler);
		} else {
			this.transientSessionHandlers.add(resultDocumentHandler);
		}

		LOG.info("Created result document URI ''{0}''", resultDocumentUri);

		return resultDocumentHandler;
	}

	public void stop() {
		LOG.info("Stopping local HTTP server at {0}...", getHttpServerUri(this.httpServer));

		this.httpServer.shutdownNow();
	}

	private static HttpServer startHttpServer() throws IOException {
		LOG.info("Starting local HTTP server at localhost:{0}", HTTP_PORT_RANGE);

		HttpServer httpServer = HttpServer.createSimpleServer(null, "localhost", HTTP_PORT_RANGE);

		NetworkListener listener = getHttpServerNetworkListener(httpServer);

		listener.getTransport().setIOStrategy(SameThreadIOStrategy.getInstance());

		ServerConfiguration configuration = httpServer.getServerConfiguration();

		configuration.addHttpHandler(
				new HtmlStaticResource(HtmlResourceType.IMAGE_PNG, TRANSPARENT_BACKGROUND_RESOURCE),
				TRANSPARENT_BACKGROUND_URL);
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

}
