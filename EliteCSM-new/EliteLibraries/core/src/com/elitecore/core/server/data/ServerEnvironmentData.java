package com.elitecore.core.server.data;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

public class ServerEnvironmentData {

	private int processors;
	private String name;
	private String version;
	private String vmName;
	private String vmVendor;
	private long memoryUsed;
	private long maximumMemory;

	public ServerEnvironmentData() {
		OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
		setProcessors(bean.getAvailableProcessors());
		setName(bean.getName());
		setVersion(bean.getVersion());
		
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		setVmName(runtimeMXBean.getName());
		setVmVendor(runtimeMXBean.getVmVendor());
        
		MemoryMXBean memoryMXBean =  ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        setMemoryUsed(memoryUsage.getUsed());
        setMaximumMemory(memoryUsage.getMax());
	}
	
	public long getMaximumMemory() {
		return maximumMemory;
	}
	public void setMaximumMemory(long maximumMemory) {
		this.maximumMemory = maximumMemory;
	}
	public long getMemoryUsed() {
		return memoryUsed;
	}
	public void setMemoryUsed(long memoryUsed) {
		this.memoryUsed = memoryUsed;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getProcessors() {
		return processors;
	}
	public void setProcessors(int processors) {
		this.processors = processors;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public String getVmVendor() {
		return vmVendor;
	}
	public void setVmVendor(String vmVendor) {
		this.vmVendor = vmVendor;
	}
}
