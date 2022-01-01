/*
 * Copyright (c) 2007-2022 Holger de Carne and contributors, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.carne.filescanner.swt.preferences;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.util.Check;
import de.carne.util.Exceptions;

/**
 * This class provides access to the current user's preferences.
 */
public class UserPreferences extends Config {

	private static final UserPreferences INSTANCE = new UserPreferences();

	private static final String KEY_INPUT_VIEW_FONT = "inputViewFont";
	private static final String KEY_RESULT_VIEW_FONT = "resultViewFont";
	private static final String KEY_RESULT_VIEW_BACKGROUND = "resultViewBackground";
	private static final String KEY_RESULT_VIEW_COLOR_NORMAL = "resultViewColorNormal";
	private static final String KEY_RESULT_VIEW_COLOR_VALUE = "resultViewColorValue";
	private static final String KEY_RESULT_VIEW_COLOR_COMMENT = "resultViewColorComment";
	private static final String KEY_RESULT_VIEW_COLOR_KEYWORD = "resultViewColorKeyword";
	private static final String KEY_RESULT_VIEW_COLOR_OPERATOR = "resultViewColorOperator";
	private static final String KEY_RESULT_VIEW_COLOR_LABEL = "resultViewColorLabel";
	private static final String KEY_RESULT_VIEW_COLOR_ERROR = "resultViewColorError";
	private static final String KEY_DISABLED_FORMATS = "disabledFormats";

	private final Preferences preferences = Preferences.userNodeForPackage(UserPreferences.class);
	private final Config defaults = Config.getDefaults();
	private final List<Consumer<Config>> consumers = new ArrayList<>();

	private UserPreferences() {
		// To prevent instantiation outside this class
	}

	/**
	 * Gets the current {@linkplain UserPreferences} instance.
	 *
	 * @return the current {@linkplain UserPreferences} instance.
	 */
	public static UserPreferences get() {
		return INSTANCE;
	}

	/**
	 * Adds a {@linkplain Consumer} instance to invoked every time the user preferences are changed.
	 *
	 * @param consumer {@linkplain Consumer} instance to add.
	 */
	public void addConsumer(Consumer<Config> consumer) {
		this.consumers.add(consumer);
		consumer.accept(this);
	}

	/**
	 * Removes a previously added {@linkplain Consumer} instance.
	 *
	 * @param consumer {@linkplain Consumer} instance to remove.
	 */
	public void removeConsumer(Consumer<Config> consumer) {
		this.consumers.remove(consumer);
	}

	/**
	 * Stores the current user preferences and triggers any registered {@linkplain Consumer} instance.
	 *
	 * @throws BackingStoreException if an error occurs while storing the preferences.
	 */
	public void store() throws BackingStoreException {
		this.preferences.sync();
		this.consumers.forEach(consumer -> consumer.accept(this));
	}

	@Override
	public FontData getInputViewFont() {
		return getFontDataPreference(KEY_INPUT_VIEW_FONT, this.defaults.getInputViewFont());
	}

	/**
	 * Sets the font for input data display.
	 *
	 * @param fontData the {@linkplain FontData} describing the font.
	 */
	public void setInputViewFont(FontData fontData) {
		this.preferences.put(KEY_INPUT_VIEW_FONT, fontData.toString());
	}

	@Override
	public FontData getResultViewFont() {
		return getFontDataPreference(KEY_RESULT_VIEW_FONT, this.defaults.getResultViewFont());
	}

	/**
	 * Sets the font for result data display.
	 *
	 * @param fontData the {@linkplain FontData} describing the font.
	 */
	public void setResultViewFont(FontData fontData) {
		this.preferences.put(KEY_RESULT_VIEW_FONT, fontData.toString());
	}

	@Override
	public @NonNull RGB getResultViewBackground() {
		return getRgbPreference(KEY_RESULT_VIEW_BACKGROUND, this.defaults.getResultViewBackground());
	}

