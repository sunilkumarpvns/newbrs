package com.elitecore.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Process {

	private String name;
	private boolean stop;
	private List<Process> processes;
	private String reason;

	public Process(String name) {
		this.name = name;
		this.processes = new ArrayList<Process>();
	}

	public boolean isStopRequested() {
		return stop;
	}

	public synchronized void stop(String reason) throws IllegalStateException{
		if(isStopRequested()){
			throw new IllegalStateException("process:"+ name +" is stoped");
		}
		
		this.reason = reason;
		synchronized (processes) {
			this.stop = true;
			for(Process process : processes){
				process.stop(reason);
			}
		}
	}

	public String getName() {
		return name;
	}
	
	public void addChild(Process process) throws IllegalStateException,NullPointerException{
		synchronized (process) {
			if(isStopRequested()){
				throw new IllegalStateException("process:"+ name +" is stoped");
			}
		
			processes.add(process);
		}
		
	}

	public List<Process> getProcesses() {
		return Collections.unmodifiableList(processes);
	}

	public String getResonse() {
		return reason;
	}
	
}
