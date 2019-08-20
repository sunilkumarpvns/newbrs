package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.List;

public class ProxyCommunicationData {
	private String ruleset;
	private List<ESIServerData> esiListData;
	private String translationMappingName;
	private String script;
	private String acceptOnTimeout;
	
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	public List<ESIServerData> getEsiListData() {
		return esiListData;
	}
	public void setEsiListData(List<ESIServerData> esiListData) {
		this.esiListData = esiListData;
	}

	public String getTranslationMappingName() {
		return translationMappingName;
	}
	public void setTranslationMappingName(String translationMappingName) {
		this.translationMappingName = translationMappingName;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getAcceptOnTimeout() {
		return acceptOnTimeout;
	}
	public void setAcceptOnTimeout(String acceptOnTimeout) {
		this.acceptOnTimeout = acceptOnTimeout;
	}
	
	@Override
	public String toString() {
		return "----------------ProxyCommunicationData------------------------\n  ruleset = "
				+ ruleset
				+ "\n  esiListData = "
				+ esiListData
				+ "\n  translationMappingName = "
				+ translationMappingName
				+ "\n  script = "
				+ script
				+ "\n  acceptOnTimeout = "
				+ acceptOnTimeout
				+ "\n----------------ProxyCommunicationData-------------------------\n";
	}
}