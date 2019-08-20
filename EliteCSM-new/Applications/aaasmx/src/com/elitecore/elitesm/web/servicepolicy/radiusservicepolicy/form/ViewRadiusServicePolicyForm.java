package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class ViewRadiusServicePolicyForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String radiusPolicyId;
	
	public String getRadiusPolicyId() {
		return radiusPolicyId;
	}
	public void setRadiusPolicyId(String radiusPolicyId) {
		this.radiusPolicyId = radiusPolicyId;
	}
	
	@Override
	public String toString() {
		return "----------------ViewRadiusServicePolicyForm------------------------\n  radiusPolicyId = "
				+ radiusPolicyId
				+ "\n----------------ViewRadiusServicePolicyForm-------------------------\n";
	}

}
