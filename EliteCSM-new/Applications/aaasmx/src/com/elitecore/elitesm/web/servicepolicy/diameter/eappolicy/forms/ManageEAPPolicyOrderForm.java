package com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ManageEAPPolicyOrderForm extends BaseWebForm {
	private String status;
	private String action;
	private List policyList;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List getPolicyList() {
		return policyList;
	}
	public void setPolicyList(List policyList) {
		this.policyList = policyList;
	}
}
