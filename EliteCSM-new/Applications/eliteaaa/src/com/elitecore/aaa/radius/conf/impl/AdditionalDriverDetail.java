package com.elitecore.aaa.radius.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class AdditionalDriverDetail {
	private String driverId;

	public AdditionalDriverDetail(){
		// required by Jaxb.
	}
	public AdditionalDriverDetail(String driverId){
		this.driverId = driverId;
	}
	@XmlElement(name = "additional-driver-id")
	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

}
