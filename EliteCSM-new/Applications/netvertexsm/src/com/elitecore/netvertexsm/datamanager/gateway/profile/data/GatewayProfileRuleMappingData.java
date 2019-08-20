package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

import com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData;

public class GatewayProfileRuleMappingData {
	private long profileRuleMappingId;
	private long profileId;
	private long ruleMappingId;
	private String accessNetworkType;
	private int orderNumber;
	private RuleMappingData ruleMappingData;
	public long getProfileRuleMappingId() {
		return profileRuleMappingId;
	}
	public void setProfileRuleMappingId(long profileRuleMappingId) {
		this.profileRuleMappingId = profileRuleMappingId;
	}
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public long getRuleMappingId() {
		return ruleMappingId;
	}
	public void setRuleMappingId(long ruleMappingId) {
		this.ruleMappingId = ruleMappingId;
	}
	public String getAccessNetworkType() {
		return accessNetworkType;
	}
	public void setAccessNetworkType(String accessNetworkType) {
		this.accessNetworkType = accessNetworkType;
	}
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	public RuleMappingData getRuleMappingData() {
		return ruleMappingData;
	}
	public void setRuleMappingData(RuleMappingData ruleMappingData) {
		this.ruleMappingData = ruleMappingData;
	}
	
	}
