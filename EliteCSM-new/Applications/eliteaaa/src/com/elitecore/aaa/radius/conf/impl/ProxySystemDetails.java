package com.elitecore.aaa.radius.conf.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class ProxySystemDetails {

	private String proxyScript;
	private String translationMappingId;
	private List<ProxySystemDetail> proxySystemDetails;

	public ProxySystemDetails(){
		//required by Jaxb.
		proxySystemDetails = new ArrayList<ProxySystemDetail>();

	}
	@XmlElement(name = "script",type = String.class)
	public String getProxyScript() {
		return proxyScript;
	}

	public void setProxyScript(String proxyScript) {
		this.proxyScript = proxyScript;
	}
	@XmlElement(name = "translation-mapping-name",type = String.class)
	public String getTranslationMappingId() {
		return translationMappingId;
	}

	public void setTranslationMappingId(String translationMappingId) {
		this.translationMappingId = translationMappingId;
	}

	@XmlElementWrapper(name ="proxy-servers")
	@XmlElement(name = "proxy-server")
	public List<ProxySystemDetail> getProxySystemDetails() {
		return proxySystemDetails;
	}

	public void setProxySystemDetails(List<ProxySystemDetail> proxySystemDetails) {
		this.proxySystemDetails = proxySystemDetails;
	}	
}
