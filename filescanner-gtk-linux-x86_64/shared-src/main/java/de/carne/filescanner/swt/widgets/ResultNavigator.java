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
package de.carne.filescanner.swt.widgets;

import de.carne.filescanner.engine.FileScannerResult;

/**
 * Callback interface used by {@linkplain ResultView} widget to support navigation between result objects.
 */
@FunctionalInterface
public interface ResultNavigator {

	/**
	 * Navigates to the given position relative to the given result object.
	 *
	 * @param from the {@linkplain FileScannerResult} to navigate from.
	 * @param position the position to navigate to.
	 * @return the {@linkplain FileScannerResult} matching the submitted position.
	 */
	FileScannerResult navigateTo(FileScannerResult from, long position);

}
