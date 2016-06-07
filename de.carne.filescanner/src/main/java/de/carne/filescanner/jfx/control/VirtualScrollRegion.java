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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

/**
 * Scrolling content of virtual size.
 */
class VirtualScrollRegion<C extends Node> extends Region {

	public interface Scrollable {

		public VirtualScrollLayout layoutVirtual(double viewWidth, double viewHeight);

		public Point2D mapViewPort(double hValue, double vValue);

		public void hScrollTo(double value);

		public void vScrollTo(double value);

	}

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
		this.hBar.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				onHScroll(newValue.doubleValue());
			}

		});
		this.vBar = new ScrollBar();
		this.vBar.setOrientation(Orientation.VERTICAL);
		this.vBar.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				onVScroll(newValue.doubleValue());
			}

		});
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

	public void scrollTo(double hValue, double vValue) {
		this.vBar.setValue(vValue);
		this.hBar.setValue(hValue);
	}

	@Override
	protected void layoutChildren() {
		double regionWidth = getWidth();
		double regionHeight = getHeight();
		VirtualScrollLayout scrollableSize = this.scrollable.layoutVirtual(regionWidth, regionHeight);
		double contentWidth = scrollableSize.getWidth();
		double contentHeight = scrollableSize.getHeight();
		double hBarHeight = 0.0;
		double vBarWdith = 0.0;

		if (contentWidth <= regionWidth && contentHeight <= regionHeight) {
			this.hBar.setVisible(false);
			this.vBar.setVisible(false);
		} else if (contentWidth > regionWidth) {
			this.hBar.setVisible(true);
			this.hBar.autosize();
			hBarHeight = this.hBar.getHeight();
			regionHeight -= hBarHeight;
			if (contentHeight > regionHeight) {
				this.vBar.setVisible(true);
				this.vBar.autosize();
				vBarWdith = this.vBar.getWidth();
				regionWidth -= vBarWdith;
			} else {
				this.vBar.setVisible(false);
			}
		} else if (contentHeight > regionHeight) {
			this.vBar.setVisible(true);
			this.vBar.autosize();
			vBarWdith = this.vBar.getWidth();
			regionWidth -= vBarWdith;
			if (contentWidth > regionWidth) {
				this.hBar.setVisible(true);
				this.hBar.autosize();
				hBarHeight = this.hBar.getHeight();
				regionHeight -= hBarHeight;
			} else {
				this.hBar.setVisible(false);
			}
		}
		if (this.hBar.isVisible()) {
			double maxValue = contentWidth - regionWidth;

			this.hBar.setMax(maxValue);
			this.hBar.setVisibleAmount(regionWidth / contentWidth * maxValue);
			this.hBar.setUnitIncrement(scrollableSize.getHIncrement());
			this.hBar.setBlockIncrement(regionWidth);
		} else {
			this.hBar.setValue(0.0);
		}
		if (this.vBar.isVisible()) {
			double maxValue = contentHeight - regionHeight;

			this.vBar.setMax(maxValue);
			this.vBar.setVisibleAmount(regionHeight / contentHeight * maxValue);
			this.vBar.setUnitIncrement(scrollableSize.getVIncrement());
			this.vBar.setBlockIncrement(regionHeight);
		} else {
			this.vBar.setValue(0.0);
		}
		this.hBar.resizeRelocate(0.0, regionHeight, regionWidth, hBarHeight);
		this.vBar.resizeRelocate(regionWidth, 0.0, vBarWdith, regionHeight);

		Point2D scrollableViewPort = this.scrollable.mapViewPort(this.hBar.getValue(), this.vBar.getValue());

		if (this.content != null) {
			this.content.setLayoutX(scrollableViewPort.getX());
			this.content.setLayoutY(scrollableViewPort.getY());
		}
		this.contentClip.setX(-scrollableViewPort.getX());
		this.contentClip.setY(-scrollableViewPort.getY());
		this.contentClip.setWidth(regionWidth);
		this.contentClip.setHeight(regionHeight);
	}

	void onHScroll(double value) {
		this.scrollable.hScrollTo(value);
	}

	void onVScroll(double value) {
		this.scrollable.vScrollTo(value);
	}

}
