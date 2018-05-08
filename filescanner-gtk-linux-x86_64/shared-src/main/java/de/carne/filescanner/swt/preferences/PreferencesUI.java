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
package de.carne.filescanner.swt.preferences;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.BackingStoreException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import de.carne.filescanner.engine.spi.Format;
import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.swt.graphics.ResourceException;
import de.carne.swt.graphics.ResourceTracker;
import de.carne.swt.layout.GridLayoutBuilder;
import de.carne.swt.layout.RowLayoutBuilder;
import de.carne.swt.platform.PlatformIntegration;
import de.carne.swt.widgets.ColorDialogBuilder;
import de.carne.swt.widgets.CompositeBuilder;
import de.carne.swt.widgets.ControlBuilder;
import de.carne.swt.widgets.FontDialogBuilder;
import de.carne.swt.widgets.ShellBuilder;
import de.carne.swt.widgets.ShellUserInterface;
import de.carne.util.Late;

class PreferencesUI extends ShellUserInterface {

	private final ResourceTracker resources;
	private Late<Button> inputViewFontButtonHolder = new Late<>();
	private Late<Button> resultViewFontButtonHolder = new Late<>();
	private Late<Button> resultViewColorNormalButtonHolder = new Late<>();
	private Late<Button> resultViewColorValueButtonHolder = new Late<>();
	private Late<Button> resultViewColorCommentButtonHolder = new Late<>();
	private Late<Button> resultViewColorKeywordButtonHolder = new Late<>();
	private Late<Button> resultViewColorOperatorButtonHolder = new Late<>();
	private Late<Button> resultViewColorLabelButtonHolder = new Late<>();
	private Late<Button> resultViewColorErrorButtonHolder = new Late<>();
	private Late<Table> formatsTableHolder = new Late<>();

	PreferencesUI(Shell root) {
		super(root);
		this.resources = ResourceTracker.forDevice(root.getDisplay()).forShell(root);
	}

	@Override
	public void open() throws ResourceException {
		Shell root = buildRoot();

		loadPreferences(UserPreferences.get());
		root.pack();
		root.setMinimumSize(root.getSize());
		root.open();
	}

	private Shell buildRoot() {
		ShellBuilder rootBuilder = new ShellBuilder(root());
		CompositeBuilder<TabFolder> prefTabs = rootBuilder.addCompositeChild(TabFolder.class, SWT.TOP);
		ControlBuilder<Label> separator = rootBuilder.addControlChild(Label.class, SWT.HORIZONTAL | SWT.SEPARATOR);
		CompositeBuilder<Composite> buttons = rootBuilder.addCompositeChild(SWT.NO_BACKGROUND);

		rootBuilder.withText(PreferencesI18N.i18nTitle()).withDefaultImages();
		buildPrefTabs(prefTabs);
		buildButtons(buttons);

		GridLayoutBuilder.layout().spacing(0, 0).margin(0, 0).apply(rootBuilder);
		GridLayoutBuilder.data(GridData.FILL_BOTH).apply(prefTabs);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(separator);
		GridLayoutBuilder.data().align(SWT.END, SWT.CENTER).grab(false, false).apply(buttons);
		return rootBuilder.get();
	}

	private void buildPrefTabs(CompositeBuilder<TabFolder> prefTabs) {
		TabFolder prefTabsFolder = prefTabs.get();
		TabItem appearanceTab = new TabItem(prefTabsFolder, SWT.NONE);
		TabItem formatsTab = new TabItem(prefTabsFolder, SWT.NONE);

		appearanceTab.setText(PreferencesI18N.i18nTabAppearance());
		buildAppearancePrefTab(prefTabs, appearanceTab);
		formatsTab.setText(PreferencesI18N.i18nTabFormats());
		buildFormatsPrefTab(prefTabs, formatsTab);
	}

