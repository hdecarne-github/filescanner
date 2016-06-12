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
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import de.carne.filescanner.core.FileScannerInput;
import de.carne.filescanner.core.FileScannerResult;
import de.carne.nio.compression.IncompleteReadException;

/**
 * {@linkplain StreamHandler} implementation backed by a combination of mapped
 * data sources.
 */
public class MappingStreamHandler implements StreamHandler {

	private abstract class Mapping {

		private final long start;

		private final long end;

		protected Mapping(long start, long end) {
			this.start = start;
			this.end = end;
		}

		public final long start() {
			return this.start;
		}

		public final long end() {
			return this.end;
		}

		public abstract int read(long position, byte[] b, int off, int len) throws IOException;

	}

	private class FileScannerInputMapping extends Mapping {

		private final FileScannerInput input;

		private final long inputStart;

		public FileScannerInputMapping(long start, long end, FileScannerInput input, long inputStart) {
			super(start, end);
			this.input = input;
			this.inputStart = inputStart;
		}

		@Override
		public int read(long position, byte[] b, int off, int len) throws IOException {
			ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
			long readPosition = this.inputStart + position - start();

			return this.input.read(buffer, readPosition);
		}

	}

	private final ArrayList<Mapping> mappings = new ArrayList<>();

	/**
	 * Map a data section of a scanner input.
	 *
	 * @param input The scanner input to map.
	 * @param start The start position in the input to map.
	 * @param end The end position in the input to map.
	 * @return The updated mapping.
	 */
	public MappingStreamHandler mapInputSection(FileScannerInput input, long start, long end) {
		assert input != null;
		assert start >= 0;
		assert end >= start;

		if (start < end) {
			int mappingCount = this.mappings.size();
			long mappingStart = (mappingCount > 0 ? this.mappings.get(mappingCount - 1).end() : 0L);
			long mappingEnd = mappingStart + end - start;

			this.mappings.add(new FileScannerInputMapping(mappingStart, mappingEnd, input, start));
		}
		return this;
	}

	/**
	 * Map a scanner result's data.
	 *
	 * @param result The scanner result to map.
	 * @return The updated mapping.
	 */
	public MappingStreamHandler mapResult(FileScannerResult result) {
		assert result != null;

		return mapInputSection(result.input(), result.start(), result.end());
	}

	@Override
	public long size() {
		return available(0l);
	}

	@Override
	public InputStream open() throws IOException {
		return new InputStream() {

			private long position = 0L;

			private long mark = -1L;

			@Override
			public int read() throws IOException {
				int read = MappingStreamHandler.this.read(this.position);

				if (read >= 0) {
					this.position++;
				}
				return read;
			}

			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				int read = MappingStreamHandler.this.read(this.position, b, off, len);

				if (read > 0) {
					this.position += read;
				}
				return read;
			}

			@Override
			public long skip(long n) throws IOException {
				assert n >= 0;

				this.position += n;
				return n;
			}

			@Override
			public int available() throws IOException {
				return (int) Math.min(MappingStreamHandler.this.available(this.position), Integer.MAX_VALUE);
			}

			@Override
			public synchronized void mark(int readlimit) {
				this.mark = this.position;
			}

			@Override
			public synchronized void reset() throws IOException {
				if (this.mark < 0) {
					throw new IOException("Invalid mark: " + this.mark);
				}
				this.position = this.mark;
			}

			@Override
			public boolean markSupported() {
				return true;
			}

		};
	}

	int read(long position) throws IOException {
		byte[] buffer = new byte[1];

		return (read(position, buffer, 0, 1) == 1 ? buffer[0] : -1);
	}

	int read(long position, byte[] b, int off, int len) throws IOException {
		int mappingCount = this.mappings.size();
		long streamEnd = (mappingCount > 0 ? this.mappings.get(mappingCount - 1).end() : 0L);
		int read = -1;

		if (position < streamEnd) {
			read = 0;
			for (Mapping mapping : this.mappings) {
				if (read >= len) {
					break;
				}
				if (position < mapping.start()) {
					continue;
				}
				if (mapping.end() <= position) {
					break;
				}

				long mappingReadPosition = position + read;
				int mappingReadLen = (int) Math.min(len - read, mapping.end() - mappingReadPosition);
				int mappingRead = mapping.read(mappingReadPosition, b, off + read, mappingReadLen);

				if (mappingRead != mappingReadLen) {
					throw new IncompleteReadException(mappingReadLen, mappingRead);
				}
				read += mappingRead;
			}
		}
		return read;
	}

	long available(long position) {
		int mappingCount = this.mappings.size();
		long streamEnd = (mappingCount > 0 ? this.mappings.get(mappingCount - 1).end() : 0L);

		return Math.max(streamEnd - position, 0L);
	}

}
