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
package de.carne.filescanner;

import java.io.IOException;
import java.util.Optional;
import java.util.SortedMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.carne.boot.ApplicationMain;
import de.carne.boot.Exceptions;
import de.carne.boot.logging.Log;
import de.carne.boot.logging.Logs;
import de.carne.filescanner.platform.FileScannerPlatform;
import de.carne.filescanner.swt.main.MainUI;
import de.carne.swt.UserApplication;
import de.carne.swt.graphics.ResourceException;
import de.carne.swt.widgets.ShellUserInterface;
import de.carne.util.Late;
import de.carne.util.Lazy;
import de.carne.util.ManifestInfos;
import de.carne.util.cmdline.CmdLineException;
import de.carne.util.cmdline.CmdLineProcessor;

/**
 * Application main class.
 */
public class FileScannerMain extends UserApplication implements ApplicationMain {

	static {
		applyLogConfig(Logs.CONFIG_DEFAULT);
	}

	private static final Log LOG = new Log();

	private static final String NAME = "filescanner";

	private final Late<MainUI> mainInterfaceHolder = new Late<>();
	private final Lazy<ExecutorService> cachedThreadPoolHolder = new Lazy<>(Executors::newCachedThreadPool);

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public int run(@Nullable String[] args) {
		int status;

		try {
			FileScannerPlatform.setupPlatform();

			CmdLineProcessor logConfigCmdLine = buildLogConfigCmdLine(args);

			logConfigCmdLine.process();

			LOG.notice("Running command ''{0}''...", logConfigCmdLine);

			logRuntimeInfo();

			Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 1);

			CmdLineProcessor applicationCmdLine = buildApplicationCmdLine(args);

			status = run(applicationCmdLine);
			shutdownCachedThreaddPool();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			Exceptions.warn(e);
			status = -1;
		} catch (CmdLineException | ResourceException e) {
			Exceptions.warn(e);
			status = -1;
		} finally {
			Logs.flush();
		}
		return status;
	}

	/**
	 * Gets this application's cached thread pool.
	 *
	 * @return this application's cached thread pool.
	 */
	public ExecutorService cachedThreadPool() {
		return this.cachedThreadPoolHolder.get();
	}

	/**
	 * Requests the application to shutdown.
	 */
	public void requestShutdown() {
		this.mainInterfaceHolder.get().close();
	}

	@Override
	protected Display setupDisplay() throws ResourceException {
		ManifestInfos fileScannerInfos = new ModuleManifestInfos();
		Display.setAppName(fileScannerInfos.name());
		Display.setAppVersion(fileScannerInfos.version());

		return new Display();
	}

	@Override
	protected ShellUserInterface setupUserInterface(Display display) throws ResourceException {
		return this.mainInterfaceHolder.set(new MainUI(new Shell(display)));
	}

	private void logRuntimeInfo() {
		SortedMap<String, ManifestInfos> runtimeInfos = ManifestInfos.getRuntimeInfos();

		LOG.info("Runtime infos:");
		for (ManifestInfos manifestInfos : runtimeInfos.values()) {
			LOG.info(" {0}", manifestInfos);
		}
	}

	private CmdLineProcessor buildLogConfigCmdLine(@Nullable String[] args) {
		CmdLineProcessor cmdLine = new CmdLineProcessor(name(), args);

		cmdLine.onSwitch(arg -> applyLogConfig(Logs.CONFIG_VERBOSE)).arg("--verbose");
		cmdLine.onSwitch(arg -> applyLogConfig(Logs.CONFIG_DEBUG)).arg("--debug");
		cmdLine.onUnnamedOption(CmdLineProcessor::ignore);
		cmdLine.onUnknownArg(CmdLineProcessor::ignore);
		return cmdLine;
	}

	private CmdLineProcessor buildApplicationCmdLine(@Nullable String[] args) {
		CmdLineProcessor cmdLine = new CmdLineProcessor(name(), args);

		cmdLine.onSwitch(CmdLineProcessor::ignore).arg("--verbose");
		cmdLine.onSwitch(CmdLineProcessor::ignore).arg("--debug");
		cmdLine.onUnnamedOption(this::openFile);
		cmdLine.onUnknownArg(this::logUnknownArg);
		return cmdLine;
	}

	private void openFile(String arg) {
		this.mainInterfaceHolder.get().openCommandLineFile(arg);
	}

	private void logUnknownArg(String arg) {
		LOG.warning("Ignoring unknown command line argument ''{0}''", arg);
	}

	private static void applyLogConfig(String config) {
		try {
			Logs.readConfig(config);
		} catch (IOException e) {
			Exceptions.warn(e);
		}
	}

	private void shutdownCachedThreaddPool() throws InterruptedException {
		Optional<ExecutorService> optionalCachedThreadPool = this.cachedThreadPoolHolder.getOptional();

		if (optionalCachedThreadPool.isPresent()) {
			ExecutorService cachedThreadPool = optionalCachedThreadPool.get();

			cachedThreadPool.shutdown();

			boolean terminated = cachedThreadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);

			if (!terminated) {
				LOG.warning("Failed to terminate cached thread pool");
			}
		}
	}

}
