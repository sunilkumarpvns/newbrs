package com.elitecore.elitesm.web.dashboard.widget.model.memoryusage;

public class ServerWiseMemoryUsageData {
	private String serverId;
	private Long[] epochTime;
	private Long[] memoryUsed;
	/**
	 * @return the serverId
	 */
	public String getServerId() {
		return serverId;
	}
	/**
	 * @return the epochTime
	 */
	public Long[] getEpochTime() {
		return epochTime;
	}
	/**
	 * @return the memoryUsed
	 */
	public Long[] getMemoryUsed() {
		return memoryUsed;
	}
	/**
	 * @param serverId the serverId to set
	 */
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	/**
	 * @param epochTime the epochTime to set
	 */
	public void setEpochTime(Long[] epochTime) {
		this.epochTime = epochTime;
	}
	/**
	 * @param memoryUsed the memoryUsed to set
	 */
	public void setMemoryUsed(Long[] memoryUsed) {
		this.memoryUsed = memoryUsed;
	}

	
	
	
}
