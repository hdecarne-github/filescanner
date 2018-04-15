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

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import de.carne.filescanner.swt.resources.Images;
import de.carne.filescanner.swt.widgets.Hex;
import de.carne.swt.graphics.ResourceException;
import de.carne.swt.layout.FormLayoutBuilder;
import de.carne.swt.layout.GridLayoutBuilder;
import de.carne.swt.widgets.CompositeBuilder;
import de.carne.swt.widgets.ControlBuilder;
import de.carne.swt.widgets.CoolBarBuilder;
import de.carne.swt.widgets.MenuBuilder;
import de.carne.swt.widgets.ShellBuilder;
import de.carne.swt.widgets.ToolBarBuilder;
import de.carne.swt.widgets.UserInterface;
import de.carne.util.Late;

/**
 * Main window UI.
 */
public class MainUI extends UserInterface<Shell> {

	private final Late<Shell> rootHolder = new Late<>();
	private final Late<Text> searchPatternHolder = new Late<>();
	private final Late<Sash> vSashHolder = new Late<>();
	private final Late<Sash> hSashHolder = new Late<>();

	/**
	 * Sends close request to the UI.
	 */
	public void close() {
		root().close();
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
	protected void build(Shell root) throws ResourceException {
		this.rootHolder.set(root);

		MainController controller = new MainController(this);
		ShellBuilder rootBuilder = new ShellBuilder(root);

		rootBuilder.withText(MainI18N.i18nTitle()).withImages(Images.IMAGES_FSLOGO);
		buildMenuBar(rootBuilder, controller);

		ControlBuilder<Sash> vSash = rootBuilder.addControlChild(Sash.class, SWT.VERTICAL);
		ControlBuilder<Sash> hSash = rootBuilder.addControlChild(Sash.class, SWT.HORIZONTAL);
		CoolBarBuilder commands = buildCommandBar(rootBuilder, controller);
		ControlBuilder<Tree> resultView = rootBuilder.addControlChild(Tree.class,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		ControlBuilder<Browser> preView = rootBuilder.addControlChild(Browser.class, SWT.NONE);
		ControlBuilder<Hex> hexView = rootBuilder.addControlChild(Hex.class, SWT.H_SCROLL | SWT.V_SCROLL);
		CoolBarBuilder status = buildStatusBar(controller);

		FormLayoutBuilder.layout().apply(root);
		FormLayoutBuilder.data().left(40).top(commands).bottom(status).apply(vSash);
		FormLayoutBuilder.data().left(vSash).top(50).right(100).apply(hSash);
		FormLayoutBuilder.data().left(0).top(0).right(100).apply(commands);
		FormLayoutBuilder.data().left(0).top(commands).right(vSash).bottom(status).apply(resultView);
		FormLayoutBuilder.data().left(vSash).top(commands).right(100).bottom(hSash).apply(preView);
		FormLayoutBuilder.data().left(vSash).top(hSash).right(100).bottom(status).apply(hexView);
		FormLayoutBuilder.data().left(0).right(100).bottom(100).apply(status);

		this.vSashHolder.set(vSash.get());
		this.hSashHolder.set(hSash.get());
		vSash.onSelected(this::onVSashSelected);
		hSash.onSelected(this::onHSashSelected);
		root.layout(true);
		resultView.get().setFocus();
	}

	private void buildMenuBar(ShellBuilder rootBuilder, MainController controller) {
		MenuBuilder menu = MenuBuilder.menuBar(rootBuilder);

		menu.addItem(SWT.CASCADE).withText(MainI18N.i18nMenuFile());
		menu.beginMenu();
		menu.addItem(SWT.PUSH).withText(MainI18N.i18nMenuFileOpen());
		menu.addItem(SWT.SEPARATOR);
		menu.addItem(SWT.PUSH).withText(MainI18N.i18nMenuFileQuit());
		menu.onSelected(controller::onQuitSelected);
		menu.endMenu();
		menu.addItem(SWT.CASCADE).withText(MainI18N.i18nMenuHelp());
		menu.beginMenu();
		menu.addItem(SWT.PUSH).withText(MainI18N.i18nMenuHelpAbout());
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
		fileTools.withImage(Images.IMAGE_OPEN_FILE16);
		fileTools.onSelected(controller::onOpenSelected);
		commands.addItem(SWT.NONE).withControl(fileTools);
		// Edit tools
		editTools.addItem(SWT.PUSH);
		editTools.withImage(Images.IMAGE_COPY_OBJECT16).withDisabledImage(Images.IMAGE_COPY_OBJECT_DISABLED16);
		editTools.onSelected(controller::onCopyObjectSelected);
		editTools.addItem(SWT.PUSH);
		editTools.withImage(Images.IMAGE_EXPORT_OBJECT16).withDisabledImage(Images.IMAGE_EXPORT_OBJECT_DISABLED16);
		editTools.onSelected(controller::onExportObjectSelected);
		commands.addItem(SWT.NONE).withControl(editTools);
		// Search tools
		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(Images.IMAGE_GOTO_NEXT16);
		searchToolsButtons.onSelected(controller::onGotoNextSelected);
		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(Images.IMAGE_GOTO_PREVIOUS16);
		searchToolsButtons.onSelected(controller::onGotoPreviousSelected);
		searchToolsButtons.addItem(SWT.SEPARATOR);
		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(Images.IMAGE_GOTO_END16);
		searchToolsButtons.onSelected(controller::onGotoEndSelected);
		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(Images.IMAGE_GOTO_START16);
		searchToolsButtons.onSelected(controller::onGotoStartSelected);
		GridLayoutBuilder.layout(2).margin(2, 2).apply(searchTools);
		GridLayoutBuilder.data().align(SWT.FILL, SWT.CENTER).grab(true, false).apply(searchPattern);
		GridLayoutBuilder.data().apply(searchToolsButtons);
		commands.addItem(SWT.NONE).withControl(searchTools);

		commands.lock(true).pack();
		this.searchPatternHolder.set(searchPattern.get());
		return commands;
	}

	private CoolBarBuilder buildStatusBar(MainController controller) {
		CoolBarBuilder status = CoolBarBuilder.horizontal(this.rootHolder, SWT.NONE);

		status.addItem(SWT.NONE);

		CompositeBuilder<Composite> session = status.addCompositeChild(SWT.NONE);

		GridLayoutBuilder.layout(3).margin(0, 0).apply(session);

		ToolBarBuilder sessionTools = session.addChild(parent -> ToolBarBuilder.horizontal(parent, SWT.FLAT));

		sessionTools.addItem(SWT.PUSH);
		sessionTools.withImage(Images.IMAGE_STOP_SCAN16).withDisabledImage(Images.IMAGE_STOP_SCAN_DISABLED16);
		sessionTools.onSelected(controller::onStopScanSelected);
		GridLayoutBuilder.data().apply(sessionTools);

		ProgressBar sessionProgress = new ProgressBar(session.get(), SWT.HORIZONTAL);

		GridLayoutBuilder.data().apply(sessionProgress);

		Label sessionStatus = new Label(session.get(), SWT.HORIZONTAL);

		GridLayoutBuilder.data().align(SWT.FILL, SWT.CENTER).grab(true, false).apply(sessionStatus);

		status.withControl(session);
		status.lock(true).pack();
		return status;
	}

}
