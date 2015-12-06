package cz.semenko.deeptextproperties.domains.tokenizer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import cz.semenko.deeptextproperties.domains.tokenizer.dto.Tuple;

/**
 * Tests for {@link TokenizerService}
 * @author Kyrylo Semenko
 */
public class TokenizerServiceTest {
	
	/** Test for {@link TokenizerService#tuples(String, Integer)} */
	@Test
	public void tuplesTest() {
		TokenizerService tokenizerService = new TokenizerService();
		String text = "abcd";
		
		List<Tuple> result = tokenizerService.tuples(text, 2);
		assertEquals("Number of tuples", 3, result.size());
		
		result = tokenizerService.tuples(text, 3);
		assertEquals("Number of tuples", 7, result.size());
		
		result = tokenizerService.tuples(text, null);
		assertEquals("Number of tuples", 10, result.size());
	}
	
	/** Test for {@link TokenizerService#collect(List)} */
	@Test
	public void tuplesCollectTest() {
		TokenizerService tokenizerService = new TokenizerService();
		List<Tuple> srcTuples = Arrays.asList(new Tuple("a", "b", 1), new Tuple("a", "b", 1));
		Set<Tuple> result = tokenizerService.collect(srcTuples);
		assertEquals("Two tuples collected to one", 1, result.size());
		assertEquals("Two tuples collected to one", Integer.valueOf(2), result.iterator().next().getNum());
	}
	
	/** Test for {@link TokenizerService#split(String, int)} */
	@Test
	public final void splitTest() {
		TokenizerService tokenizerService = new TokenizerService();
		String param = "012.34567.89";
		
		Collection<String> result = tokenizerService.split(param, 12);
		assertEquals(param, result.iterator().next());
		
		result = tokenizerService.split(param, 100);
		assertEquals(param, result.iterator().next());
		
		result = tokenizerService.split(param, 2);
		assertEquals(6, result.size());
		
		result = tokenizerService.split(param, 1);
		assertEquals(12, result.size());
		
		result = tokenizerService.split(param, 6);
		assertEquals(3, result.size());
	}
}
