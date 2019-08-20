package com.elitecore.elitesm.web.dashboard.widget.model.cpuusage;

public class CpuUsageData {

	private String instanceID;
	private long createTime;
	private float cpuAverageUsage;
	private long cpuUsageMinVal;
	private long cpuUsageMaxVal;
	private String jvmMemoryPoolName;
	private String jvmMemoryPoolType;
	private String strCreateTime;
	
	/**
	 * @return the instanceID
	 */
	public String getInstanceID() {
		return instanceID;
	}
	/**
	 * @return the createTime
	 */
	public long getCreateTime() {
		return createTime;
	}
	/**
	 * @return the cpuAverageUsage
	 */
	public float getCpuAverageUsage() {
		return cpuAverageUsage;
	}
	
	/**
	 * @param instanceID the instanceID to set
	 */
	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}
	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	/**
	 * @param cpuAverageUsage the cpuAverageUsage to set
	 */
	public void setCpuAverageUsage(float cpuAverageUsage) {
		this.cpuAverageUsage = cpuAverageUsage;
	}
	public long getCpuUsageMinVal() {
		return cpuUsageMinVal;
	}
	public void setCpuUsageMinVal(long cpuUsageMinVal) {
		this.cpuUsageMinVal = cpuUsageMinVal;
	}
	public long getCpuUsageMaxVal() {
		return cpuUsageMaxVal;
	}
	public void setCpuUsageMaxVal(long cpuUsageMaxVal) {
		this.cpuUsageMaxVal = cpuUsageMaxVal;
	}
	public String getJvmMemoryPoolName() {
		return jvmMemoryPoolName;
	}
	public void setJvmMemoryPoolName(String jvmMemoryPoolName) {
		this.jvmMemoryPoolName = jvmMemoryPoolName;
	}
	public String getJvmMemoryPoolType() {
		return jvmMemoryPoolType;
	}
	public void setJvmMemoryPoolType(String jvmMemoryPoolType) {
		this.jvmMemoryPoolType = jvmMemoryPoolType;
	}
	public String getStrCreateTime() {
		return strCreateTime;
	}
	public void setStrCreateTime(String strCreateTime) {
		this.strCreateTime = strCreateTime;
	}
	
}
