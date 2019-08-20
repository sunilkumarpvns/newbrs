package com.elitecore.aaa.diameter.translators.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.diameter.packet.jaxb.DiameterPacketData;
import com.elitecore.aaa.core.radius.packet.jaxb.RadiusPacketData;
import com.elitecore.diameterapi.core.translator.policy.impl.TranslationMappingConfigurationData;

@XmlRootElement(name = "case")
public class ResquestTranslationTestCaseData {

	private TranslationMappingConfigurationData translationMappingConfigurationData;
	private DiameterPacketData fromDiameterRequest;
	private RadiusPacketData expectedRadPacket;
	private String behaviour;
	
	@XmlElement(name = "trasnaltion-mapping")
	public TranslationMappingConfigurationData getTranslationMappingConfigurationData() {
		return translationMappingConfigurationData;	
	}

	@XmlElement(name = "diameter-packet")
	public DiameterPacketData getFromDiameterRequest() {
		return fromDiameterRequest;
	}

	@XmlElement(name = "radius-packet")
	public RadiusPacketData getExpectedRadPacket() {
		return expectedRadPacket;
	}

	public void setTranslationMappingConfigurationData(
			TranslationMappingConfigurationData translationMappingConfigurationData) {
		this.translationMappingConfigurationData = translationMappingConfigurationData;
	}

	public void setFromDiameterRequest(DiameterPacketData fromDiameterRequest) {
		this.fromDiameterRequest = fromDiameterRequest;
	}

	public void setExpectedRadPacket(RadiusPacketData expectedRadPacket) {
		this.expectedRadPacket = expectedRadPacket;
	}

	@XmlAttribute(name = "behaviour")
	public String getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}
}
