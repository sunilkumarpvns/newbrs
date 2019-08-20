package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class SecondaryDriverRelData {
	private String cacheDriverInstId;
	private String secondaryDriverInstId;
	
	public String getCacheDriverInstId() {
		return cacheDriverInstId;
	}
	public void setCacheDriverInstId(String cacheDriverInstId) {
		this.cacheDriverInstId = cacheDriverInstId;
	}
	public String getSecondaryDriverInstId() {
		return secondaryDriverInstId;
	}
	public void setSecondaryDriverInstId(String secondaryDriverInstId) {
		this.secondaryDriverInstId = secondaryDriverInstId;
	}
	
	@Override
	public String toString() {
		return "----------------SecondaryDriverRelData------------------------\n  cacheDriverInstId = "
				+ cacheDriverInstId
				+ "\n  secondaryDriverInstId = "
				+ secondaryDriverInstId
				+ "\n----------------SecondaryDriverRelData-------------------------\n";
	}

}
