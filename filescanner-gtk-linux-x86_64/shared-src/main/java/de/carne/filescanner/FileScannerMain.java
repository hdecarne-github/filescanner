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
import de.carne.filescanner.swt.main.MainInterface;
import de.carne.filescanner.swt.resources.Images;
import de.carne.swt.UserApplication;
import de.carne.swt.graphics.ResourceException;
import de.carne.swt.widgets.UserInterface;
import de.carne.util.ApplicationManifestInfo;
import de.carne.util.Exceptions;
import de.carne.util.Late;
import de.carne.util.cmdline.CmdLineException;
import de.carne.util.cmdline.CmdLineProcessor;
import de.carne.util.logging.Log;
import de.carne.util.logging.Logs;

/**
 * Application entry point.
 */
public class FileScannerMain extends UserApplication implements ApplicationMain {

	static {
		applyLogConfig(Logs.CONFIG_DEFAULT);
	}

	private static final Log LOG = new Log();

	private static final String NAME = "filescanner";

	private final Late<MainInterface> mainViewHolder = new Late<>();

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public int run(String[] args) {
		int status;

		try {
			CmdLineProcessor logConfigCmdLine = buildLogConfigCmdLine(args);

			logConfigCmdLine.process();

			LOG.notice("Running ''{0}''...", logConfigCmdLine);

			CmdLineProcessor applicationCmdLine = buildApplicationCmdLine(args);

			status = run(applicationCmdLine);
		} catch (CmdLineException | ResourceException e) {
			Exceptions.warn(e);
			status = -1;
		} finally {
			Logs.flush();
		}
		return status;
	}

	@Override
	protected Display setupDisplay() throws ResourceException {
		Display.setAppName(ApplicationManifestInfo.APPLICATION_NAME);
		Display.setAppVersion(ApplicationManifestInfo.APPLICATION_VERSION);

		Display display = new Display();

		Images.setup(display);
		return display;
	}

	@Override
	protected UserInterface<Shell> setupUserInterface(Display display) throws ResourceException {
		Shell root = new Shell(display);
		MainInterface mainView = this.mainViewHolder.set(new MainInterface());

		mainView.setup(root);
		return mainView;
	}

	private static void applyLogConfig(String config) {
		try {
			Logs.readConfig(config);
		} catch (IOException e) {
			Exceptions.warn(e);
		}
	}

	private CmdLineProcessor buildLogConfigCmdLine(String[] args) {
		CmdLineProcessor cmdLine = new CmdLineProcessor(name(), args);

		cmdLine.onSwitch(arg -> applyLogConfig(Logs.CONFIG_VERBOSE)).arg("--verbose");
		cmdLine.onSwitch(arg -> applyLogConfig(Logs.CONFIG_DEBUG)).arg("--debug");
		cmdLine.onUnnamedOption(CmdLineProcessor::ignore);
		cmdLine.onUnknownArg(CmdLineProcessor::ignore);
		return cmdLine;
	}

	private CmdLineProcessor buildApplicationCmdLine(String[] args) {
		CmdLineProcessor cmdLine = new CmdLineProcessor(name(), args);

		cmdLine.onSwitch(CmdLineProcessor::ignore).arg("--verbose");
		cmdLine.onSwitch(CmdLineProcessor::ignore).arg("--debug");
		cmdLine.onUnnamedOption(CmdLineProcessor::ignore);
		cmdLine.onUnknownArg(CmdLineProcessor::ignore);
		return cmdLine;
	}

}
