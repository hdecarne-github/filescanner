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
package de.carne.jfx.beans;

import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;

/**
 * This class provides {@code ReaonlyObjectProperty} which is backed up by
 * {@code ObjectProperty} of another type.
 *
 * @param <T> The source type.
 * @param <R> The read-only type.
 */
public class TransformationReadOnlyObjectProperty<T, R> extends ReadOnlyObjectPropertyBase<R> {

	private final ObjectProperty<T> source;
	private final Function<T, R> transformation;
	private boolean valid = true;

	/**
	 * Construct {@code TransformedReadOnlyObjectProperty}.
	 *
	 * @param source The source property.
	 * @param transformation The transformation function.
	 */
	public TransformationReadOnlyObjectProperty(ObjectProperty<T> source, Function<T, R> transformation) {
		assert source != null;
		assert transformation != null;

		this.source = source;
		this.transformation = transformation;
		this.source.addListener(new WeakInvalidationListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				sourceInvalidated(observable);
			}

		}));
	}

	void sourceInvalidated(Observable observable) {
		if (this.valid) {
			this.valid = false;
			fireValueChangedEvent();
		}
	}

	@Override
	public Object getBean() {
		return null;
	}

	@Override
	public String getName() {
		return this.source.getName();
	}

	@Override
	public R get() {
		this.valid = true;
		return this.transformation.apply(this.source.get());
	}

}
