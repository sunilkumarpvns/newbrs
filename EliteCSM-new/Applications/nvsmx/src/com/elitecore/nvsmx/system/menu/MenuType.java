package com.elitecore.nvsmx.system.menu;

public enum MenuType {
	CONTAINER("yes"),
	NONCONTAINER("no"),
	UNKNOWN("unknown");
	
	public String type;
	
	private MenuType(String type){
		this.type = type;
	}
	
	public static MenuType fromString(String type){
		if(type.equalsIgnoreCase(CONTAINER.type)){
			return CONTAINER;
		}else if(type.equalsIgnoreCase(NONCONTAINER.type)){
			return NONCONTAINER;
		}else{
			return UNKNOWN;
		}
	}
}
