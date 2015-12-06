package cz.semenko.deeptextproperties.domains.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cz.semenko.deeptextproperties.Application;
import cz.semenko.deeptextproperties.domains.tokenizer.TokenizerService;
import cz.semenko.deeptextproperties.domains.tokenizer.dto.Tuple;

/**
 * Tests for {@link TupleJdbcTemplate} class
 * @author Kyrylo Semenko kyrylo.semenko@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class TupleJdbcTemplateTest {
	@Autowired
	private TupleJdbcTemplate tupleJdbcTemplate;
	@Autowired
	private TokenizerService tokenizerService;

	/** Test for {@link TupleJdbcTemplate} insert methods */
	@Test
	@Ignore("Performance test")
	public final void insertTest() {
		try {
			// length = 271 characters
			String text = "The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails). Whether the deletion is successful or not is not checked by this rule. No exception will be thrown in case the deletion fails.";
			// size = 49020 tuples
			List<Tuple> tuples = tokenizerService.tuples(text, 20);
			
			tupleJdbcTemplate.removeAll();
			int num = 10;
			
			Date start = new Date();
			for(int i = 1; i < num; i++) {
				Date startLocal = new Date();
				tupleJdbcTemplate.insert(tuples);
				System.out.println("insert template: " + (new Date().getTime() - startLocal.getTime()) + " ms");
			}
			System.out.println("insert template overall: " + (new Date().getTime() - start.getTime()));
			// insert template overall: 72138
			
			tupleJdbcTemplate.removeAll();
			
			start = new Date();
			for(int i = 1; i < num; i++) {
				Date startLocal = new Date();
				tupleJdbcTemplate.insertPreparedStatement(tuples);
				System.out.println("jdbc: " + (new Date().getTime() - startLocal.getTime()) + " ms");
			}
			System.out.println("insert jdbc overall: " + (new Date().getTime() - start.getTime()));
			// insert jdbc overall: 62939
			
			tupleJdbcTemplate.removeAll();
			
			start = new Date();
			for(int i = 1; i < num; i++) {
				Date startLocal = new Date();
				tupleJdbcTemplate.insertString(tuples);
				System.out.println("string: " + (new Date().getTime() - startLocal.getTime()) + " ms");
			}
			System.out.println("insert string overall: " + (new Date().getTime() - start.getTime()));
			// insert string overall: 40524
			// insert string overall: 41555
			// insert string overall: 43901
			// insert string overall: 40448 - reuse of statement instance
			// insert string overall: 42503 - reuse of statement instance
			
			tupleJdbcTemplate.removeAll();
			
			/* 10 000 000 tuples in 30 min. */
			/* 50 000 000 tuples in 188 min. */
			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** Test for {@link TupleJdbcTemplate} insert methods */
	@Test
//	@Ignore("Performance test")
	public final void insertUniqueTest() {
		try {
			// length = 271 characters
			String text = "The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails). Whether the deletion is successful or not is not checked by this rule. No exception will be thrown in case the deletion fails.";
			// Instead of size = 49020 tuples  now size is 1330
			Set<Tuple> tuples = tokenizerService.tuplesUnique(text, 20);
			
			tupleJdbcTemplate.removeAll();
			int num = 10;
			
			Date start = new Date();
			
			start = new Date();
			for(int i = 1; i < num; i++) {
				Date startLocal = new Date();
				tupleJdbcTemplate.insertString(tuples);
				System.out.println("string: " + (new Date().getTime() - startLocal.getTime()) + " ms");
			}
			System.out.println("insert string overall: " + (new Date().getTime() - start.getTime()));
			
			tupleJdbcTemplate.removeAll();
			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** Test for {@link TupleJdbcTemplate} select methods */
	@Test
	public final void selectTest() {
		try {
			tupleJdbcTemplate.removeAll();
			
			String text = "The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails). Whether the deletion is successful or not is not checked by this rule. No exception will be thrown in case the deletion fails.";
			Collection<Tuple> tuples = tokenizerService.tuplesUnique(text, 20);
			
			tupleJdbcTemplate.upsert(tuples);
			
			Collection<Tuple> result = tupleJdbcTemplate.getByLeftAndRight(tuples, true);
			assertEquals("Number of returned tuple objects", tuples.size(), result.size());
			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** Reads text from multiple files to database */
	@Test
	@Ignore("Research test")
	public final void corpuseTest() {
		try {
			long startTime = System.currentTimeMillis();
			File dir = new File("C:\\Users\\k\\git\\Slovicka\\Slovicka\\Data");
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (!files[i].getName().startsWith("vojna") 
						|| files[i].getName().startsWith("vojna_i_mir_2-1") 
						|| files[i].getName().startsWith("vojna_i_mir_1")) {
					continue;
				}
				System.out.println(System.currentTimeMillis() + " Start of file " + files[i].getName() + " with size " + files[i].getTotalSpace());

				BufferedReader br = new BufferedReader(new FileReader(files[i]));
				String line;
			    while ((line = br.readLine()) != null) {
			    	Collection<String> sentences = tokenizerService.split(line, 300);
			    	for (String nextLine : sentences) {
				    	System.out.println(new Date() + ": " + nextLine);
				    	Collection<Tuple> tuples = tokenizerService.tuplesUnique(nextLine, 20);
				    	tupleJdbcTemplate.upsert(tuples);
			    	}
			    }
				br.close();

				System.out.println(System.currentTimeMillis() + " End of file " + files[i].getName());
			}
			System.out.println("Total seconds: " + ((System.currentTimeMillis() - startTime)/1000));
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
