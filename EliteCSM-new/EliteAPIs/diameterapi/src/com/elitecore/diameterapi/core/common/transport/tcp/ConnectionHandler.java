package com.elitecore.diameterapi.core.common.transport.tcp;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLHandshakeException;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.packet.MalformedPacketException;
import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.Connection;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionEventListener;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.tcp.config.PeerConnectionData;
import com.elitecore.diameterapi.core.common.transport.tcp.connection.ConnectionFactory;
import com.elitecore.diameterapi.core.common.transport.tcp.exception.HandShakeFailException;
import com.elitecore.diameterapi.core.common.util.constant.ConnectionRole;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;
import com.elitecore.diameterapi.mibs.constants.SecurityProtocol;

public class ConnectionHandler implements Runnable, NetworkConnectionHandler{
	
	private static final String MODULE = "CONN-HNDLR";
	private Connection connection; 
    private ConnectionRole conenctionRole = ConnectionRole.Responder;
    private List<NetworkConnectionEventListener> networkConnectionEventListeners; 
    private ConnectorContext context;
    private ConnectionFactory connectionFactory;
    private String hostName;
    private final ReentrantLock connectionLock;
    private static final int MAX_MALFORMED_LIMIT = 10;
    private int malformedRequestCount = 0;
	
	public ConnectionHandler(Connection connection, ConnectionFactory connectionFactory, ConnectionRole connectionRole, ConnectorContext context) throws Exception {
		this.conenctionRole = connectionRole;
		this.context = context;
		this.connectionFactory = connectionFactory;
		this.connection = connection;
		this.networkConnectionEventListeners = new ArrayList<NetworkConnectionEventListener>();
		this.hostName = "";
		connectionLock = new ReentrantLock();
	}
	
