package com.elitecore.diameterapi.diameter.stack.cc.fsm.states;

import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.diameter.stack.cc.fsm.CCActionExecutor;
import com.elitecore.diameterapi.diameter.stack.cc.fsm.enums.CCEvents;

public class OpenState extends CCBaseState {

	public OpenState(CCActionExecutor actionExecutor) {
		super(actionExecutor);
	}

	@Override
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum type = stateEvent.getEventIdentifier();
		CCEvents ccEvent = CCEvents.values()[type.eventOrdinal()];
		switch (ccEvent) {
		case CcUpdateRequest:
			actionExecutor.sendCcAnswer(stateEvent);
			actionExecutor.restartTcc(stateEvent);
			break;
			
		case CcTerminationRequest:
			actionExecutor.sendCcAnswer(stateEvent);
			actionExecutor.stopTcc(stateEvent);
			break;
		
		case SessionSupervisionTimerExpired:
			actionExecutor.sendAbortSessionRequest(stateEvent);
			break;
			
		case UnknownEvent:
		default:
			
			actionExecutor.sendErrorMessage(stateEvent);
			break;
		}
		
		return true;
	}

}
