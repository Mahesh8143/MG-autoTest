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

public class TextLogWriter implements ILogWriter {

	private BufferedWriter log;
	private DateFormat df = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM);
	private int failCount = 0;
	private int successCount = 0;

	@Override
	public void logEqualsLiteral(final String aTestName, final Object aLiteral, final SqlArg aSql) {
		try {
			this.logTestStart(aTestName);
			log.append("Comparing: ");
			log.append(String.valueOf(aLiteral));
			log.newLine();
			log.append("       to: ");
			log.append(aSql.toString());
			log.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void logTestStart(final String aTestName) throws IOException {
		this.hdiv();
		log.append(aTestName);
		log.append(" started at ");
		log.append(df.format(new Date()));
		log.newLine();
	}

	@Override
	public void fail(final String... errorMessages) {
		failCount++;
		try {
			log.append("Test Failed at ");
			log.append(df.format(new Date()));
			log.newLine();
			for (String aString : errorMessages) {
				log.append("Error: ");
				log.append(aString);
				log.newLine();
			}
			log.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void success(final String aString) {
		successCount++;
		try {
			log.append("Test Completed Successfully at ");
			log.append(df.format(new Date()));
			log.newLine();
			log.append(aString);
			log.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void logRows(final boolean byName, final int rowCount, final SqlArg a, final ResultSet rs1, final SqlArg b, final ResultSet rs2) {

		try {
			ResultSetMetaData rmd = rs1.getMetaData();
			int cc = rmd.getColumnCount();
			StringBuilder sb1 = new StringBuilder();
			sb1.append(a.getSystemName());
			StringBuilder sb2 = new StringBuilder();
			sb2.append(b.getSystemName());
			for (int i = 0; i < cc; i++) {
				if (i > 0) {
					sb1.append(", ");
					sb2.append(", ");
				}
				if (byName) {
					String columnName = rmd.getColumnName(i + 1);
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
			log.append(sb1.toString());
			log.newLine();
			log.append(sb2.toString());
			log.newLine();
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
			log.append("Comparing: ");
			log.append(a.toString());
			log.newLine();
			log.append("       to: ");
			log.append(b.toString());
			log.newLine();
			if (ignoreColumnNames.length > 0 && byName) {
				log.append(" ignoring: ");
				log.append(Arrays.toString(ignoreColumnNames));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() {
		try {
			this.hdiv();
			log.append("Test Run Finished at ");
			log.append(df.format(new Date()));
			log.newLine();
			NumberFormat f = NumberFormat.getIntegerInstance();
			log.append(f.format(failCount));
			log.append(" failures. ");
			log.append(f.format(successCount));
			log.append(" successes.");
			log.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isFor(final String logFileName) {
		if (logFileName == null || logFileName.isEmpty())
			return false;
		return logFileName.toUpperCase().endsWith(".TXT");
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
			log.append(aTestName);
			log.newLine();
			log.append("Test Run Starting ");
			log.append(df.format(new Date()));
			log.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	private void hdiv() {
		try {
			log.append("--------------------------------------------------------------");
			log.newLine();
			log.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
