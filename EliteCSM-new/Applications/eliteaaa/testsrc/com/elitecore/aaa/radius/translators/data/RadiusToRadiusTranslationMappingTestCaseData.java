package com.elitecore.aaa.radius.translators.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.radius.packet.jaxb.RadiusPacketData;
import com.elitecore.diameterapi.core.translator.policy.impl.TranslationMappingConfigurationData;

@XmlRootElement(name = "test-case")
public class RadiusToRadiusTranslationMappingTestCaseData {

	private TranslationMappingConfigurationData translationMappingConfigurationData;
	private String expectedMapping;
	private String behaviour;
	private RadiusPacketData radiusPacketData;
	
	@XmlElement(name = "translation-mapping")
	public TranslationMappingConfigurationData getTranslationMappingConfigurationData() {
		return translationMappingConfigurationData;	
	}

	public void setTranslationMappingConfigurationData(
			TranslationMappingConfigurationData translationMappingConfigurationData) {
		this.translationMappingConfigurationData = translationMappingConfigurationData;
	}
	
	@XmlAttribute(name = "expected-mapping")
	public String getExpectedMapping() {
		return expectedMapping;
	}

	public void setExpectedMapping(String expectedMapping) {
		this.expectedMapping = expectedMapping;
	}

	@XmlAttribute(name = "behaviour")
	public String getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}
	
	@XmlElement(name = "radius-packet")
	public RadiusPacketData getRadiusPacketData() {
		return radiusPacketData;
	}
	
	public void setRadiusPacketData(RadiusPacketData radiusPacketData) {
		this.radiusPacketData = radiusPacketData;
	}
}
