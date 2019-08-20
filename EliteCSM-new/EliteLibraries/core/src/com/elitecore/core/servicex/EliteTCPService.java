package com.elitecore.core.servicex;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.packet.IPacket;
import com.elitecore.core.commons.packet.PacketFactory;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.base.BaseEliteService;

public abstract class EliteTCPService extends BaseEliteService 
implements UniSocketService<TCPServiceRequest, TCPServiceResponse> {
	private static final String MODULE = "TCP-SERVICE";
	private ServerSocket serverSocket = null;
	private Thread mainThread;
	private static final int SOCKET_IDLE_TIMEOUT_MS=2000;
	
	private String listeningServiceAddress;
	private int listeningServicePort;
	
	private ThreadPoolExecutor subProcessThreadExecutor;
    private LinkedBlockingQueue <Runnable> subProcessThreadTaskQueue;
	
    public EliteTCPService(ServerContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void initService() throws ServiceInitializationException {
		LogManager.getLogger().debug(MODULE,"Entered into the init method of TCP Service.");
		listeningServiceAddress = getSocketDetail().getIPAddress();
        listeningServicePort = getSocketDetail().getPort();
		subProcessThreadTaskQueue = new LinkedBlockingQueue<Runnable>(getMaxRequestQueueSize());
        subProcessThreadExecutor = new ThreadPoolExecutor(getMinThreadPoolSize(), getMaxThreadPoolSize(), getThreadKeepAliveTime(), TimeUnit.MILLISECONDS, subProcessThreadTaskQueue);
        subProcessThreadExecutor.setThreadFactory(new EliteThreadFactory(getThreadIdentifier(), getThreadIdentifier()+"-THR",getWorkerThreadPriority()));
        subProcessThreadExecutor.prestartAllCoreThreads();
	}
	@Override
	protected synchronized boolean startService() {
		LogManager.getLogger().info(MODULE, "Starting service " + getServiceIdentifier());
		
		InetAddress bindAddress = null;
        int port;
        if (serverSocket == null || serverSocket.isClosed() ) {

       		try {
				bindAddress = InetAddress.getByName(getSocketDetail().getIPAddress());
			} catch (UnknownHostException e) {
            	LogManager.getLogger().warn(MODULE, "Problem binding configured service address: " + getSocketDetail().getIPAddress() + ", reason: " + e.getMessage() + ", service will be listening on 0.0.0.0");
            	try {
					bindAddress = InetAddress.getByName("0.0.0.0");
				} catch (UnknownHostException e1) {
					// nothing to do
				}
			}

            try {
            	port = getSocketDetail().getPort();
            	SocketAddress socketAddress = new InetSocketAddress(bindAddress,port);
                serverSocket = new ServerSocket();
                serverSocket.bind(socketAddress);
                serverSocket.setSoTimeout(SOCKET_IDLE_TIMEOUT_MS);

                mainThread = new Thread(new ConnectionListener());		
	            mainThread.setName(getThreadIdentifier()+"LIS-"+getMainThreadPriority());
	            mainThread.setPriority(getMainThreadPriority());
	            mainThread.start();	
	            listeningServiceAddress = serverSocket.getInetAddress().getHostAddress();
	            listeningServicePort = serverSocket.getLocalPort();
	            return true;
                
            }catch (IOException ioExp) {
                LogManager.getLogger().error(MODULE, ioExp.getMessage());
                LogManager.getLogger().trace(ioExp);
                return false;
            }catch(Exception exp) {
            	LogManager.getLogger().error(MODULE, exp.getMessage());
            	LogManager.getLogger().trace(exp);
            	return false;
            }

        }else{
        	LogManager.getLogger().warn(MODULE, "Ignoring the start request received for already running Diameter service.");
        }
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean stopService() {
		LogManager.getLogger().info(MODULE, "Stop flag set for " + getServiceIdentifier());
		return true;
	}
	
	private String getListeningSocketAddress() {
		return listeningServiceAddress + ":" + String.valueOf(listeningServicePort);
	}
	
	@Override
	public ServiceDescription getDescription() {
		return new ServiceDescription(getServiceIdentifier(), getStatus(), 
				getListeningSocketAddress(), getStartDate(), getRemarks());
	}
		
	@Override
	protected void shutdownService() {
		LogManager.getLogger().info(MODULE, "Final shutdown process started for " + getServiceIdentifier());
		LogManager.getLogger().info(MODULE, getServiceIdentifier() + " - stopping worker threads");
		subProcessThreadExecutor.shutdown();
	}

	protected abstract int getMinThreadPoolSize();
	protected abstract int getMaxThreadPoolSize();
	protected abstract int getMainThreadPriority();
	protected abstract int getWorkerThreadPriority();
	protected abstract int getThreadKeepAliveTime();
	protected abstract int getMaxRequestQueueSize();
	protected abstract int getSocketReceiveBufferSize();
	protected abstract int getSocketSendBufferSize();
	
	
	
	public class ConnectionListener implements Runnable {
        public void run() {
            while(!isStopRequested()){
                try {
                	Socket clientSocket = serverSocket.accept();
                    LogManager.getLogger().debug(MODULE, "New connection request received. Address ::"+clientSocket.getInetAddress()+" Local port :: "+clientSocket.getLocalPort()+", Remote Port :: "+clientSocket.getPort());
                    ConnectionHandler connectionHandler = createConnectionHandler(clientSocket);
                    new Thread(connectionHandler).start();        
                    
                }catch (SocketTimeoutException socketExp) {
                    if (isStopRequested()) {
                    	LogManager.getLogger().debug(MODULE, "Stop service requested for TCPIPService, stopping accept new connections request.");
                    }
                }catch (SocketException socketExp) {
                    if (isStopRequested()) {
                    	LogManager.getLogger().debug(MODULE, "Stop service requested for TCPIPService, stopping accept new connections request.");
                    }else {                    	
                    	LogManager.getLogger().error(MODULE, "Error while accepting connection, retrying, error is : " + socketExp);
                        LogManager.getLogger().trace(MODULE,socketExp);
                    }
                }catch (Exception e) {
                	LogManager.getLogger().error(MODULE, "Error while waiting for connection request, reason " + e);
                    LogManager.getLogger().trace(MODULE,e);
                }
            }
            LogManager.getLogger().debug(MODULE,"Stopped accepting connections.");
        }
        
	}
	
	protected ConnectionHandler createConnectionHandler(Socket clientSocket) {
		return new ConnectionHandler(clientSocket);
	}
	
	public class ConnectionHandler implements Runnable,IConnectionContext{
		protected Socket clientSocket;
        protected InputStream inStream ;
        protected OutputStream outStream;
        TCPServiceRequest serviceRequest;
    	
		public ConnectionHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;            
            try {
            	if(clientSocket != null){
            		inStream = new DataInputStream(new BufferedInputStream(this.clientSocket.getInputStream()));
    				outStream = new DataOutputStream(new BufferedOutputStream(this.clientSocket.getOutputStream()));
    				this.clientSocket.setSendBufferSize(65536);
    				this.clientSocket.setReceiveBufferSize(65536);    				
            	}            	
			} catch (IOException e) {
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		public Socket getClientSocket(){
			return clientSocket;
		}
        public void sendResponse(IPacket packet) throws IOException {
        	try{
        		LogManager.getLogger().trace(MODULE,"Sending Packet to :: "+clientSocket.getInetAddress().getHostAddress());        		
        		
                packet.writeTo(outStream);
                outStream.flush();  
                
        	}catch (IOException e) {
        		LogManager.getLogger().error(MODULE,"Connection Socket Closed :: Remote Port: "+clientSocket.getPort()+" Local Port :: "+clientSocket.getLocalPort());
        		LogManager.getLogger().trace(MODULE,e);
        		throw e;
			}catch (Exception e) {
        		LogManager.getLogger().trace(MODULE,e);
			}
        }

        public void sendResponse(byte[] packetBytes) throws IOException {
        	try{
        		if(outStream == null){
        			LogManager.getLogger().error(MODULE, "Error in sending packet bytes to client " + getSocketDetail().getIPAddress() + ", Reason: Output stream is null.");
        			return;
        		}
                outStream.write(packetBytes);
                outStream.flush();                
        	}catch (IOException e) {
        		LogManager.getLogger().error(MODULE,"Connection Socket Closed :: Remote Port: "+clientSocket.getPort()+" Local Port :: "+clientSocket.getLocalPort());
        		LogManager.getLogger().trace(MODULE,e);
        		throw e;
			}catch (Exception e) {
        		LogManager.getLogger().trace(MODULE,e);
			}
        }

        public void closeConnection() {
			
        	try{
        		inStream.close();
        	}catch (IOException e) {
        		LogManager.getLogger().trace(MODULE, e);
        	}
        	try{
        		outStream.close();
        	}catch (IOException e) {
        		LogManager.getLogger().trace(MODULE, e);
        	}

        	try {
        		if(!clientSocket.isClosed()){
        			clientSocket.close();
        		}
        	} catch (IOException e) {
        		LogManager.getLogger().trace(MODULE,e);
        	}
        }
        public void run() { 
        	/*
        	 * 
        	 	Pre service plugin....
        	*/
            try {
                this.clientSocket.setTcpNoDelay(false);
                IPacket packet = getPacket();
                byte[] packetBytes;
                PacketFactory packetFactory=getPacketFactory();
                while((packetBytes=packetFactory.createPacketBytes(inStream))!=null){
                        LogManager.getLogger().debug(MODULE, "Packet obtained from stream,assiging task to process packet.");                    
                        long startTime = System.currentTimeMillis();
                    try {
                    	 	serviceRequest = formServiceSpecificRequest(getClientAddress(), getClientPort(), packetBytes);
                    	 	handleServiceRequest(serviceRequest, null);
                            subProcessThreadExecutor.execute(new PacketProcess(packet, this));                            
                    }catch(Exception exp){
                    	LogManager.getLogger().error(MODULE, "Error in handle request of " + getName() + " service, reason : " + exp);
                    	LogManager.getLogger().trace(MODULE, exp);
                    }	
                    
                    LogManager.getLogger().debug(MODULE,"Time Taken To Handle the Request is :"+(System.currentTimeMillis() - startTime));                    
                    packet = getPacket();
                    
                }
            }catch (IOException ioExp) {
            	LogManager.getLogger().error(MODULE, "I/O Error handling client " + this.clientSocket.getInetAddress() + ", reason : " + ioExp);
                LogManager.getLogger().trace(MODULE,ioExp);
            }catch (Exception e){            	
            	LogManager.getLogger().error(MODULE, "General error handling client " + this.clientSocket.getInetAddress() + ", reason : " + e);
                LogManager.getLogger().trace(MODULE,e);
            }
            LogManager.getLogger().debug(MODULE, "Connection Stream Reading stop");
            
            // Neeed to generate Closed event.....
            
            /*TODO:   Post Plugin implementation..
             *
            	if(isValidatedConnection) {
            		postService(this);
            	}            
            */
            
            try{
        		inStream.close();
        	}catch (IOException e) {
        		LogManager.getLogger().trace(MODULE, e);
        	}
        	try{
        		outStream.close();
        	}catch (IOException e) {
        		LogManager.getLogger().trace(MODULE, e);
        	}

        	try {
        		if(!clientSocket.isClosed()){
        			clientSocket.close();
        		}
        	} catch (IOException e) {
        		LogManager.getLogger().trace(MODULE,e);
        		LogManager.getLogger().error(MODULE, "I/O Error handling client " + this.clientSocket.getInetAddress() + ", reason : " + e);
        	}            
        }

		private int getClientPort() {			
			return clientSocket.getPort();
		}
		private InetAddress getClientAddress() {			
			return clientSocket.getInetAddress();
		}
		@Override
		public boolean getDisconnectionState() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String getHostAddress() {			
			return clientSocket.getInetAddress().toString();
		}

		@Override
		public Object getObject(Object objectKey) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setDisconnectionState(boolean bClosedConnection) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setObject(Object objectKey, Object objectValue) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public class PacketProcess implements Runnable{
		private IPacket packet;
		private IConnectionContext connectionContext;
		public PacketProcess(IPacket packet, IConnectionContext connectionContext){
			this.packet = packet;
			this.connectionContext = connectionContext;
		}

		public void run() {                   
			handleRequest(packet, connectionContext);
		}
	}        
	
	public abstract void handleServiceRequest(TCPServiceRequest request, TCPServiceResponse response);
	public abstract String getThreadIdentifier();
	public abstract String getName();        
	public abstract IPacket getPacket();
	public abstract void handleRequest(IPacket packet, IConnectionContext connectionContext);
	protected abstract PacketFactory getPacketFactory();
}

