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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import de.carne.filescanner.core.format.ResultAttribute;
import de.carne.filescanner.core.format.ResultContext;

/**
 * This class defines basic format spec attributes.
 * <p>
 * Basic attributes are of the form &lt;name&gt; = &lt;value&gt; where value is
 * a reasonable simple type. Attributes can be bound to a
 * {@linkplain ResultContext} and hence evaluated during decode or render phase.
 * </p>
 *
 * @param <T> The attribute' data type.
 */
public abstract class Attribute<T> extends FormatSpec implements ResultAttribute<T> {

	private final String name;

	private boolean bound = false;

	private final ArrayList<Function<T, Boolean>> validators = new ArrayList<>();

	private final ArrayList<AttributeRenderer<T>> extraRendererList = new ArrayList<>();

	/**
	 * Construct {@code Attribute}.
	 *
	 * @param name The attribute's name.
	 */
	protected Attribute(String name) {
		assert name != null;

		this.name = name;
	}

	/**
	 * Mark this attribute as locally bound.
	 *
	 * @return The updated data attribute spec.
	 */
	public final Attribute<T> bind() {
		this.bound = true;
		return this;
	}

	/**
	 * Check whether this attribute is bound.
	 *
	 * @return [@code true} if the attribute is bound.
	 */
	public final boolean isBound() {
		return this.bound;
	}

	/**
	 * Bind the attribute value.
	 *
	 * @param value The value to bind.
	 */
	protected final void bindValue(T value) {
		ResultContext.get().setAttribute(this, value);
	}

	/**
	 * Add an validator for this attribute.
	 *
	 * @param validator The validator to add.
	 * @return The updated data attribute spec.
	 */
	public final Attribute<T> addValidator(Function<T, Boolean> validator) {
		assert validator != null;

		this.validators.add(validator);
		return this;
	}

	/**
	 * Add a valid value for this attribute.
	 *
	 * @param validValue The valid value to add.
	 * @return The updated data attribute spec.
	 */
	public final Attribute<T> addValidValue(T validValue) {
		assert validValue != null;

		Function<T, Boolean> validator;

		if (validValue.getClass().isArray()) {
			validator = v -> Arrays.equals((Object[]) v, (Object[]) validValue);
		} else {
			validator = v -> v.equals(validValue);
		}
		return addValidator(validator);
	}

	/**
	 * Add a set of valid values for this attribute.
	 *
	 * @param validValues The valid values to add.
	 * @return The updated data attribute spec.
	 */
	public final Attribute<T> addValidValues(Set<T> validValues) {
		return addValidator(v -> validValues.size() == 0 || validValues.contains(v));
	}

	/**
	 * Check whether any validators are defined for this attribute.
	 *
	 * @return {@code true} if at least one validator has been defined for this
	 *         attribute.
	 */
	protected boolean hasValidators() {
		return !this.validators.isEmpty();
	}

	/**
	 * Evaluate the validators defined for this attribute.
	 *
	 * @param value The attribute value to validate.
	 * @return {@code true} if all validators accept the value or if no
	 *         validator has been added yet.
	 */
	protected boolean validateValue(T value) {
		boolean valid = true;

		for (Function<T, Boolean> validator : this.validators) {
			Boolean validatorResult = validator.apply(value);

			if (!validatorResult.booleanValue()) {
				valid = false;
				break;
			}
		}
		return valid;
	}

	/**
	 * Add an extra {@linkplain AttributeRenderer} for this attribute.
	 *
	 * @param extraRenderer The renderer to add.
	 * @return The updated data attribute spec.
	 */
	public final Attribute<T> addExtraRenderer(AttributeRenderer<T> extraRenderer) {
		assert extraRenderer != null;

		this.extraRendererList.add(extraRenderer);
		return this;
	}

	/**
	 * Get the registered extra {@linkplain AttributeRenderer} for this
	 * attribute.
	 *
	 * @return The registered extra {@linkplain AttributeRenderer} for this
	 *         attribute.
	 */
	protected final List<AttributeRenderer<T>> getExtraRenderer() {
		return Collections.unmodifiableList(this.extraRendererList);
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.format.ResultAttribute#name()
	 */
	@Override
	public final String name() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.function.Supplier#get()
	 */
	@Override
	public T get() {
		assert this.bound;

		return ResultContext.get().getAttribute(this);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Attribute '" + this.name + "'";
	}

}
