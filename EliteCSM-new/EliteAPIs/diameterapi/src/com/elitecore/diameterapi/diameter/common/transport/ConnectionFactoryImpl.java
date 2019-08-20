package com.elitecore.diameterapi.diameter.common.transport;

import java.net.Socket;

import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.core.commons.tls.constant.TLSConnectionMode;
import com.elitecore.diameterapi.core.common.transport.Connection;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectorContext;
import com.elitecore.diameterapi.core.common.transport.tcp.config.PeerConnectionData;
import com.elitecore.diameterapi.core.common.transport.tcp.connection.ConnectionFactory;
import com.elitecore.diameterapi.core.common.transport.tcp.connection.TCPConnection;
import com.elitecore.diameterapi.core.common.transport.tcp.connection.TLSConnection;
import com.elitecore.diameterapi.core.common.transport.tcp.exception.HandShakeFailException;
import com.elitecore.diameterapi.core.common.util.constant.ConnectionRole;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeer;
import com.elitecore.diameterapi.diameter.common.peers.PeerProvider;

public class ConnectionFactoryImpl implements ConnectionFactory{
	
	private PeerProvider peerProvider;
	
	public ConnectionFactoryImpl(PeerProvider peerProvider) {
		this.peerProvider = peerProvider;
	}
	
	
	@Override
	public final Connection createConnection(Socket clientSocket, ConnectionRole connectionRole, ConnectorContext context) throws Exception{
		
		String peerAddress = clientSocket.getInetAddress().getHostAddress();
		
		DiameterPeer diameterPeer = peerProvider.getPeer(peerAddress);
		
		if(diameterPeer == null){
			diameterPeer = peerProvider.getPeer(clientSocket.getInetAddress().getHostName());
		}
		
		
		TCPConnection connection = null;
		if(diameterPeer != null){
			PeerConnectionData peerData = diameterPeer.getPeerData();
			
			connection = new TCPConnection(clientSocket, context, peerData);
		} else{
			connection = new TCPConnection(clientSocket, context, null);
		}
		
		((TCPConnection)connection).init();
		
		
		/*	
		 	If peerData found then check that "ConnectionStandart = RFC6733" is enabled 
								OR 
		 	peerData not found then check that "ConnectionStandart = RFC6733" on stack level is enabled
		 */ 
		if(diameterPeer != null){
			if(diameterPeer.getPeerData().getSecurityStandard() == SecurityStandard.RFC_6733){
				secureConnectionWithTLS(connection, connectionRole, context, diameterPeer.createEliteSSLContext());
			}
		}else if(context.getDefaultSecurityStandard() == SecurityStandard.RFC_6733) {
			secureConnectionWithTLS(connection, connectionRole, context, context.createEliteSSLContext());
		}
		
		return connection;
	}
	
	
	@Override
	public final Connection secureConnectionWithTLS(Connection connection, ConnectionRole connectionRole, ConnectorContext context, EliteSSLContextExt eliteSSLContext) throws HandShakeFailException{
		
		TLSConnection tlsConnection;
		if(connectionRole == ConnectionRole.Responder){
			tlsConnection = new TLSConnection(context,(TCPConnection)connection,TLSConnectionMode.SERVER, eliteSSLContext);
		} else {
			tlsConnection = new TLSConnection(context,(TCPConnection)connection, TLSConnectionMode.CLIENT, eliteSSLContext);
		}
		
		tlsConnection.startHandshake();
		return tlsConnection;
	}

}
