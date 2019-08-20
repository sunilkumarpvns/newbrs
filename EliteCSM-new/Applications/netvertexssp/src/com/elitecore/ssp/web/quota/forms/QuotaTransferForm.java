package com.elitecore.ssp.web.quota.forms;

import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class QuotaTransferForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	
	private String errorMessage;
	private String userIdToBeRequested;
	private String quotaToBeRequested;

	public String getUserIdToBeRequested() {
		return userIdToBeRequested;
	}
	public void setUserIdToBeRequested(String userIdToBeRequested) {
		this.userIdToBeRequested = userIdToBeRequested;
	}
	public String getQuotaToBeRequested() {
		return quotaToBeRequested;
	}
	public void setQuotaToBeRequested(String quotaToBeRequested) {
		this.quotaToBeRequested = quotaToBeRequested;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}