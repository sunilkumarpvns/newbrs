package com.elitecore.corenetvertex.constants;

public enum RollingType {
	
	TIME_BASED("TIME",1,"Time Based"),
	SIZE_BASED("SIZE",2,"Size Based");
	
	public final String type;
	public final int value;
	public final String label;

	private RollingType(String type , int value, String label){
		this.type = type;
		this.value = value;
		this.label = label;
	}
	
	public static RollingType fromValue(String type) {
		if(type == null || type.trim().isEmpty() == true){
			return null;
		}
		type = type.trim();
		
		if (type.equalsIgnoreCase(SIZE_BASED.type) || type.equals(String.valueOf(SIZE_BASED.value))) {
			return RollingType.SIZE_BASED;
		}else if(type.equalsIgnoreCase(TIME_BASED.type) || type.equals(String.valueOf(TIME_BASED.value))){
			return RollingType.TIME_BASED;
		}else{
			return null;
		}
	}

	public static RollingType fromValue(int val) {
		
		if (val == TIME_BASED.value) {
			return TIME_BASED;
		} else if (val == SIZE_BASED.value) {
			return SIZE_BASED;
		} else {
			return null;
		}
		
		
	}

	public static RollingType fromValue(int val, RollingType defaultType) {

		if (val == TIME_BASED.value) {
			return TIME_BASED;
		} else if (val == SIZE_BASED.value) {
			return SIZE_BASED;
		} else {
			return defaultType;
		}


	}
}
