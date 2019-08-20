package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "additional-driver")
public class AdditionalDriverDetail {
	private Long driverId;

	public AdditionalDriverDetail(){
	}
	
	@XmlElement(name = "driver-instance-id")
	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}
}
