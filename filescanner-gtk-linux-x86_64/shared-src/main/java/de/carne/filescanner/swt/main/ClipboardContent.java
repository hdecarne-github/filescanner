/*
 * Copyright (c) 2007-2022 Holger de Carne and contributors, All Rights Reserved.
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
package de.carne.filescanner.swt.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.ImageData;

import de.carne.filescanner.engine.transfer.TransferSource;
import de.carne.nio.file.FileUtil;
import de.carne.nio.file.attribute.FileAttributes;
import de.carne.util.Late;

abstract class ClipboardContent {

	protected final Transfer transfer;

	protected ClipboardContent(Transfer transfer) {
		this.transfer = transfer;
	}

	public static ClipboardContent textContent() {
		return new ClipboardContent(TextTransfer.getInstance()) {

			private Late<String> textData = new Late<>();

			@Override
			public void prepareTransfer(ProgressCallback progress, TransferSource transferSource) throws IOException {
				try (OutputStream textDataStream = new ProgressOutputStream(progress, new ByteArrayOutputStream())) {
					transferSource.transfer(textDataStream);
					this.textData.set(textDataStream.toString());
				}
			}

			@Override
			public ClipboardContentHolder transfer(Clipboard clipboard) {
				clipboard.setContents(new Object[] { this.textData.get() }, new Transfer[] { this.transfer });
				return ClipboardContentHolder.KEEP;
			}

		};
	}

	public static ClipboardContent htmlContent(boolean detachable) {
		return new ClipboardContent(HTMLTransfer.getInstance()) {

			private Late<String> htmlData = new Late<>();

			@Override
			public void prepareTransfer(ProgressCallback progress, TransferSource transferSource) throws IOException {
				try (OutputStream textDataStream = new ProgressOutputStream(progress, new ByteArrayOutputStream())) {
					transferSource.transfer(textDataStream);
					this.htmlData.set(textDataStream.toString());
				}
			}

			@Override
			public ClipboardContentHolder transfer(Clipboard clipboard) {
				clipboard.setContents(new Object[] { this.htmlData.get() }, new Transfer[] { this.transfer });
				return (detachable ? ClipboardContentHolder.KEEP : ClipboardContentHolder.DISPOSE_ON_DETACH);
			}

		};
	}

	public static ClipboardContent imageContent() {
		return new ClipboardContent(ImageTransfer.getInstance()) {

			private Late<ImageData> imageData = new Late<>();

			@Override
			public void prepareTransfer(ProgressCallback progress, TransferSource transferSource) throws IOException {
				try (PipedTransferSource imageStream = new PipedTransferSource(progress, transferSource)) {
					this.imageData.set(new ImageData(imageStream));
				}
			}

			@Override
			public ClipboardContentHolder transfer(Clipboard clipboard) {
				clipboard.setContents(new Object[] { this.imageData.get() }, new Transfer[] { this.transfer });
				return ClipboardContentHolder.KEEP;
			}

		};
	}

	public static ClipboardContent fileContent(String suffix) {
		return new ClipboardContent(FileTransfer.getInstance()) {

			private Late<Path> fileData = new Late<>();

			@Override
			public void prepareTransfer(ProgressCallback progress, TransferSource transferSource) throws IOException {
				Path tmpDir = FileUtil.tmpDir();
				Path file = Files.createTempFile(tmpDir, null, suffix, FileAttributes.userFileDefault(tmpDir));

				try (OutputStream fileStream = new ProgressOutputStream(progress,
						Files.newOutputStream(file, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE))) {
					transferSource.transfer(fileStream);
				}
				this.fileData.set(file);
			}

			@Override
			public ClipboardContentHolder transfer(Clipboard clipboard) {
				Path file = this.fileData.get();

				clipboard.setContents(new Object[] { new String[] { file.toString() } },
						new Transfer[] { this.transfer });
				return ClipboardContentHolder.deleteFile(file);
			}

		};
	}

	public abstract void prepareTransfer(ProgressCallback progress, TransferSource transferSource) throws IOException;

	public abstract ClipboardContentHolder transfer(Clipboard clipboard);

}
