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
package de.carne.filescanner.swt.export;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

import de.carne.filescanner.engine.FileScannerResult;
import de.carne.swt.graphics.ResourceException;

/**
 * Dialog for file scanner result export setup.
 */
public class ExportDialog extends Dialog {

	/**
	 * Constructs a new {@linkplain ExportDialog} instance.
	 *
	 * @param parent the dialog parent to use.
	 */
	public ExportDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}

	/**
	 * Opens and runs the dialog.
	 *
	 * @param result the {@linkplain FileScannerResult} to export.
	 * @return the export options selected by the user or {@code null} if the dialog has been cancelled.
	 * @throws ResourceException if a required resource is not available.
	 */
	@Nullable
	public ExportOptions open(FileScannerResult result) throws ResourceException {
		ExportUI userInterface = new ExportUI(new Shell(getParent(), getStyle()), result);

		userInterface.open();
		userInterface.run();
		return userInterface.getExportOptions();
	}

}
