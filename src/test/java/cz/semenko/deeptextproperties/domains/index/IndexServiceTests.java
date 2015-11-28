package cz.semenko.deeptextproperties.domains.index;

import static org.junit.Assert.fail;

import java.util.List;

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
			IndexService indexService = new IndexService();
			indexService.setIndexDirectoryPath(folder.getRoot().getCanonicalPath());
			indexService.init();
			TokenizerService tokenizerService = new TokenizerService();
			String text = "The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails). Whether the deletion is successful or not is not checked by this rule. No exception will be thrown in case the deletion fails.";
			List<Tuple> tuples = tokenizerService.tuples(text, (short) 30);
			indexService.append(tuples);
			indexService.append(tuples);
			indexService.append(tuples);
			System.out.println("TODO");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
