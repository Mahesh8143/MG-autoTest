package com.csc.sics.mg.batch.dao;

import org.knowm.yank.annotations.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csc.sics.mg.domain.ReferenceData;

public class ExecutionStatusReferenceData extends ReferenceData {

	public static Logger LOG = LoggerFactory.getLogger(ExecutionStatusReferenceData.class);

	@Column("STATUS_NAME")
	protected String name;

	@Column("STATUS_CODE")
	protected String code;

}
