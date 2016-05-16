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
package de.carne.jfx.logview;

import java.util.List;
import java.util.logging.Level;

import de.carne.jfx.ImageRegistry;
import javafx.scene.image.Image;

/**
 * Image registry for log data display.
 * <p>
 * To support a consistent application style the log viewer does not have it's
 * own image resources. Instead the application is required to register the
 * necessary images prior to the first log viewer display using this registry
 * class.
 * </p>
 */
public final class LogImages {

	private static final ImageRegistry<Level> LEVEL_IMAGE_REGISTRY = new ImageRegistry<>();

	/**
	 * Register a level image.
	 *
	 * @param level The level to register the image for.
	 * @param image The image to register.
	 */
	public static void registerImage(Level level, Image image) {
		LEVEL_IMAGE_REGISTRY.registerImage(level, image);
	}

	/**
	 * Get all images registered for a specific level.
	 * <p>
	 * The resulting list ordered largest to smallest according to their image's
	 * space.
	 * </p>
	 *
	 * @param level The level to get the images for.
	 * @return The list of registered images (may be empty).
	 */
	public static List<Image> getImages(Level level) {
		return LEVEL_IMAGE_REGISTRY.getImages(level);
	}

	/**
	 * Get the largest image registered for a specific level.
	 *
	 * @param level The level to get the image for.
	 * @return The found image or {@code null} if no image has been registered
	 *         yet.
	 */
	public static Image getImage(Level level) {
		return LEVEL_IMAGE_REGISTRY.getImage(level);
	}

	/**
	 * Get the registered image for a specific level and space requirement.
	 *
	 * @param level The level to get the image for.
	 * @param space The space requirement to match.
	 * @return The found image or {@code null} if no image has been registered
	 *         yet.
	 */
	public static Image getImage(Level level, double space) {
		return LEVEL_IMAGE_REGISTRY.getImage(level, space);
	}

}
