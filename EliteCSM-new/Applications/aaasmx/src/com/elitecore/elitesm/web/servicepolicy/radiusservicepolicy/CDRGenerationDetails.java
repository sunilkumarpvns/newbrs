package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class CDRGenerationDetails {

	private String ruleset;
	private String primaryDriverId;
	private String secondaryDriverId;
	private String script;
	private String waitForCDRDump;
	
	public String getRuleset() {
		return ruleset;
	}
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	public String getPrimaryDriverId() {
		return primaryDriverId;
	}
	public void setPrimaryDriverId(String primaryDriverId) {
		this.primaryDriverId = primaryDriverId;
	}
	public String getSecondaryDriverId() {
		return secondaryDriverId;
	}
	public void setSecondaryDriverId(String secondaryDriverId) {
		this.secondaryDriverId = secondaryDriverId;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getWaitForCDRDump() {
		return waitForCDRDump;
	}
	public void setWaitForCDRDump(String waitForCDRDump) {
		this.waitForCDRDump = waitForCDRDump;
	}
}
