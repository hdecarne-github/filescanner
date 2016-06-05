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
package de.carne.filescanner.jfx.preferences;

import de.carne.filescanner.spi.Format;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Model class for active format selection.
 */
public final class EnabledFormatModel {

	private final SimpleObjectProperty<Format> formatProperty = new SimpleObjectProperty<>();

	private final SimpleBooleanProperty enabledProperty = new SimpleBooleanProperty();

	/**
	 * Construct {@code ActiveFormatModel}.
	 *
	 * @param format The format.
	 * @param enabled The format's enabled flag.
	 */
	public EnabledFormatModel(Format format, boolean enabled) {
		this.formatProperty.set(format);
		this.enabledProperty.set(enabled);
	}

	/**
	 * Get the format property.
	 *
	 * @return The format property.
	 */
	public ObjectProperty<Format> formatProperty() {
		return this.formatProperty;
	}

	/**
	 * Get the format's enabled flag property.
	 *
	 * @return The format's enabled flag property.
	 */
	public BooleanProperty enabledProperty() {
		return this.enabledProperty;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.formatProperty.get().name();
	}

}
