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

import java.text.MessageFormat;
import java.util.function.Supplier;

/**
 * A {@linkplain String} based {@linkplain ValueExpression}.
 */
public class StringExpression extends ValueExpression<String> {

	/**
	 * Construct {@code StringExpression} with static value.
	 *
	 * @param staticValue This expressions static value.
	 */
	public StringExpression(String staticValue) {
		super(staticValue);
	}

	/**
	 * Construct {@code NumberExpression} with dynamic value.
	 *
	 * @param valueLambda The Lambda expression providing the value.
	 */
	public StringExpression(Supplier<String> valueLambda) {
		super(valueLambda);
	}

	/**
	 * Construct {@code NumberExpression} with dynamic value.
	 *
	 * @param pattern The {@linkplain MessageFormat} format pattern to apply to
	 *        the Lambda expression result.
	 * @param lambdaValue The Lambda expression supplying the value.
	 */
	public StringExpression(String pattern, Supplier<String> lambdaValue) {
		super(new Supplier<String>() {

			@Override
			public String get() {
				return MessageFormat.format(pattern, lambdaValue.get());
			}

		});
	}

}
