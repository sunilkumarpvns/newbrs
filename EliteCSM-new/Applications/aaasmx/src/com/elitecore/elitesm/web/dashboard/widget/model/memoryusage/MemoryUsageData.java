package com.elitecore.elitesm.web.dashboard.widget.model.memoryusage;

import java.sql.Timestamp;

public class MemoryUsageData {

	private Timestamp timestamp;
	private String instanceId;
    private Long memoryUsage;
	private Long epochTime;
	private Long heapUsed;
	private Long heapUsedMinVal;
	private Long heapUsedMaxVal;
	private Long nonHeapUsed;
	private Long nonHeapMinVal;
	private Long nonHeapMaxVal;
	private String strCreateTime;
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
		if(timestamp != null)
	     setEpochTime(timestamp.getTime());
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public Long getMemoryUsage() {
		return memoryUsage;
	}
	public void setMemoryUsage(Long memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
	
	public Long getEpochTime() {
		return epochTime;
	}
	public void setEpochTime(Long epochTime) {
		this.epochTime = epochTime;
	}
	public Long getHeapUsed() {
		return heapUsed;
	}
	public void setHeapUsed(Long heapUsed) {
		this.heapUsed = heapUsed;
	}
	public Long getHeapUsedMinVal() {
		return heapUsedMinVal;
	}
	public void setHeapUsedMinVal(Long heapUsedMinVal) {
		this.heapUsedMinVal = heapUsedMinVal;
	}
	public Long getHeapUsedMaxVal() {
		return heapUsedMaxVal;
	}
	public void setHeapUsedMaxVal(Long heapUsedMaxVal) {
		this.heapUsedMaxVal = heapUsedMaxVal;
	}
	public Long getNonHeapUsed() {
		return nonHeapUsed;
	}
	public void setNonHeapUsed(Long nonHeapUsed) {
		this.nonHeapUsed = nonHeapUsed;
	}
	public Long getNonHeapMinVal() {
		return nonHeapMinVal;
	}
	public void setNonHeapMinVal(Long nonHeapMinVal) {
		this.nonHeapMinVal = nonHeapMinVal;
	}
	public Long getNonHeapMaxVal() {
		return nonHeapMaxVal;
	}
	public void setNonHeapMaxVal(Long nonHeapMaxVal) {
		this.nonHeapMaxVal = nonHeapMaxVal;
	}
	
	@Override
	public String toString() {
		return epochTime+","+memoryUsage;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((instanceId == null) ? 0 : instanceId.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemoryUsageData other = (MemoryUsageData) obj;
		if (instanceId == null) {
			if (other.instanceId != null)
				return false;
		} else if (!instanceId.equals(other.instanceId))
			return false;
		return true;
	}
	public String getStrCreateTime() {
		return strCreateTime;
	}
	public void setStrCreateTime(String strCreateTime) {
		this.strCreateTime = strCreateTime;
	}
	

	
}
