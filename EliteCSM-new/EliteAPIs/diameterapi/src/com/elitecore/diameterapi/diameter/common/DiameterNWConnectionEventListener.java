/**
 * 
 */
package com.elitecore.diameterapi.diameter.common;

import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.fsm.exception.UnhandledTransitionException;
import com.elitecore.diameterapi.core.common.peer.IPeerListener;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionEventListener;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerEvent;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;

/**
 * @author pulin
 *
 */
public class DiameterNWConnectionEventListener implements
		NetworkConnectionEventListener {

	private final String MODULE = "DIA-NW-LIST";
	private IPeerListener peerListener;
	
	public DiameterNWConnectionEventListener(IPeerListener peerListener) {
		this.peerListener = peerListener;
	}
	
	@Override
	public void connectionBreak(NetworkConnectionHandler connectionHandler, ConnectionEvents event) {
		LogManager.getLogger().warn(MODULE, "Connection has Broken, Event: " + event);
		try {
			if(connectionHandler.isResponder())
				peerListener.handleEvent(DiameterPeerEvent.RPeerDisc, event);
			else
				peerListener.handleEvent(DiameterPeerEvent.IPeerDisc, event);
		} catch (UnhandledTransitionException e) {
			
			if(LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, e.getMessage());
			}
		}
	}

	@Override
	public void connectionEstablished() {
	}

	@Override
	public void connectionDPR(Map<PeerDataCode,String> eventParam, ConnectionEvents event) {
		try {
			peerListener.handleEvent(DiameterPeerEvent.Stop, event, eventParam);
		} catch (UnhandledTransitionException e) {
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	@Override
	public void connectionFailure(NetworkConnectionHandler connectionHandler) {
		LogManager.getLogger().warn(MODULE, "Connection Creation Failed");
		try {
			peerListener.handleEvent(DiameterPeerEvent.IRcvConnNack, ConnectionEvents.CONNECTION_FAILURE);
		} catch (UnhandledTransitionException e) {
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}
	
	@Override
	public void connectionCreated(NetworkConnectionHandler connectionHandler) {
		LogManager.getLogger().warn(MODULE, "Connection Created");
		try {
			peerListener.handleEvent(DiameterPeerEvent.IRcvConnAck, ConnectionEvents.CONNECTION_CREATED);
		} catch (UnhandledTransitionException e) {
			LogManager.getLogger().trace(MODULE, e);
		}
		
	}
	
	

}
