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
package de.carne.filescanner.swt.main;

import de.carne.boot.logging.Log;

/**
 * Main window controll.
 */
class MainController {

	private static final Log LOG = new Log();

	private final MainUI ui;

	MainController(MainUI ui) {
		this.ui = ui;
	}

	void onOpenSelected() {
		LOG.info("onOpenSelected");
	}

	void onQuitSelected() {
		this.ui.close();
	}

	void onCopyObjectSelected() {
		LOG.info("onCopyObjectSelected");
	}

	void onExportObjectSelected() {
		LOG.info("onExportObjectSelected");
	}

	void onGotoNextSelected() {
		LOG.info("onGotoNextSelected");
	}

	void onGotoPreviousSelected() {
		LOG.info("onGotoPreviousSelected");
	}

	void onGotoEndSelected() {
		LOG.info("onGotoEndSelected");
	}

	void onGotoStartSelected() {
		LOG.info("onGotoStartSelected");
	}

	void onStopScanSelected() {
		LOG.info("onStopScanSelected");
	}

}
