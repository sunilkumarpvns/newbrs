/**
 * 
 */
package com.elitecore.diameterapi.core.common.transport.tcp;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.net.SocketFactory;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory.EliteThread;
import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.core.commons.tls.EliteSSLContextFactory;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.internal.tasks.CallableSingleExecutionAsyncTask;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.peer.IPeerListener;
import com.elitecore.diameterapi.core.common.transport.Connection;
import com.elitecore.diameterapi.core.common.transport.ConnectorState;
import com.elitecore.diameterapi.core.common.transport.INetworkConnector;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.PacketProcessImpl;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.common.transport.tcp.config.PeerConnectionData;
import com.elitecore.diameterapi.core.common.transport.tcp.connection.ConnectionFactory;
import com.elitecore.diameterapi.core.common.util.constant.ConnectionRole;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerEvent;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.diameter.stack.DiameterStack;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import org.apache.logging.log4j.ThreadContext;
/**
 * @author pulin
 *
 */
public abstract class TCPNetworkConnector implements INetworkConnector {
	
    public static final int CONNECTION_TIMEOUT_MS = 3000;
    
    public static final int SOCKET_IDLE_TIMEOUT_MS=2000;
    
    private ConnectorState currentState = ConnectorState.NOT_STARTED;
    
    private ServerSocket serverSocket;
    
    protected DiameterStack stack;
    
    private SocketDetail boundSocketDetail;

    private Thread connectionLSNRThread;
	
	private List<ConnectionHandler> totalConnectionHandlers;

    private final static String MODULE = "TCP-NET-CONNECTOR"; 
    
    private ConnectorContext context ;
        
    private ConnectionFactory connectionFactory;
    
    private ServiceRemarks remarks;
    
    private boolean stopRequested = false;
    
	public TCPNetworkConnector(final DiameterStack stack) {
		
		this.stack = stack;
		this.totalConnectionHandlers = new ArrayList<ConnectionHandler>();
		context = new ConnectorContext() {

			@Override
			public int getSocketReceiveBufferSize() {
				return TCPNetworkConnector.this.getSocketReceiveBufferSize();
			}

			@Override
			public int getSocketSendBufferSize() {
				return TCPNetworkConnector.this.getSocketSendBufferSize();
			}

			@Override
			public boolean removeConnectionHandler(NetworkConnectionHandler handler) {
				if(handler != null){
					totalConnectionHandlers.remove(handler);
					LogManager.getLogger().warn(MODULE, "Total connection handlers are: " + totalConnectionHandlers.size());
					return true;
				}
				return false;
				
			}

			@Override
			public void executeInAsync(Packet packet, NetworkConnectionHandler handler) {

				try {
					stack.addMDC((DiameterPacket) packet);
					stack.submitToWorker(new PacketProcessImpl(packet, handler, stack, ThreadContext.getContext()));
				} catch (Exception exp) {
					LogManager.getLogger().warn(MODULE, exp.getMessage());
					LogManager.getLogger().trace(MODULE, exp);
				} finally {
					stack.clearMDC();

				}
			}

			@Override
			public String getNetworkAddress() {
				return serverSocket.getInetAddress().getHostAddress();
			}

			@Override
			public int getNetworkPort() {
				return serverSocket.getLocalPort();
			}

			@Override
			public void executeInSync(Packet packet, NetworkConnectionHandler handler) {
				stack.handleReceivedMessage(packet, handler);
			}

			@Override
			public PeerConnectionData getPeerConnectionData(String ipAddress) {
				return stack.getStackContext().getPeerData(ipAddress);
			}

			@Override
			public <T> ScheduledFuture<T> scheduleCallableSingleExecutionTask(CallableSingleExecutionAsyncTask<T> task) {
				return stack.getStackContext().scheduleCallableSingleExecutionTask(task);
			}
			
			@Override
			public SecurityStandard getDefaultSecurityStandard() {
				return TCPNetworkConnector.this.geSecurityStandard();
			}

			@Override
			public EliteSSLContextExt createEliteSSLContext() throws Exception {
				EliteSSLContextFactory eliteSSLContextFactory = stack.getStackContext().getEliteSSLContextFactory();
				return eliteSSLContextFactory.createSSLContext(getDefalutSSLParameter());
			}

			
		};
		
		
	}
	
