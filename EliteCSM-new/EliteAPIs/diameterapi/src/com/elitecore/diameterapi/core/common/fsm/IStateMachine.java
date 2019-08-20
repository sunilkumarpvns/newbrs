package com.elitecore.diameterapi.core.common.fsm;

import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;
import com.elitecore.diameterapi.core.common.fsm.exception.UnhandledTransitionException;

/**
 * @author pulindani
 * @version 1.0
 * @created 17-Jul-2008 3:16:15 PM
 */
public interface IStateMachine {

	/**
	 * 
	 * 
	 */
	public void switchCurrentStateTo(IStateEnum oldState,IStateEnum state);


	/**
	 * 
	 * @param stateTransitionData
	 */
	public void onStateTransitionTrigger(IStateTransitionData stateTransitionData) throws UnhandledTransitionException;
	
	/**
	 * 
	 * 
	 */
	public State fetchCurrentState();
	
	/**
	 * 
	 */
	public IStateEnum currentState();
	
	public boolean stop();
	
	public int getCurrentState();
	
	
	/**
	 * 
	 * @return elapsed time (in hundredths of a second)
	 *	since the last state change.
	 */
	public long getStateDuration();
	
}