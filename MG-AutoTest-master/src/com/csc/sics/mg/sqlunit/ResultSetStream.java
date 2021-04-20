package com.csc.sics.mg.sqlunit;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultSetStream {

	private final ResultSet rs;
	private final List<Map<String, Object>> peekedRows = new ArrayList<Map<String, Object>>();
	private final List<String> columnNames = new ArrayList<String>();

	public ResultSetStream(final ResultSet resultSet) throws SQLException {
		rs = resultSet;
		int i = 1;
		while (i <= rs.getMetaData().getColumnCount()) {
			columnNames.add(rs.getMetaData().getColumnName(i));
			i++;
		}
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public boolean next() throws SQLException {
		if (this.hasPeekedRows()) {
			this.removeFirstPeekedRow();
			return true;
		}
		return rs.next();
	}

	private void removeFirstPeekedRow() {
		peekedRows.remove(0);
	}

	private boolean hasPeekedRows() {
		return peekedRows.size() > 0;
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return rs.getMetaData();
	}

	public Object getObject(final String cname) throws SQLException {
		if (this.hasPeekedRows()) {
			return this.currentPeekedRow().get(cname);
		} else {
			return rs.getObject(cname);
		}
	}

	private Map<String, Object> currentPeekedRow() {
		return peekedRows.get(0);
	}

	public void close() throws SQLException {
		peekedRows.clear();
		rs.getStatement().close();
	}

	public boolean peek() throws SQLException {
		if (rs.next()) {
			final Map<String, Object> aRow = this.getNextRow();
			peekedRows.add(aRow);
			return true;
		} else {
			return false;
		}
	}

	private Map<String, Object> getNextRow() throws SQLException {
		final Map<String, Object> aRow = new LinkedHashMap<String, Object>();
		for (final String aName : columnNames) {
			aRow.put(aName, rs.getObject(aName));
		}
		return aRow;
	}

	public Map<String, Object> getCurrentRow() throws SQLException {
		if (this.hasPeekedRows()) {
			return peekedRows.get(0);
		} else {
			return this.getNextRow();
		}
	}

}
