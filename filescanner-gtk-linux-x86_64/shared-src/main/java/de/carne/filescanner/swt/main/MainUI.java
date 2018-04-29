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
package de.carne.filescanner.swt.main;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.carne.boot.Exceptions;
import de.carne.boot.logging.Log;
import de.carne.filescanner.engine.FileScannerProgress;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.swt.preferences.Config;
import de.carne.filescanner.swt.preferences.PreferencesDialog;
import de.carne.filescanner.swt.preferences.UserPreferences;
import de.carne.filescanner.swt.resources.Images;
import de.carne.filescanner.swt.widgets.Hex;
import de.carne.nio.compression.Check;
import de.carne.swt.graphics.ResourceException;
import de.carne.swt.graphics.ResourceTracker;
import de.carne.swt.layout.FormLayoutBuilder;
import de.carne.swt.layout.GridLayoutBuilder;
import de.carne.swt.platform.PlatformIntegration;
import de.carne.swt.widgets.CompositeBuilder;
import de.carne.swt.widgets.ControlBuilder;
import de.carne.swt.widgets.CoolBarBuilder;
import de.carne.swt.widgets.FileDialogBuilder;
import de.carne.swt.widgets.MenuBuilder;
import de.carne.swt.widgets.ShellBuilder;
import de.carne.swt.widgets.ToolBarBuilder;
import de.carne.swt.widgets.UserInterface;
import de.carne.text.MemoryUnitFormat;
import de.carne.util.Late;

/**
 * Main window UI.
 */
public class MainUI extends UserInterface<Shell> {

	private static final Log LOG = new Log();

	private final ResourceTracker resources;
	private final Late<HtmlRenderService> resultRenderServiceHolder = new Late<>();
	private final Late<MainController> controllerHolder = new Late<>();
	private final Late<Text> searchPatternHolder = new Late<>();
	private final Late<Tree> resultTreeHolder = new Late<>();
	private final Late<Browser> resultViewHolder = new Late<>();
	private final Late<Hex> inputViewHolder = new Late<>();
	private final Late<ToolItem> cancelButtonHolder = new Late<>();
	private final Late<ProgressBar> sessionProgressHolder = new Late<>();
	private final Late<Label> sessionStatusHolder = new Late<>();
	private final Late<Sash> vSashHolder = new Late<>();
	private final Late<Sash> hSashHolder = new Late<>();
	private final Consumer<Config> configConsumer = this::applyConfig;

	/**
	 * Constructs a new {@linkplain MainUI} instance.
	 *
	 * @param root the root {@linkplain Shell}.
	 */
	public MainUI(Shell root) {
		super(root);
		this.resources = ResourceTracker.forDevice(root.getDisplay());
	}

	/**
	 * Opens the given command line file for scanning.
	 *
	 * @param file the file to scan.
	 */
	public void openCommandLineFile(String file) {
		openFile(file);
	}

	/**
	 * Opens the given file for scanning.
	 *
	 * @param file the file to scan.
	 */
	public void openFile(String file) {
		LOG.info("Opening file ''{0}''...", file);

		try {
			FileScannerResult rootResult = this.controllerHolder.get().openFile(file);

			setRootResultTreeItem(rootResult);
			this.inputViewHolder.get().setResult(rootResult);
		} catch (Exception e) {
			Exceptions.warn(e);
		}
	}

	/**
	 * Sends close request to the UI.
	 */
	public void close() {
		root().close();
	}

	void resetSession(boolean session) {
		this.resultRenderServiceHolder.get().clear();
		this.searchPatternHolder.get().setEnabled(session);
		this.resultTreeHolder.get().removeAll();
		this.resultViewHolder.get().setText(MainI18N.i18nTextDefaultResultViewHtml());
		this.cancelButtonHolder.get().setEnabled(session);
		this.sessionProgressHolder.get().setSelection(0);
		this.sessionStatusHolder.get().setText("");
	}

	void sessionRunning(boolean running) {
		this.cancelButtonHolder.get().setEnabled(running);
	}

