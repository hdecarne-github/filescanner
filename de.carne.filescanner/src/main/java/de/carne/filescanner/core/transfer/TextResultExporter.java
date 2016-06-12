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

import de.carne.filescanner.core.FileScannerResult;

/**
 * Base class for text exporter.
 */
public abstract class TextResultExporter extends ResultExporter {

	public static final class Text {

		private final String plainText;

		private final String rtfText;

		Text(String plainText, String rtfText) {
			assert plainText != null || rtfText != null;

			this.plainText = plainText;
			this.rtfText = rtfText;
		}

		public boolean isPlainText() {
			return this.plainText != null;
		}

		public String getPlainText() {
			return this.plainText;
		}

		public boolean isRtfText() {
			return this.rtfText != null;
		}

		public String getRtfText() {
			return this.rtfText;
		}

	}

	protected TextResultExporter(String name) {
		super(name);
	}

	public abstract Text export(FileScannerResult result);

}
