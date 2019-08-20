package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class NASDetails {
	private Long nasDetailId;
	private String ruleset;
	private Long serverId;
	private Long translationMappingId;
	private String script;
	private String acceptOnTimeout;
	
	public Long getNasDetailId() {
		return nasDetailId;
	}
	public void setNasDetailId(Long nasDetailId) {
		this.nasDetailId = nasDetailId;
	}
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	public Long getServerId() {
		return serverId;
	}
	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}
	public Long getTranslationMappingId() {
		return translationMappingId;
	}
	public void setTranslationMappingId(Long translationMappingId) {
		this.translationMappingId = translationMappingId;
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
}
