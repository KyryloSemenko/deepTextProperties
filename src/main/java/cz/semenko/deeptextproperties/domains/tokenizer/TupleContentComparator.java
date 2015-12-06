package cz.semenko.deeptextproperties.domains.tokenizer;

import java.util.Comparator;

import cz.semenko.deeptextproperties.domains.tokenizer.dto.Tuple;

/**
 * Compares two {@link Tuple} by {@link Tuple#prev} and {@link Tuple#fol}
 * @author Kyrylo Semenko
 *
 */
public class TupleContentComparator implements Comparator<Tuple> {
	@Override
	public int compare(Tuple o1, Tuple o2) {
		int result = o1.getPrev().compareTo(o2.getPrev());
		if (result == 0) {
			return o1.getFol().compareTo(o2.getFol());
		}
		return result;
	}
}
