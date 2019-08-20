package com.elitecore.core.commons.scheduler;

import java.io.Serializable;
import java.util.Date;

public class EliteTaskData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String taskName;
	private Date taskStartupTime;
	private Date nextExecutionTime;
	private Date lastExecutionTime;
	private String instanceName;
	private String remark;
	
	public EliteTaskData() {
	}
	
	public EliteTaskData(String name, Date startTime, Date nextTime) {
		taskName = name;
		taskStartupTime = startTime;
		nextExecutionTime = nextTime;
	}
	
	public String getInstanceName() {
		return instanceName;
	}
	
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	
	public Date getNextExecutionTime() {
		return nextExecutionTime;
	}
	
	public void setNextExecutionTime(Date nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}
	
	public String getTaskName() {
		return taskName;
	}
	
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public Date getTaskStartupTime() {
		return taskStartupTime;
	}
	
	public void setTaskStartupTime(Date taskStartupTime) {
		this.taskStartupTime = taskStartupTime;
	}
	
	public Date getLastExecutionTime() {
		return lastExecutionTime;
	}
	
	public void setLastExecutionTime(Date lastExecutionTime) {
		this.lastExecutionTime = lastExecutionTime;
	}
	
	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
