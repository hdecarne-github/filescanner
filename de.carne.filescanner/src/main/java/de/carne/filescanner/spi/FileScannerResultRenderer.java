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
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for all scan result renderer.
 * <p>
 * As rendering can take some time the class is designed to support an
 * asynchronous rendering model where rendering can be interrupted at any time.
 * </p>
 */
public abstract class FileScannerResultRenderer {

	/**
	 * Render features.
	 */
	public static enum Feature {

		/**
		 * Render with transparency support.
		 */
		TRANSPARENCY

	}

	/**
	 * Render modes.
	 */
	public static enum Mode {

		/**
		 * Render normal text.
		 */
		NORMAL,

		/**
		 * Render value text.
		 */
		VALUE,

		/**
		 * Render comment text.
		 */
		COMMENT,

		/**
		 * Render keyword text.
		 */
		KEYWORD,

		/**
		 * Render operator text.
		 */

		OPERATOR,
		/**
		 * Render label text.
		 */

		LABEL,

		/**
		 * Render error text.
		 */
		ERROR

	}

	/**
	 * Interface used to access stream based data.
	 */
	public static interface StreamHandler {

		/**
		 * Open an {@code InputStream} for data access.
		 *
		 * @return The opened {@code InputStream}.
		 * @throws IOException if an I/O error occurs.
		 */
		public InputStream open() throws IOException;

	}

	private AtomicBoolean open = new AtomicBoolean(true);
	private boolean prepared = false;
	private final HashSet<Feature> enabledFeatures = new HashSet<>();
	private Mode currentMode = null;
	private Mode nextMode = Mode.NORMAL;

	private void checkInterrupted() throws InterruptedException {
		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
	}

	private void ensureOpenAndPrepared() throws IOException, InterruptedException {
		if (!this.open.get()) {
			throw new IOException("Renderer closed");
		}
		if (!this.prepared) {
			writePreamble(Collections.unmodifiableSet(this.enabledFeatures));
			this.prepared = true;
		}
	}

	/**
	 * Check whether this renderer has generated any output so far.
	 *
	 * @return {@code true} if the renderer has generated output.
	 */
	public final boolean hasOutput() {
		return this.prepared;
	}

	/**
	 * Enable a specific rendering feature.
	 *
	 * @param feature The feature to enable.
	 * @return The updated renderer.
	 */
	public final FileScannerResultRenderer enableFeature(Feature feature) {
		assert feature != null;

		this.enabledFeatures.add(feature);
		return this;
	}

