package cz.semenko.deeptextproperties.domains.index;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
	private static final String FIELD_OCCURRENCES = "occurrences";

	/** Name of the document field */
	private static final String FIELD_RIGHT = "right";

	/** Name of the document field */
	private static final String FIELD_LEFT = "left";
	
	/** Key in properties file. Value is Lucene index directory path in file system */
	private static final String INDEX_DIRECTORY_KEY = "INDEX_DIRECTORY_KEY";
	
	/** Just "" */
	private static final String EMPTY_STRING = "";

	/** Just 0 */
	private static final int ZERO = 0;

	@Autowired
	private Environment environment;
	
	/** See {@link IndexWriter} */
	private IndexWriter writer;

	/** See {@link StandardAnalyzer} */
	private StandardAnalyzer analyzer;
	
	/** Path to index directory */
	private String indexDirectoryPath;
	
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
			FSDirectory fsDirectory = FSDirectory.open(indexPath);
			analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			
			writer = new IndexWriter(fsDirectory, config);
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
			Field leftFiled = new Field(FIELD_LEFT, EMPTY_STRING, TextField.TYPE_STORED);
			document.add(leftFiled);
			Field rightField = new Field(FIELD_RIGHT, EMPTY_STRING, TextField.TYPE_STORED);
			document.add(rightField);
			Field occurrencesField = new IntField(FIELD_OCCURRENCES, ZERO, Field.Store.NO);
			document.add(occurrencesField);
			
			for (Tuple tuple : tuples) {
				leftFiled.setStringValue(tuple.getLeft());
				rightField.setStringValue(tuple.getRight());
				occurrencesField.setIntValue(tuple.getOccurrences());
				try {
					writer.addDocument(document);
				} catch (Exception e) {
					logger.error("Document addition failed. Tuple: " + tuple);
					throw new RuntimeException(e);
				}
			}
			
			writer.commit();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
