/*
 * I18N resource strings
 *
 * Generated on Jun 12, 2016 11:01:51 PM
 */
package de.carne.filescanner.core.transfer;

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
	 * Resource key {@code STR_RAW_EXPORT_NAME}
	 * <p>
	 * Binary data
	 * </p>
	 */
	public static final String STR_RAW_EXPORT_NAME = "STR_RAW_EXPORT_NAME";

	/**
	 * Resource string {@code STR_RAW_EXPORT_NAME}
	 * <p>
	 * Binary data
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_RAW_EXPORT_NAME(Object... arguments) {
		return format(STR_RAW_EXPORT_NAME, arguments);
	}

}
