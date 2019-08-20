package com.elitecore.corenetvertex.sm.driver.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * TimeUnits for Driver Management
 * @author dhyani.raval
 */
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
	
	private String key;
	private final String val;
	private static Map<String,TimeUnits> timeUnitMap = new HashMap();

	static {

		for(TimeUnits timeUnits : values()) {
			timeUnitMap.put(timeUnits.key,timeUnits);
		}
	}
	
	TimeUnits(String key, String val){
		this.key = key;
		this.val = val;
		
	}

	public String getKey() {
		return key;
	}

	public String getVal() {
		return val;
	}

	public static TimeUnits fromKey(String key) {
		return timeUnitMap.get(key);
	}
}
