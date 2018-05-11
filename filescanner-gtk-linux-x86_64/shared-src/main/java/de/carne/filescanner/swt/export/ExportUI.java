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
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.carne.boot.check.Nullable;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.FileScannerResultExporter;
import de.carne.filescanner.swt.resources.Images;
import de.carne.swt.graphics.ResourceException;
import de.carne.swt.graphics.ResourceTracker;
import de.carne.swt.layout.GridLayoutBuilder;
import de.carne.swt.platform.PlatformIntegration;
import de.carne.swt.util.Property;
import de.carne.swt.widgets.CompositeBuilder;
import de.carne.swt.widgets.ControlBuilder;
import de.carne.swt.widgets.ShellBuilder;
import de.carne.swt.widgets.ShellUserInterface;
import de.carne.util.Late;

class ExportUI extends ShellUserInterface {

	private final FileScannerResult result;
	private final ResourceTracker resources;
	private final Late<Combo> exportTypeHolder = new Late<>();
	private final Late<Text> exportPathTextHolder = new Late<>();
	private final Property<Integer> exporterTypeSelection = new Property<>(Integer.valueOf(0));

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
		ControlBuilder<Text> exportPathText = rootBuilder.addControlChild(Text.class, SWT.SINGLE | SWT.BORDER);
		ControlBuilder<Button> exportPathButton = rootBuilder.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Label> separator = rootBuilder.addControlChild(Label.class, SWT.HORIZONTAL | SWT.SEPARATOR);
		CompositeBuilder<Composite> buttons = rootBuilder.addCompositeChild(SWT.NONE);

		rootBuilder.withText(ExportI18N.i18nTitle()).withDefaultImages();
		exportTypeLabel.get().setText(ExportI18N.i18nLabelExportType());
		exportPathLabel.get().setText(ExportI18N.i18nLabelExportPath());
		exportPathButton.get().setImage(this.resources.getImage(Images.class, Images.IMAGE_OPEN_FILE16));

		buildButtons(buttons);

		GridLayoutBuilder.layout(3).apply(rootBuilder);
		GridLayoutBuilder.data().apply(exportTypeLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).span(2, 1).apply(exportType);
		GridLayoutBuilder.data().apply(exportPathLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(exportPathText);
		GridLayoutBuilder.data().apply(exportPathButton);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).span(3, 1).apply(separator);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).span(3, 1).apply(buttons);

		this.exportTypeHolder.set(exportType.get());
		this.exportPathTextHolder.set(exportPathText.get());
		this.exporterTypeSelection.addChangedListener(this::onExporterTypeSelectionChanged);
		initializeOptions();

		return rootBuilder.get();
	}

	private void buildButtons(CompositeBuilder<Composite> buttons) {
		ControlBuilder<ProgressBar> exportProgress = buttons.addControlChild(ProgressBar.class,
				SWT.HORIZONTAL | SWT.SMOOTH);
		ControlBuilder<Button> cancelButton = buttons.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Button> exportButton = buttons.addControlChild(Button.class, SWT.PUSH);

		if (PlatformIntegration.isButtonOrderLeftToRight()) {
			exportButton.get().moveAbove(cancelButton.get());
		}
		cancelButton.get().setText(ExportI18N.i18nButtonCancel());
		cancelButton.onSelected(this::onCancelSelected);
		exportButton.get().setText(ExportI18N.i18nButtonExport());
		exportButton.onSelected(this::onExportSelected);
		GridLayoutBuilder.layout(3).apply(buttons);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(exportProgress);
		GridLayoutBuilder.data().apply(cancelButton);
		GridLayoutBuilder.data().apply(exportButton);
	}

	private void onCancelSelected() {
		root().close();
	}

	private void onExportSelected() {

	}

	private void onExporterTypeSelectionChanged(@Nullable Integer newValue,
			@SuppressWarnings("unused") @Nullable Integer oldValue) {
		if (newValue != null) {
			int exporterIndex = newValue.intValue();
			FileScannerResultExporter exporter = this.result.exporters()[exporterIndex];

			this.exportTypeHolder.get().select(exporterIndex);
			this.exportPathTextHolder.get().setText(determineExportPath(exporter.defaultStreamName(this.result)));
		}
	}

	private String determineExportPath(String defaultStreamName) {
		return defaultStreamName;
	}

	private void initializeOptions() {
		Combo exportType = this.exportTypeHolder.get();
		FileScannerResultExporter[] exporters = this.result.exporters();

		for (FileScannerResultExporter exporter : exporters) {
			exportType.add(String.format("%1$s (%2$s)", exporter.name(), exporter.type().mimeType()));
		}
		this.exporterTypeSelection.set(Integer.valueOf(0), true);
	}

}
