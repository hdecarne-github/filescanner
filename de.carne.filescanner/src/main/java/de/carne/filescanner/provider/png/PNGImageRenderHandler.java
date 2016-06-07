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
package de.carne.filescanner.provider.png;

import java.io.IOException;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.format.spec.FormatSpec;
import de.carne.filescanner.core.format.spec.FormatSpec.RenderHandler;
import de.carne.filescanner.core.transfer.FileScannerResultRenderer;
import de.carne.filescanner.core.transfer.FileScannerResultRenderer.Feature;
import de.carne.filescanner.core.transfer.MappingStreamHandler;

/**
 * Custom render handler for PNG image display.
 */
public class PNGImageRenderHandler implements RenderHandler {

	@Override
	public void render(FormatSpec spec, FileScannerResult result, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException {
		if (result.decodeStatus() == null) {
			MappingStreamHandler mapping = new MappingStreamHandler();

			mapping.mapResult(result);
			renderer.enableFeature(Feature.TRANSPARENCY);
			renderer.renderImage(mapping);
			renderer.close();
		} else {
			spec.specRender(result, result.start(), result.end(), renderer);
		}
	}

}
