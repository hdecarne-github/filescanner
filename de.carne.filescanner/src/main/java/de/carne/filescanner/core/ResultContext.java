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
package de.carne.filescanner.core;

import java.util.HashMap;
import java.util.Map;

import de.carne.filescanner.core.format.Attribute;
import de.carne.util.logging.Log;

/**
 * This class keeps track of all context specific information of a scanner
 * result.
 */
public abstract class ResultContext {

	private static final Log LOG = new Log(ResultContext.class);

	private final HashMap<Attribute<?>, Object> contextAttributes = new HashMap<>();

	/**
	 * Get the parent context.
	 *
	 * @return The parent context or {@code null} if there is none.
	 */
	protected abstract ResultContext parent();

	/**
	 * Add all result scoped attributes from another context.
	 *
	 * @param context The context to add the result scoped attributes from.
	 */
	public void addResultAttributes(ResultContext context) {
		for (Map.Entry<Attribute<?>, Object> contextEntry : context.contextAttributes.entrySet()) {
			Attribute<?> attribute = contextEntry.getKey();

			if (attribute.bindScope() == Attribute.BindScope.RESULT) {
				this.contextAttributes.put(attribute, contextEntry.getValue());
			}
		}
	}

	/**
	 * Set a context attribute value.
	 *
	 * @param attribute The attribute to set.
	 * @param value The attribute value to set.
	 */
	public <T> void setAttribute(Attribute<T> attribute, T value) {
		assert attribute != null;

		ResultContext currentContext = this;

		while (currentContext != null && !currentContext.contextAttributes.containsKey(attribute)) {
			currentContext = currentContext.parent();
		}
		if (currentContext == null) {
			LOG.debug(null, "New context attribute ''{0}'' = {1}", attribute.name(), value);
			currentContext = this;
		}
		currentContext.contextAttributes.put(attribute, value);
	}

	/**
	 * Get a context attribute value.
	 *
	 * @param attribute The attribute to get.
	 * @return The set attribute value or {@code null} if none has been set.
	 */
	public <T> T getAttribute(Attribute<T> attribute) {
		assert attribute != null;

		ResultContext currentContext = this;
		Object value = null;

		while (value == null && currentContext != null) {
			value = this.contextAttributes.get(attribute);
			currentContext = currentContext.parent();
		}
		return attribute.getValueType().cast(value);
	}

}
