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
	 * Find out {@link Tuple}s.<br>
	 * For example word <i>one</i> will be break to next tuples: [n:e, o:n, o:ne, on:e]<br>
	 * And word <i>abcde</i> will be break to next tuples: [d:e, c:d, c:de, cd:e, b:c, b:cd, b:cde, bc:d, bc:de, bcd:e, a:b, a:bc, a:bcd, a:bcde, ab:c, ab:cd, ab:cde, abc:d, abc:de, abcd:e]
	 * @param text to be tokenized
	 * @param maxTupleLength max number of characters in result {@link Tuple#left} plus {@link Tuple#right}. If null - without constraint.
	 */
	public List<Tuple> tuples(String text, Short maxTupleLength) {
		
		List<Tuple>tuples = new ArrayList<Tuple>();
		
		for (int i = text.length() - 2; i >= 0; i--) {
			int srcStartPos = i;
			
			int maxSrcLength = text.length() - srcStartPos - 1;
			for (int srcLength = 1; srcLength <= maxSrcLength; srcLength++) {
				int maxTgtLength = text.length() - srcStartPos - srcLength;
				for (int tgtLength = 1; tgtLength <= maxTgtLength; tgtLength++) {
					String src = text.substring(srcStartPos, srcStartPos + srcLength);
					int tgtStartPos = srcStartPos + src.length();
					String tgt = text.substring(tgtStartPos, tgtStartPos + tgtLength);
					if (maxTupleLength != null) {
						if (src.length() + tgt.length() > maxTupleLength) {
							continue;
						}
					}
					Tuple tuple = new Tuple(src, tgt, 0);
					tuples.add(tuple);
				}
			}
		}
		return tuples;
	}

}
