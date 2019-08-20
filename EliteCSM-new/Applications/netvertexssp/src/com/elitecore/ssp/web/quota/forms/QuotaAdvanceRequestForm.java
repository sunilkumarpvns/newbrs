package com.elitecore.ssp.web.quota.forms;

import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class QuotaAdvanceRequestForm extends BaseWebForm{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4577863843326330439L;

	private int noOfMonth;
	
	private int quotaAmount;
	
	private long totalQuotaToBeMoved;
	
	private String actionPerformed;

	public int getNoOfMonth() {
		return noOfMonth;
	}

	public void setNoOfMonth(int noOfMonth) {
		this.noOfMonth = noOfMonth;
	}

	public int getQuotaAmount() {
		return quotaAmount;
	}

	public void setQuotaAmount(int quotaAmount) {
		this.quotaAmount = quotaAmount;
	}

	public long getTotalQuotaToBeMoved() {
		
		totalQuotaToBeMoved = quotaAmount * noOfMonth;
		
		return totalQuotaToBeMoved;
	}

	public void setTotalQuotaToBeMoved(int totalQuotaToBeMoved) {
		this.totalQuotaToBeMoved = totalQuotaToBeMoved;
	}

	public String getActionPerformed() {
		return actionPerformed;
	}

	public void setActionPerformed(String actionPerformed) {
		this.actionPerformed = actionPerformed;
	}
}
