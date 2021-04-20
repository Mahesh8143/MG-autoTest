package com.csc.sics.mg.sqlunit;

import java.sql.ResultSet;

public class NullLogWriter implements ILogWriter {

	@Override
	public boolean isFor(final String logFileName) {
		// I'm valid for any file name.
		return true;
	}

	@Override
	public boolean initializeFor(final String aTestName, final String logFileName) {
		return true;
	}

	@Override
	public void logEqualsLiteral(final String aTestName, final Object aLiteral2, final SqlArg aSql) {
	}

	@Override
	public void fail(final String... errorMessages) {
	}

	@Override
	public void logRows(final boolean byName, final int rowCount, final SqlArg a, final ResultSet rs1, final SqlArg b, final ResultSet rs2) {
	}

	@Override
	public void logEquals(final String aTestName, final boolean byName, final SqlArg a, final SqlArg b, final String[] ignoreColumnNames) {
	}

	@Override
	public void close() {
	}

	@Override
	public void success(final String aString) {
	}

}
