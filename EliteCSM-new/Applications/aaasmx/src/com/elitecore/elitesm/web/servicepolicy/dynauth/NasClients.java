package com.elitecore.elitesm.web.servicepolicy.dynauth;

import java.util.List;

public class NasClients{
	
	private static final long serialVersionUID = 1L;
	private String ruleset;
	private List<ESIData> esiListData;
	private String translationMapConfigId;
	private String script;
	private Long orderNumber;
	
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	public List<ESIData> getEsiListData() {
		return esiListData;
	}
	public void setEsiListData(List<ESIData> esiListData) {
		this.esiListData = esiListData;
	}
	public String getTranslationMapConfigId() {
		return translationMapConfigId;
	}
	public void setTranslationMapConfigId(String translationMapConfigId) {
		this.translationMapConfigId = translationMapConfigId;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	@Override
	public String toString() {
		return "NasClients [ruleset=" + ruleset + ", esiListData="
				+ esiListData + ", translationMapConfigId="
				+ translationMapConfigId + ",script=" + script + "]";
	}
	public Long getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
}

