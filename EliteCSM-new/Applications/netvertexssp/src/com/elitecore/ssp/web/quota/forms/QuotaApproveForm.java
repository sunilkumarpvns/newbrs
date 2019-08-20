package com.elitecore.ssp.web.quota.forms;

import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class QuotaApproveForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	
	private String actionPerformedForApproval;
	private String requesterUserName;
	private String requestedQuota;
	
	public String getActionPerformedForApproval() {
		return actionPerformedForApproval;
	}
	public void setActionPerformedForApproval(String actionPerformedForApproval) {
		this.actionPerformedForApproval = actionPerformedForApproval;
	}
	public String getRequesterUserName() {
		return requesterUserName;
	}
	public void setRequesterUserName(String requesterUserName) {
		this.requesterUserName = requesterUserName;
	}
	public String getRequestedQuota() {
		return requestedQuota;
	}
	public void setRequestedQuota(String requestedQuota) {
		this.requestedQuota = requestedQuota;
	}

}