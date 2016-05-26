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
package de.carne.filescanner.spi;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;

import de.carne.filescanner.core.FileScanner;
import de.carne.filescanner.util.Units;
import de.carne.util.logging.Log;

/**
 * Base class for all kinds of scanner inputs.
 */
public abstract class FileScannerInput implements Closeable {

	private static final Log LOG = new Log(FileScannerInput.class);

	private static final String PROPERTY_PACKAGE = FileScanner.class.getPackage().getName();

	private static final String CACHE_ALIGNMENT_PROPERTY = PROPERTY_PACKAGE + ".cacheAlignment";

	/**
	 * The cache alignment used for
	 * {@linkplain #cachedRead(long, int, ByteOrder)} calls:
	 */
	public static final int CACHE_ALIGNMENT;

	static {
		String cacheAlignmentProperty = System.getProperty(CACHE_ALIGNMENT_PROPERTY);
		int cacheAlignment = 0;

		if (cacheAlignmentProperty != null) {
			try {
				cacheAlignment = Integer.parseUnsignedInt(cacheAlignmentProperty, 16);
			} catch (NumberFormatException e) {
				// ignore
			}
			if (cacheAlignment <= 0 || (cacheAlignment & 0xfff) != 0) {
				LOG.warning(null, "Ignoring invalid {0} value: ''{1}''", CACHE_ALIGNMENT_PROPERTY,
						cacheAlignmentProperty);
				cacheAlignment = 0;
			}
		}
		if (cacheAlignment == 0L) {
			cacheAlignment = 0x1000;
		}
		LOG.notice(null, "Using cache alignment: {0}h ({1})", Integer.toString(cacheAlignment, 16),
				Units.formatByteValue(cacheAlignment));
		CACHE_ALIGNMENT = cacheAlignment;
	}

	private static final String CACHE_SIZE_PROPERTY = PROPERTY_PACKAGE + ".cacheSize";

	/**
	 * The cache size used for {@linkplain #cachedRead(long, int, ByteOrder)}
	 * calls:
	 */
	public static final int CACHE_SIZE;

	static {
		String cacheSizeProperty = System.getProperty(CACHE_SIZE_PROPERTY);
		int cacheSize = 0;

		if (cacheSizeProperty != null) {
			try {
				cacheSize = Integer.parseUnsignedInt(cacheSizeProperty, 16);
			} catch (NumberFormatException e) {
				// ignore
			}
			if (cacheSize <= 0 || (cacheSize & (CACHE_ALIGNMENT - 1)) != 0) {
				LOG.warning(null, "Ignoring invalid {0} value: ''{1}''", CACHE_SIZE_PROPERTY, cacheSizeProperty);
				cacheSize = 0;
			}
		}
		if (cacheSize == 0) {
			cacheSize = 0x10000;
		}
		LOG.notice(null, "Using cache size: {0}h ({1})", Long.toString(cacheSize, 16),
				Units.formatByteValue(cacheSize));
		CACHE_SIZE = cacheSize;
	}

	private static final ThreadLocal<Cache> CACHE = new ThreadLocal<>();

	private final FileScanner scanner;

	private final Path path;

	private final IOException ioStatus;

	/**
	 * Construct {@code FileScannerInput}.
	 *
	 * @param scanner The {@code FileScanner} scanning this input.
	 * @param path The input's path.
	 * @param ioStatus The input's I/O status (may be {@code null}).
	 */
	public FileScannerInput(FileScanner scanner, Path path, IOException ioStatus) {
		assert scanner != null;
		assert path != null;

		this.scanner = scanner;
		this.path = path;
		this.ioStatus = ioStatus;
	}

	/**
	 * Get the {@code FileScanner} scanning this input.
	 *
	 * @return The {@code FileScanner} scanning this input.
	 */
	public final FileScanner scanner() {
		return this.scanner;
	}

	/**
	 * Get the input's path.
	 *
	 * @return The input's path.
	 */
	public final Path path() {
		return this.path;
	}

