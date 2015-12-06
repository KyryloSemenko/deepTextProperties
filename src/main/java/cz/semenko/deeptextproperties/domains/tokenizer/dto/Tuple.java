package cz.semenko.deeptextproperties.domains.tokenizer.dto;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * DTO object. Represents pair of two strings. First string followed by second string. 
 * @author Kyrylo Semenko
 *
 */
public class Tuple implements Comparable<Tuple> {
	
	/** ID of database row in table */
	private Long id;
	
	/** First token in pair (left), for example <b>a</b>:bcd */
	private String prev;
	
	/** Second token in pair (right), for example a:<b>bcd</b> */
	private String fol;
	
	/** Number of num the same {@link Tuple#prev} and {@link Tuple#fol} in some place */
	private Integer num;
	
	/** Empty constructor */
	public Tuple() {}
	
	/** Constructor */
	public Tuple(String previouse, String following) {
		setPrev(previouse);
		setFol(following);
	}
	
	/**
	 * Constructor 
	 * @param previouse {@link Tuple#prev}
	 * @param following {@link Tuple#fol}
	 * @param num {@link Tuple#num}
	 */
	public Tuple(String previouse, String following, Integer num) {
		setPrev(previouse);
		setFol(following);
		setNum(num);
	}
	
	/**
	 * Constructor 
	 * @param id {@link Tuple#id}
	 * @param previouse {@link Tuple#prev}
	 * @param following {@link Tuple#fol}
	 * @param num {@link Tuple#num}
	 */
	public Tuple(Long id, String previouse, String following, Integer num) {
		setId(id);
		setPrev(previouse);
		setFol(following);
		setNum(num);
	}
	
	@Override
	public String toString() {
		return "'" + prev + ":" + fol + ":" + num + "'";
	}

	@Override
	public int compareTo(Tuple o) {
		if (o == null) {
			return 1;
		}
		return new CompareToBuilder()
			.append(this.getPrev(), o.getPrev())
			.append(this.getFol(), o.getFol())
			.toComparison();
	}
	
// getters and setters //
	
	/**
	 * See {@link Tuple#id}
	 */
	public Long getId() {
		return id;
	}

	/**
	 * See {@link Tuple#id}
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * See {@link Tuple#prev}
	 */
	public String getPrev() {
		return prev;
	}

	/**
	 * See {@link Tuple#prev}
	 */
	public void setPrev(String value) {
		this.prev = value;
	}

	/**
	 * See {@link Tuple#fol}
	 */
	public String getFol() {
		return fol;
	}

	/**
	 * See {@link Tuple#fol}
	 */
	public void setFol(String value) {
		this.fol = value;
	}

	/**
	 * See {@link Tuple#num}
	 */
	public Integer getNum() {
		return num;
	}

	/**
	 * See {@link Tuple#num}
	 */
	public void setNum(Integer num) {
		this.num = num;
	}

}
