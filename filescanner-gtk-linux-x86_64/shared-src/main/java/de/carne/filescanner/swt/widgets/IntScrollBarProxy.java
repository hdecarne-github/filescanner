/*
 * Copyright (c) 2007-2020 Holger de Carne and contributors, All Rights Reserved.
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

import org.eclipse.swt.widgets.ScrollBar;

final class IntScrollBarProxy extends ScrollBarProxy {

	public IntScrollBarProxy(ScrollBar scrollBar) {
		super(scrollBar);
	}

	public int selection() {
		return getSelection();
	}

	public void layout(int maximum, int thumb) {
		setValues(-1, maximum, thumb);
	}

	public int scrollTo(int selection) {
		setSelection(selection);
		return getSelection();
	}

	public int scrollLines(int delta) {
		if (delta != 0) {
			setSelection(getSelection() + delta);
		}
		return getSelection();
	}

	public int scrollPage(int direction) {
		return scrollLines(Integer.signum(direction) * getPageIncrement());
	}

}
