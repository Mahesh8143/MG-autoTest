package com.csc.sics.mg.domain;

import org.knowm.yank.annotations.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountingOrder {

	public static Logger LOG = LoggerFactory.getLogger(AccountingOrder.class);

	@Column("ORDER_ID")
	private String orderId;

	@Column("TITLE")
	private String title;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
