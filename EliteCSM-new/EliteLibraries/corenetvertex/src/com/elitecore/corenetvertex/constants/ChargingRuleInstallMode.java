package com.elitecore.corenetvertex.constants;


public enum ChargingRuleInstallMode {
	
	GROUPED("Grouped"),
	SINGLE("Single");
	
	public final String val;
	
	private ChargingRuleInstallMode(String val){
		this.val = val;
	}
	
	public static ChargingRuleInstallMode fromValue(String val){
		if(val != null) {
			if (GROUPED.name().equals(val)) {
				return GROUPED;
			} else if (SINGLE.name().equals(val)) {
				return SINGLE;
			}
		}
		return GROUPED;
	}

}