	/**
	 * Set rendering mode for the following render operations.
	 *
	 * @param mode The mode to set.
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer setMode(Mode mode) throws IOException, InterruptedException {
		assert mode != null;

		checkInterrupted();
		this.nextMode = mode;
		return this;
	}

	private void applyMode() throws IOException, InterruptedException {
		if (!this.nextMode.equals(this.currentMode)) {
			if (this.currentMode != null) {
				writeEndMode(this.currentMode);
			}
			this.currentMode = this.nextMode;
			writeBeginMode(this.currentMode);
		}
	}

	/**
	 * Set rendering mode to {@code Mode#NORMAL} for the following render
	 * operations.
	 *
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer setNormalMode() throws IOException, InterruptedException {
		return setMode(Mode.NORMAL);
	}

	/**
	 * Set rendering mode to {@code Mode#VALUE} for the following render
	 * operations.
	 *
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer setValueMode() throws IOException, InterruptedException {
		return setMode(Mode.VALUE);
	}

	/**
	 * Set rendering mode to {@code Mode#COMMENT} for the following render
	 * operations.
	 *
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer setCommentMode() throws IOException, InterruptedException {
		return setMode(Mode.COMMENT);
	}

	/**
	 * Set rendering mode to {@code Mode#KEYWORD} for the following render
	 * operations.
	 *
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer setKeywordMode() throws IOException, InterruptedException {
		return setMode(Mode.KEYWORD);
	}

	/**
	 * Set rendering mode to {@code Mode#OPERATOR} for the following render
	 * operations.
	 *
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer setOperatorMode() throws IOException, InterruptedException {
		return setMode(Mode.OPERATOR);
	}

	/**
	 * Set rendering mode to {@code Mode#LABEL} for the following render
	 * operations.
	 *
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer setLabelMode() throws IOException, InterruptedException {
		return setMode(Mode.LABEL);
	}

	/**
	 * Set rendering mode to {@code Mode#ERROR} for the following render
	 * operations.
	 *
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer setErrorMode() throws IOException, InterruptedException {
		return setMode(Mode.ERROR);
	}

	/**
	 * Render a line break.
	 *
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer renderBreak() throws IOException, InterruptedException {
		checkInterrupted();
		ensureOpenAndPrepared();
		writeBreak();
		return this;
	}

	/**
	 * Render a simple text.
	 * <p>
	 * The text is rendered according to the current render {@linkplain Mode}.
	 * </p>
	 *
	 * @param text The text to render.
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer renderText(String text) throws IOException, InterruptedException {
		checkInterrupted();
		ensureOpenAndPrepared();
		applyMode();
		writeText(this.currentMode, text);
		return this;
	}

	/**
	 * Render a referencing text.
	 * <p>
	 * The text is rendered according to the current render {@linkplain Mode}.
	 * </p>
	 *
	 * @param text The text to render.
	 * @param position The position to reference.
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer renderRefText(String text, long position)
			throws IOException, InterruptedException {
		checkInterrupted();
		ensureOpenAndPrepared();
		applyMode();
		writeRefText(this.currentMode, text, position);
		return this;
	}

	/**
	 * Render an image.
	 *
	 * @param streamHandler The image data to render.
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer renderImage(StreamHandler streamHandler)
			throws IOException, InterruptedException {
		checkInterrupted();
		ensureOpenAndPrepared();
		applyMode();
		writeImage(this.currentMode, streamHandler);
		return this;
	}

	/**
	 * Render a referencing image.
	 *
	 * @param streamHandler The image data to render.
	 * @param position The position to reference.
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer renderRefText(StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		checkInterrupted();
		ensureOpenAndPrepared();
		applyMode();
		writeRefImage(this.currentMode, streamHandler, position);
		return this;
	}

	/**
	 * Render an image.
	 *
	 * @param streamHandler The video data to render.
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer renderVideo(StreamHandler streamHandler)
			throws IOException, InterruptedException {
		checkInterrupted();
		ensureOpenAndPrepared();
		applyMode();
		writeVideo(this.currentMode, streamHandler);
		return this;
	}

	/**
	 * Render a referencing video.
	 *
	 * @param streamHandler The video data to render.
	 * @param position The position to reference.
	 * @return The updated renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final FileScannerResultRenderer renderRefVideo(StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		checkInterrupted();
		ensureOpenAndPrepared();
		applyMode();
		writeRefVideo(this.currentMode, streamHandler, position);
		return this;
	}

	/**
	 * Convenience function for finishing a render step.
	 * <p>
	 * Depending on the submitted either {@linkplain #close()} (overall
	 * rendering is done) or {@linkplain #renderBreak()} (line rendering is
	 * done) is called.
	 * </p>
	 *
	 * @param close Whether to call {@linkplain #close()} or
	 *        {@linkplain #renderBreak()}.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final void renderBreakOrClose(boolean close) throws IOException, InterruptedException {
		if (close) {
			close();
		} else {
			renderBreak();
		}
	}

	/**
	 * Close the rendering process.
	 *
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	public final void close() throws IOException, InterruptedException {
		checkInterrupted();
		ensureOpenAndPrepared();
		writeEpilogue();
		this.open.set(false);
	}

	/**
	 * Write any necessary preamble data.
	 * <p>
	 * This function is called exactly once and always first during the
	 * rendering process.
	 * </p>
	 *
	 * @param features The enabled features for this renderer.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected void writePreamble(Set<Feature> features) throws IOException, InterruptedException {
		// default is to write nothing
	}

	/**
	 * Write any necessary epilogue data.
	 * <p>
	 * This function is called exactly once and always last during the rendering
	 * process.
	 * </p>
	 *
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected void writeEpilogue() throws IOException, InterruptedException {
		// default is to write nothing
	}

	/**
	 * Write any necessary mode setup data.
	 * <p>
	 * This function is called to apply the rendering mode for an upcoming write
	 * data operation.
	 * </p>
	 *
	 * @param mode The beginning mode.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected void writeBeginMode(Mode mode) throws IOException, InterruptedException {
		// default is to write nothing
	}

	/**
	 * Write any necessary mode cleanup data.
	 * <p>
	 * This function is called to finish a previously applied (
	 * {@linkplain #writeBeginMode(Mode)}) rendering mode.
	 * </p>
	 *
	 * @param mode The ending mode.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected void writeEndMode(Mode mode) throws IOException, InterruptedException {
		// default is to write nothing
	}

	/**
	 * Write a line break.
	 *
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected abstract void writeBreak() throws IOException, InterruptedException;

	/**
	 * Write text data.
	 *
	 * @param mode The currently active render mode.
	 * @param text The text to write.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected abstract void writeText(Mode mode, String text) throws IOException, InterruptedException;

	/**
	 * Write referencing text data.
	 *
	 * @param mode The currently active render mode.
	 * @param text The text to write.
	 * @param position The referenced position.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected void writeRefText(Mode mode, String text, long position) throws IOException, InterruptedException {
		writeText(mode, text);
	}

	/**
	 * Write image data.
	 *
	 * @param mode The currently active render mode.
	 * @param streamHandler The image data to render.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected abstract void writeImage(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException;

	/**
	 * Write referencing image data.
	 *
	 * @param mode The currently active render mode.
	 * @param streamHandler The image data to render.
	 * @param position The referenced position.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected void writeRefImage(Mode mode, StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		writeImage(mode, streamHandler);
	}

	/**
	 * Write video data.
	 *
	 * @param mode The currently active render mode.
	 * @param streamHandler The video data to render.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected abstract void writeVideo(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException;

	/**
	 * Write referencing video data.
	 *
	 * @param mode The currently active render mode.
	 * @param streamHandler The video data to render.
	 * @param position The referenced position.
	 * @throws IOException if an I/O error occurs.
	 * @throws InterruptedException if the render thread was interrupted.
	 */
	protected void writeRefVideo(Mode mode, StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		writeVideo(mode, streamHandler);
	}

}
