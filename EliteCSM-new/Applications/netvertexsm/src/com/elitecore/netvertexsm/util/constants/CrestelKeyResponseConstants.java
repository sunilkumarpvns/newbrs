package com.elitecore.netvertexsm.util.constants;

public enum CrestelKeyResponseConstants {
		
	BALANCE("BALANCE"),
	CREDIT_STATUS("CREDIT-STATUS"),
	RATE_IN_MINOR_UNITS("RATE-IN-MINOR-UNITS"),
	CURRENCY_CODE("CURRENCY-CODE"),
	EXPONENT("EXPONENT"),
	COST_UNIT("COST-UNIT"),
	MAX_SESSION_TIME("MAX-SESSION-TIME"),
	MAX_SESSION_VOLUME("MAX-SESSION-VOLUME"),
	COST("COST"),
	RATE("RATE"),
	DESTINATION_NAME("DESTINATION-NAME"),
	RESERVATION_AMOUNT("RESERVATION-AMOUNT"),
	RESERVATIONID("RESERVATIONID");
	
	public final String val;
	
	private CrestelKeyResponseConstants(String val) {
		this.val = val;
	}
		
	public String getVal() {
		return val;
	}

}
