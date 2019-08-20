package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

@XmlType(propOrder={"outPluginList"})
public class OutPluginsDetailData {

	private List<PluginEntryDetailData> outPluginList;

	public OutPluginsDetailData(){
		outPluginList = new ArrayList<PluginEntryDetailData>();
	}

	@XmlElement(name = "plugin-entry")
	@Valid
	@NotEmpty(message="Atleast one OutPlugin entry must be specified")
	public List<PluginEntryDetailData> getOutPluginList() {
		return outPluginList;
	}

	public void setOutPluginList(List<PluginEntryDetailData> outPluginList) {
		this.outPluginList = outPluginList;
	}
}
