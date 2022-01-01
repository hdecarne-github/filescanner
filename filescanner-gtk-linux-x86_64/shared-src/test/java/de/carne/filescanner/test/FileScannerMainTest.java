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
package de.carne.filescanner.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.carne.boot.Application;
import de.carne.filescanner.swt.FileScannerMain;
import de.carne.swt.platform.PlatformIntegration;
import de.carne.test.swt.DisableIfThreadNotSWTCapable;
import de.carne.test.swt.tester.SWTTest;
import de.carne.test.swt.tester.accessor.ButtonAccessor;
import de.carne.test.swt.tester.accessor.ControlAccessor;
import de.carne.test.swt.tester.accessor.ItemAccessor;
import de.carne.test.swt.tester.accessor.ShellAccessor;

/**
 * Test {@link FileScannerMain} class.
 */
@DisableIfThreadNotSWTCapable
class FileScannerMainTest extends SWTTest {

	private static final long DEFAULT_TIMEOUT = 180 * 1000l;
	private static final long LONG_TIMEOUT = 300 * 1000l;

	@BeforeAll
	static void setLocale() {
		Locale.setDefault(Locale.US);
	}

	@Test
	void testFileScanner() {
		Script script = script(Application::run).args("--debug");

		script.add(this::doOpenFile);
		script.add(this::waitScanFinished, this::doVerifyScanResult, LONG_TIMEOUT);
		script.add(this::doOpenExport, true);
		script.add(() -> accessShell("Export scan result"), this::doVerifyExport, DEFAULT_TIMEOUT);
		script.add(this::doClose);
		script.execute();
	}

	private void doOpenFile() {
		traceAction();

		Path file = Paths.get("./build/libs",
				"filescanner-" + PlatformIntegration.toolkitName() + "-" + getFileScannerVersion() + ".jar");

		mockFileDialog().offerResult(file.toString());

		accessShell().accessMenuBar().accessItem(ItemAccessor.matchText("&Open\u2026")).select();
	}

	private ControlAccessor<ProgressBar> waitScanFinished() {
		ControlAccessor<ProgressBar> progressBarAccessor = accessShell().accessChild(ControlAccessor<ProgressBar>::new,
				ProgressBar.class, control -> true);
		Optional<? extends ProgressBar> optionalProgressBar = progressBarAccessor.getOptional();

		return (optionalProgressBar.isPresent() && isProgressBarMaxedOut(optionalProgressBar.get())
				? progressBarAccessor
				: new ControlAccessor<>(Optional.empty()));
	}

	private boolean isProgressBarMaxedOut(ProgressBar progressBar) {
		return progressBar.getSelection() >= progressBar.getMaximum();
	}

	private static final String ZIP_ARCHIVE_RESULT_NAME = "ZIP archive";

	private void doVerifyScanResult(@SuppressWarnings("unused") ControlAccessor<ProgressBar> progressBar) {
		Tree resultView = accessShell().accessChild(ControlAccessor<Tree>::new, Tree.class, control -> true).get();

		TreeItem rootItem = resultView.getItem(0);

		Assertions.assertNotNull(rootItem);
		Assertions.assertTrue(rootItem.getExpanded());
		Assertions.assertEquals(1, rootItem.getItemCount());

		TreeItem zipArchiveItem = rootItem.getItem(0);

		Assertions.assertEquals(ZIP_ARCHIVE_RESULT_NAME, zipArchiveItem.getText());

		resultView.setSelection(zipArchiveItem);
	}

	private void doOpenExport() {
		traceAction();

		accessShell().accessMenuBar().accessItem(ItemAccessor.matchText("&Export\u2026")).select();
	}

	private void doVerifyExport(ShellAccessor exportDialog) {
		exportDialog.accessButton(ButtonAccessor.matchText("Cancel")).select();
	}

	private void doClose() {
		traceAction();

		accessShell().get().close();
	}

	private String getFileScannerVersion() {
		Properties properties = new Properties();

		try (InputStream propertiesStream = new FileInputStream("../gradle.properties")) {
			properties.load(propertiesStream);
		} catch (IOException e) {
			Assertions.fail(e);
		}
		return Objects.requireNonNull(properties.getProperty("version"));
	}

}
