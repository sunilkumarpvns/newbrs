package com.elitecore.diameterapi.core.common.transport.tcp.connection;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.Connection;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectorContext;
import com.elitecore.diameterapi.core.common.transport.tcp.DiameterInputStream;
import com.elitecore.diameterapi.core.common.transport.tcp.TCPNetworkConnector;
import com.elitecore.diameterapi.core.common.transport.tcp.config.PeerConnectionData;
import com.elitecore.diameterapi.core.common.util.APIUtility;
import com.elitecore.diameterapi.mibs.constants.SecurityProtocol;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPConnection implements Connection{

	private static final String MODULE = "TCP-CONN";
	/*
	 * On Closing Socket, this will try for Graceful Shutdown, 
	 * If there is no Graceful shutdown till SO_LINGER_SECONDS, 
	 * TCP Stack will close abruptly with Reset TCP Connection.  
	 */
	private static final int SO_LINGER_SECONDS = 1;
	private static final boolean SO_LINGER_ON = true;
	private static final short DEFAULT_BUFFER_SIZE = -1;
	private Socket clientSocket;
    private DiameterInputStream inStream ;
    private OutputStream outStream;
    private String clientAddress;
    private int clientPort;
    private AtomicBoolean connected = new AtomicBoolean();
    private PeerConnectionData peerData;
    private ConnectorContext context;
    
    public TCPConnection(Socket clientSocket, ConnectorContext context, PeerConnectionData peerData){
    	this.clientSocket = clientSocket;            
		this.context = context;
		this.clientAddress = clientSocket.getInetAddress().getHostAddress();
		this.clientPort = clientSocket.getPort();
    	this.peerData = peerData;
    }
    
    public void init(){
    	 try {
     		inStream = new DiameterInputStream(this.clientSocket.getInputStream());
			outStream = new DataOutputStream(new BufferedOutputStream(this.clientSocket.getOutputStream()));
			
			int sockRecBufSize;
			int sockSendBufSize;
			boolean nagleAlgo;
			if (peerData != null ){
				sockRecBufSize = peerData.getSocketReceiveBufferSize();
				sockSendBufSize = peerData.getSocketSendBufferSize();
				nagleAlgo = peerData.isNagleAlgoEnabled();
			
			} else {
				sockRecBufSize = context.getSocketReceiveBufferSize();
				sockSendBufSize = context.getSocketSendBufferSize();
				nagleAlgo = false;
			}
			
			try {
				this.clientSocket.setSoTimeout(TCPNetworkConnector.SOCKET_IDLE_TIMEOUT_MS);

				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Socket So Timeout set for: " + clientAddress + ": " + clientPort + 
							" is: " + TCPNetworkConnector.SOCKET_IDLE_TIMEOUT_MS + " ms");
			} catch (IllegalArgumentException e){
				LogManager.getLogger().warn(MODULE, "Invalid Socket So Timeout size: " + TCPNetworkConnector.SOCKET_IDLE_TIMEOUT_MS + "ms configured for " 
								+ clientAddress + ": " + clientPort + ". Continuing with default time: " + 
								(clientSocket.getSoTimeout() == 0? "DISABLED" : (clientSocket.getSoTimeout() + "ms")));
			}
			
			
			try {
				if (sockSendBufSize == DEFAULT_BUFFER_SIZE) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Configured send buffer size: " + sockSendBufSize + " for " + clientAddress 
								+ ":" + clientPort + ", so using OS default send buffer size: " + clientSocket.getSendBufferSize());
					}
				} else {
					this.clientSocket.setSendBufferSize(sockSendBufSize);
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Socket send buffer size for " + clientAddress + ":" + clientPort + " is " + clientSocket.getSendBufferSize());
					}
				}

			} catch (IllegalArgumentException e){
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Invalid send buffer size: " + sockSendBufSize + " configured for " + clientAddress + ":" + clientPort 
							+ ", so using OS default send buffer size: " + clientSocket.getSendBufferSize());
				}
			}
			
			try { 
				if (sockRecBufSize == DEFAULT_BUFFER_SIZE) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Configured receive buffer size: " + sockRecBufSize + " for " + clientAddress 
								+ ":" + clientPort + ", so using OS default receive buffer size: " + clientSocket.getReceiveBufferSize());
					}
				} else {
					this.clientSocket.setReceiveBufferSize(sockRecBufSize);
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Socket receive buffer size for " + clientAddress + ":" + clientPort + " is " + clientSocket.getReceiveBufferSize());
					}
				}
			} catch (IllegalArgumentException e){
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Invalid receive buffer size: " + sockRecBufSize + " configured for " + clientAddress + ":" + clientPort 
							+ ", so using OS default send buffer size: " + clientSocket.getReceiveBufferSize());
				}
			}
			
			// Nagle Algorithm enabled = TCP No Delay set to false
			this.clientSocket.setTcpNoDelay(!nagleAlgo);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "TCP No-Delay set for: " + clientAddress+":"+clientPort + " is: " + clientSocket.getTcpNoDelay());
			
			//Setting SO_LINGER Timeout
			this.clientSocket.setSoLinger(SO_LINGER_ON, SO_LINGER_SECONDS);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "TCP So Linger is set for: " + 
						clientAddress+":"+clientPort + 
						" is: " + clientSocket.getSoLinger());
			}
			
			this.connected.set(true);
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
		}
    }

    @Override
	public String getSourceIpAddress() {
		return getClientAddress();
	}

	@Override
	public int getSourcePort() {
		return getClientPort();
	}

	@Override
	public String getLocalAddress() {
		return this.clientSocket.getLocalAddress().getHostAddress();
	}
	
	@Override
	public int hashCode() {
		try {
			int hashCode = 0;
			InetAddress address = clientSocket.getInetAddress();
			byte[] addressByte = address.getAddress();
			byte[] portByte = APIUtility.intToByteArray(this.clientPort);
			
			hashCode = addressByte[2];
			hashCode = hashCode << 8 | addressByte[3];
			hashCode = hashCode << 8 | portByte[2]; 
			hashCode = hashCode << 8 | portByte[3];
			return hashCode;
		}catch(Exception e) {
			
		}
		
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null){
			return false;
		}
		
		try {
			TCPConnection connectionHandler = (TCPConnection) obj;
			if(this.clientAddress.equalsIgnoreCase(connectionHandler.clientAddress)) {
				if(this.clientPort == connectionHandler.clientPort) {
					return true;
				}
			}
		}catch(Exception e) {
			LogManager.getLogger().error(MODULE, "Error during comparing TCP Connection. Reason:" + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		return super.equals(obj);
		
	}

	@Override
	public boolean isConnected() {
		return connected.get();
	}


	@Override
	public void write(Packet packet) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(packet.getLength());
		packet.writeTo(buffer);
		outStream.write(buffer.toByteArray());
		outStream.flush();
	}
	
	@Override
	public InetAddress getSourceInetAddress(){
		return clientSocket.getInetAddress();
	}
	
	@Override
	public String getClientAddress(){
		return clientAddress;
	}
	
	@Override
	public int getClientPort(){
		return clientPort;
	}

	@Override
	public DiameterInputStream getInputStream() {
		return inStream;
	}

	public void closeConnection() {

		if (connected.compareAndSet(true, false) == false) {
			return;
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE, "Closing Connection to " + this.clientAddress + "/" + this.clientPort);
		
		try{
			if(inStream != null) {
				inStream.close();
			}
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while closing input stream for peer: " + this.clientSocket.getInetAddress() + ", reason : " + e);
			LogManager.getLogger().trace(MODULE, e);
		}
		try{
			if(outStream!= null) {
				outStream.close();
			}
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while closing output stream for peer: " + this.clientSocket.getInetAddress() + ", reason : " + e);
			LogManager.getLogger().trace(MODULE, e);
		}


		try {
			if(clientSocket != null && !clientSocket.isClosed()){
				clientSocket.close();
			}
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().error(MODULE, "Error while closing tcp socket for peer: " + this.clientSocket.getInetAddress() + ", reason : " + e);
		}

	}
	
	

	@Override
	public boolean isClosed() {
		return clientSocket.isClosed();
	}

	@Override
	public boolean isInputShutdown() {
		return clientSocket.isInputShutdown();
	}

	@Override
	public boolean isOutputShutdown() {
		return clientSocket.isOutputShutdown();
	}
	
	Socket getSocket(){
		return clientSocket;
	}

	@Override
	public void read(byte[] data, int off, int len) {
		//read not supported
		
	}

	@Override
	public EliteSSLContextExt getEliteSSLContext() {
		return null;
	}

	@Override
	public int getLocalPort() {
		return clientSocket.getLocalPort();
	}

	@Override
	public SecurityProtocol getSecurityProtocol() {
		return SecurityProtocol.NONE;
	}

}
