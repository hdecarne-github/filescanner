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

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.regex.Pattern;

import de.carne.filescanner.core.FileScannerInput;
import de.carne.filescanner.core.FormatFileScannerResult;
import de.carne.filescanner.core.format.FormatSpec;
import de.carne.util.logging.Log;

/**
 * Base class for all known and decodable file formats.
 */
public abstract class Format {

	private static final Log LOG = new Log(Format.class);

	private static final ArrayList<Format> FORMATS = new ArrayList<>();

	/**
	 * Get the known formats.
	 *
	 * @return The known formats.
	 */
	public static Collection<Format> getFormats() {
		Collection<Format> formats;

		synchronized (FORMATS) {
			if (FORMATS.isEmpty()) {
				ServiceLoader<Format> loader = ServiceLoader.load(Format.class);

				for (Format format : loader) {
					FORMATS.add(format);
				}
			}
			formats = Collections.unmodifiableCollection(FORMATS);
		}
		return formats;
	}

	private final String name;

	private final ByteOrder order;

	private final ArrayList<FormatSpec> headerSpecs = new ArrayList<>();

	private final ArrayList<FormatSpec> trailerSpecs = new ArrayList<>();

	private final ArrayList<Pattern> inputNamePatterns = new ArrayList<>();

	/**
	 * Construct {@code Format}.
	 *
	 * @param name The format name.
	 * @param order The format's byte order.
	 */
	protected Format(String name, ByteOrder order) {
		assert name != null;
		assert order != null;

		this.name = name;
		this.order = order;
		LOG.debug(null, "Loaded format: {0}", this);
	}

	/**
	 * Register a header spec.
	 *
	 * @param spec The spec to register.
	 */
	protected final void registerHeaderSpec(FormatSpec spec) {
		this.headerSpecs.add(spec);
	}

	/**
	 * Register a trailer spec.
	 *
	 * @param spec The spec to register.
	 */
	protected final void registerTrailerSpec(FormatSpec spec) {
		this.trailerSpecs.add(spec);
	}

	/**
	 * Register an input name pattern.
	 * <p>
	 * The registered {@code Pattern} object will be case insensitive.
	 * </p>
	 *
	 * @param inputNamePattern The input name pattern to register.
	 */
	protected final void registerInputPathMatcher(String inputNamePattern) {
		this.inputNamePatterns.add(Pattern.compile(inputNamePattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
	}

	/**
	 * Get the format's name.
	 *
	 * @return The format's name.
	 */
	public final String name() {
		return this.name;
	}

	/**
	 * Get the format's byte order.
	 *
	 * @return The format's byte order.
	 */
	public final ByteOrder order() {
		return this.order;
	}

	/**
	 * Get the format's header specs.
	 *
	 * @return The format's header specs.
	 */
	public final List<FormatSpec> headerSpecs() {
		return Collections.unmodifiableList(this.headerSpecs);
	}

	/**
	 * Get the format's trailer specs.
	 *
	 * @return The format's trailer specs.
	 */
	public final List<FormatSpec> trailerSpecs() {
		return Collections.unmodifiableList(this.trailerSpecs);
	}

	/**
	 * Get the format's input name patterns.
	 *
	 * @return The format's input name patterns.
	 */
	public final List<Pattern> inputNamePatterns() {
		return Collections.unmodifiableList(this.inputNamePatterns);
	}

	/**
	 * Decode the format.
	 *
	 * @param input The input to decode from.
	 * @param position The position to start decoding at.
	 * @return The decode result or {@code null} if there was nothing to decode.
	 * @throws IOException if an I/O error occurs.
	 */
	public FormatFileScannerResult decodeInput(FileScannerInput input, long position) throws IOException {
		FormatFileScannerResult decoded = new FormatFileScannerResult(this, input, position);

		decodeFormatSpec(decoded, getFormatSpec(), input, position);
		return decoded;
	}

	protected abstract FormatSpec getFormatSpec();

	protected final void decodeFormatSpec(FormatFileScannerResult result, FormatSpec spec, FileScannerInput input,
			long position) throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "'" + this.name + "' (" + getClass().getName() + ")";
	}

}
