package com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.states;

import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.PCBActionExecutor;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.enums.PCBEvents;

public class ReopenState extends BasePCBState {

	public ReopenState(PCBActionExecutor actionExecutor) {
		super(actionExecutor);
	}

	@Override
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum type = stateEvent.getEventIdentifier();
		PCBEvents pcbEvent = PCBEvents.values()[type.eventOrdinal()];
		switch (pcbEvent) {
		
		case ReceiveDWAAndNumEqualsTwo:			
			actionExecutor.setPending(false);
			actionExecutor.incrementNumDwa();
			actionExecutor.failback();
			break;
			
		case ReceiveDWAAndNumLessThanTwo:
			actionExecutor.setPending(false);
			actionExecutor.incrementNumDwa();							
			break;

		case ReceiveNonDWA:
			actionExecutor.throwaway();
			break;
			
		case TimerExpiresAndNotPending:
			actionExecutor.setPending(true);
			actionExecutor.sendWatchdog();
			actionExecutor.setWatchdog();
			break;
			
		case TimerExpiresAndPendingAndDWALessThanZero:
			actionExecutor.sendDPR();
			actionExecutor.setWatchdog();
			break;
			
		case TimerExpiresAndPendingAndDWANotLessThanZero:
			actionExecutor.setNumDwa(-1);
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
