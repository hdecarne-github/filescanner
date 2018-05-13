/*
 * Copyright (c) 2007-2018 Holger de Carne and contributors, All Rights Reserved.
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

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import de.carne.boot.Exceptions;
import de.carne.boot.check.Nullable;
import de.carne.boot.logging.Log;
import de.carne.filescanner.engine.FileScannerResult;
import de.carne.filescanner.engine.transfer.RenderOutput;
import de.carne.filescanner.engine.transfer.RenderStyle;
import de.carne.filescanner.engine.transfer.StringRenderer;
import de.carne.io.Closeables;
import de.carne.nio.file.FileUtil;
import de.carne.nio.file.attribute.FileAttributes;
import de.carne.util.SystemProperties;

final class SearchIndex implements AutoCloseable {

	private static final Log LOG = new Log();

	private static final int MAX_INDEX_LENGTH = SystemProperties.intValue(SearchIndex.class, ".maxIndexLength", 4096);

	private static final String FIELD_ID = "id";
	private static final String FIELD_KEY_STORED = "key";
	private static final String FIELD_END_STORED = "end";
	private static final String FIELD_CONTENT = "content";

	private static final Sort SORT_FORWARD = new Sort(new SortField(FIELD_ID, SortField.Type.STRING, false));
	private static final Sort SORT_BACKWARD = new Sort(new SortField(FIELD_ID, SortField.Type.STRING, true));

	private final Path indexPath;
	private final FSDirectory indexDirectory;
	@Nullable
	private Updater indexUpdater = null;
	@Nullable
	private Searcher indexSearcher = null;

	public SearchIndex() throws IOException {
		this.indexPath = Files.createTempDirectory(getClass().getSimpleName(),
				FileAttributes.userDirectoryDefault(FileUtil.tmpDir()));
		this.indexDirectory = FSDirectory.open(this.indexPath);

		LOG.info("Created search index ''{0}''", this.indexPath);
	}

	public void addResult(FileScannerResult result) {
		try {
			Updater updater = getUpdater();

			addResultHelper(updater, result, true);
			updater.commit();
		} catch (InterruptedException e) {
			Exceptions.ignore(e);
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			LOG.error(e, "Failed to add result to search index ''{0}''", this.indexPath);
		}
	}

	private void addResultHelper(Updater updater, FileScannerResult result, boolean checkForUpdate)
			throws IOException, InterruptedException {
		BytesRef resultKey = new BytesRef(result.key());
		long resultEnd = result.end();
		Document currentDocument = (checkForUpdate ? updater.getDocument(resultKey) : null);
		boolean processResultChildren;
		boolean checkChildrenForUpdate;

		if (currentDocument == null) {
			LOG.debug("Adding result ''{0}'' to search index", result);

			updater.addDocument(buildDocument(resultKey, resultEnd, getResultContent(result)));
			processResultChildren = true;
			checkChildrenForUpdate = false;
		} else if (currentDocument.getField(FIELD_END_STORED).numericValue().longValue() != resultEnd) {
			LOG.debug("Updating result ''{0}'' in search index", result);

			updater.updateDocument(resultKey, buildDocument(resultKey, resultEnd, getResultContent(result)));
			processResultChildren = true;
			checkChildrenForUpdate = true;
		} else {
			LOG.debug("Result ''{0}'' is already up-to-date in search index", result);

			processResultChildren = result.type() == FileScannerResult.Type.INPUT;
			checkChildrenForUpdate = true;
		}
		if (processResultChildren) {
			for (FileScannerResult resultChild : result.children()) {
				addResultHelper(updater, resultChild, checkChildrenForUpdate);
			}
		}
	}

	private String getResultContent(FileScannerResult result) throws IOException, InterruptedException {
		@SuppressWarnings("resource")
		StringRenderer resultContent = new StringRenderer(MAX_INDEX_LENGTH);

		try {
			resultContent.emitText(RenderStyle.NORMAL, result.name(), true);
			RenderOutput.render(result, resultContent);
		} catch (InterruptedIOException e) {
			LOG.debug(e, "Result ''{0}'' content truncated at length {1}", result, e.bytesTransferred);
		} finally {
			resultContent.close();
		}
		return resultContent.toString();
	}

	public void seal() {
		try {
			Closeables.close(this.indexUpdater);
		} catch (IOException e) {
			LOG.error(e, "Failed to close updater for index ''{0}''", this.indexPath);
		}
	}

	@Nullable
	public byte[] searchFoward(FileScannerResult start, String query) throws IOException {
		return getSearcher().search(new BytesRef(start.key()), null, query, SORT_FORWARD);
	}

	@Nullable
	public byte[] searchBackward(FileScannerResult start, String query) throws IOException {
		return getSearcher().search(null, new BytesRef(start.key()), query, SORT_BACKWARD);
	}

	@Override
	public synchronized void close() {
		try {
			LOG.info("Closing and discarding search index ''{0}''...", this.indexPath);

			Closeables.closeAll(this.indexUpdater, this.indexSearcher);
			FileUtil.delete(this.indexPath);
		} catch (IOException e) {
			LOG.error(e, "Failed to close and discard search index ''{0}''", this.indexPath);
		}
	}

	private Document buildDocument(BytesRef resultKey, long resultEnd, String resultContent) {
		Document document = new Document();

		document.add(new SortedDocValuesField(FIELD_ID, resultKey));
		document.add(new StoredField(FIELD_KEY_STORED, resultKey));
		document.add(new StoredField(FIELD_END_STORED, resultEnd));
		document.add(new TextField(FIELD_CONTENT, resultContent, Store.NO));
		return document;
	}

	private synchronized Updater getUpdater() throws IOException {
		Updater checkedUpdater = this.indexUpdater;

		if (checkedUpdater == null) {
			checkedUpdater = this.indexUpdater = new Updater(this.indexDirectory);
		}
		return checkedUpdater;
	}

	private synchronized Searcher getSearcher() throws IOException {
		Searcher checkedSearcher = this.indexSearcher;

		if (checkedSearcher == null) {
			checkedSearcher = this.indexSearcher = new Searcher(this.indexDirectory);
		}
		return checkedSearcher;
	}

	private static class Updater implements Closeable {

		private final IndexWriter indexWriter;
		private final SearcherManager searcherManager;

		Updater(FSDirectory indexDirectory) throws IOException {
			this.indexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig().setOpenMode(OpenMode.CREATE));
			this.searcherManager = new SearcherManager(this.indexWriter, null);
		}

		@Nullable
		public Document getDocument(BytesRef resultKey) throws IOException {
			BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

			queryBuilder.add(SortedDocValuesField.newSlowExactQuery(FIELD_ID, resultKey), Occur.MUST);

			IndexSearcher indexSearcher = this.searcherManager.acquire();
			Document document = null;

			try {
				TopDocs searchResult = indexSearcher.search(queryBuilder.build(), 1);

				if (searchResult.totalHits != 0) {
					document = indexSearcher.doc(searchResult.scoreDocs[0].doc);
				}
			} finally {
				this.searcherManager.release(indexSearcher);
			}
			return document;
		}

		public void addDocument(Document document) throws IOException {
			this.indexWriter.addDocument(document);
		}

		public void updateDocument(BytesRef resultKey, Document document) throws IOException {
			this.indexWriter.updateDocument(new Term(FIELD_ID, resultKey), document);
		}

		public void commit() throws IOException {
			try {
				this.indexWriter.commit();
			} finally {
				this.searcherManager.maybeRefresh();
			}
		}

		@Override
		public void close() throws IOException {
			Closeables.closeAll(this.searcherManager, this.indexWriter);
		}

	}

	private static class Searcher implements Closeable {

		private DirectoryReader indexReader;
		@Nullable
		private IndexSearcher cachedIndexSearcher = null;

		Searcher(FSDirectory indexDirectory) throws IOException {
			this.indexReader = DirectoryReader.open(indexDirectory);
		}

		@Nullable
		public byte[] search(@Nullable BytesRef resultKeyFrom, @Nullable BytesRef resultKeyTo, String queryString,
				Sort sort) throws IOException {
			SimpleQueryParser queryParser = new SimpleQueryParser(new StandardAnalyzer(), FIELD_CONTENT);
			Query query = queryParser.parse(queryString);
			BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

			queryBuilder.add(SortedDocValuesField.newSlowRangeQuery(FIELD_ID, resultKeyFrom, resultKeyTo,
					resultKeyFrom == null, resultKeyTo == null), Occur.MUST);
			queryBuilder.add(query, Occur.MUST);

			IndexSearcher indexSearcher = getIndexSearcher();
			TopDocs searchResult = indexSearcher.search(queryBuilder.build(), 1, sort);
			byte[] resultKey = null;

			if (searchResult.totalHits != 0) {
				Document document = indexSearcher.doc(searchResult.scoreDocs[0].doc);
				BytesRef storedResultKey = document.getField(FIELD_KEY_STORED).binaryValue();

				resultKey = new byte[storedResultKey.length];
				System.arraycopy(storedResultKey.bytes, storedResultKey.offset, resultKey, 0, storedResultKey.length);
			}
			return resultKey;
		}

		@Override
		public void close() throws IOException {
			this.indexReader.close();
		}

		private synchronized IndexSearcher getIndexSearcher() throws IOException {
			DirectoryReader newIndexReader = DirectoryReader.openIfChanged(this.indexReader);

			if (newIndexReader != null) {
				DirectoryReader oldIndexReader = this.indexReader;

				this.indexReader = newIndexReader;
				this.cachedIndexSearcher = null;
				oldIndexReader.close();
			}

			IndexSearcher checkedIndexSearcher = this.cachedIndexSearcher;

			if (checkedIndexSearcher == null) {
				checkedIndexSearcher = this.cachedIndexSearcher = new IndexSearcher(this.indexReader);
			}
			return checkedIndexSearcher;
		}

	}

}
