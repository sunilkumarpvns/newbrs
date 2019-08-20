package com.elitecore.elitesm.web.servicepolicy.dynauth.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ManageDynAuthPolicyOrderForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String status;
	private String action;
	private List<DynAuthPolicyInstData> policyList;
	
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
	public List<DynAuthPolicyInstData> getPolicyList() {
		return policyList;
	}
	public void setPolicyList(List<DynAuthPolicyInstData> policyList) {
		this.policyList = policyList;
	}
	
	
	
}
