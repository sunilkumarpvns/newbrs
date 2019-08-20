package com.elitecore.corenetvertex.pkg.ims;

import java.util.HashMap;
import java.util.Map;

public enum PCCRuleAttributeAction {

	STANDARD("Standard"),
	OVERRIDE("Override"),
	SKIP("Skip"),
	MIN("Minimum"),
	MAX("Maximum"),
	ADD("Add");
	
	
	public final String val;
	private static Map<String,PCCRuleAttributeAction> displayValToPCCAttribute; 
	
	PCCRuleAttributeAction(String val) {
		this.val = val;
	}
	
	static {
		displayValToPCCAttribute = new HashMap<>();
		
		for(PCCRuleAttributeAction action : values()) {
			displayValToPCCAttribute.put(action.val, action);
		}
	}

	public static PCCRuleAttributeAction fromStringVal(String val) {
		return displayValToPCCAttribute.get(val);
	}

	public String getVal() {
		return val;
	}
	
}