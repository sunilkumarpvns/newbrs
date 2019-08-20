package com.elitecore.diameterapi.core.common.transport.priority;

import javax.xml.bind.annotation.XmlEnumValue;



public enum Priority {
	@XmlEnumValue(value = "High")
	HIGH(3,"High"),
	@XmlEnumValue(value = "Medium")
	MEDIUM(2,"Medium"),
	@XmlEnumValue(value = "Low")
	LOW(1,"Low")
	;
	
	public final int val;
	public final String priority;
	
	Priority(int val, String priority){
		this.val = val;
		this.priority = priority;
	}
	
	public static Priority fromPriority(int val) {
		if(HIGH.val == val){
			return HIGH;
		}else if(MEDIUM.val == val){
			return MEDIUM;
		}else if (LOW.val == val){
			return LOW;
		}else{
			return null;
		}
	}
	
	public static Priority fromPriority(String priority) {
		if(HIGH.priority.equalsIgnoreCase(priority)) {
			return HIGH;
		}else if(MEDIUM.priority.equalsIgnoreCase(priority)) {
			return MEDIUM;
		}else if(LOW.priority.equalsIgnoreCase(priority)){
			return LOW;
		}else{
			return null;
		}
	}
}
