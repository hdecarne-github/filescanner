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
package de.carne.filescanner.core.format.spec;

import java.util.function.Supplier;

/**
 * This class defines a value to be evaluated during format spec interpretation.
 * <p>
 * The actual value can either be defined statically or by an lambda expression
 * (of type {@linkplain Supplier}).
 * </p>
 *
 * @param <T> The expressed value type.
 */
public class ValueExpression<T> {

	private final T value;

	private final Supplier<T> valueLambda;

	/**
	 * Construct {@code ValueExpression} with static value.
	 *
	 * @param value This expressions static value.
	 */
	public ValueExpression(T value) {
		assert value != null;

		this.value = value;
		this.valueLambda = null;
	}

	/**
	 * Construct {@code ValueExpression} with dynamic value.
	 *
	 * @param valueLambda The Lambda expression supplying the value.
	 */
	public ValueExpression(Supplier<T> valueLambda) {
		assert valueLambda != null;

		this.value = null;
		this.valueLambda = valueLambda;
	}

	/**
	 * Get this expression's value if the decode phase has not yet been began.
	 * <p>
	 * For static expressions this function already returns the defined value.
	 * For a lambda based expression this functions returns {@code null}.
	 * </p>
	 *
	 * @return The expression's value or {@code null} if the value is not yet
	 *         defined.
	 */
	public T beforeDecode() {
		return (this.value != null ? this.value : null);
	}

	/**
	 * Get this expression's value during or after the decode phase.
	 *
	 * @return The expression's value.
	 */
	public T decode() {
		return (this.value != null ? this.value : this.valueLambda.get());
	}

}
