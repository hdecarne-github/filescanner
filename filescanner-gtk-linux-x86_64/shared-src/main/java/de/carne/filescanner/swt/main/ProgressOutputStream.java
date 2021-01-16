/*
 * Copyright (c) 2007-2021 Holger de Carne and contributors, All Rights Reserved.
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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.jdt.annotation.Nullable;

class ProgressOutputStream extends FilterOutputStream {

	private final ProgressCallback progress;

	ProgressOutputStream(ProgressCallback progress, OutputStream out) {
		super(out);
		this.progress = progress;
	}

	@Override
	public void write(int b) throws IOException {
		this.out.write(b);
		if (!this.progress.addProgress(1)) {
			throw new StoppedException();
		}
	}

	@Override
	public void write(byte @Nullable [] b) throws IOException {
		this.out.write(b);
		if (!this.progress.addProgress(b != null ? b.length : 0)) {
			throw new StoppedException();
		}
	}

	@Override
	public void write(byte @Nullable [] b, int off, int len) throws IOException {
		this.out.write(b, off, len);
		if (!this.progress.addProgress(len)) {
			throw new StoppedException();
		}
	}

	@Override
	public String toString() {
		return this.out.toString();
	}

}
