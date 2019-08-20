package com.elitecore.aaa.radius.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class PrepaidSystemDetail {
	private int esiId;
	private int weightage;

	public PrepaidSystemDetail() {
		// required By Jaxb.
	}

	public PrepaidSystemDetail(int esiId, int weightage){
		this.esiId = esiId;
		this.weightage = weightage;
	}
	@XmlElement(name = "prepaid-system-esi-id",type = int.class)
	public int getEsiId() {
		return esiId;
	}
	public void setEsiId(int esiId) {
		this.esiId = esiId;
	}
	@XmlElement(name = "prepaid-system-weightage",type = int.class)
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
