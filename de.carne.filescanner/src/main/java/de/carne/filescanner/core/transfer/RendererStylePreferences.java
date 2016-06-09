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
package de.carne.filescanner.core.transfer;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Renderer style preferences access.
 */
public final class RendererStylePreferences {

	private static final String PREF_DEFAULT = "default";

	private static final Preferences PREFERENCES = Preferences.userNodeForPackage(RendererStylePreferences.class);

	/**
	 * Save the preferences.
	 *
	 * @throws BackingStoreException if a save error occurs.
	 */
	public static void sync() throws BackingStoreException {
		PREFERENCES.sync();
	}

	/**
	 * Get the default style.
	 *
	 * @return The default style.
	 */
	public static RendererStyle getDefaultStyle() {
		return getStyle(PREF_DEFAULT);
	}

	private static RendererStyle getStyle(String key) {
		String preference = PREFERENCES.get(key, null);

		return (preference != null ? RendererStyle.fromPreferenceString(preference) : new RendererStyle());
	}

	/**
	 * Set the default style.
	 * 
	 * @param style The default style.
	 */
	public void setDefaultStyle(RendererStyle style) {
		assert style != null;

		setStyle(PREF_DEFAULT, style);
	}

	private void setStyle(String key, RendererStyle style) {
		String preference = style.toPreferenceString();

		PREFERENCES.put(key, preference);
	}

}
