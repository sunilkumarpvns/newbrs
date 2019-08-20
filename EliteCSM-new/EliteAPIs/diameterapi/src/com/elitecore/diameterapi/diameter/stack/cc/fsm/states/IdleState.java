package com.elitecore.diameterapi.diameter.stack.cc.fsm.states;

import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.diameter.stack.cc.fsm.CCActionExecutor;
import com.elitecore.diameterapi.diameter.stack.cc.fsm.enums.CCEvents;

public class IdleState extends CCBaseState {
	
	public IdleState(CCActionExecutor actionExecutor) {
		super(actionExecutor);
	}

	public boolean process(IStateTransitionData transitionData) {
		
		return true;
	}
	@Override
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum type = stateEvent.getEventIdentifier();
		CCEvents ccEvent = CCEvents.values()[type.eventOrdinal()];
		switch (ccEvent) {
		case CcInitialRequest:
			actionExecutor.sendCcAnswer(stateEvent);
			actionExecutor.startTcc(stateEvent);
			break;
			
		case CcEventRequest:
			actionExecutor.sendCcAnswer(stateEvent);
			break;
			
		case UnknownEvent:
		default:
			actionExecutor.sendErrorMessage(stateEvent);
			break;
		}
		return true;
	}

}
