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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.spi.FileScannerResultRenderer.StreamHandler;
import de.carne.util.logging.Log;

/**
 * This class renders scanner results in HTML format.
 * <p>
 * The render result is accessed via an URL. The latter makes this solution
 * usable with any browser like UI control.
 * </p>
 */
public class HtmlResultRendererURLHandler implements StreamHandler {

	private static final Log LOG = new Log(HtmlResultRendererURLHandler.class);

	/**
	 * The custom URL protocol used to access the rendered output.
	 */
	public static final String PROTOCOL_RENDERER = "renderer";

	/**
	 * The {@code URLStreamHandlerFactory} for the custom URL protocol.
	 * <p>
	 * As the
	 * {@linkplain URL#setURLStreamHandlerFactory(URLStreamHandlerFactory)}
	 * function can only be called once per VM, we do not automatically register
	 * the custom protocol. Either perform a
	 * </p>
	 * <p>
	 * <code>
	 * URL.setURLStreamHandlerFactory(HtmlResultRenderer.URL_STREAM_HANDLER_FACTORY);
	 * </code>
	 * </p>
	 * <p>
	 * call prior to using this renderer or integrate the factory according to
	 * your application design.
	 * </p>
	 */
	public static final URLStreamHandlerFactory URL_STREAM_HANDLER_FACTORY = new URLStreamHandlerFactory() {

		@Override
		public URLStreamHandler createURLStreamHandler(String protocol) {
			return createRendererURLStreamHandler(protocol);
		}

	};

	private static final String STREAM_PREFIX = "stream";

	private static final HashMap<URL, StreamHandler> URL_MAP = new HashMap<>();

	private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

	private FileScannerResult result;

	private URL baseURL;

	private int streamIndex = 0;

	private HtmlResultRendererURLHandler(FileScannerResult result, URL baseURL) {
		this.result = result;
		this.baseURL = baseURL;
	}

	/**
	 * Open a new renderer URL.
	 *
	 * @param result The {@code FileScannerResult} to render.
	 * @return The created {@URL} for accessing the renderer output.
	 * @throws IOException if an I/O error occurs.
	 */
	public static URL open(FileScannerResult result) throws IOException {
		assert result != null;

		URL baseURL = new URL(PROTOCOL_RENDERER, UUID.randomUUID().toString(), "");

		LOG.debug(null, "Creating renderer URL: ''{0}''", baseURL);

		HtmlResultRendererURLHandler renderer = new HtmlResultRendererURLHandler(result, baseURL);

		URL_MAP.put(baseURL, renderer);
		return baseURL;
	}

	URL openStream(StreamHandler handler) throws IOException {
		URL streamURL = new URL(PROTOCOL_RENDERER, this.baseURL.getHost(), STREAM_PREFIX + this.streamIndex);

		LOG.debug(null, "Creating stream URL: ''0}''", streamURL);

		URL_MAP.put(streamURL, handler);
		this.streamIndex++;
		return streamURL;
	}

	/**
	 * Close a renderer URL.
	 * <p>
	 * Closing the URL invalidates the URL.
	 * </p>
	 *
	 * @param u The {@URL} to close.
	 */
	public static void close(URL u) {
		assert u != null;

		String host = u.getHost();

		synchronized (URL_MAP) {
			for (URL url : URL_MAP.keySet()) {
				if (host.equals(url.getHost())) {

					LOG.debug(null, "Releasing renderer URL ''{0}''", url);

					URL_MAP.remove(url);
				}
			}
		}
	}

	static URLStreamHandler createRendererURLStreamHandler(String protocol) {
		URLStreamHandler handler = null;

		if (PROTOCOL_RENDERER.equals(protocol)) {
			handler = new URLStreamHandler() {

				@Override
				protected URLConnection openConnection(URL u) throws IOException {
					return openRendererConnection(u);
				}

			};
		}
		return handler;
	}

	static URLConnection openRendererConnection(URL u) {
		return new URLConnection(u) {

			private InputStream inputStream = null;

			@Override
			public InputStream getInputStream() throws IOException {
				if (this.inputStream == null) {
					this.inputStream = getStreamHandler(getURL()).open();
				}
				return this.inputStream;
			}

			@Override
			public void connect() throws IOException {
				// Nothing to do here
			}

		};
	}

	static StreamHandler getStreamHandler(URL u) throws IOException {
		LOG.debug(null, "Accessing renderer URL ''{0}''", u);

		StreamHandler streamHandler = URL_MAP.get(u);

		if (streamHandler == null) {
			throw new FileNotFoundException(u.toExternalForm());
		}
		return streamHandler;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.spi.FileScannerResultRenderer.StreamHandler#open()
	 */
	@SuppressWarnings("resource")
	@Override
	public InputStream open() throws IOException {
		return new Renderer(THREAD_POOL, new PipedOutputStream());
	}

	private class Renderer extends PipedInputStream implements Callable<Renderer> {

		private final PipedOutputStream pipe;
		private final Future<Renderer> future;

		Renderer(ExecutorService executorService, PipedOutputStream pipe) throws IOException {
			super(pipe);
			this.pipe = pipe;
			this.future = executorService.submit(this);
		}

		@Override
		public Renderer call() throws Exception {
			try {
				renderResult(this.pipe);
			} finally {
				this.pipe.flush();
				this.pipe.close();
			}
			return this;
		}

		@Override
		public void close() throws IOException {
			this.future.cancel(true);
			try {
				this.pipe.close();
			} finally {
				super.close();
			}
		}

	}

	void renderResult(PipedOutputStream pipe) throws IOException, InterruptedException {
		this.result.render(new HtmlResultRenderer(this, pipe));
	}

}
