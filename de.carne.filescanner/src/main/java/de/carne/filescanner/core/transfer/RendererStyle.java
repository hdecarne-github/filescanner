/*
 * Copyright (c) 2007-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.core.transfer;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import de.carne.filescanner.core.transfer.ResultRenderer.Mode;
import de.carne.util.logging.Log;

/**
 * Class used for defining the rendering style.
 */
public final class RendererStyle {

	private static final Log LOG = new Log(RendererStyle.class);

	private static final String PREFERENCE_DELIMITER = "|";

	/**
	 * Font info.
	 */
	public static class FontInfo {

		private final String name;

		private final double size;

		FontInfo(String name, double size) {
			this.name = name;
			this.size = size;
		}

		FontInfo(FontInfo fontInfo) {
			this(fontInfo.name, fontInfo.size);
		}

		/**
		 * Get the font name.
		 *
		 * @return The font name.
		 */
		public String name() {
			return this.name;
		}

		/**
		 * Get the font size.
		 *
		 * @return The font size.
		 */
		public double size() {
			return this.size;
		}

	}

	private FontInfo fontInfo = new FontInfo("Courier New", 14.0);

	private HashMap<Mode, Integer> colors = new HashMap<>();

	/**
	 * Construct {@code RendererStyle}.
	 */
	public RendererStyle() {
		this.colors.put(Mode.NORMAL, 0x000000);
		this.colors.put(Mode.VALUE, 0x0000ff);
		this.colors.put(Mode.COMMENT, 0x00ff00);
		this.colors.put(Mode.KEYWORD, 0x660066);
		this.colors.put(Mode.OPERATOR, 0x000000);
		this.colors.put(Mode.LABEL, 0xc0c0c0);
		this.colors.put(Mode.ERROR, 0xff0000);
	}

	/**
	 * Set style.
	 *
	 * @param style The style to set.
	 */
	public void setStyle(RendererStyle style) {
		assert style != null;

		this.fontInfo = new FontInfo(style.fontInfo);
		this.colors.putAll(style.colors);
	}

	/**
	 * Set this style's font info.
	 *
	 * @param name The font name.
	 * @param size The font size.
	 */
	public void setFontInfo(String name, double size) {
		assert name != null;
		assert size > 0.0;

		this.fontInfo = new FontInfo(name, size);
	}

	/**
	 * Get this style's font info.
	 *
	 * @return This style's font info.
	 */
	public FontInfo getFontInfo() {
		return this.fontInfo;
	}

	/**
	 * Set this style's color for a specific {@linkplain Mode}.
	 *
	 * @param mode The mode to set the color for.
	 * @param color The color.
	 */
	public void setColor(Mode mode, int color) {
		assert mode != null;

		this.colors.put(mode, color);
	}

	/**
	 * Get this style's color for a specific {@linkplain Mode}.
	 *
	 * @param mode The mode to get the color for.
	 * @return This style's color for the submitted mode.
	 */
	public int getColor(Mode mode) {
		return this.colors.get(mode).intValue();
	}

	/**
	 * Convert to style to a string suitable for storing it as a preference.
	 *
	 * @return The preference string.
	 */
	public String toPreferenceString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append(this.fontInfo.name()).append(PREFERENCE_DELIMITER);
		buffer.append(Double.toHexString(this.fontInfo.size())).append(PREFERENCE_DELIMITER);
		buffer.append(Integer.toHexString(this.colors.get(Mode.NORMAL))).append(PREFERENCE_DELIMITER);
		buffer.append(Integer.toHexString(this.colors.get(Mode.VALUE))).append(PREFERENCE_DELIMITER);
		buffer.append(Integer.toHexString(this.colors.get(Mode.COMMENT))).append(PREFERENCE_DELIMITER);
		buffer.append(Integer.toHexString(this.colors.get(Mode.KEYWORD))).append(PREFERENCE_DELIMITER);
		buffer.append(Integer.toHexString(this.colors.get(Mode.OPERATOR))).append(PREFERENCE_DELIMITER);
		buffer.append(Integer.toHexString(this.colors.get(Mode.LABEL))).append(PREFERENCE_DELIMITER);
		buffer.append(Integer.toHexString(this.colors.get(Mode.ERROR))).append(PREFERENCE_DELIMITER);
		return buffer.toString();
	}

	/**
	 * Construct {@code ReferenceStyle} from a preference string.
	 *
	 * @param preference The preference string to use.
	 * @return The constructed style.
	 * @see #toPreferenceString()
	 */
	public static RendererStyle fromPreferenceString(String preference) {
		RendererStyle style = new RendererStyle();
		StringTokenizer tokens = new StringTokenizer(preference, PREFERENCE_DELIMITER);

		try {
			style.setFontInfo(tokens.nextToken(), Double.parseDouble(tokens.nextToken()));
			style.setColor(Mode.NORMAL, Integer.parseUnsignedInt(tokens.nextToken(), 16));
			style.setColor(Mode.VALUE, Integer.parseUnsignedInt(tokens.nextToken(), 16));
			style.setColor(Mode.COMMENT, Integer.parseUnsignedInt(tokens.nextToken(), 16));
			style.setColor(Mode.KEYWORD, Integer.parseUnsignedInt(tokens.nextToken(), 16));
			style.setColor(Mode.OPERATOR, Integer.parseUnsignedInt(tokens.nextToken(), 16));
			style.setColor(Mode.LABEL, Integer.parseUnsignedInt(tokens.nextToken(), 16));
			style.setColor(Mode.ERROR, Integer.parseUnsignedInt(tokens.nextToken(), 16));
		} catch (NoSuchElementException | NumberFormatException e) {
			LOG.warning(null, "Unexpected style preference ''{0}'', using defaults", preference);
		}
		return style;
	}

}
