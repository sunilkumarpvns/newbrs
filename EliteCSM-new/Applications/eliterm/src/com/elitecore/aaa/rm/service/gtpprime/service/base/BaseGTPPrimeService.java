/**
 * 
 */
package com.elitecore.aaa.rm.service.gtpprime.service.base;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.conf.RMServerConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientData;
import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeServiceCounterListener;
import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeServiceCounters;
import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeServiceRequest;
import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeServiceResponse;
import com.elitecore.aaa.rm.service.gtpprime.util.cli.GTPPrimeServCommand;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.servicex.EliteUDPService;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.servicex.UDPServiceRequest;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.coregtp.commons.elements.BaseGTPPrimeElement;
import com.elitecore.coregtp.commons.elements.IpAddress;
import com.elitecore.coregtp.commons.packet.GTPPrimePacketV1;
import com.elitecore.coregtp.commons.packet.ParseException;
import com.elitecore.coregtp.commons.util.constants.GTPPrimeConstants;
import com.elitecore.coregtp.commons.util.constants.GTPPrimeElementTypeConstants;

/**
 * @author dhaval.jobanputra
 *
 */
public abstract class BaseGTPPrimeService extends EliteUDPService<GTPPrimeServiceRequest, GTPPrimeServiceResponse> {

	private static final String MODULE = "Base-GTP'";
	protected AAAServerContext context;
	private GTPPrimeServiceCounterListener counterListener;

	public BaseGTPPrimeService(ServerContext serverContext) {
		super(serverContext);
		this.context = (AAAServerContext) serverContext;
		counterListener = new GTPPrimeServiceCounterListener(new GTPPrimeServiceCounters(context));
	}

