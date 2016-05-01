/*
 * I18N resource strings
 *
 * Generated on May 1, 2016 10:18:08 PM
 */
package de.carne.filescanner.jfx;

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
	 * Resource key {@code STR_UNEXPECTED_EXCEPTION_MESSAGE}
	 * <p>
	 * An unexpected error has occurred: ''{0}''
	 * </p>
	 */
	public static final String STR_UNEXPECTED_EXCEPTION_MESSAGE = "STR_UNEXPECTED_EXCEPTION_MESSAGE";

	/**
	 * Resource string {@code STR_UNEXPECTED_EXCEPTION_MESSAGE}
	 * <p>
	 * An unexpected error has occurred: ''{0}''
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_UNEXPECTED_EXCEPTION_MESSAGE(Object... arguments) {
		return format(STR_UNEXPECTED_EXCEPTION_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_VM_INFO}
	 * <p>
	 * Java VM Version: {0} Vendor: ''{1}''
	 * </p>
	 */
	public static final String STR_VM_INFO = "STR_VM_INFO";

	/**
	 * Resource string {@code STR_VM_INFO}
	 * <p>
	 * Java VM Version: {0} Vendor: ''{1}''
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_VM_INFO(Object... arguments) {
		return format(STR_VM_INFO, arguments);
	}

	/**
	 * Resource key {@code STR_VERBOSE_ENABLED_MESSAGE}
	 * <p>
	 * Verbose logging enabled
	 * </p>
	 */
	public static final String STR_VERBOSE_ENABLED_MESSAGE = "STR_VERBOSE_ENABLED_MESSAGE";

	/**
	 * Resource string {@code STR_VERBOSE_ENABLED_MESSAGE}
	 * <p>
	 * Verbose logging enabled
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_VERBOSE_ENABLED_MESSAGE(Object... arguments) {
		return format(STR_VERBOSE_ENABLED_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_DEBUG_ENABLED_MESSAGE}
	 * <p>
	 * Debug logging enabled
	 * </p>
	 */
	public static final String STR_DEBUG_ENABLED_MESSAGE = "STR_DEBUG_ENABLED_MESSAGE";

	/**
	 * Resource string {@code STR_DEBUG_ENABLED_MESSAGE}
	 * <p>
	 * Debug logging enabled
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_DEBUG_ENABLED_MESSAGE(Object... arguments) {
		return format(STR_DEBUG_ENABLED_MESSAGE, arguments);
	}

	/**
	 * Resource key {@code STR_INVALID_PARAMETER_MESSAGE}
	 * <p>
	 * Invalid parameter: ''{0}''
	 * </p>
	 */
	public static final String STR_INVALID_PARAMETER_MESSAGE = "STR_INVALID_PARAMETER_MESSAGE";

	/**
	 * Resource string {@code STR_INVALID_PARAMETER_MESSAGE}
	 * <p>
	 * Invalid parameter: ''{0}''
	 * </p>
	 *
	 * @param arguments Format arguments.
	 * @return The formated string.
	 */
	public static String formatSTR_INVALID_PARAMETER_MESSAGE(Object... arguments) {
		return format(STR_INVALID_PARAMETER_MESSAGE, arguments);
	}

}
