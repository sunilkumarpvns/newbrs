package com.elitecore.elitesm.datamanager.dashboard.data.chartdata;

import java.io.Serializable;
import java.sql.Timestamp;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class MemoryUsageData extends BaseData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Timestamp timestamp;
	private String instanceId;
	private Integer memoryUsage;
	private Integer id;
	private Long epochTime;
	
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
	public Integer getMemoryUsage() {
		return memoryUsage;
	}
	public void setMemoryUsage(Integer memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Long getEpochTime() {
		return epochTime;
	}
	public void setEpochTime(Long epochTime) {
		this.epochTime = epochTime;
	}
	
	@Override
	public String toString() {
		return epochTime+","+memoryUsage;
	}
	
}
