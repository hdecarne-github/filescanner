/*
 * Copyright (c) 2007-2020 Holger de Carne and contributors, All Rights Reserved.
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.swt.dnd.Clipboard;

import de.carne.boot.Exceptions;

abstract class ClipboardContentHolder {

	public static final ClipboardContentHolder KEEP = new ClipboardContentHolder() {

		@Override
		public ClipboardContentHolder detach(Clipboard clipboard) {
			return this;
		}

		@Override
		public ClipboardContentHolder dispose(Clipboard clipboard) {
			return this;
		}

	};

	public static final ClipboardContentHolder DISPOSE_ON_DETACH = new ClipboardContentHolder() {

		@Override
		public ClipboardContentHolder detach(Clipboard clipboard) {
			return dispose(clipboard);
		}

		@Override
		public ClipboardContentHolder dispose(Clipboard clipboard) {
			clipboard.clearContents();
			return KEEP;
		}

	};

	public static ClipboardContentHolder deleteFile(Path file) {
		return new ClipboardContentHolder() {

			@Override
			public ClipboardContentHolder detach(Clipboard clipboard) {
				return this;
			}

			@Override
			public ClipboardContentHolder dispose(Clipboard clipboard) {
				clipboard.clearContents();
				try {
					Files.delete(file);
				} catch (IOException e) {
					Exceptions.ignore(e);
				}
				return KEEP;
			}
		};
	}

	public abstract ClipboardContentHolder detach(Clipboard clipboard);

	public abstract ClipboardContentHolder dispose(Clipboard clipboard);

}
