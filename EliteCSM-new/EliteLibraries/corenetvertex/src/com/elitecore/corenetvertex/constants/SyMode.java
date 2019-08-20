package com.elitecore.corenetvertex.constants;

public enum SyMode {
	
	PULL,
	PUSH;

	
	public static SyMode fromValue(String value){
		if(PULL.name().equalsIgnoreCase(value)){
			return PULL;
		}else if(PUSH.name().equalsIgnoreCase(value)){
			return PUSH;
		} else {
			return null;
		}
	}

}
