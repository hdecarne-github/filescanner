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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.carne.filescanner.util.ByteFormatter;
import de.carne.filescanner.util.Hexadecimal;
import de.carne.filescanner.util.Printer;
import de.carne.util.logging.Log;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Skin for displaying raw byte data from a {@code FileChannel} object.
 */
class FileViewSkin extends SkinBase<FileView> implements VirtualScrollRegion.Scrollable {

	private static final Log LOG = new Log(FileViewSkin.class);

	private static HashMap<FileViewType, ByteFormatter> BYTE_FORMATTER_MAP = new HashMap<>();

	static {
		BYTE_FORMATTER_MAP.put(FileViewType.BINARY, ByteFormatter.BINARY);
		BYTE_FORMATTER_MAP.put(FileViewType.OCTAL, ByteFormatter.OCTAL);
		BYTE_FORMATTER_MAP.put(FileViewType.HEXADECIMAL_L, ByteFormatter.HEXADECIMAL_L);
		BYTE_FORMATTER_MAP.put(FileViewType.HEXADECIMAL_U, ByteFormatter.HEXADECIMAL_U);
	}

	/*
	 * Assume binary formatting to get maximum.
	 */
	private static final int MAX_FORMAT_LINE_LENGTH = 16 + 3 + (16 * 8) + 15 + 2 + 16;

	private final ByteBuffer readBuffer = ByteBuffer.allocateDirect(16);

	private final VirtualScrollRegion<TextFlow> scrollRegion;
	private TextFlow viewPane;
	private double cachedViewPaneLineHeight = Double.NaN;

