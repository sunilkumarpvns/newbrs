package com.elitecore.aaa.radius.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class ProxySystemDetail {
	private int esiId;
	private int  weightage;

	public ProxySystemDetail() {
		// required By Jaxb.
	}
	public ProxySystemDetail(int esiId, int weightage){
		this.esiId = esiId;
		this.weightage = weightage;
	}
	@XmlElement(name = "proxy-system-esi-id",type = int.class)
	public int getEsiId() {
		return esiId;
	}
	public void setEsiId(int esiId) {
		this.esiId = esiId;
	}
	@XmlElement(name = "proxy-system-weightage",type = int.class)
	public int getWeightage() {
		return weightage;
	}
	public void setWeightage(int weightage) {
		this.weightage = weightage;
	}


}
