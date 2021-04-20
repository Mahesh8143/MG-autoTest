package com.csc.sics.mg.domain;

import java.util.ArrayList;
import java.util.List;

import org.knowm.yank.annotations.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchJob {

	public static Logger LOG = LoggerFactory.getLogger(BatchJob.class);

	@Column("OBJECT_ID")
	private String objectId;

	@Column("IDENTIFIER")
	private String identifier;

	@Column("TITLE")
	private String title;

	@Column("ACTIVATION_STATUS")
	private String activationStatus;

	@Column("PRIORITY")
	private int priority;

	private List<BatchJobStep> jobSteps;

	private ReferenceData executionStatus;

	public BatchJob() {
		this.setJobSteps(new ArrayList<BatchJobStep>());
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getActivationStatus() {
		return activationStatus;
	}

	public void setActivationStatus(String status) {
		this.activationStatus = status;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public List<BatchJobStep> getJobSteps() {
		return jobSteps;
	}

	public void setJobSteps(List<BatchJobStep> jobSteps) {
		this.jobSteps = jobSteps;
	}

	public ReferenceData getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(ReferenceData executionStatus) {
		this.executionStatus = executionStatus;
	}

	public void addJobStep(BatchJobStep jobStep) {
		this.getJobSteps().add(jobStep);
	}

	public boolean isDatabaseVerification() {
		return false;
	}

}
