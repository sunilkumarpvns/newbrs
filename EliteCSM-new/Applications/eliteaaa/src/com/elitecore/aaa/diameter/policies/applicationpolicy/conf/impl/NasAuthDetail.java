package com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.radius.conf.impl.AdditionalDriverDetail;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

@XmlType(propOrder={})
public class NasAuthDetail {
	
	private List<PrimaryDriverDetail> primaryDriverDetails;
	private List<AdditionalDriverDetail> additionalDriverDetails;
	private String script;
	
	private List<PluginEntryDetail> prePlugins;
	private List<PluginEntryDetail> postPlugins;
	
	public NasAuthDetail() {
		this.primaryDriverDetails = new ArrayList<PrimaryDriverDetail>();
		this.additionalDriverDetails = new ArrayList<AdditionalDriverDetail>();
		
		this.prePlugins = new ArrayList<PluginEntryDetail>();
		this.postPlugins = new ArrayList<PluginEntryDetail>();
	}

	@XmlElementWrapper(name = "primary-group")
	@XmlElement(name = "primary-driver")
	public List<PrimaryDriverDetail> getPrimaryDriverDetails() {
		return primaryDriverDetails;
	}

	@XmlElementWrapper(name = "additional-group")
	@XmlElement(name = "additional-driver")
	public List<AdditionalDriverDetail> getAdditionalDriverDetails() {
		return additionalDriverDetails;
	}

	@XmlElement(name = "script",type=String.class)
	public String getScript() {
		return script;
	}

	public void setPrimaryDriverDetails(
			List<PrimaryDriverDetail> primaryDriverDetails) {
		this.primaryDriverDetails = primaryDriverDetails;
	}

	public void setAdditionalDriverDetails(
			List<AdditionalDriverDetail> additionalDriverDetails) {
		this.additionalDriverDetails = additionalDriverDetails;
	}

	public void setScript(String script) {
		this.script = script;
	}
	
	@XmlElementWrapper(name = "in-plugins-list")
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getPrePlugins() {
		return prePlugins;
	}
	
	public void setPrePlugins(List<PluginEntryDetail> prePlugins) {
		this.prePlugins = prePlugins;
	}
	
	@XmlElementWrapper(name = "out-plugins-list")
	@XmlElement(name = "plugin-entry")
	public List<PluginEntryDetail> getPostPlugins() {
		return postPlugins;
	}
	
	public void setPostPlugins(List<PluginEntryDetail> postPlugins) {
		this.postPlugins = postPlugins;
	}
}