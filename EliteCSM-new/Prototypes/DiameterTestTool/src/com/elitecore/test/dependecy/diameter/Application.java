package com.elitecore.test.dependecy.diameter;

import java.util.HashMap;
import java.util.Map;

public enum Application {
	
	BASE("BASE"),
	NASREQ("NAS"),	
	MOBILE_IPV4("MOBILEIPv4"),
	BASEACCOUNTING("BASE-ACCT"),
	CC("CC"),	
	EAP("EAP"),
	SIP("SIP"),
	TGPP_CX_PX("CX-PX"),
	TGPP_SH_PH("SH-PH"),
	TGPP_RE("RE"),
	TGPP_WX("WX"),
	TGPP_ZN("ZN"),
	TGPP_ZH("ZH"),
	TGPP_GQ("GQ"),
	TGPP_GMB("GMB"),
	TGPP_GX_OVER_GY("GX-OVER-GY"),
	TGPP_MM10("MM10"),
	TGPP_PR("PR"),
	TGPP_RX_29_214_18("RX"),
	TGPP_GX_29_212_18("GX"),
	TGPP_STA("STA"),
	TGPP_S6A("S6A"),
	TGPP_S13_S13("S13/S13'"),
	TGPP_SLG("SLG"),
	TGPP_SWM("SWM"),
	TGPP_SWX("SWX"),
	TGPP_GXX("GXX"),
	TGPP_S9("S9"),
	TGPP_ZPN("ZPN"),
	TGPP_S6B("S6B"),
	TGPP_SLH("SLH"),
	TGPP_SGMB("SGMB"),
	TGPP_SY( "SY"), 
	UNKNOWN( "UNKNOWN"), 
	TGPP_GY("GY");
	
	private final String displayName;
	private static Map<String, Application> displayNameToApplication = new HashMap<String, Application>();
	
	Application( String displayName) {
		this.displayName = displayName;
	}
	
	static {
		
		for(Application application : values()) {
			displayNameToApplication.put(application.displayName.toUpperCase(), application);
		}
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public static Application fromDisplayName(String val) {
		return displayNameToApplication.get(val.toUpperCase());
	}
	
	
	
	
}
