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

import java.util.Collection;
import java.util.Collections;

import de.carne.util.Platform;
import javafx.scene.image.Image;

/**
 * Utility class for support of Java FX platform specific functionality.
 */
public final class FXPlatform {

	private static final Image[] EMPTY_ICONS = new Image[0];

	/**
	 * Filter stage icons according to platform preference.
	 *
	 * @param icons The available icons.
	 * @return The filtered icons.
	 */
	public static Image[] stageIcons(Image... icons) {
		if (Platform.IS_OS_X) {
			return EMPTY_ICONS;
		}
		return icons;
	}

	/**
	 * Filter stage icons according to platform preference.
	 *
	 * @param icons The available icons.
	 * @return The filtered icons.
	 */
	public static Collection<Image> stageIcons(Collection<Image> icons) {
		if (Platform.IS_OS_X) {
			return Collections.EMPTY_LIST;
		}
		return icons;
	}

}
