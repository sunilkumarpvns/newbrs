package com.elitecore.corenetvertex.constants;


public enum CustomerType {

	PREPAID("Prepaid"), 
	POSTPAID("Postpaid");

	public String val;
	
	CustomerType(String val) {
		this.val = val;
	}
	
	public static CustomerType fromVal(String val) {

		if (PREPAID.val.equalsIgnoreCase(val)) {
			return PREPAID;
		} else if (POSTPAID.val.equalsIgnoreCase(val)) {
			return POSTPAID;
		} else {
			return null;
		}
	}
}
