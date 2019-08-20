package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "secondary-driver")
public class SecondaryAndCacheDriverDetail {
	private Long secondaryDriverId;
	private Long cacheDriverId;

	public SecondaryAndCacheDriverDetail(){
	}

	@XmlElement(name = "driver-instance-id")
	public Long getSecondaryDriverId() {
		return secondaryDriverId;
	}
	
	public void setSecondaryDriverId(Long secondaryDriverId) {
		this.secondaryDriverId = secondaryDriverId;
	}
	
	@XmlElement(name = "cache-driver-id",type = Long.class)
	public Long getCacheDriverId() {
		return cacheDriverId;
	}
	public void setCacheDriverId(Long cacheDriverId) {
		this.cacheDriverId = cacheDriverId;
	}
}