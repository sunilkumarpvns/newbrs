package com.elitecore.elitesm.web.servicepolicy.dynauth.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewDynAuthServicePolicyForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String dynAuthPolicyId;

	public String getDynAuthPolicyId() {
		return dynAuthPolicyId;
	}

	public void setDynAuthPolicyId(String dynAuthPolicyId) {
		this.dynAuthPolicyId = dynAuthPolicyId;
	}
	
}
