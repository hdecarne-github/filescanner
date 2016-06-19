/*
 * I18N resource strings
 *
 * Generated on 19.06.2016 18:41:47
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
	 * Resource key {@code STR_INVALID_FILE_INPUT}
	 * <p>
	 * Cannot start export.<br/>''{0}'' is not a valid file name.
	 * </p>
	 */
	public static final String STR_INVALID_FILE_INPUT = "STR_INVALID_FILE_INPUT";

	/**
	 * Resource string {@code STR_INVALID_FILE_INPUT}
	 * <p>
	 * Cannot start export.<br/>''{0}'' is not a valid file name.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_FILE_INPUT(Object... arguments) {
		return format(STR_INVALID_FILE_INPUT, arguments);
	}

	/**
	 * Resource key {@code STR_EXPORT_ERROR}
	 * <p>
	 * An error occurred while writing to export file<br/>''{0}''.
	 * </p>
	 */
	public static final String STR_EXPORT_ERROR = "STR_EXPORT_ERROR";

	/**
	 * Resource string {@code STR_EXPORT_ERROR}
	 * <p>
	 * An error occurred while writing to export file<br/>''{0}''.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_EXPORT_ERROR(Object... arguments) {
		return format(STR_EXPORT_ERROR, arguments);
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

	/**
	 * Resource key {@code STR_FILE_NOT_WRITABLE}
	 * <p>
	 * Cannot start export.<br/>''{0}'' is not a file or cannot be overwritten.
	 * </p>
	 */
	public static final String STR_FILE_NOT_WRITABLE = "STR_FILE_NOT_WRITABLE";

	/**
	 * Resource string {@code STR_FILE_NOT_WRITABLE}
	 * <p>
	 * Cannot start export.<br/>''{0}'' is not a file or cannot be overwritten.
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_FILE_NOT_WRITABLE(Object... arguments) {
		return format(STR_FILE_NOT_WRITABLE, arguments);
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
	 * Resource key {@code STR_CONFIRM_OVERWRITE}
	 * <p>
	 * File ''{0}'' already exists.<br/>Do you want to overwrite the existing file?
	 * </p>
	 */
	public static final String STR_CONFIRM_OVERWRITE = "STR_CONFIRM_OVERWRITE";

	/**
	 * Resource string {@code STR_CONFIRM_OVERWRITE}
	 * <p>
	 * File ''{0}'' already exists.<br/>Do you want to overwrite the existing file?
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_CONFIRM_OVERWRITE(Object... arguments) {
		return format(STR_CONFIRM_OVERWRITE, arguments);
	}

}
