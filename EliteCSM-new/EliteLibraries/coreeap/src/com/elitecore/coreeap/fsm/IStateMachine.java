package com.elitecore.coreeap.fsm;

import com.elitecore.coreeap.util.constants.fsm.IEnum;

public interface IStateMachine {
	/**
	 * 
	 * @param state    state
	 */
	public void changeCurrentState(IEnum state);
	/**
	 * 
	 * @param event    event
	 */
	public void setCurrentEvent(IEnum event);
	
	public IEnum getCurrentState();
	public IEnum getCurrentEvent();
}
