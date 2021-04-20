package com.csc.sics.mg.sqlunit;

import com.csc.sics.mg.sqlunit.HtmlLogWriter;
import com.csc.sics.mg.sqlunit.ILogWriter;
import com.csc.sics.mg.sqlunit.LogWriterFactory;
import com.csc.sics.mg.sqlunit.NullLogWriter;
import com.csc.sics.mg.sqlunit.SqlTestUtils;
import com.csc.sics.mg.sqlunit.TextLogWriter;
import com.csc.sics.mg.sqlunit.XmlLogWriter;

public class LogWriterFactory {

	private static ILogWriter writer;

	public static ILogWriter getWriter() {
		if (writer == null)
			writer = new NullLogWriter();
		return writer;
	}

	public static void initializeFor(final String logFileName) {
		LogWriterFactory.initializeFor(SqlTestUtils.prettyMethodName(logFileName.subSequence(0, logFileName.lastIndexOf('.')).toString()), logFileName);
	}

	public static void initializeFor(final String aTestName, final String logFileName) {
		if (writer != null) {
			writer.close();
		}
		for (ILogWriter aWriter : getWriters()) {
			if (aWriter.isFor(logFileName)) {
				if (aWriter.initializeFor(aTestName, logFileName)) {
					writer = aWriter;
					break;
				}
			}
		}
	}

	private static ILogWriter[] getWriters() {
		ILogWriter[] writers = new ILogWriter[4];
		writers[0] = new TextLogWriter();
		writers[1] = new HtmlLogWriter();
		writers[2] = new XmlLogWriter();
		writers[3] = new NullLogWriter();
		return writers;
	}

}
