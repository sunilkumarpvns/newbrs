package com.elitecore.elitesm.ws.rest.serverconfig.server.services.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement(name = "pre-plugins")
@XmlType(propOrder = {"prePlugins"})
public class PrePluginsDetail {
	
	@Valid
	@NotEmpty(message = "Atleast one Pre Plugin Entry must be specified." )
	private List<PluginEntryDetail> prePlugins;

	public PrePluginsDetail(){
		//required by Jaxb.
		prePlugins = new ArrayList<PluginEntryDetail>();
	}
	
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getPrePlugins() {
		return prePlugins;
	}

	public void setPrePlugins(List<PluginEntryDetail> prePlugins) {
		this.prePlugins = prePlugins;
	}
}
