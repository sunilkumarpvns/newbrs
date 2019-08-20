package com.elitecore.aaa.radius.service.plugins.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.plugins.UniversalPluginConfigurationImpl;
import com.elitecore.aaa.core.radius.packet.jaxb.RadiusPacketData;
import com.elitecore.aaa.radius.service.assertion.CustomAssertion;


@XmlRootElement(name = "test-case")
public class UniversalPluginTestCaseData {
	
	private UniversalPluginConfigurationImpl universalPluginData;
	
	private RadiusPacketData requestPacketData;
	private RadiusPacketData responsePacketData;
	
	private String expectedPacketType;
	private String behaviour;
	
	private CustomAssertion assertion;
	
	@XmlElement(name = "universal-plugin-detail")
	public UniversalPluginConfigurationImpl getUniversalPluginAuthData() {
		return universalPluginData;
	}

	public void setUniversalPluginAuthData(UniversalPluginConfigurationImpl universalPluginAuthData) {
		this.universalPluginData = universalPluginAuthData;
	}
	
	@XmlElement(name = "request-packet")
	public RadiusPacketData getRequestPacketData() {
		return requestPacketData;
	}
	
	public void setRequestPacketData(RadiusPacketData requestPacketData) {
		this.requestPacketData = requestPacketData;
	}
	
	@XmlElement(name = "response-packet")
	public RadiusPacketData getResponsePacketData() {
		return responsePacketData;
	}
	
	public void setResponsePacketData(RadiusPacketData responsePacketData) {
		this.responsePacketData = responsePacketData;
	}
	
	@XmlElement(name = "expected-packet-type")
	public String getExpectedPacketType() {
		return expectedPacketType;
	}
	
	public void setExpectedPacketType(String expectedPacketType) {
		this.expectedPacketType = expectedPacketType;
	}
	
	@XmlAttribute(name = "behaviour")
	public String getBehaviour() {
		return behaviour;
	}
	
	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}
	
	@XmlElement(name = "assert")
	public CustomAssertion getAssertion() {
		return assertion;
	}

	public void setAssertion(CustomAssertion assertion) {
		this.assertion = assertion;
	}
}
