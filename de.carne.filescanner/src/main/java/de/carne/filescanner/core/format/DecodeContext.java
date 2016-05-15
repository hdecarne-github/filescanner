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

import java.util.HashMap;

/**
 *
 */
public class DecodeContext {

	public static class Attribute<T> {

	}

	private final HashMap<Object, Object> context = new HashMap<>();

	private final DecodeContext parent;

	public DecodeContext(DecodeContext parent) {
		this.parent = parent;
	}

	public <T> void setAttribute(Attribute<T> attribute, T value) {
		this.context.put(attribute, value);
	}

}