	private void buildAppearancePrefTab(CompositeBuilder<TabFolder> prefTabs, TabItem prefTab) {
		CompositeBuilder<Composite> appearance = prefTabs.addCompositeChild(SWT.NO_BACKGROUND);
		ControlBuilder<Label> inputViewLabel = appearance.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Label> inputViewFontLabel = appearance.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Button> inputViewFontButton = appearance.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Label> resultViewLabel = appearance.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Label> resultViewFontLabel = appearance.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Button> resultViewFontButton = appearance.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Label> resultViewColorNormalLabel = appearance.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Button> resultViewColorNormalButton = appearance.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Label> resultViewColorValueLabel = appearance.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Button> resultViewColorValueButton = appearance.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Label> resultViewColorCommentLabel = appearance.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Button> resultViewColorCommentButton = appearance.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Label> resultViewColorKeywordLabel = appearance.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Button> resultViewColorKeywordButton = appearance.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Label> resultViewColorOperatorLabel = appearance.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Button> resultViewColorOperatorButton = appearance.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Label> resultViewColorLabelLabel = appearance.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Button> resultViewColorLabelButton = appearance.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Label> resultViewColorErrorLabel = appearance.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Button> resultViewColorErrorButton = appearance.addControlChild(Button.class, SWT.PUSH);

		inputViewLabel.get().setText(PreferencesI18N.i18nLabelInputView());
		inputViewFontLabel.get().setText(PreferencesI18N.i18nLabelInputViewFont());
		inputViewFontButton.get().setText(PreferencesI18N.i18nButtonInputViewFont());
		inputViewFontButton.onSelected(this::onChooseInputViewFont);
		resultViewLabel.get().setText(PreferencesI18N.i18nLabelResultView());
		resultViewFontLabel.get().setText(PreferencesI18N.i18nLabelResultViewFont());
		resultViewFontButton.get().setText(PreferencesI18N.i18nButtonResultViewFont());
		resultViewFontButton.onSelected(this::onChooseResultViewFont);
		resultViewColorNormalLabel.get().setText(PreferencesI18N.i18nLabelResultViewColorNormal());
		resultViewColorNormalButton.get().setText(PreferencesI18N.i18nButtonResultViewColorNormal());
		resultViewColorNormalButton.onSelected(() -> onChooseResultViewColor(this.resultViewColorNormalButtonHolder));
		resultViewColorValueLabel.get().setText(PreferencesI18N.i18nLabelResultViewColorValue());
		resultViewColorValueButton.get().setText(PreferencesI18N.i18nButtonResultViewColorValue());
		resultViewColorValueButton.onSelected(() -> onChooseResultViewColor(this.resultViewColorValueButtonHolder));
		resultViewColorCommentLabel.get().setText(PreferencesI18N.i18nLabelResultViewColorComment());
		resultViewColorCommentButton.get().setText(PreferencesI18N.i18nButtonResultViewColorComment());
		resultViewColorCommentButton.onSelected(() -> onChooseResultViewColor(this.resultViewColorCommentButtonHolder));
		resultViewColorKeywordLabel.get().setText(PreferencesI18N.i18nLabelResultViewColorKeyword());
		resultViewColorKeywordButton.get().setText(PreferencesI18N.i18nButtonResultViewColorKeyword());
		resultViewColorKeywordButton.onSelected(() -> onChooseResultViewColor(this.resultViewColorKeywordButtonHolder));
		resultViewColorOperatorLabel.get().setText(PreferencesI18N.i18nLabelResultViewColorOperator());
		resultViewColorOperatorButton.get().setText(PreferencesI18N.i18nButtonResultViewColorOperator());
		resultViewColorOperatorButton
				.onSelected(() -> onChooseResultViewColor(this.resultViewColorOperatorButtonHolder));
		resultViewColorLabelLabel.get().setText(PreferencesI18N.i18nLabelResultViewColorLabel());
		resultViewColorLabelButton.get().setText(PreferencesI18N.i18nButtonResultViewColorLabel());
		resultViewColorLabelButton.onSelected(() -> onChooseResultViewColor(this.resultViewColorLabelButtonHolder));
		resultViewColorErrorLabel.get().setText(PreferencesI18N.i18nLabelResultViewColorError());
		resultViewColorErrorButton.get().setText(PreferencesI18N.i18nButtonResultViewColorError());
		resultViewColorErrorButton.onSelected(() -> onChooseResultViewColor(this.resultViewColorErrorButtonHolder));

		GridLayoutBuilder.layout(2).spacing(5, 0).apply(appearance);
		GridLayoutBuilder.data().span(2, 1).apply(inputViewLabel);
		GridLayoutBuilder.data().indent(5, 0).apply(inputViewFontLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(inputViewFontButton);
		GridLayoutBuilder.data().span(2, 1).indent(0, 5).apply(resultViewLabel);
		GridLayoutBuilder.data().indent(5, 0).apply(resultViewFontLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(resultViewFontButton);
		GridLayoutBuilder.data().indent(5, 0).apply(resultViewColorNormalLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(resultViewColorNormalButton);
		GridLayoutBuilder.data().indent(5, 0).apply(resultViewColorValueLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(resultViewColorValueButton);
		GridLayoutBuilder.data().indent(5, 0).apply(resultViewColorCommentLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(resultViewColorCommentButton);
		GridLayoutBuilder.data().indent(5, 0).apply(resultViewColorKeywordLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(resultViewColorKeywordButton);
		GridLayoutBuilder.data().indent(5, 0).apply(resultViewColorOperatorLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(resultViewColorOperatorButton);
		GridLayoutBuilder.data().indent(5, 0).apply(resultViewColorLabelLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(resultViewColorLabelButton);
		GridLayoutBuilder.data().indent(5, 0).apply(resultViewColorErrorLabel);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(resultViewColorErrorButton);
		prefTab.setControl(appearance.get());

		this.inputViewFontButtonHolder.set(inputViewFontButton.get());
		this.resultViewFontButtonHolder.set(resultViewFontButton.get());
		this.resultViewColorNormalButtonHolder.set(resultViewColorNormalButton.get());
		this.resultViewColorValueButtonHolder.set(resultViewColorValueButton.get());
		this.resultViewColorCommentButtonHolder.set(resultViewColorCommentButton.get());
		this.resultViewColorKeywordButtonHolder.set(resultViewColorKeywordButton.get());
		this.resultViewColorOperatorButtonHolder.set(resultViewColorOperatorButton.get());
		this.resultViewColorLabelButtonHolder.set(resultViewColorLabelButton.get());
		this.resultViewColorErrorButtonHolder.set(resultViewColorErrorButton.get());
	}

	private void buildFormatsPrefTab(CompositeBuilder<TabFolder> prefTabs, TabItem prefTab) {
		CompositeBuilder<Composite> formats = prefTabs.addCompositeChild(SWT.NO_BACKGROUND);
		ControlBuilder<Label> enabledFormatsLabel = formats.addControlChild(Label.class, SWT.NONE);
		ControlBuilder<Table> formatsTable = formats.addControlChild(Table.class,
				SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		enabledFormatsLabel.get().setText(PreferencesI18N.i18nLabelEnabledFormats());

		GridLayoutBuilder.layout().apply(formats);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).apply(enabledFormatsLabel);
		GridLayoutBuilder.data(GridData.FILL_BOTH).apply(formatsTable);
		prefTab.setControl(formats.get());

		this.formatsTableHolder.set(formatsTable.get());
	}

	private void buildButtons(CompositeBuilder<Composite> buttons) {
		ControlBuilder<Button> cancelButton = buttons.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Button> applyButton = buttons.addControlChild(Button.class, SWT.PUSH);
		ControlBuilder<Button> applyAndCloseButton = buttons.addControlChild(Button.class, SWT.PUSH);

		if (PlatformIntegration.isButtonOrderLeftToRight()) {
			applyButton.get().moveAbove(null);
			applyAndCloseButton.get().moveAbove(null);
		}
		cancelButton.get().setText(PreferencesI18N.i18nButtonCancel());
		cancelButton.onSelected(this::onCancelSelected);
		applyButton.get().setText(PreferencesI18N.i18nButtonApply());
		applyButton.onSelected(this::onApplySelected);
		applyAndCloseButton.get().setText(PreferencesI18N.i18nButtonApplyAndClose());
		applyAndCloseButton.onSelected(this::onApplyAndCloseSelected);
		RowLayoutBuilder.layout().fill(true).apply(buttons);
		RowLayoutBuilder.data().apply(cancelButton);
		RowLayoutBuilder.data().apply(applyButton);
		RowLayoutBuilder.data().apply(applyAndCloseButton);
	}

	private void onChooseInputViewFont() {
		FontData[] oldFontData = this.inputViewFontButtonHolder.get().getFont().getFontData();
		FontDialog fontDialog = FontDialogBuilder.choose(root()).withFontList(oldFontData).get();
		FontData newFontData = fontDialog.open();

		if (newFontData != null) {
			Font font = this.resources.getFont(newFontData);

			this.inputViewFontButtonHolder.get().setFont(font);
		}
		root().pack();
	}

	private void onChooseResultViewFont() {
		FontData[] oldFontData = this.resultViewFontButtonHolder.get().getFont().getFontData();
		FontDialog fontDialog = FontDialogBuilder.choose(root()).withFontList(oldFontData).get();
		FontData newFontData = fontDialog.open();

		if (newFontData != null) {
			Font font = this.resources.getFont(newFontData);

			this.resultViewFontButtonHolder.get().setFont(font);
			this.resultViewColorNormalButtonHolder.get().setFont(font);
			this.resultViewColorValueButtonHolder.get().setFont(font);
			this.resultViewColorCommentButtonHolder.get().setFont(font);
			this.resultViewColorKeywordButtonHolder.get().setFont(font);
			this.resultViewColorOperatorButtonHolder.get().setFont(font);
			this.resultViewColorLabelButtonHolder.get().setFont(font);
			this.resultViewColorErrorButtonHolder.get().setFont(font);
		}
		root().pack();
	}

	private void onChooseResultViewColor(Late<Button> resultViewColorButtonHolder) {
		RGB oldRgb = resultViewColorButtonHolder.get().getForeground().getRGB();
		ColorDialog colorDialog = ColorDialogBuilder.choose(root()).withRgb(oldRgb).get();
		RGB newRgb = colorDialog.open();

		if (newRgb != null) {
			Color color = this.resources.getColor(newRgb);

			resultViewColorButtonHolder.get().setForeground(color);
		}
	}

	private void onCancelSelected() {
		root().close();
	}

	private void onApplySelected() {
		try {
			storePreferences(UserPreferences.get());
		} catch (BackingStoreException e) {
			unexpectedException(e);
		}
	}

	private void onApplyAndCloseSelected() {
		try {
			storePreferences(UserPreferences.get());
			root().close();
		} catch (BackingStoreException e) {
			unexpectedException(e);
		}
	}

	private void loadPreferences(UserPreferences preferences) {
		Font inputViewFont = this.resources.getFont(preferences.getInputViewFont());
		Font resultViewFont = this.resources.getFont(preferences.getResultViewFont());
		Color normalColor = this.resources.getColor(preferences.getResultViewColor(RenderStyle.NORMAL));
		Color valueColor = this.resources.getColor(preferences.getResultViewColor(RenderStyle.VALUE));
		Color commentColor = this.resources.getColor(preferences.getResultViewColor(RenderStyle.COMMENT));
		Color keywordColor = this.resources.getColor(preferences.getResultViewColor(RenderStyle.KEYWORD));
		Color operatorColor = this.resources.getColor(preferences.getResultViewColor(RenderStyle.OPERATOR));
		Color labelColor = this.resources.getColor(preferences.getResultViewColor(RenderStyle.LABEL));
		Color errorColor = this.resources.getColor(preferences.getResultViewColor(RenderStyle.ERROR));

		this.inputViewFontButtonHolder.get().setFont(inputViewFont);
		this.resultViewFontButtonHolder.get().setFont(resultViewFont);
		this.resultViewColorNormalButtonHolder.get().setFont(resultViewFont);
		this.resultViewColorNormalButtonHolder.get().setForeground(normalColor);
		this.resultViewColorValueButtonHolder.get().setFont(resultViewFont);
		this.resultViewColorValueButtonHolder.get().setForeground(valueColor);
		this.resultViewColorCommentButtonHolder.get().setFont(resultViewFont);
		this.resultViewColorCommentButtonHolder.get().setForeground(commentColor);
		this.resultViewColorKeywordButtonHolder.get().setFont(resultViewFont);
		this.resultViewColorKeywordButtonHolder.get().setForeground(keywordColor);
		this.resultViewColorOperatorButtonHolder.get().setFont(resultViewFont);
		this.resultViewColorOperatorButtonHolder.get().setForeground(operatorColor);
		this.resultViewColorLabelButtonHolder.get().setFont(resultViewFont);
		this.resultViewColorLabelButtonHolder.get().setForeground(labelColor);
		this.resultViewColorErrorButtonHolder.get().setFont(resultViewFont);
		this.resultViewColorErrorButtonHolder.get().setForeground(errorColor);

		Set<String> disabledFormats = preferences.getDisabledFormats();
		SortedSet<String> formatNames = new TreeSet<>();

		Format.providers().forEach(format -> formatNames.add(format.name()));
		for (String formatName : formatNames) {
			TableItem formatItem = new TableItem(this.formatsTableHolder.get(), SWT.NONE);

			formatItem.setText(formatName);
			formatItem.setChecked(!disabledFormats.contains(formatName));
		}
	}

	private void storePreferences(UserPreferences preferences) throws BackingStoreException {
		preferences.setInputViewFont(this.inputViewFontButtonHolder.get().getFont().getFontData()[0]);
		preferences.setResultViewFont(this.resultViewFontButtonHolder.get().getFont().getFontData()[0]);
		preferences.setResultViewColor(RenderStyle.NORMAL,
				this.resultViewColorNormalButtonHolder.get().getForeground().getRGB());
		preferences.setResultViewColor(RenderStyle.VALUE,
				this.resultViewColorValueButtonHolder.get().getForeground().getRGB());
		preferences.setResultViewColor(RenderStyle.COMMENT,
				this.resultViewColorCommentButtonHolder.get().getForeground().getRGB());
		preferences.setResultViewColor(RenderStyle.KEYWORD,
				this.resultViewColorKeywordButtonHolder.get().getForeground().getRGB());
		preferences.setResultViewColor(RenderStyle.OPERATOR,
				this.resultViewColorOperatorButtonHolder.get().getForeground().getRGB());
		preferences.setResultViewColor(RenderStyle.LABEL,
				this.resultViewColorLabelButtonHolder.get().getForeground().getRGB());
		preferences.setResultViewColor(RenderStyle.ERROR,
				this.resultViewColorErrorButtonHolder.get().getForeground().getRGB());

		Set<String> disabledFormats = new HashSet<>();

		for (TableItem formatItem : this.formatsTableHolder.get().getItems()) {
			if (!formatItem.getChecked()) {
				disabledFormats.add(formatItem.getText());
			}
		}
		preferences.setDisabledFormats(disabledFormats);
		preferences.store();
	}

}
