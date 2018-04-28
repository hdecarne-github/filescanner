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
	 * Resource key {@code I18N_MENU_EDIT}
	 * <p>
	 * &Edit
	 */
	public static final String I18N_MENU_EDIT = "I18N_MENU_EDIT";

	/**
	 * Resource string {@code I18N_MENU_EDIT}
	 * <p>
	 * &Edit
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuEdit(Object... arguments) {
		return format(I18N_MENU_EDIT, arguments);
	}

	/**
	 * Resource key {@code I18N_MENU_EDIT_COPY}
	 * <p>
	 * &Copy
	 */
	public static final String I18N_MENU_EDIT_COPY = "I18N_MENU_EDIT_COPY";

	/**
	 * Resource string {@code I18N_MENU_EDIT_COPY}
	 * <p>
	 * &Copy
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuEditCopy(Object... arguments) {
		return format(I18N_MENU_EDIT_COPY, arguments);
	}

	/**
	 * Resource key {@code I18N_MENU_EDIT_EXPORT}
	 * <p>
	 * &Export…
	 */
	public static final String I18N_MENU_EDIT_EXPORT = "I18N_MENU_EDIT_EXPORT";

	/**
	 * Resource string {@code I18N_MENU_EDIT_EXPORT}
	 * <p>
	 * &Export…
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuEditExport(Object... arguments) {
		return format(I18N_MENU_EDIT_EXPORT, arguments);
	}

	/**
	 * Resource key {@code I18N_MENU_EDIT_GOTO}
	 * <p>
	 * &Goto
	 */
	public static final String I18N_MENU_EDIT_GOTO = "I18N_MENU_EDIT_GOTO";

	/**
	 * Resource string {@code I18N_MENU_EDIT_GOTO}
	 * <p>
	 * &Goto
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuEditGoto(Object... arguments) {
		return format(I18N_MENU_EDIT_GOTO, arguments);
	}

	/**
	 * Resource key {@code I18N_MENU_EDIT_GOTO_RESULT_END}
	 * <p>
	 * Result &end
	 */
	public static final String I18N_MENU_EDIT_GOTO_RESULT_END = "I18N_MENU_EDIT_GOTO_RESULT_END";

	/**
	 * Resource string {@code I18N_MENU_EDIT_GOTO_RESULT_END}
	 * <p>
	 * Result &end
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuEditGotoResultEnd(Object... arguments) {
		return format(I18N_MENU_EDIT_GOTO_RESULT_END, arguments);
	}

	/**
	 * Resource key {@code I18N_MENU_EDIT_GOTO_RESULT_START}
	 * <p>
	 * Result &start
	 */
	public static final String I18N_MENU_EDIT_GOTO_RESULT_START = "I18N_MENU_EDIT_GOTO_RESULT_START";

	/**
	 * Resource string {@code I18N_MENU_EDIT_GOTO_RESULT_START}
	 * <p>
	 * Result &start
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuEditGotoResultStart(Object... arguments) {
		return format(I18N_MENU_EDIT_GOTO_RESULT_START, arguments);
	}

	/**
	 * Resource key {@code I18N_MENU_EDIT_GOTO_SEARCH_NEXT}
	 * <p>
	 * Search &next
	 */
	public static final String I18N_MENU_EDIT_GOTO_SEARCH_NEXT = "I18N_MENU_EDIT_GOTO_SEARCH_NEXT";

	/**
	 * Resource string {@code I18N_MENU_EDIT_GOTO_SEARCH_NEXT}
	 * <p>
	 * Search &next
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuEditGotoSearchNext(Object... arguments) {
		return format(I18N_MENU_EDIT_GOTO_SEARCH_NEXT, arguments);
	}

	/**
	 * Resource key {@code I18N_MENU_EDIT_GOTO_SEARCH_PREV}
	 * <p>
	 * Search &previous
	 */
	public static final String I18N_MENU_EDIT_GOTO_SEARCH_PREV = "I18N_MENU_EDIT_GOTO_SEARCH_PREV";

	/**
	 * Resource string {@code I18N_MENU_EDIT_GOTO_SEARCH_PREV}
	 * <p>
	 * Search &previous
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuEditGotoSearchPrev(Object... arguments) {
		return format(I18N_MENU_EDIT_GOTO_SEARCH_PREV, arguments);
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
	 * Resource key {@code I18N_MENU_FILE_PREFERENCES}
	 * <p>
	 * &Preferences…
	 */
	public static final String I18N_MENU_FILE_PREFERENCES = "I18N_MENU_FILE_PREFERENCES";

	/**
	 * Resource string {@code I18N_MENU_FILE_PREFERENCES}
	 * <p>
	 * &Preferences…
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuFilePreferences(Object... arguments) {
		return format(I18N_MENU_FILE_PREFERENCES, arguments);
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
	 * Resource key {@code I18N_MENU_HELP_LOGS}
	 * <p>
	 * &Logs…
	 */
	public static final String I18N_MENU_HELP_LOGS = "I18N_MENU_HELP_LOGS";

	/**
	 * Resource string {@code I18N_MENU_HELP_LOGS}
	 * <p>
	 * &Logs…
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMenuHelpLogs(Object... arguments) {
		return format(I18N_MENU_HELP_LOGS, arguments);
	}

	/**
	 * Resource key {@code I18N_TEXT_DEFAULT_RESULT_VIEW_HTML}
	 * <p>
	 * &lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset="utf-8"&gt;&lt;&frasl;head&gt;&lt;body&gt;&lt;&frasl;body&gt;&lt;&frasl;html&gt;
	 */
	public static final String I18N_TEXT_DEFAULT_RESULT_VIEW_HTML = "I18N_TEXT_DEFAULT_RESULT_VIEW_HTML";

	/**
	 * Resource string {@code I18N_TEXT_DEFAULT_RESULT_VIEW_HTML}
	 * <p>
	 * &lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset="utf-8"&gt;&lt;&frasl;head&gt;&lt;body&gt;&lt;&frasl;body&gt;&lt;&frasl;html&gt;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nTextDefaultResultViewHtml(Object... arguments) {
		return format(I18N_TEXT_DEFAULT_RESULT_VIEW_HTML, arguments);
	}

	/**
	 * Resource key {@code I18N_TEXT_FILE_OPEN_FILTER}
	 * <p>
	 * &#42;|All files
	 */
	public static final String I18N_TEXT_FILE_OPEN_FILTER = "I18N_TEXT_FILE_OPEN_FILTER";

	/**
	 * Resource string {@code I18N_TEXT_FILE_OPEN_FILTER}
	 * <p>
	 * &#42;|All files
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nTextFileOpenFilter(Object... arguments) {
		return format(I18N_TEXT_FILE_OPEN_FILTER, arguments);
	}

	/**
	 * Resource key {@code I18N_TEXT_SESSION_STATUS}
	 * <p>
	 * Scanned: {0} ({1}&frasl;s) Elapsed: {2}:{3,number,00}:{4,number,00}.{5,number,000}
	 */
	public static final String I18N_TEXT_SESSION_STATUS = "I18N_TEXT_SESSION_STATUS";

	/**
	 * Resource string {@code I18N_TEXT_SESSION_STATUS}
	 * <p>
	 * Scanned: {0} ({1}&frasl;s) Elapsed: {2}:{3,number,00}:{4,number,00}.{5,number,000}
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nTextSessionStatus(Object... arguments) {
		return format(I18N_TEXT_SESSION_STATUS, arguments);
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
