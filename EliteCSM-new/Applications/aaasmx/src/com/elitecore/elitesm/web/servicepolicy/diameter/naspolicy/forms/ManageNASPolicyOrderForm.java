package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ManageNASPolicyOrderForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	private String action;
	private List<NASPolicyInstData> policyList;
	private String status;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<NASPolicyInstData> getPolicyList() {
		return policyList;
	}
	public void setPolicyList(List<NASPolicyInstData> policyList) {
		this.policyList = policyList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