	public boolean start(ConnectionFactory connectionFactory) {
		boundSocketDetail = new SocketDetail(getNetworkAddress(), getNetworkPort());

		this.connectionFactory = connectionFactory;

		if (currentState == ConnectorState.NOT_STARTED || currentState == ConnectorState.STOPPED) {
			currentState = ConnectorState.STARTUP_IN_PROGRESS;

			InetAddress bindAddress = null;
			if (serverSocket == null || serverSocket.isClosed()) {

				try {
					
					bindAddress = InetAddress.getByName(getNetworkAddress());
					SocketAddress socketAddress = new InetSocketAddress(bindAddress, getNetworkPort());
					serverSocket = new ServerSocket();
					serverSocket.bind(socketAddress);
					serverSocket.setSoTimeout(SOCKET_IDLE_TIMEOUT_MS);
					getLogger().info(MODULE, "Server socket ip: " + serverSocket.getLocalSocketAddress());
				} catch (IOException e) {
					if (e instanceof UnknownHostException) {
						
						getLogger().warn(MODULE, "Unknown host address: " + getNetworkAddress() + ", Reason: " + e.getMessage()
								+ ", service will be listening on " + CommonConstants.UNIVERSAL_IP);
					} else {
					
						getLogger().warn(MODULE, "Problem while binding configured service on address: " + getNetworkAddress() + ", Reason: " + e.getMessage()
							+ ", service will be listening on " + CommonConstants.UNIVERSAL_IP);
					}
					
					if (bindServiceOnUniversalIp(getNetworkPort()) == false) {
						
						if (getNetworkPort() != DiameterConstants.DIAMETER_SERVICE_PORT) {
							
							getLogger().warn(MODULE, "Failed to start service on socket: " + CommonConstants.UNIVERSAL_IP + ":" + getNetworkPort() 
									+ ". Reason: " + e.getMessage()
									+  ", service will attempt to listen on socket: " + CommonConstants.UNIVERSAL_IP + ":" + DiameterConstants.DIAMETER_SERVICE_PORT);
							
							if (bindServiceOnUniversalIp(DiameterConstants.DIAMETER_SERVICE_PORT) == false) {
								getLogger().warn(MODULE, "Failed to start service on socket: " + CommonConstants.UNIVERSAL_IP + ":" + DiameterConstants.DIAMETER_SERVICE_PORT 
										+ ". Reason: " + e.getMessage());
								return false;
							} 
						} else {
							
							getLogger().warn(MODULE, "Failed to start service on socket: " + CommonConstants.UNIVERSAL_IP + ":" + DiameterConstants.DIAMETER_SERVICE_PORT 
									+ ". Reason: " + e.getMessage());
							return false;
						}
					}
				}
				try {

					connectionLSNRThread = new Thread(new ConnectionListener());
					connectionLSNRThread.setName(getThreadIdentifier() + "-LIS-THR");
					connectionLSNRThread.setPriority(stack.getMainThreadPriority());
					connectionLSNRThread.start();
					boundSocketDetail = new SocketDetail(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());

				} catch(Exception exp) { 
					remarks = ServiceRemarks.UNKNOWN_PROBLEM;
					currentState = ConnectorState.NOT_STARTED;
					getLogger().error(MODULE,"Failed to start " + getThreadIdentifier() + ". Reason: " + exp.getMessage());
					getLogger().trace(MODULE, exp);
					return false; 
				}
			}
		}
		return true;
	}


