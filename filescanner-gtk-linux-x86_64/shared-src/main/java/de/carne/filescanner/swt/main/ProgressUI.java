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
package de.carne.filescanner.swt.main;

import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;

import de.carne.boot.Application;
import de.carne.boot.check.Check;
import de.carne.filescanner.FileScannerMain;
import de.carne.filescanner.swt.resources.Images;
import de.carne.swt.graphics.ResourceTracker;
import de.carne.swt.layout.RowLayoutBuilder;
import de.carne.swt.widgets.ControlBuilder;
import de.carne.swt.widgets.ShellBuilder;
import de.carne.swt.widgets.ShellUserInterface;
import de.carne.swt.widgets.ToolBarBuilder;
import de.carne.util.Late;

class ProgressUI extends ShellUserInterface implements ProgressCallback {

	public static final int STYLE = SWT.TOOL | SWT.APPLICATION_MODAL;

	private static final long UPDATE_FREQUENCY = 500l * 1000l * 1000l;

	private final ResourceTracker resources;
	private long total = -1;
	private long progress = 0;
	private long lastUpdateNanos = System.nanoTime();
	private Late<ToolItem> stopCommandHolder = new Late<>();
	private Late<ProgressBar> determinateProgressHolder = new Late<>();
	private Late<ProgressBar> indeterminateProgressHolder = new Late<>();
	private Late<Future<Void>> taskHolder = new Late<>();

	public ProgressUI(Shell shell) {
		super(shell);
		this.resources = ResourceTracker.forDevice(shell.getDisplay()).forShell(shell);
	}

	@Override
	public void open() {
		ShellBuilder rootBuilder = buildRoot();

		rootBuilder.pack();
		rootBuilder.position(SWT.CENTER, SWT.CENTER);

		Shell root = rootBuilder.get();

		root.open();
	}

	public Future<Void> run(Future<Void> taskFuture) {
		this.taskHolder.set(taskFuture);
		super.run();
		return taskFuture;
	}

	@Override
	public synchronized void setTotal(long total) {
		this.total = total;
	}

	@Override
	public synchronized void addProgress(long progressDelta) {
		this.progress += progressDelta;

		long updateNanos = System.nanoTime();

		if (this.total > 0
				&& (this.progress == progressDelta || (updateNanos - this.lastUpdateNanos) >= UPDATE_FREQUENCY)) {
			this.lastUpdateNanos = updateNanos;
			Application.getMain(FileScannerMain.class).runNoWait(this::updateProgressUI);
		}
	}

	private synchronized void updateProgressUI() {
		if (!root().isDisposed()) {
			ProgressBar determinateProgress = this.determinateProgressHolder.get();

			if (!determinateProgress.isVisible()) {
				determinateProgress.setVisible(true);
				Check.isInstanceOf(determinateProgress.getLayoutData(), RowData.class).exclude = false;

				ProgressBar indeterminateProgress = this.indeterminateProgressHolder.get();

				indeterminateProgress.setVisible(false);
				Check.isInstanceOf(indeterminateProgress.getLayoutData(), RowData.class).exclude = true;
				root().layout();
			}

			int progressValue = 0;

			if (this.total > 0) {
				progressValue = (int) ((this.progress * 100) / this.total);
			}
			determinateProgress.setSelection(progressValue);
		}
	}

	@Override
	public void done() {
		Application.getMain(FileScannerMain.class).runNoWait(this::closeProgressUI);
	}

	private void closeProgressUI() {
		if (!root().isDisposed()) {
			root().close();
		}
	}

	private void onStopSelected() {
		this.taskHolder.get().cancel(true);
		this.stopCommandHolder.get().setEnabled(false);
		root().close();
	}

	private ShellBuilder buildRoot() {
		ShellBuilder rootBuilder = new ShellBuilder(get());
		ControlBuilder<ProgressBar> determinateProgress = rootBuilder.addControlChild(ProgressBar.class,
				SWT.HORIZONTAL);
		ControlBuilder<ProgressBar> indeterminateProgress = rootBuilder.addControlChild(ProgressBar.class,
				SWT.HORIZONTAL | SWT.INDETERMINATE);
		ToolBarBuilder progressTools = ToolBarBuilder.horizontal(rootBuilder, SWT.FLAT);

		progressTools.addItem(SWT.PUSH);
		progressTools.withImage(this.resources.getImage(Images.class, Images.IMAGE_STOP16))
				.withDisabledImage(this.resources.getImage(Images.class, Images.IMAGE_STOP_DISABLED16))
				.onSelected(this::onStopSelected);
		this.stopCommandHolder.set(progressTools.currentItem());

		determinateProgress.get().setVisible(false);

		RowLayoutBuilder.layout(SWT.HORIZONTAL).center(true).apply(rootBuilder);
		RowLayoutBuilder.data().exclude(true).apply(determinateProgress);
		RowLayoutBuilder.data().apply(indeterminateProgress);
		RowLayoutBuilder.data().apply(progressTools);

		this.determinateProgressHolder.set(determinateProgress.get());
		this.indeterminateProgressHolder.set(indeterminateProgress.get());
		return rootBuilder;
	}

}
