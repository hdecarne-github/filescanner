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
package de.carne.filescanner.jfx.control;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import de.carne.filescanner.util.Hex;
import de.carne.filescanner.util.Printer;
import de.carne.util.logging.Log;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Skin for displaying raw byte data from a {@code FileChannel} object.
 */
class HexViewSkin extends SkinBase<HexView> implements VirtualScrollRegion.Scrollable {

	private static final Log LOG = new Log(HexViewSkin.class);

	private static final int MAX_LINE_LENGTH = 16 + 3 + (16 * 2) + 15 + 2 + 16;

	private static final char NON_PRINTABLE_CHAR = '.';

	private final VirtualScrollRegion<TextFlow> scrollRegion;
	private TextFlow viewPane;

	HexViewSkin(HexView skinnable) {
		super(skinnable);
		this.scrollRegion = new VirtualScrollRegion<>(this);
		getChildren().add(this.scrollRegion);
		this.viewPane = new TextFlow();
		this.scrollRegion.setContent(this.viewPane);
		this.viewPane.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		this.viewPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		this.viewPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		onFileChanged(null);
		skinnable.fileProperty().addListener(new ChangeListener<FileChannel>() {

			@Override
			public void changed(ObservableValue<? extends FileChannel> observable, FileChannel oldValue,
					FileChannel newValue) {
				onFileChanged(newValue);
			}

		});
	}

	void onFileChanged(FileChannel file) {
		getSkinnable().requestLayout();
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.jfx.control.VirtualScrollRegion.Scrollable#
	 * layoutVirtual()
	 */
	@Override
	public Dimension2D layoutVirtual() {
		this.viewPane.autosize();
		return new Dimension2D(0, 0);
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.scene.control.SkinBase#layoutChildren(double, double, double,
	 * double)
	 */
	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		HexView skinnable = getSkinnable();

		@SuppressWarnings("resource")
		FileChannel file = skinnable.getFile();
		long fileSize = 0l;

		if (file != null) {
			try {
				fileSize = file.size();
			} catch (IOException e) {
				logIOError(e);
			}
		}

		long maxLine = (fileSize > 0 ? fileSize >>> 4 : -1);
		long currentPosition = skinnable.getPosition().longValue();
		long currentLine = currentPosition >>> 4;
		double layoutWidth = 0.0;
		double layoutHeight = 0.0;
		ByteBuffer buffer = null;

		while (currentLine <= maxLine && layoutHeight < contentHeight) {
			if (buffer != null) {
				buffer.clear();
			} else {
				this.viewPane.getChildren().clear();
				buffer = ByteBuffer.allocateDirect(16);
			}
			try {
				assert file != null;

				file.read(buffer, currentPosition);
			} catch (IOException e) {
				logIOError(e);
			}
			buffer.flip();

			List<String> lineParts = formatLineParts(buffer, currentPosition, 0, 0);
			String line = lineParts.get(0);
			Text lineText = new Text(0.0, layoutHeight, line);

			lineText.setFont(skinnable.getFont());
			this.viewPane.getChildren().add(lineText);

			Bounds lineTextBounds = lineText.getBoundsInLocal();

			layoutWidth = Math.max(layoutWidth, lineTextBounds.getWidth());
			layoutHeight += lineTextBounds.getHeight();

			long nextPosition = currentPosition + 16;

			currentPosition = nextPosition;
			currentLine++;
		}
		super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
	}

	private List<String> formatLineParts(ByteBuffer buffer, long position, long selStart, long selEnd) {
		ArrayList<String> lineParts = new ArrayList<>(5);
		StringBuilder stringBuffer = new StringBuilder(MAX_LINE_LENGTH + 1);

		Hex.formatL(stringBuffer, position);
		stringBuffer.append("h  ");
		for (long bytePosition = position; bytePosition < position + 16; bytePosition++) {
			if (buffer.hasRemaining()) {
				byte byteValue = buffer.get();

				Hex.formatU(stringBuffer, byteValue);
				stringBuffer.append(' ');
			} else {
				stringBuffer.append("   ");
			}
		}
		stringBuffer.append(' ');
		buffer.flip();
		for (long bytePosition = position; bytePosition < position + 16; bytePosition++) {
			if (buffer.hasRemaining()) {
				byte byteValue = buffer.get();

				Printer.format(stringBuffer, byteValue, NON_PRINTABLE_CHAR);
			} else {
				stringBuffer.append(' ');
			}
		}
		stringBuffer.append('\n');
		lineParts.add(stringBuffer.toString());
		return lineParts;
	}

	private void logIOError(IOException e) {
		LOG.error(e, null, "An I/O error occured");
	}

}
