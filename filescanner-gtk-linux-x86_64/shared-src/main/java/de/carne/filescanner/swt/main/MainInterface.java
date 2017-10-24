/*
 * Copyright (c) 2017 Holger de Carne and contributors, All Rights Reserved.
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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import de.carne.filescanner.swt.resources.Images;
import de.carne.filescanner.swt.widgets.Hex;
import de.carne.swt.graphics.ResourceException;
import de.carne.swt.layout.GridLayoutBuilder;
import de.carne.swt.widgets.CompositeBuilder;
import de.carne.swt.widgets.CoolBarBuilder;
import de.carne.swt.widgets.MenuBuilder;
import de.carne.swt.widgets.ToolBarBuilder;
import de.carne.swt.widgets.UserInterface;
import de.carne.util.Late;

/**
 * Main window.
 */
public class MainInterface extends UserInterface<Shell> {

	private final Late<MainController> agentHolder = new Late<>();

	@Override
	protected void build(Shell root) throws ResourceException {
		MainController agent = this.agentHolder.set(new MainController());

		agent.root.set(root);
		root.setImages(Images.IMAGES_FSLOGO.get());
		root.setText(MainI18N.i18nTitle());
		buildMenuBar(agent);

		CoolBar commands = buildCommandBar(agent);
		Label separator = new Label(root, SWT.HORIZONTAL | SWT.SEPARATOR);
		Tree resultView = new Tree(root, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.BORDER);
		Sash vSash = new Sash(root, SWT.VERTICAL);
		Browser preView = new Browser(root, SWT.BORDER | SWT.WEBKIT);
		Sash hSash = new Sash(root, SWT.HORIZONTAL);
		Hex hexView = new Hex(root, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		CoolBar status = buildStatusBar(agent);

		FormLayout rootLayout = new FormLayout();

		root.setLayout(rootLayout);

		FormData commandsLayout = new FormData();

		commandsLayout.left = new FormAttachment(0, 0);
		commandsLayout.top = new FormAttachment(0, 0);
		commandsLayout.right = new FormAttachment(100, 0);
		commands.setLayoutData(commandsLayout);

		FormData separatorLayout = new FormData();

		separatorLayout.left = new FormAttachment(0, 0);
		separatorLayout.top = new FormAttachment(commands);
		separatorLayout.right = new FormAttachment(100, 0);
		separator.setLayoutData(separatorLayout);

		FormData resultViewLayout = new FormData();

		resultViewLayout.left = new FormAttachment(0, 0);
		resultViewLayout.top = new FormAttachment(separator);
		resultViewLayout.right = new FormAttachment(vSash);
		resultViewLayout.bottom = new FormAttachment(status);
		resultView.setLayoutData(resultViewLayout);

		FormData vSashLayout = new FormData();

		vSashLayout.left = new FormAttachment(33, 0);
		vSashLayout.top = new FormAttachment(separator);
		vSashLayout.bottom = new FormAttachment(status);
		vSash.setLayoutData(vSashLayout);

		FormData preViewLayout = new FormData();

		preViewLayout.left = new FormAttachment(vSash);
		preViewLayout.top = new FormAttachment(separator);
		preViewLayout.right = new FormAttachment(100, 0);
		preViewLayout.bottom = new FormAttachment(hSash);
		preView.setLayoutData(preViewLayout);

		FormData hSashLayout = new FormData();

		hSashLayout.left = new FormAttachment(vSash);
		hSashLayout.top = new FormAttachment(50, 0);
		hSashLayout.right = new FormAttachment(100, 0);
		hSash.setLayoutData(hSashLayout);

		FormData hexViewLayout = new FormData();

		hexViewLayout.left = new FormAttachment(vSash);
		hexViewLayout.top = new FormAttachment(hSash);
		hexViewLayout.right = new FormAttachment(100, 0);
		hexViewLayout.bottom = new FormAttachment(status);
		hexView.setLayoutData(hexViewLayout);

		FormData statusLayout = new FormData();

		statusLayout.left = new FormAttachment(0, 0);
		statusLayout.right = new FormAttachment(100, 0);
		statusLayout.bottom = new FormAttachment(100, 0);
		status.setLayoutData(statusLayout);

		root.setMinimumSize(600, 400);
		root.layout(true);
		resultView.setFocus();
	}

	private void buildMenuBar(MainController agent) {
		MenuBuilder menu = MenuBuilder.menuBar(agent.root);

		menu.addItem(SWT.CASCADE).withText(MainI18N.i18nMenuFile());
		menu.beginMenu();
		menu.addItem(SWT.PUSH).withText(MainI18N.i18nMenuFileOpen());
		menu.addItem(SWT.SEPARATOR);
		menu.addItem(SWT.PUSH).withText(MainI18N.i18nMenuFileQuit());
		menu.onSelected(agent::onQuitSelected);
		menu.endMenu();
		menu.addItem(SWT.CASCADE).withText(MainI18N.i18nMenuHelp());
		menu.beginMenu();
		menu.addItem(SWT.PUSH).withText(MainI18N.i18nMenuHelpAbout());
		menu.endMenu();
	}

	private CoolBar buildCommandBar(MainController agent) {
		CoolBarBuilder commands = CoolBarBuilder.horizontal(agent.root, SWT.NONE);
		ToolBarBuilder fileTools = ToolBarBuilder.horizontal(commands, SWT.FLAT);
		ToolBarBuilder editTools = ToolBarBuilder.horizontal(commands, SWT.FLAT);
		CompositeBuilder<Composite> searchTools = commands.addCompositeChild(SWT.NONE);
		Text searchToolsText = new Text(searchTools.get(), SWT.SEARCH | SWT.ICON_SEARCH);
		ToolBarBuilder searchToolsButtons = ToolBarBuilder.horizontal(searchTools, SWT.FLAT);

		commands.addItem(SWT.NONE);
		fileTools.addItem(SWT.PUSH);
		fileTools.withImage(Images.IMAGE_OPEN_FILE16);
		fileTools.onSelected(agent::onOpenSelected);
		commands.withControl(fileTools);
		commands.addItem(SWT.NONE);
		editTools.addItem(SWT.PUSH);
		editTools.withImage(Images.IMAGE_COPY_OBJECT16).withDisabledImage(Images.IMAGE_COPY_OBJECT_DISABLED16);
		editTools.onSelected(agent::onCopyObjectSelected);
		editTools.addItem(SWT.PUSH);
		editTools.withImage(Images.IMAGE_EXPORT_OBJECT16).withDisabledImage(Images.IMAGE_EXPORT_OBJECT_DISABLED16);
		editTools.onSelected(agent::onExportObjectSelected);
		commands.withControl(editTools);
		commands.addItem(SWT.NONE);

		GridLayoutBuilder.layout(2).margin(2, 2).apply(searchTools);

		GridLayoutBuilder.data().align(SWT.FILL, SWT.CENTER).grab(true, false).apply(searchToolsText);

		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(Images.IMAGE_GOTO_NEXT16);
		searchToolsButtons.onSelected(agent::onGotoNextSelected);
		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(Images.IMAGE_GOTO_PREVIOUS16);
		searchToolsButtons.onSelected(agent::onGotoPreviousSelected);
		searchToolsButtons.addItem(SWT.SEPARATOR);
		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(Images.IMAGE_GOTO_END16);
		searchToolsButtons.onSelected(agent::onGotoEndSelected);
		searchToolsButtons.addItem(SWT.PUSH);
		searchToolsButtons.withImage(Images.IMAGE_GOTO_START16);
		searchToolsButtons.onSelected(agent::onGotoStartSelected);

		GridLayoutBuilder.data().apply(searchToolsButtons);

		commands.withControl(searchTools);
		commands.setLocked(true).pack();
		return commands.get();
	}

	private CoolBar buildStatusBar(MainController agent) {
		CoolBarBuilder status = CoolBarBuilder.horizontal(agent.root, SWT.NONE);

		status.addItem(SWT.NONE);

		CompositeBuilder<Composite> session = status.addCompositeChild(SWT.NONE);

		GridLayoutBuilder.layout(3).margin(0, 0).apply(session);

		ToolBarBuilder sessionTools = session.addChild(parent -> ToolBarBuilder.horizontal(parent, SWT.FLAT));

		sessionTools.addItem(SWT.PUSH);
		sessionTools.withImage(Images.IMAGE_STOP_SCAN16).withDisabledImage(Images.IMAGE_STOP_SCAN_DISABLED16);
		sessionTools.onSelected(agent::onStopScanSelected);
		GridLayoutBuilder.data().apply(sessionTools);

		ProgressBar sessionProgress = new ProgressBar(session.get(), SWT.HORIZONTAL);

		GridLayoutBuilder.data().apply(sessionProgress);

		Label sessionStatus = new Label(session.get(), SWT.HORIZONTAL);

		GridLayoutBuilder.data().align(SWT.FILL, SWT.CENTER).grab(true, false).apply(sessionStatus);

		status.withControl(session);
		status.setLocked(true).pack();
		return status.get();
	}

}
