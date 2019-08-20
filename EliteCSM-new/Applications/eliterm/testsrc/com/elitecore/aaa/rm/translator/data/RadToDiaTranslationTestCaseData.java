package com.elitecore.aaa.rm.translator.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.diameterapi.diameter.common.packet.assertion.CustomAssertion;


@XmlRootElement(name= "case")
public class RadToDiaTranslationTestCaseData {

	private TranslationMappingConfigurationData translationMappingConfigurationData;
	private RadiusPacketData fromRadiusPacket;
	private String behaviour;
	
	private DiameterPacketData destDiameterRequest;
	private DiameterPacketData diameterAnswer;

	@XmlElement(name="from-diameter-answer")
	public DiameterPacketData getDiameterAnswer() {
		return diameterAnswer;
	}

	public void setDiameterAnswer(DiameterPacketData diameterResponse) {
		this.diameterAnswer = diameterResponse;
	}

	private CustomAssertion customAssertion;
	
	@XmlElement(name = "translation-mapping")
	public TranslationMappingConfigurationData getTranslationMappingConfigurationData() {
		return translationMappingConfigurationData;	
	}

	@XmlElement(name = "diameter-request")
	public DiameterPacketData getDestDiameterRequest() {
		return destDiameterRequest;
	}

	@XmlElement(name = "radius-packet")
	public RadiusPacketData getFromRadiusPacket() {
		return fromRadiusPacket;
	}

	public void setTranslationMappingConfigurationData(
			TranslationMappingConfigurationData translationMappingConfigurationData) {
		this.translationMappingConfigurationData = translationMappingConfigurationData;
	}

	public void setDestDiameterRequest(DiameterPacketData destDiametertRequest) {
		this.destDiameterRequest = destDiametertRequest;
	}

	public void setFromRadiusPacket(RadiusPacketData fromRadiusPacket) {
		this.fromRadiusPacket = fromRadiusPacket;
	}

	@XmlAttribute(name = "behaviour")
	public String getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}
	
	@XmlElement(name = "assert")
	public CustomAssertion getCustomAssertion() {
		return customAssertion;
	}
	
	public void setCustomAssertion(CustomAssertion customAssertion) {
		this.customAssertion = customAssertion;
	}
}
