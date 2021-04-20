package com.csc.sics.mg.sqlunit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

public abstract class AbstractTestCase {

	private final List<SqlConnectionSpec> connections = new ArrayList<SqlConnectionSpec>();
	public static Logger LOG = Logger.getLogger("SQL Unit Test");

	@Rule
	public TestName name = new TestName();

	/**
	 * Called before every test method. Use to create the DBMS connections needed for all tests for this class.<BR>
	 * Doing this here prevents a lot of boilerplate code for creating and closing connections in every test method.
	 * 
	 * @throws Exception
	 * 
	 * @see {@link #createConnection(String, String, String, String)}
	 */
	@Before
	public abstract void setUpConnections() throws Exception;

	/**
	 * @return a new SQL Assertion Object. Use this to define tests.
	 */
	protected AssertSql Assert() {
		return new AssertSql(SqlTestUtils.prettyMethodName(name.getMethodName()), LogWriterFactory.getWriter());

	}

	protected AssertSql Assert(final String s) {
		return new AssertSql(SqlTestUtils.prettyMethodName(s), LogWriterFactory.getWriter());

	}

	/**
	 * Create a connection to be used by the test methods.<BR>
	 * If visible, the SQL Server and Oracle driver classes are automatically loaded.<BR>
	 * Other drivers must be manually loaded before calling this.
	 * 
	 * @param aSystemName
	 *          A name to be used as a reference for retrieving the connection later.
	 * @param aUrl
	 *          the JDBC url to be used by the driver to connect to the DBMS
	 * @param userName
	 *          The user id to connect to the DB
	 * @param password
	 *          the password for the associated user id.
	 * @throws SQLException
	 * @see {@link #getConnection(String)}
	 * 
	 */
	protected void createConnection(final String aSystemName, final String aUrl, final String userName, final String password) throws SQLException {
		try {
			final Connection aC = DriverManager.getConnection(aUrl, userName, password);
			LOG.info("Connected to: " + aSystemName + " [" + aUrl + " : " + userName + "]");
			connections.add(new SqlConnectionSpec(aSystemName, aC));
		} catch (SQLException e) {
			LOG.error("Error creating connection to " + String.valueOf(aSystemName), e);
			throw e;
		}
	}

	/**
	 * Automatically close any opened connections.
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		for (final SqlConnectionSpec anEntry : connections) {
			try {
				anEntry.close();
				LOG.info("Disconnected from: " + anEntry.getSystemName());
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Return the SQL connection spec that was defined earlier.
	 * 
	 * @param aName
	 *          the name used when the connection was created
	 * @return the previously opened connection setup in {@link #setUpConnections()}
	 * @see #createConnection(String, String, String, String)
	 * 
	 */
	protected SqlConnectionSpec getConnection(final String aName) {
		for (final SqlConnectionSpec aSpec : connections) {
			if (aName.equals(aSpec.getSystemName())) {
				return aSpec;
			}
		}
		return null;
	}

}
