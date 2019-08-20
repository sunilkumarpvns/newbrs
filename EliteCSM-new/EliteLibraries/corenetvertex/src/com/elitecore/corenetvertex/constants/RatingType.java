package com.elitecore.corenetvertex.constants;

/**
 * This enum will define rating type for file location. It can be Partner or
 * Subscriber
 * 
 * @author Ajay Pandey
 *
 */

public enum RatingType {

	PARTNER("Partner"), SUBSCRIBER("Subscriber");

	private  String value;
	
	private RatingType(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}
	
	public static RatingType fromVal(String value) {
		for (RatingType status : values()) {
			if (status.value.equalsIgnoreCase(value)) {
				return status;
			}
		}
		return null;
	}
}
