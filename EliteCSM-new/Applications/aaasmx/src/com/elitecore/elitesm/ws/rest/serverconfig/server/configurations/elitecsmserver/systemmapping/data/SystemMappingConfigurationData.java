package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.systemmapping.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = {"radPluginInfoList","diameterPluginInfoList"})
@XmlRootElement(name = "mapping")
public class SystemMappingConfigurationData {
	private List<PluginInfoData> radPluginInfoList;
	private List<PluginInfoData> diameterPluginInfoList;
	
	public SystemMappingConfigurationData() {
		radPluginInfoList = new ArrayList<PluginInfoData>();
		diameterPluginInfoList = new ArrayList<PluginInfoData>();
	}
	
	@XmlElementWrapper(name ="rad-plugins")
	@XmlElement(name ="plugin")
	@Size(min=1,message="Atleast one plugin entry required in Radius plugins")
	public List<PluginInfoData> getRadPluginInfoList() {
		return radPluginInfoList;
	}

	public void setRadPluginInfoList(List<PluginInfoData> radPluginInfoList) {
		this.radPluginInfoList = radPluginInfoList;
	}

	@XmlElementWrapper(name ="diameter-plugins")
	@XmlElement(name ="plugin")
	@Size(min=1,message="Atleast one plugin entry required in Diameter plugins")
	public List<PluginInfoData> getDiameterPluginInfoList() {
		return diameterPluginInfoList;
	}

	public void setDiameterPluginInfoList(List<PluginInfoData> diameterPluginInfoList) {
		this.diameterPluginInfoList = diameterPluginInfoList;
	}	
}
