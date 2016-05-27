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
package de.carne.filescanner.core.input;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;

import de.carne.filescanner.core.FileScanner;
import de.carne.filescanner.spi.FileScannerInput;
import de.carne.nio.compression.spi.Decoder;
import de.carne.util.logging.Log;

/**
 * Class for decode cache access and management.
 * <p>
 * The decode cache receives the decoded data for all identified encoded data
 * streams. Based upon the decode cache's decoded content the necessary scanner
 * inputs are created to perform recursive scans on the decoded data.
 * </p>
 */
public final class DecodeCache implements AutoCloseable {

	private static final Log LOG = new Log(DecodeCache.class);

	private Path decodeCachePath = null;

	private FileChannel decodeCacheWriteChannel = null;

	private ThreadLocal<FileChannel> decodeCacheReadChannel = new ThreadLocal<>();

	private final LinkedList<FileChannel> decodeCacheReadChannels = new LinkedList<>();

	/**
	 * Decode an encoded data stream to the decode cache.
	 *
	 * @param input The input to decode from.
	 * @param position The position to start decoding at.
	 * @param decoder The decoder to use for decoding.
	 * @param path
	 * @return The created scanner input.
	 * @throws IOException if an I/O error occurs.
	 */
	public synchronized FileScannerInput decodeInput(FileScannerInput input, long position, Decoder decoder, Path path)
			throws IOException {
		assert input != null;
		assert position >= 0;
		assert decoder != null;

		ensureWriteChannelOpen();

		long decodeInputStart = this.decodeCacheWriteChannel.size();
		long decodeInputEnd = decodeInputStart;
		IOException decodeInputIOStatus = null;

		try (InputReadChannel inputReadChannel = new InputReadChannel(input, position)) {
			ByteBuffer decodeBuffer = ByteBuffer.allocateDirect(FileScannerInput.CACHE_SIZE);

			while (decoder.decode(decodeBuffer, inputReadChannel) > 0) {
				decodeBuffer.flip();
				decodeInputEnd += this.decodeCacheWriteChannel.write(decodeBuffer, decodeInputEnd);
				decodeBuffer.clear();
			}
		} catch (IOException e) {
			decodeInputIOStatus = e;
		}
		return new DecodeCacheFileScannerInput(input.scanner(), path, decodeInputIOStatus, decodeInputStart,
				decodeInputEnd);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public synchronized void close() {
		IOException closeException = null;

		if (this.decodeCacheWriteChannel != null) {
			for (FileChannel readChannel : this.decodeCacheReadChannels) {
				try {
					readChannel.close();
				} catch (IOException e) {
					if (closeException == null) {
						closeException = e;
					}
				}
			}
			this.decodeCacheReadChannels.clear();
			try {
				this.decodeCacheWriteChannel.close();
			} catch (IOException e) {
				if (closeException == null) {
					closeException = e;
				}
			}
			this.decodeCacheWriteChannel = null;
		}
		if (closeException != null) {
			LOG.error(closeException, null, "An error occurred while closing decode cache file ''{0}''",
					this.decodeCachePath);
		}
	}

	private synchronized void ensureWriteChannelOpen() throws IOException {
		if (this.decodeCacheWriteChannel == null) {
			this.decodeCachePath = Files.createTempFile(DecodeCache.class.getSimpleName(), null);
			this.decodeCacheWriteChannel = FileChannel.open(this.decodeCachePath, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		}
	}

	@SuppressWarnings("resource")
	private synchronized FileChannel getReadChannel() throws IOException {
		ensureWriteChannelOpen();

		FileChannel readChannel = this.decodeCacheReadChannel.get();

		if (readChannel == null || !this.decodeCacheReadChannels.contains(readChannel)) {
			readChannel = FileChannel.open(this.decodeCachePath, StandardOpenOption.READ);
			this.decodeCacheReadChannels.add(readChannel);
			this.decodeCacheReadChannel.set(readChannel);
		}
		return readChannel;
	}

	int decodeCacheRead(ByteBuffer dst, long position, long readLimit) throws IOException {
		int maxRead = dst.remaining();
		int read;

		if (position + maxRead <= readLimit) {
			read = getReadChannel().read(dst, position);
		} else {
			ByteBuffer limitedDst = dst.duplicate();

			limitedDst.limit((int) (readLimit - position));
			read = getReadChannel().read(limitedDst, position);
			dst.position(limitedDst.position());
		}
		return read;
	}

	private class DecodeCacheFileScannerInput extends FileScannerInput {

		private final long decodeCacheStart;

		private final long decodeCacheEnd;

		public DecodeCacheFileScannerInput(FileScanner scanner, Path path, IOException ioStatus, long decodeCacheStart,
				long decodeCacheEnd) {
			super(scanner, path, ioStatus);
			this.decodeCacheStart = decodeCacheStart;
			this.decodeCacheEnd = decodeCacheEnd;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.filescanner.spi.FileScannerInput#size()
		 */
		@Override
		public long size() throws IOException {
			return this.decodeCacheEnd - this.decodeCacheStart;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * de.carne.filescanner.spi.FileScannerInput#read(java.nio.ByteBuffer,
		 * long)
		 */
		@Override
		public int read(ByteBuffer dst, long position) throws IOException {
			assert dst != null;
			assert position >= 0;

			long decodeCachePosition = this.decodeCacheStart + position;
			int read;

			if (decodeCachePosition < this.decodeCacheEnd) {
				read = decodeCacheRead(dst, decodeCachePosition, this.decodeCacheEnd);
			} else {
				read = -1;
			}
			return read;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.filescanner.spi.FileScannerInput#close0()
		 */
		@Override
		protected void close0() throws Exception {
			// Nothing to do here
		}

	}

}
