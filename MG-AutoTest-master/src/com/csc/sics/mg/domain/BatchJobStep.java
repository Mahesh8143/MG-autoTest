package com.csc.sics.mg.domain;

import java.util.Map;

import org.knowm.yank.annotations.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchJobStep {

	public static Logger LOG = LoggerFactory.getLogger(BatchJobStep.class);

	@Column("OBJECT_ID")
	protected String objectId;

	@Column("SEQUENCE")
	private int sequence;

	@Column("JOB_STEP_TYPE")
	private String type;

	private Map<String, String> jobStepParameters;

	private AccountingOrder accountingOrder;

	private ReferenceData executionStatus;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Map<String, String> getJobStepParameters() {
		return jobStepParameters;
	}

	public void setJobStepParameters(Map<String, String> jobStepParameters) {
		this.jobStepParameters = jobStepParameters;
	}

	public AccountingOrder getAccountingOrder() {
		return accountingOrder;
	}

	public void setAccountingOrder(AccountingOrder accountingOrder) {
		this.accountingOrder = accountingOrder;
	}

	public ReferenceData getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(ReferenceData executionStatus) {
		this.executionStatus = executionStatus;
	}

	public boolean isAccountingOrderJobStep() {
		return "ACCORD".equals(this.getType());
	}

	public boolean isSQLJobStep() {
		return "SQL".equals(this.getType());
	}
}
