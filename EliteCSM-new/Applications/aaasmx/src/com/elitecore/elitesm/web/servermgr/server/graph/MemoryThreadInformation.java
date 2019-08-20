package com.elitecore.elitesm.web.servermgr.server.graph;

import java.util.List;

import com.elitecore.elitesm.web.dashboard.memoryusage.ThreadInformationData;

public class MemoryThreadInformation {
	private long currentTime;
	private long heapMemoryUsage;
	private long heapMemoryPeakUsage;
	private int totalThread;
	private int peakThread;
	private Long psMarkSweepCount;
	private Long psMarkSweepTime;
	private Long psScavengeCount;
	private Long psScavengeTime;
	
	private String psEdenused;
	private String psEdenmax;
	private String psEdenusage;
	private String psEdenpeakused;
	private String psEdenpeakmax;
	
	private String psSurvivorused;
	private String psSurvivormax;
	private String psSurvivorusage;
	private String psSurvivorpeakused;
	private String psSurvivorpeakmax;
	
	private String oldgenused;
	private String oldgenmax;
	private String oldgenusage;
	private String oldgenpeakused;
	private String oldgenpeakmax;
	
	private String permgenused;
	private String permgenmax;
	private String permgenusage;
	private String permgenpeakused;
	private String permgenpeakmax;
	
	private String codecacheused;
	private String codecachemax;
	private String codecacheusage;
	private String codecachepeakused;
	private String codecachepeakmax;
	
	private List<ThreadInformationData> threadInfoDataList;
	
