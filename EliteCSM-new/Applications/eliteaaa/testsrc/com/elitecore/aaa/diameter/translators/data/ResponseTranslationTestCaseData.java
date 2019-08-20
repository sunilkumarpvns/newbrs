package com.elitecore.aaa.diameter.translators.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.diameter.packet.jaxb.DiameterPacketData;
import com.elitecore.aaa.core.radius.packet.jaxb.RadiusPacketData;
import com.elitecore.diameterapi.core.translator.policy.impl.TranslationMappingConfigurationData;

@XmlRootElement(name = "case")
public class ResponseTranslationTestCaseData {

	private TranslationMappingConfigurationData translationMappingConfigurationData;
	private DiameterPacketData sourceDiameterRequest;
	private RadiusPacketData destRadPacket;
	
	private RadiusPacketData radResponsePacket;
	private DiameterPacketData expectedDiameterAnswer;
	private String selectedMapping;
	private String behaviour;
	
	@XmlElement(name = "trasnaltion-mapping")
	public TranslationMappingConfigurationData getTranslationMappingConfigurationData() {
		return translationMappingConfigurationData;	
	}

	public void setTranslationMappingConfigurationData(
			TranslationMappingConfigurationData translationMappingConfigurationData) {
		this.translationMappingConfigurationData = translationMappingConfigurationData;
	}

	@XmlElement(name = "source-dia-request")
	public DiameterPacketData getSourceDiameterRequest() {
		return sourceDiameterRequest;
	}

	public void setSourceDiameterRequest(DiameterPacketData sourceDiameterRequest) {
		this.sourceDiameterRequest = sourceDiameterRequest;
	}

	@XmlElement(name = "dest-rad-packet")
	public RadiusPacketData getDestRadPacket() {
		return destRadPacket;
	}

	public void setDestRadPacket(RadiusPacketData destRadPacket) {
		this.destRadPacket = destRadPacket;
	}

	@XmlElement(name = "radius-response")
	public RadiusPacketData getRadResponsePacket() {
		return radResponsePacket;
	}

	public void setRadResponsePacket(RadiusPacketData radResponsePacket) {
		this.radResponsePacket = radResponsePacket;
	}

	@XmlElement(name = "expected-diameter-answer")
	public DiameterPacketData getExpectedDiameterAnswer() {
		return expectedDiameterAnswer;
	}

	public void setExpectedDiameterAnswer(DiameterPacketData expectedDiameterAnswer) {
		this.expectedDiameterAnswer = expectedDiameterAnswer;
	}
	
	@XmlAttribute (name = "selectedmapping")
	public String getSelectedMapping() {
		return selectedMapping;
	}

	public void setSelectedMapping(String selectedMapping) {
		this.selectedMapping = selectedMapping;
	}

	@XmlAttribute(name = "behaviour")
	public String getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}
}
