package com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ManageCGPolicyOrderForm extends BaseWebForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String status;
	private String action;
	private List<CGPolicyData> policyList;

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
	
	public List<CGPolicyData> getPolicyList() {
		return policyList;
	}
	
	public void setPolicyList(List<CGPolicyData> policyList) {
		this.policyList = policyList;
	}
}
