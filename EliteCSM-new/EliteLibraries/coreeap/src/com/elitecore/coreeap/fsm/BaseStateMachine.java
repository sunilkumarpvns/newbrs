package com.elitecore.coreeap.fsm;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.constants.fsm.IEnum;

public class BaseStateMachine {
	public static final String MODULE = "BASE STATE MACHINE";
	IEnum currentState = null;
	IEnum currentEvent = null;

	public BaseStateMachine(){

	}

	/**
	 * 
	 * @exception Throwable
	 */
	public void finalize()
	  throws Throwable{
		super.finalize();
	}

	/**
	 * 
	 * @param state    state
	 */
	public void changeCurrentState(IEnum state){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "State transition from " + this.currentState + " to " + state);
		this.currentState = state;
	}
	
	public IEnum getCurrentState() {
		return currentState;
	}

	/**
	 * 
	 * @param event    event
	 */
	public void setCurrentEvent(IEnum event){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Event transition from " + this.currentEvent + " to " + event);
		this.currentEvent = event;
	}
	
	public IEnum getCurrentEvent() {
		return currentEvent;
	}
}
