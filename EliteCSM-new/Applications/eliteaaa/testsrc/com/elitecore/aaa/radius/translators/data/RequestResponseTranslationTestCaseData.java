package com.elitecore.aaa.radius.translators.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.radius.packet.jaxb.RadiusPacketData;
import com.elitecore.diameterapi.core.translator.policy.impl.TranslationMappingConfigurationData;

@XmlRootElement(name = "test-case")
public class RequestResponseTranslationTestCaseData {

	private String behaviour;
	private TranslationMappingConfigurationData translationMappingConfigurationData;
	private RadiusPacketData fromRadiusRequest;
	private RadiusPacketData toRadiusRequest;
	
	private RadiusPacketData fromRadiusResponse;
	private RadiusPacketData toRadiusRadiusResponse;
	
	private String selectedMapping;
	
	public RequestResponseTranslationTestCaseData() {
	}
	
	@XmlAttribute(name = "behaviour")
	public String getBehaviour() {
		return behaviour;
	}
	
	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}
	
	@XmlElement(name = "translation-mapping")
	public TranslationMappingConfigurationData getTranslationMappingConfigurationData() {
		return translationMappingConfigurationData;	
	}

	public void setTranslationMappingConfigurationData(
			TranslationMappingConfigurationData translationMappingConfigurationData) {
		this.translationMappingConfigurationData = translationMappingConfigurationData;
	}

	@XmlElement(name = "from-radius-request")
	public RadiusPacketData getFromRadiusRequest() {
		return fromRadiusRequest;
	}

	public void setFromRadiusRequest(RadiusPacketData fromRadiusRequest) {
		this.fromRadiusRequest = fromRadiusRequest;
	}
	
	@XmlElement(name = "to-radius-request")
	public RadiusPacketData getToRadiusRequest() {
		return toRadiusRequest;
	}
	
	public void setToRadiusRequest(RadiusPacketData toRadiusRequest) {
		this.toRadiusRequest = toRadiusRequest;
	}
	
	@XmlElement(name = "from-radius-response")
	public RadiusPacketData getFromRadiusResponse() {
		return fromRadiusResponse;
	}
	
	public void setFromRadiusResponse(RadiusPacketData fromRadiusResponse) {
		this.fromRadiusResponse = fromRadiusResponse;
	}
	
	@XmlElement(name = "to-radius-response")
	public RadiusPacketData getToRadiusRadiusResponse() {
		return toRadiusRadiusResponse;
	}
	
	public void setToRadiusRadiusResponse(
			RadiusPacketData toRadiusRadiusResponse) {
		this.toRadiusRadiusResponse = toRadiusRadiusResponse;
	}
	
	@XmlElement(name = "selected-mapping")
	public String getSelectedMapping() {
		return selectedMapping;
	}
	
	public void setSelectedMapping(String selectedMapping) {
		this.selectedMapping = selectedMapping;
	}
}