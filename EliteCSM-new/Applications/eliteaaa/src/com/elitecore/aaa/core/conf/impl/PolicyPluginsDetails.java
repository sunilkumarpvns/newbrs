package com.elitecore.aaa.core.conf.impl;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
public class PolicyPluginsDetails {

	private String postPlugins;
	private String prePlugins;


	public PolicyPluginsDetails(){
		// Required by Jaxb.
	}

	@XmlElement(name = "pre-plugins",type = String.class)
	public String getPrePlugins() {
		return prePlugins;
	}
	public void setPrePlugins(String prePlugins) {
		this.prePlugins = prePlugins;
	}
	@XmlElement(name = "post-plugins",type = String.class)
	public String getPostPlugins() {
		return postPlugins;
	}
	public void setPostPlugins(String postPlugins) {
		this.postPlugins = postPlugins;
	}


}
