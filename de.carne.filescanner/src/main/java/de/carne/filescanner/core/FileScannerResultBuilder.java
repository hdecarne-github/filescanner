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
package de.carne.filescanner.core;

import java.io.IOException;
import java.nio.ByteOrder;

import de.carne.filescanner.core.format.Renderable;
import de.carne.filescanner.spi.FileScannerInput;
import de.carne.filescanner.spi.FileScannerResultRenderer;
import de.carne.filescanner.spi.Format;

/**
 * This class is used to build up the scan result hierarchy for a single format.
 * <p>
 * Result objects stored in the buffer are not required to be complete from the
 * beginning. Furthermore several of the later immutable attributes can still be
 * changed while the result object is still in the buffer.
 * </p>
 */
public final class FileScannerResultBuilder extends FileScannerResult {

	private long end;

	private final FileScannerResultBuilder parent;

	private String title;

	private final Renderable renderable;

	/**
	 * Construct {@code FileScannerResultBuilder}.
	 *
	 * @param format The format to decode.
	 * @param input The result object's input.
	 * @param start The result object's start position.
	 */
	public FileScannerResultBuilder(Format format, FileScannerInput input, long start) {
		this(null, FileScannerResultType.FORMAT, input, format.order(), start, format.name(), format.decodable());
	}

	private FileScannerResultBuilder(FileScannerResultBuilder parent, FileScannerResultType type,
			FileScannerInput input, ByteOrder order, long start, String title, Renderable renderable) {
		super(type, input, order, start);
		this.end = start;
		this.parent = parent;
		if (this.parent != null) {
			this.parent.addChild(this);
		}
		this.title = title;
		this.renderable = renderable;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerResult#end()
	 */
	@Override
	public long end() {
		return Math.max(this.end, getChildrenEnd());
	}

	/**
	 * Update the result's end position.
	 *
	 * @param updatedEnd The updated end position to set.
	 */
	public void updateEnd(long updatedEnd) {
		assert start() <= updatedEnd;

		this.end = updatedEnd;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerResult#parent()
	 */
	@Override
	public FileScannerResult parent() {
		return this.parent;
	}

	/*
	 * (non-Javadoc)
	 * @see de.carne.filescanner.core.FileScannerResult#title()
	 */
	@Override
	public String title() {
		return this.title;
	}

	/**
	 * Updated the result object's title.
	 *
	 * @param updatedTitle The updated title to set.
	 */
	public void updateTitle(String updatedTitle) {
		assert updatedTitle != null;

		this.title = updatedTitle;
	}

	/**
	 * Get this builder's {@linkplain Renderable}.
	 *
	 * @return This builder's {@linkplain Renderable}.
	 */
	public Renderable renderable() {
		return this.renderable;
	}

	/**
	 * Add a new result to the builder.
	 *
	 * @param resultType The result type to build up.
	 * @param resultStart The result object's start position.
	 * @param resultRenderable The result's {@linkplain Renderable}.
	 * @return The builder for the added result object.
	 */
	public FileScannerResultBuilder addResult(FileScannerResultType resultType, long resultStart,
			Renderable resultRenderable) {
		assert type() != FileScannerResultType.INPUT;
		assert resultType != null;
		assert start() <= resultStart;

		return new FileScannerResultBuilder(this, resultType, input(), order(), resultStart, "", resultRenderable);
	}

	/**
	 * Adds all result object's collected in the buffer to the scan results.
	 * <p>
	 * During this step also all identified nested input data streams are
	 * submitted for scanning.
	 * </p>
	 *
	 * @param result The result object to add the buffered results to.
	 * @return The decoded result object.
	 */
	public FileScannerResult toResult(FileScannerResult result) {
		FileScannerResult decoded = null;

		if (size() > 0) {
			decoded = new FinalFileScannerResult(result, this);
			for (FileScannerResult child : children()) {
				FileScannerResultBuilder childBuilder = ((FileScannerResultBuilder) child);

				childBuilder.toResult(decoded);
			}
		}
		return decoded;
	}

	private static class FinalFileScannerResult extends FileScannerResult {

		private final long end;

		private final FileScannerResult parent;

		private final String title;

		private final Renderable renderable;

		FinalFileScannerResult(FileScannerResult parent, FileScannerResultBuilder builder) {
			super(builder.type(), builder.input(), builder.order(), builder.start());
			this.end = builder.end();
			this.parent = parent;
			this.parent.addChild(this);
			this.title = builder.title();
			this.renderable = builder.renderable();
			this.context().addResultAttributes(builder.context());
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.filescanner.core.FileScannerResult#end()
		 */
		@Override
		public long end() {
			return this.end;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.filescanner.core.FileScannerResult#parent()
		 */
		@Override
		public FileScannerResult parent() {
			return this.parent;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.filescanner.core.FileScannerResult#title()
		 */
		@Override
		public String title() {
			return this.title;
		}

		/*
		 * (non-Javadoc)
		 * @see de.carne.filescanner.core.FileScannerResult#render(de.carne.
		 * filescanner.spi.FileScannerResultRenderer)
		 */
		@Override
		public void render(FileScannerResultRenderer renderer) throws IOException, InterruptedException {
			if (this.renderable != null) {
				this.renderable.render(this, renderer);
			} else {
				super.render(renderer);
			}
		}

	}

}
