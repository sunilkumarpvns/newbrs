package com.elitecore.elitesm.web.core.system.systeminformation.form;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;
import com.elitecore.elitesm.web.dashboard.memoryusage.GarbageCollectorData;
import com.elitecore.elitesm.web.dashboard.memoryusage.HeapMemoryUsageData;
import com.elitecore.elitesm.web.dashboard.memoryusage.NonHeapMemoryUsage;
import com.elitecore.elitesm.web.dashboard.memoryusage.ThreadInformationData;

public class SystemInformationForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private Map<String,String> systemPropertiesMap;
	private List<HeapMemoryUsageData> heapMemoryList;
	private List<NonHeapMemoryUsage> nonHeapMemoryList;
	private List<GarbageCollectorData> garbageCollectorDataList;
	private List<ThreadInformationData> threadInfoDataList;
	private Long heapMemoryUsed;
	private Long heapMax;
	private Long heapUsage;
	private Long freeHeapMemory;
	
	private Long nonHeapMemoryUsed;
	private Long nonHeapMax;
	private Long nonHeapUsage;
	private Long freeNonHeapMemory;
	private String serverUpTime;
	
	public Map<String,String> getSystemPropertiesMap() {
		return systemPropertiesMap;
	}

	public void setSystemPropertiesMap(Map<String,String> systemPropertiesMap) {
		this.systemPropertiesMap = systemPropertiesMap;
	}

	public List<HeapMemoryUsageData> getHeapMemoryList() {
		return heapMemoryList;
	}

	public void setHeapMemoryList(List<HeapMemoryUsageData> heapMemoryList) {
		this.heapMemoryList = heapMemoryList;
	}

	public Long getHeapUsage() {
		return heapUsage;
	}

	public void setHeapUsage(Long heapUsage) {
		this.heapUsage = heapUsage;
	}

	public Long getNonHeapUsage() {
		return nonHeapUsage;
	}

	public void setNonHeapUsage(Long nonHeapUsage) {
		this.nonHeapUsage = nonHeapUsage;
	}

	public List<NonHeapMemoryUsage> getNonHeapMemoryList() {
		return nonHeapMemoryList;
	}

	public void setNonHeapMemoryList(List<NonHeapMemoryUsage> nonHeapMemoryList) {
		this.nonHeapMemoryList = nonHeapMemoryList;
	}

	public String getServerUpTime() {
		return serverUpTime;
	}

	public void setServerUpTime(String serverUpTime) {
		this.serverUpTime = serverUpTime;
	}

	public Long getNonHeapMemoryUsed() {
		return nonHeapMemoryUsed;
	}

	public void setNonHeapMemoryUsed(Long nonHeapMemoryUsed) {
		this.nonHeapMemoryUsed = nonHeapMemoryUsed;
	}

	public Long getNonHeapMax() {
		return nonHeapMax;
	}

	public void setNonHeapMax(Long nonHeapMax) {
		this.nonHeapMax = nonHeapMax;
	}

	public Long getHeapMemoryUsed() {
		return heapMemoryUsed;
	}

	public void setHeapMemoryUsed(Long heapMemoryUsed) {
		this.heapMemoryUsed = heapMemoryUsed;
	}

	public Long getHeapMax() {
		return heapMax;
	}

	public void setHeapMax(Long heapMax) {
		this.heapMax = heapMax;
	}

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

	public List<GarbageCollectorData> getGarbageCollectorDataList() {
		return garbageCollectorDataList;
	}

	public void setGarbageCollectorDataList(List<GarbageCollectorData> garbageCollectorDataList) {
		this.garbageCollectorDataList = garbageCollectorDataList;
	}

	public List<ThreadInformationData> getThreadInfoDataList() {
		return threadInfoDataList;
	}

	public void setThreadInfoDataList(List<ThreadInformationData> threadInfoDataList) {
		this.threadInfoDataList = threadInfoDataList;
	}

	
	
}
