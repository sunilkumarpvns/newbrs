package com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory;



public class JVMDetailMemoryUsageData {
	
	private String serverInstance;
	private String createTime;
	private int jvmPoolIndex;
	private long jvmPoolUsed;
	private String jvmPoolName;
	private long epochTime;
	private long systemLoadAvgMin;
	private long systemLoadAvgMax;
	private float systemLoadAvg;
	
	/**
	 * @return the serverInstance
	 */
	public String getServerInstance() {
		return serverInstance;
	}
	/**
	 * @return the creatTime
	 */
	public String getCreateTime() {
		return createTime;
	}
	/**
	 * @return the jvmPoolIndex
	 */
	public int getJvmPoolIndex() {
		return jvmPoolIndex;
	}
	/**
	 * @return the jvmPoolUsed
	 */
	public long getJvmPoolUsed() {
		return jvmPoolUsed;
	}
	/**
	 * @return the jvmPoolName
	 */
	public String getJvmPoolName() {
		return jvmPoolName;
	}
	/**
	 * @return the epochTime
	 */
	public long getEpochTime() {
		return epochTime;
	}
	/**
	 * @param serverInstance the serverInstance to set
	 */
	public void setServerInstance(String serverInstance) {
		this.serverInstance = serverInstance;
	}
	/**
	 * @param creatTime the creatTime to set
	 */
	public void setCreateTime(String creatTime) {
		this.createTime = creatTime;
	}
	/**
	 * @param jvmPoolIndex the jvmPoolIndex to set
	 */
	public void setJvmPoolIndex(int jvmPoolIndex) {
		this.jvmPoolIndex = jvmPoolIndex;
	}
	/**
	 * @param jvmPoolUsed the jvmPoolUsed to set
	 */
	public void setJvmPoolUsed(long jvmPoolUsed) {
		this.jvmPoolUsed = jvmPoolUsed;
	}
	/**
	 * @param jvmPoolName the jvmPoolName to set
	 */
	public void setJvmPoolName(String jvmPoolName) {
		this.jvmPoolName = jvmPoolName;
	}
	/**
	 * @param epochTime the epochTime to set
	 */
	public void setEpochTime(long epochTime) {
		this.epochTime = epochTime;
	}
	
	public long getSystemLoadAvgMin() {
		return systemLoadAvgMin;
	}
	public void setSystemLoadAvgMin(long systemLoadAvgMin) {
		this.systemLoadAvgMin = systemLoadAvgMin;
	}
	public long getSystemLoadAvgMax() {
		return systemLoadAvgMax;
	}
	public void setSystemLoadAvgMax(long systemLoadAvgMax) {
		this.systemLoadAvgMax = systemLoadAvgMax;
	}
	public float getSystemLoadAvg() {
		return systemLoadAvg;
	}
	public void setSystemLoadAvg(float systemLoadAvg) {
		this.systemLoadAvg = systemLoadAvg;
	}
	
	@Override
	public String toString() {
		return "JVMDetailMemoryUsageData [serverInstance=" + serverInstance
				+ ", createTime=" + createTime + ", jvmPoolIndex="
				+ jvmPoolIndex + ", jvmPoolUsed=" + jvmPoolUsed
				+ ", jvmPoolName=" + jvmPoolName + ", epochTime=" + epochTime
				+ ", systemLoadAvgMin=" + systemLoadAvgMin
				+ ", systemLoadAvgMax=" + systemLoadAvgMax + ", systemLoadAvg="
				+ systemLoadAvg + "]";
	}
	
	
	
}
