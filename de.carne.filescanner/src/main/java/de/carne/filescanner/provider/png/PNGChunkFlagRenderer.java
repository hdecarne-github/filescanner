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
package de.carne.filescanner.provider.png;

import java.io.IOException;

import de.carne.filescanner.core.format.spec.U32FlagRenderer;
import de.carne.filescanner.core.transfer.ResultRenderer;

/**
 * Custom renderer for PNG chunk flags.
 */
public final class PNGChunkFlagRenderer extends U32FlagRenderer {

	private static int FLAG_MASK = 0x20202020;

	@Override
	public void render(Integer value, ResultRenderer renderer) throws IOException, InterruptedException {
		super.render(Integer.valueOf(value.intValue() & FLAG_MASK), renderer);
	}

}
