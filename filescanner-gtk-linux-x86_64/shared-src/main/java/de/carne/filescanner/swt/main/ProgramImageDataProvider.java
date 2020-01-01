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

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.program.Program;

final class ProgramImageDataProvider implements ImageDataProvider {

	private final Program program;

	private ProgramImageDataProvider(Program program) {
		this.program = program;
	}

	public static Image createImage(Device device, Program program) {
		return new Image(device, new ProgramImageDataProvider(program));
	}

	@Override
	@Nullable
	public ImageData getImageData(int zoom) {
		ImageData imageData;

		switch (zoom) {
		case 100:
			imageData = scaleImageData(this.program.getImageData(), 16, true);
			break;
		case 200:
			imageData = scaleImageData(this.program.getImageData(), 32, false);
			break;
		default:
			imageData = null;
		}
		return imageData;
	}

	@Nullable
	private ImageData scaleImageData(@Nullable ImageData imageData, int size, boolean force) {
		ImageData scaledImageData = null;

		if (imageData != null) {
			if (imageData.width == size && imageData.height == size) {
				scaledImageData = imageData;
			} else if (force) {
				scaledImageData = imageData.scaledTo(size, size);
			}
		}
		return scaledImageData;
	}

}
