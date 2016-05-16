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
package de.carne.jfx.messagebox;

import java.util.List;

import de.carne.jfx.ImageRegistry;
import javafx.scene.image.Image;

/**
 * Image registry for message box display.
 * <p>
 * To support a consistent application style the message box does not have it's
 * own image resources. Instead the application is required to register the
 * necessary images prior to the first message box display using this registry
 * class.
 * </p>
 */
public class MessageBoxImages {

	private static final ImageRegistry<MessageBoxStyle> STYLE_IMAGE_REGISTRY = new ImageRegistry<>();

	/**
	 * Register a message box style image.
	 *
	 * @param style The style to register the image for.
	 * @param image The image to register.
	 */
	public static void registerImage(MessageBoxStyle style, Image image) {
		STYLE_IMAGE_REGISTRY.registerImage(style, image);
	}

	/**
	 * Get all images registered for a specific style.
	 * <p>
	 * The resulting list ordered largest to smallest according to their image's
	 * space.
	 * </p>
	 *
	 * @param style The style to get the images for.
	 * @return The list of registered images (may be empty).
	 */
	public static List<Image> getImages(MessageBoxStyle style) {
		return STYLE_IMAGE_REGISTRY.getImages(style);
	}

	/**
	 * Get the largest image registered for a specific style.
	 *
	 * @param style The style to get the image for.
	 * @return The found image or {@code null} if no image has been registered
	 *         yet.
	 */
	public static Image getImage(MessageBoxStyle style) {
		return STYLE_IMAGE_REGISTRY.getImage(style);
	}

	/**
	 * Get the registered image for a specific style and space requirement.
	 *
	 * @param style The style to get the image for.
	 * @param space The space requirement to match.
	 * @return The found image or {@code null} if no image has been registered
	 *         yet.
	 */
	public static Image getImage(MessageBoxStyle style, double space) {
		return STYLE_IMAGE_REGISTRY.getImage(style, space);
	}

}
