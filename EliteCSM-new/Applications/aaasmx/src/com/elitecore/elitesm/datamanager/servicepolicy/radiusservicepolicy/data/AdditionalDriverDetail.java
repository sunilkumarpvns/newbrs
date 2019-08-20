package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"driverId"})
public class AdditionalDriverDetail {

	private String driverId;

	@XmlElement(name = "additional-driver-id")
	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

}