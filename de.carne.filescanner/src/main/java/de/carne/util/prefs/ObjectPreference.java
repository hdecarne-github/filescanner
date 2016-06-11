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

import java.util.function.Function;
import java.util.prefs.Preferences;

/**
 * Utility class for managing a object preference.
 * 
 * @param <T> The preference type.
 */
public final class ObjectPreference<T> extends Preference {

	private final Function<String, T> fromStringLambda;

	private final Function<T, String> toStringLambda;

	/**
	 * Construct {@code ObjectPreference}.
	 *
	 * @param key The preference key.
	 * @param fromStringLambda The lambda function to use for string to object
	 *        conversion.
	 */
	public ObjectPreference(String key, Function<String, T> fromStringLambda) {
		this(key, fromStringLambda, o -> o.toString());
	}

	/**
	 * Construct {@code ObjectPreference}.
	 *
	 * @param key The preference key.
	 * @param fromStringLambda The lambda function to use for string to object
	 *        conversion.
	 * @param toStringLambda The lambda function to use for object to string
	 *        conversion.
	 */
	public ObjectPreference(String key, Function<String, T> fromStringLambda, Function<T, String> toStringLambda) {
		super(key);

		assert fromStringLambda != null;
		assert toStringLambda != null;

		this.fromStringLambda = fromStringLambda;
		this.toStringLambda = toStringLambda;
	}

	/**
	 * Get the preference value:
	 *
	 * @param preferences The {@linkplain Preferences} backing up this
	 *        preference.
	 * @return The preference value or {@code null} if the preference is not
	 *         set.
	 */
	public T get(Preferences preferences) {
		return get(preferences, null);
	}

	/**
	 * Get the preference value:
	 *
	 * @param preferences The {@linkplain Preferences} backing up this
	 *        preference.
	 * @param defaultValue The default value to return if the preference is not
	 *        set.
	 * @return The preference value or the submitted default if the preference
	 *         is not set.
	 */
	public T get(Preferences preferences, T defaultValue) {
		assert preferences != null;

		String valueString = preferences.get(key(), null);
		T value = defaultValue;

		if (valueString != null) {
			value = this.fromStringLambda.apply(valueString);
		}
		return value;
	}

	/**
	 * Set the preference value.
	 *
	 * @param preferences The {@linkplain Preferences} backing up this
	 *        preference.
	 * @param value The value to set. If this parameter is {@code null} the
	 *        preference is removed.
	 */
	public void set(Preferences preferences, T value) {
		assert preferences != null;

		if (value != null) {
			preferences.put(key(), this.toStringLambda.apply(value));
		} else {
			preferences.remove(key());
		}
	}

}
