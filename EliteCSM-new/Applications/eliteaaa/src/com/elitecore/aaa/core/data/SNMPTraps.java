package com.elitecore.aaa.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = {})
public class SNMPTraps {

	private List<SNMPTrapAlertConfiguration> snmpTrapAlertConfigurationsList;

	public SNMPTraps(){
		//required by Jaxb.
		snmpTrapAlertConfigurationsList = new ArrayList<SNMPTrapAlertConfiguration>();
	}

	@XmlElement(name = "trap-listner")
	public List<SNMPTrapAlertConfiguration> getSnmpTrapAlertConfigurationsList() {
		return snmpTrapAlertConfigurationsList;
	}

	public void setSnmpTrapAlertConfigurationsList(List<SNMPTrapAlertConfiguration> snmpTrapAlertConfigurationsList) {
		this.snmpTrapAlertConfigurationsList = snmpTrapAlertConfigurationsList;
	}

}
