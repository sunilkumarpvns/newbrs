package com.elitecore.diameterapi.diameter.common.fsm.peer.states;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.fsm.peer.IPeerAtomicActionsExecutor;
import com.elitecore.diameterapi.diameter.common.fsm.peer.IPeerStateMachineContext;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerEvent;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;

public class PeerStateWaitConnAckElect extends PeerStateBase {

	private final String MODULE = "WAIT_I_CEA";
	
	public PeerStateWaitConnAckElect(
			IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
		super(DiameterPeerState.Wait_Conn_Ack_Elect, actionsExecutor, peerStateMachineContext);
	}

	public StateEvent entryAction(StateEvent event) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Entry action is called. with event::" + event);
		
		peerActionsExecutor.startTimeoutEventTimer();
		return null;
	}
	
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum eventEnum = stateEvent.getEventIdentifier();
		DiameterPeerEvent diameterPeerEvents = DiameterPeerEvent.VALUES[eventEnum.eventOrdinal()];
		switch (diameterPeerEvents) {
			case IRcvConnAck:
				peerActionsExecutor.atomicActionSndCER(stateEvent);
				peerActionsExecutor.atomicActionElect(stateEvent);
				break;
			case IRcvConnNack:
				peerActionsExecutor.atomicActionRSndCEA(stateEvent);
				break;
			case RPeerDisc:
				peerActionsExecutor.atomicActionRDisc(stateEvent);
				break;
			case RConnCER:
				peerActionsExecutor.atomicActionRReject((NetworkConnectionHandler) (stateEvent.getStateTransitionData().getData(PeerDataCode.CONNECTION)));
				break;
			case Timeout:
				peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
				break;
			case Start:
				peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
				break;
			case IPeerDisc:
				peerActionsExecutor.atomicActionIDisc(stateEvent);
				break;
			default:
				break;
		}
		return true;
	}

	@Override
	public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
		DiameterPacket diameterPacket = ((DiameterPacket)stateTransitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET));
		NetworkConnectionHandler connectionHandler = ((NetworkConnectionHandler)stateTransitionData.getData(PeerDataCode.CONNECTION));
		if(diameterPacket == null) {
			if(connectionHandler != null) {
				return new StateEvent(DiameterPeerState.Wait_Conn_Ack_Elect, DiameterPeerEvent.IRcvConnAck, DiameterPeerState.Wait_Returns, stateTransitionData);
			}else if(connectionHandler == null) {
				return new StateEvent(DiameterPeerState.Wait_Conn_Ack_Elect, DiameterPeerEvent.IRcvConnNack, DiameterPeerState.R_Open, stateTransitionData);
			}
		}else if((diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode()) && (diameterPacket.isRequest())) { //State = ROpen
			if(!peerStateMachineContext.getPeerListener().isSameConnection(connectionHandler)) {
				return new StateEvent(DiameterPeerState.Wait_Conn_Ack_Elect, DiameterPeerEvent.RConnCER, DiameterPeerState.Wait_Conn_Ack_Elect, stateTransitionData);
			}
		}
		return null;
	}

}
