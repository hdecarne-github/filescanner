/*
 * I18N resource strings
 *
 * Generated on May 1, 2016 10:44:08 PM
 */
package de.carne.filescanner.jfx.session;

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

	private static String format(String key, Object... arguments) {
		String pattern = BUNDLE.getString(key);

		return (arguments.length > 0 ? MessageFormat.format(pattern, arguments) : pattern);
	}

	/**
	 * Resource key {@code STR_SESSION_TITLE}
	 * <p>
	 * FileScanner
	 * </p>
	 */
	public static final String STR_SESSION_TITLE = "STR_SESSION_TITLE";

	/**
	 * Resource string {@code STR_SESSION_TITLE}
	 * <p>
	 * FileScanner
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_SESSION_TITLE(Object... arguments) {
		return format(STR_SESSION_TITLE, arguments);
	}

}
