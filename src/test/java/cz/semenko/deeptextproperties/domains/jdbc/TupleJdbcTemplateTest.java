package cz.semenko.deeptextproperties.domains.jdbc;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

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

	/** Test for {@link TupleJdbcTemplate#insert(List)} method */
	@Test
	@Ignore("Performance test")
	public final void insertTest() {
		try {
			// length = 271 characters
			String text = "The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails). Whether the deletion is successful or not is not checked by this rule. No exception will be thrown in case the deletion fails.";
			List<Tuple> tuples = tokenizerService.tuples(text, 20);
			// size = 49020 tuples
			Date start = new Date();
			for(int i = 1; i < 1000; i++) {
				tupleJdbcTemplate.insert(tuples);
				System.out.println("i: " + i + ", count: " + tuples.size() * i + ", " + new Date());
			}
	  		Date stop = new Date();
	  		System.out.println(start);
	  		System.out.println(stop);
	  		System.out.println("duration: " + (stop.getTime() - start.getTime()) + " ms.");
/*10 000 000 tuples in 30 min.*/
	  		
	  		
/*
i: 998, count: 48921960, Tue Dec 01 00:38:20 CET 2015
i: 999, count: 48970980, Tue Dec 01 00:38:28 CET 2015
Mon Nov 30 21:30:28 CET 2015
Tue Dec 01 00:38:28 CET 2015
duration: 11279506 ms.

50 000 000 tuples in 188 min.
*/
	  		
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