	/**
	 * Get the input's I/O status.
	 * <p>
	 * The I/O status is initially set during input creation and indicates
	 * possible problems during input creation (e.g. erroneous data decoding for
	 * encoded data streams).
	 * </p>
	 *
	 * @return The input's I/O status. {@code null} if the status ok.
	 */
	public final IOException ioStatus() {
		return this.ioStatus;
	}

	/**
	 * Get the input's size.
	 *
	 * @return The input's size.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract long size() throws IOException;

	/**
	 * Read data from input.
	 *
	 * @param dst The {@code ByteBuffer} to read into.
	 * @param position The position to read from.
	 * @return The number of read bytes.
	 * @throws IOException if an I/O error occurs.
	 */
	public abstract int read(ByteBuffer dst, long position) throws IOException;

	/**
	 * Read and cache data from input.
	 *
	 * @param position The position to read from.
	 * @param size The number of bytes to read.
	 * @param order The byte order of the returned buffer.
	 * @return A byte buffer containing the read bytes. If the input end was
	 *         reached during the read operation the buffer may contain less
	 *         bytes than requested.
	 * @throws IOException if an I/O error occurs.
	 */
	public ByteBuffer cachedRead(long position, int size, ByteOrder order) throws IOException {
		Cache cache = CACHE.get();

		if (cache == null) {
			cache = new Cache(LOG);
			CACHE.set(cache);
		}
		return cache.read(position, size, order);
	}

