package com.elitecore.corenetvertex.constants;

public enum QuotaProfileType {

	RnC_BASED("RnC Based"),
	USAGE_METERING_BASED("Usage Metering Based"),
	SY_COUNTER_BASED("Sy Counter Based");
	
	private String val;
	
	QuotaProfileType(String val){
		this.val = val;
	}
	
	public static QuotaProfileType fromValue(String val){
		
		if(USAGE_METERING_BASED.val.equals(val)) {
			return USAGE_METERING_BASED;
		} else if (SY_COUNTER_BASED.val.equals(val)) {
			return SY_COUNTER_BASED;
		} else if (RnC_BASED.val.equals(val)) {
			return RnC_BASED;
		}
		return null;
	}
	
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	
}
