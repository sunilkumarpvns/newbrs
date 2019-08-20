/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.fsm.peer.states;

import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.fsm.peer.IPeerAtomicActionsExecutor;
import com.elitecore.diameterapi.diameter.common.fsm.peer.IPeerStateMachineContext;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerEvent;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;

/**
 * @author pulindani
 *
 */
public class PeerStateWaitReturn extends PeerStateBase {

	/**
	 * @param actionsExecutor
	 */
	public PeerStateWaitReturn(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
		super(DiameterPeerState.Wait_Returns, actionsExecutor, peerStateMachineContext);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.diameterapi.core.fsm.State#processEvent(com.elitecore.diameterapi.core.fsm.StateEvent)
	 */
	
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum eventEnum = stateEvent.getEventIdentifier();
		DiameterPeerEvent diameterPeerEvents = DiameterPeerEvent.VALUES[eventEnum.eventOrdinal()];
		switch (diameterPeerEvents) {
			case WinElection:
				peerActionsExecutor.atomicActionIDisc(stateEvent);
				peerActionsExecutor.atomicActionRSndCEA(stateEvent);
				break;
			case IPeerDisc:
				peerActionsExecutor.atomicActionIDisc(stateEvent);
				peerActionsExecutor.atomicActionRSndCEA(stateEvent);
				break;
			case IRcvCEA:
				peerActionsExecutor.atomicActionRDisc(stateEvent);
				break;
			case RPeerDisc:
				peerActionsExecutor.atomicActionRDisc(stateEvent);
				break;
			case RConnCER:
				peerActionsExecutor.atomicActionRReject((NetworkConnectionHandler) (stateEvent.getStateTransitionData().getData(PeerDataCode.CONNECTION)));
				break;
			case Timeout:
				peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_DPR);
				break;
			case Start:
				peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
				break;
			default:
				break;
		}
		return true;
	}

	@Override
	public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
		return null;
	}

}
