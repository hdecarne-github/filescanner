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
 * A {@linkplain Number} based {@linkplain ValueExpression}.
 *
 * @param <T> The expressed number type.
 */
public class NumberExpression<T extends Number> extends ValueExpression<T> {

	/**
	 * Construct {@code NumberExpression} with static value.
	 *
	 * @param value This expressions static value.
	 */
	public NumberExpression(T value) {
		super(value);
	}

	/**
	 * Construct {@code NumberExpression} with dynamic value.
	 *
	 * @param valueLambda The Lambda expression providing the value.
	 */
	public NumberExpression(Supplier<T> valueLambda) {
		super(valueLambda);
	}

}