	public void run() {
		ConnectionEvents event = ConnectionEvents.DISCONNECTED;
		try {
			doConnectionNegotiation();
			boolean connectionCloseEventGenerated = false;
			while(true){ //NOSONAR - Reason: Loops should not be infinite
				try{
					DiameterPacket packet = connection.getInputStream().readDiameterPacket();
					malformedRequestCount = 0;
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Packet obtained from stream, assigning task to process packet");          
					context.executeInAsync(packet, this);
					
				}  catch (MalformedPacketException e){
					/*
					 * Malformed Packet recognized from Input Stream, 
					 * Unable to Parse bytes. 
					 */
					malformedRequestCount++;
					if(connectionCloseEventGenerated == false || malformedRequestCount % 100 == 0){
						LogManager.getLogger().error(MODULE, "Consecutive: " + malformedRequestCount + 
								" Malformed Diameter Packet recieved from Peer: " + 
								connection.getClientAddress() + ", Reason: " + e.getMessage());
					}
					if(connectionCloseEventGenerated == false && isMaxMalformedLimitReached()){
						connectionCloseEventGenerated = true;
						LogManager.getLogger().error(MODULE, "Max Malformed Limit Reached, Sending DPR to Peer: "+ connection.getClientAddress());
						event = ConnectionEvents.CONNECTION_DPR;
						notifyNetworkEventListener(event, networkConnectionEventListeners);
					}
					LogManager.getLogger().trace(MODULE, e);
				}

			}
		}  catch (MalformedPacketException e){ 
			/*
			 * Malformed Bytes recognized from Input Stream, 
			 * Unable to Parse bytes.
			 * Closing Connection as CER was expected 
			 * and found Malformed Bytes arrived from Stream.
			 */
			LogManager.getLogger().error(MODULE, "Discarding Malformed Diameter Packet, Closing Connection for peer: " + connection.getClientAddress() + ", reason : " + e.getMessage());
			ignoreTrace(e);
			closeConnection(event);
		} catch (EOFException e) {

			/*
			 * EOF occurs when input stream is closed from Peer, 
			 * while reading from Stream.
			 * Hence, closing Connection.
			 */
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "End of Stream reached, Closing Connection for Peer: " + 
						connection.getClientAddress() + ", Reason : " + e);
			closeConnection(event);

		} catch (SSLHandshakeException e) {
			//FIXME --monica.lulla introduce Alert 
			Stack.generateAlert(StackAlertSeverity.ERROR, null, MODULE, 
					"Handshake fail of TLS on IP: " + connection.getClientAddress() + 
								". Reason: " +  e.getMessage());

			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)) {
				LogManager.getLogger().error(MODULE, 
						"Closing Connection due to Handshake Error handling client " + 
						connection.getClientAddress() + ". Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
			closeConnection(ConnectionEvents.HANDSHAKE_FAIL);
			
		} catch (IOException ioExp) {

			/*
			 * IO occurs,
			 * when input stream is closed by diameterAPI,
			 * (example shutdown) 
			 * while reading from Stream.
			 * Hence, closing Connection.
			 */
			LogManager.getLogger().error(MODULE, "Closing Connection due to I/O Error handling client " + 
					connection.getClientAddress() + ". Reason: " + ioExp.getMessage());
			LogManager.getLogger().trace(MODULE, ioExp);
			closeConnection(event);

		}catch (Exception e){            	
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Closing Connection due to General error handling client " + connection.getClientAddress() + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
			closeConnection(event);
		} 
	}
    
    
    private void doConnectionNegotiation() throws Exception{
    	DiameterPacket packet = connection.getInputStream().readDiameterPacket();
    	if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
    		LogManager.getLogger().debug(MODULE, "Packet obtained from stream, assigning task to process packet");
    	context.executeInSync(packet, this);
    }
    
    private boolean isMaxMalformedLimitReached() {
		if(malformedRequestCount >= MAX_MALFORMED_LIMIT){
			return true;
		}
		return false;
	}

	@Override
	public boolean isConnected() {
		return connection.isConnected();
	}
	
	@Override
	public boolean isResponder() {
		return conenctionRole == ConnectionRole.Responder;
	}

	@Override
	public void send(Packet packet)	throws IOException{
		
		if (connection.isConnected() == false) {
			throw new IOException("Connection is closed");
		}
		
    	try{
   			connection.write(packet);
    	}catch (IOException e) {
    		if (connection.isClosed() || (!connection.isConnected()) || connection.isInputShutdown() || connection.isOutputShutdown()){
    			closeConnection(ConnectionEvents.DISCONNECTED);
    		} else {
    			LogManager.getLogger().trace(MODULE, e);
    			if (LogManager.getLogger().isWarnLogLevel()) {
    				LogManager.getLogger().warn(MODULE, "Error while sending packet to " + 
    						connection.getClientAddress() + ":" + connection.getClientPort() + 
    						" ignored. Reason: " + e.getMessage());
    			}
    		}
    		throw e;
		}catch (Exception e) {
    		LogManager.getLogger().trace(MODULE,e);
		}
	}
	
	public void closeConnection(ConnectionEvents event) {

		try {
			if (!connectionLock.tryLock(10, TimeUnit.MILLISECONDS)) {
				return;
			}
			
			if (connection.isConnected() == false) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Close connection invoked where connection is already closed.");
				return;
			}
			
			/*
			 * Disconnected event is generated for Network Faliure,
			 * hence for this case closing Connection for Clean Up.
			 * 
			 * In all other cases, 
			 * We close connection after generating Notification Event 
			 * (viz. generally used for DPR Sending)
			 * 
			 * Benefit: Traffic will be stopped sending TO Peer 
			 * as soon as network down is detected.
			 * 
			 */
			if(ConnectionEvents.DISCONNECTED == event) {
				connection.closeConnection();
			}
			try {
				if (context.removeConnectionHandler(this)) {
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Successfully removed Connection Handler(" + connection.getClientAddress() + ":" + connection.getClientPort() + ") from Total Connection Listeners");
					}
				} else {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Failed to remove Connection Handler(" + connection.getClientAddress() + ":" + connection.getClientAddress() + ") from Total Connection Listeners");
					}
				}
			} catch (Exception e){
				LogManager.getLogger().error(MODULE, "Failed to remove Connection Handler( " + connection.getClientAddress() + ":" + connection.getClientAddress() + ") from Total Connection Listeners. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}

			if(networkConnectionEventListeners != null && !networkConnectionEventListeners.isEmpty()){
				notifyNetworkEventListener(event, networkConnectionEventListeners);
			}
			connection.closeConnection();

		} catch (InterruptedException e1) {
			return;
		} finally {
			try{
				connectionLock.unlock();
			} catch (Exception e) {
				ignoreTrace(e);
			} 
		}

	}
	
	@Override
	public void terminateConnection(){
		try {
			if (context.removeConnectionHandler(this)) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Successfully removed Connection Handler(" + 
							connection.getClientAddress() + ":" + connection.getClientPort() + 
							") from Total Connection Listeners");
				}
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Failed to remove Connection Handler(" + 
							connection.getClientAddress() + ":" + connection.getClientAddress() + 
							") from Total Connection Listeners");
				}
			}
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Failed to remove Connection Handler( " + 
					connection.getClientAddress() + ":" + connection.getClientAddress() + 
					") from Total Connection Listeners. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}finally{
			if(connection != null && connection.isConnected())
				connection.closeConnection();
		}
	}
	
	private void notifyNetworkEventListener(ConnectionEvents event, List<NetworkConnectionEventListener> connectionEventListeners) {
    	switch (event) {
	    	case CONNECTION_ESTABLISHED:
	    		for(NetworkConnectionEventListener connectionEventListener : connectionEventListeners) {
	    			connectionEventListener.connectionEstablished();
	    		}
	    		break;
	    	case HANDSHAKE_FAIL:
	    	case CONNECTION_BREAK:
	    	case DISCONNECTED:
	    		for(NetworkConnectionEventListener connectionEventListener : connectionEventListeners) {
	    			connectionEventListener.connectionBreak(this, event);
	    		}
	    		break;
	    	case CONNECTION_DPR:
	    		Map<PeerDataCode,String> eventParam = new HashMap<PeerDataCode, String>(2);
	    		eventParam.put(PeerDataCode.DISCONNECT_REASON, CommonConstants.MALFORMED_PACKET);
	    		for(NetworkConnectionEventListener connectionEventListener : connectionEventListeners) {
	    			connectionEventListener.connectionDPR(eventParam, event);
	    		}
	    		break;
	    default:
	    	break;
    	}
    }

	@Override
	public void addNetworkConnectionEventListener(NetworkConnectionEventListener networkConnectionEventListener) {
		if(networkConnectionEventListener != null){
			networkConnectionEventListeners.add(networkConnectionEventListener);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if(obj==null) {
				return false;
			}
			ConnectionHandler connectionHandler = (ConnectionHandler) obj;
			return connection.equals(connectionHandler.connection);
		}catch(Exception e) { 
			ignoreTrace(e);
		}
		
		return super.equals(obj);
		
	}
	
	@Override
	public int hashCode() {
		try {
			return connection.hashCode();
		}catch(Exception e) { 
			ignoreTrace(e);
		}
		
		return super.hashCode();
	}
	
	@Override
	public InetAddress getSourceInetAddress() {
		return connection.getSourceInetAddress();
	}

	@Override
	public String getSourceIpAddress() {
		return connection.getSourceIpAddress();
	}

	@Override
	public int getSourcePort() {
		return connection.getSourcePort();
	}

	@Override
	public String getHostName() {
		return hostName;
	}

	@Override
	public void setHostName(String hostName) {
		this.hostName = hostName;
		
	}

	@Override
	public String getLocalAddress() {
		return connection.getLocalAddress();
	}

	@Override
	public void secureConnection(PeerConnectionData peerData,EliteSSLContextExt eliteSSLContext) throws HandShakeFailException {
		connection  = connectionFactory.secureConnectionWithTLS(connection, conenctionRole, context, eliteSSLContext);
	}

	@Override
	public int getLocalPort() {
		return connection.getLocalPort();
	}

	@Override
	public SecurityProtocol getSecurityProtocol() {
		return connection.getSecurityProtocol();
	}

	
	
}
