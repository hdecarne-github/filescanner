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
package de.carne.filescanner.core.format;

import java.util.function.Supplier;

/**
 *
 */
public class ValueExpression<T> {

	private final T staticValue;

	private final Supplier<T> dynamicValue;

	private final T noneValue;

	public ValueExpression(T staticValue, T noneValue) {
		assert staticValue != null;
		assert noneValue != null;

		this.staticValue = staticValue;
		this.dynamicValue = null;
		this.noneValue = noneValue;
	}

	public ValueExpression(Supplier<T> dynamicValue, T noneValue) {
		assert dynamicValue != null;
		assert noneValue != null;

		this.staticValue = null;
		this.dynamicValue = dynamicValue;
		this.noneValue = noneValue;
	}

	public T beforeEval() {
		return (this.staticValue != null ? this.staticValue : this.noneValue);
	}

	public T afterEval() {
		return (this.staticValue != null ? this.staticValue : this.dynamicValue.get());
	}

}
