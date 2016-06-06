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
package de.carne.filescanner.jfx;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultType;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Utility class for result icon creation (including state dependent overlays).
 */
public final class ResultGraphics {

	/**
	 * Get the result graphic.
	 *
	 * @param result The result object to get the graphic for.
	 * @return The result graphic.
	 */
	public static Node get(FileScannerResult result) {
		assert result != null;

		Image image;

		switch (result.type()) {
		case INPUT:
			image = Images.IMAGE_INPUT_RESULT16;
			break;
		case FORMAT:
			image = (result.parent().type() == FileScannerResultType.INPUT ? Images.IMAGE_FORMAT1_RESULT16
					: Images.IMAGE_FORMAT2_RESULT16);
			break;
		case ENCODED_INPUT:
			image = Images.IMAGE_ENCODED_INPUT_RESULT16;
			break;
		default:
			throw new IllegalStateException("Unexpected result type: " + result.type());
		}

		Node graphic;

		if (result.decodeStatus() == null) {
			graphic = new ImageView(image);
		} else {
			graphic = new Group(new ImageView(image), new ImageView(Images.IMAGE_ERROR_OVERLAY16));
		}
		return graphic;
	}

}
