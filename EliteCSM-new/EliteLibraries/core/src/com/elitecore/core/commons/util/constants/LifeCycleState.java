package com.elitecore.core.commons.util.constants;


/**
 * These states can be used to define Life Cycle  
 * states of any entity 
 * like Server, service or Stack.
 * 
 * @author monica.lulla
 *
 */
public enum LifeCycleState {
	
	NOT_STARTED("Not Started"),
	STARTUP_IN_PROGRESS("Startup In Progress"), 
	RUNNING("Running"),
	SHUTDOWN_IN_PROGRESS("Shutdown In Progress"),
	STOPPED("Stopped"),
	RUNNING_WITH_LAST_CONF("Running**"), 
	RESTART_CALLED("Restart signal sent"),
	RESTART_IN_PROGRESS("Restart is already in progress");
	
	public final String message;
	
	
	private LifeCycleState(String message) {
		this.message = message;
	};
	
}