	protected void initService() throws ServiceInitializationException{
		super.initService();

		if (((RMServerConfiguration)((AAAServerContext)getServerContext()).getServerConfiguration()).getGTPPrimeConfiguration().getClientList() != null){
			counterListener.init();
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counters initialized.");
			}
		} else{
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Can not initialize counters for clients. Client configuration is null");
			}
		}
	}

	@Override
	public List<ICommand> getCliCommands() {	
		List<ICommand> cmdList = new ArrayList<ICommand>();
		cmdList.add(new GTPPrimeServCommand());
		return cmdList;
	}

	protected abstract int getMainThreadPriority();
	protected abstract int getMaxRequestQueueSize();
	protected abstract int getMaxThreadPoolSize();
	protected abstract int getMinThreadPoolSize();
	protected abstract int getSocketReceiveBufferSize();
	protected abstract int getSocketSendBufferSize();
	protected abstract int getThreadKeepAliveTime();
	protected abstract int getWorkerThreadPriority();

	public abstract void handleServiceRequest(GTPPrimeServiceRequest request,GTPPrimeServiceResponse response);

	public abstract String getKey();
	protected abstract ServiceContext getServiceContext();
	public abstract void readConfiguration() throws LoadConfigurationException ;
	public abstract boolean reloadServiceConfiguration();
	public abstract String getServiceIdentifier();

	protected void incrementInvalidClientRequestCounter (){
		counterListener.listenGTPPrimeTotalInvalidClientRequest();
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Counter incremented for Invalid client request");
		}
	}

	protected void incrementMalformedRequestPacketCounter(UDPServiceRequest request) {
		counterListener.listenGTPPrimeMalformedRequestPacketCounter(request.getSourceAddress().getHostAddress());
	}

	@Override
	protected void incrementRequestReceivedCounter(String clientAddress) {
		counterListener.listenGTPPrimeTotalRequestReceived();
	}

	protected void incrementRequestReceivedCounter(UDPServiceRequest request) {
		GTPPrimeServiceRequest gtpRequest = (GTPPrimeServiceRequest) request;

		int messageType = gtpRequest.getMessageType();

		if(messageType == GTPPrimeConstants.ECHO_REQUEST.getTypeID()) {
			counterListener.listenGTPPrimeTotalEchoRequestReceivedEvent(gtpRequest.getSourceAddress().getHostAddress());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counter incremented for Echo request");
			}
		} else if(messageType == GTPPrimeConstants.NODE_ALIVE_REQUEST.getTypeID()) {
			counterListener.listenGTPPrimeTotalNodeAliveRequestReceivedEvent(gtpRequest.getSourceAddress().getHostAddress());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counter incremented for Node alive request");
			}
		} else if (messageType == GTPPrimeConstants.REDIRECTION_REQUEST.getTypeID()){
			counterListener.listenGTPPrimeTotalRedirectionRequestReceivedEvent(gtpRequest.getSourceAddress().getHostAddress());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counter incremented for Redirection request");
			}
		} else if (messageType == GTPPrimeConstants.DATA_RECORD_TRANSFER_REQUEST.getTypeID()){
			counterListener.listenGTPPrimeTotalDataRecordTransferRequestReceivedEvent(gtpRequest.getSourceAddress().getHostAddress());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counter incremented for Data transfer request");
			}
		} 
	}

	@Override
	protected void incrementResponseCounter( ServiceResponse serviceResponse){
		GTPPrimeServiceResponse gtpResponse = (GTPPrimeServiceResponse) serviceResponse;
		int messageType = gtpResponse.getMessageType();

		if(messageType == GTPPrimeConstants.ECHO_RESPONSE.getTypeID()) {
			counterListener.listenGTPPrimeTotalEchoResponseSentEvent(gtpResponse.getClientData().getClientIP());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counter incremented for Echo response");
			}
		} else if(messageType == GTPPrimeConstants.NODE_ALIVE_RESPONSE.getTypeID()) {
			counterListener.listenGTPPrimeTotalNodeAliveResponseSentEvent(gtpResponse.getClientData().getClientIP());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counter incremented for Node alive response");
			}
		} else if (messageType == GTPPrimeConstants.REDIRECTION_RESPONSE.getTypeID()){
			if (gtpResponse.isFailure()){
				counterListener.listenGTPPrimeTotalRedirectionFailureResponseSentEvent(gtpResponse.getClientData().getClientIP());
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Counter incremented for Redirection response Failure");
				}
			} else {
				counterListener.listenGTPPrimeTotalRedirectionResponseSentEvent(gtpResponse.getClientData().getClientIP());
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Counter incremented for Redirection response success");
				}
			}
		} else if (messageType == GTPPrimeConstants.DATA_RECORD_TRANSFER_RESPONSE.getTypeID()){
			if (gtpResponse.isFailure()){
				counterListener.listenGTPPrimeTotalDataRecordTransferFailureResponseSentEvent(gtpResponse.getClientData().getClientIP());
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Counter incremented for Data transfer response Failure");
				}
			} else {
				counterListener.listenGTPPrimeTotalDataRecordTransferResponseSentEvent(gtpResponse.getClientData().getClientIP());
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Counter incremented for Data transfer response Success");
				}
			}
		}else if (messageType == GTPPrimeConstants.VERSION_NOT_SUPPORTED.getTypeID()){
			counterListener.listenGTPPrimeTotalVersionNotSupportedResponseSentEvent(gtpResponse.getClientData().getClientIP());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counter incremented for version not supported response");
			}
		}
	}

	@Override
	protected void incrementRequestDroppedCounter(ServiceRequest request){
		GTPPrimeServiceRequest gtpRequest = (GTPPrimeServiceRequest) request;
		counterListener.listenGTPPrimeDroppedRequest(gtpRequest.getSourceAddress().getHostAddress());		
		if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
			LogManager.getLogger().trace(MODULE, "increment Total Request dropped counter of GTP' UDP Service is called.");
		}
		int messageType = gtpRequest.getMessageType();

		if(messageType == GTPPrimeConstants.ECHO_REQUEST.getTypeID()) {
			counterListener.listenGTPPrimeTotalEchoRequestDropped(gtpRequest.getSourceAddress().getHostAddress());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counter incremented for Echo request Dropped");
			}
		} else if(messageType == GTPPrimeConstants.NODE_ALIVE_REQUEST.getTypeID()) {
			counterListener.listenGTPPrimeTotalNodeAliveRequestDropped(gtpRequest.getSourceAddress().getHostAddress());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counter incremented for Node alive request Dropped");
			}
		} else if (messageType == GTPPrimeConstants.REDIRECTION_REQUEST.getTypeID()){
			counterListener.listenGTPPrimeTotalRedirectionRequestDropped(gtpRequest.getSourceAddress().getHostAddress());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counter incremented for Redirection request Dropped");
			}
		} else if (messageType == GTPPrimeConstants.DATA_RECORD_TRANSFER_REQUEST.getTypeID()){
			counterListener.listenGTPPrimeTotalDataRecordTransferRequestDropped(gtpRequest.getSourceAddress().getHostAddress());
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Counter incremented for Data transfer request Dropped");
			}
		} 
	}

	@Override
	protected void incrementRequestDroppedCounter(String clientAddress , ServiceRequest request){
		counterListener.listenGTPPrimeDroppedRequest(clientAddress);
		if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
			LogManager.getLogger().trace(MODULE, "increment Request dropped counter of GTP' UDP Service is called.");
		}
	}
	@Override
	public  void generateAlertOnThreadNotAvailable(){
		context.generateSystemAlert(AlertSeverity.INFO, Alerts.THREADNOTAVAILABLE, getServiceIdentifier(), 
				"Worker Thread not available", 0, "Worker Thread not available");
	}
	@Override
	public  void generateAlertOnHighResponseTime(int endToEndResponseTime){
		context.generateSystemAlert(AlertSeverity.INFO, Alerts.HIGHAAARESPONSETIME, getServiceIdentifier(), "High Response Time : "+endToEndResponseTime+"ms.", endToEndResponseTime, getServerContext().getServerInstanceId());
	}

	public void incrementEchoRequestSentCounter(String clientIP){
		counterListener.listenGTPPrimeTotalEchoRequestSentEvent(clientIP);
	}

	public void incrementNodeAliveRequestSentCounter(String clientIP){
		counterListener.listenGTPPrimeTotalNodeAliveRequestSentEvent(clientIP);
	}

	public void incrementEchoResponseReceivedCounter(String clientIP){
		counterListener.listenGTPPrimeTotalEchoResponseReceivedEvent(clientIP);
	}

	public void incrementNodeAliveResponseReceivedCounter(String clientIP){
		counterListener.listenGTPPrimeTotalNodeAliveResponseReceivedEvent(clientIP);
	}

	public void incrementNodeAliveRequestRetryCounter(String clientIP){
		counterListener.listenGTPPrimeTotalNodeAliveRequestRetryEvent(clientIP);
	}

	public void incrementEchoRequestRetryCounter(String clientIP){
		counterListener.listenGTPPrimeTotalEchoRequestRetryEvent(clientIP);
	}

	public void incrementMalformedEchoResponseReceivedCounter(String clientIP){
		counterListener.listenGTPPrimeTotalMalformedEchoResponseReceivedEvent(clientIP);
	}

	public void incrementMalformedNodeAliveResponseReceivedCounter(String clientIP){
		counterListener.listenGTPPrimeTotalMalformedNodeAliveResponseReceivedEvent(clientIP);
	}

	public class GTPPrimeEchoRequestExecuter extends BaseIntervalBasedTask {

		private int clientPort;
		private String clientIP;
		private int socketTimeout;
		private int clientRetryCounter;
		private DatagramSocket socket;
		private int intervalSeconds;
		private int sequenceNumber;

		private static final int VERSION_SUPPORTED = 2;
		private static final int GTPP_PROTOCOL = 0;
		private static final int SPAREBITS_VALUE = 7;
		private static final int HEADER_TYPE = 0;

		public GTPPrimeEchoRequestExecuter( ServerContext context , GTPPrimeClientData client, DatagramSocket socket){
			super();
			this.clientPort = client.getClientPort();
			if (client.getClientIP().equals("0.0.0.0")){
				try {
					this.clientIP = InetAddress.getLocalHost().getHostAddress();
					LogManager.getLogger().warn(MODULE, "Client IP is configured 0.0.0.0 so considering IP: " + this.clientIP + " to send Echo requests");
				} catch (UnknownHostException e) {}
			} else {
				this.clientIP = client.getClientIP();
			}
			socketTimeout = (int) client.getRequestExpiryTime();
			clientRetryCounter = client.getRequestRetry();
			if (clientRetryCounter < 0){
				clientRetryCounter = 0;
			}
			intervalSeconds = client.getEchoRequestInterval();
			sequenceNumber  = 1 ;
			this.socket = socket;
		}
		@Override
		public long getInitialDelay() {
			return 0;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			int localRetryCounter = 0;
			byte[] rawBytes;
			GTPPrimePacketV1 gtpPacket = new GTPPrimePacketV1();
			GTPPrimePacketV1 receivedGTPPacket = new GTPPrimePacketV1();
			boolean timeoutFlag = false;

			gtpPacket.setMessageType(GTPPrimeConstants.ECHO_REQUEST.typeID);
			gtpPacket.setFirstByte(createFirstByte());
			gtpPacket.setPayloadLength(0);
			gtpPacket.setSeqNumber(sequenceNumber);
			while (localRetryCounter < clientRetryCounter){
				try {
					rawBytes = gtpPacket.getBytes();
					DatagramPacket sendDatagramPacket = new DatagramPacket(rawBytes , rawBytes.length , InetAddress.getByName(clientIP) , clientPort);
					socket.send(sendDatagramPacket);
					incrementRequestSentCounter(clientIP);
					if (timeoutFlag){
						incrementEchoRequestRetryCounter(clientIP);	

					} 
					localRetryCounter++;
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "ECHO request sent successfully to " + clientIP + ":"+ clientPort);
						LogManager.getLogger().info(MODULE, gtpPacket.toString());
					}
					socket.setSoTimeout(socketTimeout);

					rawBytes = new byte[1024];
					DatagramPacket receiveDatagramPacket = new DatagramPacket(rawBytes,rawBytes.length);
					socket.receive(receiveDatagramPacket);
					receivedGTPPacket.setBytes(rawBytes);
					if (receivedGTPPacket.getMessageType() == GTPPrimeConstants.ECHO_RESPONSE.typeID){
						if (receivedGTPPacket.getSeqNumber() == sequenceNumber){
							incrementResponeReceivedCounter(clientIP);
							if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
								LogManager.getLogger().info(MODULE, "Echo response Received from: " + receiveDatagramPacket.getAddress().getHostAddress());
								LogManager.getLogger().info(MODULE, gtpPacket.toString());
							}
							if (sequenceNumber >= Integer.MAX_VALUE){
								sequenceNumber =0;
							}
							sequenceNumber++;
							return;
						}
					}
				}catch(SocketTimeoutException e){
					timeoutFlag = true;
					if (!(localRetryCounter < clientRetryCounter)){
						if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE, clientIP +":"+clientPort + " Failed to respond Echo request.");
						}
					} 
				}catch (SocketException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Error in Socket binding. Reason: " + e.getMessage());
					}
					return;
				} catch (UnknownHostException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Clietn IP invalid. Reason: " + e.getMessage());
					}
					return;
				} catch (IOException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Error occured in sending or receiving datagram packet or in Parsing packet. Reason: " + e.getMessage());
					}
					return;
				} catch (ParseException e) {
					timeoutFlag = true;
					incrementMalformedEchoResponseReceivedCounter(clientIP);
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Parsing error in response received from: " + clientIP + " Reason: "+ e.getMessage());
					}
				} catch (Exception e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Unknown error in Echo request sending/receiving. Reason: "+ e.getMessage());
					}
					return;
				}
			}
		}

		public void incrementRequestSentCounter(String clientIP) {
			incrementEchoRequestSentCounter(clientIP);
		}

		public void incrementResponeReceivedCounter(String clientIP) {
			incrementEchoResponseReceivedCounter(clientIP);
		}

		public byte createFirstByte() {
			int gtpPrimeVersion = VERSION_SUPPORTED;
			int protocolType = GTPP_PROTOCOL;
			int spareBits = SPAREBITS_VALUE;
			int headerType = HEADER_TYPE;
			gtpPrimeVersion = gtpPrimeVersion << 5;
			protocolType = protocolType << 4;
			spareBits = spareBits << 1;
			return new Byte(new Integer(gtpPrimeVersion | protocolType | spareBits | headerType).byteValue());
		}

		@Override
		public long getInterval() {
			return intervalSeconds;
		}
	}

	public class GTPPrimeNodeAliveRequestExecuter extends BaseGTPPrimeRequestExecuter {

		private int localRetryCounter;

		public GTPPrimeNodeAliveRequestExecuter(ServerContext context,GTPPrimeClientData client, DatagramSocket socket) {
			super(context, client);
			localRetryCounter = 0;
			this.socket = socket;
			if (clientRetryCounter < 0){
				clientRetryCounter = 0;
			}
		}

		public void execute(AsyncTaskContext context) {
			byte[] rawBytes;
			GTPPrimePacketV1 gtpPacket = new GTPPrimePacketV1();
			GTPPrimePacketV1 receivedGTPPacket = new GTPPrimePacketV1();
			int sequenceNumber = 2;
			boolean timeoutFlag = false;
			while (localRetryCounter < clientRetryCounter){
				try {
					gtpPacket.setMessageType(GTPPrimeConstants.NODE_ALIVE_REQUEST.typeID);
					gtpPacket.setFirstByte(createFirstByte());
					gtpPacket.setPayloadLength(0);
					gtpPacket.setSeqNumber(sequenceNumber);
					IpAddress toSendIE = new IpAddress();
					toSendIE.setType(GTPPrimeElementTypeConstants.NODE_ADDRES.typeID);
					InetAddress addr;
					try {
						addr = InetAddress.getByName(getSocketDetails().get(0).getIPAddress());
						toSendIE.setAddress(addr);
						if (addr.getClass().equals(Inet6Address.class)){
							toSendIE.setLength(16);
						} else{
							toSendIE.setLength(4);
						}
					} catch (UnknownHostException e1) {
						if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
							LogManager.getLogger().error(MODULE, "Found unknown host to send node alive request. unknown address: " + getSocketDetails().get(0).getIPAddress());
							LogManager.getLogger().error(MODULE, "Will try to send address of local host: " + InetAddress.getLocalHost().toString());
						}
						try {
							addr = InetAddress.getLocalHost();
							toSendIE.setAddress(addr);
							if (addr.getClass().equals(Inet6Address.class)){
								toSendIE.setLength(16);
							} else{
								toSendIE.setLength(4);
							}
						} catch (UnknownHostException e){
							if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
								LogManager.getLogger().error(MODULE, "Could not resolve local host address to send node alive request.");
								LogManager.getLogger().error(MODULE, "Node alive request will not be sent.");
								return;
							}	
						}
					}
					ArrayList<BaseGTPPrimeElement> toSendIEList = new ArrayList<BaseGTPPrimeElement>();
					toSendIEList.add(toSendIE);
					gtpPacket.setElementList(toSendIEList);
					gtpPacket.setPayloadLength(7);	
					rawBytes = gtpPacket.getBytes();
					if (clientIP.equals("0.0.0.0")){
						clientIP = InetAddress.getLocalHost().getHostAddress();
						LogManager.getLogger().warn(MODULE, "configured client IP is 0.0.0.0 so considering IP: " + clientIP + " to send Node Alive Request");
					}
					DatagramPacket sendDatagramPacket = new DatagramPacket(rawBytes , rawBytes.length , InetAddress.getByName(clientIP) , clientPort);
					socket.send(sendDatagramPacket);
					if (timeoutFlag){
						incrementNodeAliveRequestRetryCounter(clientIP);
					} else {
						incrementRequestSentCounter(clientIP);
					}

					localRetryCounter++;
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "Node alive request sent successfully to " + clientIP + ":"+ clientPort + "  With address: " + toSendIE.getAddress());
						LogManager.getLogger().info(MODULE, gtpPacket.toString());
					}

					socket.setSoTimeout(socketTimeout);

					rawBytes = new byte[1024];
					DatagramPacket receiveDatagramPacket = new DatagramPacket(rawBytes,rawBytes.length);
					socket.receive(receiveDatagramPacket);
					receivedGTPPacket.setBytes(rawBytes);
					if (receivedGTPPacket.getMessageType() == GTPPrimeConstants.NODE_ALIVE_RESPONSE.typeID){
						if (receivedGTPPacket.getSeqNumber() == sequenceNumber){
							incrementResponeReceivedCounter(clientIP);
							if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
								LogManager.getLogger().info(MODULE, "Node alive response Received from: " + receiveDatagramPacket.getAddress().getHostAddress());
								LogManager.getLogger().info(MODULE, gtpPacket.toString());
							}
							return;
						}
					}
				}catch(SocketTimeoutException e){
					timeoutFlag = true;
					if (!(localRetryCounter < clientRetryCounter)){
						if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
							LogManager.getLogger().warn(MODULE, clientIP +":"+clientPort + " Failed to respond Node Alive request.");
						}
					} 
				}catch (SocketException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Error in Socket binding. Reason: " + e.getMessage());
					}
					return;
				} catch (UnknownHostException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Clietn IP or local IP invalid. Reason: " + e.getMessage());
					}
					return;
				} catch (IOException e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Error occured in sending or receiving datagram packet or in Parsing packet. Reason: " + e.getMessage());
					}
					return;
				} catch (ParseException e) {
					timeoutFlag = true;
					incrementMalformedEchoResponseReceivedCounter(clientIP);
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Parsing error in response received from: " + clientIP + " Reason: "+ e.getMessage());
					}
				} catch (Exception e) {
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Unknown Error occured in sending or receiving Node Alive request. Reason: " + e.getMessage());
					}
					return;
				} finally {
					socket.close();
				}
			}
		}

		@Override
		public void incrementRequestSentCounter(String clientIP) {
			incrementNodeAliveRequestSentCounter(clientIP);
		}

		@Override
		public void incrementResponeReceivedCounter(String clientIP) {
			incrementNodeAliveResponseReceivedCounter(clientIP);
		}
	}
}
