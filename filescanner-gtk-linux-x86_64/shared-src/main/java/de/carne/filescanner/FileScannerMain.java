/*
 * Copyright (c) 2007-2017 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner;

import java.io.IOException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.carne.ApplicationMain;
import de.carne.util.Exceptions;
import de.carne.util.cmdline.CmdLineException;
import de.carne.util.cmdline.CmdLineProcessor;
import de.carne.util.logging.Log;
import de.carne.util.logging.Logs;

/**
 * Application entry point.
 */
public class FileScannerMain implements ApplicationMain {

	static {
		applyLogConfig(Logs.CONFIG_DEFAULT);
	}

	private static final Log LOG = new Log();

	private static final String NAME = "filescanner";

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public int run(String[] args) {
		CmdLineProcessor cmdLine = buildCmdLineProcessor(args);

		LOG.notice("Running ''{0}''...", cmdLine);

		int status;

		try {
			cmdLine.process();

			Display display = new Display();
			Shell shell = new Shell(display);

			shell.open();
			shell.pack();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			status = 0;
		} catch (CmdLineException e) {
			// TODO: Proper user message
			Exceptions.warn(e);
			status = -1;
		}
		return status;
	}

	private CmdLineProcessor buildCmdLineProcessor(String[] args) {
		CmdLineProcessor cmdLine = new CmdLineProcessor(name(), args);

		cmdLine.onSwitch((arg) -> applyLogConfig(Logs.CONFIG_VERBOSE)).arg("--verbose");
		cmdLine.onSwitch((arg) -> applyLogConfig(Logs.CONFIG_DEBUG)).arg("--debug");
		cmdLine.onSwitch((arg) -> showVersion()).arg("--version");
		return cmdLine;
	}

	private void showVersion() {
		// TODO: Display version info
	}

	private static void applyLogConfig(String config) {
		try {
			Logs.readConfig(config);
		} catch (IOException e) {
			Exceptions.warn(e);
		}
	}

}
