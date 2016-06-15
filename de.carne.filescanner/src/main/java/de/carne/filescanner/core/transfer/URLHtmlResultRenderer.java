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

/**
 * {@linkplain HtmlResultRenderer} implementation used by
 * {@linkplain HtmlResultRendererURLHandler} class.
 */
class URLHtmlResultRenderer extends HtmlResultRenderer {

	private final HtmlResultRendererURLHandler urlHandler;

	private final StringBuilder buffer = new StringBuilder();

	protected URLHtmlResultRenderer(HtmlResultRendererURLHandler urlHandler) {
		this.urlHandler = urlHandler;
	}

	@Override
	protected URL registerStreamHandler(StreamHandler streamHandler) throws IOException, InterruptedException {
		return this.urlHandler.registerStreamHandler(streamHandler);
	}

	@Override
	protected void write(String... artifacts) throws IOException, InterruptedException {
		for (String artifact : artifacts) {
			this.buffer.append(artifact);
		}
	}

	@Override
	public String toString() {
		return this.buffer.toString();
	}

}
