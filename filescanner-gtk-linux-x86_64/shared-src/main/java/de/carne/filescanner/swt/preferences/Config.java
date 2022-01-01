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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.swt.platform.PlatformIntegration;

/**
 * Base class defining the application wide configuration options.
 */
public abstract class Config {

	/**
	 * Gets the platform specific default {@linkplain Config}.
	 *
	 * @return the platform specific default {@linkplain Config}.
	 */
	public static Config getDefaults() {
		Config defaults;

		if (PlatformIntegration.isCocoa()) {
			defaults = new CocoaDefaults();
		} else {
			defaults = new GenericDefaults();
		}
		return defaults;
	}

	/**
	 * Gets the configured font for input data display.
	 *
	 * @return the configured font for input data display.
	 */
	public abstract FontData getInputViewFont();

	/**
	 * Gets the configured font for result data display.
	 *
	 * @return the configured font for result data display.
	 */
	public abstract FontData getResultViewFont();

	/**
	 * Gets the configured background color for result data display.
	 *
	 * @return the configured background color for result data display.
	 */
	public abstract RGB getResultViewBackground();

	/**
	 * Gets the configured style color for result data display.
	 *
	 * @param style the style to get the color for.
	 * @return the configured style color for result data display.
	 */
	public abstract RGB getResultViewColor(RenderStyle style);

	/**
	 * Gets the configured style colors for result data display.
	 *
	 * @return the configured style colors for result data display.
	 */
	public abstract Map<RenderStyle, RGB> getResultViewColors();

	/**
	 * Gets the set of disabled formats.
	 *
	 * @return the set of disabled formats.
	 */
	public abstract Set<String> getDisabledFormats();

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("config:").append(System.lineSeparator());
		buffer.append(" inputViewFont: ").append(getInputViewFont()).append(System.lineSeparator());
		buffer.append(" resultViewFont: ").append(getResultViewFont()).append(System.lineSeparator());
		buffer.append(" resultViewBackground: ").append(getResultViewBackground()).append(System.lineSeparator());
		for (RenderStyle renderStyle : RenderStyle.values()) {
			buffer.append(" resultViewColor (").append(renderStyle).append("): ")
					.append(getResultViewColor(renderStyle)).append(System.lineSeparator());
		}
		buffer.append(" disabledFormats: ").append(getDisabledFormats());
		return buffer.toString();
	}

	private static class GenericDefaults extends Config {

		private static final RGB DEFAULT_COLOR = new RGB(0x00, 0x00, 0x00);
		private static final Map<RenderStyle, RGB> DEFAULT_COLORS = new EnumMap<>(RenderStyle.class);

		static {
			DEFAULT_COLORS.put(RenderStyle.NORMAL, DEFAULT_COLOR);
			DEFAULT_COLORS.put(RenderStyle.VALUE, new RGB(0x33, 0x4d, 0xb3));
			DEFAULT_COLORS.put(RenderStyle.COMMENT, new RGB(0x66, 0x99, 0x66));
			DEFAULT_COLORS.put(RenderStyle.KEYWORD, new RGB(0x66, 0x00, 0x66));
			DEFAULT_COLORS.put(RenderStyle.OPERATOR, DEFAULT_COLOR);
			DEFAULT_COLORS.put(RenderStyle.LABEL, new RGB(0xc0, 0xc0, 0xc0));
			DEFAULT_COLORS.put(RenderStyle.ERROR, new RGB(0xff, 0x00, 0x00));
		}

		GenericDefaults() {
			// Nothing to do here
		}

		@Override
		public FontData getInputViewFont() {
			return new FontData("Courier New", 11, SWT.NORMAL);
		}

		@Override
		public FontData getResultViewFont() {
			return new FontData();
		}

		@Override
		public @NonNull RGB getResultViewBackground() {
			return new RGB(0xfc, 0xfc, 0xfc);
		}

		@Override
		public RGB getResultViewColor(RenderStyle style) {
			return DEFAULT_COLORS.getOrDefault(style, DEFAULT_COLOR);
		}

		@Override
		public Map<RenderStyle, RGB> getResultViewColors() {
			return Collections.unmodifiableMap(DEFAULT_COLORS);
		}

		@Override
		public Set<String> getDisabledFormats() {
			return Collections.emptySet();
		}

	}

	private static class CocoaDefaults extends GenericDefaults {

		CocoaDefaults() {
			// Nothing to do here
		}

		@Override
		public FontData getInputViewFont() {
			return new FontData("Monaco", 11, SWT.NORMAL);
		}

	}

}
