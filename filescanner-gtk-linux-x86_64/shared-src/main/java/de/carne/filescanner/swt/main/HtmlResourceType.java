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

import org.glassfish.grizzly.http.util.ContentType;

enum HtmlResourceType {

	TEXT_HTML("text/html", "utf-8"),

	TEXT_CSS("text/css", "utf-8"),

	IMAGE_PNG("image/png");

	private final ContentType contentType;

	private HtmlResourceType(String mimeType) {
		this.contentType = ContentType.newContentType(mimeType, null);
	}

	private HtmlResourceType(String mimeType, String characterEncoding) {
		this.contentType = ContentType.newContentType(mimeType, characterEncoding);
	}

	public ContentType contentType() {
		return this.contentType;
	}

}
