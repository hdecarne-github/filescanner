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
package de.carne.filescanner.jfx.session;

import java.util.HashMap;
import java.util.List;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.FileScannerResultType;
import de.carne.filescanner.jfx.Images;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Helper class for efficient handling of a possibly huge number of tree items.
 */
final class ResultTreeItemFactory {

	class ResultTreeItem extends TreeItem<FileScannerResult> {

		private boolean synced = false;

		ResultTreeItem(FileScannerResult result, Node graphic) {
			super(result, graphic);
		}

		public void syncChildren() {
			ObservableList<TreeItem<FileScannerResult>> children = super.getChildren();
			List<FileScannerResult> resultChildren = getValue().children();
			int resultChildIndex = 0;

			for (FileScannerResult resultChild : resultChildren) {
				if (resultChildIndex == children.size()) {
					children.add(create(resultChild));
				} else if (!resultChild.equals(children.get(resultChildIndex).getValue())) {
					children.add(resultChildIndex, create(resultChild));
				}
				resultChildIndex++;
			}
		}

		/*
		 * (non-Javadoc)
		 * @see javafx.scene.control.TreeItem#isLeaf()
		 */
		@Override
		public boolean isLeaf() {
			return getValue().childrenCount() == 0;
		}

		/*
		 * (non-Javadoc)
		 * @see javafx.scene.control.TreeItem#getChildren()
		 */
		@Override
		public ObservableList<TreeItem<FileScannerResult>> getChildren() {
			if (!this.synced) {
				syncChildren();
				this.synced = true;
			}
			return super.getChildren();
		}

	}

	private final HashMap<FileScannerResult, ResultTreeItem> itemMap = new HashMap<>();

	public void clear() {
		this.itemMap.clear();
	}

	public ResultTreeItem create(FileScannerResult result) {
		ResultTreeItem resultItem = new ResultTreeItem(result, getResultGraphic(result));

		this.itemMap.put(result, resultItem);
		return resultItem;
	}

	public ResultTreeItem get(FileScannerResult result) {
		return this.itemMap.get(result);
	}

	private static Node getResultGraphic(FileScannerResult result) {
		Image image;

		switch (result.type()) {
		case INPUT:
			image = Images.IMAGE_INPUT_RESULT16;
			break;
		case FORMAT:
			image = (result.parent().type() == FileScannerResultType.INPUT ? Images.IMAGE_FORMAT1_RESULT16
					: Images.IMAGE_FORMAT2_RESULT16);
			break;
		case ENCODED_INPUT:
			image = Images.IMAGE_ENCODED_INPUT_RESULT16;
			break;
		default:
			throw new IllegalStateException("Unexpected result type: " + result.type());
		}

		Node graphic;

		if (result.decodeStatus() == null) {
			graphic = new ImageView(image);
		} else {
			graphic = new Group(new ImageView(image), new ImageView(Images.IMAGE_ERROR_OVERLAY16));
		}
		return graphic;
	}

}
