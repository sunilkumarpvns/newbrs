package com.elitecore.elitesm.web.servicepolicy.diameter.creditcontrolpolicy.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.servicepolicy.diameter.creditcontrolpolicy.data.CreditControlPolicyData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ManageCreditControlPolicyOrderForm extends BaseWebForm{

	private static final long serialVersionUID = 1L;
	private String status;
	private String action;
	private List<CreditControlPolicyData> policyList;
	
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
	public List<CreditControlPolicyData> getPolicyList() {
		return policyList;
	}
	public void setPolicyList(List<CreditControlPolicyData> policyList) {
		this.policyList = policyList;
	}
}
