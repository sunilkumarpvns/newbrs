package com.elitecore.corenetvertex.constants;

import java.util.HashMap;
import java.util.Map;

public enum PriorityLevel {

	PRIORITY_LEVEL_1("1",1),
	PRIORITY_LEVEL_2("2",2),
	PRIORITY_LEVEL_3("3",3),
	PRIORITY_LEVEL_4("4",4),
	PRIORITY_LEVEL_5("5",5),
	PRIORITY_LEVEL_6("6",6),
	PRIORITY_LEVEL_7("7",7),
	PRIORITY_LEVEL_8("8",8),
	PRIORITY_LEVEL_9("9",9),
	PRIORITY_LEVEL_10("10",10),
	PRIORITY_LEVEL_11("11",11),
	PRIORITY_LEVEL_12("12",12),
	PRIORITY_LEVEL_13("13",13),
	PRIORITY_LEVEL_14("14",14),
	PRIORITY_LEVEL_15("15",15);
	
	public final String displayVal;
	public final int val;
	public final String stringVal;
	
	private static Map<String,PriorityLevel> stringValToPriority;
	
	PriorityLevel(String displayVal, int val) {
		this.displayVal = displayVal;
		this.val = val;
		this.stringVal = String.valueOf(val);
	}
	
	static {
		stringValToPriority = new HashMap<>(15, 1);
		for(PriorityLevel arp : values()) {
			stringValToPriority.put(arp.stringVal, arp);
		}
	}
	
	public static PriorityLevel fromVal(int val) {
		
		switch (val) {
			case 1:

				return PRIORITY_LEVEL_1;

			case 2:
				return PRIORITY_LEVEL_2;

			case 3:
				return PRIORITY_LEVEL_3;
				
			case 4:
				return PRIORITY_LEVEL_4;
				
			case 5:
				return PRIORITY_LEVEL_5;
				
			case 6:
				return PRIORITY_LEVEL_6;
				
			case 7:
				return PRIORITY_LEVEL_7;
				
			case 8:
				return PRIORITY_LEVEL_8;
				
			case 9:
				return PRIORITY_LEVEL_9;
				
			case 10:
				return PRIORITY_LEVEL_10;
				
			case 11:
				return PRIORITY_LEVEL_11;
				
			case 12:
				return PRIORITY_LEVEL_12;
				
			case 13:
				return PRIORITY_LEVEL_13;
				
			case 14:
				return PRIORITY_LEVEL_14;
				
			case 15:
				return PRIORITY_LEVEL_15;

		}
		
		return null;
	}
	
	public static PriorityLevel fromVal(String val) {
		return stringValToPriority.get(val);
	}
	
	public boolean isHigher(PriorityLevel that) {
		/*
		 * lower is better
		 */
		return this.val < that.val;
	}
	
	
	
}
