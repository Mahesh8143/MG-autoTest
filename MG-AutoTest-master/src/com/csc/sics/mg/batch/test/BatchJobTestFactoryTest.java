package com.csc.sics.mg.batch.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.knowm.yank.Yank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csc.sics.mg.batch.dao.BatchJobDAO;
import com.csc.sics.mg.batch.dao.BatchJobStepDAO;
import com.csc.sics.mg.domain.BatchJob;
import com.csc.sics.mg.domain.BatchJobStep;
import com.csc.sics.mg.domain.ReferenceData;
import com.csc.sics.mg.sqlunit.AbstractTestCase;

public class BatchJobTestFactoryTest extends AbstractTestCase {

	public static Logger LOG = LoggerFactory.getLogger(BatchJobTestFactoryTest.class);
	private static List<BatchJob> batchJobsToTest;
	public static final String EXECUTION_STATUS_CODE_FINISHED = "FIN";
	static Properties properties = null;


	private interface BatchJobTestExecutable extends Executable {
		public String getTestName();
	}

	private interface BatchJobStepTestExecutable extends Executable {
		public String getTestName();
	}

	
	@BeforeAll
	private static void before() throws FileNotFoundException, IOException {
		try {

			Properties dbProps = new Properties();
			properties = new Properties();
			properties.load(new FileInputStream("./resources/dbConnection.properties"));
			dbProps.setProperty("jdbcUrl", properties.getProperty("targetUrl"));
			dbProps.setProperty("username", properties.getProperty("targetUsername"));
			dbProps.setProperty("password", properties.getProperty("targetPassword"));
			dbProps.setProperty("driverClassName", properties.getProperty("driverClassName"));
			Yank.setupDefaultConnectionPool(dbProps);
			batchJobsToTest = findBatchJobsToTest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterAll
	private static void after() {
		Yank.releaseDefaultConnectionPool();
	}

	private static List<BatchJob> findBatchJobsToTest() {
		return BatchJobDAO.retrieveBatchJobs(properties.getProperty("schdIdentifier"), "OC");
	}

	@TestFactory
	Collection<DynamicTest> generateDynamicTests() {

		if (batchJobsToTest.size() == 0) {
			LOG.info("Found no '" + "' Batch Job Templates registered with Desired Scheduler '"
					+ properties.getProperty("schdIdentifier")
					+ "'. Please check Batch Job setup in SICS SysAdmin Utility.");
			return Collections.emptyList();
		}
		LOG.info(
				"Found " + batchJobsToTest.size() + " '" + "' Batch Job Template(s) registered with Desired Scheduler '"
						+ properties.getProperty("schdIdentifier") + "'.");
		LOG.info("Generating JUnit tests...");
		List<DynamicTest> testcases = new ArrayList<DynamicTest>();
		int i = 1;
		for (final BatchJob aJob : batchJobsToTest) {
			BatchJobTestExecutable batchJobTestExecutable = new BatchJobTestExecutable() {
				private Logger LOG = LoggerFactory.getLogger(BatchJobTestFactoryTest.BatchJobTestExecutable.class);
				private BatchJob batchJob = aJob;

				@Override
				public void execute() {

					ReferenceData batchJobExecutionStatus = BatchJobDAO.retrieveStatusForBatchJob(
							properties.getProperty("schdIdentifier"), batchJob.getIdentifier());
					batchJob.setExecutionStatus(batchJobExecutionStatus);
					LOG.info("Execution status of Batch Job '" + batchJob.getIdentifier() + "' is '"
							+ batchJob.getExecutionStatus() + "'.");

					// Checking if the Batch Job status is "Finished"
					Assertions.assertEquals(EXECUTION_STATUS_CODE_FINISHED, batchJob.getExecutionStatus().getCode());

				}

				public String getTestName() {
					return "Batch Job Test: " + batchJob.getIdentifier();
				}

			};
			DynamicTest testcase = DynamicTest.dynamicTest(batchJobTestExecutable.getTestName(),
					batchJobTestExecutable);
			testcases.add(testcase);
			LOG.info("Created test " + i + ": '" + testcase.getDisplayName() + "' for Batch Job '"
					+ aJob.getIdentifier() + "' with Priority: " + aJob.getPriority() + ".");
			LOG.info("Job Step sequence:");
			for (BatchJobStep aJobStep : aJob.getJobSteps()) {
				if (aJobStep.isAccountingOrderJobStep()) {
					LOG.info(aJobStep.getSequence() + ": Job Step type '" + aJobStep.getType() + "' "
							+ aJobStep.getAccountingOrder().getOrderId() + " '"
							+ aJobStep.getAccountingOrder().getTitle() + "'");
				} else {
					LOG.info(aJobStep.getSequence() + ": Job Step type '" + aJobStep.getType() + "'");
				}
				BatchJobStepTestExecutable batchJobStepTestExecutable = new BatchJobStepTestExecutable() {
					private Logger LOG = LoggerFactory
							.getLogger(BatchJobTestFactoryTest.BatchJobStepTestExecutable.class);
					private BatchJobStep batchJobStep = aJobStep;

					@Override
					public void execute() {
						LOG.info("Executing test '" + this.getTestName() + "'...");
						// Check execution status for each Job Step within the Batch Job
						LOG.info("Create test for checking Batch Job Step execution status...");
						aJob.getIdentifier();
						ReferenceData batchJobStepExecutionStatus = BatchJobStepDAO
								.retrieveStatusForBatchJobStep(aJob.getIdentifier(), batchJobStep.getSequence());
						aJobStep.setExecutionStatus(batchJobStepExecutionStatus);
						LOG.info("Batch Job Step execution status: '" + batchJobStep.getExecutionStatus().getCode()
								+ "'.");
						// Checking if the Batch Job status is "Finished"
						Assertions.assertEquals(EXECUTION_STATUS_CODE_FINISHED,
								batchJobStep.getExecutionStatus().getCode());
					}

					@Override
					public String getTestName() {
						String prefix = "Step " + batchJobStep.getSequence() + ": ";
						if (batchJobStep.isAccountingOrderJobStep()) {
							return prefix + "Accounting Order Job Step Test: "
									+ batchJobStep.getAccountingOrder().getTitle();
						}
						if (batchJobStep.isSQLJobStep()) {
							return prefix + "SQL Job Step Test";
						}
						return prefix + "Unknown Job Step Test (Job Step type: " + batchJobStep.getType() + ")";
					}
				};
				DynamicTest jobStepTestcase = DynamicTest.dynamicTest(batchJobStepTestExecutable.getTestName(),
						batchJobStepTestExecutable);
				testcases.add(jobStepTestcase);
				LOG.info("Created test " + i + ": '" + jobStepTestcase.getDisplayName() + "' for Batch Job Step.");
			}
			i++;
		}
		return testcases;
	}

	@Override
	public void setUpConnections() throws Exception {
		// TODO Auto-generated method stub

	}

}
