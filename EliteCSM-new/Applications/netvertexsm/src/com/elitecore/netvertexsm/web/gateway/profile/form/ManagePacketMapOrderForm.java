package com.elitecore.netvertexsm.web.gateway.profile.form;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ManagePacketMapOrderForm extends BaseWebForm {
	
	private String status;
	private String action;
	private List packetMapList;
	private long profileId;
	
	
	public List getPacketMapList() {
		return packetMapList;
	}
	public void setPacketMapList(List packetMapList) {
		this.packetMapList = packetMapList;
	}
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
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
}
