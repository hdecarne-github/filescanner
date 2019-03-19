/*
 * Copyright (c) 2007-2019 Holger de Carne and contributors, All Rights Reserved.
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

import de.carne.boot.platform.Platform;

final class LongScrollBarProxy extends ScrollBarProxy {

	private static final long SCROLL_LIMIT = (Platform.IS_MACOS ? Integer.MAX_VALUE >> 8 : Integer.MAX_VALUE);

	private long maximum = 1;
	private long thumb = 1;
	private long scale = 1;
	private long selection = 0;

	public LongScrollBarProxy(ScrollBar scrollBar) {
		super(scrollBar);
	}

	public long selection() {
		return this.selection;
	}

	@SuppressWarnings("hiding")
	public void layout(long maximum, long thumb) {
		this.maximum = maximum;
		this.thumb = thumb;
		this.scale = 1;
		while ((this.maximum + this.scale - 1) / this.scale > SCROLL_LIMIT) {
			this.scale++;
		}
		scrollTo(this.selection);
		setValues((int) (this.selection / this.scale), (int) (this.maximum / this.scale),
				(int) Math.max(this.thumb / this.scale, 1));
	}

	@SuppressWarnings("hiding")
	public long scrollTo(long selection) {
		this.selection = Math.max(Math.min(selection, this.maximum - this.thumb), 0);
		setSelection((int) (this.selection / this.scale));
		return this.selection;
	}

	public long scrollLine(int direction) {
		return scrollRelative(direction, 1);
	}

	public long scrollPage(int direction) {
		return scrollRelative(direction, this.thumb);
	}

	private long scrollRelative(int direction, long distance) {
		if (direction > 0) {
			this.selection = Math.max(Math.min(this.selection + distance, this.maximum - this.thumb), 0);
		} else if (direction < 0) {
			this.selection = Math.max(Math.min(this.selection - distance, this.maximum - this.thumb), 0);
		}
		setSelection((int) (this.selection / this.scale));
		return this.selection;
	}

	@Override
	protected void onSelectionChanged(int scrollBarSelection, int limit) {
		long lowerSelection = scrollBarSelection * this.scale;
		long upperSelection = lowerSelection + this.scale - 1;

		if (scrollBarSelection == 0) {
			scrollTo(0);
		} else if (scrollBarSelection == limit) {
			scrollTo(this.maximum);
		} else if (this.selection < lowerSelection) {
			scrollTo(lowerSelection);
		} else if (this.selection > upperSelection) {
			scrollTo(upperSelection);
		}
		super.onSelectionChanged(scrollBarSelection, limit);
	}

}
