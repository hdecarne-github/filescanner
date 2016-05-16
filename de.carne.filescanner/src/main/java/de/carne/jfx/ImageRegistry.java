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
package de.carne.jfx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javafx.scene.image.Image;

/**
 * Utility class providing an image registry.
 *
 * @param <T> The registry key type.
 */
public final class ImageRegistry<T> {

	private final HashMap<T, List<Image>> registry = new HashMap<>();

	/**
	 * Register an image.
	 *
	 * @param key The key to register the image for.
	 * @param image The image to register.
	 */
	public void registerImage(T key, Image image) {
		assert key != null;
		assert image != null;

		List<Image> images = this.registry.get(key);

		if (images == null) {
			images = new ArrayList<>();
		}

		double imageSpace = getImageSpace(image);
		int imageIndex = 0;
		int imagesSize = images.size();

		while (imageIndex < imagesSize && imageSpace < getImageSpace(images.get(imageIndex))) {
			imageIndex++;
		}
		images.add(imageIndex, image);
		this.registry.put(key, images);
	}

	/**
	 * Get all images registered for a specific key.
	 * <p>
	 * The resulting list ordered largest to smallest according to their image's
	 * space.
	 * </p>
	 *
	 * @param key The key to get the images for.
	 * @return The list of registered images (may be empty).
	 */
	public List<Image> getImages(T key) {
		assert key != null;

		List<Image> images = this.registry.get(key);

		return (images != null ? Collections.unmodifiableList(images) : Collections.emptyList());
	}

	/**
	 * Get the largest registered image for a specific key.
	 *
	 * @param key The key to get the image for.
	 * @return The found image or {@code null} if no image has been registered
	 *         yet.
	 */
	public Image getImage(T key) {
		assert key != null;

		List<Image> images = this.registry.get(key);

		return (images != null ? images.get(0) : null);
	}

	/**
	 * Get the registered image for a specific key and space requirement.
	 *
	 * @param key The key to get the image for.
	 * @param space The space requirement to match.
	 * @return The found image or {@code null} if no image has been registered
	 *         yet.
	 */
	public Image getImage(T key, double space) {
		assert key != null;

		List<Image> images = this.registry.get(key);
		Image matchingImage = null;

		if (images != null) {
			for (Image image : images) {
				double imageSpace = getImageSpace(image);

				matchingImage = image;
				if (imageSpace >= space) {
					break;
				}
			}
		}
		return matchingImage;
	}

	private static double getImageSpace(Image image) {
		return image.getWidth() * image.getHeight();
	}

}
