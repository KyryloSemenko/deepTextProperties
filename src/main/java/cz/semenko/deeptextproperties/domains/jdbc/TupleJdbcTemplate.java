package cz.semenko.deeptextproperties.domains.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cz.semenko.deeptextproperties.domains.tokenizer.dto.Tuple;

/**
 * {@link JdbcTemplate} for access to table TUPLE_ROW
 * @author Kyrylo Semenko kyrylo.semenko@gmail.com
 */
@Service
public class TupleJdbcTemplate {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/** Insert {@link Tuple} batch template */
	String insertBatch = "INSERT INTO tuple_row (\"left\", \"right\", occurrences) VALUES (?, ?, ?)";
	
	/** Inserts list of {@link Tuple}s as one batch insert */
	public int[] insert(final List<Tuple> tuples) {
		return jdbcTemplate.batchUpdate(insertBatch, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Tuple tuple = tuples.get(i);
				ps.setString(1, tuple.getLeft());
				ps.setString(2, tuple.getRight());
				ps.setInt(3, tuple.getOccurrences());
			}
			
			@Override
			public int getBatchSize() {
				return tuples.size();
			}
		});
	}

}
