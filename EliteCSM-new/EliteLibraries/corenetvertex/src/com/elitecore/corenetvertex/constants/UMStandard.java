package com.elitecore.corenetvertex.constants;

import static com.elitecore.commons.logging.LogManager.getLogger;


public enum UMStandard {
	MSCC(0,"MSCC"),
	TGPPR9(1, "3GPP-R9"),
	TGPPR9_SESSION_ONLY(2, "3GPP-R9-SESSION-ONLY"),
	HUAWEI(3, "HUAWEI"),;
	
	private static final String MODULE = "UM-STD";
	public final int value;
	public final String displayValue;
	private UMStandard(int value, String displayValue) {
		this.value = value;
		this.displayValue = displayValue;
	}
	
	public static UMStandard fromValue(int value){
		if (MSCC.value == value)
			return MSCC;

		if (TGPPR9.value == value) {
			return TGPPR9;
		}

		if (HUAWEI.value == value) {
			return HUAWEI;
		}

		if (TGPPR9_SESSION_ONLY.value == value) {
			return TGPPR9_SESSION_ONLY;
		}
		
		return null;
	}
	
	public static UMStandard fromValue(String value){
		try {
			int val= Integer.parseInt(value.trim());
			return fromValue(val);		
		}catch(Exception e){ 
			getLogger().trace(MODULE, e);
		}
		return null;
	}
}
