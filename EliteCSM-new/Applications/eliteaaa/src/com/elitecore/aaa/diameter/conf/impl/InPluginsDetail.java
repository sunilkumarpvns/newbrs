package com.elitecore.aaa.diameter.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

@XmlType(propOrder={})
public class InPluginsDetail {

	private List<PluginEntryDetail> inPluginList;

	public InPluginsDetail(){
		inPluginList = new ArrayList<PluginEntryDetail>();
	}
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getInPlugins() {
		return inPluginList;
	}

	public void setInPlugins(List<PluginEntryDetail> inPluginList) {
		this.inPluginList = inPluginList;
	}

}
