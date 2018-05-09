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
package de.carne.filescanner.swt.export;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.carne.filescanner.engine.FileScannerResult;
import de.carne.swt.graphics.ResourceException;
import de.carne.swt.graphics.ResourceTracker;
import de.carne.swt.layout.FillLayoutBuilder;
import de.carne.swt.layout.GridLayoutBuilder;
import de.carne.swt.layout.RowLayoutBuilder;
import de.carne.swt.platform.PlatformIntegration;
import de.carne.swt.widgets.CompositeBuilder;
import de.carne.swt.widgets.ControlBuilder;
import de.carne.swt.widgets.ShellBuilder;
import de.carne.swt.widgets.ShellUserInterface;

class ExportUI extends ShellUserInterface {

	private final FileScannerResult result;
	private final ResourceTracker resources;

	ExportUI(Shell root, FileScannerResult result) {
		super(root);
		this.result = result;
		this.resources = ResourceTracker.forDevice(root.getDisplay()).forShell(root);
	}

	@Override
	public void open() throws ResourceException {
		Shell root = buildRoot();

		root.pack();
		root.setMinimumSize(root.getSize());
		root.open();
	}

	private Shell buildRoot() {
		ShellBuilder rootBuilder = new ShellBuilder(root());
		ControlBuilder<Label> exportTypeLabel = rootBuilder.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Combo> exportType = rootBuilder.addControlChild(Combo.class, SWT.READ_ONLY);
		ControlBuilder<Label> exportPathLabel = rootBuilder.addControlChild(Label.class, SWT.NONE);
		CompositeBuilder<Composite> exportPath = rootBuilder.addCompositeChild(SWT.NO_BACKGROUND);
		ControlBuilder<Text> exportPathText = exportPath.addControlChild(Text.class, SWT.SINGLE);
		ControlBuilder<Button> exportPathButton = exportPath.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Label> separator = rootBuilder.addControlChild(Label.class, SWT.HORIZONTAL | SWT.SEPARATOR);
		CompositeBuilder<Composite> buttons = rootBuilder.addCompositeChild(SWT.NO_BACKGROUND);

		rootBuilder.withText(ExportI18N.i18nTitle()).withDefaultImages();
		exportTypeLabel.get().setText(ExportI18N.i18nLabelExportType());
		exportPathLabel.get().setText(ExportI18N.i18nLabelExportPath());

		buildButtons(buttons);

		FillLayoutBuilder.layout().apply(exportPath);

		GridLayoutBuilder.layout(2).apply(rootBuilder);
		GridLayoutBuilder.data().apply(exportTypeLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(exportType);
		GridLayoutBuilder.data().apply(exportPathLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(exportPath);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).span(2, 1).apply(separator);
		GridLayoutBuilder.data().align(SWT.END, SWT.CENTER).grab(false, false).span(2, 1).apply(buttons);
		return rootBuilder.get();
	}

	private void buildButtons(CompositeBuilder<Composite> buttons) {
		ControlBuilder<Button> cancelButton = buttons.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Button> exportButton = buttons.addControlChild(Button.class, SWT.PUSH);

		if (PlatformIntegration.isButtonOrderLeftToRight()) {
			exportButton.get().moveAbove(null);
		}
		cancelButton.get().setText(ExportI18N.i18nButtonCancel());
		cancelButton.onSelected(this::onCancelSelected);
		exportButton.get().setText(ExportI18N.i18nButtonExport());
		exportButton.onSelected(this::onExportSelected);
		RowLayoutBuilder.layout().fill(true).apply(buttons);
		RowLayoutBuilder.data().apply(cancelButton);
		RowLayoutBuilder.data().apply(exportButton);
	}

	private void onCancelSelected() {
		root().close();
	}

	private void onExportSelected() {

	}

}
