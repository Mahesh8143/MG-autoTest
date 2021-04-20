package com.csc.sics.mg.sqlunit;

import java.util.Arrays;
import java.util.List;

public class KeyedSqlArg extends SqlArg {

	private final List<String> keyColumns;

	public KeyedSqlArg(final SqlConnectionSpec aConnSpec, final String aSqlStatement, final List<String> keyColumns) {
		super(aConnSpec, aSqlStatement);
		this.keyColumns = keyColumns;
	}

	public KeyedSqlArg(final SqlConnectionSpec aConnSpec, final String aSqlStatement, final String... keyColumns) {
		this(aConnSpec, aSqlStatement, Arrays.asList(keyColumns));
	}

	public List<String> keyColumns() {
		return keyColumns;
	}
}
