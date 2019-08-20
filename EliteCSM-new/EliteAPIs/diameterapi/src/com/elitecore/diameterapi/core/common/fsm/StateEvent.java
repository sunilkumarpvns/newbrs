package com.elitecore.diameterapi.core.common.fsm;

import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;

/**
 * 
 * @author pulindani
 *
 */
public class StateEvent {
	
	private IEventEnum event;
	private IStateEnum state;
	private IStateEnum nextState;
	private IStateEnum nextStateEntryEvent;
	private IStateTransitionData stateTransitionData;

	public StateEvent(IStateEnum state, IEventEnum event) {
		this(state, event, state.getNextState(event), null);
	}

	public StateEvent(IStateEnum state, IEventEnum event, IStateEnum nextState) {
		this(state, event, nextState, null);
	}

	public StateEvent(IStateEnum state, IEventEnum event, IStateTransitionData stateTransitionData) {
		this(state, event, state.getNextState(event), stateTransitionData);
	}

	public StateEvent(IStateEnum state, IEventEnum event, IStateEnum nextState, IStateTransitionData stateTransitionData) {
		this.state = state;
		this.event = event;
		this.nextState = nextState;
		this.stateTransitionData = stateTransitionData;
	}
	
	public IEventEnum getEventIdentifier() {
		return event;
	}
	
	public IStateEnum getStateIdentifier() {
		return state;
	}
	
	public IStateEnum getNextStateIdentifier() {
		return nextState;
	}
	
	public IStateTransitionData getStateTransitionData() {
		return stateTransitionData;
	}
	
	/**
	 * @param nextStateEntryEvent the nextStateEntryEvent to set
	 */
	public void setNextStateEntryEvent(IStateEnum nextStateEntryEvent) {
		this.nextStateEntryEvent = nextStateEntryEvent;
	}

	/**
	 * @return the nextStateEntryEvent
	 */
	public IStateEnum getNextStateEntryEvent() {
		return nextStateEntryEvent;
	}

	public boolean isSyncEvent() {
		if(state != nextState) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return "State: " + this.state + "/Event: " + event + "/Next State: " + nextState;
	}
	
}
