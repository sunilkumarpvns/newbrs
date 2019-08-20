package com.elitecore.aaa.core.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class ServiceTypes {

	private List<ServiceDetails> configuredServiceList;
	
	public ServiceTypes(){
		//required by Jaxb.
		configuredServiceList = new ArrayList<ServiceDetails>();
	}
	
	@XmlElement(name = "service")
	public List<ServiceDetails> getService() {
		return configuredServiceList;
	}

	public void setService(List<ServiceDetails> configuredServiceList) {
		this.configuredServiceList = configuredServiceList;
	}
}
