package com.elitecore.corenetvertex.constants;

public enum TierRateType {

	FLAT("FLAT"),
	INCREMENTAL("INCREMENTAL");

	private String val;
	TierRateType(String val){
		this.val = val;
	}
	
	public static TierRateType fromVal(String value) {
		for (TierRateType tierRateType : values()) {
			if (tierRateType.name().equalsIgnoreCase(value)) {
				return tierRateType;
			}
		}
		return null;
	}
}
