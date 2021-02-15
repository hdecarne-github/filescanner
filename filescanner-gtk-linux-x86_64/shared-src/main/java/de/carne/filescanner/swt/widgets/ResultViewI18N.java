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
	 * &#39;&lt;br&gt;&#39;
	 */
	public static final String I18N_BREAK = "I18N_BREAK";

	/**
	 * Resource string {@code I18N_BREAK}
	 * <p>
	 * &#39;&lt;br&gt;&#39;
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
	 * &#39;&lt;&frasl;body&gt;&lt;&frasl;html&gt;&#39;
	 */
	public static final String I18N_EPILOGUE = "I18N_EPILOGUE";

	/**
	 * Resource string {@code I18N_EPILOGUE}
	 * <p>
	 * &#39;&lt;&frasl;body&gt;&lt;&frasl;html&gt;&#39;
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
	 * &#39;&lt;a href=&quot;&#39;{0}&#39;&quot;&gt;&#39;{1}&#39;&lt;&frasl;a&gt;&#39;
	 */
	public static final String I18N_HREF = "I18N_HREF";

	/**
	 * Resource string {@code I18N_HREF}
	 * <p>
	 * &#39;&lt;a href=&quot;&#39;{0}&#39;&quot;&gt;&#39;{1}&#39;&lt;&frasl;a&gt;&#39;
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
	 * &#39;&lt;img src=&quot;&#39;{0}&#39;&quot; alt=&quot;&#39;{1}&#39;&quot;&gt;&#39;
	 */
	public static final String I18N_IMG = "I18N_IMG";

	/**
	 * Resource string {@code I18N_IMG}
	 * <p>
	 * &#39;&lt;img src=&quot;&#39;{0}&#39;&quot; alt=&quot;&#39;{1}&#39;&quot;&gt;&#39;
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
	 * &#39;&lt;div class=&quot;indent&quot;&gt;&#39;
	 */
	public static final String I18N_INDENT_IN = "I18N_INDENT_IN";

	/**
	 * Resource string {@code I18N_INDENT_IN}
	 * <p>
	 * &#39;&lt;div class=&quot;indent&quot;&gt;&#39;
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
	 * &#39;&lt;&frasl;div&gt;&#39;
	 */
	public static final String I18N_INDENT_OUT = "I18N_INDENT_OUT";

	/**
	 * Resource string {@code I18N_INDENT_OUT}
	 * <p>
	 * &#39;&lt;&frasl;div&gt;&#39;
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
	 * &#39;[&lt;a href=&quot;&#39;{0}&#39;&quot;&gt;&#39;{1}&#39;&lt;&frasl;a&gt;]&#39;
	 */
	public static final String I18N_MEDIA = "I18N_MEDIA";

	/**
	 * Resource string {@code I18N_MEDIA}
	 * <p>
	 * &#39;[&lt;a href=&quot;&#39;{0}&#39;&quot;&gt;&#39;{1}&#39;&lt;&frasl;a&gt;]&#39;
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
	 * &#39;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;base href=&quot;&#39;{0}&#39;&quot;&gt;&lt;link rel=&quot;stylesheet&quot; type=&quot;text&frasl;css&quot; href=&quot;&#39;{1}&#39;&quot;&gt;&lt;&frasl;head&gt;&lt;body&gt;&#39;
	 */
	public static final String I18N_PROLOGUE_DEFAULT = "I18N_PROLOGUE_DEFAULT";

	/**
	 * Resource string {@code I18N_PROLOGUE_DEFAULT}
	 * <p>
	 * &#39;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;base href=&quot;&#39;{0}&#39;&quot;&gt;&lt;link rel=&quot;stylesheet&quot; type=&quot;text&frasl;css&quot; href=&quot;&#39;{1}&#39;&quot;&gt;&lt;&frasl;head&gt;&lt;body&gt;&#39;
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
	 * &#39;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;base href=&quot;&#39;{0}&#39;&quot;&gt;&lt;link rel=&quot;stylesheet&quot; type=&quot;text&frasl;css&quot; href=&quot;&#39;{1}&#39;&quot;&gt;&lt;&frasl;head&gt;&lt;body class=&quot;&#39;{2}&#39;&quot;&gt;
	 */
	public static final String I18N_PROLOGUE_EXTENDED = "I18N_PROLOGUE_EXTENDED";

	/**
	 * Resource string {@code I18N_PROLOGUE_EXTENDED}
	 * <p>
	 * &#39;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;base href=&quot;&#39;{0}&#39;&quot;&gt;&lt;link rel=&quot;stylesheet&quot; type=&quot;text&frasl;css&quot; href=&quot;&#39;{1}&#39;&quot;&gt;&lt;&frasl;head&gt;&lt;body class=&quot;&#39;{2}&#39;&quot;&gt;
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
	 * &#39;&lt;&frasl;span&gt;&#39;
	 */
	public static final String I18N_STYLE_END = "I18N_STYLE_END";

	/**
	 * Resource string {@code I18N_STYLE_END}
	 * <p>
	 * &#39;&lt;&frasl;span&gt;&#39;
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
	 * &#39;&lt;span class=&quot;&#39;{0}&#39;&quot;&gt;&#39;
	 */
	public static final String I18N_STYLE_START = "I18N_STYLE_START";

	/**
	 * Resource string {@code I18N_STYLE_START}
	 * <p>
	 * &#39;&lt;span class=&quot;&#39;{0}&#39;&quot;&gt;&#39;
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
	 * &#39;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;style&gt;body {background-color: &#39;{0}&#39;;color: &#39;{1}&#39;;}&lt;&frasl;style&gt;&lt;&frasl;head&gt;&lt;body&gt;&lt;code&gt;&#39;{2} {3} (build: {4})&#39;&lt;&frasl;code&gt;&lt;&frasl;body&gt;&lt;&frasl;html&gt;&#39;
	 */
	public static final String I18N_TEXT_DEFAULT_RESULT_VIEW_HTML = "I18N_TEXT_DEFAULT_RESULT_VIEW_HTML";

	/**
	 * Resource string {@code I18N_TEXT_DEFAULT_RESULT_VIEW_HTML}
	 * <p>
	 * &#39;&lt;!DOCTYPE HTML&gt;&lt;html&gt;&lt;head&gt;&lt;meta charset=&quot;utf-8&quot;&gt;&lt;style&gt;body {background-color: &#39;{0}&#39;;color: &#39;{1}&#39;;}&lt;&frasl;style&gt;&lt;&frasl;head&gt;&lt;body&gt;&lt;code&gt;&#39;{2} {3} (build: {4})&#39;&lt;&frasl;code&gt;&lt;&frasl;body&gt;&lt;&frasl;html&gt;&#39;
	 *
	 * @param arguments Format arguments.
	 * @return The formatted string.
	 */
	public static String i18nTextDefaultResultViewHtml(Object... arguments) {
		return format(I18N_TEXT_DEFAULT_RESULT_VIEW_HTML, arguments);
	}

}
