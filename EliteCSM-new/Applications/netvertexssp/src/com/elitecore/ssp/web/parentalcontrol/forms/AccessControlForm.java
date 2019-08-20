package com.elitecore.ssp.web.parentalcontrol.forms;

import java.util.List;

import com.elitecore.netvertexsm.ws.cxfws.ssp.parental.ParentalPolicy;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage;
import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class AccessControlForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	
	private AddOnPackage[] promotionalData;
	private List<ParentalPolicy> parentalPolicyList;
	private long usageControlAddOn;
			
	public long getUsageControlAddOn() {
		return usageControlAddOn;
	}

	public void setUsageControlAddOn(long usageControlAddOn) {
		this.usageControlAddOn = usageControlAddOn;
	}

	public AddOnPackage[] getPromotionalData() {
		return promotionalData;
	}

	public void setPromotionalData(AddOnPackage[] promotionalData) {
		this.promotionalData = promotionalData;
	}

	public List<ParentalPolicy> getParentalPolicyList() {
		return parentalPolicyList;
	}

	public void setParentalPolicyList(List<ParentalPolicy> parentalPolicyList) {
		this.parentalPolicyList = parentalPolicyList;
	}

 }
