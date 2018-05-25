/*
 * Copyright (c) 2007-2018 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.swt.main;

import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.AttributeFactory;

class SearchIndexTokenizer extends CharTokenizer {

	public SearchIndexTokenizer() {
		// Nothing to do here
	}

	public SearchIndexTokenizer(AttributeFactory factory) {
		super(factory);
	}

	public SearchIndexTokenizer(AttributeFactory factory, int maxTokenLen) {
		super(factory, maxTokenLen);
	}

	@Override
	protected boolean isTokenChar(int c) {
		return !Character.isWhitespace(c);
	}

	@Override
	protected int normalize(int c) {
		return Character.toLowerCase(c);
	}

}
