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

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.FSDirectory;

import de.carne.filescanner.core.FileScannerResult;
import de.carne.filescanner.core.transfer.StringTextResultRenderer;
import de.carne.util.IOUtils;
import de.carne.util.Nanos;
import de.carne.util.Strings;
import de.carne.util.logging.Log;

/**
 * Search index service for scan result indexing and searching.
 */
class SearchIndex implements AutoCloseable {

	private static final Log LOG = new Log(SearchIndex.class);

	private static final String ID_INDEX_FIELD = "id_index";

	private static final String ID_VALUE_FIELD = "id_value";

	private static final String CONTENT_FIELD = "content";

	private static final Sort REVERSE_INDEXORDER = new Sort(new SortField(null, Type.DOC, true));

	private final HashMap<Integer, FileScannerResult> idMap = new HashMap<>();

	private Path indexPath = null;

	private DirectoryReader reader = null;

	private IndexSearcher searcher = null;

	public synchronized boolean isReady() {
		return this.reader != null;
	}

	@SuppressWarnings("resource")
	public synchronized void rebuild(FileScannerResult result) throws IOException, InterruptedException {
		close();

		Nanos nanos = new Nanos();
		Path indexDirectoryPath = Files.createTempDirectory(SearchIndex.class.getSimpleName());

		LOG.debug(null, "Re-building index: Index path ''{0}''", indexDirectoryPath);

		FSDirectory indexDirectory = null;
		IndexWriter indexWriter = null;

		try {
			indexDirectory = FSDirectory.open(indexDirectoryPath);

			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(createAnalyzer()).setOpenMode(OpenMode.CREATE);

			indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
			rebuildIndexHelper(indexWriter, result);
			indexWriter.commit();
			indexWriter.close();
			this.reader = DirectoryReader.open(indexDirectory);
			this.searcher = new IndexSearcher(this.reader);
			this.indexPath = indexDirectoryPath;
		} catch (IOException e) {
			if (indexWriter != null && indexWriter.isOpen()) {
				closeIndexResource("index writer", indexWriter);
			}
			discardIndex(indexDirectoryPath);
			throw e;
		}
		LOG.info(null, "Re-building index done: Elapsed time {0}", nanos.toMillisString());
	}

	private void rebuildIndexHelper(IndexWriter writer, FileScannerResult result)
			throws IOException, InterruptedException {
		int resultId = updateIdMap(result);
		String resultContent = getResultContent(result);
		Document document = new Document();

		document.add(new IntPoint(ID_INDEX_FIELD, resultId));
		document.add(new StoredField(ID_VALUE_FIELD, resultId));
		document.add(new TextField(CONTENT_FIELD, resultContent, Store.NO));
		writer.addDocument(document);
		for (FileScannerResult child : result.children()) {
			rebuildIndexHelper(writer, child);
		}
	}

	@Override
	public synchronized void close() {
		this.searcher = null;
		closeIndexResource("index reader", this.reader);
		this.reader = null;
		if (this.indexPath != null) {
			discardIndex(this.indexPath);
			this.indexPath = null;
		}
		this.idMap.clear();
	}

	private void closeIndexResource(String name, Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				LOG.error(e, null, "An error occurred while closing {0}: Index path ''{1}''", name, this.indexPath);
			}
		}
	}

	private Analyzer createAnalyzer() {
		return new StandardAnalyzer();
	}

	private static void discardIndex(Path indexDirectoryPath) {
		if (indexDirectoryPath != null) {
			try {
				IOUtils.deleteDirectory(indexDirectoryPath);
			} catch (IOException e) {
				LOG.error(e, null, "Deleting index directory ''{0}'' failed", indexDirectoryPath);
			}
		}
	}

	private static String getResultContent(FileScannerResult result) throws IOException, InterruptedException {
		StringTextResultRenderer content = new StringTextResultRenderer();

		content.renderText(result.title());
		content.renderBreak();
		result.render(content);
		return content.toString();
	}

	private int updateIdMap(FileScannerResult result) {
		int resultId;

		synchronized (this.idMap) {
			resultId = this.idMap.size();
			this.idMap.put(resultId, result);
			result.setData(resultId);
		}
		return resultId;
	}

	public synchronized FileScannerResult search(FileScannerResult start, String queryString, boolean next)
			throws IOException {
		SimpleQueryParser searchQueryParser = new SimpleQueryParser(createAnalyzer(), CONTENT_FIELD);
		Query searchQuery = (Strings.notEmpty(queryString) ? searchQueryParser.parse(queryString) : null);
		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
		int startId;

		if (start != null) {
			startId = start.getData(Integer.class).intValue();
		} else if (next) {
			startId = -1;
		} else {
			startId = this.idMap.size();
		}

		if (searchQuery != null) {
			queryBuilder.add(searchQuery, Occur.MUST);
		}
		if (next) {
			queryBuilder.add(IntPoint.newRangeQuery(ID_INDEX_FIELD, startId + 1, Integer.MAX_VALUE), Occur.MUST);
		} else {
			queryBuilder.add(IntPoint.newRangeQuery(ID_INDEX_FIELD, 0, startId - 1), Occur.MUST);
		}

		BooleanQuery query = queryBuilder.build();

		TopFieldDocs searchResult = this.searcher.search(query, 1, (next ? Sort.INDEXORDER : REVERSE_INDEXORDER));
		FileScannerResult found = null;

		if (searchResult.scoreDocs.length > 0) {
			assert searchResult.scoreDocs.length == 1;

			Document document = this.searcher.doc(searchResult.scoreDocs[0].doc);

			found = this.idMap.get(document.getField(ID_VALUE_FIELD).numericValue().intValue());
		}
		return found;
	}

}
