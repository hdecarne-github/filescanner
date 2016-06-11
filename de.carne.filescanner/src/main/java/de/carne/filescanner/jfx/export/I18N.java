/*
 * I18N resource strings
 *
 * Generated on 11.06.2016 16:48:03
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
	 * Resource key {@code STR_ALL_FILES_FILTER}
	 * <p>
	 * All files
	 * </p>
	 */
	public static final String STR_ALL_FILES_FILTER = "STR_ALL_FILES_FILTER";

	/**
	 * Resource string {@code STR_ALL_FILES_FILTER}
	 * <p>
	 * All files
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_ALL_FILES_FILTER(Object... arguments) {
		return format(STR_ALL_FILES_FILTER, arguments);
	}

	/**
	 * Resource key {@code STR_RESULT_DESCRIPTION}
	 * <p>
	 * {0} ({1})
	 * </p>
	 */
	public static final String STR_RESULT_DESCRIPTION = "STR_RESULT_DESCRIPTION";

	/**
	 * Resource string {@code STR_RESULT_DESCRIPTION}
	 * <p>
	 * {0} ({1})
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_RESULT_DESCRIPTION(Object... arguments) {
		return format(STR_RESULT_DESCRIPTION, arguments);
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
