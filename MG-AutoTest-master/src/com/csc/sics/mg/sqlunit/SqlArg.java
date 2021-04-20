package com.csc.sics.mg.sqlunit;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlArg {

	/**
	 * Create a new SQL Argument for use in a SQL Assertion test.
	 * 
	 * @param aConnSpec
	 *          A spec for a JDBC connection
	 * @param aSqlStatement
	 *          The SQL Statement used to return the result set.
	 */
	public SqlArg(final SqlConnectionSpec aConnSpec, final String aSqlStatement) {
		connection = aConnSpec;
		sql = aSqlStatement;
	}

	private final SqlConnectionSpec connection;
	private final String sql;

	public ResultSet getResultSet() throws SQLException {
		return connection.executeQuery(sql);
	}

	public String getSystemName() {
		return connection.getSystemName();
	}

	public String getSql() {
		return sql;
	}

	@Override
	public String toString() {
		return connection.getSystemName() + " [" + sql + "]";
	}

}
