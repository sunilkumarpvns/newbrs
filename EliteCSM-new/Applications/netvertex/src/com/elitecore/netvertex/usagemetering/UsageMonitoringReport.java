package com.elitecore.netvertex.usagemetering;

import java.util.HashMap;
import java.util.Map;

public enum UsageMonitoringReport {

	USAGE_MONITORING_REPORT_REQUIRED(0);

	private static Map<Integer, UsageMonitoringReport> monitoringReportMap;

	static{
		monitoringReportMap = new HashMap<Integer, UsageMonitoringReport>();
		for (UsageMonitoringReport monitoringReport : values()) {
			monitoringReportMap.put(monitoringReport.val, monitoringReport);
		}
	}
	
	public final int val;
	
	UsageMonitoringReport(int val){
		this.val = val;
	}
	
	public int getVal(){
		return val;
	}
	
	public static UsageMonitoringReport getMonitoringReportMap(int report) {
		return monitoringReportMap.get(report);
	}

}
