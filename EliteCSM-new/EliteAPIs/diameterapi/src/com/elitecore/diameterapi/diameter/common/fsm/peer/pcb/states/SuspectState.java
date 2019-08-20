package com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.states;

import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.PCBActionExecutor;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.enums.PCBEvents;

public class SuspectState extends BasePCBState {

	public SuspectState(PCBActionExecutor actionExecutor) {
		super(actionExecutor);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum type = stateEvent.getEventIdentifier();
		
		PCBEvents pcbEvent = PCBEvents.values()[type.eventOrdinal()];
		switch (pcbEvent) {
		case ReceiveDWA:			
			actionExecutor.setPending(false);
			actionExecutor.failback();
			actionExecutor.setWatchdog();
			break;
			
			
		case ReceiveNonDWA:
			actionExecutor.failback();
			actionExecutor.setWatchdog();
			break;
			
			
		case TimerExpires:
			actionExecutor.sendDPR();
			actionExecutor.setWatchdog();
			break;
			
		case ConnectionDown:
			actionExecutor.closeConnection(ConnectionEvents.CONNECTION_BREAK);
			actionExecutor.setWatchdog();
			break;
		default:
			break;
		}
		
		return true;
	}

}
