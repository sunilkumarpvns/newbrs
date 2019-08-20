package com.elitecore.corenetvertex.constants;

public enum PacketMappingConstants {

	STATIC("STATIC"),
	DYNAMIC("DYNAMIC"),
	NONE("none");

	public final String val;
	
	private PacketMappingConstants(String val) {
		this.val = val;
	}

	public String getVal(){
		return val;
	}
	
	public static PacketMappingConstants fromValue(String val){
		
		if(STATIC.val.equalsIgnoreCase(val)){
			return STATIC;
		} else if(DYNAMIC.val.equalsIgnoreCase(val)){
			return DYNAMIC;
		} else {
			return NONE;
		}
		
		
	}
}
