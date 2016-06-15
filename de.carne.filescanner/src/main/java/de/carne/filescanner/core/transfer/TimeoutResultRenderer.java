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

import de.carne.util.Nanos;

/**
 * {@linkplain ResultRenderer} implementation that enforces a time limit on the
 * rendering process and interrupts as soon as the time limit is reached.
 *
 * @param <T> The actual renderer's type.
 */
public class TimeoutResultRenderer<T extends ResultRenderer> extends ResultRenderer {

	private final T renderer;

	private final Nanos nanos = new Nanos();

	private final long limit;

	/**
	 * Construct {@code TimeoutResultRenderer}.
	 *
	 * @param renderer The actual renderer.
	 * @param timeoutMillis The time limit to enforce in milliseconds.
	 */
	public TimeoutResultRenderer(T renderer, long timeoutMillis) {
		assert renderer != null;

		this.renderer = renderer;
		this.limit = Nanos.toNanos(timeoutMillis);
	}

	/**
	 * Get the actual renderer.
	 *
	 * @return The actual renderer.
	 */
	public T getRenderer() {
		return this.renderer;
	}

	@Override
	public ResultRenderer setStyle(RendererStyle style) {
		this.renderer.setStyle(style);
		return super.setStyle(style);
	}

	@Override
	public ResultRenderer enableFeature(Feature feature) {
		this.renderer.enableFeature(feature);
		return super.enableFeature(feature);
	}

	@Override
	protected void writePrologue() throws IOException, InterruptedException {
		if (this.nanos.elapsed() > this.limit) {
			throw new InterruptedException();
		}
		this.renderer.writePrologue();
	}

	@Override
	protected void writeEpilogue() throws IOException, InterruptedException {
		if (this.nanos.elapsed() > this.limit) {
			throw new InterruptedException();
		}
		this.renderer.writeEpilogue();
	}

	@Override
	protected void writeBeginMode(Mode mode) throws IOException, InterruptedException {
		if (this.nanos.elapsed() > this.limit) {
			throw new InterruptedException();
		}
		this.renderer.writeBeginMode(mode);
	}

	@Override
	protected void writeEndMode(Mode mode) throws IOException, InterruptedException {
		if (this.nanos.elapsed() > this.limit) {
			throw new InterruptedException();
		}
		this.renderer.writeEndMode(mode);
	}

	@Override
	protected void writeBreak() throws IOException, InterruptedException {
		if (this.nanos.elapsed() > this.limit) {
			throw new InterruptedException();
		}
		this.renderer.writeBreak();
	}

	@Override
	protected void writeText(Mode mode, String text) throws IOException, InterruptedException {
		if (this.nanos.elapsed() > this.limit) {
			throw new InterruptedException();
		}
		this.renderer.writeText(mode, text);
	}

	@Override
	protected void writeRefText(Mode mode, String text, long position) throws IOException, InterruptedException {
		if (this.nanos.elapsed() > this.limit) {
			throw new InterruptedException();
		}
		this.renderer.writeRefText(mode, text, position);
	}

	@Override
	protected void writeImage(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException {
		if (this.nanos.elapsed() > this.limit) {
			throw new InterruptedException();
		}
		this.renderer.writeImage(mode, streamHandler);
	}

	@Override
	protected void writeRefImage(Mode mode, StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		if (this.nanos.elapsed() > this.limit) {
			throw new InterruptedException();
		}
		this.renderer.writeRefImage(mode, streamHandler, position);
	}

	@Override
	protected void writeVideo(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException {
		if (this.nanos.elapsed() > this.limit) {
			throw new InterruptedException();
		}
		this.renderer.writeVideo(mode, streamHandler);
	}

	@Override
	protected void writeRefVideo(Mode mode, StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		if (this.nanos.elapsed() > this.limit) {
			throw new InterruptedException();
		}
		this.renderer.writeRefVideo(mode, streamHandler, position);
	}

}
