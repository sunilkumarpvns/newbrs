package com.elitecore.elitesm.ws.ytl.cal.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cal-subscriber-operations")
public class CALAAAConfig {

	private InstanceDetails instanceDetails;
	private List<LogicalMapping> logicalMappings = new ArrayList<LogicalMapping>();

	@XmlElement(name = "instance-details")
	public InstanceDetails getInstanceDetails() {
		return instanceDetails;
	}
	
	public void setInstanceDetails(InstanceDetails instanceDetails) {
		this.instanceDetails = instanceDetails;
	}
	
	@XmlElementWrapper(name = "logical-mappings")
	@XmlElement(name = "logical-mapping")
	public List<LogicalMapping> getLogicalMappings() {
		return logicalMappings;
	}
}
