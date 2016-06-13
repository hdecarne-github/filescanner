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

/**
 * {@linkplain ResultRenderer} implementation that combines multiple renderer
 * implementations into one to create multiple render results in one render
 * call.
 */
public class CombinedResultRenderer extends ResultRenderer {

	private final ResultRenderer[] renderers;

	/**
	 * Construct {@code CombinedResultRenderer}.
	 *
	 * @param renderers The renderers to combine.
	 */
	public CombinedResultRenderer(ResultRenderer... renderers) {
		this.renderers = renderers;
	}

	@Override
	protected void writePrologue() throws IOException, InterruptedException {
		for (ResultRenderer renderer : this.renderers) {
			renderer.writePrologue();
		}
	}

	@Override
	protected void writeEpilogue() throws IOException, InterruptedException {
		for (ResultRenderer renderer : this.renderers) {
			renderer.writeEpilogue();
		}
	}

	@Override
	protected void writeBeginMode(Mode mode) throws IOException, InterruptedException {
		for (ResultRenderer renderer : this.renderers) {
			renderer.writeBeginMode(mode);
		}
	}

	@Override
	protected void writeEndMode(Mode mode) throws IOException, InterruptedException {
		for (ResultRenderer renderer : this.renderers) {
			renderer.writeEndMode(mode);
		}
	}

	@Override
	protected void writeBreak() throws IOException, InterruptedException {
		for (ResultRenderer renderer : this.renderers) {
			renderer.writeBreak();
		}
	}

	@Override
	protected void writeText(Mode mode, String text) throws IOException, InterruptedException {
		for (ResultRenderer renderer : this.renderers) {
			renderer.writeText(mode, text);
		}
	}

	@Override
	protected void writeRefText(Mode mode, String text, long position) throws IOException, InterruptedException {
		for (ResultRenderer renderer : this.renderers) {
			renderer.writeRefText(mode, text, position);
		}
	}

	@Override
	protected void writeImage(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException {
		for (ResultRenderer renderer : this.renderers) {
			renderer.writeImage(mode, streamHandler);
		}
	}

	@Override
	protected void writeRefImage(Mode mode, StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		for (ResultRenderer renderer : this.renderers) {
			renderer.writeRefImage(mode, streamHandler, position);
		}
	}

	@Override
	protected void writeVideo(Mode mode, StreamHandler streamHandler) throws IOException, InterruptedException {
		for (ResultRenderer renderer : this.renderers) {
			renderer.writeVideo(mode, streamHandler);
		}
	}

	@Override
	protected void writeRefVideo(Mode mode, StreamHandler streamHandler, long position)
			throws IOException, InterruptedException {
		for (ResultRenderer renderer : this.renderers) {
			renderer.writeRefVideo(mode, streamHandler, position);
		}
	}

}
