package com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class CommonStatusData extends BaseData implements ICommonStatusData{
	private String commonStatusId;
	private String showHide;
	public String getCommonStatusId() {
		return commonStatusId;
	}
	public void setCommonStatusId(String commonStatusId) {
		this.commonStatusId = commonStatusId;
	}
	public String getShowHide() {
		return showHide;
	}
	public void setShowHide(String showHide) {
		this.showHide = showHide;
	}
	
}
