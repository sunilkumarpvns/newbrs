package com.elitecore.core.logmonitor;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.core.util.cli.cmd.logmonitorext.Monitor;

public class LogMonitorManager {

	private final LinkedHashMap<String ,Monitor<?,?>> monitorMap;
	private static LogMonitorManager logMonitorManager;

	@VisibleForTesting
	public LogMonitorManager() {
		monitorMap =new LinkedHashMap<String, Monitor<?,?>>();
	}


	public static LogMonitorManager getInstance(){
		return logMonitorManager;
	}

	static {
		logMonitorManager = new LogMonitorManager();
		
	}

	public void registerMonitor(Monitor<?,?> monitor) throws RegistrationFailedException{
		if(monitor.getType() == null){
			throw new  RegistrationFailedException("Failed to register monitor. Reason : monitor type is not specified.");
		}
		if(monitorMap.containsKey(monitor.getType())){
			throw new  RegistrationFailedException("Failed to register monitor. Reason : LogmonitorExt Command already contains monitor with type : " + monitor.getType());
		}
		monitorMap.put(monitor.getType(),monitor);	
	}

	public Monitor<?,?> getLogMonitor(String monitorType){
		return monitorMap.get(monitorType);
	}
	public Collection<Monitor<?,?>> getRegisteredMonitors() {
		return monitorMap.values();
	}
	public Set<String> getRegisteredLogMonitorTypes() {
		return monitorMap.keySet();
	}

	public Collection<Monitor<?,?>> getRegisteredLogMonitors() {
		return monitorMap.values();
	}

	public void addLogMonitor(String monitorType , String condition , Long durationMinutes) throws Exception{
		Monitor<?,?> monitor = monitorMap.get(monitorType);
		if(monitor == null){
			throw new Exception("Invalid monitor type: " + monitorType);
		}
		//MonitorInfo 
		monitor.add(condition, durationMinutes);
	}

	public boolean clearLogMonitor(String monitorType , String condition){
		Monitor<?,?> monitor = monitorMap.get(monitorType);
		if(monitor == null){
			return false;
		}
		if(condition == null || condition.trim().length() == 0){
			monitor.clearAll();
		}else{
			monitor.clear(condition);
		}
		return true;
	}

	public void clearLogMonitor(String condition){
		for(Monitor<?,?> monitor:  getRegisteredLogMonitors()){
			monitor.clear(condition);
		}
	}

	public boolean clearAllLogMonitor(String monitorType){
		Monitor<?,?> monitor = monitorMap.get(monitorType);
		if(monitor == null){
			return false;
		}
		monitor.clearAll();
		return true;
	}
	
	public void clearAllLogMonitor(){
		for(Monitor<?,?> monitor:  getRegisteredLogMonitors()){
			monitor.clearAll();
		}
	}
}