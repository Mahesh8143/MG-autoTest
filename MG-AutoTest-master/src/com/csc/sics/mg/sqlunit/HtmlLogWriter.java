package com.csc.sics.mg.sqlunit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class HtmlLogWriter implements ILogWriter {

	private BufferedWriter log;
	private DateFormat df = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM);
	private NumberFormat intf = NumberFormat.getIntegerInstance();
	private int failCount = 0;
	private int successCount = 0;

	@Override
	public void logEqualsLiteral(final String aTestName, final Object aLiteral, final SqlArg aSql) {
		try {
			this.logTestStart(aTestName);
			this.writeStartTag("table", "compare");
			this.writeStartTag("tr", "compare");
			this.writeInTag("td", "compareRight", "Comparing value:");
			this.writeInTag("td", "compareSql", String.valueOf(aLiteral));
			this.writeEndTag("tr");
			this.writeStartTag("tr", "compare");
			this.writeInTag("td", "compareRight", "To result from:");
			this.writeInTag("td", "compare", aSql.getSystemName());
			this.writeEndTag("tr");
			this.writeStartTag("tr", "compare");
			this.writeInTag("td", "compare", "");
			this.writeInTag("td", "compareSql", aSql.getSql());
			this.writeEndTag("tr");
			this.writeEndTag("table");

			this.writeBreak();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void logTestStart(final String aTestName) throws IOException {
		this.writeStartTag("div", "start");
		this.writeInTag("p", aTestName + " started at " + df.format(new Date()));
		this.writeEndTag("div");
		log.newLine();
	}

	@Override
	public void fail(final String... errorMessages) {
		failCount++;
		try {
			this.writeStartTag("div", "fail");
			this.writeInTag("B", "Test Failed at " + df.format(new Date()));
			for (String string : errorMessages) {
				this.writeBreak();
				this.write("Error: ");
				this.write(string);
			}
			this.writeEndTag("div");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeStartTag(final String aTag, final String aClass) throws IOException {
		this.write("<");
		this.write(aTag);
		this.write(" class=\"");
		this.write(aClass);
		this.write("\">");
	}

	@Override
	public void success(final String aString) {
		successCount++;
		try {
			this.writeBreak();
			this.writeStartTag("div", "success");
			this.write(aString);
			this.write(" Test Completed Successfully at " + df.format(new Date()));
			this.writeEndTag("div");
			this.writeBreak();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void logRows(final boolean byName, final int rowCount, final SqlArg a, final ResultSet rs1, final SqlArg b, final ResultSet rs2) {

		try {
			this.writeStartTag("table", "rows");
			ResultSetMetaData rmd = rs1.getMetaData();
			int cc = rmd.getColumnCount();
			this.writeStartTag("tr", "rows");
			this.writeInTag("th", "rows", "Row: " + intf.format(rowCount));
			for (int i = 0; i < cc; i++) {
				String columnName;
				if (byName) {
					columnName = rmd.getColumnName(i + 1);
				} else {
					columnName = ("COLUMN" + (i + 1));
				}
				this.writeInTag("th", "rows", columnName);
			}
			this.writeEndTag("tr");

			this.writeStartTag("tr", "rows");
			this.writeInTag("td", "rows", a.getSystemName());
			for (int i = 0; i < cc; i++) {
				Object columnValue;
				if (byName) {
					columnValue = rs1.getObject(rmd.getColumnName(i + 1));
				} else {
					columnValue = rs1.getObject(i + 1);
				}
				this.writeInTag("td", "rows", String.valueOf(columnValue));
			}
			this.writeEndTag("tr");

			this.writeStartTag("tr", "rows");
			this.writeInTag("td", "rows", b.getSystemName());
			for (int i = 0; i < cc; i++) {
				Object columnValue;
				if (byName) {
					columnValue = rs2.getObject(rmd.getColumnName(i + 1));
				} else {
					columnValue = rs2.getObject(i + 1);
				}
				this.writeInTag("td", "rows", String.valueOf(columnValue));
			}
			this.writeEndTag("tr");

			this.writeEndTag("table");
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
			this.logTestStart(aTestName);

			this.writeStartTag("table", "compare");
			this.writeStartTag("tr", "compare");
			if (byName) {
				this.writeInTag("td", "compareRight", "By Column Name");
			} else {
				this.writeInTag("td", "compareRight", "By Column Index");
			}
			this.writeEndTag("tr");
			this.writeStartTag("tr", "compare");
			this.writeInTag("td", "compareRight", "Comparing:");
			this.writeInTag("td", "compare", a.getSystemName());
			this.writeEndTag("tr");
			this.writeStartTag("tr", "compare");
			this.writeInTag("td", "compare", "");
			this.writeInTag("td", "compareSql", a.getSql());
			this.writeEndTag("tr");
			this.writeStartTag("tr", "compare");
			this.writeInTag("td", "compareRight", "to:");
			this.writeInTag("td", "compare", b.getSystemName());
			this.writeEndTag("tr");
			this.writeStartTag("tr", "compare");
			this.writeInTag("td", "compare", "");
			this.writeInTag("td", "compareSql", b.getSql());
			this.writeEndTag("tr");
			if (ignoreColumnNames.length > 0 && byName) {
				this.writeStartTag("tr", "compare");
				this.writeInTag("td", "compareRight", "Ignoring:");
				this.writeInTag("td", "compareSql", Arrays.toString(ignoreColumnNames));
				this.writeEndTag("tr");
			}

			this.writeEndTag("table");

			this.writeBreak();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() {
		try {
			this.writeStartTag("div", "close");
			this.write("Test Run Finished at " + df.format(new Date()));
			this.writeBreak();
			NumberFormat f = NumberFormat.getIntegerInstance();
			this.write(f.format(failCount));
			this.write(" failures. ");
			this.write(f.format(successCount));
			this.write(" successes.");
			this.writeEndTag("div");
			this.writeEndTag("body");
			this.writeEndTag("html");
			log.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeInTag(final String aTag, final String aClass, final String aString) throws IOException {
		this.writeStartTag(aTag, aClass);
		this.write(aString);
		this.writeEndTag(aTag);
	}

	private void writeInTag(final String aTag, final String aString) throws IOException {
		this.writeStartTag(aTag);
		this.write(aString);
		this.writeEndTag(aTag);
	}

	private void writeBreak() throws IOException {
		this.writeStartTag("BR");
		log.newLine();
	}

	@Override
	public boolean isFor(final String logFileName) {
		if (logFileName == null || logFileName.isEmpty())
			return false;
		return logFileName.toUpperCase().endsWith(".HTM") || logFileName.toUpperCase().endsWith(".HTML");
	}

	@Override
	public boolean initializeFor(final String aTestName, final String logFileName) {
		File aFile = new File(logFileName);
		if (aFile.exists() && (!aFile.isFile() || !aFile.canWrite()))
			return false;
		try {
			log = new BufferedWriter(new FileWriter(aFile, false));
		} catch (IOException e) {
			return false;
		}
		try {
			this.write("<!DOCTYPE html>");
			this.writeStartTag("html");
			this.writeStartTag("head");
			this.writeStyleSheet();
			this.writeInTag("title", aTestName + " Test Run Started " + df.format(new Date()));
			this.writeEndTag("head");
			this.writeStartTag("body");
			this.writeInTag("H2", aTestName);
			this.writeInTag("H2", "Test Run Starting " + df.format(new Date()));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void writeStyleSheet() throws IOException {
		this.writeStartTag("style");
		this.write("body {font-family:Arial, Helvetica, sans-serif; }");
		log.newLine();
		log.newLine();
		this.write("table.rows {border-collapse:collapse}");
		log.newLine();
		this.write("table.rows, tr.rows, th.rows, td.rows {border:1px solid #A9A9A9; font-size:90%}");
		log.newLine();
		this.write("th.rows {background-color:#ADD8E6; color:black}");
		log.newLine();
		this.write("table.compare, td.compare, th.compare {border: 0px solid #DCDCDC}");
		this.write("td.compareRight {font-weight:bold; text-align:right;}");
		this.write("td.compareSql {font-size:90%; font-family: \"Courier New\", Courier, monospace}");
		log.newLine();
		this.write(".start {text-decoration:underline; font-size:110%; font-weight:bold; color: #4B0082}");
		log.newLine();
		this.write(".fail {color:red;}");
		log.newLine();
		this.write(".success {color:green;}");
		log.newLine();
		this.write(".close {font-size:110%; font-weight:bold; margin-top:10px;}");
		log.newLine();
		this.writeEndTag("style");
	}

	private void write(final String string) throws IOException {
		log.append(string);
	}

	private void writeStartTag(final String aTag) throws IOException {
		log.append('<');
		log.append(aTag);
		log.append('>');
	}

	private void writeEndTag(final String aTag) throws IOException {
		this.writeStartTag('/' + aTag);
	}

}
