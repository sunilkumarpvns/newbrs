package com.elitecore.aaa.core.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={})
public class JNDIProprtyDetails {
	private List<JNDIProprtyDetail> jndiProprtyDetailList;
	
	public JNDIProprtyDetails() {
		this.jndiProprtyDetailList = new ArrayList<JNDIProprtyDetail>();
	}

	@XmlElement(name="jndi-property")
	public List<JNDIProprtyDetail> getJndiProprtyDetailList() {
		return jndiProprtyDetailList;
	}

	public void setJndiProprtyDetailList(
			List<JNDIProprtyDetail> jndiProprtyDetailList) {
		this.jndiProprtyDetailList = jndiProprtyDetailList;
	}
	
	
}
