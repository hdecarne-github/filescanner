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
package de.carne.filescanner.jfx.session;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

import de.carne.util.logging.Log;
import javafx.geometry.Bounds;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.PopupWindow.AnchorLocation;

/**
 * Helper class for notification display and management.
 */
final class Notifications {

	private static final Log LOG = new Log(Notifications.class);

	private static final int TRACE_LIMIT = 3;

	private static final int TOOLTIP_LIMIT = 5;

	private final Region displayRegion;

	private final LinkedList<Tooltip> tooltips = new LinkedList<>();

	private Tooltip defaultTooltip = null;

	Notifications(Region displayRegion) {
		this.displayRegion = displayRegion;
	}

	public void clear() {
		for (Tooltip tooltip : this.tooltips) {
			tooltip.hide();
		}
		this.tooltips.clear();
		if (this.defaultTooltip != null) {
			this.defaultTooltip.hide();
		}
	}

	public void toggleDisplay(Image defaultIcon, String defaultMessage) {
		if (this.tooltips.size() > 0) {
			showTooltips(true);
		} else {
			if (this.defaultTooltip == null) {
				this.defaultTooltip = createTooltip(defaultIcon, defaultMessage, null);
			}
			if (this.defaultTooltip.isShowing()) {
				this.defaultTooltip.hide();
			} else {
				showTooltip(this.defaultTooltip, 0.0);
			}
		}
	}

	public void showNotice(Image icon, String message) {
		LOG.notice(null, message);
		addTooltip(icon, message, null);
		showTooltips(false);
	}

	public void showWarning(Image icon, String message, Throwable details) {
		LOG.warning(details, null, message);
		addTooltip(icon, message, details);
		showTooltips(false);
	}

	public void showError(Image icon, String message, Throwable details) {
		LOG.error(details, null, message);
		addTooltip(icon, message, details);
		showTooltips(false);
	}

	private void addTooltip(Image icon, String message, Throwable details) {
		this.tooltips.addFirst(createTooltip(icon, message, details));
		while (this.tooltips.size() > TOOLTIP_LIMIT) {
			this.tooltips.removeLast().hide();
		}
	}

	private void showTooltips(boolean all) {
		Tooltip recentTooltip = this.tooltips.getFirst();
		double displacement = showTooltip(recentTooltip, 0.0);

		for (Tooltip tooltip : this.tooltips) {
			if (tooltip == recentTooltip) {
				continue;
			}
			if (all || tooltip.isShowing()) {
				displacement = showTooltip(tooltip, displacement);
			}
		}
	}

	private double showTooltip(Tooltip tooltip, double displacement) {
		double gap = tooltip.getGraphicTextGap();
		Bounds bounds = this.displayRegion.localToScreen(this.displayRegion.getBoundsInLocal());

		tooltip.show(this.displayRegion.getScene().getWindow());
		tooltip.setAnchorX(bounds.getMinX() + gap);

		double nextDisplacement = displacement + gap + tooltip.getHeight();

		tooltip.setAnchorY(bounds.getMaxY() - nextDisplacement);
		tooltip.setAutoHide(true);
		return nextDisplacement;
	}

	private static Tooltip createTooltip(Image icon, String message, Throwable details) {
		String tooltipMessage;

		if (details != null) {
			StringWriter buffer = new StringWriter();
			PrintWriter writer = new PrintWriter(buffer);

			writer.print(message);

			int traceIndex = 0;

			for (StackTraceElement trace : details.getStackTrace()) {
				if (traceIndex >= TRACE_LIMIT) {
					writer.println();
					writer.print("\t\u2026");
					break;
				}
				traceIndex++;
				writer.println();
				writer.print("\tat ");
				writer.print(trace.toString());
			}
			writer.flush();
			tooltipMessage = buffer.toString();
		} else {
			tooltipMessage = message;
		}

		Tooltip tooltip = new Tooltip(tooltipMessage);

		tooltip.setGraphic(new ImageView(icon));
		tooltip.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
		return tooltip;
	}

}
