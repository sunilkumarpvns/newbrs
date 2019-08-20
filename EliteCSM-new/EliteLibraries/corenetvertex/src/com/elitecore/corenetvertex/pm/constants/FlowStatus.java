package com.elitecore.corenetvertex.pm.constants;

import java.util.HashMap;
import java.util.Map;


public enum FlowStatus {
	ENABLED_UPLINK(0,"Enabled Uplink"),
	ENABLED_DOWNLINK(1,"Enabled Downlink"),
	ENABLED(2,"Enabled"),
	DISABLED(3,"Disabled"),
	REMOVED(4, "Removed");

	public final int val;
	public final String displayVal;
	
	private static Map<String, FlowStatus> displayValueToFlowStatus;

	private FlowStatus(int val,String displayVal) {
		this.val = val;
		this.displayVal = displayVal;
	}
	
	static {
		displayValueToFlowStatus = new HashMap<String, FlowStatus>();
		
		for(FlowStatus flowStatus : values()) {
			displayValueToFlowStatus.put(flowStatus.displayVal, flowStatus);
		}
	}

	public int getVal() {
		return val;
	}
	
	public static FlowStatus fromValue(long val) {
		if (ENABLED.val == val) {
			return ENABLED;
		} else if (ENABLED_DOWNLINK.val == val) {
			return ENABLED_DOWNLINK;
		} else if (ENABLED_UPLINK.val == val) {
			return ENABLED_UPLINK;
		} else if (DISABLED.val == val) {
			return DISABLED;
		} else if (REMOVED.val == val) {
			return REMOVED;
		}
		return null;
	}

	public static FlowStatus fromValue(String val) {
		return displayValueToFlowStatus.get(val);
	}

}
