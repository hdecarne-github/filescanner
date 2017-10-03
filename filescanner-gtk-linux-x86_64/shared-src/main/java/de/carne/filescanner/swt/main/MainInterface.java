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
import org.eclipse.swt.widgets.Shell;

import de.carne.filescanner.swt.resources.Images;
import de.carne.swt.ResourceException;
import de.carne.swt.widgets.MenuBuilder;
import de.carne.swt.widgets.UserInterface;

/**
 * Main window.
 */
public class MainInterface extends UserInterface<Shell, MainController> {

	@Override
	protected MainController build() throws ResourceException {
		MainAgent agent = new MainAgent();
		Shell root = root();

		agent.root.set(root);
		root.setImages(Images.IMAGES_FSLOGO.get());
		root.setText(MainI18N.i18nTitle());
		buildMenuBar(agent);
		return agent;
	}

	private void buildMenuBar(MainAgent agent) {
		MenuBuilder menu = MenuBuilder.menuBar(agent.root.get());

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

}
