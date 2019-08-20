package com.elitecore.commons.kpi.util.constants;

public enum CounterTypeConstants {
	
	LONG("long"),
	INTEGER("integer"),
	STRING("string"),
	TIMESTAMP("timestamp")
	;
	
	private String value;
	
	private CounterTypeConstants(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
}
