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

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;

/**
 * Custom control for display raw byte data from a {@code FileChannel} object.
 */
public class FileView extends Control {

	private static final String USER_AGENT_STYLESHEET = FileView.class
			.getResource(FileView.class.getSimpleName() + ".css").toExternalForm();

	/**
	 * Construct {@code FileView}.
	 */
	public FileView() {
		getStyleClass().add("file-view");
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.scene.control.Control#createDefaultSkin()
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new FileViewSkin(this);
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.scene.layout.Region#getUserAgentStylesheet()
	 */
	@Override
	public String getUserAgentStylesheet() {
		return USER_AGENT_STYLESHEET;
	}

	private final SimpleObjectProperty<FileViewType> viewTypeProperty = new SimpleObjectProperty<>(
			FileViewType.HEXADECIMAL_U);

	/**
	 * Get the view type.
	 *
	 * @return The view type.
	 */
	public FileViewType getViewType() {
		return this.viewTypeProperty.get();
	}

	/**
	 * Set the view type.
	 *
	 * @param viewType The view type to set.
	 */
	public void setViewType(FileViewType viewType) {
		this.viewTypeProperty.set(viewType != null ? viewType : FileViewType.HEXADECIMAL_U);
	}

	/**
	 * Get the view type property.
	 *
	 * @return The view type property.
	 */
	public ObjectProperty<FileViewType> viewTypeProperty() {
		return this.viewTypeProperty;
	}

	private final SimpleObjectProperty<FileAccess> fileProperty = new SimpleObjectProperty<>(null);

	/**
	 * Get the displayed file.
	 *
	 * @return The displayed file.
	 */
	public FileAccess getFile() {
		return this.fileProperty.get();
	}

	/**
	 * Set the file to display.
	 *
	 * @param file The file to display.
	 */
	public void setFile(FileAccess file) {
		this.fileProperty.set(file);
		setPosition(null);
	}

	/**
	 * Get the file property.
	 *
	 * @return The file property.
	 */
	public ObjectProperty<FileAccess> fileProperty() {
		return this.fileProperty;
	}

	private final SimpleLongProperty positionProperty = new SimpleLongProperty(0l);

	/**
	 * Get the displayed position.
	 *
	 * @return The displayed position.
	 */
	public Long getPosition() {
		return this.positionProperty.get();
	}

	/**
	 * Set the displayed position.
	 *
	 * @param position The position to display.
	 */
	public void setPosition(Long position) {
		this.positionProperty.set(position != null ? position : Long.valueOf(0l));
	}

	/**
	 * Get the position property.
	 *
	 * @return The position property.
	 */
	public LongProperty positionProperty() {
		return this.positionProperty;
	}

	private final SimpleObjectProperty<PositionRange> selectionProperty = new SimpleObjectProperty<>(
			new PositionRange());

	/**
	 * Get the selection range.
	 *
	 * @return The selection range.
	 */
	public PositionRange getSelection() {
		return this.selectionProperty.get();
	}

	/**
	 * Set the selection range.
	 *
	 * @param selection The selection range to set.
	 */
	public void setSelection(PositionRange selection) {
		this.selectionProperty.set(new PositionRange(selection));
	}

	/**
	 * Get the selection property.
	 *
	 * @return The selection property.
	 */
	public ObjectProperty<PositionRange> selectionProperty() {
		return this.selectionProperty;
	}

	private static final Font DEFAULT_MONOSPACED_FONT = new Font("Courier New", 14.0);

	private final SimpleObjectProperty<Font> fontProperty = new SimpleObjectProperty<>(DEFAULT_MONOSPACED_FONT);

	/**
	 * Get the display font.
	 *
	 * @return The display font.
	 */
	public Font getFont() {
		return this.fontProperty.get();
	}

	/**
	 * Set the display font.
	 *
	 * @param font The display font.
	 */
	public void setFont(Font font) {
		this.fontProperty.set(font != null ? font : DEFAULT_MONOSPACED_FONT);
	}

	/**
	 * Get the font property.
	 *
	 * @return The font property.
	 */
	public ObjectProperty<Font> fontProperty() {
		return this.fontProperty;
	}

}
