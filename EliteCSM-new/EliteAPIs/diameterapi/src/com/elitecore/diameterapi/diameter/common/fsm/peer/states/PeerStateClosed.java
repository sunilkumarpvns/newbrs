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


public class PeerStateClosed extends PeerStateBase {
	private static final String MODULE = "PEER-STATE-CLOSED"; 
	public PeerStateClosed(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
		super(DiameterPeerState.Closed, actionsExecutor, peerStateMachineContext);
	}
	
	@Override
	public StateEvent entryAction(StateEvent event){
		StateEvent stateEvent = null;
		try {
			this.peerActionsExecutor.onConnectionDown();
		}catch(Exception e) {
			LogManager.getLogger().warn(MODULE, e.getMessage());
		}
		return stateEvent;
	}


	
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum event = stateEvent.getEventIdentifier();
		DiameterPeerEvent peerEvent = DiameterPeerEvent.VALUES[event.eventOrdinal()];
		switch (peerEvent) {
			case Start:
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Processing Start event.");
				
				peerActionsExecutor.atomicActionSndConnReq(stateEvent);
				break;
			case RConnCER:
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Processing RConnCER event.");
				
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

		if(diameterPacket != null) {
			if((diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode()) && (diameterPacket.isRequest())) {
				return new StateEvent(DiameterPeerState.Closed, DiameterPeerEvent.RConnCER, DiameterPeerState.R_Open, stateTransitionData);
			}else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Received Packet is other than CER, Closing received connection");
				connectionHandler.closeConnection(ConnectionEvents.REJECT_CONNECTION);
			}
		}
		return null;
	}
}