	private boolean bindServiceOnUniversalIp(int port) {

		try {
			InetAddress bindAddress = InetAddress.getByName(CommonConstants.UNIVERSAL_IP);

			SocketAddress socketAddress = new InetSocketAddress(bindAddress, port);
			serverSocket = new ServerSocket();
			serverSocket.bind(socketAddress);
			remarks = ServiceRemarks.STARTED_ON_UNIVERSAL_IP;
			serverSocket.setSoTimeout(SOCKET_IDLE_TIMEOUT_MS);
			boundSocketDetail = new SocketDetail(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
		} catch (Exception e) {
			
			remarks = ServiceRemarks.PROBLEM_BINDING_IP_PORT;
			currentState = ConnectorState.NOT_STARTED;
			getLogger().error(MODULE, "Failed to start service on universal IP. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			return false;
		}

		return true;
	}
	
	public class ConnectionListener implements Runnable {
        public void run() {
            currentState = ConnectorState.RUNNING;
            while(!TCPNetworkConnector.this.stopRequested){
            	 ConnectionHandler connectionHandler = null;
            	 Socket clientSocket = null;
                try {
                	clientSocket = serverSocket.accept();
                	Connection connection = connectionFactory.createConnection(clientSocket, ConnectionRole.Responder, context);
                    connectionHandler = new ConnectionHandler(connection, connectionFactory, ConnectionRole.Responder, context);
                    Thread connectionHLThread = new EliteThread(connectionHandler,getThreadIdentifier() + "-HL-"+clientSocket.getInetAddress().getHostAddress()+":"+clientSocket.getPort(),com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants.DIAMETER_STACK_IDENTIFIER);
                    connectionHLThread.setPriority(stack.getMainThreadPriority());
                    connectionHLThread.start();	
                    totalConnectionHandlers.add(connectionHandler);
                    if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
    					LogManager.getLogger().debug(MODULE, "Successfully Added new connection handler: " + connectionHandler.getSourceIpAddress() + ":" + connectionHandler.getSourcePort());
    					LogManager.getLogger().debug(MODULE, "Total connection handlers are: " + totalConnectionHandlers.size());
    				}
                }catch (SocketTimeoutException socketExp) {
                	//useful when direct TLS is started
                	if(clientSocket !=  null){
                		try{clientSocket.close();}catch(IOException ex){LogManager.getLogger().trace(MODULE, ex);}
                	}
                	
                    if (TCPNetworkConnector.this.stopRequested) {
                    	if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "Stop request for TCPNetworkConnector, stopping accept new connections request.");
                    }
                    ignoreTrace(socketExp);
                }catch (SocketException socketExp) {
                	//useful when direct TLS is started
                	if(clientSocket !=  null){
                		try{clientSocket.close();}catch(IOException ex){LogManager.getLogger().trace(MODULE, ex);}
                	}
                	if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, socketExp.getMessage());
                }catch (Exception e) {
                	//useful when direct TLS is started
                	if(clientSocket !=  null){
                		try{clientSocket.close();}catch(IOException ex){LogManager.getLogger().trace(MODULE, ex);}
                	}
                	if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, e.getMessage());
                	LogManager.getLogger().trace(MODULE, e);
                }
            }
            currentState = ConnectorState.STOPPED;
        }
        
        
	}
	
	public void openConnection(IPeerListener peerListener) {
		ConnectionSender connectionSender = new ConnectionSender(peerListener);
        Thread connectionSendThread = new EliteThread(connectionSender, getThreadIdentifier() + "-SEND-THR",com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants.DIAMETER_STACK_IDENTIFIER);
        connectionSendThread.setPriority(stack.getMainThreadPriority());
        connectionSendThread.start();	
	}
	
	public class ConnectionSender implements Runnable {

		private IPeerListener peerListener;
		
		public ConnectionSender(IPeerListener peerListener) {
			this.peerListener = peerListener;
		}

		@Override
		public void run() {
			ConnectionEvents connectionEvent = ConnectionEvents.CONNECTION_FAILURE;
			DiameterPeerEvent peerEvent = DiameterPeerEvent.IRcvConnNack;
			ConnectionHandler connectionHandler = null;
			Socket clientSocket = null;
			try {
				
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					String logMessageToAppend =  peerListener.getLocalIpAddress() == null ? "" : ", with local address: " + peerListener.getLocalIpAddress() + ":" + peerListener.getLocalPort(); 
					LogManager.getLogger().info(MODULE, "Attempting connection to " + peerListener.getRemoteInetAddress()+ ":" + peerListener.getCommunicationPort() + logMessageToAppend);
				}
				
				InetAddress localInetAddress = null;
				try {
					
					localInetAddress = resolveLocalAddress();
					
				} catch (UnknownHostException e) {
						if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
							LogManager.getLogger().error(MODULE, "Invalid local address: " + e.getMessage() + " for connection: " + peerListener.getRemoteInetAddress()+ ":" + peerListener.getCommunicationPort());
						
						LogManager.getLogger().trace(MODULE, e);
						
						if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "ConnectionEvent: " + ConnectionEvents.CONNECTION_FAILURE + 
							" with Peer Event: " + peerEvent + " for Peer: " + peerListener.getPeerName());
						
						peerListener.handleEvent(peerEvent, ConnectionEvents.CONNECTION_FAILURE);
					
						return;
				}
				
				
				clientSocket = SocketFactory.getDefault().createSocket();
				clientSocket.bind(new InetSocketAddress(localInetAddress, peerListener.getLocalPort()));
				clientSocket.connect(new InetSocketAddress(peerListener.getRemoteInetAddress(), peerListener.getCommunicationPort()), CONNECTION_TIMEOUT_MS);
				
				
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Successfully connected to " + peerListener.getRemoteInetAddress()+ ":" + peerListener.getCommunicationPort() + ", with local Address: " 
   							+ clientSocket.getLocalAddress()  + ":" + clientSocket.getLocalPort());
							
				Connection connection = connectionFactory.createConnection(clientSocket, ConnectionRole.Initiator, context);
				
				connectionHandler = new ConnectionHandler(connection, connectionFactory, ConnectionRole.Initiator, context);
				Thread connectionHLThread = new EliteThread(connectionHandler,getThreadIdentifier() + "-HL-"+clientSocket.getInetAddress().getHostAddress()+":"+clientSocket.getPort(), com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants.DIAMETER_STACK_IDENTIFIER);
				connectionHLThread.setPriority(stack.getMainThreadPriority());
				connectionHLThread.start();	
				
				peerListener.setConnectionListener(connectionHandler);
				totalConnectionHandlers.add(connectionHandler);
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Successfully Added new connection handler: " + connectionHandler.getSourceIpAddress() + ":" + connectionHandler.getSourcePort() + " Local Address: " + clientSocket.getLocalAddress()+ ":"+clientSocket.getLocalPort());
					LogManager.getLogger().debug(MODULE, "Total connection handlers are: " + totalConnectionHandlers.size());
				}
			
				connectionEvent = ConnectionEvents.CONNECTION_CREATED;
				peerEvent = DiameterPeerEvent.IRcvConnAck;
				
			} catch (IOException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, e.getMessage() + " for connection: " + peerListener.getRemoteInetAddress()+ ":" + peerListener.getCommunicationPort());
				LogManager.getLogger().trace(MODULE, e);
					try {
						if (clientSocket != null && !clientSocket.isClosed()) {
							clientSocket.close();
							clientSocket = null;
						}
					} catch (Exception exc){ 
						ignoreTrace(exc);
					}
			} catch(Exception ex){
				//useful when direct TLS is started
				try {
					if(clientSocket != null){
						clientSocket.close();
						clientSocket = null;
					}
					
				} catch (IOException e) {LogManager.getLogger().trace(MODULE, e);} 
				LogManager.getLogger().trace(MODULE, ex);
			}finally{
				
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "ConnectionEvent: " + connectionEvent + 
					" with Peer Event: " + peerEvent + " for Peer: " + peerListener.getPeerName());
				peerListener.handleEvent(peerEvent, connectionEvent);
			}
			
		}
		
		private InetAddress resolveLocalAddress() throws UnknownHostException {
			InetAddress localInetAddress = peerListener.getLocalIpAddress() == null ? null : InetAddress.getByName(peerListener.getLocalIpAddress()) ;
			return localInetAddress;
		}
	}
	
	public String getNetworkAddress() {
		return "127.0.0.1";
	}
	
	@Override
	public SocketDetail getBondSocketDetail() {
		return boundSocketDetail;
	}
	
	@Override
	public ServiceRemarks getRemarks() {
		return remarks;
	}
	
	public int getNetworkPort() {
		return DiameterConstants.DIAMETER_SERVICE_PORT;
	}
	
	protected abstract int getSocketReceiveBufferSize();
	protected abstract int getSocketSendBufferSize();
	protected abstract String getThreadIdentifier();


	public boolean stop() {
		
		if(stopRequested == true){
			LogManager.getLogger().debug(MODULE, "Shutdown in progress");
			return true;
		}
		
		this.stopRequested = true;
		
		
		try{
			serverSocket.close();
		}catch(Exception ex){
			LogManager.getLogger().trace(MODULE, ex);
		}
		
		ConnectionHandler[] connectionHandlers = totalConnectionHandlers.toArray(new ConnectionHandler[totalConnectionHandlers.size()]);
		for (ConnectionHandler connHandler : connectionHandlers){
			try{
				connHandler.closeConnection(ConnectionEvents.SHUTDOWN);
			}catch(Exception ex){
				LogManager.getLogger().trace(MODULE, ex);
			}
			
		}
		
		return true;
	}
	
	@Override
	public TransportProtocols getTransportProtocol() {
		return TransportProtocols.TCP;
	}
}
