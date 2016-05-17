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
package de.carne.filescanner.core.transfer;

import java.io.IOException;

/**
 * {@code HtmlResultRenderer} writing it's output to a string buffer while
 * obeying to a time limit.
 */
class BufferHtmlResultRenderer extends HtmlResultRenderer {

	private final long timeLimit;

	private final StringBuilder buffer = new StringBuilder();

	BufferHtmlResultRenderer(HtmlResultRendererURLHandler urlHandler, int fastTimeout) {
		super(urlHandler);
		this.timeLimit = System.currentTimeMillis() + fastTimeout;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.transfer.HtmlResultRenderer#write(java.lang.
	 * String[])
	 */
	@Override
	protected void write(String... artefacts) throws IOException, InterruptedException {
		if (this.timeLimit < System.currentTimeMillis()) {
			throw new InterruptedException();
		}
		for (String artefact : artefacts) {
			this.buffer.append(artefact);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.buffer.toString();
	}

}
