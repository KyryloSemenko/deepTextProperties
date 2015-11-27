package cz.semenko.deeptextproperties.domains.tokenizer;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import cz.semenko.deeptextproperties.domains.tokenizer.dto.Tuple;

/**
 * Tests for {@link TokenizerService}
 * @author Kyrylo Semenko
 */
public class TokenizerServiceTest {
	
	/** Test for {@link TokenizerService#tuples(String, Short)} */
	@Test
	public void tuplesTest() {
		TokenizerService tokenizerService = new TokenizerService();
		String text = "abcd";
		
		List<Tuple> result = tokenizerService.tuples(text, null);
		assertEquals("Number of tuples", 10, result.size());
		
		result = tokenizerService.tuples(text, (short) 2);
		assertEquals("Number of tuples", 3, result.size());
		
		result = tokenizerService.tuples(text, (short) 3);
		assertEquals("Number of tuples", 7, result.size());
		
	}

}
