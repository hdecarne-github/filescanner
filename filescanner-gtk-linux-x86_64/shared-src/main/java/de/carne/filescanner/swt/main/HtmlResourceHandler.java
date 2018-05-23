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

import java.io.InputStream;
import java.net.URL;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import de.carne.boot.check.Nullable;
import de.carne.io.IOUtil;

class HtmlResourceHandler extends HttpHandler {

	private final URL resourceUrl;

	HtmlResourceHandler(String resource) {
		this(HtmlResourceHandler.class.getResource(resource));
	}

	HtmlResourceHandler(URL resourceUrl) {
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
