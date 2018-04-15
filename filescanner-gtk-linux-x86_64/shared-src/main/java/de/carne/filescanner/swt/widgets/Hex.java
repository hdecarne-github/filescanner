/*
 * Copyright (c) 2007-2018 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Custom control for displaying raw hexadecimal data to the user.
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class Hex extends Canvas {

	/**
	 * Constructs a new {@linkplain Hex} instance.
	 *
	 * @param parent the widget's owner.
	 * @param style the widget's style.
	 */
	public Hex(Composite parent, int style) {
		super(parent, style);

		Display display = getDisplay();

		setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
	}

}
