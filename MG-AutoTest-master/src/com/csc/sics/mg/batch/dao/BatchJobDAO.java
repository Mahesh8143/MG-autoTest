package com.csc.sics.mg.batch.dao;

import java.util.ArrayList;
import java.util.List;

import org.knowm.yank.Yank;
import org.knowm.yank.exceptions.YankSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csc.sics.mg.domain.BatchJob;
import com.csc.sics.mg.domain.ReferenceData;
import com.csc.sics.mg.utils.ResourceLoader;

public class BatchJobDAO {

	public static Logger LOG = LoggerFactory.getLogger(BatchJobDAO.class);
	public static final String FILENAME_SQL_RETRIEVE_BATCH_JOBS_QUERY = "./resources/retrieveBatchJobs.sql";
	public static final String FILENAME_SQL_CHECK_BATCH_JOB_EXECUTION_STATUS_QUERY = "./resources/retrieveBatchJobExecutionStatus.sql";

	public static List<BatchJob> retrieveBatchJobs(String schedulerName, String activationStatus) {
		String sql = ResourceLoader.loadFileResourceAsString(FILENAME_SQL_RETRIEVE_BATCH_JOBS_QUERY);
		Object[] params = { schedulerName, activationStatus };
		List<BatchJob> batchJobs = null;
		try {
			// Execute the query and map the SQL ResultSet to a list of BatchJob beans
			batchJobs = Yank.queryBeanList(sql, BatchJob.class, params);
			if(batchJobs==null) {
				LOG.error("Check Scheduled jobs configured & executed in System Administration Utility");
			}
		} catch (YankSQLException e) {
			LOG.error("Failed to retrieve Batch Jobs", e);
			throw (e);
		}
		if (batchJobs != null && !batchJobs.isEmpty()) {
			List<BatchJob> batchJobsCopy = new ArrayList<BatchJob>(batchJobs);
			for (BatchJob batchJob : batchJobsCopy) {
				LOG.info("Retrieving Job Steps for Batch Job '" + batchJob.getIdentifier() + "'");
				batchJob.setJobSteps(BatchJobStepDAO.retrieveBatchJobStepsForJob(batchJob.getObjectId()));

			}
		}
		return batchJobs;
	}

	public static ReferenceData retrieveStatusForBatchJob(String schedulerName, String identifier) {
		String sql = ResourceLoader.loadFileResourceAsString(FILENAME_SQL_CHECK_BATCH_JOB_EXECUTION_STATUS_QUERY);
		Object[] params = { schedulerName, identifier };
		try {
			// Execute the query and map the SQL ResultSet to an
			// ExecutionStatusReferenceData bean
			ReferenceData executionStatus = Yank.queryBean(sql, ExecutionStatusReferenceData.class, params);
			if(executionStatus==null) {
				LOG.error("Check Scheduled jobs configured & executed in System Administration Utility");
			}
			LOG.info("Execution status for Batch Job '" + identifier + "' is '" + executionStatus.getName() + "'");
			return executionStatus;
		} catch (YankSQLException e) {
			LOG.error("Failed to check execution status of Batch Job '" + identifier + "'", e);
			throw e;
		}
	}

}
