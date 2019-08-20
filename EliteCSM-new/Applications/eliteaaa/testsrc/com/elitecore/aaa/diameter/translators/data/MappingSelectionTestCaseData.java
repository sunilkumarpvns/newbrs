package com.elitecore.aaa.diameter.translators.data;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.core.diameter.packet.jaxb.DiameterPacketData;
import com.elitecore.diameterapi.core.translator.policy.impl.TranslationMappingConfigurationData;
import com.elitecore.diameterapi.diameter.common.packet.assertion.CustomAssertion;

@XmlRootElement(name = "case")
public class MappingSelectionTestCaseData {

	private TranslationMappingConfigurationData translationMappingConfigurationData;
	private TranslationMappingConfigurationData baseTranslationMappingConfigurationData;
	private DiameterPacketData fromDiameterRequest;
	private String expectedMapping;
	private String behaviour;
	private Map<String,String> webServiceDataMap;
	private CustomAssertion assertion;
	
	public MappingSelectionTestCaseData() {
		webServiceDataMap = new LinkedHashMap<String,String>();
	}
	
	@XmlElement(name = "trasnaltion-mapping")
	public TranslationMappingConfigurationData getTranslationMappingConfigurationData() {
		return translationMappingConfigurationData;	
	}

	@XmlElement(name = "base-translation-mapping")
	public TranslationMappingConfigurationData getBaseTranslationMappingConfigurationData() {
		return baseTranslationMappingConfigurationData;
	}

	public void setBaseTranslationMappingConfigurationData(
			TranslationMappingConfigurationData baseTranslationMappingConfigurationData) {
		this.baseTranslationMappingConfigurationData = baseTranslationMappingConfigurationData;
	}

	@XmlElement(name = "diameter-packet")
	public DiameterPacketData getFromDiameterRequest() {
		return fromDiameterRequest;
	}

	@XmlAttribute(name = "expectedMapping")
	public String getExpectedMapping() {
		return expectedMapping;
	}

	public void setTranslationMappingConfigurationData(
			TranslationMappingConfigurationData translationMappingConfigurationData) {
		this.translationMappingConfigurationData = translationMappingConfigurationData;
	}

	public void setFromDiameterRequest(DiameterPacketData fromDiameterRequest) {
		this.fromDiameterRequest = fromDiameterRequest;
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

	@XmlElement(name = "web-service-request-map")
	@XmlJavaTypeAdapter(value = MapAdapter.class)
	public Map<String, String> getWebServiceRequestMap() {
		return webServiceDataMap;
	}

	public void setWebServiceRequestMap(Map<String, String> webServiceDataMap) {
		this.webServiceDataMap = webServiceDataMap;
	}
	
	@XmlElement(name = "assert")
	public CustomAssertion getAssertion() {
		return assertion;
	}

	public void setAssertion(CustomAssertion assertion) {
		this.assertion = assertion;
	}
}
