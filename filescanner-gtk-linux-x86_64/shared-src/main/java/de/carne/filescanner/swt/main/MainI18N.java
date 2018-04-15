/*
 * I18N resource strings (automatically generated - do not edit)
 */
package de.carne.filescanner.swt.main;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Resource bundle: de/carne/filescanner/swt/main/MainI18N.properties
 */
public final class MainI18N {

	/**
	 * The name of the {@linkplain ResourceBundle} wrapped by this class.
	 */
	public static final String BUNDLE_NAME = MainI18N.class.getName();

	/**
	 * The {@linkplain ResourceBundle} wrapped by this class.
	 */
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private MainI18N() {
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
	 * Resource key {@code I18N_MENU_FILE}
	 * <p>
	 * &File
	 */
	public static final String I18N_MENU_FILE = "I18N_MENU_FILE";

	/**
	 * Resource string {@code I18N_MENU_FILE}
	 * <p>
	 * &File
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuFile(Object... arguments) {
		return format(I18N_MENU_FILE, arguments);
	}

	/**
	 * Resource key {@code I18N_MENU_FILE_OPEN}
	 * <p>
	 * &Open…
	 */
	public static final String I18N_MENU_FILE_OPEN = "I18N_MENU_FILE_OPEN";

	/**
	 * Resource string {@code I18N_MENU_FILE_OPEN}
	 * <p>
	 * &Open…
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuFileOpen(Object... arguments) {
		return format(I18N_MENU_FILE_OPEN, arguments);
	}

	/**
	 * Resource key {@code I18N_MENU_FILE_QUIT}
	 * <p>
	 * &Quit
	 */
	public static final String I18N_MENU_FILE_QUIT = "I18N_MENU_FILE_QUIT";

	/**
	 * Resource string {@code I18N_MENU_FILE_QUIT}
	 * <p>
	 * &Quit
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuFileQuit(Object... arguments) {
		return format(I18N_MENU_FILE_QUIT, arguments);
	}

	/**
	 * Resource key {@code I18N_MENU_HELP}
	 * <p>
	 * &Help
	 */
	public static final String I18N_MENU_HELP = "I18N_MENU_HELP";

	/**
	 * Resource string {@code I18N_MENU_HELP}
	 * <p>
	 * &Help
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuHelp(Object... arguments) {
		return format(I18N_MENU_HELP, arguments);
	}

	/**
	 * Resource key {@code I18N_MENU_HELP_ABOUT}
	 * <p>
	 * &About…
	 */
	public static final String I18N_MENU_HELP_ABOUT = "I18N_MENU_HELP_ABOUT";

	/**
	 * Resource string {@code I18N_MENU_HELP_ABOUT}
	 * <p>
	 * &About…
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuHelpAbout(Object... arguments) {
		return format(I18N_MENU_HELP_ABOUT, arguments);
	}

	/**
	 * Resource key {@code I18N_TITLE}
	 * <p>
	 * FileScanner
	 */
	public static final String I18N_TITLE = "I18N_TITLE";

	/**
	 * Resource string {@code I18N_TITLE}
	 * <p>
	 * FileScanner
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nTitle(Object... arguments) {
		return format(I18N_TITLE, arguments);
	}

}
