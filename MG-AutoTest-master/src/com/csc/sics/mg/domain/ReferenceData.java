package com.csc.sics.mg.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReferenceData {

	public static Logger LOG = LoggerFactory.getLogger(ReferenceData.class);

	protected String name;
	protected String code;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
