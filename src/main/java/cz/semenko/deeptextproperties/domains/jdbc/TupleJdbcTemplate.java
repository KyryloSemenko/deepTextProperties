package cz.semenko.deeptextproperties.domains.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cz.semenko.deeptextproperties.domains.tokenizer.dto.Tuple;

/**
 * {@link JdbcTemplate} for access to table TUPLE
 * @author Kyrylo Semenko kyrylo.semenko@gmail.com
 */
@Repository
public class TupleJdbcTemplate {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** Reused {@link Connection} */
	Connection connection = null;
	
	String insertBatch = "INSERT INTO tuple (prev, fol, num) VALUES (?, ?, ?)";
	
	/** 
	 * Inserts list of {@link Tuple}s as one batch insert
	 * @param tuples collection of {@link Tuple} objects for insert to database
	 * @return an array of the number of rows affected by each statement
	 */
	public int[] insert(final List<Tuple> tuples) {
		
		return jdbcTemplate.batchUpdate(insertBatch, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Tuple tuple = tuples.get(i);
				ps.setString(1, tuple.getPrev());
				ps.setString(2, tuple.getFol());
				ps.setInt(3, tuple.getNum());
			}
			
			@Override
			public int getBatchSize() {
				return tuples.size();
			}
		});
	}
	
	/** 
	 * Inserts list of {@link Tuple}s as one batch insert
	 * @param tuples collection of {@link Tuple} objects for insert to database
	 */
	public void insertPreparedStatement(final List<Tuple> tuples) {
		try {
			if (connection == null) {
				DataSource ds = jdbcTemplate.getDataSource();
				Connection connection = ds.getConnection();
				connection.setAutoCommit(false);
			}
			PreparedStatement ps = connection.prepareStatement(insertBatch);
			
			for (Tuple tuple : tuples) {
				ps.setString(1, tuple.getPrev());
				ps.setString(2, tuple.getFol());
				ps.setInt(3, tuple.getNum());
				ps.addBatch();
			}
			ps.executeBatch();
			ps.clearBatch(); 
			connection.commit();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** 
	 * Inserts list of {@link Tuple}s as one batch insert
	 * @param tuples collection of {@link Tuple} objects for insert to database
	 */
	public void insertString(final Collection<Tuple> tuples) {
		StringBuilder builder = new StringBuilder("INSERT INTO tuple (prev, fol, num) VALUES ");
		try {
			if (connection == null) {
				DataSource ds = jdbcTemplate.getDataSource();
				connection = ds.getConnection();
			}
			for (Iterator<Tuple> iter = tuples.iterator(); iter.hasNext();) {
				Tuple tuple = iter.next();
				builder.append("('");
				builder.append(StringEscapeUtils.escapeSql(tuple.getPrev()));
				builder.append("','");
				builder.append(StringEscapeUtils.escapeSql(tuple.getFol()));
				builder.append("','");
				builder.append(tuple.getNum());
				builder.append("')");
				if (iter.hasNext()) {
					builder.append(",");
				} else {
					builder.append(";");
				}
			}
			
			Statement statement = connection.createStatement();
			statement.execute(builder.toString());
			statement.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Delete from tuple
	 */
	public void removeAll() {
		jdbcTemplate.execute("delete from tuple");
	}

	/**
	 * Returns {@link Tuple}s found by {@link Tuple#prev} and {@link Tuple#fol}
	 */
	public Collection<Tuple> getByLeftAndRight(Collection<Tuple> tuples, boolean orderedByText) {
		try {
			StringBuilder sql = new StringBuilder("select id,prev,fol,num from tuple where (");
			for (Iterator<Tuple> iter = tuples.iterator(); iter.hasNext();) {
				Tuple tuple = iter.next();
				sql.append("(prev='" + StringEscapeUtils.escapeSql(tuple.getPrev()) + "' and fol='" + StringEscapeUtils.escapeSql(tuple.getFol()) + "')");
				if (iter.hasNext()) {
					sql.append("or");
				}
			}
			sql.append(") ");
			if (orderedByText) {
				sql.append("order by prev,fol");
			}
			sql.append(";");
			
			if (connection == null) {
				DataSource ds = jdbcTemplate.getDataSource();
				connection = ds.getConnection();
			}
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql.toString());
			List<Tuple> result = new ArrayList<>();
			while(rs.next()) {
				Tuple tuple = new Tuple();
				tuple.setId(rs.getLong(1));
				tuple.setPrev(rs.getString(2));
				tuple.setFol(rs.getString(3));
				tuple.setNum(rs.getInt(4));
				result.add(tuple);
			}
			return result;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Inserts or updates {@link Tuple}s in DB.<br>
	 * In case when {@link Tuple} with the same {@link Tuple#prev} and {@link Tuple#fol} found, then adds {@link Tuple#num} from collection item to {@link Tuple#num} database item.<br>
	 * In case when {@link Tuple} with the same {@link Tuple#prev} and {@link Tuple#fol} not found, then inserts the {@link Tuple} row to DB table.
	 * @param tuples collection of {@link Tuple}s to insert or update
	 */
	public void upsert(Collection<Tuple> tuples) {
		if(tuples.size() == 0) {
			return;
		}
		try {
			StringBuilder sql = new StringBuilder("INSERT INTO tuple (prev,fol,num) VALUES ");
			for (Iterator<Tuple> iter = tuples.iterator(); iter.hasNext();) {
				Tuple tuple = iter.next();
				sql.append("('");
				sql.append(StringEscapeUtils.escapeSql(tuple.getPrev()));
				sql.append("','");
				sql.append(StringEscapeUtils.escapeSql(tuple.getFol()));
				sql.append("',");
				sql.append(tuple.getNum());
				sql.append(")");
				if (iter.hasNext()) {
					sql.append(",");
				}
			}
			sql.append(" ON CONFLICT ON CONSTRAINT tuple_prev_fol_key DO UPDATE SET num = tuple.num + EXCLUDED.num;");
			if (connection == null) {
				connection = jdbcTemplate.getDataSource().getConnection();
			}
			Statement statement = connection.createStatement();
			statement.execute(sql.toString());
			statement.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
}
