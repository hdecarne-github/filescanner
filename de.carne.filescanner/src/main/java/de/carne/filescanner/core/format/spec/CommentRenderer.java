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

import java.io.IOException;
import java.util.function.Function;

import de.carne.filescanner.core.transfer.ResultRenderer;

/**
 * Simple attribute renderer which adds a comment part to the output.
 * <p>
 * The necessary attribute value to string conversion has to be defined via a
 * Lambda expression.
 * </p>
 *
 * @param <T> The attribute' data type.
 */
public class CommentRenderer<T> extends AttributeRenderer<T> {

	private final Function<T, String> function;

	/**
	 * Construct {@code CommentRenderer}.
	 *
	 * @param function The function to use for value to string conversion.
	 */
	public CommentRenderer(Function<T, String> function) {
		assert function != null;

		this.function = function;
	}

	@Override
	public void render(T value, ResultRenderer renderer) throws IOException, InterruptedException {
		String valueString = this.function.apply(value);

		if (valueString != null) {
			renderer.setCommentMode().renderText(" // ").renderText(valueString);
		}
	}

}
