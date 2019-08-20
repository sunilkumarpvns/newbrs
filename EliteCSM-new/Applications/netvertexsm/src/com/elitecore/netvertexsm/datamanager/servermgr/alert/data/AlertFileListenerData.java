package com.elitecore.netvertexsm.datamanager.servermgr.alert.data;

public class AlertFileListenerData extends BaseAlertListener{
	
	private java.lang.Long fileListenerId;
	private String fileName;
	private Long rollingType;
	private Long rollingUnit;
	private Long maxRollingUnit;
	private String compRollingUnit;
	
	public java.lang.Long getFileListenerId() {
		return fileListenerId;
	}
	public void setFileListenerId(java.lang.Long fileListenerId) {
		this.fileListenerId = fileListenerId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Long getRollingType() {
		return rollingType;
	}
	public void setRollingType(Long rollingType) {
		this.rollingType = rollingType;
	}
	public Long getRollingUnit() {
		return rollingUnit;
	}
	public void setRollingUnit(Long rollingUnit) {
		this.rollingUnit = rollingUnit;
	}
	public Long getMaxRollingUnit() {
		return maxRollingUnit;
	}
	public void setMaxRollingUnit(Long maxRollingUnit) {
		this.maxRollingUnit = maxRollingUnit;
	}
	public String getCompRollingUnit() {
		return compRollingUnit;
	}
	public void setCompRollingUnit(String compRollingUnit) {
		this.compRollingUnit = compRollingUnit;
	}
    	
	
	
}
