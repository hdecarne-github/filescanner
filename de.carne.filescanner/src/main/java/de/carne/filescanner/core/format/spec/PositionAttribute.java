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
package de.carne.filescanner.core.format.spec;

import java.io.IOException;
import java.nio.ByteBuffer;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.spi.FileScannerResultRenderer;

/**
 * This class defines Number based attributes of fixed size that are used to
 * reference specific format positions.
 *
 * @param <T> The attribute's type.
 */
public abstract class PositionAttribute<T extends Number> extends NumberAttribute<T> {

	/**
	 * Construct {@code PositionAttribute}.
	 *
	 * @param type The attribute's type.
	 * @param name The attribute's name.
	 * @param format The attribute's primary format.
	 */
	protected PositionAttribute(NumberAttributeType type, String name, NumberFormat<T> format) {
		super(type, name, format);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * de.carne.filescanner.core.format.spec.NumberAttribute#specRender(de.carne
	 * .filescanner.core.FileScannerResult, long, long,
	 * de.carne.filescanner.spi.FileScannerResultRenderer)
	 */
	@Override
	public void specRender(FileScannerResult result, long start, long end, FileScannerResultRenderer renderer)
			throws IOException, InterruptedException {
		int typeSize = type().size();
		ByteBuffer buffer = result.cachedRead(start, typeSize);
		T value = getValue(buffer);

		if (value != null) {
			renderer.setNormalMode().renderText(name());
			renderer.setOperatorMode().renderText(" = ");

			long refPosition = value.longValue();

			renderer.setValueMode().renderRefText(format().apply(value), refPosition);
			for (AttributeRenderer<T> extraRenderer : getExtraRenderer()) {
				extraRenderer.render(value, renderer);
			}
		}
		renderer.renderBreakOrClose(isResult());
	}

}
