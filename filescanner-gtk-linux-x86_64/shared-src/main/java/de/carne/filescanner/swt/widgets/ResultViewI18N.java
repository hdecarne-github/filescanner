/*
 * I18N resource strings (automatically generated - do not edit)
 */
package de.carne.filescanner.swt.widgets;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Resource bundle: de/carne/filescanner/swt/widgets/ResultViewI18N.properties
 */
public final class ResultViewI18N {

	/**
	 * The name of the {@linkplain ResourceBundle} wrapped by this class.
	 */
	public static final String BUNDLE_NAME = ResultViewI18N.class.getName();

	/**
	 * The {@linkplain ResourceBundle} wrapped by this class.
	 */
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private ResultViewI18N() {
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
	 * Resource key {@code I18N_BREAK}
	 * <p>
	 * &apos;&lt;br&gt;&apos;
	 */
	public static final String I18N_BREAK = "I18N_BREAK";

	/**
	 * Resource string {@code I18N_BREAK}
	 * <p>
	 * &apos;&lt;br&gt;&apos;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nBreak(Object... arguments) {
		return format(I18N_BREAK, arguments);
	}

	/**
	 * Resource key {@code I18N_EPILOGUE}
	 * <p>
	 * &apos;&lt;&frasl;body&gt;&lt;&frasl;html&gt;&apos;
	 */
	public static final String I18N_EPILOGUE = "I18N_EPILOGUE";

	/**
	 * Resource string {@code I18N_EPILOGUE}
	 * <p>
	 * &apos;&lt;&frasl;body&gt;&lt;&frasl;html&gt;&apos;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nEpilogue(Object... arguments) {
		return format(I18N_EPILOGUE, arguments);
	}

	/**
	 * Resource key {@code I18N_HREF}
	 * <p>
	 * &apos;&lt;a href=&quot;&apos;{0}&apos;&quot;&gt;&apos;{1}&apos;&lt;&frasl;a&gt;&apos;
	 */
	public static final String I18N_HREF = "I18N_HREF";

	/**
	 * Resource string {@code I18N_HREF}
	 * <p>
	 * &apos;&lt;a href=&quot;&apos;{0}&apos;&quot;&gt;&apos;{1}&apos;&lt;&frasl;a&gt;&apos;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nHref(Object... arguments) {
		return format(I18N_HREF, arguments);
	}

	/**
	 * Resource key {@code I18N_IMG}
	 * <p>
	 * &apos;&lt;img src=&quot;&apos;{0}&apos;&quot; alt=&quot;&apos;{1}&apos;&quot;&gt;&apos;
	 */
	public static final String I18N_IMG = "I18N_IMG";

	/**
	 * Resource string {@code I18N_IMG}
	 * <p>
	 * &apos;&lt;img src=&quot;&apos;{0}&apos;&quot; alt=&quot;&apos;{1}&apos;&quot;&gt;&apos;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nImg(Object... arguments) {
		return format(I18N_IMG, arguments);
	}

	/**
	 * Resource key {@code I18N_INDENT_IN}
	 * <p>
	 * &apos;&lt;div class=&quot;indent&quot;&gt;&apos;
	 */
	public static final String I18N_INDENT_IN = "I18N_INDENT_IN";

	/**
	 * Resource string {@code I18N_INDENT_IN}
	 * <p>
	 * &apos;&lt;div class=&quot;indent&quot;&gt;&apos;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nIndentIn(Object... arguments) {
		return format(I18N_INDENT_IN, arguments);
	}

	/**
	 * Resource key {@code I18N_INDENT_OUT}
	 * <p>
	 * &apos;&lt;&frasl;div&gt;&apos;
	 */
	public static final String I18N_INDENT_OUT = "I18N_INDENT_OUT";

	/**
	 * Resource string {@code I18N_INDENT_OUT}
	 * <p>
	 * &apos;&lt;&frasl;div&gt;&apos;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nIndentOut(Object... arguments) {
		return format(I18N_INDENT_OUT, arguments);
	}

	/**
	 * Resource key {@code I18N_MEDIA}
	 * <p>
	 * &apos;[&lt;a href=&quot;&apos;{0}&apos;&quot;&gt;&apos;{1}&apos;&lt;&frasl;a&gt;]&apos;
	 */
	public static final String I18N_MEDIA = "I18N_MEDIA";

	/**
	 * Resource string {@code I18N_MEDIA}
	 * <p>
	 * &apos;[&lt;a href=&quot;&apos;{0}&apos;&quot;&gt;&apos;{1}&apos;&lt;&frasl;a&gt;]&apos;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nMedia(Object... arguments) {
		return format(I18N_MEDIA, arguments);
	}

	/**
	 * Resource key {@code I18N_PROLOGUE_DEFAULT}
	 * <p>
	 * &apos;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;base href=&quot;&apos;{0}&apos;&quot;&gt;&lt;link rel=&quot;stylesheet&quot; type=&quot;text&frasl;css&quot; href=&quot;&apos;{1}&apos;&quot;&gt;&lt;&frasl;head&gt;&lt;body&gt;&apos;
	 */
	public static final String I18N_PROLOGUE_DEFAULT = "I18N_PROLOGUE_DEFAULT";

	/**
	 * Resource string {@code I18N_PROLOGUE_DEFAULT}
	 * <p>
	 * &apos;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;base href=&quot;&apos;{0}&apos;&quot;&gt;&lt;link rel=&quot;stylesheet&quot; type=&quot;text&frasl;css&quot; href=&quot;&apos;{1}&apos;&quot;&gt;&lt;&frasl;head&gt;&lt;body&gt;&apos;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nPrologueDefault(Object... arguments) {
		return format(I18N_PROLOGUE_DEFAULT, arguments);
	}

	/**
	 * Resource key {@code I18N_PROLOGUE_EXTENDED}
	 * <p>
	 * &apos;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;base href=&quot;&apos;{0}&apos;&quot;&gt;&lt;link rel=&quot;stylesheet&quot; type=&quot;text&frasl;css&quot; href=&quot;&apos;{1}&apos;&quot;&gt;&lt;&frasl;head&gt;&lt;body class=&quot;&apos;{2}&apos;&quot;&gt;
	 */
	public static final String I18N_PROLOGUE_EXTENDED = "I18N_PROLOGUE_EXTENDED";

	/**
	 * Resource string {@code I18N_PROLOGUE_EXTENDED}
	 * <p>
	 * &apos;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;base href=&quot;&apos;{0}&apos;&quot;&gt;&lt;link rel=&quot;stylesheet&quot; type=&quot;text&frasl;css&quot; href=&quot;&apos;{1}&apos;&quot;&gt;&lt;&frasl;head&gt;&lt;body class=&quot;&apos;{2}&apos;&quot;&gt;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nPrologueExtended(Object... arguments) {
		return format(I18N_PROLOGUE_EXTENDED, arguments);
	}

	/**
	 * Resource key {@code I18N_STYLE_END}
	 * <p>
	 * &apos;&lt;&frasl;span&gt;&apos;
	 */
	public static final String I18N_STYLE_END = "I18N_STYLE_END";

	/**
	 * Resource string {@code I18N_STYLE_END}
	 * <p>
	 * &apos;&lt;&frasl;span&gt;&apos;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nStyleEnd(Object... arguments) {
		return format(I18N_STYLE_END, arguments);
	}

	/**
	 * Resource key {@code I18N_STYLE_START}
	 * <p>
	 * &apos;&lt;span class=&quot;&apos;{0}&apos;&quot;&gt;&apos;
	 */
	public static final String I18N_STYLE_START = "I18N_STYLE_START";

	/**
	 * Resource string {@code I18N_STYLE_START}
	 * <p>
	 * &apos;&lt;span class=&quot;&apos;{0}&apos;&quot;&gt;&apos;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nStyleStart(Object... arguments) {
		return format(I18N_STYLE_START, arguments);
	}

	/**
	 * Resource key {@code I18N_TEXT_DEFAULT_RESULT_VIEW_HTML}
	 * <p>
	 * &apos;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;style&gt;body {background-color: &apos;{0}&apos;;color: &apos;{1}&apos;;}&lt;&frasl;style&gt;&lt;&frasl;head&gt;&lt;body&gt;&lt;code&gt;&apos;{2} {3} (build: {4})&apos;&lt;&frasl;code&gt;&lt;&frasl;body&gt;&lt;&frasl;html&gt;&apos;
	 */
	public static final String I18N_TEXT_DEFAULT_RESULT_VIEW_HTML = "I18N_TEXT_DEFAULT_RESULT_VIEW_HTML";

	/**
	 * Resource string {@code I18N_TEXT_DEFAULT_RESULT_VIEW_HTML}
	 * <p>
	 * &apos;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;style&gt;body {background-color: &apos;{0}&apos;;color: &apos;{1}&apos;;}&lt;&frasl;style&gt;&lt;&frasl;head&gt;&lt;body&gt;&lt;code&gt;&apos;{2} {3} (build: {4})&apos;&lt;&frasl;code&gt;&lt;&frasl;body&gt;&lt;&frasl;html&gt;&apos;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nTextDefaultResultViewHtml(Object... arguments) {
		return format(I18N_TEXT_DEFAULT_RESULT_VIEW_HTML, arguments);
	}

}
