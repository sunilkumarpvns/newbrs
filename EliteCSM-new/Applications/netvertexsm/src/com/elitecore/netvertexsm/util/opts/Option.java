package com.elitecore.netvertexsm.util.opts;

public class Option {
	private String displayName;
	private String value;
	
	public Option(String displayName,String value){
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
