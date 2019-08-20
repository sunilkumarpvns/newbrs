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

public class PeerStateIOpen extends PeerStateBase {

	/**
	 * 
	 * @param actionsExecutor
	 */
	public PeerStateIOpen(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
		super(DiameterPeerState.I_Open, actionsExecutor, peerStateMachineContext);
	}
	
	@Override
	public StateEvent entryAction(StateEvent event){
		StateEvent stateEvent = null;

		if(!this.peerStateMachineContext.getPeerListener().isPeerConnected()) {
			stateEvent = new StateEvent(stateEnum, DiameterPeerEvent.IPeerDisc, DiameterPeerState.Closed);
			this.processEvent(stateEvent);
		}else {
			this.peerActionsExecutor.onConnectionUp();
		}
		return stateEvent;
	}

	/**
	 * 
	 */
	
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum eventEnum = stateEvent.getEventIdentifier();
		DiameterPeerEvent diameterPeerEvents = DiameterPeerEvent.VALUES[eventEnum.eventOrdinal()];
		
		switch(diameterPeerEvents) {
			case SendMessage:
				peerActionsExecutor.atomicActionSndMessage(stateEvent);
				break;
			case IrcvMessage:
				peerActionsExecutor.atomicActionProcess(stateEvent);
				break;
			case IRcvDWR:
				peerActionsExecutor.atomicActionProcessDWR(stateEvent);
				peerActionsExecutor.atomicActionRSndDWA(stateEvent);
				break;
			case IRcvDWA:
				peerActionsExecutor.atomicActionProcessDWA(stateEvent);
				break;
			case RConnCER:
				peerActionsExecutor.atomicActionProcessDuplicateConnection(stateEvent);
				break;
			case Stop:
				peerActionsExecutor.atomicActionISndDPR(stateEvent,diameterPeerEvents);
				break;
			case IRcvDPR:
				peerActionsExecutor.atomicActionISndDPA(stateEvent);
				peerActionsExecutor.atomicActionIDisc(stateEvent);
				break;
			case IPeerDisc:
				peerActionsExecutor.atomicActionIDisc(stateEvent);
				break;
			case IRcvCER:
				peerActionsExecutor.atomicActionISndCEA(stateEvent);
				break;
			case IRcvCEA:
				peerActionsExecutor.atomicActionProcessCEA(stateEvent);
				break;
			case RPeerDisc:
				peerActionsExecutor.atomicActionRDisc(stateEvent);
			default:
				return false;
		}
		
		return true;
	}

	@Override
	public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
		DiameterPacket diameterPacket = (DiameterPacket)stateTransitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET);
		NetworkConnectionHandler connectionHandler = ((NetworkConnectionHandler)stateTransitionData.getData(PeerDataCode.CONNECTION));

		if((diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode()) && (diameterPacket.isRequest())) { 
			if(!peerStateMachineContext.getPeerListener().isSameConnection(connectionHandler)) {
				return new StateEvent(DiameterPeerState.I_Open, DiameterPeerEvent.RConnCER, DiameterPeerState.I_Open, stateTransitionData);
			}
			return new StateEvent(DiameterPeerState.I_Open, DiameterPeerEvent.IRcvCER, DiameterPeerState.I_Open, stateTransitionData);
		}else if((diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode()) && (!diameterPacket.isRequest())) {
			return new StateEvent(DiameterPeerState.I_Open, DiameterPeerEvent.IRcvCEA, DiameterPeerState.I_Open, stateTransitionData);
		}else if((diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode()) && (diameterPacket.isRequest())) {
			return new StateEvent(DiameterPeerState.I_Open, DiameterPeerEvent.IRcvDWR, DiameterPeerState.I_Open, stateTransitionData);
		}else if((diameterPacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode()) && !(diameterPacket.isRequest())) {
			return new StateEvent(DiameterPeerState.I_Open, DiameterPeerEvent.IRcvDWA, DiameterPeerState.I_Open, stateTransitionData);
		}else if((diameterPacket.getCommandCode() == CommandCode.DISCONNECT_PEER.getCode()) && (diameterPacket.isRequest())) { 
			return new StateEvent(DiameterPeerState.I_Open, DiameterPeerEvent.IRcvDPR, DiameterPeerState.Closed, stateTransitionData);
		}else if((!DiameterUtility.isBaseProtocolPacket(diameterPacket.getCommandCode()))) {
			return new StateEvent(DiameterPeerState.I_Open, DiameterPeerEvent.IrcvMessage, DiameterPeerState.I_Open, stateTransitionData);
		}else {
			return null;
		}
	}

}
