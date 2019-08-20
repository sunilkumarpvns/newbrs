/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.fsm.peer.states;


import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.StateEvent;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.peer.IPeerListener;
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

/**
 * @author pulin
 *
 */
public class PeerStateWaitICEA extends PeerStateBase {

	private final String MODULE = "WAIT_I_CEA";
	/**
	 * @param stateEnum
	 * @param actionsExecutor
	 */
	public PeerStateWaitICEA(IPeerAtomicActionsExecutor actionsExecutor, IPeerStateMachineContext peerStateMachineContext) {
		super(DiameterPeerState.Wait_I_CEA, actionsExecutor, peerStateMachineContext);
		// TODO Auto-generated constructor stub
	}
	
	public StateEvent entryAction(StateEvent event) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Entry action is called. with event::" + event);

		peerActionsExecutor.startTimeoutEventTimer();
		return null;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.diameterapi.core.common.fsm.State#processEvent(com.elitecore.diameterapi.core.common.fsm.StateEvent)
	 */
	@Override
	public boolean processEvent(StateEvent stateEvent) {
		IEventEnum event = stateEvent.getEventIdentifier();
		DiameterPeerEvent peerEvent = DiameterPeerEvent.VALUES[event.eventOrdinal()];
		switch (peerEvent) {
			case Start:
				peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
			break;
			case IRcvCEA :
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Processing IRcvCEA event.");
				ResultCode resultCode = ResultCode.DIAMETER_UNABLE_TO_DELIVER;
				
				resultCode = peerActionsExecutor.atomicActionProcessCEA(stateEvent);
				if(resultCode != ResultCode.DIAMETER_SUCCESS ){
				}
				break;
			case RConnCER :
				break;
			case IPeerDisc :
				peerActionsExecutor.atomicActionIDisc(stateEvent);
				break;
			case IRcvNonCEA :
				peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_DPR);
				break;
			case Timeout :
				IPeerListener peerListener = peerStateMachineContext.getPeerListener();
				
				if(peerListener != null) {
						if (peerListener.isSendDPRonCloseEvent()) {
							peerActionsExecutor.atomicActionISndDPR(stateEvent,peerEvent);
						} else {
							peerActionsExecutor.atomicActionError(stateEvent, ConnectionEvents.CONNECTION_BREAK);
						}
					}
				break;
			case RPeerDisc:
				peerActionsExecutor.atomicActionRDisc(stateEvent);
			default:
				break;
		}
		
		return true;
	}

	@Override
	public StateEvent getStateEvent(IStateTransitionData stateTransitionData) {
		DiameterPacket diameterPacket = ((DiameterPacket)stateTransitionData.getData(PeerDataCode.DIAMETER_RECEIVED_PACKET));
		NetworkConnectionHandler connectionHandler = ((NetworkConnectionHandler)stateTransitionData.getData(PeerDataCode.CONNECTION));
		
		if((diameterPacket.getCommandCode() == CommandCode.CAPABILITIES_EXCHANGE.getCode()) && (!diameterPacket.isRequest()))  {
			if(peerStateMachineContext.getPeerListener().isSameConnection(connectionHandler)) {
				return new StateEvent(DiameterPeerState.Wait_I_CEA, DiameterPeerEvent.IRcvCEA, DiameterPeerState.I_Open, stateTransitionData);
			}else {
				return new StateEvent(DiameterPeerState.Wait_I_CEA, DiameterPeerEvent.RConnCER, DiameterPeerState.Wait_Returns, stateTransitionData);
			}
		}else {
			return new StateEvent(DiameterPeerState.Wait_I_CEA, DiameterPeerEvent.IRcvNonCEA, DiameterPeerState.Closed, stateTransitionData);
		}
	}

}
