package com.elitecore.aaa.diameter.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class DiameterPluginsDetail {
	
	private InPluginsDetail inPlugins;
	private OutPluginsDetail outPlugins;
	
	public DiameterPluginsDetail() {
		this.inPlugins = new InPluginsDetail();
		this.outPlugins  = new OutPluginsDetail();
	}

	@XmlElement(name = "in-plugins")
	public InPluginsDetail getInPlugins() {
		return inPlugins;
	}

	public void setInPlugins(InPluginsDetail inPlugins) {
		this.inPlugins = inPlugins;
	}

	@XmlElement(name = "out-plugins")
	public OutPluginsDetail getOutPlugins() {
		return outPlugins;
	}

	public void setOutPlugins(OutPluginsDetail outPlugins) {
		this.outPlugins = outPlugins;
	}

}
