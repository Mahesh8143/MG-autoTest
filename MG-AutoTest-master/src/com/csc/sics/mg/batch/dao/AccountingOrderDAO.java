package com.csc.sics.mg.batch.dao;

import org.knowm.yank.Yank;
import org.knowm.yank.exceptions.YankSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csc.sics.mg.domain.AccountingOrder;
import com.csc.sics.mg.utils.ResourceLoader;

public class AccountingOrderDAO {

	public static Logger LOG = LoggerFactory.getLogger(AccountingOrderDAO.class);

	public static final String FILENAME_SQL_RETRIEVE_ORDER_TITLE_QUERY = "./resources/retrieveOrder.sql";

	public static AccountingOrder retrieveAccountingOrder(String orderId) {
		String sql = ResourceLoader.loadFileResourceAsString(FILENAME_SQL_RETRIEVE_ORDER_TITLE_QUERY);
		Object[] params = { orderId };
		AccountingOrder accountingOrder;
		try {
			accountingOrder = Yank.queryBean(sql, AccountingOrder.class, params);
			if(accountingOrder==null) {
				LOG.error("Check Scheduled jobs configured & executed in System Administration Utility");
			}
			
		} catch (YankSQLException e) {
			LOG.error("Failed to retrieve Accounting Order '" + orderId + "'", e);
			throw (e);
		}
		return accountingOrder;
	}

}
