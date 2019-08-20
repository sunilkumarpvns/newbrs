package com.elitecore.elitesm.web.dashboard.memoryusage;

public class ThreadInformationData {

	private String threadName;
	private String threadState;
	private Long blockedCount;
	private String totalBlockedTime="-";
	private Long waitedCount;
	private String totalWaitedTime="-";
	private String statckTraceInfo;
	
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public String getThreadState() {
		return threadState;
	}
	public void setThreadState(String threadState) {
		this.threadState = threadState;
	}
	public Long getBlockedCount() {
		return blockedCount;
	}
	public void setBlockedCount(Long blockedCount) {
		this.blockedCount = blockedCount;
	}
	public String getTotalBlockedTime() {
		return totalBlockedTime;
	}
	public void setTotalBlockedTime(String totalBlockedTime) {
		this.totalBlockedTime = totalBlockedTime;
	}
	public Long getWaitedCount() {
		return waitedCount;
	}
	public void setWaitedCount(Long waitedCount) {
		this.waitedCount = waitedCount;
	}
	public String getTotalWaitedTime() {
		return totalWaitedTime;
	}
	public void setTotalWaitedTime(String totalWaitedTime) {
		this.totalWaitedTime = totalWaitedTime;
	}
	public String getStatckTraceInfo() {
		return statckTraceInfo;
	}
	public void setStatckTraceInfo(String statckTraceInfo) {
		this.statckTraceInfo = statckTraceInfo;
	}
}
