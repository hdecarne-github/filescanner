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
package de.carne.util.prefs;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import de.carne.util.Strings;

/**
 * Utility class for managing a directory preference.
 */
public final class DirectoryPreference extends Preference {

	/**
	 * Construct {@code DirectoryPreference}.
	 *
	 * @param key The preference key.
	 */
	public DirectoryPreference(String key) {
		super(key);
	}

	/**
	 * Get the preference value (as {@linkplain File}).
	 *
	 * @param preferences The {@linkplain Preferences} backing up this
	 *        preference.
	 * @return The preference value or {@code null} if the preference is not
	 *         set.
	 */
	public File getAsFile(Preferences preferences) {
		return getAsFile(preferences, null);
	}

	/**
	 * Get the preference value (as {@linkplain File}).
	 *
	 * @param preferences The {@linkplain Preferences} backing up this
	 *        preference.
	 * @param defaultDirectory The default directory to return if the preference
	 *        is not set.
	 * @return The preference value or the submitted default if the preference
	 *         is not set.
	 */
	public File getAsFile(Preferences preferences, File defaultDirectory) {
		Path path = getAsPath(preferences);

		return (path != null ? path.toFile() : defaultDirectory);
	}

	/**
	 * Get the preference value (as {@linkplain Path}).
	 *
	 * @param preferences The {@linkplain Preferences} backing up this
	 *        preference.
	 * @return The preference value or {@code null} if the preference is not
	 *         set.
	 */
	public Path getAsPath(Preferences preferences) {
		return getAsPath(preferences, null);
	}

	/**
	 * Get the preference value (as {@linkplain Path}).
	 *
	 * @param preferences The {@linkplain Preferences} backing up this
	 *        preference.
	 * @param defaultDirectory The default directory to return if the preference
	 *        is not set.
	 * @return The preference value or the submitted default if the preference
	 *         is not set.
	 */
	public Path getAsPath(Preferences preferences, Path defaultDirectory) {
		assert preferences != null;

		String pathString = preferences.get(key(), null);
		Path path = null;

		if (Strings.notEmpty(pathString)) {
			try {
				path = Paths.get(pathString).toAbsolutePath();
			} catch (InvalidPathException e) {
				// ignore
			}
		}
		return path;
	}

	/**
	 * Set the preference value (from a {@linkplain File} object).
	 *
	 * @param preferences The {@linkplain Preferences} backing up this
	 *        preference.
	 * @param directory The directory to set. If this parameter is {@code null}
	 *        the preference is removed.
	 */
	public void set(Preferences preferences, File directory) {
		set(preferences, (directory != null ? directory.toPath() : null));
	}

	/**
	 * Set the preference value (from a {@linkplain Path} object).
	 *
	 * @param preferences The {@linkplain Preferences} backing up this
	 *        preference.
	 * @param directory The directory to set. If this parameter is {@code null}
	 *        the preference is removed.
	 */
	public void set(Preferences preferences, Path directory) {
		assert preferences != null;

		if (directory != null) {
			preferences.put(key(), directory.toString());
		} else {
			preferences.remove(key());
		}
	}

	/**
	 * Set the preference value (from a {@linkplain File} object).
	 *
	 * @param preferences The {@linkplain Preferences} backing up this
	 *        preference.
	 * @param file The file to get the directory from. If this parameter is
	 *        {@code null} the preference is removed.
	 */
	public void setFromFile(Preferences preferences, File file) {
		setFromFile(preferences, (file != null ? file.toPath() : null));
	}

	/**
	 * Set the preference value (from a {@linkplain Path} object).
	 *
	 * @param preferences The {@linkplain Preferences} backing up this
	 *        preference.
	 * @param file The file to get the directory from. If this parameter is
	 *        {@code null} the preference is removed.
	 */
	public void setFromFile(Preferences preferences, Path file) {
		assert preferences != null;

		if (file != null) {
			preferences.put(key(), file.getParent().toString());
		} else {
			preferences.remove(key());
		}
	}

}
