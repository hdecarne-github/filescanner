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
package de.carne.filescanner.swt.widgets;

import java.util.function.Supplier;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ScrollBar;

abstract class ScrollBarProxy implements Supplier<ScrollBar>, SelectionListener {

	private final ScrollBar scrollBar;

	protected ScrollBarProxy(ScrollBar scrollBar) {
		this.scrollBar = scrollBar;
		this.scrollBar.addSelectionListener(this);
	}

	@Override
	public ScrollBar get() {
		return this.scrollBar;
	}

	public boolean isVisible() {
		return this.scrollBar.isVisible();
	}

	public void setVisible(boolean visible) {
		this.scrollBar.setVisible(visible);
	}

	protected void setValues(int selection, int maximum, int thumb) {
		int selectionValue = (selection >= 0 ? selection : this.scrollBar.getSelection());

		this.scrollBar.setValues(Math.max(0, Math.min(maximum, selectionValue)), 0, maximum, thumb, 1, thumb);
	}

	protected int getPageIncrement() {
		return this.scrollBar.getPageIncrement();
	}

	protected int getSelection() {
		return this.scrollBar.getSelection();
	}

	protected void setSelection(int selection) {
		this.scrollBar.setSelection(selection);
	}

	@SuppressWarnings("unused")
	protected void onSelectionChanged(int selection, int limit) {
		this.scrollBar.setSelection(selection);
		this.scrollBar.getParent().redraw();
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		widgetDefaultSelected(event);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent event) {
		onSelectionChanged(this.scrollBar.getSelection(), this.scrollBar.getMaximum() - this.scrollBar.getThumb());
	}

}
