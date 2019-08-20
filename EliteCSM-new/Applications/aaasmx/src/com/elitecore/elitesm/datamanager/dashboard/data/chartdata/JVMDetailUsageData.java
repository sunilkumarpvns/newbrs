package com.elitecore.elitesm.datamanager.dashboard.data.chartdata;

import java.util.Arrays;

public class JVMDetailUsageData {

	private String[] serverInstance;
	private String[] createTime;
	private String[] jvmPoolName;
	private Long[] systemLoadAvgMin;
	private Long[] systemLoadAvgMax;
	private Float[] systemLoadAvg;
	
	public String[] getServerInstance() {
		return serverInstance;
	}
	public void setServerInstance(String[] serverInstance) {
		this.serverInstance = serverInstance;
	}
	public String[] getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String[] createTime) {
		this.createTime = createTime;
	}
	public String[] getJvmPoolName() {
		return jvmPoolName;
	}
	public void setJvmPoolName(String[] jvmPoolName) {
		this.jvmPoolName = jvmPoolName;
	}
	public Long[] getSystemLoadAvgMin() {
		return systemLoadAvgMin;
	}
	public void setSystemLoadAvgMin(Long[] systemLoadAvgMin) {
		this.systemLoadAvgMin = systemLoadAvgMin;
	}
	public Long[] getSystemLoadAvgMax() {
		return systemLoadAvgMax;
	}
	public void setSystemLoadAvgMax(Long[] systemLoadAvgMax) {
		this.systemLoadAvgMax = systemLoadAvgMax;
	}
	public Float[] getSystemLoadAvg() {
		return systemLoadAvg;
	}
	public void setSystemLoadAvg(Float[] systemLoadAvg) {
		this.systemLoadAvg = systemLoadAvg;
	}
	
	@Override
	public String toString() {
		return "JVMDetailUsageData [serverInstance="
				+ Arrays.toString(serverInstance) + ", createTime="
				+ Arrays.toString(createTime) + ", jvmPoolName="
				+ Arrays.toString(jvmPoolName) + ", systemLoadAvgMin="
				+ Arrays.toString(systemLoadAvgMin) + ", systemLoadAvgMax="
				+ Arrays.toString(systemLoadAvgMax) + ", systemLoadAvg="
				+ Arrays.toString(systemLoadAvg) + "]";
	}
	
	
}