	/**
	 * Sets the background color for result data display.
	 *
	 * @param rgb the {@linkplain RGB} instance describing the color.
	 */
	public void setResultViewBackground(RGB rgb) {
		this.preferences.put(KEY_RESULT_VIEW_BACKGROUND, rgb.toString());
	}

	@Override
	public RGB getResultViewColor(RenderStyle style) {
		return getRgbPreference(style2ColorKey(style), this.defaults.getResultViewColor(style));
	}

	@Override
	public Map<RenderStyle, RGB> getResultViewColors() {
		EnumMap<RenderStyle, RGB> colors = new EnumMap<>(RenderStyle.class);

		for (RenderStyle style : RenderStyle.values()) {
			colors.put(style, getResultViewColor(style));
		}
		return colors;
	}

	/**
	 * Sets the style color for result data display.
	 *
	 * @param style the style to set the color for.
	 * @param rgb the {@linkplain RGB} instance describing the color.
	 */
	public void setResultViewColor(RenderStyle style, RGB rgb) {
		this.preferences.put(style2ColorKey(style), rgb.toString());
	}

	@Override
	public Set<String> getDisabledFormats() {
		String disabledFormatsString = this.preferences.get(KEY_DISABLED_FORMATS,
				String.join("|", this.defaults.getDisabledFormats()));
		StringTokenizer disabledFormatsTokens = new StringTokenizer(disabledFormatsString, "|");
		Set<String> disabledFormats = new HashSet<>();

		while (disabledFormatsTokens.hasMoreTokens()) {
			disabledFormats.add(disabledFormatsTokens.nextToken());
		}
		return disabledFormats;
	}

	/**
	 * Sets the disabled formats.
	 *
	 * @param disabledFormats the set of disabled formats.
	 */
	public void setDisabledFormats(Set<String> disabledFormats) {
		this.preferences.put(KEY_DISABLED_FORMATS, String.join("|", disabledFormats));
	}

	private FontData getFontDataPreference(String key, FontData defaultFontData) {
		String fontDataString = this.preferences.get(key, defaultFontData.toString());
		FontData fontData = null;

		try {
			fontData = new FontData(fontDataString);
		} catch (IllegalArgumentException e) {
			Exceptions.warn(e);
		}
		return (fontData != null ? fontData : defaultFontData);
	}

	private static final Pattern RGB_PATTERN = Pattern.compile("RGB \\{(\\d*), (\\d*), (\\d*)\\}");

	private RGB getRgbPreference(String key, RGB defaultRgb) {
		String rgbString = this.preferences.get(key, defaultRgb.toString());
		RGB rgb = null;

		try {
			Matcher matcher = RGB_PATTERN.matcher(rgbString);

			if (matcher.matches()) {
				int red = Integer.parseInt(matcher.group(1));
				int green = Integer.parseInt(matcher.group(2));
				int blue = Integer.parseInt(matcher.group(3));

				rgb = new RGB(red, green, blue);
			}
		} catch (NumberFormatException e) {
			Exceptions.warn(e);
		}
		return (rgb != null ? rgb : defaultRgb);
	}

	private String style2ColorKey(RenderStyle style) {
		String colorKey;

		switch (style) {
		case NORMAL:
			colorKey = KEY_RESULT_VIEW_COLOR_NORMAL;
			break;
		case VALUE:
			colorKey = KEY_RESULT_VIEW_COLOR_VALUE;
			break;
		case COMMENT:
			colorKey = KEY_RESULT_VIEW_COLOR_COMMENT;
			break;
		case KEYWORD:
			colorKey = KEY_RESULT_VIEW_COLOR_KEYWORD;
			break;
		case OPERATOR:
			colorKey = KEY_RESULT_VIEW_COLOR_OPERATOR;
			break;
		case LABEL:
			colorKey = KEY_RESULT_VIEW_COLOR_LABEL;
			break;
		case ERROR:
			colorKey = KEY_RESULT_VIEW_COLOR_ERROR;
			break;
		default:
			throw Check.unexpected(style);
		}
		return colorKey;
	}

}
