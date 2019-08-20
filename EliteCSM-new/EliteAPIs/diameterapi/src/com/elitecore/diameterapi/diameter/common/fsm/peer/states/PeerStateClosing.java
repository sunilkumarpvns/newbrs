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
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class PeerStateClosing extends PeerStateBase {

	private final String MODULE = "CLOSING";
	
	public PeerStateClosing(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
		super(DiameterPeerState.Closing, actionsExecutor, peerStateMachineContext);
	}

	public StateEvent entryAction(StateEvent event) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Entry action is called. with event::" + event);
		
		peerActionsExecutor.startTimeoutEventTimer();
		return null;
	}

	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum type = stateEvent.getEventIdentifier();
		DiameterPeerEvent peerEvent = DiameterPeerEvent.VALUES[type.eventOrdinal()];
		switch (peerEvent) {
			case IRcvDPA:
				peerActionsExecutor.atomicActionIDisc(stateEvent);
				break;
			case RRcvDPA:
				peerActionsExecutor.atomicActionRDisc(stateEvent);
				break;
			case Timeout:
				peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
				break;
			case IPeerDisc:
				peerActionsExecutor.atomicActionIDisc(stateEvent);
				break;
			case RPeerDisc:
				peerActionsExecutor.atomicActionRDisc(stateEvent);
				break;
			case Start:
				peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
				break;
			case RConnCER:
				
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Processing RConnCER event.");
				peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.FORCE_CLOSE);
				
				ResultCode resultCode = ResultCode.DIAMETER_UNABLE_TO_DELIVER;
				
				if(peerActionsExecutor.atomicActionRAccept(stateEvent))								
					resultCode = peerActionsExecutor.atomicActionProcessCER(stateEvent);				

				peerActionsExecutor.atomicActionRSndCEA(stateEvent,resultCode);
				
				if(resultCode != ResultCode.DIAMETER_SUCCESS ){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Result code is " + resultCode.toString());
					}
				}
				break;
			default:
				return false;
		}
		return true;

	}
	
	@Override
	public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
		DiameterPacket diameterPacket = ((DiameterPacket)stateTransitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET));
		NetworkConnectionHandler connectionHandler = ((NetworkConnectionHandler)stateTransitionData.getData(PeerDataCode.CONNECTION));

		if((diameterPacket.getCommandCode() == CommandCode.DISCONNECT_PEER.getCode()) && (!diameterPacket.isRequest()) && connectionHandler.isResponder()) { 
			return new StateEvent(DiameterPeerState.Closing, DiameterPeerEvent.RRcvDPA, DiameterPeerState.Closed, stateTransitionData);
		}else if((diameterPacket.getCommandCode() == CommandCode.DISCONNECT_PEER.getCode()) && (!diameterPacket.isRequest())) { 
			return new StateEvent(DiameterPeerState.Closing, DiameterPeerEvent.IRcvDPA, DiameterPeerState.Closed, stateTransitionData);
		}if((diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode()) && (diameterPacket.isRequest())) {
			return new StateEvent(DiameterPeerState.Closing, DiameterPeerEvent.RConnCER, DiameterPeerState.R_Open, stateTransitionData);
		}
		return null;
	}

}