	public long getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	public long getHeapMemoryUsage() {
		return heapMemoryUsage;
	}
	public void setHeapMemoryUsage(long heapMemoryUsage) {
		this.heapMemoryUsage = heapMemoryUsage;
	}
	public long getHeapMemoryPeakUsage() {
		return heapMemoryPeakUsage;
	}
	public void setHeapMemoryPeakUsage(long heapMemoryPeakUsage) {
		this.heapMemoryPeakUsage = heapMemoryPeakUsage;
	}
	public int getTotalThread() {
		return totalThread;
	}
	public void setTotalThread(int totalThread) {
		this.totalThread = totalThread;
	}
	public int getPeakThread() {
		return peakThread;
	}
	public void setPeakThread(int peakThread) {
		this.peakThread = peakThread;
	}
	public Long getPsMarkSweepCount() {
		return psMarkSweepCount;
	}
	public void setPsMarkSweepCount(Long psMarkSweepCount) {
		this.psMarkSweepCount = psMarkSweepCount;
	}
	public Long getPsMarkSweepTime() {
		return psMarkSweepTime;
	}
	public void setPsMarkSweepTime(Long psMarkSweepTime) {
		this.psMarkSweepTime = psMarkSweepTime;
	}
	public Long getPsScavengeCount() {
		return psScavengeCount;
	}
	public void setPsScavengeCount(Long psScavengeCount) {
		this.psScavengeCount = psScavengeCount;
	}
	public Long getPsScavengeTime() {
		return psScavengeTime;
	}
	public void setPsScavengeTime(Long psScavengeTime) {
		this.psScavengeTime = psScavengeTime;
	}
	public String getPsEdenused() {
		return psEdenused;
	}
	public void setPsEdenused(String psEdenused) {
		this.psEdenused = psEdenused;
	}
	public String getPsEdenmax() {
		return psEdenmax;
	}
	public void setPsEdenmax(String psEdenmax) {
		this.psEdenmax = psEdenmax;
	}
	public String getPsEdenusage() {
		return psEdenusage;
	}
	public void setPsEdenusage(String psEdenusage) {
		this.psEdenusage = psEdenusage;
	}
	public String getPsEdenpeakused() {
		return psEdenpeakused;
	}
	public void setPsEdenpeakused(String psEdenpeakused) {
		this.psEdenpeakused = psEdenpeakused;
	}
	public String getPsEdenpeakmax() {
		return psEdenpeakmax;
	}
	public void setPsEdenpeakmax(String psEdenpeakmax) {
		this.psEdenpeakmax = psEdenpeakmax;
	}
	public String getPsSurvivorused() {
		return psSurvivorused;
	}
	public void setPsSurvivorused(String psSurvivorused) {
		this.psSurvivorused = psSurvivorused;
	}
	public String getPsSurvivormax() {
		return psSurvivormax;
	}
	public void setPsSurvivormax(String psSurvivormax) {
		this.psSurvivormax = psSurvivormax;
	}
	public String getPsSurvivorusage() {
		return psSurvivorusage;
	}
	public void setPsSurvivorusage(String psSurvivorusage) {
		this.psSurvivorusage = psSurvivorusage;
	}
	public String getPsSurvivorpeakused() {
		return psSurvivorpeakused;
	}
	public void setPsSurvivorpeakused(String psSurvivorpeakused) {
		this.psSurvivorpeakused = psSurvivorpeakused;
	}
	public String getPsSurvivorpeakmax() {
		return psSurvivorpeakmax;
	}
	public void setPsSurvivorpeakmax(String psSurvivorpeakmax) {
		this.psSurvivorpeakmax = psSurvivorpeakmax;
	}
	public String getOldgenused() {
		return oldgenused;
	}
	public void setOldgenused(String oldgenused) {
		this.oldgenused = oldgenused;
	}
	public String getOldgenmax() {
		return oldgenmax;
	}
	public void setOldgenmax(String oldgenmax) {
		this.oldgenmax = oldgenmax;
	}
	public String getOldgenusage() {
		return oldgenusage;
	}
	public void setOldgenusage(String oldgenusage) {
		this.oldgenusage = oldgenusage;
	}
	public String getOldgenpeakused() {
		return oldgenpeakused;
	}
	public void setOldgenpeakused(String oldgenpeakused) {
		this.oldgenpeakused = oldgenpeakused;
	}
	public String getOldgenpeakmax() {
		return oldgenpeakmax;
	}
	public void setOldgenpeakmax(String oldgenpeakmax) {
		this.oldgenpeakmax = oldgenpeakmax;
	}
	public String getPermgenused() {
		return permgenused;
	}
	public void setPermgenused(String permgenused) {
		this.permgenused = permgenused;
	}
	public String getPermgenmax() {
		return permgenmax;
	}
	public void setPermgenmax(String permgenmax) {
		this.permgenmax = permgenmax;
	}
	public String getPermgenusage() {
		return permgenusage;
	}
	public void setPermgenusage(String permgenusage) {
		this.permgenusage = permgenusage;
	}
	public String getPermgenpeakused() {
		return permgenpeakused;
	}
	public void setPermgenpeakused(String permgenpeakused) {
		this.permgenpeakused = permgenpeakused;
	}
	public String getPermgenpeakmax() {
		return permgenpeakmax;
	}
	public void setPermgenpeakmax(String permgenpeakmax) {
		this.permgenpeakmax = permgenpeakmax;
	}
	public String getCodecacheused() {
		return codecacheused;
	}
	public void setCodecacheused(String codecacheused) {
		this.codecacheused = codecacheused;
	}
	public String getCodecachemax() {
		return codecachemax;
	}
	public void setCodecachemax(String codecachemax) {
		this.codecachemax = codecachemax;
	}
	public String getCodecacheusage() {
		return codecacheusage;
	}
	public void setCodecacheusage(String codecacheusage) {
		this.codecacheusage = codecacheusage;
	}
	public String getCodecachepeakused() {
		return codecachepeakused;
	}
	public void setCodecachepeakused(String codecachepeakused) {
		this.codecachepeakused = codecachepeakused;
	}
	public String getCodecachepeakmax() {
		return codecachepeakmax;
	}
	public void setCodecachepeakmax(String codecachepeakmax) {
		this.codecachepeakmax = codecachepeakmax;
	}
	public List<ThreadInformationData> getThreadInfoDataList() {
		return threadInfoDataList;
	}
	public void setThreadInfoDataList(List<ThreadInformationData> threadInfoDataList) {
		this.threadInfoDataList = threadInfoDataList;
	}
}
