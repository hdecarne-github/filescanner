/*
 * Copyright (c) 2007-2019 Holger de Carne and contributors, All Rights Reserved.
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
import java.nio.ByteBuffer;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.carne.boot.Exceptions;
import de.carne.boot.platform.Platform;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.input.FileScannerInput;

/**
 * Custom control for displaying raw hexadecimal data to the user.
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class Hex extends Canvas implements DisposeListener, FocusListener, TraverseListener, KeyListener,
		MouseWheelListener, ControlListener, PaintListener {

	private final IntScrollBarProxy horizontal;
	private final LongScrollBarProxy vertical;
	private @Nullable FileScannerResult result = null;
	private @Nullable Font defaultFont = null;
	private @Nullable Layout cachedLayout = null;

	/**
	 * Constructs a new {@linkplain Hex} instance.
	 *
	 * @param parent the widget's owner.
	 * @param style the widget's style.
	 */
	public Hex(Composite parent, int style) {
		super(parent, style | SWT.H_SCROLL | SWT.V_SCROLL);
		this.horizontal = new IntScrollBarProxy(Objects.requireNonNull(getHorizontalBar()));
		this.vertical = new LongScrollBarProxy(Objects.requireNonNull(getVerticalBar()));

		Display display = getDisplay();

		setFont(getDefaultFont(display));
		setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		addDisposeListener(this);
		addFocusListener(this);
		addTraverseListener(this);
		addKeyListener(this);
		addMouseWheelListener(this);
		addControlListener(this);
		addPaintListener(this);
	}

	/**
	 * Sets the {@linkplain FileScannerResult} to display.
	 *
	 * @param result the {@linkplain FileScannerResult} to display.
	 */
	public void setResult(@Nullable FileScannerResult result) {
		if (!Objects.equals(this.result, result)) {
			this.result = result;
			this.cachedLayout = null;
			redraw();
		}
	}

	/**
	 * Gets the currently displayed {@linkplain FileScannerResult}.
	 *
	 * @return the currently displayed {@linkplain FileScannerResult} or {@code null} if none has been set.
	 */
	public @Nullable FileScannerResult getResult() {
		return this.result;
	}

	/**
	 * Scrolls to the given position.
	 * <p>
	 * The submitted position is automatically clamped to the range of the currently displayed data.
	 * </p>
	 *
	 * @param position the position to scroll to.
	 */
	public void scrollTo(long position) {
		this.vertical.scrollTo(position >> DATA_LINE_SHIFT);
		redraw();
	}

	@Override
	public void widgetDisposed(DisposeEvent event) {
		if (this.defaultFont != null) {
			this.defaultFont.dispose();
		}
	}

	@Override
	public void keyTraversed(TraverseEvent event) {
		event.doit = true;
	}

	@Override
	public void focusGained(FocusEvent event) {
		redraw();
	}

	@Override
	public void focusLost(FocusEvent event) {
		redraw();
	}

	@Override
	public void keyPressed(KeyEvent event) {
		switch (event.keyCode) {
		case SWT.ARROW_LEFT:
			if ((event.stateMask & SWT.COMMAND) == SWT.COMMAND) {
				this.horizontal.scrollTo(0);
			} else {
				this.horizontal.scrollLines(-1);
			}
			redraw();
			break;
		case SWT.ARROW_RIGHT:
			if ((event.stateMask & SWT.COMMAND) == SWT.COMMAND) {
				this.horizontal.scrollTo(Integer.MAX_VALUE);
			} else {
				this.horizontal.scrollLines(1);
			}
			redraw();
			break;
		case SWT.ARROW_UP:
			if ((event.stateMask & SWT.COMMAND) == SWT.COMMAND) {
				this.vertical.scrollTo(0);
			} else {
				this.vertical.scrollLines(-1);
			}
			redraw();
			break;
		case SWT.ARROW_DOWN:
			if ((event.stateMask & SWT.COMMAND) == SWT.COMMAND) {
				this.vertical.scrollTo(Long.MAX_VALUE);
			} else {
				this.vertical.scrollLines(1);
			}
			redraw();
			break;
		case SWT.PAGE_UP:
			this.vertical.scrollPage(-1);
			redraw();
			break;
		case SWT.PAGE_DOWN:
			this.vertical.scrollPage(1);
			redraw();
			break;
		case SWT.HOME:
			this.vertical.scrollTo(0);
			redraw();
			break;
		case SWT.END:
			this.vertical.scrollTo(Long.MAX_VALUE);
			redraw();
			break;
		default:
			// Nothing to do here
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		// Nothing to do here
	}

	@Override
	public void mouseScrolled(MouseEvent event) {
		this.vertical.scrollLines(event.count);
	}

	@Override
	public void controlMoved(ControlEvent event) {
		// Nothing to do here
	}

	@Override
	public void controlResized(ControlEvent event) {
		Layout checkedLayout = this.cachedLayout;

		if (checkedLayout != null) {
			checkedLayout.resized = true;
			redraw();
		}
	}

	@Override
	public void setFont(@Nullable Font font) {
		this.cachedLayout = null;
		super.setFont(font);
		redraw();
	}

	private static final int DATA_LINE_SHIFT = 4;
	private static final int DATA_LINE_SIZE = 1 << DATA_LINE_SHIFT;
	private static final String DISPLAY_TEMPLATE = "0000000000000000h  00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................";
	private static final int DISPLAY_LINE_LENGTH = DISPLAY_TEMPLATE.length();
	private static final int DISPLAY_LINE_LENGTH1_BASE = 19;
	private static final int DISPLAY_LINE_LENGTH2_BASE = 66;
	private static final int DISPLAY_LINE_LENGTH3_BASE = 68;
	private static final String[] BYTE_HEX_MAP = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08",
			"09", "0a", "0b", "0c", "0d", "0e", "0f", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1a",
			"1b", "1c", "1d", "1e", "1f", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2a", "2b", "2c",
			"2d", "2e", "2f", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3a", "3b", "3c", "3d", "3e",
			"3f", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4a", "4b", "4c", "4d", "4e", "4f", "50",
			"51", "52", "53", "54", "55", "56", "57", "58", "59", "5a", "5b", "5c", "5d", "5e", "5f", "60", "61", "62",
			"63", "64", "65", "66", "67", "68", "69", "6a", "6b", "6c", "6d", "6e", "6f", "70", "71", "72", "73", "74",
			"75", "76", "77", "78", "79", "7a", "7b", "7c", "7d", "7e", "7f", "80", "81", "82", "83", "84", "85", "86",
			"87", "88", "89", "8a", "8b", "8c", "8d", "8e", "8f", "90", "91", "92", "93", "94", "95", "96", "97", "98",
			"99", "9a", "9b", "9c", "9d", "9e", "9f", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "aa",
			"ab", "ac", "ad", "ae", "af", "b0", "b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8", "b9", "ba", "bb", "bc",
			"bd", "be", "bf", "c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "ca", "cb", "cc", "cd", "ce",
			"cf", "d0", "d1", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "da", "db", "dc", "dd", "de", "df", "e0",
			"e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9", "ea", "eb", "ec", "ed", "ee", "ef", "f0", "f1", "f2",
			"f3", "f4", "f5", "f6", "f7", "f8", "f9", "fa", "fb", "fc", "fd", "fe", "ff" };
	private static final char[] BYTE_CHAR_MAP = new char[] { '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
			'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 0x20,
			0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f, 0x30, 0x31, 0x32,
			0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x3f, 0x40, 0x41, 0x42, 0x43, 0x44,
			0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f, 0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56,
			0x57, 0x58, 0x59, 0x5a, 0x5b, 0x5c, 0x5d, 0x5e, 0x5f, 0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
			0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a,
			0x7b, 0x7c, 0x7d, 0x7e, '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.',
			'.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 0xa1, 0xa2, 0xa3, 0xa4,
			0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xab, 0xac, '.', 0xae, 0xaf, 0xb0, 0xb1, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6,
			0xb7, 0xb8, 0xb9, 0xba, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf, 0xc0, 0xc1, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8,
			0xc9, 0xca, 0xcb, 0xcc, 0xcd, 0xce, 0xcf, 0xd0, 0xd1, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda,
			0xdb, 0xdc, 0xdd, 0xde, 0xdf, 0xe0, 0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xeb, 0xec,
			0xed, 0xee, 0xef, 0xf0, 0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa, 0xfb, 0xfc, 0xfd, 0xfe,
			0xff };

	@SuppressWarnings("squid:S3776")
	@Override
	public void paintControl(PaintEvent event) {
		FileScannerResult checkedResult = this.result;

		if (checkedResult != null) {
			Display display = getDisplay();
			Color background = getBackground();
			Color foreground = getForeground();
			Color backgroundSelected;
			Color foregroundSelected;

			if (isFocusControl()) {
				backgroundSelected = display.getSystemColor(SWT.COLOR_LIST_SELECTION);
				foregroundSelected = display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
			} else {
				backgroundSelected = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
				foregroundSelected = display.getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
			}

			try {
				FileScannerInput input = checkedResult.input();
				long inputSize = input.size();
				long selectionStart;
				long selectionEnd;

				if (checkedResult.type() != FileScannerResult.Type.INPUT) {
					selectionStart = checkedResult.start();
					selectionEnd = checkedResult.end();
				} else {
					selectionStart = 0;
					selectionEnd = 0;
				}

				GC gc = Objects.requireNonNull(event.gc);
				Layout layout = calculationAndUpdateLayout(gc, inputSize, selectionStart);

				if (!layout.resized) {
					int skipLineCount = Math.max(((event.y - layout.originY) / layout.scrollUnitY), 0);
					int drawX = layout.originX - this.horizontal.getSelection() * layout.scrollUnitX;
					int drawY = layout.originY + (skipLineCount - 1) * layout.scrollUnitY;
					int drawYLimit = event.y + event.height;
					long dataPosition = (this.vertical.selection() + skipLineCount - 1) << DATA_LINE_SHIFT;
					ByteBuffer dataBuffer = ByteBuffer.allocate(DATA_LINE_SIZE);
					StringBuilder formatBuffer = new StringBuilder(DISPLAY_LINE_LENGTH);

					while (drawY < drawYLimit && dataPosition < inputSize) {
						if (dataPosition >= 0) {
							dataBuffer.clear();
							input.read(dataBuffer, dataPosition);
							dataBuffer.flip();
							formatBuffer.setLength(0);
							formatDisplayLine(formatBuffer, dataPosition, dataBuffer);
							if (selectionEnd <= dataPosition || dataPosition + DATA_LINE_SIZE <= selectionStart) {
								event.gc.drawString(formatBuffer.toString(), drawX, drawY, false);
							} else {
								int offset1 = DISPLAY_LINE_LENGTH1_BASE;
								int offset3 = DISPLAY_LINE_LENGTH3_BASE;

								if (selectionStart > dataPosition) {
									int delta = (int) (selectionStart - dataPosition);

									offset1 += delta * 3;
									offset3 += delta;
								}

								int offset2 = DISPLAY_LINE_LENGTH2_BASE;
								int offset4 = DISPLAY_LINE_LENGTH;

								if (selectionEnd < dataPosition + DATA_LINE_SIZE) {
									int delta = (int) (dataPosition + DATA_LINE_SIZE - selectionEnd);

									offset2 -= delta * 3;
									offset4 -= delta;
								}

								String formatString1 = formatBuffer.substring(0, offset1);
								String formatString2 = formatBuffer.substring(offset1, offset2);
								String formatString3 = formatBuffer.substring(offset2, offset3);
								String formatString4 = formatBuffer.substring(offset3, offset4);
								String formatString5 = formatBuffer.substring(offset4);
								int nextDrawX = drawX;

								event.gc.drawString(formatString1, nextDrawX, drawY, false);
								nextDrawX += event.gc.textExtent(formatString1, SWT.NONE).x;
								event.gc.setBackground(backgroundSelected);
								event.gc.setForeground(foregroundSelected);
								event.gc.drawString(formatString2, nextDrawX, drawY, false);
								nextDrawX += event.gc.textExtent(formatString2, SWT.NONE).x;
								event.gc.setBackground(background);
								event.gc.setForeground(foreground);
								event.gc.drawString(formatString3, nextDrawX, drawY, false);
								nextDrawX += event.gc.textExtent(formatString3, SWT.NONE).x;
								event.gc.setBackground(backgroundSelected);
								event.gc.setForeground(foregroundSelected);
								event.gc.drawString(formatString4, nextDrawX, drawY, false);
								nextDrawX += event.gc.textExtent(formatString4, SWT.NONE).x;
								event.gc.setBackground(background);
								event.gc.setForeground(foreground);
								event.gc.drawString(formatString5, nextDrawX, drawY, false);
							}
						}
						drawY += layout.scrollUnitY;
						dataPosition += DATA_LINE_SIZE;
					}
				}
			} catch (IOException e) {
				Exceptions.warn(e);
			}
		} else {
			calculationAndUpdateLayout(Objects.requireNonNull(event.gc), 0, 0);
		}
	}

	private void formatDisplayLine(StringBuilder buffer, long position, ByteBuffer data) {
		buffer.append(BYTE_HEX_MAP[(int) ((position >> 56) & 0xff)]);
		buffer.append(BYTE_HEX_MAP[(int) ((position >> 48) & 0xff)]);
		buffer.append(BYTE_HEX_MAP[(int) ((position >> 40) & 0xff)]);
		buffer.append(BYTE_HEX_MAP[(int) ((position >> 32) & 0xff)]);
		buffer.append(BYTE_HEX_MAP[(int) ((position >> 24) & 0xff)]);
		buffer.append(BYTE_HEX_MAP[(int) ((position >> 16) & 0xff)]);
		buffer.append(BYTE_HEX_MAP[(int) ((position >> 8) & 0xff)]);
		buffer.append(BYTE_HEX_MAP[(int) (position & 0xff)]);
		buffer.append("h  ");
		data.mark();
		for (int dataIndex = 0; dataIndex < DATA_LINE_SIZE; dataIndex++) {
			if (data.hasRemaining()) {
				buffer.append(BYTE_HEX_MAP[data.get() & 0xff]);
				buffer.append(' ');
			} else {
				buffer.append("   ");
			}
		}
		buffer.append(' ');
		data.reset();
		for (int dataIndex = 0; dataIndex < DATA_LINE_SIZE; dataIndex++) {
			if (data.hasRemaining()) {
				buffer.append(BYTE_CHAR_MAP[data.get() & 0xff]);
			} else {
				buffer.append(' ');
			}
		}
	}

	private static final String FONT_NAME_COURIER_NEW = "Courier New";
	private static final String FONT_NAME_MONACO = "Monaco";

	private Font getDefaultFont(Display display) {
		Font checkedFont = this.defaultFont;

		if (checkedFont == null) {
			FontData defaultFontData;

			if (Platform.IS_LINUX) {
				defaultFontData = new FontData(FONT_NAME_COURIER_NEW, 11, SWT.NORMAL);
			} else if (Platform.IS_MACOS) {
				defaultFontData = new FontData(FONT_NAME_MONACO, 11, SWT.NORMAL);
			} else if (Platform.IS_WINDOWS) {
				defaultFontData = new FontData(FONT_NAME_COURIER_NEW, 11, SWT.NORMAL);
			} else {
				defaultFontData = new FontData(FONT_NAME_COURIER_NEW, 11, SWT.NORMAL);
			}
			checkedFont = this.defaultFont = new Font(display, defaultFontData);
		}
		return checkedFont;
	}

	private Layout calculationAndUpdateLayout(GC gc, long inputSize, long defaultPosition) {
		Layout layout = this.cachedLayout;

		if (layout == null) {
			layout = this.cachedLayout = new Layout(gc, DISPLAY_TEMPLATE);
			updateScrollBars(layout, inputSize, defaultPosition >> DATA_LINE_SHIFT);
		} else if (layout.resized) {
			updateScrollBars(layout, inputSize, -1);
		}
		return layout;
	}

	@SuppressWarnings("squid:S3776")
	private void updateScrollBars(Layout layout, long inputSize, long verticalSelection) {
		long inputLines = ((inputSize + DATA_LINE_SIZE - 1) >> DATA_LINE_SHIFT);
		int verticalDisplayUnits;
		int horizontalDisplayUnits;
		boolean reresized;

		layout.resized = false;
		do {
			reresized = false;

			Rectangle clientArea = getClientArea();

			horizontalDisplayUnits = Math.max((clientArea.width - layout.originX) / layout.scrollUnitX, 1);
			if (inputSize == 0 || DISPLAY_LINE_LENGTH <= horizontalDisplayUnits) {
				if (this.horizontal.isVisible()) {
					this.horizontal.setVisible(false);
					layout.resized = reresized = true;
				}
			} else if (!this.horizontal.isVisible()) {
				this.horizontal.setVisible(true);
				layout.resized = reresized = true;
			}
			verticalDisplayUnits = Math.max((clientArea.height - layout.originY) / layout.scrollUnitY, 1);
			if (inputSize == 0 || inputLines <= verticalDisplayUnits) {
				if (this.vertical.isVisible()) {
					this.vertical.setVisible(false);
					layout.resized = reresized = true;
				}
			} else if (!this.vertical.isVisible()) {
				this.vertical.setVisible(true);
				layout.resized = reresized = true;
			}
		} while (reresized);
		if (!layout.resized) {
			if (inputSize > 0 && DISPLAY_LINE_LENGTH > horizontalDisplayUnits) {
				this.horizontal.layout(DISPLAY_LINE_LENGTH, horizontalDisplayUnits);
			} else {
				this.horizontal.layout(0, 1);
			}
			if (inputSize > 0 && inputLines > verticalDisplayUnits) {
				this.vertical.layout(inputLines, verticalDisplayUnits);
				if (verticalSelection >= 0) {
					this.vertical.scrollTo(verticalSelection);
				}
			} else {
				this.vertical.layout(0, 1);
			}
		} else {
			redraw();
		}
	}

	private static class Layout {

		final int originX;
		final int originY;
		final int scrollUnitX;
		final int scrollUnitY;
		boolean resized = true;

		Layout(GC gc, String template) {
			this.originX = 3;
			this.originY = 3;

			Point extent = gc.textExtent(template, SWT.NONE);
			int extentUnits = template.length();

			this.scrollUnitX = (extent.x + extentUnits - 1) / extentUnits;
			this.scrollUnitY = extent.y;
		}

	}

}
