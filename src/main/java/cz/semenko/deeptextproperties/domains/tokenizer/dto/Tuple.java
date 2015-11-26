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
		setSrc(src);
		setRight(tgt);
	}
	
	/** Constructor */
	public Tuple(String src, String tgt, Integer occurrences) {
		setSrc(src);
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
			.append(this.getSrc(), o.getSrc())
			.append(this.getRight(), o.getRight())
			.toComparison();
	}
	
// getters and setters //
	
	/**
	 * See {@link Tuple#left}
	 */
	public String getSrc() {
		return left;
	}

	/**
	 * See {@link Tuple#left}
	 */
	public void setSrc(String src) {
		this.left = src;
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
	public void setRight(String right) {
		this.right = right;
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
