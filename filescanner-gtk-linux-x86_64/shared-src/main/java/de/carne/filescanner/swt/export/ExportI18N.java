/*
 * I18N resource strings (automatically generated - do not edit)
 */
package de.carne.filescanner.swt.export;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Resource bundle: de/carne/filescanner/swt/export/ExportI18N.properties
 */
public final class ExportI18N {

	/**
	 * The name of the {@linkplain ResourceBundle} wrapped by this class.
	 */
	public static final String BUNDLE_NAME = ExportI18N.class.getName();

	/**
	 * The {@linkplain ResourceBundle} wrapped by this class.
	 */
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private ExportI18N() {
		// Prevent instantiation
	}

	/**
	 * Format a resource string.
	 * @param key The resource key.
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String format(String key, Object... arguments) {
		String pattern = BUNDLE.getString(key);

		return MessageFormat.format(pattern, arguments);
	}

	/**
	 * Resource key {@code I18N_BUTTON_CANCEL}
	 * <p>
	 * Cancel
	 */
	public static final String I18N_BUTTON_CANCEL = "I18N_BUTTON_CANCEL";

	/**
	 * Resource string {@code I18N_BUTTON_CANCEL}
	 * <p>
	 * Cancel
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nButtonCancel(Object... arguments) {
		return format(I18N_BUTTON_CANCEL, arguments);
	}

	/**
	 * Resource key {@code I18N_BUTTON_EXPORT}
	 * <p>
	 * Export
	 */
	public static final String I18N_BUTTON_EXPORT = "I18N_BUTTON_EXPORT";

	/**
	 * Resource string {@code I18N_BUTTON_EXPORT}
	 * <p>
	 * Export
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nButtonExport(Object... arguments) {
		return format(I18N_BUTTON_EXPORT, arguments);
	}

	/**
	 * Resource key {@code I18N_LABEL_EXPORT_PATH}
	 * <p>
	 * Path
	 */
	public static final String I18N_LABEL_EXPORT_PATH = "I18N_LABEL_EXPORT_PATH";

	/**
	 * Resource string {@code I18N_LABEL_EXPORT_PATH}
	 * <p>
	 * Path
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nLabelExportPath(Object... arguments) {
		return format(I18N_LABEL_EXPORT_PATH, arguments);
	}

	/**
	 * Resource key {@code I18N_LABEL_EXPORT_TYPE}
	 * <p>
	 * Export
	 */
	public static final String I18N_LABEL_EXPORT_TYPE = "I18N_LABEL_EXPORT_TYPE";

	/**
	 * Resource string {@code I18N_LABEL_EXPORT_TYPE}
	 * <p>
	 * Export
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nLabelExportType(Object... arguments) {
		return format(I18N_LABEL_EXPORT_TYPE, arguments);
	}

	/**
	 * Resource key {@code I18N_TITLE}
	 * <p>
	 * Export result
	 */
	public static final String I18N_TITLE = "I18N_TITLE";

	/**
	 * Resource string {@code I18N_TITLE}
	 * <p>
	 * Export result
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nTitle(Object... arguments) {
		return format(I18N_TITLE, arguments);
	}

}
