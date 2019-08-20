package com.elitecore.corenetvertex.constants;


public enum TimeBasedRollingUnit {
	
	MINUTE("MINUTE",3),
	HOUR("HOUR",4),
	DAILY("DAILY",5);
	
	public final String unit;
	public final int value;
	
	
	private TimeBasedRollingUnit(String unit ,int value){
		this.unit = unit;
		this.value = value;
	}
	public static TimeBasedRollingUnit fromValue(String value){
		
		if(value == null || value.trim().isEmpty() == true){
			return null;
		}
		value = value.trim();
		
		if(value.equalsIgnoreCase(DAILY.unit) || value.equals(String.valueOf(DAILY.value))){
			return TimeBasedRollingUnit.DAILY;
		}else if(value.equalsIgnoreCase(HOUR.unit) || value.equals(String.valueOf(HOUR.value))){
			return TimeBasedRollingUnit.HOUR;
		}else if(value.equalsIgnoreCase(MINUTE.unit) || value.equals(String.valueOf(MINUTE.value))){
			return TimeBasedRollingUnit.MINUTE;
		}else{
			return null;
		}
	}

}
