package com.elitecore.aaa.radius.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class PrimaryDriverDetail {
	private String driverInstanceId;
	private int weightage;

	public PrimaryDriverDetail() {
		//required by Jaxb.
	}
	public PrimaryDriverDetail(String driverInstaceId,int weightage){
		this.driverInstanceId = driverInstaceId;
		this.weightage = weightage;
	}

	@XmlElement(name = "driver-instace-id",type = String.class)
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
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
