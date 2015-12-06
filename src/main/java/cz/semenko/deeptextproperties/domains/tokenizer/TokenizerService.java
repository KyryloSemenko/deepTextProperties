package cz.semenko.deeptextproperties.domains.tokenizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import cz.semenko.deeptextproperties.domains.tokenizer.dto.Tuple;

/**
 * {@link Service} that helps to find out {@link Tuple}s in text
 * @author Kyrylo Semenko
 */
@Service
public class TokenizerService {
	
	/** Just . character */
	private static final String DOT = ".";

	/**
	 * Finds out {@link Tuple}s in text.<br>
	 * For example from word <i>one</i> the method extracted next tuples: [n:e, o:n, o:ne, on:e].<br>
	 * And word <i>abcde</i> will be break into next tuples: [d:e, c:d, c:de, cd:e, b:c, b:cd, b:cde, bc:d, bc:de, bcd:e, a:b, a:bc, a:bcd, a:bcde, ab:c, ab:cd, ab:cde, abc:d, abc:de, abcd:e]
	 * @param text to be tokenized
	 * @param maxTupleLength max number of characters in result {@link Tuple#prev} plus {@link Tuple#fol}.<br>
	 * If null - method return all possible tuples without constraint.<br>
	 * If not null, for example 2 and text is <i>abcd</i>, result is ['d:e', 'c:d', 'b:c', 'a:b'].
	 */
	public List<Tuple> tuples(String text, Integer maxTupleLength) {
		
		List<Tuple>tuples = new ArrayList<Tuple>();
		
		for (int i = text.length() - 2; i >= 0; i--) {
			int previouseStartPos = i;
			
			int maxSrcLength = text.length() - previouseStartPos - 1;
			for (int srcLength = 1; srcLength <= maxSrcLength; srcLength++) {
				int maxTgtLength = text.length() - previouseStartPos - srcLength;
				for (int tgtLength = 1; tgtLength <= maxTgtLength; tgtLength++) {
					String src = text.substring(previouseStartPos, previouseStartPos + srcLength);
					int tgtStartPos = previouseStartPos + src.length();
					String tgt = text.substring(tgtStartPos, tgtStartPos + tgtLength);
					if (maxTupleLength != null) {
						if (src.length() + tgt.length() > maxTupleLength) {
							continue;
						}
					}
					Tuple tuple = new Tuple(src, tgt, 1);
					tuples.add(tuple);
				}
			}
		}
		return tuples;
	}

	/**
	 * Calls {@link TokenizerService#tuples(String, Integer)} and then collects result to unique {@link Tuple}s,
	 * see {@link TokenizerService#collect(List)}
	 */
	public Set<Tuple> tuplesUnique(String text, Integer maxTupleLength) {
		List<Tuple> tuples = tuples(text, maxTupleLength);
		return collect(tuples);
	}

	/**
	 * Collects collection of {@link Tuple}s into unique result. Compares {@link Tuple}s by {@link TupleContentComparator}.<br>
	 * For example from ['a:b:1', 'a:b:1'] it creates ['a:b:2']
	 */
	public Set<Tuple> collect(List<Tuple> srcTuples) {
		TupleContentComparator comparator = new TupleContentComparator();
		Set<Tuple> result = new HashSet<>();
		label:
		for (int i = 0; i < srcTuples.size(); i++) {
			Tuple tuple = srcTuples.get(i);
			for (Tuple resultTuple : result) {
				if (comparator.compare(tuple, resultTuple) == 0) {
					if (tuple.getNum() == null) {
						resultTuple.setNum(1);
					} else {
						resultTuple.setNum(resultTuple.getNum() + 1);
					}
					break label;
				}
			}
			result.add(new Tuple(tuple.getId(), tuple.getPrev(), tuple.getFol(), tuple.getNum()));
		}
		return result;
	}

	/**
	 * In case when length of the line is larger then maxLength, divides line to smaller strings.<br>
	 * For example string 012.34567.89 will be split on [012., 34567., 89] strings if maxLength parameter is 6.
	 */
	public Collection<String> split(String line, int maxLength) {
		if (line == null) {
			return null;
		}
		if (line.length() <= maxLength) {
			return Collections.singletonList(line);
		}
		List<String> result = new ArrayList<>();
		int fromPos = 0;
		int toPos = 0;
		int separatorPos = 0;
		while(toPos < line.length()) {
			if (fromPos + maxLength <= line.length() - 1) {
				separatorPos = line.substring(fromPos,  fromPos + maxLength).lastIndexOf(DOT);
			} else {
				result.add(line.substring(fromPos));
				break;
			}
			if (separatorPos == -1) {
				toPos = fromPos + maxLength;
			} else {
				toPos = fromPos + separatorPos + 1;
			}
			result.add(line.substring(fromPos, toPos));
			fromPos = toPos;
		}
		return result;
	}

}
