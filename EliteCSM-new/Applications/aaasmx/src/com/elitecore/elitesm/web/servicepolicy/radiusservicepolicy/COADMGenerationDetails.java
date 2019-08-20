package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class COADMGenerationDetails {

	private String ruleset;
	private String packetType;
	private String translationMapping;
	
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	public String getPacketType() {
		return packetType;
	}
	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}
	public String getTranslationMapping() {
		return translationMapping;
	}
	public void setTranslationMapping(String translationMapping) {
		this.translationMapping = translationMapping;
	}
}
