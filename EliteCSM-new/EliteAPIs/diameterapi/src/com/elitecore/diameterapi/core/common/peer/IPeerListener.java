package com.elitecore.diameterapi.core.common.peer;


import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.fsm.exception.UnhandledTransitionException;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerStatusListener;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;

/**
 * 
 * @author pulindani
 *
 */
public interface IPeerListener {

	public void processReceivedDiameterPacket(Packet Packet, NetworkConnectionHandler connectionHandler) throws UnhandledTransitionException;
	
	public void handleEvent(IEventEnum eventEnum, ConnectionEvents event)throws UnhandledTransitionException;
		
	public void handleStateTransition(IStateTransitionData stateTransitionData) throws UnhandledTransitionException;
	
	public void setConnectionListener(NetworkConnectionHandler connectionHandler);
	
	public boolean isSameConnection(NetworkConnectionHandler connectionHandler);
	
	public boolean isPeerConnected();
	
	public String getPeerName();
	
	public PeerData getPeerData();
	
	public String getHostIdentity();
	
	public String getRealm();
	
	public int getCommunicationPort();
	
	public void onConnectionUp();
	public void onConnectionDown();
	
	public List<String>getHostIPAddresses();
	
	public String getLocalIpAddress();
	public InetAddress getRemoteInetAddress();
	public void setRemoteIpAddress(String remoteIp);
	public void setRemoteInetAddress(InetAddress remoteInetAddress);
	public int getLocalPort();
	public void setRemotePort(int remotePort);
	public void setHostIdentity(String hostIdentity);
	public DiameterPeerState registerStatusListener(DiameterPeerStatusListener listener);
	public boolean isSendDPRonCloseEvent();

	void handleEvent(IEventEnum eventEnum, ConnectionEvents event,Map<PeerDataCode, String> eventParam)throws UnhandledTransitionException;

	void sendDiameterRequest(DiameterRequest diameterRequest, @Nonnull ResponseListener listener) throws UnhandledTransitionException;

	void sendDiameterAnswer(DiameterAnswer diameterAnswer) throws UnhandledTransitionException;
}
