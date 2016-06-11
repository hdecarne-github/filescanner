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
package de.carne.filescanner.core;

import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import de.carne.filescanner.spi.Format;

/**
 * File scanner preferences access.
 */
public final class FileScannerPreferences {

	private static final String PREF_FORMAT_ENABLED = ".enabled";

	private static final Preferences PREFERENCES = Preferences.userNodeForPackage(FileScannerPreferences.class);

	/**
	 * Save the preferences.
	 *
	 * @throws BackingStoreException if a save error occurs.
	 */
	public static void sync() throws BackingStoreException {
		PREFERENCES.sync();
	}

	/**
	 * Get the set of enabled formats.
	 *
	 * @return The set of enabled formats.
	 */
	public synchronized static Set<Format> getEnabledFormats() {
		HashSet<Format> enabledFormats = new HashSet<>();

		for (Format format : Format.getFormats()) {
			boolean formatEnabled = PREFERENCES.getBoolean(preferenceKey(format, PREF_FORMAT_ENABLED), true);

			if (formatEnabled) {
				enabledFormats.add(format);
			}
		}
		return enabledFormats;
	}

	/**
	 * Set the enabled formats.
	 *
	 * @param enabledFormats The set of enabled formats or {@code null} to
	 *        enable all formats.
	 */
	public synchronized static void setEnabledFormats(Set<Format> enabledFormats) {
		for (Format format : Format.getFormats()) {
			boolean formatEnabled = (enabledFormats != null ? enabledFormats.contains(format) : true);

			PREFERENCES.putBoolean(preferenceKey(format, PREF_FORMAT_ENABLED), formatEnabled);
		}
	}

	private static String preferenceKey(Format format, String key) {
		return format.getClass().getName() + key;
	}

}
