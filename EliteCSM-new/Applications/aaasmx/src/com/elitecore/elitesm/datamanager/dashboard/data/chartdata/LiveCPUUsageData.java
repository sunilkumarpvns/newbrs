package com.elitecore.elitesm.datamanager.dashboard.data.chartdata;

import java.util.Arrays;

public class LiveCPUUsageData {

	private String[] serverName;
	private String[] jvmPoolType;
	private String[] jvmPoolName;
	private Long[] systemMinValue;
	private Long[] systemMaxValue;
	private Float[] systemAverage;
	private String[] createTime;
	
	public String[] getServerName() {
		return serverName;
	}
	public void setServerName(String[] serverName) {
		this.serverName = serverName;
	}
	public String[] getJvmPoolType() {
		return jvmPoolType;
	}
	public void setJvmPoolType(String[] jvmPoolType) {
		this.jvmPoolType = jvmPoolType;
	}
	public String[] getJvmPoolName() {
		return jvmPoolName;
	}
	public void setJvmPoolName(String[] jvmPoolName) {
		this.jvmPoolName = jvmPoolName;
	}
	public Long[] getSystemMinValue() {
		return systemMinValue;
	}
	public void setSystemMinValue(Long[] systemMinValue) {
		this.systemMinValue = systemMinValue;
	}
	public Long[] getSystemMaxValue() {
		return systemMaxValue;
	}
	public void setSystemMaxValue(Long[] systemMaxValue) {
		this.systemMaxValue = systemMaxValue;
	}
	public Float[] getSystemAverage() {
		return systemAverage;
	}
	public void setSystemAverage(Float[] systemAverage) {
		this.systemAverage = systemAverage;
	}
	@Override
	public String toString() {
		return "LiveCPUUsageData [serverName=" + Arrays.toString(serverName)
				+ ", jvmPoolType=" + Arrays.toString(jvmPoolType)
				+ ", jvmPoolName=" + Arrays.toString(jvmPoolName)
				+ ", systemMinValue=" + Arrays.toString(systemMinValue)
				+ ", systemMaxValue=" + Arrays.toString(systemMaxValue)
				+ ", systemAverage=" + Arrays.toString(systemAverage) + "]";
	}
	public String[] getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String[] createTime) {
		this.createTime = createTime;
	}
	
	
}
