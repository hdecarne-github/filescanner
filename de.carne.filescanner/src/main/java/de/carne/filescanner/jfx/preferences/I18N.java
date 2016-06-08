/*
 * I18N resource strings
 *
 * Generated on 07.06.2016 20:40:09
 */
package de.carne.filescanner.jfx.preferences;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Package localization resources.
 */
public final class I18N {

	/**
	 * The BUNDLE represented by this class.
	 */
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(I18N.class.getName());

	/**
	 * Format a resource string.
	 * @param key The resource key.
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String format(String key, Object... arguments) {
		String pattern = BUNDLE.getString(key);

		return (arguments.length > 0 ? MessageFormat.format(pattern, arguments) : pattern);
	}

	/**
	 * Resource key {@code STR_PREFERENCES_TITLE}
	 * <p>
	 * FileScanner Preferences
	 * </p>
	 */
	public static final String STR_PREFERENCES_TITLE = "STR_PREFERENCES_TITLE";

	/**
	 * Resource string {@code STR_PREFERENCES_TITLE}
	 * <p>
	 * FileScanner Preferences
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_PREFERENCES_TITLE(Object... arguments) {
		return format(STR_PREFERENCES_TITLE, arguments);
	}

}
