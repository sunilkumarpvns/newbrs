package com.elitecore.elitesm.datamanager.servermgr.data;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.elitesm.ws.rest.validator.server.ValidServiceType;

@XmlRootElement(name="configured-services")
public class NetServiceData {
	
	private List<String> serviceNames;
	
	@XmlElement(name="service-name")
	@ValidServiceType
	public List<String> getServicesNames() {
		return serviceNames;
	}

	public void setServicesNames(List<String> servicesNames) {
		this.serviceNames = servicesNames;
	}

}