	/**
	 * Start scanning this input.
	 *
	 * @see FileScanner#queueInput(FileScannerInput)
	 * @throws IOException if an I/O error occurs.
	 */
	public void startScan() throws IOException {
		this.scanner.queueInput(this);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public final void close() {
		try {
			close0();
		} catch (Exception e) {
			LOG.warning(e, null, "An error occurred while closing input: ''{0}''", this.path);
		}
	}

	/**
	 * Function responsible for the actual closing operation.
	 *
	 * @throws Exception if an error occurs.
	 */
	protected abstract void close0() throws Exception;

	private static class FileChannelInput extends FileScannerInput {

		private final ThreadLocal<FileChannel> fileReadChannel = new ThreadLocal<>();

		private final LinkedList<FileChannel> fileReadChannels = new LinkedList<>();

		FileChannelInput(FileScanner scanner, Path path) {
			super(scanner, path, null);
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.filescanner.spi.FileScannerInput#size()
		 */
		@Override
		public long size() throws IOException {
			return getReadChannel().size();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * de.carne.filescanner.spi.FileScannerInput#read(java.nio.ByteBuffer,
		 * long)
		 */
		@Override
		public int read(ByteBuffer dst, long position) throws IOException {
			return getReadChannel().read(dst, position);
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.filescanner.spi.FileScannerInput#close0()
		 */
		@Override
		protected synchronized void close0() throws Exception {
			IOException closeException = null;

			for (FileChannel readChannel : this.fileReadChannels) {
				try {
					readChannel.close();
				} catch (IOException e) {
					if (closeException == null) {
						closeException = e;
					}
				}
			}
			this.fileReadChannels.clear();
			if (closeException != null) {
				throw closeException;
			}
		}

		@SuppressWarnings("resource")
		private synchronized FileChannel getReadChannel() throws IOException {
			FileChannel readChannel = this.fileReadChannel.get();

			if (readChannel == null || !this.fileReadChannels.contains(readChannel)) {
				readChannel = FileChannel.open(this.path(), StandardOpenOption.READ);
				this.fileReadChannels.add(readChannel);
				this.fileReadChannel.set(readChannel);
			}
			return readChannel;
		}

	}

	/**
	 * Open file based {@code FileScannerInput}.
	 *
	 * @param scanner The {@code FileScanner} to open the input for.
	 * @param path The file path to open.
	 * @return The opened input.
	 * @throws IOException if an I/O error occurs.
	 */
	public static FileScannerInput open(FileScanner scanner, Path path) throws IOException {
		assert path != null;

		return new FileChannelInput(scanner, path);
	}

	/**
	 * Open file based {@code FileScannerInput}.
	 *
	 * @param scanner The {@code FileScanner} to open the input for.
	 * @param fileName The file name to open.
	 * @return The opened input.
	 * @throws IOException if an I/O error occurs.
	 */
	public static FileScannerInput open(FileScanner scanner, String fileName) throws IOException {
		assert fileName != null;

		return open(scanner, Paths.get(fileName));
	}

	/**
	 * Open file based {@code FileScannerInput}.
	 *
	 * @param scanner The {@code FileScanner} to open the input for.
	 * @param file The file to open.
	 * @return The opened input.
	 * @throws IOException if an I/O error occurs.
	 */
	public static FileScannerInput open(FileScanner scanner, File file) throws IOException {
		assert file != null;

		return open(scanner, file.toPath());
	}

	int nestedRead(ByteBuffer dst, long position, long readLimit) throws IOException {
		int maxRead = dst.remaining();
		int read;

		if (position + maxRead <= readLimit) {
			read = read(dst, position);
		} else {
			ByteBuffer limitedDst = dst.duplicate();

			limitedDst.limit((int) (readLimit - position));
			read = read(limitedDst, position);
			dst.position(limitedDst.position());
		}
		return read;
	}

	/**
	 * Create a nested input based by this input.
	 * 
	 * @param start The start position of the nested input.
	 * @param end The end position of the nested input.
	 * @param nestedPath The path of the nested input.
	 * @return The created input.
	 * @throws IOException if an I/O error occurs.
	 */
	public FileScannerInput slice(long start, long end, Path nestedPath) throws IOException {
		assert start >= 0;
		assert end >= start;
		assert nestedPath != null;

		return new FileScannerInput(this.scanner, nestedPath, null) {

			private final long nestedStart = start;

			private final long nestedEnd = Math.min(end, FileScannerInput.this.size());

			@Override
			public long size() throws IOException {
				return this.nestedEnd - this.nestedStart;
			}

			@Override
			public int read(ByteBuffer dst, long position) throws IOException {
				assert dst != null;
				assert position >= 0;

				long nestedPosition = this.nestedStart + position;
				int read;

				if (nestedPosition < this.nestedEnd) {
					read = nestedRead(dst, nestedPosition, this.nestedEnd);
				} else {
					read = -1;
				}
				return read;
			}

			@Override
			protected void close0() throws Exception {
				// Nothing to do here
			}

		};
	}

	private class Cache {

		private final Log log;

		private final ByteBuffer cacheBuffer = ByteBuffer.allocateDirect(CACHE_SIZE);

		private long cachePosition = -1l;

		public Cache(Log log) {
			this.log = log;
		}

		public ByteBuffer read(long position, int size, ByteOrder order) throws IOException {
			long readPosition = position & ~(CACHE_ALIGNMENT - 1);
			int readSize = (int) (position - readPosition) + size;
			ByteBuffer buffer;

			if (readSize <= CACHE_SIZE) {
				if (this.cachePosition < 0 || readPosition < this.cachePosition
						|| (this.cachePosition + CACHE_SIZE) < (readPosition + readSize)) {
					this.cachePosition = -1l;
					this.cacheBuffer.clear();
					FileScannerInput.this.read(this.cacheBuffer, readPosition);
					this.cacheBuffer.flip();
					this.cachePosition = readPosition;
				}
				buffer = this.cacheBuffer.asReadOnlyBuffer().order(order);

				int bufferPosition = (int) (position - this.cachePosition);

				buffer.position(Math.min(bufferPosition, buffer.remaining()));
			} else {
				this.log.warning(null, "Excessive read request ({1}); ignoring cache", Units.formatByteValue(readSize));

				buffer = ByteBuffer.allocate(size).order(order);
				FileScannerInput.this.read(buffer, position);
				buffer.flip();
			}
			return buffer;
		}

	}

}
