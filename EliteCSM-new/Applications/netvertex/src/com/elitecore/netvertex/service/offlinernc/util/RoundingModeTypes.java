package com.elitecore.netvertex.service.offlinernc.util;

import javax.annotation.Nullable;

public enum RoundingModeTypes {

	TRUNCATE("TRUNCATE"),
	UPPER("UPPER");
	
	private String roundingType;

	private RoundingModeTypes (String roundingType) {
		this.roundingType = roundingType;
	}
	
	public String getType() {
		return roundingType;
	}
	
	public static @Nullable RoundingModeTypes getByName(String name){
		for (RoundingModeTypes mode : RoundingModeTypes.values()) {
			if (mode.getType().equals(name)) {
				return mode;
			}
		}
		
		return null;
	}
}