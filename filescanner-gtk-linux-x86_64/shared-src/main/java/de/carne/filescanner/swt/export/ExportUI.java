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

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.carne.boot.Exceptions;
import de.carne.boot.check.Nullable;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.FileScannerResultExporter;
import de.carne.filescanner.swt.resources.Images;
import de.carne.nio.file.FileUtil;
import de.carne.swt.graphics.ResourceException;
import de.carne.swt.graphics.ResourceTracker;
import de.carne.swt.layout.GridLayoutBuilder;
import de.carne.swt.layout.RowLayoutBuilder;
import de.carne.swt.platform.PlatformIntegration;
import de.carne.swt.util.Property;
import de.carne.swt.widgets.ButtonBuilder;
import de.carne.swt.widgets.CompositeBuilder;
import de.carne.swt.widgets.ControlBuilder;
import de.carne.swt.widgets.FileDialogBuilder;
import de.carne.swt.widgets.LabelBuilder;
import de.carne.swt.widgets.MessageBoxBuilder;
import de.carne.swt.widgets.ShellBuilder;
import de.carne.swt.widgets.ShellUserInterface;
import de.carne.util.Late;
import de.carne.util.prefs.PathPreference;
import de.carne.util.validation.InputValidator;
import de.carne.util.validation.PathValidator;
import de.carne.util.validation.StringValidator;
import de.carne.util.validation.ValidationException;

class ExportUI extends ShellUserInterface {

	private static final PathPreference PREF_EXPORT_DIR = new PathPreference("exportDir", FileUtil.workingDir());

	private final Preferences preferences = Preferences.userNodeForPackage(ExportUI.class);
	private final FileScannerResult result;
	private final ResourceTracker resources;
	private final Late<Combo> exportTypeHolder = new Late<>();
	private final Late<Text> exportPathTextHolder = new Late<>();
	private final Property<Integer> exporterTypeSelection = new Property<>(Integer.valueOf(0));
	@Nullable
	private ExportOptions exportOptions = null;

	ExportUI(Shell root, FileScannerResult result) {
		super(root);
		this.result = result;
		this.resources = ResourceTracker.forDevice(root.getDisplay()).forShell(root);
	}

	@Override
	public void open() throws ResourceException {
		ShellBuilder rootBuilder = buildRoot();

		rootBuilder.pack();
		rootBuilder.position(SWT.DEFAULT, SWT.DEFAULT);

		Shell root = rootBuilder.get();

		root.setMinimumSize(0, root.getSize().y);
		root.open();
	}

	@Nullable
	public ExportOptions getExportOptions() {
		return this.exportOptions;
	}

