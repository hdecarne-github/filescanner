/*
 * I18N resource strings
 *
 * Generated on Jun 6, 2016 3:52:55 PM
 */
package de.carne.filescanner.jfx.export;

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
	 * Resource key {@code STR_EXPORT_TITLE}
	 * <p>
	 * Export
	 * </p>
	 */
	public static final String STR_EXPORT_TITLE = "STR_EXPORT_TITLE";

	/**
	 * Resource string {@code STR_EXPORT_TITLE}
	 * <p>
	 * Export
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_EXPORT_TITLE(Object... arguments) {
		return format(STR_EXPORT_TITLE, arguments);
	}

}
