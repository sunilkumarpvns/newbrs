package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewNASServicePolicyForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	
	private String nasPolicyId;

	public String getNasPolicyId() {
		return nasPolicyId;
	}

	public void setNasPolicyId(String nasPolicyId) {
		this.nasPolicyId = nasPolicyId;
	}
}
