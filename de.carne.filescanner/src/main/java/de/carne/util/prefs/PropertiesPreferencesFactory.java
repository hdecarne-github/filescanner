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
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * Property file based preferences implementation.
 */
public class PropertiesPreferencesFactory implements PreferencesFactory {

	private static final String PREFERENCES_DIR;

	static {
		String packageName = PropertiesPreferencesFactory.class.getPackage().getName();

		PREFERENCES_DIR = System.getProperty(packageName, "." + packageName);
	}

	@Override
	public Preferences systemRoot() {
		String userHome = System.getProperty("user.home", ".");
		String systemName;

		try {
			systemName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			systemName = "UNKNOWN";
		}

		Path propertiesPath = Paths.get(userHome, PREFERENCES_DIR, "system-" + systemName + ".properties");

		return new PropertiesPreferences(propertiesPath);
	}

	@Override
	public Preferences userRoot() {
		String userHome = System.getProperty("user.home", ".");
		Path propertiesPath = Paths.get(userHome, PREFERENCES_DIR, "user.properties");

		return new PropertiesPreferences(propertiesPath);
	}

	/**
	 * Create a {@linkplain Preferences} object backed up by a given properties
	 * file.
	 *
	 * @param propertiesPath The properties file use for backing up the
	 *        preferences.
	 * @return The created {@linkplain Preferences} object.
	 */
	public static Preferences fromFile(Path propertiesPath) {
		return new PropertiesPreferences(propertiesPath);
	}

	/**
	 * Get a directory within the preferences folder for storing user specific
	 * data.
	 * <p>
	 * If the directory does not yet exist, it is created.
	 * </p>
	 *
	 * @param name The directory name to use.
	 * @return The created directory.
	 */
	public static File directory(String name) {
		assert name != null;

		String userHome = System.getProperty("user.home", ".");
		Path directoryPath = Paths.get(userHome, PREFERENCES_DIR, name);

		try {
			Files.createDirectories(directoryPath);
		} catch (IOException e) {
			throw new RuntimeException("Unable to create preference directory '" + directoryPath + "'");
		}
		return directoryPath.toFile();
	}

}
