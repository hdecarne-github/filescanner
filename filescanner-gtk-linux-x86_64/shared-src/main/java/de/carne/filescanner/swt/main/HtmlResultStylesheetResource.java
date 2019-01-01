/*
 * Copyright (c) 2007-2019 Holger de Carne and contributors, All Rights Reserved.
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

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.filescanner.swt.preferences.Config;

class HtmlResultStylesheetResource extends HttpHandler {

	private String stylesheet;

	public HtmlResultStylesheetResource(Config config, String transparentBackgroundPath) {
		StringBuilder header = new StringBuilder();

		header.append("body { ");
		cssFont(header, config.getResultViewFont());
		header.append(" }");
		header.append(" .transparent { background-image: url(\"").append(transparentBackgroundPath).append("\"); }");
		for (RenderStyle style : RenderStyle.values()) {
			header.append(" .").append(style.name().toLowerCase()).append(" {");
			cssColor(header, config.getResultViewColor(style));
			header.append("}");
		}
		this.stylesheet = header.toString();
	}

	private static void cssFont(StringBuilder css, FontData font) {
		css.append("font-family:\"").append(font.getName()).append("\";");
		css.append("font-style:");
		if ((font.getStyle() & SWT.ITALIC) != 0) {
			css.append("italic;");
		} else {
			css.append("normal;");
		}
		css.append("font-weight:");
		if ((font.getStyle() & SWT.BOLD) != 0) {
			css.append("bold;");
		} else {
			css.append("normal;");
		}
		css.append("font-size:").append(font.getHeight()).append("pt;");
	}

	private static void cssColor(StringBuilder css, RGB rgb) {
		css.append("color:rgb(").append(rgb.red).append(",").append(rgb.green).append(",").append(rgb.blue)
				.append(");");
	}

	@Override
	public void service(@Nullable Request request, @Nullable Response response) throws Exception {
		if (request != null && response != null) {
			response.setContentType(HtmlResourceType.TEXT_CSS.contentType());
			response.getWriter().write(this.stylesheet);
		}
	}

}
