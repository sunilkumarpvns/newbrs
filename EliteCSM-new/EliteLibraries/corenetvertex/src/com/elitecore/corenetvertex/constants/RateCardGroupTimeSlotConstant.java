package com.elitecore.corenetvertex.constants;

public enum RateCardGroupTimeSlotConstant {
	
	SPECIAL_DAY_RATE("SPECIAL_RATE"),
	PEAK_DAY_RATE("PEAK_RATE"),
	OFF_PEAK_DAY_RATE("OFF_PEAK_RATE"),
	WEEKEND_DAY_RATE("WEEKEND_RATE");
	
	private String value;
	
	private RateCardGroupTimeSlotConstant(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
}
