package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "primary-driver")
public class PrimaryDriverDetail {
	
	private Long driverInstanceId;
	private int weightage;

	public PrimaryDriverDetail() {
	}

	@XmlElement(name = "driver-instance-id")
	@NotNull(message = "Select at least one primary driver for policy")
	public Long getDriverInstanceId() {
		return driverInstanceId;
	}
	
	public void setDriverInstanceId(Long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@XmlElement(name = "weightage",type = int.class)
	public int getWeightage() {
		return weightage;
	}
	
	public void setWeightage(int weightage) {
		this.weightage = weightage;
	}
}