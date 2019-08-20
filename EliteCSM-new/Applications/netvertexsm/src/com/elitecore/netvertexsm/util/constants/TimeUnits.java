package com.elitecore.netvertexsm.util.constants;

public enum TimeUnits {
	MIN_1("1","1 Min"),
	MIN_2("2","2 Min"),
	MIN_3("3","3 Min"),
	MIN_5("5","5 Min"),
	MIN_10("10","10 Min"),
	MIN_15("15","15 Min"),
	MIN_20("20","20 Min"),
	MIN_30("30","30 Min"),
	HOURLY("60","Hourly"),
	DAILY("1440","Daily");
	
	public String key;
	public final String val;
	
	private TimeUnits(String key, String val){
		this.key = key;
		this.val = val;
		
	}
}
