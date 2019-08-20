package com.elitecore.diameterapi.diameter.common.peers;

import java.io.IOException;

import com.elitecore.diameterapi.core.common.session.SessionsFactory;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.explicitrouting.ExplicitRoutingHandler;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.routerx.DiameterRouter;
import com.elitecore.diameterapi.diameter.common.session.DiameterAppMessageHandler;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.diameter.stack.DuplicateDetectionHandler;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;

public class DiameterVirtualPeer extends DiameterPeer{
	private NetworkConnectionHandler virtualConnectionHandler;
	private IDiameterStackContext stackContext;
	public DiameterVirtualPeer(PeerData peerData, NetworkConnectionHandler virtualConnectionHandler, IDiameterStackContext stackContext, 
			DiameterRouter diameterRouter, SessionFactoryManager sessionFactoryManager, DiameterAppMessageHandler appMessageHandler, ExplicitRoutingHandler explicitRoutingHandler, DuplicateDetectionHandler duplicateMessageHandler) {
		super(peerData, stackContext, diameterRouter, sessionFactoryManager, appMessageHandler, explicitRoutingHandler, duplicateMessageHandler);
		this.stackContext = stackContext;
		this.virtualConnectionHandler = virtualConnectionHandler;
	}
	
	@Override
	protected void writeToStream(DiameterPacket packet)
			throws IOException {
		// TODO can we remove this method and simply use super.send()
		
		boolean destHostReplaced = false, destRealmReplaced = false;
		IDiameterAVP destHost = packet.getAVP(DiameterAVPConstants.DESTINATION_HOST);
		IDiameterAVP destRealm = packet.getAVP(DiameterAVPConstants.DESTINATION_REALM);
		if (destHost != null && DiameterConstants.VIRTUAL_IDENTIFIER.equals(destHost.getStringValue())) {
			destHost.setStringValue(getPeerData().getHostIdentity());
			destHostReplaced = true;
		}

		if (destRealm != null && DiameterConstants.VIRTUAL_IDENTIFIER.equals(destRealm.getStringValue())) {
			destRealm.setStringValue(getPeerData().getRealmName());
			destRealmReplaced = true;
		}
		
		packet.setSendTime(System.currentTimeMillis());
		
		virtualConnectionHandler.send(packet);
		
		this.stackContext.updateOutputStatistics(packet, getHostIdentity());
		
		if (destHostReplaced) {
			destHost.setStringValue(DiameterConstants.VIRTUAL_IDENTIFIER);
		}

		if (destRealmReplaced) {
			destRealm.setStringValue(DiameterConstants.VIRTUAL_IDENTIFIER);
		}
	}
	
	public void start() {
		super.start();
		getPeerStateMachine().switchCurrentStateTo(DiameterPeerState.fromStateOrdinal(getPeerStateMachine().getCurrentState()), DiameterPeerState.R_Open);
		setConnectionListener(virtualConnectionHandler);
		getPeerStateMachine().onConnectionUp();
	}
	
	@Override
	public boolean stop() {
		getPeerStateMachine().switchCurrentStateTo(DiameterPeerState.fromStateOrdinal(getPeerStateMachine().getCurrentState()), DiameterPeerState.Closed);
		closeConnection(ConnectionEvents.SHUTDOWN);
		return super.stop();
	}
}
