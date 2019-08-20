package com.elitecore.elitesm.web.servermgr.server.graph;

public class PieChartData {

	private Long freeHeapMemory;
	private Long freeNonHeapMemory;
	private Long heapUsed;
	private Long nonHeapused;
	public Long getFreeHeapMemory() {
		return freeHeapMemory;
	}
	public void setFreeHeapMemory(Long freeHeapMemory) {
		this.freeHeapMemory = freeHeapMemory;
	}
	public Long getFreeNonHeapMemory() {
		return freeNonHeapMemory;
	}
	public void setFreeNonHeapMemory(Long freeNonHeapMemory) {
		this.freeNonHeapMemory = freeNonHeapMemory;
	}
	public Long getHeapUsed() {
		return heapUsed;
	}
	public void setHeapUsed(Long heapUsed) {
		this.heapUsed = heapUsed;
	}
	public Long getNonHeapused() {
		return nonHeapused;
	}
	public void setNonHeapused(Long nonHeapused) {
		this.nonHeapused = nonHeapused;
	}
}