	private ShellBuilder buildRoot() {
		ShellBuilder rootBuilder = new ShellBuilder(root());
		LabelBuilder title = rootBuilder.addLabelChild(SWT.NONE);
		LabelBuilder separator1 = rootBuilder.addLabelChild(SWT.HORIZONTAL | SWT.SEPARATOR);
		LabelBuilder exportTypeLabel = rootBuilder.addLabelChild(SWT.NONE);
		ControlBuilder<Combo> exportType = rootBuilder.addControlChild(Combo.class, SWT.READ_ONLY);
		LabelBuilder exportPathLabel = rootBuilder.addLabelChild(SWT.NONE);
		ControlBuilder<Text> exportPathText = rootBuilder.addControlChild(Text.class, SWT.SINGLE | SWT.BORDER);
		ButtonBuilder exportPathButton = rootBuilder.addButtonChild(SWT.PUSH);
		LabelBuilder separator2 = rootBuilder.addLabelChild(SWT.HORIZONTAL | SWT.SEPARATOR);
		CompositeBuilder<Composite> buttons = rootBuilder.addCompositeChild(SWT.NONE);

		rootBuilder.withText(ExportI18N.i18nTitle()).withDefaultImages();
		title.withText(ExportI18N.i18nLabelExportResult(this.result.name()));
		exportTypeLabel.withText(ExportI18N.i18nLabelExportType());
		exportPathLabel.withText(ExportI18N.i18nLabelExportPath());
		exportPathButton.withImage(this.resources.getImage(Images.class, Images.IMAGE_OPEN_FILE16));
		exportPathButton.onSelected(this::onExportPathSelected);

		buildButtons(buttons);

		GridLayoutBuilder.layout(3).apply(rootBuilder);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).span(3, 1).apply(title);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).span(3, 1).apply(separator1);
		GridLayoutBuilder.data().apply(exportTypeLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).span(2, 1).apply(exportType);
		GridLayoutBuilder.data().apply(exportPathLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(exportPathText);
		GridLayoutBuilder.data().apply(exportPathButton);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).span(3, 1).apply(separator2);
		GridLayoutBuilder.data().align(SWT.END, SWT.TOP).grab(false, false).span(3, 1).apply(buttons);

		this.exportTypeHolder.set(exportType.get());
		this.exportPathTextHolder.set(exportPathText.get());
		this.exporterTypeSelection.addChangedListener(this::onExporterTypeSelectionChanged);
		initializeOptions();

		return rootBuilder;
	}

	private void initializeOptions() {
		Combo exportType = this.exportTypeHolder.get();
		FileScannerResultExporter[] exporters = this.result.exporters();

		for (FileScannerResultExporter exporter : exporters) {
			exportType.add(String.format("%1$s (%2$s)", exporter.name(), exporter.type().mimeType()));
		}
		this.exporterTypeSelection.set(Integer.valueOf(0), true);
	}

	private void buildButtons(CompositeBuilder<Composite> buttons) {
		ButtonBuilder cancelButton = buttons.addButtonChild(SWT.PUSH);
		ButtonBuilder exportButton = buttons.addButtonChild(SWT.PUSH);

		if (PlatformIntegration.isButtonOrderLeftToRight()) {
			exportButton.get().moveAbove(cancelButton.get());
		}
		cancelButton.withText(ExportI18N.i18nButtonCancel());
		cancelButton.onSelected(this::onCancelSelected);
		exportButton.withText(ExportI18N.i18nButtonExport());
		exportButton.onSelected(this::onExportSelected);
		RowLayoutBuilder.layout().fill(true).margin(0, 0, 0, 0).apply(buttons);
		RowLayoutBuilder.data().apply(cancelButton);
		RowLayoutBuilder.data().apply(exportButton);
	}

	private void onExportPathSelected() {
		try {
			FileDialog fileDialog = FileDialogBuilder.save(root()).withFileName(getCurrentExportPath())
					.withOverwrite(false).get();
			String exportPath = fileDialog.open();

			if (exportPath != null) {
				this.exportPathTextHolder.get().setText(exportPath);
			}
		} catch (Exception e) {
			unexpectedException(e);
		}
	}

	private void onCancelSelected() {
		root().close();
	}

	private void onExportSelected() {
		try {
			ExportOptions validatedExportOptions = validateAndGetExportOptions();

			if (!validatedExportOptions.overwrite() || confirmOverwrite(validatedExportOptions.path())) {
				Path exportPathParent = validatedExportOptions.path().getParent();

				if (exportPathParent != null) {
					PREF_EXPORT_DIR.put(this.preferences, exportPathParent);
					this.preferences.sync();
				}
				this.exportOptions = validatedExportOptions;
				root().close();
			}
		} catch (ValidationException e) {
			validationMessageBox(e);
		} catch (Exception e) {
			unexpectedException(e);
		}
	}

	private void onExporterTypeSelectionChanged(@Nullable Integer newValue,
			@SuppressWarnings("unused") @Nullable Integer oldValue) {
		if (newValue != null) {
			int exporterIndex = newValue.intValue();
			FileScannerResultExporter exporter = this.result.exporters()[exporterIndex];

			this.exportTypeHolder.get().select(exporterIndex);
			this.exportPathTextHolder.get().setText(buildExportPath(exporter.defaultFileName(this.result)));
		}
	}

	private String buildExportPath(String defaultFileName) {
		Path baseDir = PREF_EXPORT_DIR.get(this.preferences);
		Path exportPath = null;

		try {
			exportPath = baseDir.resolve(defaultFileName).normalize();
		} catch (InvalidPathException e) {
			Exceptions.ignore(e);
		}
		return (exportPath != null ? exportPath.toString() : "");
	}

	private String getCurrentExportPath() {
		Path currentExportPath = null;

		try {
			currentExportPath = Paths.get(this.exportPathTextHolder.get().getText().trim());
		} catch (InvalidPathException e) {
			Exceptions.ignore(e);
		}
		return (currentExportPath != null ? currentExportPath.toString() : "");
	}

	private ExportOptions validateAndGetExportOptions() throws ValidationException {
		int exporterIndex = InputValidator
				.checkNotNull(this.exporterTypeSelection.get(), ExportI18N::i18nMessageNoExporter).get().intValue();
		Path exportPath = StringValidator
				.checkNotEmpty(this.exportPathTextHolder.get().getText(), ExportI18N::i18nMessageNoExportPath)
				.convert(PathValidator::fromString, ExportI18N::i18nMessageInvalidExportPath).get();
		boolean overwrite = exportPath.toFile().exists();

		return new ExportOptions(this.result.exporters()[exporterIndex], exportPath, overwrite);
	}

	private boolean confirmOverwrite(Path exportPath) {
		MessageBox messageBox = MessageBoxBuilder.build(root(), SWT.ICON_QUESTION | SWT.YES | SWT.NO)
				.withText(root().getText()).withMessage(ExportI18N.i18nMessageOverwriteExportPath(exportPath)).get();

		return messageBox.open() == SWT.YES;
	}

	private void validationMessageBox(ValidationException validationException) {
		try {
			MessageBox messageBox = MessageBoxBuilder.error(root()).withText(root().getText())
					.withMessage(validationException.getMessage()).get();

			messageBox.open();
		} catch (Exception e) {
			unexpectedException(e);
		}
	}

}
