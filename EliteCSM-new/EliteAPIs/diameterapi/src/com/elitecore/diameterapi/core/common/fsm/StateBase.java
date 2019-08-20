package com.elitecore.diameterapi.core.common.fsm;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

/**
 * 
 * @author pulindani
 *
 */
public abstract class StateBase implements State{
	private static final String MODULE = "STATE-BASE";
	
	public StateEvent entryAction(StateEvent event) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Entry action of Base is called. with event::" + event);
		return null;
	}

	public void exitAction(StateEvent event) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Exit action of Base is called. with event::" + event);		
	}
	
	public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "GET STATE EVENT of Base is called with State Transition Data: " + stateTransitionData);
		
		return null;
		
	}
}
