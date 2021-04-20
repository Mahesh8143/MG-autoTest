package com.csc.sics.mg.autotests;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import com.csc.sics.mg.sqlunit.AbstractTestCase;
import com.csc.sics.mg.sqlunit.LogWriterFactory;
import com.csc.sics.mg.sqlunit.SqlArg;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SicsDataComparisonTest extends AbstractTestCase {

	private static final String TARGET = "TARGET";
	private static final String SOURCE = "SOURCE";
	public static Logger LOG = Logger.getLogger(SicsDataComparisonTest.class);

	@BeforeEach
	public void setUpConnections() throws Exception {

		Properties properties = new Properties();
		properties.load(new FileInputStream("./resources/dbConnection.properties"));
		String sourceUrl = properties.getProperty("sourceUrl");
		String sourceUsername = properties.getProperty("sourceUsername");
		String sourcePassword = properties.getProperty("sourcePassword");
		String targetUrl = properties.getProperty("targetUrl");
		String targetUsername = properties.getProperty("targetUsername");
		String targetPassword = properties.getProperty("targetPassword");
		this.createConnection(SOURCE, sourceUrl, sourceUsername, sourcePassword);
		this.createConnection(TARGET, targetUrl, targetUsername, targetPassword);

	}

	@BeforeAll
	public static void setup() {
		LogWriterFactory.initializeFor("SicsDataComparision.xml");
	}

	@AfterAll
	public static void shutdown() {
		LogWriterFactory.getWriter().close();
	}

	@TestFactory
	@DisplayName("Sics Data Test")

	Collection<DynamicTest> dynamicTestCollection() {
		Collection<DynamicTest> dynamicTests = new ArrayList<DynamicTest>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			LOG.info("Trying to read json testcases file in dynamicTestCollection method");

			HashMap<String, List<TestCase>> testCasesMap = mapper.readValue(
					new FileInputStream("./resources/testCases.json"),
					new TypeReference<HashMap<String, List<TestCase>>>() {
					});
			for (List<TestCase> testCase : testCasesMap.values()) {
				for (TestCase aTestCase : testCase) {

					dynamicTests.add(DynamicTest.dynamicTest(aTestCase.getName(), () -> {

						if (aTestCase.getLiteral() != null) {
							this.Assert(aTestCase.getName()).equalsLiteral(aTestCase.getLiteral(),
									new SqlArg(this.getConnection(TARGET), aTestCase.getQuery()));
						} else {
							this.Assert(aTestCase.getName()).setNumericTolerance(1.01)
									.addIgnoreColumnNames(aTestCase.getIgnoreColumns())
									.equalsByColumnName(new SqlArg(this.getConnection(SOURCE), aTestCase.getQuery()),
											new SqlArg(this.getConnection(TARGET), aTestCase.getQuery()));
						}

					}

					));
				}
			}

		} catch (Exception e) {
			LOG.error(e);
		}
		return dynamicTests;
	}
}
