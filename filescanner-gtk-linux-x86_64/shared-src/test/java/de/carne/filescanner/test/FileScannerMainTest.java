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
package de.carne.filescanner.test;

import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.carne.filescanner.FileScannerMain;
import de.carne.test.swt.DisableIfThreadNotSWTCapable;
import de.carne.test.swt.tester.SWTTest;

/**
 * Test {@link FileScannerMain} class.
 */
@DisableIfThreadNotSWTCapable
class FileScannerMainTest extends SWTTest {

	@BeforeAll
	static void setLocale() {
		Locale.setDefault(Locale.US);
	}

	@Test
	void testFileScanner() {
		Script script = script(new FileScannerMain());

		script.add(this::doCloss);
		script.execute();
	}

	private void doCloss() {
		traceAction();

		accessShell().get().close();
	}

}
