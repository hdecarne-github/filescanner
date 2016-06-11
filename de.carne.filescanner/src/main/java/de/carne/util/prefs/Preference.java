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

/**
 * Base class for all preference utility classes.
 */
public abstract class Preference {

	private final String key;

	/**
	 * Construct {@code Preference}.
	 *
	 * @param key The preference key.
	 */
	protected Preference(String key) {
		this.key = key;
	}

	/**
	 * Get the preference key.
	 * 
	 * @return The preference key.
	 */
	public final String key() {
		return this.key;
	}

	@Override
	public String toString() {
		return key();
	}

}
