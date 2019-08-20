package com.elitecore.aaa.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = {})
public class SysLogAlerts {

	private List<SysLogAlertConfiguration> sysLogAlertConfigurationsList;

	public SysLogAlerts(){
		// required by jaxb.
		sysLogAlertConfigurationsList = new ArrayList<SysLogAlertConfiguration>();
	}

	@XmlElement(name = "system-listner")
	public List<SysLogAlertConfiguration> getSysLogAlertConfigurationsList() {
		return sysLogAlertConfigurationsList;
	}

	public void setSysLogAlertConfigurationsList(List<SysLogAlertConfiguration> sysLogAlertConfigurationsList) {
		this.sysLogAlertConfigurationsList = sysLogAlertConfigurationsList;
	}
	


}
