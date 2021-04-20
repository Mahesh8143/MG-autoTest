package com.csc.sics.mg.sqlunit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.AssertionFailedError;

public class SqlConnectionSpec {

	private final String systemName;
	private final Connection dbConnection;

	public SqlConnectionSpec(final String systemName, final Connection dbConnection) {
		super();

		this.systemName = systemName;
		this.dbConnection = dbConnection;
	}

	public String getSystemName() {
		return systemName;
	}

	public void close() throws SQLException {
		dbConnection.close();
	}

	public ResultSet executeQuery(final String sql) throws SQLException {
		final Statement st = dbConnection.createStatement();
		return st.executeQuery(sql);
	}

	public ResultSet resultSet(final String sql) {
		Statement st;
		try {
			st = dbConnection.createStatement();
			return st.executeQuery(sql);
		} catch (SQLException e) {
			throw new AssertionFailedError(e.getLocalizedMessage());
		}
	}

}
