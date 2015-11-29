package cz.semenko.deeptextproperties.domains.jdbc;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

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
	public final void test() {
		try {
			Date start = new Date();
			// length = 271
			String text = "The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails). Whether the deletion is successful or not is not checked by this rule. No exception will be thrown in case the deletion fails.";
			List<Tuple> tuples = tokenizerService.tuples(text, 20);
	  		tupleJdbcTemplate.insert(tuples);
	  		Date stop = new Date();
	  		System.out.println(start);
	  		System.out.println(stop);
	  		// TODO obtain data from DB and check it
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