	void sessionProgress(FileScannerProgress progress) {
		this.sessionProgressHolder.get().setSelection(progress.scanProgress());

		MemoryUnitFormat memoryUnitFormat = MemoryUnitFormat.getMemoryUnitInstance();
		String statusScanned = memoryUnitFormat.format(progress.scannedBytes());
		long scanRate = progress.scanRate();
		String statusRate = (scanRate >= 0 ? memoryUnitFormat.format(progress.scanRate()) : "\u221e");
		int[] elapsedValues = elapsedValues(progress.scanTimeNanos());

		this.sessionStatusHolder.get().setText(MainI18N.i18nTextSessionStatus(statusScanned, statusRate,
				elapsedValues[0], elapsedValues[1], elapsedValues[2], elapsedValues[3]));
	}

	private int[] elapsedValues(long nanos) {
		long remaining = nanos / 1000000;
		int ms = (int) (remaining % 1000);

		remaining /= 1000;

		int s = (int) (remaining % 60);

		remaining /= 60;

		int m = (int) (remaining % 60);

		remaining /= 60;

		int h = (int) remaining;

		return new int[] { h, m, s, ms };
	}

	void sessionResult(FileScannerResult result) {
		Object data = result.getData();

		if (data != null) {
			TreeItem resultItem = Check.isInstanceOf(data, TreeItem.class);

			if (resultItem.getParentItem() == null && resultItem.getItemCount() == 0) {
				resultItem.setItemCount(result.childrenCount());
				resultItem.setExpanded(true);
			} else {
				resultItem.setItemCount(result.childrenCount());
			}
			for (FileScannerResult resultChild : result.children()) {
				sessionResult(resultChild);
			}
		}
	}

	private void setRootResultTreeItem(FileScannerResult rootResult) {
		TreeItem rootResultItem = new TreeItem(this.resultTreeHolder.get(), SWT.NONE);

		decorateResultTreeItem(rootResultItem, rootResult);
		rootResultItem.setItemCount(rootResult.childrenCount());
		rootResultItem.setData(rootResult);
		rootResult.setData(rootResultItem);
	}

	private void onSetResultTreeItemData(Event event) {
		TreeItem resultItemParent = Check.isInstanceOf(event.item, TreeItem.class).getParentItem();
		FileScannerResult resultParent = Check.isInstanceOf(resultItemParent.getData(), FileScannerResult.class);
		FileScannerResult[] results = resultParent.children();
		int resultItemCount = Math.min(results.length, resultItemParent.getItemCount());

		for (int resultIndex = event.index; resultIndex < resultItemCount; resultIndex++) {
			TreeItem resultItem = resultItemParent.getItem(resultIndex);
			FileScannerResult result = results[resultIndex];

			decorateResultTreeItem(resultItem, result);
			resultItem.setItemCount(result.childrenCount());
			resultItem.setData(result);
			result.setData(resultItem);
		}
	}

	private void decorateResultTreeItem(TreeItem item, FileScannerResult result) {
		switch (result.type()) {
		case INPUT:
			item.setText(shortInputName(result.name()));
			item.setImage(this.resources.getImage(Images.class, Images.IMAGE_RESULT_INPUT16));
			break;
		case FORMAT:
			item.setText(result.name());
			item.setImage(this.resources.getImage(Images.class, Images.IMAGE_RESULT_FORMAT16));
			break;
		case ENCODED_INPUT:
			item.setText(result.name());
			item.setImage(this.resources.getImage(Images.class, Images.IMAGE_RESULT_ENCODED_INPUT16));
			break;
		}
	}

	private String shortInputName(String name) {
		int shortNameIndex = name.lastIndexOf('/');

		if (shortNameIndex < 0) {
			shortNameIndex = name.lastIndexOf('\\');
		}
		return (shortNameIndex >= 0 && shortNameIndex + 1 < name.length() ? name.substring(shortNameIndex + 1) : name);
	}

