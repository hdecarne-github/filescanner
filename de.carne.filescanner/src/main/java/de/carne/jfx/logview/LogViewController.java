/*
 * Copyright (c) 2007-2016 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.jfx.logview;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import de.carne.jfx.ImageViewTableCell;
import de.carne.jfx.StageController;
import de.carne.util.logging.Log;
import de.carne.util.logging.LogBufferHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Dialog controller for live log display.
 */
public class LogViewController extends StageController {

	private static final Log LOG = new Log(LogViewController.class);

	private final Handler logHandler = new Handler() {

		@Override
		public void publish(LogRecord record) {
			publishLogRecord(record);
		}

		@Override
		public void flush() {
			// Nothing to do here
		}

		@Override
		public void close() throws SecurityException {
			// Nothing to do here
		}

	};

	@FXML
	TableView<LogRecordModel> ctlLogTable;

	@FXML
	TableColumn<LogRecordModel, Image> ctlLogTableLevel;

	@FXML
	TableColumn<LogRecordModel, String> ctlLogTableTimestamp;

	@FXML
	TableColumn<LogRecordModel, String> ctlLogTableMessage;

	/*
	 * (non-Javadoc)
	 * @see de.carne.jfx.StageController#setupStage(javafx.stage.Stage)
	 */
	@Override
	protected void setupStage(Stage controllerStage) throws IOException {
		super.setupStage(controllerStage);
		this.ctlLogTableLevel.setCellFactory(ImageViewTableCell.forTableColumn());
		this.ctlLogTableLevel.setCellValueFactory(new PropertyValueFactory<>("levelImage"));
		this.ctlLogTableTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestampString"));
		this.ctlLogTableMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
		controllerStage.showingProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				onShowingChanged(newValue);
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.jfx.StageController#getStyle()
	 */
	@Override
	protected StageStyle getStyle() {
		return StageStyle.UTILITY;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.jfx.StageController#getModality()
	 */
	@Override
	protected Modality getModality() {
		return Modality.NONE;
	}

	void onShowingChanged(Boolean newValue) {
		LogBufferHandler logBuffer = LogBufferHandler.getHandler(LOG.getLogger());

		if (logBuffer != null) {
			if (Boolean.TRUE.equals(newValue)) {
				logBuffer.addHandler(this.logHandler);
			} else {
				logBuffer.removeHandler(this.logHandler);
			}
		}
	}

	void publishLogRecord(LogRecord record) {
		if (Platform.isFxApplicationThread()) {
			ObservableList<LogRecordModel> logItems = this.ctlLogTable.getItems();
			int selectedItemIndex = this.ctlLogTable.getSelectionModel().getSelectedIndex();
			boolean updateSelection = selectedItemIndex < 0 || (selectedItemIndex + 1) == logItems.size();

			logItems.add(new LogRecordModel(record));
			while (logItems.size() > LogBufferHandler.BUFFER_SIZE) {
				logItems.remove(0);
			}
			if (updateSelection) {
				this.ctlLogTable.getSelectionModel().selectLast();
				this.ctlLogTable.scrollTo(this.ctlLogTable.getSelectionModel().getSelectedIndex());
			}
		} else {
			CountDownLatch latch = new CountDownLatch(1);

			Platform.runLater(new Runnable() {

				private final LogRecord recordParam = record;

				@Override
				public void run() {
					try {
						publishLogRecord(this.recordParam);
					} finally {
						latch.countDown();
					}
				}

			});
			try {
				latch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
