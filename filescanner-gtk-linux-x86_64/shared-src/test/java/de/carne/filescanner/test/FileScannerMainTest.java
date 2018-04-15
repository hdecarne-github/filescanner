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
package de.carne.filescanner.test;

import org.junit.jupiter.api.Test;

import de.carne.boot.Application;
import de.carne.filescanner.FileScannerMain;
import de.carne.test.swt.tester.SWTTester;

/**
 * Test {@link FileScannerMain} class.
 */
class FileScannerMainTest extends SWTTester {

	@Override
	protected void runSWTApplication(String[] args) {
		Application.main(args);
	}

	@Test
	void testFileScanner() {
		runner().check(this::checkMenuQuit).run();
	}

	private void checkMenuQuit() {
		getShell("FileScanner").menuBar().item("&Quit").select();
	}

}