	private void onResultTreeItemSelected(SelectionEvent event) {
		try {
			TreeItem resultItem = Check.isInstanceOf(event.item, TreeItem.class);
			FileScannerResult result = Check.isInstanceOf(resultItem.getData(), FileScannerResult.class);

			this.inputViewHolder.get().setResult(result);
			this.resultViewHolder.get().setUrl(this.resultRenderServiceHolder.get().setResult(result));
		} catch (Exception e) {
			Exceptions.warn(e);
		}
	}

	private void onDisposed() {
		LOG.info("Disposing Main UI...");

		UserPreferences.get().removeConsumer(this.configConsumer);
		this.controllerHolder.get().close();
		this.resultRenderServiceHolder.get().dispose();
		this.resources.disposeAll();

		LOG.info("Main UI disposed");
	}

	private void onOpenSelected() {
		FileDialog openFileDialog = FileDialogBuilder.open(root()).withFilter(MainI18N.i18nTextFileOpenFilter()).get();
		String file = openFileDialog.open();

		if (file != null) {
			openFile(file);
		}
	}

	private void onPreferencesSelected() {
		PreferencesDialog preferencesDialog = new PreferencesDialog(root());

		try {
			preferencesDialog.open();
		} catch (Exception e) {
			Exceptions.warn(e);
		}
	}

	private void onGotoResultEnd() {
		Hex inputView = this.inputViewHolder.get();
		FileScannerResult result = inputView.getResult();

		if (result != null) {
			inputView.scrollTo(result.end());
		}
	}

	private void onGotoResultStart() {
		Hex inputView = this.inputViewHolder.get();
		FileScannerResult result = inputView.getResult();

		if (result != null) {
			inputView.scrollTo(result.start());
		}
	}

	private void onAboutSelected() {

	}

	private void onVSashSelected(SelectionEvent event) {
		Sash vSash = this.vSashHolder.get();

		if ((event.detail & SWT.DRAG) != SWT.DRAG || (vSash.getStyle() & SWT.SMOOTH) == SWT.SMOOTH) {
			FormData layoutData = (FormData) vSash.getLayoutData();

			layoutData.left = new FormAttachment(0, event.x);
			vSash.setLayoutData(layoutData);
			vSash.requestLayout();
		}
	}

	private void onHSashSelected(SelectionEvent event) {
		Sash hSash = this.hSashHolder.get();

		if ((event.detail & SWT.DRAG) != SWT.DRAG || (hSash.getStyle() & SWT.SMOOTH) == SWT.SMOOTH) {
			FormData layoutData = (FormData) hSash.getLayoutData();

			layoutData.top = new FormAttachment(0, event.y);
			hSash.setLayoutData(layoutData);
			hSash.requestLayout();
		}
	}

