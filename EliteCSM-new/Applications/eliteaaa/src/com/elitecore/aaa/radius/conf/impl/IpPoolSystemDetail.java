package com.elitecore.aaa.radius.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class IpPoolSystemDetail {
	private int esiId;
	private int weightage;

	public IpPoolSystemDetail() {
		// required By Jaxb.
	}
	public IpPoolSystemDetail(int esiId, int weightage){
		this.esiId = esiId;
		this.weightage = weightage;
	}

	@XmlElement(name = "ippool-system-esi-id",type = int.class)
	public int getEsiId() {
		return esiId;
	}
	public void setEsiId(int esiId) {
		this.esiId = esiId;
	}

	@XmlElement(name = "ippool-system-weightage",type = int.class)
	public int getWeightage() {
		return weightage;
	}
	public void setWeightage(int weightage) {
		this.weightage = weightage;
	}
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.print("		ID: "+esiId+" , Weighted: "+weightage);
		out.close();
		return stringBuffer.toString();
	}


}
