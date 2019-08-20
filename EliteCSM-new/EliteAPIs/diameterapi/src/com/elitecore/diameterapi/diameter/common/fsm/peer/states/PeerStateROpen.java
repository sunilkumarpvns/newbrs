package com.elitecore.diameterapi.diameter.common.fsm.peer.states;

import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.diameter.common.fsm.peer.IPeerAtomicActionsExecutor;
import com.elitecore.diameterapi.diameter.common.fsm.peer.IPeerStateMachineContext;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerEvent;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;

public class PeerStateROpen extends PeerStateBase {

	public PeerStateROpen(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
		super(DiameterPeerState.R_Open, actionsExecutor, peerStateMachineContext);
	}
	@Override
	public StateEvent entryAction(StateEvent event){
		StateEvent stateEvent = null;

		if(!this.peerStateMachineContext.getPeerListener().isPeerConnected()) {
			stateEvent = new StateEvent(stateEnum, DiameterPeerEvent.RPeerDisc, DiameterPeerState.Closed);
			this.processEvent(stateEvent);
		}else {
			this.peerActionsExecutor.onConnectionUp();
		}
		
		//DiameterAnswer diameterAnswer = event.getStateTransitionData().getData(PeerDataCode.DIAMETER_SENT_PACKET);
		return stateEvent;
	}
	
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum type = stateEvent.getEventIdentifier();
		DiameterPeerEvent peerEvent = DiameterPeerEvent.VALUES[type.eventOrdinal()];
		switch (peerEvent) {
			case SendMessage:
				peerActionsExecutor.atomicActionSndMessage(stateEvent);
				break;
			case RrcvMessage:
				peerActionsExecutor.atomicActionProcess(stateEvent);
				break;
			case RRcvDWR:
				peerActionsExecutor.atomicActionProcessDWR(stateEvent);
				peerActionsExecutor.atomicActionRSndDWA(stateEvent);
				break;
			case RRcvDWA:
				peerActionsExecutor.atomicActionProcessDWA(stateEvent);
				break;
			case RConnCER:
				peerActionsExecutor.atomicActionProcessDuplicateConnection(stateEvent);
				break;
			case Stop:
				peerActionsExecutor.atomicActionRSndDPR(stateEvent,peerEvent);
				break;
			case RRcvDPR:
				peerActionsExecutor.atomicActionRSndDPA(stateEvent);
				peerActionsExecutor.atomicActionRDisc(stateEvent);
				break;
			case RPeerDisc:
				peerActionsExecutor.atomicActionRDisc(stateEvent);
				break;
			case RRcvCER:
				peerActionsExecutor.atomicActionRSndCEA(stateEvent);
				break;
			case RRcvCEA:
				peerActionsExecutor.atomicActionProcessCEA(stateEvent);
				break;
			case IPeerDisc:
				peerActionsExecutor.atomicActionIDisc(stateEvent);
			default:
				return false;
		}
		
		return true;
	}
	
	@Override
	public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
		DiameterPacket diameterPacket = ((DiameterPacket)stateTransitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET));
		NetworkConnectionHandler connectionHandler = ((NetworkConnectionHandler)stateTransitionData.getData(PeerDataCode.CONNECTION));

		if((diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode()) && (diameterPacket.isRequest())) { //State = ROpen
			if(!peerStateMachineContext.getPeerListener().isSameConnection(connectionHandler)) {
				return new StateEvent(DiameterPeerState.R_Open, DiameterPeerEvent.RConnCER, DiameterPeerState.R_Open, stateTransitionData);
			}
			return new StateEvent(DiameterPeerState.R_Open, DiameterPeerEvent.RRcvCER, DiameterPeerState.R_Open, stateTransitionData);
		}else if((diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode()) && (!diameterPacket.isRequest())) { //State = ROpen
			return new StateEvent(DiameterPeerState.R_Open, DiameterPeerEvent.RRcvCEA, DiameterPeerState.R_Open, stateTransitionData);
		}else if((diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode()) && (diameterPacket.isRequest())) { //State = ROpen
			return new StateEvent(DiameterPeerState.R_Open, DiameterPeerEvent.RRcvDWR, DiameterPeerState.R_Open, stateTransitionData);
		}else if((diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode()) && !(diameterPacket.isRequest())) { //State = ROpen
			return new StateEvent(DiameterPeerState.R_Open, DiameterPeerEvent.RRcvDWA, DiameterPeerState.R_Open, stateTransitionData);
		}else if((diameterPacket.getCommandCode() == CommandCode.DISCONNECT_PEER.getCode()) && (diameterPacket.isRequest())) { //State = ROpen
			return new StateEvent(DiameterPeerState.R_Open, DiameterPeerEvent.RRcvDPR, DiameterPeerState.Closed, stateTransitionData);
		}else if((!DiameterUtility.isBaseProtocolPacket(diameterPacket.getCommandCode()))) { //State = ROpen
			return new StateEvent(DiameterPeerState.R_Open, DiameterPeerEvent.RrcvMessage, DiameterPeerState.R_Open, stateTransitionData);
		}else {
			return null;
		}
	}

}
