package com.csc.sics.mg.batch.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knowm.yank.Yank;
import org.knowm.yank.exceptions.YankSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csc.sics.mg.domain.BatchJobStep;
import com.csc.sics.mg.domain.ReferenceData;
import com.csc.sics.mg.utils.ResourceLoader;

public class BatchJobStepDAO {

	public static Logger LOG = LoggerFactory.getLogger(BatchJobStepDAO.class);
	public static final String FILENAME_SQL_RETRIEVE_BATCH_JOB_STEPS_QUERY = "./resources/retrieveBatchJobSteps.sql";
	public static final String FILENAME_SQL_RETRIEVE_BATCH_JOB_STEP_PARAMETERS_QUERY = "./resources/retrieveBatchJobStepParameters.sql";
	public static final String FILENAME_SQL_CHECK_BATCH_JOB_STEP_EXECUTION_STATUS_QUERY = "./resources/retrieveBatchJobStepExecutionStatus.sql";
	public static final String PARAMETER_KEY_FOR_ORDER_ID = "ORDER_ID";

	public static List<BatchJobStep> retrieveBatchJobStepsForJob(String objectId) {
		String sql = ResourceLoader.loadFileResourceAsString(FILENAME_SQL_RETRIEVE_BATCH_JOB_STEPS_QUERY);
		Object[] params = { objectId };
		List<BatchJobStep> batchJobSteps = null;
		try {
			// Execute the query and map the SQL ResultSet to a list of BatchJobStep beans
			batchJobSteps = Yank.queryBeanList(sql, BatchJobStep.class, params);
			if(batchJobSteps==null) {
				LOG.error("Check Scheduled jobs configured & executed in System Administration Utility");
			}
			
		} catch (YankSQLException e) {
			LOG.error("Failed to retrieve Batch Job Steps", e);
			throw (e);
		}
		for (BatchJobStep batchJobStep : batchJobSteps) {
			LOG.info("Retrieving Job Step Parameters for Batch Job Step " + batchJobStep.getSequence());
			batchJobStep.setJobStepParameters(
					BatchJobStepDAO.retrieveJobStepParametersForJobStep(batchJobStep.getObjectId()));
			if (batchJobStep.isAccountingOrderJobStep()) {
				String orderId = batchJobStep.getJobStepParameters().get(PARAMETER_KEY_FOR_ORDER_ID);
				LOG.info("Retrieving Accounting Order '" + orderId + "' for Accounting Order Batch Job Step #"
						+ batchJobStep.getSequence());
				batchJobStep.setAccountingOrder(AccountingOrderDAO.retrieveAccountingOrder(orderId));
			}
		}
		return batchJobSteps;
	}

	public static Map<String, String> retrieveJobStepParametersForJobStep(String objectId) {
		String sql = ResourceLoader.loadFileResourceAsString(FILENAME_SQL_RETRIEVE_BATCH_JOB_STEP_PARAMETERS_QUERY);
		Object[] params = { objectId };
		List<Object[]> resultSet = null;
		try {
			resultSet = Yank.queryObjectArrays(sql, params);
			if(resultSet==null) {
				LOG.error("Check Scheduled jobs configured & executed in System Administration Utility");
			}
			
		} catch (YankSQLException e) {
			LOG.error("Failed to retrieve Batch Job Step Parameters", e);
			throw (e);
		}
		Map<String, String> jobStepParameters = new HashMap<String, String>();
		for (Object[] parameterPair : resultSet) {
			String parameterKey = (String) parameterPair[0];
			String parameterValue = (String) parameterPair[1];
			jobStepParameters.put(parameterKey, parameterValue);
		}
		return jobStepParameters;
	}

	public static ReferenceData retrieveStatusForBatchJobStep(String batchJobIdentifier, int jobStepSequenceNumber) {
		String sql = ResourceLoader.loadFileResourceAsString(FILENAME_SQL_CHECK_BATCH_JOB_STEP_EXECUTION_STATUS_QUERY);
		LOG.info("Executing SQL to retrieve Batch Job Step execution status: " + sql);
		Object[] params = { batchJobIdentifier, jobStepSequenceNumber };
		try {
			// Execute the query and map the SQL ResultSet to an
			// ExecutionStatusReferenceData bean
			ReferenceData executionStatus = Yank.queryBean(sql, ExecutionStatusReferenceData.class, params);
			if(executionStatus==null) {
				LOG.error("Check Scheduled jobs configured & executed in System Administration Utility");
			}
			
			LOG.info("Execution status for Batch Job with identifier '" + batchJobIdentifier
					+ "' and job step sequence number " + jobStepSequenceNumber + "' is '" + executionStatus.getName()
					+ "'.");
			return executionStatus;
		} catch (YankSQLException e) {
			LOG.info("Failed to check execution status for Batch Job Step with Batch Job with identifier '"
					+ batchJobIdentifier + "' and job step sequence number " + jobStepSequenceNumber + "'.");
			throw e;
		}
	}

}
