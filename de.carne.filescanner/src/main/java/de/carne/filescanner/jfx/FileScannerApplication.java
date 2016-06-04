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
package de.carne.filescanner.jfx;

import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Map;

import de.carne.ApplicationLoader;
import de.carne.Main;
import de.carne.filescanner.core.transfer.HtmlResultRendererURLHandler;
import de.carne.filescanner.jfx.session.SessionController;
import de.carne.filescanner.util.Units;
import de.carne.jfx.StageController;
import de.carne.jfx.logview.LogImages;
import de.carne.jfx.messagebox.MessageBoxImages;
import de.carne.jfx.messagebox.MessageBoxStyle;
import de.carne.util.Exceptions;
import de.carne.util.logging.Log;
import de.carne.util.logging.LogConfig;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX application class responsible for starting up the GUI.
 */
public class FileScannerApplication extends Application implements Main {

	private static final Log LOG = new Log(FileScannerApplication.class);

	private static final String PARAMTER_PREFIX = "-";

	private static final String PARAMETER_VERBOSE = "--verbose";
	private static final String PARAMETER_DEBUG = "--debug";

	private static final String PARAMETER_DISABLE_INDEXING = "--disable-indexing";

	static {
		ApplicationLoader.registerURLStreamHandlerFactory(HtmlResultRendererURLHandler.PROTOCOL_RENDERER,
				HtmlResultRendererURLHandler.URL_STREAM_HANDLER_FACTORY);

		LogImages.registerImage(Log.LEVEL_NOTICE, Images.IMAGE_LOG_NOTICE16);
		LogImages.registerImage(Log.LEVEL_ERROR, Images.IMAGE_LOG_ERROR16);
		LogImages.registerImage(Log.LEVEL_WARNING, Images.IMAGE_LOG_WARNING16);
		LogImages.registerImage(Log.LEVEL_INFO, Images.IMAGE_LOG_INFO16);
		LogImages.registerImage(Log.LEVEL_DEBUG, Images.IMAGE_LOG_DEBUG16);

		MessageBoxImages.registerImage(MessageBoxStyle.ICON_INFO, Images.IMAGE_INFO16);
		MessageBoxImages.registerImage(MessageBoxStyle.ICON_INFO, Images.IMAGE_INFO32);
		MessageBoxImages.registerImage(MessageBoxStyle.ICON_WARNING, Images.IMAGE_WARNING16);
		MessageBoxImages.registerImage(MessageBoxStyle.ICON_WARNING, Images.IMAGE_WARNING32);
		MessageBoxImages.registerImage(MessageBoxStyle.ICON_ERROR, Images.IMAGE_ERROR16);
		MessageBoxImages.registerImage(MessageBoxStyle.ICON_ERROR, Images.IMAGE_ERROR32);
		MessageBoxImages.registerImage(MessageBoxStyle.ICON_QUESTION, Images.IMAGE_QUESTION16);
		MessageBoxImages.registerImage(MessageBoxStyle.ICON_QUESTION, Images.IMAGE_QUESTION32);
	}

	void handleUncaughtException(Thread t, Throwable e, UncaughtExceptionHandler next) {
		LOG.error(e, I18N.BUNDLE, I18N.STR_UNEXPECTED_EXCEPTION_MESSAGE, Exceptions.toMessage(e));
		if (next != null) {
			next.uncaughtException(t, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.Main#run(java.lang.String[])
	 */
	@Override
	public int run(String[] args) {
		Application.launch(getClass(), args);
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage stage) throws Exception {
		Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			private UncaughtExceptionHandler next = Thread.currentThread().getUncaughtExceptionHandler();

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				handleUncaughtException(t, e, this.next);
			}

		});
		LOG.debug(null, "Starting JavaFX GUI...");

		SessionController session = StageController.setupPrimaryStage(stage, SessionController.class);

		session.getStage().show();
		logVMInfo();
		logLoaderInfo();
		processParameters(session);
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		LOG.debug(null, "JavaFX GUI stopped");
	}

	private void processParameters(SessionController session) {
		Parameters parameters = getParameters();

		for (Map.Entry<String, String> parameter : parameters.getNamed().entrySet()) {
			LOG.warning(I18N.BUNDLE, I18N.STR_INVALID_PARAMETER_MESSAGE,
					parameter.getKey() + "=" + parameter.getValue());
		}

		String openFile = null;

		for (String parameter : parameters.getUnnamed()) {
			if (PARAMETER_VERBOSE.equals(parameter)) {
				LogConfig.applyConfig(LogConfig.CONFIG_VERBOSE);
				LOG.notice(I18N.BUNDLE, I18N.STR_VERBOSE_ENABLED_MESSAGE);
			} else if (PARAMETER_DEBUG.equals(parameter)) {
				LogConfig.applyConfig(LogConfig.CONFIG_DEBUG);
				LOG.notice(I18N.BUNDLE, I18N.STR_DEBUG_ENABLED_MESSAGE);
			} else if (PARAMETER_DISABLE_INDEXING.equals(parameter)) {
				session.disableIndexing();
				LOG.notice(I18N.BUNDLE, I18N.STR_INDEXING_DISABLED_MESSAGE);
			} else if (openFile == null && !parameter.startsWith(PARAMTER_PREFIX)) {
				openFile = parameter;
			} else {
				LOG.warning(I18N.BUNDLE, I18N.STR_INVALID_PARAMETER_MESSAGE, parameter);
			}
		}
		if (openFile != null) {
			session.openFile(openFile);
		}
	}

	private void logVMInfo() {
		String vmVersion = System.getProperty("java.version");
		String vmVendor = System.getProperty("java.vendor");

		LOG.notice(I18N.BUNDLE, I18N.STR_VM_VERSION_INFO, vmVersion, vmVendor);

		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();

		LOG.notice(I18N.BUNDLE, I18N.STR_VM_HEAP_INFO, Units.formatByteValue(maxMemory));
	}

	private void logLoaderInfo() {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();

		if (classloader instanceof URLClassLoader) {
			URL[] urls = ((URLClassLoader) classloader).getURLs();

			LOG.debug(null, "Using ClassLoader {0} with URL path {1}", classloader.getClass().getName(),
					Arrays.toString(urls));
		}
	}

}
