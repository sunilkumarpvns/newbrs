package com.elitecore.aaa.diameter.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

@XmlType(propOrder={})
public class OutPluginsDetail {

	private List<PluginEntryDetail> outPluginList;

	public OutPluginsDetail(){
		outPluginList = new ArrayList<PluginEntryDetail>();
	}

	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getOutPlugins() {
		return outPluginList;
	}

	public void setOutPlugins(List<PluginEntryDetail> outPluginList) {
		this.outPluginList = outPluginList;
	}
}
