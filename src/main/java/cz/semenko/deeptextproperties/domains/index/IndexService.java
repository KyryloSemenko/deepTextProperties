package cz.semenko.deeptextproperties.domains.index;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import cz.semenko.deeptextproperties.domains.tokenizer.dto.Tuple;

/**
 * {@link Service} for Lucene indexer
 * @author Kyrylo Semenko kyrylo.semenko@gmail.com
 */
@Service
public class IndexService {
	
	private static final Logger logger = LoggerFactory.getLogger(IndexService.class);

	/** Name of the document field */
	private static final String FIELD_NUM = "num";

	/** Name of the document field */
	private static final String FIELD_FOL = "fol";

	/** Name of the document field */
	private static final String FIELD_PREV = "prev";
	
	/** Key in properties file. Value is Lucene index directory path in file system */
	private static final String INDEX_DIRECTORY_KEY = "INDEX_DIRECTORY_KEY";
	
	/** Just "" */
	private static final String EMPTY_STRING = "";

	/** Just 1 as a number of occurrences */
	private static final int ONE = 1;

	@Autowired
	private Environment environment;
	
	/** See {@link IndexWriter} */
	private IndexWriter indexWriter;

	/** See {@link StandardAnalyzer} */
	private StandardAnalyzer standardAnalyzer;
	
	/** Path to index directory */
	private String indexDirectoryPath;
	
	/** See {@link FSDirectory} */
	private FSDirectory fsDirectory;
	
	/**
	 * Initialisation of Lucene index
	 */
	@PostConstruct
	void init() {
		try {
			if (environment != null) {
				setIndexDirectoryPath(environment.getProperty(INDEX_DIRECTORY_KEY));
			} else {
				// indexDirectoryPath can be defined in test directly by setter
			}
			Path indexPath = FileSystems.getDefault().getPath(indexDirectoryPath);
			File dir = indexPath.toFile();
			if (!dir.exists()) {
				boolean success = dir.mkdirs();
				if (!success) {
					throw new RuntimeException("Directory " + indexPath.toString() + " can not be created.");
				}
				logger.info("Indexer directory created: " + dir.getCanonicalPath());
			}
			fsDirectory = FSDirectory.open(indexPath);
			standardAnalyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			
			indexWriter = new IndexWriter(fsDirectory, config);
			
			logger.info("Apache Lucene indexer configured. Index directory: " + dir.getCanonicalPath());
		} catch (Exception e) {
			throw new RuntimeException("Indexer initialization failed. Message: " + e.getMessage(), e);
		}
	}
	
	/**
	 * See {@link IndexService#indexDirectoryPath}
	 */
	public void setIndexDirectoryPath(String indexDirectoryPath) {
		this.indexDirectoryPath = indexDirectoryPath;
	}

