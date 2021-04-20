package com.csc.sics.mg.sqlunit;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.AssertionFailedError;

public class AssertSql extends AbstractTest {

	private final NumberFormat intFormat;
	private final String testName;
	private final ILogWriter logWriter;
	private String[] ignoreColumnNames = new String[0];

	/**
	 * Create a new Assertion Object
	 * 
	 * @param aTestName
	 *          The name of this test
	 * @param aLogWriter
	 *          A log writer to record the test results to.
	 */
	public AssertSql(final String aTestName, final ILogWriter aLogWriter) {
		super();
		testName = aTestName;
		this.logInfo("Running Test: " + aTestName);
		logWriter = aLogWriter;
		intFormat = NumberFormat.getIntegerInstance();
	}

	/**
	 * Compare the provided literal to the first column of the first row of the result set returned by the SqlArg. <BR>
	 * No returned rows is considered a failure.
	 * 
	 * @param aLiteral
	 *          A literal value that is the expected result in the first column of the first row of the SQL result set.
	 * @param aSql
	 *          the SQL to retrieve the expected value.
	 */
	public void equalsLiteral(final Object aLiteral, final SqlArg aSql) {
		logWriter.logEqualsLiteral(testName, aLiteral, aSql);
		this.logInfo("Comparing: " + String.valueOf(aLiteral));
		this.logInfo("       To: " + aSql.toString());
		ResultSet rs1 = null;
		try {
			rs1 = aSql.getResultSet();

			if (!rs1.next()) {
				this.fail("No Results from SQL");
			}

			final Object c1 = rs1.getObject(1);

			final String aString = this.checkEqual("Literal", aLiteral, c1);
			if (aString != null) {
				this.fail(aString);
			}
			rs1.close();
		}

		catch (final Exception e) {
			try {
				if (rs1 != null) {
					rs1.close();
				}
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
			this.fail(e.getLocalizedMessage());
		}
		this.success("");
	}

	
	/**
	 * Compare the result sets returned by the 2 SQL args for equality. <BR>
	 * Every column in every row in result set A is compared to the corresponding row in result set B for equality. <BR>
	 * This is achieved by getting every column from row A and checking the value returned from the same column name in
	 * Row B. <BR>
	 * If row A and row B have different named columns or a different number of columns, the test will fail.
	 * 
	 * @param a
	 *          the SQL to retrieve a result set
	 * @param b
	 *          the SQL to retrieve a result set
	 */
	public void equalsByColumnName(final SqlArg a, final SqlArg b) {
		this.equals(true, a, b);
	}

	/**
	 * Compare the result sets returned by the 2 SQL args for equality. <BR>
	 * Every column in every row in result set A is compared to the corresponding row in result set B for equality. <BR>
	 * This is achieved by getting every column from row A and checking the value returned from the same column index in
	 * Row B. <BR>
	 * If row A and row B have a different column sequence or a different number of columns, the test will fail.
	 * 
	 * @param a
	 *          the SQL to retrieve a result set
	 * @param b
	 *          the SQL to retrieve a result set
	 */
	public void equalsByColumnIndex(final SqlArg a, final SqlArg b) {
		this.equals(false, a, b);
	}

	private void equals(final boolean byName, final SqlArg a, final SqlArg b) {

		ResultSet rs1 = null;
		ResultSet rs2 = null;
		final List<String> errors = new ArrayList<String>();

		logWriter.logEquals(testName, byName, a, b, ignoreColumnNames);
		this.logInfo("Comparing: " + a.toString());
		this.logInfo("       To: " + b.toString());
		if (ignoreColumnNames.length > 0) {
			this.logInfo("Ignoring Columns: " + Arrays.toString(ignoreColumnNames));
		}
		int rowCount = 0;

		try {
			rs1 = a.getResultSet();
			rs2 = b.getResultSet();

			int cc1 = -1;
			int cc2 = -1;
			final List<String> cnames = new ArrayList<String>();
			while (rs1.next()) {
				rowCount++;
				if (!rs2.next()) {
					this.fail("Unequal row counts after reading " + intFormat.format(rowCount) + " records");
				}
				if (cc1 < 0) {
					final ResultSetMetaData rmd1 = rs1.getMetaData();
					final ResultSetMetaData rmd2 = rs2.getMetaData();
					cc1 = this.getColumnCount(rmd1);
					cc2 = this.getColumnCount(rmd2);
					if (cc1 != cc2) {
						this.fail("Unequal Column Counts in result sets. " + cc1 + " vs " + cc2);
					}
					for (int j = 0; j < cc1; j++) {
						cnames.add(rmd1.getColumnName(j + 1));
					}
				}
				for (int j = 0; j < cc1; j++) {
					Object c1 = null;
					Object c2 = null;
					String colDesc = "Row " + intFormat.format(rowCount) + ": ";
					if (byName) {
						final String cname = cnames.get(j);
						if (this.shouldProcessColumn(cname)) {
							colDesc = colDesc + cname;
							c1 = rs1.getObject(cname);
							try {
								c2 = rs2.getObject(cname);
							} catch (final SQLException e) {
								if (cname == null || cname.isEmpty()) {
									this.fail("Missing Column Name for column " + (j + 1) + ". Alias columns or use column index checking.");
								}
								this.fail(e.getLocalizedMessage());
							}
						}
					} else {
						colDesc = colDesc + "Column" + (j + 1);
						c1 = rs1.getObject(j + 1);
						c2 = rs2.getObject(j + 1);
					}
					final String aString = this.checkEqual(colDesc, c1, c2);
					if (aString != null) {
						errors.add(aString);
					}
				}

				if (errors.size() > 0) {
					this.logRows(byName, rowCount, a, rs1, b, rs2);
					this.fail(errors);
				}
				if (rowCount % 10000 == 0) {
					this.logInfo("Checked " + intFormat.format(rowCount) + " records");
				}
			}
			this.logInfo("Checked " + intFormat.format(rowCount) + " records");
			if (rs2.next()) {
				this.fail("Unequal row counts after reading " + intFormat.format(rowCount) + " records.");
			}
			rs1.getStatement().close();
			rs2.getStatement().close();

		} catch (final Exception e) {
			try {
				if (rs1 != null) {
					rs1.getStatement().close();
				}
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
			try {
				if (rs2 != null) {
					rs2.getStatement().close();
				}
			} catch (final SQLException e1) {
				e1.printStackTrace();
			}
			this.fail(e.getLocalizedMessage());
		}
		this.success("Checked " + intFormat.format(rowCount) + " rows.");
	}

	private int getColumnCount(final ResultSetMetaData rmd) throws SQLException {
		return this.getValidationColumns(rmd).size();
	}

	private List<String> getValidationColumns(final ResultSetMetaData rmd) throws SQLException {
		int cc = rmd.getColumnCount();
		List<String> columns = new ArrayList<String>(cc);
		for (int i = 1; i <= cc; i++) {
			String columnName = rmd.getColumnName(i);
			if (this.shouldProcessColumn(columnName)) {
				columns.add(columnName);
			}
		}
		return columns;
	}

	private boolean shouldProcessColumn(final String cname) {
		if (ignoreColumnNames.length > 0) {
			for (String string : ignoreColumnNames) {
				if (cname.equalsIgnoreCase(string)) {
					return false;
				}
			}
		}
		return true;
	}

	private void success(final String aString) {
		logWriter.success(aString);
		this.logInfo("Test " + testName + " completed successfully");
	}

	private void logRows(final boolean byName, final int rowCount, final SqlArg a, final ResultSet rs1, final SqlArg b, final ResultSet rs2) {
		try {
			final ResultSetMetaData rmd = rs1.getMetaData();
			final int cc = rmd.getColumnCount();
			final StringBuilder sb1 = new StringBuilder();
			sb1.append("Row1: ");
			final StringBuilder sb2 = new StringBuilder();
			sb2.append("Row2: ");
			for (int i = 0; i < cc; i++) {
				if (i > 0) {
					sb1.append(", ");
					sb2.append(", ");
				}
				if (byName) {
					final String columnName = rmd.getColumnName(i + 1);
					sb1.append(columnName);
					sb1.append("=");
					sb1.append(rs1.getObject(columnName));
					sb2.append(columnName);
					sb2.append("=");
					sb2.append(rs2.getObject(columnName));
				} else {
					sb1.append("COLUMN" + (i + 1));
					sb1.append("=");
					sb1.append(rs1.getObject(i + 1));
					sb2.append("COLUMN" + (i + 1));
					sb2.append("=");
					sb2.append(rs2.getObject(i + 1));
				}
			}
			this.logInfo(sb1.toString());
			this.logInfo(sb2.toString());
			logWriter.logRows(byName, rowCount, a, rs1, b, rs2);
		} catch (final SQLException e) {
			// e.printStackTrace();
		}
	}

	private void logInfo(final String string) {
		final String[] s = string.split("\n");
		for (final String aLogLine : s) {
			if (aLogLine.trim().length() > 0) {
				AbstractTestCase.LOG.info(string);
			}
		}
	}

	private void fail(final List<String> errors) {
		this.fail(errors.toArray(new String[errors.size()]));
	}

	private void fail(final String... errors) {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final String string : errors) {
			if (string.trim().length() > 0) {
				if (!first) {
					sb.append('\n');
				}
				sb.append(string);
				first = false;
			}
		}
		AbstractTestCase.LOG.error(sb.toString());
		logWriter.fail(errors);
		throw new AssertionFailedError(sb.toString());
	}

	@Override
	String failCheckEquals(final String aName, final Object c1, final Object c2) {
		return "Unequal Column Values in [" + aName + "] Expected " + String.valueOf(c1) + " but got " + String.valueOf(c2);
	}

	@Override
	public AssertSql setNumericTolerance(final double aTolerance) {
		return (AssertSql) super.setNumericTolerance(aTolerance);
	}

	public AssertSql addIgnoreColumnNames(final String... strings) {
		ignoreColumnNames = strings;
		return this;
	}

}
