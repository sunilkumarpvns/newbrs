package com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.validator.constraints.Range;

//@XmlType(propOrder = {"driverInstanceId", "weightage"})
public class PrimaryDriverDetail {
	@Valid
	private String driverInstanceId;
	
	@Range(min = 0, max = 9, message = "Invalid Range of weightage, It must be between 0-9")
	private Integer weightage;

	@XmlElement(name = "driver-instace-id",type = String.class)
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@XmlElement(name = "weightage",type = Integer.class)
	public Integer getWeightage() {
		return weightage;
	}
	public void setWeightage(Integer weightage) {
		this.weightage = weightage;
	}
}