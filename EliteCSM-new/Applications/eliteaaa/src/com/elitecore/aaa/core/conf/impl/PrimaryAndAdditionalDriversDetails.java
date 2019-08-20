package com.elitecore.aaa.core.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.radius.conf.impl.AdditionalDriverDetail;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;

@XmlType(propOrder = {})
public class PrimaryAndAdditionalDriversDetails {

	private List<PrimaryDriverDetail> primaryDriverGroupList;
	private List<AdditionalDriverDetail> additionalDriverList;
	private String driverScript;

	public PrimaryAndAdditionalDriversDetails(){
		//required by Jaxb.
		primaryDriverGroupList = new ArrayList<PrimaryDriverDetail>();
		additionalDriverList = new ArrayList<AdditionalDriverDetail>();
	}
	@XmlElement(name = "driver-script",type=String.class)
	public String getDriverScript() {
		return driverScript;
	}
	public void setDriverScript(String script) {
		this.driverScript = script;
	}
	@XmlElementWrapper(name = "primary-group")
	@XmlElement(name = "primary-driver")
	public List<PrimaryDriverDetail> getPrimaryDriverGroup() {
		return primaryDriverGroupList;
	}
	public void setPrimaryDriverGroup(List<PrimaryDriverDetail> primaryDriverGroup) {
		this.primaryDriverGroupList = primaryDriverGroup;
	}
	@XmlElementWrapper(name = "additional-group")
	@XmlElement(name = "additional-driver")
	public List<AdditionalDriverDetail> getAdditionalDriverList() {
		return additionalDriverList;
	}
	public void setAdditionalDriverList(List<AdditionalDriverDetail> additionalDriverList) {
		this.additionalDriverList = additionalDriverList;
	}

}
