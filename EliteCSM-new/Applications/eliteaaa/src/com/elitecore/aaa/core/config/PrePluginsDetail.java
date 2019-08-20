package com.elitecore.aaa.core.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

@XmlType(propOrder = {})
public class PrePluginsDetail {

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
