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
package de.carne.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Utility class providing I/O related functions.
 */
public final class IOUtils {

	/**
	 * Copy all bytes from {@code in} to {@code out}.
	 *
	 * @param in The {@code InputStream} to read from.
	 * @param out The {@code OutputStream} to write to.
	 * @return The number of bytes copied.
	 * @throws IOException if an I/O error occurs.
	 */
	public static long copy(InputStream in, OutputStream out) throws IOException {
		assert in != null;
		assert out != null;

		long copied = 0;
		int read;
		byte[] buffer = new byte[4096];

		while ((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
			copied += read;
		}
		return copied;
	}

	/**
	 * Copy all characters from {@code in} to {@code out}.
	 *
	 * @param in The {@code Reader} to read from.
	 * @param out The {@code Writer} to write to.
	 * @return The number of bytes characters.
	 * @throws IOException if an I/O error occurs.
	 */
	public static long copy(Reader in, Writer out) throws IOException {
		assert in != null;
		assert out != null;

		long copied = 0;
		int read;
		char[] buffer = new char[4096];

		while ((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
			copied += read;
		}
		return copied;
	}

	/**
	 * Delete a directory (and any contained file or sub-directory).
	 *
	 * @param directory The directory to delete.
	 * @throws IOException if an I/O error occurs.
	 */
	public static void deleteDirectory(Path directory) throws IOException {
		assert directory != null;

		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

}
