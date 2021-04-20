package com.csc.sics.mg.autotests;

public class TestCase {

	private String name;
	private String[] ignoreColumns;
	private String query;
	private Object literal;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getLiteral() {
		return literal;
	}

	public void setLiteral(Object literal) {
		this.literal = literal;
	}

	public String[] getIgnoreColumns() {
		return ignoreColumns;
	}

	public void setIgnoreColumns(String[] ignoreColumns) {
		this.ignoreColumns = ignoreColumns;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
