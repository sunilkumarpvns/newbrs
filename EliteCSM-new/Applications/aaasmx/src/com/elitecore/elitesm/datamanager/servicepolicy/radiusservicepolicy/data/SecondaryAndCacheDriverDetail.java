package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import javax.xml.bind.annotation.XmlElement;

//@XmlType(propOrder = {"secondaryDriverId", "cacheDriverId"})
public class SecondaryAndCacheDriverDetail {
	private String secondaryDriverId;
	private String cacheDriverId;

	@XmlElement(name = "secondary-driver-id",type = String.class)
	public String getSecondaryDriverId() {
		return secondaryDriverId;
	}
	public void setSecondaryDriverId(String secondaryDriverId) {
		this.secondaryDriverId = secondaryDriverId;
	}
	
	@XmlElement(name = "cache-driver-id",type = String.class)
	public String getCacheDriverId() {
		return cacheDriverId;
	}
	public void setCacheDriverId(String cacheDriverId) {
		this.cacheDriverId = cacheDriverId;
	}
}