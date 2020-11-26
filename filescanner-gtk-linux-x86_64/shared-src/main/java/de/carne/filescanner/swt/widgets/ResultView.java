/*
 * Copyright (c) 2007-2020 Holger de Carne and contributors, All Rights Reserved.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;

import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.transfer.FileScannerResultRenderHandler;
import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.filescanner.engine.transfer.TransferSource;
import de.carne.swt.browseradapter.BrowserAdapter;
import de.carne.swt.browseradapter.PlatformBrowserAdapterProvider;
import de.carne.swt.browseradapter.WebKitBrowserAdapterProvider;
import de.carne.swt.layout.GridLayoutBuilder;
import de.carne.util.Exceptions;
import de.carne.util.Strings;

/**
 * Custom control for rendering and displaying file scanner results.
 */
public class ResultView extends Composite {

	private static final String ABOUT_BLANK_URL = "about:blank";

	private final BrowserAdapter browser;
	private final Link pagination;
	private @Nullable ResultViewContentHandler contentHandler = null;
	private List<ResultNavigator> resultNavigators = new ArrayList<>(1);
	private boolean inNavigation = false;

	/**
	 * Constructs a new {@linkplain ResultView} instance.
	 *
	 * @param parent the widget's owner.
	 * @param style the widget's style.
	 */
	public ResultView(Composite parent, int style) {
		super(parent, style);
		this.browser = BrowserAdapter.getInstance(this, SWT.NONE, WebKitBrowserAdapterProvider.NAME,
				PlatformBrowserAdapterProvider.NAME);

		Composite browserWidget = this.browser.getBrowserWidget();

		this.pagination = new Link(this, SWT.NONE);
		browserWidget.addListener(SWT.MenuDetect, event -> event.doit = false);
		browserWidget.addTraverseListener(event -> event.doit = true);
		this.pagination.addListener(SWT.Selection, this::onPageSelection);
		GridLayoutBuilder.layout().margin(0, 0).apply(this);
		GridLayoutBuilder.data(GridData.FILL_BOTH).apply(browserWidget);
		GridLayoutBuilder.data(GridData.FILL_HORIZONTAL).exclude(true).apply(this.pagination);

		Display display = getDisplay();

		setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));

		String defaultUrl = ABOUT_BLANK_URL;

		try {
			defaultUrl = ResultViewServer.getInstance(this).getDefaultUri().toASCIIString();
		} catch (IOException e) {
			Exceptions.warn(e);
		}
		this.browser.setUrl(defaultUrl);
	}

	/**
	 * Sets the style parameters to use for result rendering.
	 * <p>
	 * Changing the render style forces a refresh of all currently active {@linkplain ResultView} instances.
	 * </p>
	 *
	 * @param font the font to use for result rendering.
	 * @param background the background color to use for result rendering.
	 * @param styleColors the style colors to use for result rendering.
	 */
	public void setRenderStyle(FontData font, RGB background, Map<RenderStyle, RGB> styleColors) {
		try {
			ResultViewServer server = ResultViewServer.getInstance(this);

			server.setStyle(font, background, styleColors);
		} catch (IOException e) {
			Exceptions.warn(e);
		}
	}

	/**
	 * Refreshes this result view by re-rendering the current result (e.g. after the render style has changed).
	 */
	public void refresh() {
		this.browser.refresh();
	}

	/**
	 * Prints this result view.
	 */
	public void print() {
		this.browser.execute("javascript:window.print();");
	}

	/**
	 * Gets the currently displayed content.
	 *
	 * @return the currently displayed content.
	 */
	public TransferSource getContent() {
		return (this.contentHandler != null ? this.contentHandler.getContent() : ResultViewContentHandler.NO_CONTENT);
	}

	/**
	 * Sets the {@linkplain FileScannerResult} to display.
	 * <p>
	 * Setting the result to {@code null} clears the displayed result and shows the default content.
	 * </p>
	 *
	 * @param result the {@linkplain FileScannerResult} to display (may be {@code null}).
	 * @param renderHandler the {@linkplain FileScannerResultRenderHandler} to use for rendering. May {@code null} to
	 * use the default handler.
	 */
	public void setResult(@Nullable FileScannerResult result, @Nullable FileScannerResultRenderHandler renderHandler) {
		if (!this.inNavigation) {
			this.browser.setUrl(setupContentHandler(result, renderHandler));
		}
	}

	/**
	 * Gets the currently displayed {@linkplain FileScannerResult}.
	 *
	 * @return the currently displayed {@linkplain FileScannerResult} or {@code null} if none is currently displayed.
	 */
	@Nullable
	public FileScannerResult getResult() {
		return (this.contentHandler != null ? this.contentHandler.result() : null);
	}

	/**
	 * Adds a {@linkplain ResultNavigator} callback to this result view instance.
	 *
	 * @param resultNavigator the {@linkplain ResultNavigator} callback to add.
	 */
	public void addResultNavigator(ResultNavigator resultNavigator) {
		this.resultNavigators.add(resultNavigator);
	}

	/**
	 * Removes a previously added {@linkplain ResultNavigator} callback from this result view instance.
	 *
	 * @param resultNavigator the {@linkplain ResultNavigator} callback to remove.
	 * @see #addResultNavigator(ResultNavigator)
	 */
	public void removeResultNavigator(ResultNavigator resultNavigator) {
		this.resultNavigators.remove(resultNavigator);
	}

	void updatePagination(String pageLinks) {
		GridData paginationLayoutData = (GridData) this.pagination.getLayoutData();
		boolean layoutRequired = false;

		if (Strings.notEmpty(pageLinks)) {
			layoutRequired = true;
			paginationLayoutData.exclude = false;
		} else {
			layoutRequired = !paginationLayoutData.exclude;
			paginationLayoutData.exclude = true;
		}
		this.pagination.setText(pageLinks);
		if (layoutRequired) {
			this.browser.getBrowserWidget().requestLayout();
			this.pagination.requestLayout();
		}
	}

	@SuppressWarnings("null")
	private void onPageSelection(Event event) {
		this.browser.setUrl(this.contentHandler.documentPageUri(event.text).toASCIIString());
	}

	String navigateTo(FileScannerResult from, long position) {
		FileScannerResult to = from;

		try {
			this.inNavigation = true;
			for (ResultNavigator resultNavigator : this.resultNavigators) {
				to = resultNavigator.navigateTo(from, position);
			}
		} finally {
			this.inNavigation = false;
		}
		return setupContentHandler(to, null);
	}

	private String setupContentHandler(@Nullable FileScannerResult result,
			@Nullable FileScannerResultRenderHandler renderHandler) {
		String resultUrl = ABOUT_BLANK_URL;

		try {
			ResultViewServer server = ResultViewServer.getInstance(this);

			if (this.contentHandler != null) {
				server.removeResult(this.contentHandler);
			}
			if (result != null) {
				this.contentHandler = server.addResult(this, result, renderHandler);
				resultUrl = this.contentHandler.documentUri().toASCIIString();
			} else {
				this.contentHandler = null;
				resultUrl = server.getDefaultUri().toASCIIString();
			}
		} catch (IOException e) {
			Exceptions.warn(e);
		}
		return resultUrl;
	}

}