	@Override
	public void open() throws ResourceException {
		LOG.info("Opening Main UI...");

		this.resultRenderServiceHolder.set(new HtmlRenderService());

		MainController controller = this.controllerHolder.set(new MainController(this));
		Shell root = root();
		ShellBuilder rootBuilder = new ShellBuilder(root);
		ControlBuilder<Sash> vSash = rootBuilder.addControlChild(Sash.class, SWT.VERTICAL);
		ControlBuilder<Sash> hSash = rootBuilder.addControlChild(Sash.class, SWT.HORIZONTAL);
		CoolBarBuilder commands = buildCommandBar(rootBuilder, controller);
		ControlBuilder<Tree> resultTree = rootBuilder.addControlChild(Tree.class,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		ControlBuilder<Browser> resultView = rootBuilder.addControlChild(Browser.class, SWT.NONE);
		ControlBuilder<Hex> inputView = rootBuilder.addControlChild(Hex.class, SWT.NONE);
		CoolBarBuilder status = buildStatusBar(rootBuilder, controller);

		rootBuilder.withText(MainI18N.i18nTitle())
				.withImages(this.resources.getImages(Images.class, Images.IMAGES_FSLOGO));
		rootBuilder.onDisposed(this::onDisposed);
		buildMenuBar(rootBuilder);
		vSash.onSelected(this::onVSashSelected);
		hSash.onSelected(this::onHSashSelected);
		resultTree.onEvent(SWT.SetData, this::onSetResultTreeItemData);
		resultTree.onSelected(this::onResultTreeItemSelected);
		resultView.onEvent(SWT.MenuDetect, event -> event.doit = false);

		FormLayoutBuilder.layout().apply(root);
		FormLayoutBuilder.data().left(40).top(commands).bottom(status).apply(vSash);
		FormLayoutBuilder.data().left(vSash).top(50).right(100).apply(hSash);
		FormLayoutBuilder.data().left(0).top(0).right(100).apply(commands);
		FormLayoutBuilder.data().left(0).top(commands).right(vSash).bottom(status).apply(resultTree);
		FormLayoutBuilder.data().left(vSash).top(commands).right(100).bottom(hSash).apply(resultView);
		FormLayoutBuilder.data().left(vSash).top(hSash).right(100).bottom(status).apply(inputView);
		FormLayoutBuilder.data().left(0).right(100).bottom(100).apply(status);

		this.resultTreeHolder.set(resultTree.get());
		this.resultViewHolder.set(resultView.get());
		this.inputViewHolder.set(inputView.get());
		this.vSashHolder.set(vSash.get());
		this.hSashHolder.set(hSash.get());

		UserPreferences preferences = UserPreferences.get();

		preferences.addConsumer(this.configConsumer);
		this.configConsumer.accept(preferences);
		root.layout(true);
		resultTree.get().setFocus();
		resetSession(false);
		root.open();
	}

	private void applyConfig(Config config) {
		Font inputViewFont = this.resources.getFont(config.getInputViewFont());

		this.inputViewHolder.get().setFont(inputViewFont);
		this.resultRenderServiceHolder.get().applyConfig(config);
		this.resultViewHolder.get().refresh();
	}

	private void buildMenuBar(ShellBuilder rootBuilder) {
		MenuBuilder menu = MenuBuilder.menuBar(rootBuilder);

		menu.addItem(SWT.CASCADE).withText(MainI18N.i18nMenuFile());
		menu.beginMenu();
		menu.addItem(SWT.PUSH).withText(MainI18N.i18nMenuFileOpen());
		menu.onSelected(this::onOpenSelected);
		if (PlatformIntegration.isCocoa()) {
			Display display = menu.get().getDisplay();

			PlatformIntegration.cocoaAddPreferencesSelectionAction(display, this::onPreferencesSelected);
			PlatformIntegration.cocoaAddQuitSelectionAction(display, this::close);
		} else {
			menu.addItem(SWT.SEPARATOR);
			menu.addItem(SWT.PUSH).withText(MainI18N.i18nMenuFilePreferences());
			menu.onSelected(this::onPreferencesSelected);
			menu.addItem(SWT.SEPARATOR);
			menu.addItem(SWT.PUSH).withText(MainI18N.i18nMenuFileQuit());
			menu.onSelected(this::close);
		}
		menu.endMenu();
		menu.addItem(SWT.CASCADE).withText(MainI18N.i18nMenuHelp());
		menu.beginMenu();
		menu.addItem(SWT.PUSH).withText(MainI18N.i18nMenuHelpLogs());
		if (PlatformIntegration.isCocoa()) {
			Display display = menu.get().getDisplay();

			PlatformIntegration.cocoaAddAboutSelectionAction(display, this::onAboutSelected);
		} else {
			menu.addItem(SWT.SEPARATOR);
			menu.addItem(SWT.PUSH).withText(MainI18N.i18nMenuHelpAbout());
			menu.onSelected(this::onAboutSelected);
		}
		menu.endMenu();
	}

	private CoolBarBuilder buildCommandBar(ShellBuilder rootBuilder, MainController controller) {
		CoolBarBuilder commands = CoolBarBuilder.horizontal(rootBuilder, SWT.NONE);
		ToolBarBuilder fileTools = ToolBarBuilder.horizontal(commands, SWT.FLAT);
		ToolBarBuilder editTools = ToolBarBuilder.horizontal(commands, SWT.FLAT);
		CompositeBuilder<Composite> searchTools = commands.addCompositeChild(SWT.NONE);
		ControlBuilder<Text> searchPattern = searchTools.addControlChild(Text.class, SWT.SEARCH | SWT.ICON_SEARCH);
		ToolBarBuilder searchToolsButtons = ToolBarBuilder.horizontal(searchTools, SWT.FLAT);

		// File tools
		fileTools.addItem(SWT.PUSH);
		fileTools.withImage(this.resources.getImage(Images.class, Images.IMAGE_OPEN_FILE16));
		fileTools.onSelected(this::onOpenSelected);
		commands.addItem(SWT.NONE).withControl(fileTools);
		// Edit tools
		editTools.addItem(SWT.PUSH);
		editTools.withImage(this.resources.getImage(Images.class, Images.IMAGE_COPY_OBJECT16))
				.withDisabledImage(this.resources.getImage(Images.class, Images.IMAGE_COPY_OBJECT_DISABLED16));
		editTools.onSelected(controller::onCopyObjectSelected);
		editTools.addItem(SWT.PUSH);
		editTools.withImage(this.resources.getImage(Images.class, Images.IMAGE_EXPORT_OBJECT16))
				.withDisabledImage(this.resources.getImage(Images.class, Images.IMAGE_EXPORT_OBJECT_DISABLED16));
		editTools.onSelected(controller::onExportObjectSelected);
		commands.addItem(SWT.NONE).withControl(editTools);
		// Search tools
		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(this.resources.getImage(Images.class, Images.IMAGE_GOTO_NEXT16));
		searchToolsButtons.onSelected(controller::onGotoNextSelected);
		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(this.resources.getImage(Images.class, Images.IMAGE_GOTO_PREVIOUS16));
		searchToolsButtons.onSelected(controller::onGotoPreviousSelected);
		searchToolsButtons.addItem(SWT.SEPARATOR);
		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(this.resources.getImage(Images.class, Images.IMAGE_GOTO_END16));
		searchToolsButtons.onSelected(this::onGotoResultEnd);
		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(this.resources.getImage(Images.class, Images.IMAGE_GOTO_START16));
		searchToolsButtons.onSelected(this::onGotoResultStart);
		GridLayoutBuilder.layout(2).margin(2, 2).apply(searchTools);
		GridLayoutBuilder.data().align(SWT.FILL, SWT.CENTER).grab(true, false).apply(searchPattern);
		GridLayoutBuilder.data().apply(searchToolsButtons);
		commands.addItem(SWT.NONE).withControl(searchTools);

		commands.lock(true).pack();
		this.searchPatternHolder.set(searchPattern.get());
		return commands;
	}

	private CoolBarBuilder buildStatusBar(ShellBuilder rootBuilder, MainController controller) {
		CoolBarBuilder status = CoolBarBuilder.horizontal(rootBuilder, SWT.NONE);

		status.addItem(SWT.NONE);

		CompositeBuilder<Composite> session = status.addCompositeChild(SWT.NONE);

		GridLayoutBuilder.layout(3).margin(0, 0).apply(session);

		ToolBarBuilder sessionTools = session.addChild(parent -> ToolBarBuilder.horizontal(parent, SWT.FLAT));

		sessionTools.addItem(SWT.PUSH);
		sessionTools.withImage(this.resources.getImage(Images.class, Images.IMAGE_STOP_SCAN16))
				.withDisabledImage(this.resources.getImage(Images.class, Images.IMAGE_STOP_SCAN_DISABLED16));
		sessionTools.onSelected(controller::onStopScanSelected);
		GridLayoutBuilder.data().apply(sessionTools);
		this.cancelButtonHolder.set(sessionTools.currentItem());

		ProgressBar sessionProgress = new ProgressBar(session.get(), SWT.HORIZONTAL);

		GridLayoutBuilder.data().apply(sessionProgress);
		this.sessionProgressHolder.set(sessionProgress);

		Label sessionStatus = new Label(session.get(), SWT.HORIZONTAL);

		GridLayoutBuilder.data().align(SWT.FILL, SWT.CENTER).grab(true, false).apply(sessionStatus);
		this.sessionStatusHolder.set(sessionStatus);

		status.withControl(session);
		status.lock(true).pack();
		return status;
	}

}
