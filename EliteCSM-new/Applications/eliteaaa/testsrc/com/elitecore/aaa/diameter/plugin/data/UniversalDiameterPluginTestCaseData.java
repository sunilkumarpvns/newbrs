package com.elitecore.aaa.diameter.plugin.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.diameter.packet.jaxb.DiameterPacketData;
import com.elitecore.diameterapi.diameter.common.packet.assertion.CustomAssertion;
import com.elitecore.diameterapi.plugins.universal.conf.DiameterUniversalPluginDetails;

@XmlRootElement(name = "test-case")
public class UniversalDiameterPluginTestCaseData {

	private DiameterUniversalPluginDetails diameterUniversalPluginData;

	private DiameterPacketData requestPacket;
	private DiameterPacketData responsePacket;
	
	private CustomAssertion assertion;
	
	private String behaviour;
	
	@XmlElement(name = "diameter-request")
	public DiameterPacketData getRequestPacket() {
		return requestPacket;
	}

	public void setRequestPacket(DiameterPacketData requestPacket) {
		this.requestPacket = requestPacket;
	}

	@XmlElement(name = "diameter-answer")
	public DiameterPacketData getResponsePacket() {
		return responsePacket;
	}


	public void setResponsePacket(DiameterPacketData responsePacket) {
		this.responsePacket = responsePacket;
	}

	@XmlElement(name = "diameter-universal-plugin-detail")
	public DiameterUniversalPluginDetails getDiameterUniversalPluginData() {
		return diameterUniversalPluginData;
	}


	public void setDiameterUniversalPluginData(
			DiameterUniversalPluginDetails diameterUniversalPluginData) {
		this.diameterUniversalPluginData = diameterUniversalPluginData;
	}

	@XmlElement(name = "assert")
	public CustomAssertion getAssertion() {
		return assertion;
	}

	public void setAssertion(CustomAssertion assertion) {
		this.assertion = assertion;
	}

	@XmlAttribute(name = "behaviour")
	public String getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}
}
