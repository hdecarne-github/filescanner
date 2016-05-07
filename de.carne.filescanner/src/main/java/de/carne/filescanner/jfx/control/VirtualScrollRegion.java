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
package de.carne.filescanner.jfx.control;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

/**
 *
 */
class VirtualScrollRegion<C extends Node> extends Region {

	public interface Scrollable {

		public Dimension2D layoutVirtual();

	}

	private static final Bounds EMPTY_BOUNDS = new BoundingBox(0.0, 0.0, 0.0, 0.0);

	private final ScrollBar hBar;

	private final ScrollBar vBar;

	private final Scrollable scrollable;

	private final Rectangle contentClip;

	private C content;

	public VirtualScrollRegion(Scrollable scrollable) {
		this(scrollable, null);
	}

	public VirtualScrollRegion(Scrollable scrollable, C content) {
		assert scrollable != null;

		this.hBar = new ScrollBar();
		this.hBar.setOrientation(Orientation.HORIZONTAL);
		this.vBar = new ScrollBar();
		this.vBar.setOrientation(Orientation.VERTICAL);
		getChildren().addAll(this.hBar, this.vBar);
		this.scrollable = scrollable;
		this.contentClip = new Rectangle();
		setContent(content);
	}

	public void setContent(C content) {
		if (this.content != null) {
			this.content.setClip(null);
			getChildren().remove(this.content);
		}
		this.content = content;
		if (this.content != null) {
			this.content.setClip(this.contentClip);
			getChildren().add(this.content);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.scene.Parent#layoutChildren()
	 */
	@Override
	protected void layoutChildren() {
		Dimension2D contentDimension = this.scrollable.layoutVirtual();
		double contentWidth = contentDimension.getWidth();
		double contentHeight = contentDimension.getHeight();
		double regionWidth = getWidth();
		double regionHeight = getHeight();
		double hBarHeight = 0.0;
		double vBarWdith = 0.0;

		if (contentWidth <= regionWidth && contentHeight <= regionHeight) {
			this.hBar.setVisible(false);
			this.vBar.setVisible(false);
		}
		this.hBar.resizeRelocate(0.0, regionHeight - hBarHeight, regionWidth - vBarWdith, hBarHeight);
		this.vBar.resizeRelocate(regionWidth - vBarWdith, 0.0, vBarWdith, regionHeight - hBarHeight);
		this.contentClip.setX(0.0);
		this.contentClip.setY(0.0);
		this.contentClip.setWidth(regionWidth - vBarWdith);
		this.contentClip.setHeight(regionHeight - hBarHeight);
	}

}
