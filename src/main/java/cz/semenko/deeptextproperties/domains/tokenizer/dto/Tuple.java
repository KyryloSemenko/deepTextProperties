package cz.semenko.deeptextproperties.domains.tokenizer.dto;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * DTO object. Represents pair of two strings. First string followed by second string. 
 * @author Kyrylo Semenko
 *
 */
public class Tuple implements Comparable<Tuple> {
	/** First token in pair */
	private String left;
	
	/** Second token in pair */
	private String right;
	
	/** Number of occurrences */
	private Integer occurrences;
	
	/** Empty constructor */
	public Tuple() {}
	
	/** Constructor */
	public Tuple(String src, String tgt) {
		setLeft(src);
		setRight(tgt);
	}
	
	/** Constructor */
	public Tuple(String src, String tgt, Integer occurrences) {
		setLeft(src);
		setRight(tgt);
		setOccurrences(occurrences);
	}
	
	@Override
	public String toString() {
		return "'" + left + ":" + right + ":" + occurrences + "'";
	}

	@Override
	public int compareTo(Tuple o) {
		if (o == null) {
			return 1;
		}
		return new CompareToBuilder()
			.append(this.getLeft(), o.getLeft())
			.append(this.getRight(), o.getRight())
			.toComparison();
	}
	
// getters and setters //
	
	/**
	 * See {@link Tuple#left}
	 */
	public String getLeft() {
		return left;
	}

	/**
	 * See {@link Tuple#left}
	 */
	public void setLeft(String value) {
		this.left = value;
	}

	/**
	 * See {@link Tuple#right}
	 */
	public String getRight() {
		return right;
	}

	/**
	 * See {@link Tuple#right}
	 */
	public void setRight(String value) {
		this.right = value;
	}

	/**
	 * See {@link Tuple#occurrences}
	 */
	public Integer getOccurrences() {
		return occurrences;
	}

	/**
	 * See {@link Tuple#occurrences}
	 */
	public void setOccurrences(Integer occurrences) {
		this.occurrences = occurrences;
	}

}
