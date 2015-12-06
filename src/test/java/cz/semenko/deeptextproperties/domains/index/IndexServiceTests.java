package cz.semenko.deeptextproperties.domains.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import cz.semenko.deeptextproperties.domains.tokenizer.TokenizerService;
import cz.semenko.deeptextproperties.domains.tokenizer.dto.Tuple;

/**
 * Test for {@link IndexService}
 * 
 * @author Kyrylo Semenko kyrylo.semenko@gmail.com
 */
public class IndexServiceTests {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	/** Test for {@link IndexService#append(List)} */
	@Test
	public void appendTest() {
		try {
			IndexService indexService = initIndexService();
			TokenizerService tokenizerService = new TokenizerService();
			String text = "one";
			List<Tuple> tuples = tokenizerService.tuples(text, 30);
			indexService.append(tuples);
			int size = indexService.indexSize();
			assertEquals("Number of documents", 4, size);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** Test for {@link IndexService#appendOrIncrease(List)} */
	@Test
	public void appendOrIncreaseTest() {
		try {
			IndexService indexService = initIndexService();
			List<Tuple> tuples = new ArrayList<>();
			tuples.add(new Tuple("a", "b", 1));
			tuples.add(new Tuple("a", "b", 1));
			tuples.add(new Tuple("a", "b", 1));
			indexService.appendOrIncrease(tuples);
			List<Tuple> result = indexService.findAllTuples();
			assertEquals("Number of documents", 1, result.size());
			assertEquals("Increased value", (Integer) 3, result.get(0).getNum());
			
			tuples = new ArrayList<>();
			tuples.add(new Tuple("a", "b", 1));
			tuples.add(new Tuple("c", "d", 1));
			tuples.add(new Tuple("c", "d", 1));
			indexService.appendOrIncrease(tuples);
			result = indexService.findAllTuples();
			assertEquals("Number of documents", 2, result.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/** Instantiates and initialises {@link IndexService} with new empty index directory */
	private IndexService initIndexService() throws IOException {
		IndexService indexService = new IndexService();
		indexService.setIndexDirectoryPath(folder.getRoot().getCanonicalPath());
		indexService.init();
		return indexService;
	}
	
	/** Calculates speed of {@link IndexService#append(List)} method */
	@Test
	@Ignore("Performance test")
	public void appendSpeedTest() {
		try {
			IndexService indexService = initIndexService();
			TokenizerService tokenizerService = new TokenizerService();
			// length = 271
			String text = "The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails). Whether the deletion is successful or not is not checked by this rule. No exception will be thrown in case the deletion fails.";
			List<Tuple> tuples = tokenizerService.tuples(text, 20);
			Date appendStartDate = new Date();
			for (int i = 0; i < 10000; i++) {
				indexService.append(tuples);
				System.out.println("i: " + i + " " + new Date());
			}
			Date appendStopDate = new Date();
			System.out.println("Update in ms: " + (appendStopDate.getTime() - appendStartDate.getTime()));
			System.out.println("Number of documents: " + indexService.indexSize());
			// 500 rows * 271 characters = 24510000 documents and 527940 ms (8.8 minutes)
			// 5700 rows * 271 characters = 4 GB index size and 1 hour 50 minutes
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** Calculates speed of {@link IndexService#appendOrIncrease(List)} method */
	@Test
	@Ignore
	public void appendOrIncreaseSpeedTest() {
		try {
			IndexService indexService = initIndexService();
			TokenizerService tokenizerService = new TokenizerService();
			String text = "The TemporaryFolder";
			Date increaseStartDate = new Date();
			indexService.appendOrIncrease(tokenizerService.tuples(text, 20));
			Date increaseStopDate = new Date();
			System.out.println("Increase: " + (increaseStopDate.getTime() - increaseStartDate.getTime()));
			System.out.println(indexService.indexSize());
			// 1140 documents in 190 sec.
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
}
