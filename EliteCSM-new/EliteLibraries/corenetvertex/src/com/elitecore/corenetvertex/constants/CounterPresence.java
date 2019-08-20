package com.elitecore.corenetvertex.constants;

public enum CounterPresence {
	OPTIONAL_HSQ ("HSQ Optional",2) {

		@Override
		public boolean isApplicableOnUsageUnavailability(int fupLevel) {
			return fupLevel == 0;
		}},
	OPTIONAL_FUP1 ("FUP Level 1 Optional",0) {

		@Override
		public boolean isApplicableOnUsageUnavailability(int fupLevel) {
			return fupLevel == 1;
		}},
	OPTIONAL_FUP2 ("FUP Level 2 Optional",3) {

		@Override
		public boolean isApplicableOnUsageUnavailability(int fupLevel) {
			return fupLevel == 2;
		}},
	MANDATORY ("Mandatory",1) {

		@Override
		public boolean isApplicableOnUsageUnavailability(int fupLevel) {
			return false;
		}};
	
	private String displayVal;
	private int val;
	
	private CounterPresence(String displayVal,int val) {
		this.displayVal = displayVal;
		this.val = val;
	}
	
	public static CounterPresence fromDisplayValue(String displayVal){
		if (OPTIONAL_HSQ.displayVal.equals(displayVal)) {
			return OPTIONAL_HSQ;
		} else if (OPTIONAL_FUP1.displayVal.equals(displayVal)) {
			return OPTIONAL_FUP1;
		} else if (OPTIONAL_FUP2.displayVal.equals(displayVal)) {
			return OPTIONAL_FUP2;
		} else{
			return MANDATORY;
		}
	}
	
	public static CounterPresence fromValue(int val) {
		if (OPTIONAL_HSQ.val == val) {
			return OPTIONAL_HSQ;
		} else if (OPTIONAL_FUP1.val == val) {
			return OPTIONAL_FUP1;
		} else if (OPTIONAL_FUP2.val == val) {
			return OPTIONAL_FUP2;
		} else {
			return MANDATORY;
		}
	}
	
	public String getDisplayVal() {
		return displayVal;
	}
	public void setDisplayVal(String displayVal) {
		this.displayVal = displayVal;
	}
	public int getVal() {
		return val;
	}
	public void setVal(int val) {
		this.val = val;
	}
	
	public abstract boolean isApplicableOnUsageUnavailability(int fupLevel);

	public static boolean isApplicableOnUsageUnavailability(int fupLevel,CounterPresence usageUnavailableAction) {
		if (usageUnavailableAction == null) {
			return false;
		}
		return usageUnavailableAction.isApplicableOnUsageUnavailability(fupLevel);
	}
	
}
