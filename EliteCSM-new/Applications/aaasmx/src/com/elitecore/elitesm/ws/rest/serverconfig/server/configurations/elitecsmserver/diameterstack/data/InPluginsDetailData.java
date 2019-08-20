package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

@XmlType(propOrder={"inPluginList"})
public class InPluginsDetailData {

	private List<PluginEntryDetailData> inPluginList;

	public InPluginsDetailData(){
		inPluginList = new ArrayList<PluginEntryDetailData>();
	}
	@XmlElement(name = "plugin-entry")
	@Valid
	@NotEmpty(message="Atleast one Inplugin entry must be specified")
	public List<PluginEntryDetailData> getInPluginList() {
		return inPluginList;
	}

	public void setInPluginList(List<PluginEntryDetailData> inPluginList) {
		this.inPluginList = inPluginList;
	}

}

