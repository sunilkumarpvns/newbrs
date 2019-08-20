package com.elitecore.corenetvertex.constants;

public enum RateCardType {
	MONETARY("Monetary"),
	NON_MONETARY("Non Monetary");

	private String value;

	private RateCardType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static RateCardType fromVal(String value) {
		for (RateCardType rateCardType : values()) {
			if (rateCardType.value.equalsIgnoreCase(value)) {
				return rateCardType;
			}
		}
		return null;
	}
}
