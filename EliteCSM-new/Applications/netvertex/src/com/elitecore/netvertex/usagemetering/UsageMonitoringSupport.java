package com.elitecore.netvertex.usagemetering;

import java.util.HashMap;
import java.util.Map;

public enum UsageMonitoringSupport {
	
	USAGE_MONITORING_DISABLED(0);
	
	private static Map<Integer, UsageMonitoringSupport> monitoringSupportMap;

	static{
		monitoringSupportMap = new HashMap<Integer, UsageMonitoringSupport>();
		for (UsageMonitoringSupport monitoringSupport : values()) {
			monitoringSupportMap.put(monitoringSupport.val, monitoringSupport);
		}
	}

	public final int val;
	
	UsageMonitoringSupport(int val){
		this.val = val;
	}
	
	public int getVal(){
		return val;
	}

	public static UsageMonitoringSupport getMonitoringSupportMap(int support) {
		return monitoringSupportMap.get(support);
	}

}
