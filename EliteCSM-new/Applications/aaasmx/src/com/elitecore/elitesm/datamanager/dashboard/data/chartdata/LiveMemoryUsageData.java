package com.elitecore.elitesm.datamanager.dashboard.data.chartdata;

public class LiveMemoryUsageData {
	private String[] serverName;
	private Long[] heapUsed;
	private Long[] heapUsedMinVal;
	private Long[] heapUsedMaxVal;
	private Long[] nonHeapUsed;
	private Long[] nonHeapMinVal;
	private Long[] nonHeapMaxVal;
	private String[] strCreateTime;
	
	public String[] getServerName() {
		return serverName;
	}
	public void setServerName(String[] serverName) {
		this.serverName = serverName;
	}
	public Long[] getHeapUsed() {
		return heapUsed;
	}
	public void setHeapUsed(Long[] heapUsed) {
		this.heapUsed = heapUsed;
	}
	public Long[] getHeapUsedMinVal() {
		return heapUsedMinVal;
	}
	public void setHeapUsedMinVal(Long[] heapUsedMinVal) {
		this.heapUsedMinVal = heapUsedMinVal;
	}
	public Long[] getHeapUsedMaxVal() {
		return heapUsedMaxVal;
	}
	public void setHeapUsedMaxVal(Long[] heapUsedMaxVal) {
		this.heapUsedMaxVal = heapUsedMaxVal;
	}
	public Long[] getNonHeapUsed() {
		return nonHeapUsed;
	}
	public void setNonHeapUsed(Long[] nonHeapUsed) {
		this.nonHeapUsed = nonHeapUsed;
	}
	public Long[] getNonHeapMinVal() {
		return nonHeapMinVal;
	}
	public void setNonHeapMinVal(Long[] nonHeapMinVal) {
		this.nonHeapMinVal = nonHeapMinVal;
	}
	public Long[] getNonHeapMaxVal() {
		return nonHeapMaxVal;
	}
	public void setNonHeapMaxVal(Long[] nonHeapMaxVal) {
		this.nonHeapMaxVal = nonHeapMaxVal;
	}
	public String[] getStrCreateTime() {
		return strCreateTime;
	}
	public void setStrCreateTime(String[] strCreateTime) {
		this.strCreateTime = strCreateTime;
	}
}
