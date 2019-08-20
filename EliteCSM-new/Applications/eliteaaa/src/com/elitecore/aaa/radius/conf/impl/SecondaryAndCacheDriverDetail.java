package com.elitecore.aaa.radius.conf.impl;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class SecondaryAndCacheDriverDetail {
	private String secondaryDriverId;
	private String cacheDriverId;

	public SecondaryAndCacheDriverDetail(){
		//required By Jaxb.
	}
	public SecondaryAndCacheDriverDetail(String secondaryDriverId, String cacheDriverId){
		this.secondaryDriverId = secondaryDriverId;
		this.cacheDriverId = cacheDriverId;
	}
	@XmlElement(name = "secondary-driver-id",type = String.class)
	public String getSecondaryDriverId() {
		return secondaryDriverId;
	}
	public void setSecondaryDriverId(String secondaryDriverId) {
		this.secondaryDriverId = secondaryDriverId;
	}
	
	@XmlElement(name = "cache-driver-id",type = String.class)
	@Nullable
	public String getCacheDriverId() {
		return cacheDriverId;
	}
	public void setCacheDriverId(String cacheDriverId) {
		this.cacheDriverId = cacheDriverId;
	}
}
