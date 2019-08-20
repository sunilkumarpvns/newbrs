package com.elitecore.elitesm.web.dashboard.memoryusage;

public class HeapMemoryUsageData {

	private String name;
	private Long used;
	private Long max;
	private Long usage;
	private Long peakused;
	private Long peakmax;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getUsed() {
		return used;
	}
	public void setUsed(Long used) {
		this.used = used;
	}
	public Long getMax() {
		return max;
	}
	public void setMax(Long max) {
		this.max = max;
	}
	public Long getUsage() {
		return usage;
	}
	public void setUsage(Long usage) {
		this.usage = usage;
	}
	public Long getPeakused() {
		return peakused;
	}
	public void setPeakused(Long peakused) {
		this.peakused = peakused;
	}
	public Long getPeakmax() {
		return peakmax;
	}
	public void setPeakmax(Long peakmax) {
		this.peakmax = peakmax;
	}
	
	
}
