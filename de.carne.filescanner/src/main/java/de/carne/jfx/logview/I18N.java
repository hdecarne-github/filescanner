/*
 * I18N resource strings
 *
 * Generated on 11.07.2016 12:00:51
 */
package de.carne.jfx.logview;

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
	 * Resource key {@code STR_LOG_VIEW_TITLE}
	 * <p>
	 * Log view
	 * </p>
	 */
	public static final String STR_LOG_VIEW_TITLE = "STR_LOG_VIEW_TITLE";

	/**
	 * Resource string {@code STR_LOG_VIEW_TITLE}
	 * <p>
	 * Log view
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_LOG_VIEW_TITLE(Object... arguments) {
		return format(STR_LOG_VIEW_TITLE, arguments);
	}

}
