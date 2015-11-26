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
	public void test() {
		TokenizerService tokenizerService = new TokenizerService();
		String text = "abcd";
		Short maxTokenLength = 2;
		List<Tuple> result = tokenizerService.tuples(text, maxTokenLength);
		assertEquals("Number of tuples", 10, result.size());
	}

}
