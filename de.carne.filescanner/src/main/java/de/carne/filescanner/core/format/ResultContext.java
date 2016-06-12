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

import java.util.ArrayList;
import java.util.HashMap;

import de.carne.filescanner.core.format.spec.Attribute;
import de.carne.util.logging.Log;

/**
 * This class keeps track of all context specific information of a scanner
 * result.
 */
public abstract class ResultContext {

	private static final Log LOG = new Log(ResultContext.class);

	private final HashMap<ResultAttribute<?>, Object> resultAttributes = new HashMap<>();

	private final ArrayList<ResultSection> resultSections = new ArrayList<>();

	/**
	 * Get the parent context.
	 *
	 * @return The parent context or {@code null} if there is none.
	 */
	protected abstract ResultContext parent();

	/**
	 * Add all result information from another context.
	 *
	 * @param context The context to copy the information from.
	 */
	protected final void contextAddResults(ResultContext context) {
		this.resultAttributes.putAll(context.resultAttributes);
		this.resultSections.addAll(context.resultSections);
	}

	/**
	 * Declare a context attribute.
	 *
	 * @param attribute The attribute to declare.
	 */
	protected final <T> void contextDeclareAttribute(ResultAttribute<T> attribute) {
		assert attribute != null;

		this.resultAttributes.put(attribute, null);
	}

	/**
	 * Set a context attribute value.
	 *
	 * @param attribute The attribute to set.
	 * @param value The attribute value to set.
	 */
	protected final <T> void contextSetAttribute(ResultAttribute<T> attribute, T value) {
		assert attribute != null;

		ResultContext currentContext = this;

		while (currentContext != null && !currentContext.resultAttributes.containsKey(attribute)) {
			currentContext = currentContext.parent();
		}
		if (currentContext == null) {
			LOG.debug(null, "Declare context attribute ''{0}'' = {1}", attribute.name(), value);
			currentContext = this;
		} else {
			LOG.debug(null, "Set context attribute ''{0}'' = {1}", attribute.name(), value);
		}
		currentContext.resultAttributes.put(attribute, value);
	}

	/**
	 * Get a context attribute value.
	 *
	 * @param attribute The attribute to get.
	 * @return The set attribute value or {@code null} if none has been set.
	 */
	protected final <T> T contextGetAttribute(Attribute<T> attribute) {
		assert attribute != null;

		ResultContext currentContext = this;
		Object value = null;

		while (value == null && currentContext != null) {
			value = currentContext.resultAttributes.get(attribute);
			currentContext = currentContext.parent();
		}
		return attribute.getValueType().cast(value);
	}

	/**
	 * Record a result section for later rendering.
	 *
	 * @param size The size of the result section.
	 * @param renderable The {@linkplain RenderableData} to use for rendering.
	 */
	protected final void contextRecordResultSection(long size, RenderableData renderable) {
		assert size >= 0L;
		assert renderable != null;

		this.resultSections.add(new ResultSection(size, renderable));
	}

	/**
	 * Get a previously recorded result section.
	 *
	 * @param index The index of the result section to retrieve.
	 * @return The result section object or {@code null} if the submitted index
	 *         has not been recorded.
	 * @see #recordResultSection(long, RenderableData)
	 */
	protected final ResultSection contextGetResultSection(int index) {
		return (index < this.resultSections.size() ? this.resultSections.get(index) : null);
	}

}
