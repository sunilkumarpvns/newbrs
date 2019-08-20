package com.elitecore.corenetvertex.constants;

public enum LogMonitorTypes {
	
	PCRF("PCRF","pcrfservice"),
	RADIUS("RADIUS","radius"),
	DIAMETER("DIAMETER","diameter");
	
	private final String displayName;
	private final String value;
	
	private LogMonitorTypes(String displayName, String value){
		this.displayName = displayName;
		this.value = value;				
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getValue() {
		return value;
	}
}
