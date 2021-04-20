package com.csc.sics.mg.sqlunit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.NumberFormat;
import java.time.LocalDateTime;

public class XmlLogWriter implements ILogWriter {

	private BufferedWriter log;
	private NumberFormat intf = NumberFormat.getIntegerInstance();
	private int failCount = 0;
	private int successCount = 0;

	@Override
	public void logEqualsLiteral(final String aTestName, final Object aLiteral, final SqlArg aSql) {
		try {
			this.writeStartTag("testCase");
			this.writeInTag("name", aTestName);
			this.writeInTag("type", "literal");
			this.writeBreak();
			this.writeInTag("rhsValue", String.valueOf(aLiteral));
			this.writeBreak();
			this.writeInTag("lhsName", aSql.getSystemName());
			this.writeInTag("lhsSQL", aSql.getSql());
			this.writeBreak();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void fail(final String... errorMessages) {
		failCount++;
		try {
			this.writeStartTag("failure");
			this.writeInTag("timestamp", this.timestampNow());
			for (String string : errorMessages) {
				this.writeBreak();
				this.writeInTag("errorMessage", string);
			}
			this.writeEndTag("failure");
			this.writeEndTag("testCase");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void logRows(final boolean byName, final int rowCount, final SqlArg a, final ResultSet rs1, final SqlArg b, final ResultSet rs2) {
		try {
			this.writeStartTag("exceptionRows");
			this.writeInTag("rowNumber", intf.format(rowCount));
			ResultSetMetaData rmd = rs1.getMetaData();
			int cc = rmd.getColumnCount();
			this.writeStartTag("columnNames");
			for (int i = 0; i < cc; i++) {
				String columnName;
				Object columnValue1;
				Object columnValue2;
				if (byName) {
					columnName = rmd.getColumnName(i + 1);
					columnValue1 = rs1.getObject(columnName);
					columnValue2 = rs2.getObject(columnName);
				} else {
					columnName = ("COLUMN" + (i + 1));
					columnValue1 = rs1.getObject(i + 1);
					columnValue2 = rs2.getObject(i + 1);
				}

				this.writeBreak();
				this.writeStartTag("column");
				this.writeInTag("columnName", columnName);
				this.writeInTag("rhsValue", String.valueOf(columnValue1));
				this.writeInTag("lhsValue", String.valueOf(columnValue2));
				this.writeEndTag("column");
			}
			this.writeEndTag("columnNames");
			this.writeEndTag("exceptionRows");
			this.writeBreak();
		} catch (Exception e) {
			try {
				log.append(e.getLocalizedMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	@Override
	public void logEquals(final String aTestName, final boolean byName, final SqlArg a, final SqlArg b, final String[] ignoreColumnNames) {
		try {
			this.writeStartTag("testCase");
			this.writeInTag("name", aTestName);
			String type = "byColumnName";
			if (!byName) {
				type = "byColumnIndex";
			}
			this.writeInTag("type", type);
			this.writeBreak();
			this.writeInTag("rhsName", a.getSystemName());
			this.writeInTag("rhsSQL", a.getSql());
			this.writeBreak();
			this.writeInTag("lhsName", b.getSystemName());
			this.writeInTag("lhsSQL", b.getSql());
			this.writeBreak();
			if (ignoreColumnNames.length > 0 && byName) {
				this.writeStartTag("ignoreColumns");
				this.writeBreak();
				for (String string : ignoreColumnNames) {
					this.writeInTag("name", string);
					this.writeBreak();
				}
				this.writeEndTag("ignoreColumns");
				this.writeBreak();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() {
		try {
			this.writeInTag("endTimestamp", this.timestampNow());
			this.writeBreak();
			this.writeInTag("failureCount", intf.format(failCount));
			this.writeInTag("successCount", intf.format(successCount));
			this.writeBreak();
			//this.writeEndTag("xml");
			this.writeEndTag("testCases");
			log.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isFor(final String logFileName) {
		if (logFileName == null || logFileName.isEmpty())
			return false;
		return logFileName.toUpperCase().endsWith(".XML");
	}

	@Override
	public boolean initializeFor(final String aTestName, final String logFileName) {
		File aFile = new File(logFileName);
		if (aFile.exists() && (!aFile.isFile() || !aFile.canWrite()))
			return false;
		try {
			log = SqlTestUtils.newBufferedWriter(aFile);
		} catch (IOException e) {
			return false;
		}
		try {
			this.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			this.writeStartTag("testCases");
			this.writeInTag("name", aTestName);
			this.writeInTag("startTimestamp", this.timestampNow());
			this.writeBreak();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private String timestampNow() {
		return LocalDateTime.now().toString();
	}

	@Override
	public void success(final String aString) {
		successCount++;
		try {
			this.writeStartTag("success");
			this.writeInTag("timestamp", this.timestampNow());
			this.writeInTag("message", aString);
			this.writeEndTag("success");
			this.writeEndTag("testCase");
			this.writeBreak();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeStartTag(final String aTag) throws IOException {
		log.append('<');
		log.append(aTag);
		log.append('>');
	}

	private void writeEndTag(final String aTag) throws IOException {
		this.writeStartTag('/' + aTag);
	}

	private void writeInTag(final String aTag, final String aString) throws IOException {
		this.writeStartTag(aTag);
		this.write(aString);
		this.writeEndTag(aTag);
	}

	private void write(final String string) throws IOException {
		log.append(string);
	}

	private void writeBreak() throws IOException {
		log.newLine();
	}

}
