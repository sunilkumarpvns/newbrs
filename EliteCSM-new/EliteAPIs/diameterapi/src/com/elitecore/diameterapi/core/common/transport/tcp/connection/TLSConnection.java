package com.elitecore.diameterapi.core.common.transport.tcp.connection;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.core.commons.tls.constant.TLSConnectionMode;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.CallableSingleExecutionAsyncTask;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.transport.Connection;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectorContext;
import com.elitecore.diameterapi.core.common.transport.tcp.DiameterInputStream;
import com.elitecore.diameterapi.core.common.transport.tcp.exception.HandShakeFailException;
import com.elitecore.diameterapi.mibs.constants.SecurityProtocol;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class TLSConnection implements Connection{
	
	private static final String MODULE = "TLS-CONN";
	public static final int SUCCESS = 1;
	public static final int FAILUER = 2;
	private TCPConnection tcpConnection;
	private SSLSocket sslSocket;
    private DiameterInputStream inStream ;
    private OutputStream outStream;
    private ConnectorContext context;
    private AtomicBoolean connected = new AtomicBoolean();
    private boolean isHandShakeComplete = false;
    private TLSConnectionMode tlsMode;
    private EliteSSLContextExt eliteSSLContext;
    
    public TLSConnection(ConnectorContext context, TCPConnection connection, TLSConnectionMode tlsMode, EliteSSLContextExt eliteSSLContext){
    	this.context = context;
    	this.tcpConnection = connection;
    	this.tlsMode = tlsMode;
    	this.eliteSSLContext = eliteSSLContext;
    }
    
    
    public synchronized void startHandshake() throws HandShakeFailException{
    	if(!isHandShakeComplete){
    		if(TLSConnectionMode.CLIENT == tlsMode){
    			wrapSocketAsClient();
    		} else {
    			wrapSocketAsServer();
    		}
        	connected.set(true);
        	isHandShakeComplete = true;
    	}
    }
    
	
	
	
	private boolean wrapSocketAsClient() throws HandShakeFailException{
		try{
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Fetch peer connection data based on IP Address = " +  tcpConnection.getSocket().getInetAddress().getHostAddress());
			

			
			if(eliteSSLContext == null){
				throw new HandShakeFailException("SSLContext not found for IP-Address = " + tcpConnection.getSocket().getInetAddress().getHostAddress());
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Start creating TLS Socket as client");
			
			
			SSLSocketFactory sslFactory =  eliteSSLContext.getSSLSocketFactory();
			
			
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Create TLS Socket on IP Address = " + context.getNetworkAddress() +":"+context.getNetworkPort());
			
			sslSocket = (SSLSocket) sslFactory.createSocket(tcpConnection.getSocket(), tcpConnection.getClientAddress(), tcpConnection.getClientPort(), true);
			
			
			//set cipher suites
			 List<String> enabledCipherLst = eliteSSLContext.getEnabledCiphersuites();
			 
			 if(enabledCipherLst == null || enabledCipherLst.isEmpty()){
				  throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + tcpConnection.getClientAddress() 
						  + ". Reason: Enabled CipherSuite Not Configured for TLSVersion: " + eliteSSLContext.getEliteSSLParameter().getMaxTlsVersion());
			  }
			  
			 String [] strAry = new String [enabledCipherLst.size()];
			 
			 strAry = enabledCipherLst.toArray(strAry);
			 sslSocket.setEnabledCipherSuites(strAry); 
			  if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Enabled Cipher Cuites : "+ Arrays.toString(sslSocket.getEnabledCipherSuites()));
			  
			  
			//set enabled protocol
			  List<TLSVersion> supportedVersionList = eliteSSLContext.getEnabledTLSVersion();
			  
			  if(supportedVersionList == null || supportedVersionList.isEmpty()){
				  throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + tcpConnection.getClientAddress() 
						  + ". Reason: Min-Max TLS Version not properly configured");
			  }
			  
			 
			  String [] supportedTLSVersions = new String [supportedVersionList.size()];
			  
			  for(int i=0; i < supportedVersionList.size(); i++){
				supportedTLSVersions[i]=supportedVersionList.get(i).version;  
			  }
			  
			  sslSocket.setEnabledProtocols(supportedTLSVersions);
			  
			  if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Supported TLS Version: "+ Arrays.toString(sslSocket.getEnabledProtocols()));
			
			inStream =  new DiameterInputStream(sslSocket.getInputStream());
			outStream = new DataOutputStream(new BufferedOutputStream(sslSocket.getOutputStream()));
			
			sslSocket.addHandshakeCompletedListener(event -> {
                //Stack.generateAlert(StackAlertSeverity.INFO,  DiameterStackAlerts.DIAMETER_HANDSHAKE_SUCCESS, MODULE, "Handshake success of TLS Client on IP : " + tcpConnection.getClientAddress());
                if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
                    LogManager.getLogger().info(MODULE, "Handshake success for TLS Client on IP : " + tcpConnection.getClientAddress());
            });
			
			long timeout = DEFAULT_TIMEOUT_IN_MS;
			if(eliteSSLContext != null && eliteSSLContext.getEliteSSLParameter().getHandshakeTimeout() > 0){
				timeout = eliteSSLContext.getEliteSSLParameter().getHandshakeTimeout();
				
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Wait for "+ timeout + "ms to complete handshake");
			
			ScheduledFuture<Integer> futurTask = this.context.scheduleCallableSingleExecutionTask(new HandshakeTask(sslSocket));
			if(FAILUER == futurTask.get(timeout, TimeUnit.MILLISECONDS)){
				throw new HandShakeFailException("HandShake Fail");			
			}
			
		}  catch (ExecutionException e) {
			//Stack.generateAlert(StackAlertSeverity.ERROR,  DiameterStackAlerts.DIAMETER_HANDSHAKE_FAIL, MODULE, "Handshake fail of TLS Client on IP : " + tcpConnection.getClientAddress() + ".Reason: " +  e.getMessage());
			throw new HandShakeFailException("HandShake fail  for TLS Client on IP : " + tcpConnection.getClientAddress() + " . Reason: " + e.getMessage(), e);
		} catch (TimeoutException e) {
			//Stack.generateAlert(StackAlertSeverity.ERROR,  DiameterStackAlerts.DIAMETER_HANDSHAKE_FAIL, MODULE, "Handshake fail of TLS Client on IP : " + tcpConnection.getClientAddress() + ".Reason: " +  e.getMessage());
			throw new HandShakeFailException("Handshake time exceeded  for TLS Client on IP : " + tcpConnection.getClientAddress() +". Reason: " + e.getMessage(), e);
		} catch (Exception e) {
			//Stack.generateAlert(StackAlertSeverity.ERROR,  DiameterStackAlerts.DIAMETER_HANDSHAKE_FAIL, MODULE, "Handshake fail of TLS Client on IP : " + tcpConnection.getClientAddress() + ".Reason: " +  e.getMessage());
			throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + tcpConnection.getClientAddress() +". Reason: " + e.getMessage(), e);
		}
		
		return false;
		
			
	}
	
	private class HandshakeTask  implements CallableSingleExecutionAsyncTask<Integer>{
		
		private SSLSocket socket;

		public HandshakeTask(SSLSocket socket) {
			this.socket = socket;
		}

		@Override
		public long getInitialDelay() {
			return 0;
		}

		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.SECONDS;
		}

		@Override
		public Integer execute(AsyncTaskContext context) {
			try{
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Start Handshaking for TLS Client on IP : " + tcpConnection.getClientAddress());
				socket.startHandshake();
				return SUCCESS;
			} catch (IOException ex){
				LogManager.getLogger().trace(MODULE, ex);
				//Stack.generateAlert(StackAlertSeverity.ERROR, DiameterStackAlerts.DIAMETER_HANDSHAKE_FAIL, MODULE, "Handshake fail of TLS Client on IP : " + tcpConnection.getClientAddress() + ".Reason: " +  ex.getMessage());
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Handshake fail for TLS Client on IP : " + tcpConnection.getClientAddress() + ", reason : " + ex.getMessage());
                LogManager.getLogger().trace(MODULE, ex);
				return FAILUER;
			}
		}
		
	}
	
	
	private boolean wrapSocketAsServer() throws HandShakeFailException{
		
		try{
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Fetching peer connection data based on IP Address = " +  tcpConnection.getClientAddress());
						

			
			if(eliteSSLContext == null){
				throw new HandShakeFailException("SSLContext not found for IP-Address = " + tcpConnection.getClientAddress());
			}
			
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Start creating TLS Socket as server");
			
			
			
			  SSLSocketFactory sslFactory =  eliteSSLContext.getSSLSocketFactory();
			  
			  
			  if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Create TLS Socket on IP Address = " + context.getNetworkAddress() +":"+context.getNetworkPort());
			  
			  sslSocket = (SSLSocket)sslFactory.createSocket(tcpConnection.getSocket(), context.getNetworkAddress(), context.getNetworkPort(),true);
			  sslSocket.setUseClientMode(false);
			  
			  if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Client aurhentication set to :" + eliteSSLContext.isClientCertificateRequestRequired());
			  
			  if(eliteSSLContext.getEliteSSLParameter().isClientCertificateRequestRequired()){
				  if(eliteSSLContext.getEliteSSLParameter().isValidateCertificateCA()){
					  sslSocket.setNeedClientAuth(false);
				  } else{
					  sslSocket.setWantClientAuth(false); 
				  }
			  }
			  
			  
			  
			  //set cipher suites
			  List<String> enabledCipherLst = eliteSSLContext.getEnabledCiphersuites();
			  
			  if(enabledCipherLst == null || enabledCipherLst.isEmpty()){
				  throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + tcpConnection.getClientAddress() 
						  + ". Reason: Enabled CipherSuite Not Configured for TLSVersion: " + eliteSSLContext.getEliteSSLParameter().getMaxTlsVersion());
			  }
			  
			  String [] strAry = new String [enabledCipherLst.size()];
			  
			  strAry = enabledCipherLst.toArray(strAry);
			  
			  sslSocket.setEnabledCipherSuites(strAry);
			  
			  if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Enabled Cipher Cuites : "+ Arrays.toString(sslSocket.getEnabledCipherSuites()));
			  
			  
			//set enabled protocol
			  List<TLSVersion> supportedVersionList = eliteSSLContext.getEnabledTLSVersion();
			  
			  if(supportedVersionList == null || supportedVersionList.isEmpty()){
				  throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + tcpConnection.getClientAddress() 
						  + ". Reason: Min-Max TLS Version not properly configured");
			  }
			  
			 
			  String [] supportedTLSVersions = new String [supportedVersionList.size()];
			  
			  for(int i=0; i < supportedVersionList.size(); i++){
				supportedTLSVersions[i]=supportedVersionList.get(i).version;  
			  }
			  
			  
			  
			  sslSocket.setEnabledProtocols(supportedTLSVersions);
			  
			  if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Suppoted TLS Version: "+ Arrays.toString(sslSocket.getEnabledProtocols()));
			  
			  
			  
			  inStream =  new DiameterInputStream(sslSocket.getInputStream());
			  outStream = new DataOutputStream(new BufferedOutputStream(sslSocket.getOutputStream()));
			  
			  
			  
			  sslSocket.addHandshakeCompletedListener(arg0 -> {
//					Stack.generateAlert(StackAlertSeverity.INFO,  DiameterStackAlerts.DIAMETER_HANDSHAKE_SUCCESS, MODULE, "Handshake success of TLS Server on IP : " + tcpConnection.getClientAddress());
                  if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
                      LogManager.getLogger().info(MODULE, "Handshake Completed for TLS Server on IP-Address : " + tcpConnection.getClientAddress());
              });
			 
			  sslSocket.startHandshake();
			  if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "TLS Socket created on IP Address = " + context.getNetworkAddress() +":"+context.getNetworkPort());
			  
			 return true;
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
//			Stack.generateAlert(StackAlertSeverity.ERROR,  DiameterStackAlerts.DIAMETER_HANDSHAKE_FAIL, MODULE, "Handshake fail of TLS Server on IP : " + tcpConnection.getClientAddress() + ".Reason: " +  e.getMessage());
			throw new HandShakeFailException("HandShake fail for TLS Client on IP : " + tcpConnection.getClientAddress() + ". Reason: " + e.getMessage(), e);
		}			
	}
	
	@Override
	public String getSourceIpAddress() {
		return tcpConnection.getSourceIpAddress();
	}

	@Override
	public int getSourcePort() {
		return tcpConnection.getSourcePort();
	}

	@Override
	public String getLocalAddress() {
		return tcpConnection.getLocalAddress();
	}
	
	@Override
	public int hashCode() {
		return tcpConnection.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) {
			return false;
		}
		
		try{
			TLSConnection tlsConnection = (TLSConnection) obj;
			return tcpConnection.equals(tlsConnection.tcpConnection);
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error during comparing TLS Connection. Reason:" + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
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
	public String getClientAddress() {
		return tcpConnection.getClientAddress();
	}

	@Override
	public int getClientPort() {
		return tcpConnection.getClientPort();
	}

	@Override
	public DiameterInputStream getInputStream() {
		return inStream;
	}
	
	@Override
	public boolean isClosed() {
		return sslSocket.isClosed();
	}


	@Override
	public boolean isInputShutdown() {
		return sslSocket.isInputShutdown();
	}

	@Override
	public boolean isOutputShutdown() {
		return sslSocket.isOutputShutdown();
	}

	@Override
	public void  closeConnection() {
		
		if (connected.compareAndSet(true, false) == false) {
			return;
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
			LogManager.getLogger().warn(MODULE, "Closing Connection to " + tcpConnection.getClientAddress() + "/" + tcpConnection.getClientPort());
		
		try{
			if(inStream != null) {
				inStream.close();
			}
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while closing input stream for peer: " + this.sslSocket.getInetAddress() + ", reason : " + e);
			LogManager.getLogger().trace(MODULE, e);
		}
		try{
			if(outStream!= null) {
				outStream.close();
			}
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while closing output stream for peer: " + this.sslSocket.getInetAddress() + ", reason : " + e);
			LogManager.getLogger().trace(MODULE, e);
		}


		try {
			if(sslSocket != null && !sslSocket.isClosed()){
				sslSocket.close();
			}
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().error(MODULE, "Error while closing ssl socket for peer: " + this.sslSocket.getInetAddress() + ", reason : " + e);
		}

	}

	@Override
	public void read(byte[] data, int off, int len) {
		//read not supported
	}
	
	public boolean isClient(){
		return this.tlsMode == TLSConnectionMode.CLIENT;
	}
	
	public boolean isHandshakeCompleted(){
		return isHandShakeComplete;
	}
	
	public EliteSSLContextExt getEliteSSLContext(){
		return eliteSSLContext;
	}


	@Override
	public int getLocalPort() {
		return tcpConnection.getLocalPort();
	}


	@Override
	public SecurityProtocol getSecurityProtocol() {
		return SecurityProtocol.TLS;
	}


	@Override
	public InetAddress getSourceInetAddress() {
		return tcpConnection.getSourceInetAddress();
	}
}
