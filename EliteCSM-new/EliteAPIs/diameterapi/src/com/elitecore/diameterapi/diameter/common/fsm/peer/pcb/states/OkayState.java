package com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.states;

import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.PCBActionExecutor;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.enums.PCBEvents;

public class OkayState extends BasePCBState {

	public OkayState(PCBActionExecutor actionExecutor) {
		super(actionExecutor);
	}

	@Override
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum type = stateEvent.getEventIdentifier();
		
		PCBEvents pcbEvent = PCBEvents.values()[type.eventOrdinal()];
		switch (pcbEvent) {
		case ReceiveDWA:			
			actionExecutor.setPending(false);
			actionExecutor.setWatchdog();
			break;
			
			
		case ReceiveNonDWA:
			actionExecutor.setWatchdog();
			break;
			
			
		case TimerExpiresAndPending:
			actionExecutor.failover();
			actionExecutor.setWatchdog();
			break;
			
			
		case TimerExpiresAndNotPending:
			actionExecutor.setPending(true);
			actionExecutor.sendWatchdog();
			actionExecutor.setWatchdog();
			break;
			
			
		case ConnectionDown:
			actionExecutor.closeConnection(ConnectionEvents.CONNECTION_BREAK);
			actionExecutor.failover();
			actionExecutor.setWatchdog();
			break;
		default:
			break;
		}
		
		return true;
	}

}