	/**
	 * Create {@link Document} for each {@link Tuple} and add it to index.<br>
	 */
	public void append(List<Tuple> tuples) {
		try {
			Document document = new Document();
			Field previouseFiled = new StringField(FIELD_PREV, EMPTY_STRING, Store.YES);
			document.add(previouseFiled);
			Field followingField = new StringField(FIELD_FOL, EMPTY_STRING, Store.YES);
			document.add(followingField);
			Field numField = new IntField(FIELD_NUM, ONE, Field.Store.YES);
			document.add(numField);
			
			for (Tuple tuple : tuples) {
				previouseFiled.setStringValue(tuple.getPrev());
				followingField.setStringValue(tuple.getFol());
				numField.setIntValue(tuple.getNum());
				try {
					indexWriter.addDocument(document);
				} catch (Exception e) {
					logger.error("Document addition failed. Tuple: " + tuple);
					throw new RuntimeException(e);
				}
			}
			
			indexWriter.commit();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * In case when exists tuple with same {@link Tuple#prev} and {@link Tuple#fol} in index,
	 * increase it {@link Tuple#num} in index. Else create new {@link Document} with {@link Tuple} and add it to index with {@link Tuple#num} 1.<br>
	 */
	public void appendOrIncrease(List<Tuple> tuples) {
		try {
			if (!indexWriter.isOpen()) {
				IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
				config.setOpenMode(OpenMode.CREATE_OR_APPEND);
				indexWriter = new IndexWriter(fsDirectory, config);
			}
			Document document = new Document();
			Field previouseFiled = new StringField(FIELD_PREV, EMPTY_STRING, Store.YES);
			document.add(previouseFiled);
			Field followingField = new StringField(FIELD_FOL, EMPTY_STRING, Store.YES);
			document.add(followingField);
			Field numberField = new IntField(FIELD_NUM, ONE, Field.Store.YES);
			document.add(numberField);
			
			for (Tuple tuple : tuples) {
				if (isEmpty(fsDirectory)) {
					previouseFiled.setStringValue(tuple.getPrev());
					followingField.setStringValue(tuple.getFol());
					numberField.setIntValue(tuple.getNum());
					indexWriter.addDocument(document);
					indexWriter.commit();
					continue;
				}
				DirectoryReader directoryReader = DirectoryReader.open(fsDirectory);
			    IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
			    BooleanQuery booleanQuery = createQuery(tuple);
			    ScoreDoc[] hits = indexSearcher.search(booleanQuery, 1).scoreDocs;
			    if (hits.length == 1) {
			    	Document hitDoc = indexSearcher.doc(hits[0].doc);
			    	int current = hitDoc.getField(FIELD_NUM).numericValue().intValue();
			    	// replace
			    	indexWriter.deleteDocuments(booleanQuery);
			    	previouseFiled.setStringValue(tuple.getPrev());
					followingField.setStringValue(tuple.getFol());
					numberField.setIntValue(++current);
					indexWriter.addDocument(document);
			    } else {
			    	previouseFiled.setStringValue(tuple.getPrev());
					followingField.setStringValue(tuple.getFol());
					numberField.setIntValue(tuple.getNum());
					indexWriter.addDocument(document);
			    }
			    directoryReader.close();
			    indexWriter.commit();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param fsDirectory 
	 * @return true if directory contains less then two files
	 * @throws IOException 
	 */
	private boolean isEmpty(FSDirectory fsDirectory) throws IOException {
		Path path = fsDirectory.getDirectory();
		File dir = path.toFile();
		return new File(dir.getCanonicalPath()).list().length < 2;
	}

	/**
	 * Creates query like <i>+prev:a +fol:b</i>to Lucene
	 */
	private BooleanQuery createQuery(Tuple tuple) {
		Term termLeft = new Term(FIELD_PREV, escape(tuple.getPrev()));
		PhraseQuery queryLeft = new PhraseQuery.Builder().add(termLeft).build();
		Term termRight = new Term(FIELD_FOL, escape(tuple.getFol()));
		PhraseQuery queryRight = new PhraseQuery.Builder().add(termRight).build();
		
		return new BooleanQuery.Builder()
			.setDisableCoord(true)
			.add(queryLeft, Occur.MUST)
			.add(queryRight, Occur.MUST)
			.build();
	}

	/**
	 * Calls the {@link QueryParser#escape(String)} method.
	 * Escapes Lucene special characters <i>+ - && || ! ( ) { } [ ] ^ " ~ * ? : \ /</i>
	 */
	private String escape(String string) {
		return QueryParser.escape(string);
	}
	
	private Query allDocuments() {
		return new MatchAllDocsQuery();
	}

	/**
	 * Returns all tuples from index. For tests purpose.
	 */
	public List<Tuple> findAllTuples() {
		try {
			List<Tuple> result = new ArrayList<>();
			if (isEmpty(fsDirectory)) {
				return result;
			}
			DirectoryReader ireader = DirectoryReader.open(fsDirectory);
			IndexSearcher isearcher = new IndexSearcher(ireader);
			ScoreDoc[] hits = isearcher.search(allDocuments(), Integer.MAX_VALUE).scoreDocs;
			for (int i = 0; i < hits.length; i++) {
				Document doc = isearcher.doc(hits[i].doc);
				result.add(new Tuple(doc.get(FIELD_PREV), doc.get(FIELD_FOL), Integer.valueOf(doc.get(FIELD_NUM))));
			}
			ireader.close();
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns number of documents in index
	 */
	public int indexSize() {
		try {
			if (isEmpty(fsDirectory)) {
				return 0;
			}
			DirectoryReader ireader;
			ireader = DirectoryReader.open(fsDirectory);
			return ireader.maxDoc();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
