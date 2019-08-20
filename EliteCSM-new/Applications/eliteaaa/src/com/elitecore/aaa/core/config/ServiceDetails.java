package com.elitecore.aaa.core.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class ServiceDetails {

	private String serviceId;
	private boolean enabled = true;

	public ServiceDetails(){
		//required by Jaxb.
	}
	@XmlElement(name = "service-id",type = String.class)
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	@XmlElement(name = "enabled",type = boolean.class,defaultValue ="true")
	public boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