	FileViewSkin(FileView skinnable) {
		super(skinnable);
		this.scrollRegion = new VirtualScrollRegion<>(this);
		getChildren().add(this.scrollRegion);
		this.viewPane = new TextFlow();
		this.scrollRegion.setContent(this.viewPane);
		this.viewPane.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		this.viewPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		this.viewPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		skinnable.fontProperty().addListener(new ChangeListener<Font>() {

			@Override
			public void changed(ObservableValue<? extends Font> observable, Font oldValue, Font newValue) {
				requestLayout(false);
			}

		});
		skinnable.viewTypeProperty().addListener(new ChangeListener<FileViewType>() {

			@Override
			public void changed(ObservableValue<? extends FileViewType> observable, FileViewType oldValue,
					FileViewType newValue) {
				requestLayout(false);
			}

		});
		skinnable.fileProperty().addListener(new ChangeListener<FileAccess>() {

			@Override
			public void changed(ObservableValue<? extends FileAccess> observable, FileAccess oldValue,
					FileAccess newValue) {
				requestLayout(true);
			}

		});
		skinnable.positionProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				requestLayout(false);
			}

		});
		skinnable.selectionProperty().addListener(new ChangeListener<PositionRange>() {

			@Override
			public void changed(ObservableValue<? extends PositionRange> observable, PositionRange oldValue,
					PositionRange newValue) {
				requestLayout(false);
			}

		});
	}

	void requestLayout(boolean resetContent) {
		if (resetContent || Double.isNaN(this.cachedViewPaneLineHeight)) {
			this.scrollRegion.scrollTo(0.0, 0.0);
		} else {
			long line = getSkinnable().getPosition().longValue() / 16;

			this.scrollRegion.scrollTo(0.0, line * this.cachedViewPaneLineHeight);
		}
		this.scrollRegion.requestLayout();
	}

	@Override
	public VirtualScrollLayout layoutVirtual(double viewWidth, double viewHeight) {
		FileView skinnable = getSkinnable();
		FileAccess file = skinnable.getFile();
		long fileSize = 0L;

		if (file != null) {
			try {
				fileSize = file.size();
			} catch (IOException e) {
				logIOError(e);
			}
		}

		long lineCount = (fileSize + 15l) >>> 4;
		long currentPosition = (skinnable.getPosition().longValue() >>> 4) << 4;
		long currentLine = currentPosition >>> 4;
		double viewPaneHeight = 0.0;
		int viewPaneLines = 0;
		ByteFormatter byteFormatter = BYTE_FORMATTER_MAP.get(skinnable.getViewType());
		PositionRange selection = skinnable.getSelection();
		VirtualScrollLayout scrollLayout = null;

		this.viewPane.getChildren().clear();
		while (currentLine < lineCount && viewPaneHeight <= viewHeight) {
			// Prepare buffer and read line data
			this.readBuffer.clear();
			try {
				assert file != null;

				file.read(this.readBuffer, currentPosition);
			} catch (IOException e) {
				logIOError(e);
			}
			this.readBuffer.flip();

			// Format the line data and create the corresponding text elements
			List<String> lineParts = formatLineParts(this.readBuffer, byteFormatter, currentPosition, selection);
			int lineLength = 0;
			boolean inSelection = false;

			for (String linePart : lineParts) {
				lineLength += linePart.length();

				Text linePartText = new Text(linePart);

				linePartText.setFont(skinnable.getFont());
				if (inSelection) {
					linePartText.setFill(Color.BLACK);
				} else {
					linePartText.setFill(Color.GRAY);
				}
				this.viewPane.getChildren().add(linePartText);
				inSelection = !inSelection;
			}

			// Calculate layout sizes (if not yet done)
			if (viewPaneLines == 0) {
				// 1st line added; do first calculation
				// due to spacing and overlapping a re-calculation is needed
				// with the 2nd line added
				// (if there is no 2d line, then the following numbers are fine)
				this.viewPane.autosize();

				double viewPaneWidth = this.viewPane.getWidth();

				this.cachedViewPaneLineHeight = this.viewPane.getHeight();
				scrollLayout = new VirtualScrollLayout(viewPaneWidth, this.cachedViewPaneLineHeight * lineCount,
						viewPaneWidth / lineLength, this.cachedViewPaneLineHeight);
			} else if (viewPaneLines == 1) {
				// 2nd line added; update calculation
				// To get the effective line height we take the height increase
				// of the view pane.
				this.viewPane.autosize();

				double viewPaneWidth = this.viewPane.getWidth();

				this.cachedViewPaneLineHeight = this.viewPane.getHeight() - this.cachedViewPaneLineHeight;
				scrollLayout = new VirtualScrollLayout(viewPaneWidth, this.cachedViewPaneLineHeight * lineCount,
						viewPaneWidth / lineLength, this.cachedViewPaneLineHeight);
			}
			viewPaneHeight += this.cachedViewPaneLineHeight;
			viewPaneLines++;
			currentPosition += 16;
			currentLine++;
		}
		this.viewPane.autosize();
		return (scrollLayout != null ? scrollLayout : VirtualScrollLayout.EMPTY);
	}

	@Override
	public Point2D mapViewPort(double hValue, double vValue) {
		return new Point2D(-hValue, 0.0);
	}

	@Override
	public void hScrollTo(double value) {
		this.viewPane.setLayoutX(-value);
	}

	@Override
	public void vScrollTo(double value) {
		if (!Double.isNaN(this.cachedViewPaneLineHeight)) {
			long position = (long) (value / this.cachedViewPaneLineHeight) * 16;

			getSkinnable().setPosition(position);
		}
	}

	private List<String> formatLineParts(ByteBuffer buffer, ByteFormatter byteFormatter, long position,
			PositionRange selection) {
		ArrayList<String> lineParts = new ArrayList<>(5);
		StringBuilder stringBuffer = new StringBuilder(MAX_FORMAT_LINE_LENGTH + 1);

		Hexadecimal.formatL(stringBuffer, position);
		stringBuffer.append("h  ");

		boolean inSelection = false;

		for (long bytePosition = position; bytePosition < position + 16; bytePosition++) {
			if (inSelection && !selection.inRange(bytePosition)) {
				lineParts.add(stringBuffer.toString());
				stringBuffer.setLength(0);
				inSelection = false;
				if (bytePosition > position) {
					stringBuffer.append(' ');
				}
			} else if (!inSelection && selection.inRange(bytePosition)) {
				if (bytePosition > position) {
					stringBuffer.append(' ');
				}
				lineParts.add(stringBuffer.toString());
				stringBuffer.setLength(0);
				inSelection = true;
			} else if (bytePosition > position) {
				stringBuffer.append(' ');
			}
			if (buffer.hasRemaining()) {
				byte byteValue = buffer.get();

				byteFormatter.format(stringBuffer, byteValue);
			} else {
				stringBuffer.append("  ");
			}
		}
		if (inSelection) {
			lineParts.add(stringBuffer.toString());
			stringBuffer.setLength(0);
			inSelection = false;
		}
		stringBuffer.append("  ");
		buffer.flip();
		for (long bytePosition = position; bytePosition < position + 16; bytePosition++) {
			if (inSelection && !selection.inRange(bytePosition)) {
				lineParts.add(stringBuffer.toString());
				stringBuffer.setLength(0);
				inSelection = false;
			} else if (!inSelection && selection.inRange(bytePosition)) {
				lineParts.add(stringBuffer.toString());
				stringBuffer.setLength(0);
				inSelection = true;
			}
			if (buffer.hasRemaining()) {
				byte byteValue = buffer.get();

				Printer.format(stringBuffer, byteValue);
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
