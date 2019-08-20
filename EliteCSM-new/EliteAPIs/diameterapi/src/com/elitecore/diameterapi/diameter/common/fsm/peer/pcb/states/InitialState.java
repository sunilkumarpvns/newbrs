package com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.states;

import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.PCBActionExecutor;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.enums.PCBEvents;

public class InitialState extends BasePCBState {

	public InitialState(PCBActionExecutor actionExecutor) {
		super(actionExecutor);
	}

	@Override
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum type = stateEvent.getEventIdentifier();
		
		PCBEvents pcbEvent = PCBEvents.values()[type.eventOrdinal()];
		switch (pcbEvent) {
		case ReceiveDWA:			
			actionExecutor.setPending(false);
			actionExecutor.throwaway();
			break;
			
			
		case ReceiveNonDWA:
			actionExecutor.throwaway();
			break;

		
		case TimerExpires:
			actionExecutor.closeConnection(ConnectionEvents.TIMER_EXPIRED);
			actionExecutor.attemptOpen();
			actionExecutor.setWatchdog();	
			break;
		
			
		case ConnectionUp:
			actionExecutor.setNumDwa(0);
			actionExecutor.setPending(false);
			actionExecutor.setWatchdog();
			break;
		default:
			break;
		}
		
		return true;
	}

}
