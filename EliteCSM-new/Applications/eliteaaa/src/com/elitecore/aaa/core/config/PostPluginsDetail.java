package com.elitecore.aaa.core.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

@XmlType(propOrder = {})
public class PostPluginsDetail {

	private List<PluginEntryDetail> postPlugins;

	public PostPluginsDetail(){
		postPlugins = new ArrayList<PluginEntryDetail>();
	}

	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getPostPlugins() {
		return postPlugins;
	}

	public void setPostPlugins(List<PluginEntryDetail> postPlugins) {
		this.postPlugins = postPlugins;
	}
}
