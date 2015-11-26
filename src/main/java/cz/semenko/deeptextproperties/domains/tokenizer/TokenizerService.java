package cz.semenko.deeptextproperties.domains.tokenizer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import cz.semenko.deeptextproperties.domains.tokenizer.dto.Tuple;

/**
 * {@link Service} that helps to break input text to parts
 * @author Kyrylo Semenko
 *
 */
@Service
public class TokenizerService {
	/**
	 * Find out {@link Tuple}s, see {@link TokenizerService}, and attach it to tuples parameter.<br>
	 * @param text to be tokenized
	 * @param maxTokenLength max number of characters in result {@link Tuple#left} or {@link Tuple#right}
	 */
	public List<Tuple> tuples(String text, Short maxTokenLength) {
		
		List<Tuple>tuples = new ArrayList<Tuple>();
		
		for (int i = text.length() - 2; i >= 0; i--) {
			int srcStartPos = i;
			
			int maxSrcLength = text.length() - srcStartPos - 1;
			for (int srcLength = 1; srcLength <= maxSrcLength; srcLength++) {
				int maxTgtLength = text.length() - srcStartPos - srcLength;
				for (int tgtLength = 1; tgtLength <= maxTgtLength; tgtLength++) {
					String src = text.substring(srcStartPos, srcStartPos + srcLength);
//					if (src.length() == maxTokenLength) {
//						continue;
//					}
					int tgtStartPos = srcStartPos + src.length();
					String tgt = text.substring(tgtStartPos, tgtStartPos + tgtLength);
//					if (tgt.length() == maxTokenLength) {
//						continue;
//					}
					
					Tuple tuple = new Tuple(src, tgt, 0);

					tuples.add(tuple);
				}
			}
		}
		return tuples;
	}

}
