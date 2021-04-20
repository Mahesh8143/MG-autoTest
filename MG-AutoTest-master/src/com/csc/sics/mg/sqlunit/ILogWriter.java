package com.csc.sics.mg.sqlunit;

import java.sql.ResultSet;

public interface ILogWriter {

	void logEqualsLiteral(String aTestName, Object aLiteral, SqlArg aSql);

	void fail(String... errorMessages);

	void logRows(boolean byName, int rowCount, SqlArg a, ResultSet rs1, SqlArg b, ResultSet rs2);

	void logEquals(String aTestName, boolean byName, SqlArg a, SqlArg b, String[] ignoreColumnNames);

	void close();

	boolean isFor(String logFileName);

	boolean initializeFor(final String aTestName, final String logFileName);

	void success(String aString);

}
