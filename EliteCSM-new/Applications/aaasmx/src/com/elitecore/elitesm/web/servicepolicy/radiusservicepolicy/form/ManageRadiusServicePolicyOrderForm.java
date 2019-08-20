package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form;

import java.util.List;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ManageRadiusServicePolicyOrderForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String action;
	private List policyList;
	private String status;
	
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
