/**
 * 
 */

package com.elitecore.aaa.rm.service.gtpprime.service;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.conf.RMServerConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.rm.conf.GTPPrimeConfiguration;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientData;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientDataImpl;
import com.elitecore.aaa.rm.service.gtpprime.service.Handlers.GTPPrimeServiceHandler;
import com.elitecore.aaa.rm.service.gtpprime.service.Handlers.GTPPrimeServiceHandlerImpl;
import com.elitecore.aaa.rm.service.gtpprime.service.base.BaseGTPPrimeService;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.IPacketHash;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coregtp.commons.elements.BaseGTPPrimeElement;
import com.elitecore.coregtp.commons.packet.BaseGTPPrimePacket;
import com.elitecore.coregtp.commons.packet.GTPPrimePacketV0;
import com.elitecore.coregtp.commons.packet.GTPPrimePacketV1;
import com.elitecore.coregtp.commons.packet.ParseException;
import com.elitecore.coregtp.commons.util.Dictionary;
import com.elitecore.coregtp.commons.util.constants.GTPPrimeConstants;
import com.elitecore.ratingapi.util.logger.Logger;

/**
 * @author dhaval.jobanputra
 * 
 */
public class GTPPrimeUDPService extends BaseGTPPrimeService {

	private static final String MODULE = "GTP'-UDP-Service";
	private static final String SERVICE_ID = "GTP-PRIME";
	private GTPPrimeConfiguration gtpPrimeConfiguration;
	
	GTPPrimeServiceHandler gtpPrimeHandler;
	private GTPPrimeServiceContext gtpServiceContext;
	private DatagramSocket echoSocket;
	private EliteRollingFileLogger serviceLogger;

	public GTPPrimeUDPService(AAAServerContext serverContext) {
		super(serverContext);
		gtpPrimeConfiguration = ((RMServerConfiguration)serverContext.getServerConfiguration()).getGTPPrimeConfiguration();
	}

	protected void initService() throws ServiceInitializationException {
		gtpServiceContext = new GTPPrimeServiceContext() {

			@Override
			public ServerContext getServerContext() {
				return GTPPrimeUDPService.this.context;
			}

			@Override
			public GTPPrimeConfiguration getGTPPrimeConfiguration() {
				return GTPPrimeUDPService.this.gtpPrimeConfiguration;
			}

		};
		super.initService();
		if (gtpPrimeConfiguration.isServiceLevelLoggerEnabled()) {			
			serviceLogger = 
				new EliteRollingFileLogger.Builder(getServerContext().getServerInstanceName(),
						gtpPrimeConfiguration.getLogLocation())
				.rollingType(gtpPrimeConfiguration.logRollingType())
				.rollingUnit(gtpPrimeConfiguration.logRollingUnit())
				.maxRolledUnits(gtpPrimeConfiguration.logMaxRolledUnits())
				.compressRolledUnits(gtpPrimeConfiguration.isCompressLogRolledUnits())
				.sysLogParameters(gtpPrimeConfiguration.getSysLogConfiguration().getHostIp(),
						gtpPrimeConfiguration.getSysLogConfiguration().getFacility())
				.build();
			serviceLogger.setLogLevel(gtpPrimeConfiguration.logLevel());

			LogManager.setLogger(getServiceIdentifier(), serviceLogger);
		}
		
		try {

			gtpPrimeHandler = new GTPPrimeServiceHandlerImpl(gtpServiceContext);
			gtpPrimeHandler.init();
		} catch (InitializationFailedException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE,"GTP' handler Initialization failed Reason: " + e.getMessage());
			}
		} catch (Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE,"GTP' Error occured in reading dictionary Reason: " + e.getMessage());
			}
		}

		if (gtpPrimeConfiguration.getClientList() != null){
			List<GTPPrimeClientDataImpl> clientList = gtpPrimeConfiguration.getClientList();
			GTPPrimeClientData client;
			File fileLocation;
			String fileName;
			String ext = "";
			if (clientList != null){
				for (int i=0 ; i<clientList.size() ; i++) {
					client = clientList.get(i);
					try  {
						fileLocation = new File( client.getFileLocation() + File.separator + client.getClientIP() );
						fileName = client.getFileName();
						if ((fileName.lastIndexOf('.') + 1) != -1){
							ext = fileName.substring(fileName.lastIndexOf('.') + 1);
						}
						if (fileLocation.isDirectory()){

							File[] files = fileLocation.listFiles();
							changeFileExtension(files, fileLocation, ext);

						} else {
							if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
								LogManager.getLogger().info(MODULE, "Given file location does not Exists. client: " + client.getClientIP() + " File Location: " + fileLocation);
							}
						}
					} catch (Exception e){
						if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
							LogManager.getLogger().error(MODULE, "Files could not be Renamed for client: " + client.getClientIP() + ". Reason: " + e.getMessage());
						}
					}
				}
			}
		}
	}

	private void changeFileExtension(File[] files, File location, String ext){
		String fileName;
		for ( int i=0 ; i<files.length ; i++){
			File file = files[i];
			try{
				if (file.isFile()){
					if (file.getName().endsWith(".inp")){
						fileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + "." + ext ;
						if (file.renameTo(new File (location, fileName))){
							if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
								LogManager.getLogger().info(MODULE, "file renamed to " + file.getName());
							}
						} else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "file could not renamed: " + file.getName());
							}
						}
					}
				}
			}catch (Exception e){
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error Occured renamig file: " + file.getName() + ". Reason: " + e.getMessage());
				}
			}
		} 
	}

	protected boolean startService() {
		boolean isStarted = false;

		if (gtpPrimeConfiguration == null ) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE,"Configurationg found null. May be configuration file is Missing. Service will not be started");
				Logger.logError(MODULE,"Configurationg found null. May be configuration file is Missing. Service will not be started");
			}
			return false;
		}
		if (gtpPrimeConfiguration.getClientList() == null ) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE,"Client Configurationg found null. May be configuration file is Missing. Service will not be started");
				Logger.logError(MODULE,"Client Configurationg found null. May be configuration file is Missing. Service will not be started");
			}
			return false;
		}
		try {
			File dictFile = new File(getServerContext().getServerHome()+ File.separator + "dictionary" + File.separator+ "gtpprime" + File.separator + "standard.xml");
			if (dictFile.exists()) {
				Dictionary.getInstance().readDictionary(dictFile);
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Dictionary file not found.");
				}
				throw new Exception("Dictionary file not found");
			}
		} catch (Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE,"Problem reading GTP' Dictionary. Service will not be started");
			}
			return false;
		}

		isStarted = super.startService();

		if (isStarted) {
			List<GTPPrimeClientDataImpl> clientList = gtpPrimeConfiguration.getClientList();

				for (int i = 0; i < clientList.size(); i++) {
					GTPPrimeClientData client = clientList.get(i);
					if (clientList.get(i).getEchoRequestInterval() > 0) {
						try {
							echoSocket = new DatagramSocket(0, InetAddress.getByName(getSocketDetails().get(0).getIPAddress()));
							GTPPrimeEchoRequestExecuter task = new GTPPrimeEchoRequestExecuter(context, client, echoSocket);
							getServerContext().getTaskScheduler().scheduleIntervalBasedTask(task);
						} catch (SocketException e) {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Socket binding Exception. Echo requests will not be sent. Reason: " + e.getMessage());
							}
						} catch (UnknownHostException e) {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Socket binding Exception. Echo requests will not be sent. Reason: " + e.getMessage());
							}
						} 
					}
					if (clientList.get(i).getNodeAliveRequest()) {
						try {
							DatagramSocket socket = new DatagramSocket (0 , InetAddress.getByName(getSocketDetails().get(0).getIPAddress()));
							GTPPrimeNodeAliveRequestExecuter task = new GTPPrimeNodeAliveRequestExecuter(context, client, socket );
							getServerContext().getTaskScheduler().scheduleSingleExecutionTask(task);
						} catch (SocketException e){
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Socket binding Exception. Node Alive request will not be sent. Reason: " + e.getMessage());
							}
						} catch (UnknownHostException e) {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "Socket binding Exception. Echo requests will not be sent. Reason: " + e.getMessage());
							}
						}
						
					}

				}
				if (gtpPrimeConfiguration.getIdleCommunicationTimeInterval() > 0){
					IdleCommunicationAlertGeneratorTask task = new IdleCommunicationAlertGeneratorTask(gtpPrimeConfiguration.getIdleCommunicationTimeInterval());
					context.getTaskScheduler().scheduleIntervalBasedTask(task);
				}
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "UDP service Could not be started");
			}
		}
		return isStarted;
	}

	public void handleServiceRequest(GTPPrimeServiceRequest request, GTPPrimeServiceResponse response) {

		incrementRequestReceivedCounter(request);
		GTPPrimeClientData client = gtpPrimeConfiguration.getClient(request.getSourceAddress());
		if (client == null) {
			response.markForDropRequest();
			incrementInvalidClientRequestCounter();
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE,"Request Received from Invalid client: "+ request.getSourceAddress() + ":"+ request.getSourcePort());
				LogManager.getLogger().error(MODULE,"Request marked for dropped");
			}

		} else {
			if (!response.isMarkedForDropRequest()){
				gtpPrimeHandler.handleRequest((GTPPrimeServiceRequest)request, (GTPPrimeServiceResponse)response, ISession.NO_SESSION);	
			} else {
				incrementMalformedRequestPacketCounter(request);
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE,"Malformed packet found. Request dropped. Counter incremented for malformed");
				}
			}
		}
	}

	@Override

	public long getClientRequestExpiryTime(InetAddress address) {
		GTPPrimeClientData clientData = gtpPrimeConfiguration.getClient(address);  
		if (clientData == null){
			return 3000;
		}
		return clientData.getRequestExpiryTime();
	}

	public String getServiceIdentifier() {
		return SERVICE_ID;
	}

	protected int getMinThreadPoolSize() {
		return gtpPrimeConfiguration.minThreadPoolSize();
	}

	protected int getMaxThreadPoolSize() {
		return gtpPrimeConfiguration.maxThreadPoolSize();
	}

	protected int getMainThreadPriority() {
		return gtpPrimeConfiguration.mainThreadPriority();
	}

	protected int getWorkerThreadPriority() {
		return gtpPrimeConfiguration.workerThreadPriority();
	}

	protected int getThreadKeepAliveTime() {
		return gtpPrimeConfiguration.threadKeepAliveTime();
	}

	protected int getMaxRequestQueueSize() {
		return gtpPrimeConfiguration.maxRequestQueueSize();
	}

	protected int getSocketReceiveBufferSize() {
		return gtpPrimeConfiguration.socketReceiveBufferSize();
	}

	protected int getSocketSendBufferSize() {
		return gtpPrimeConfiguration.socketSendBufferSize();
	}

	@Override
	public GTPPrimeServiceResponse formServiceSpecificResposne(GTPPrimeServiceRequest serviceRequest) {
		return new GTPPrimeServiceResponseImpl(serviceRequest);
	}

	@Override
	public GTPPrimeServiceRequest formServiceSpecificRequest(InetAddress sourceAddress, int sourcePort, byte[] requestBytes, SocketDetail serverSocketDetail) {
		return new GTPPrimeServiceRequestImpl(sourceAddress, sourcePort, requestBytes, serverSocketDetail);

	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {

	}

	@Override
	public boolean reloadServiceConfiguration() {/*
		try {
			gtpPrimeConfiguration.reloadConfiguration();

		} catch (LoadConfigurationException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE,"Problem during reloading GTP' service Configuration. Reason :" + e.getMessage());
			}
			return false;
		}
		return true;
	*/return true;}

	@Override
	protected ServiceContext getServiceContext() {
		return gtpServiceContext;
	}

	@Override
	public boolean stopService() {
		boolean isStopped = false;
		isStopped = super.stopService();
		try {
		if(echoSocket != null) {
			echoSocket.close();
		}
		} catch (Exception e){
			LogManager.getLogger().trace(MODULE, e);
		}
		gtpPrimeHandler.stop();
		return isStopped;
	}

	class GTPPrimeServiceRequestImpl implements GTPPrimeServiceRequest {

		private static final String MODULE = "GTP'-Service-Request";
		private BaseGTPPrimePacket gtpPrimeRequestPacket;
		private InetAddress sourceAddress;
		private int sourcePort;
		private final long requestReceivedNano;
		private boolean isMalformed = false;
		private SocketDetail serverSocketDetail;

		public GTPPrimeServiceRequestImpl(InetAddress sourceAddress, int sourcePort, byte[] requestBytes, SocketDetail serverSocketDetail) {
			requestReceivedNano = System.nanoTime();
			this.sourceAddress = sourceAddress;
			this.sourcePort = sourcePort;
			this.serverSocketDetail = serverSocketDetail;

			if (getVersion(requestBytes[0]) == 0) {
				gtpPrimeRequestPacket = new GTPPrimePacketV0();
			} else if (getVersion(requestBytes[0]) == 1
					|| getVersion(requestBytes[0]) == 2) {
				gtpPrimeRequestPacket = new GTPPrimePacketV1();
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE,"Request received of unknown Version: "+ getVersion(requestBytes[0]));
				}
				gtpPrimeRequestPacket = new GTPPrimePacketV1();
			}

			try {
				gtpPrimeRequestPacket.setBytes(requestBytes);
			} catch (ParseException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, e.getMessage());
				}
				isMalformed = true;
			}

			gtpPrimeRequestPacket.setClientIP(sourceAddress.getHostAddress());
			gtpPrimeRequestPacket.setClientPort(sourcePort);
		}
		
		@Override
		public boolean isMalformed(){
			return isMalformed;
		}

		private int getVersion(int byteValue) {
			int temp = byteValue;
			return (byte) (temp >>> 5);

		}

		public int getMessageType() {
			return gtpPrimeRequestPacket.getMessageType();
		}

		public int getSeqNumber() {
			return gtpPrimeRequestPacket.getSeqNumber();
		}

		@Override
		public Object getParameter(String str) {
			return null;
		}

		@Override
		public byte[] getRequestBytes() {
			return null;
		}

		@Override
		public long getRequestReceivedNano() {
			return requestReceivedNano;
		}

		@Override
		public InetAddress getSourceAddress() {
			return sourceAddress;
		}

		@Override
		public int getSourcePort() {
			return sourcePort;
		}

		@Override
		public void setParameter(String key, Object parameterValue) {
		}

		public String toString() {
			return gtpPrimeRequestPacket.toString();
		}

		public int getType() {
			return gtpPrimeRequestPacket.getMessageType();
		}

		@Override
		public byte getVersion() {
			return gtpPrimeRequestPacket.getVersion();
		}

		@Override
		public byte getHeaderType() {
			return gtpPrimeRequestPacket.getHeaderType();
		}

		@Override
		public byte getProtocolType() {
			return gtpPrimeRequestPacket.getProtocolType();
		}

		@Override
		public byte getSpareBits() {
			return gtpPrimeRequestPacket.getSpareBits();
		}

		@Override
		public List<BaseGTPPrimeElement> getElementList() {
			return gtpPrimeRequestPacket.getElementList();
		}

		@Override
		public IPacketHash getPacketHash() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SocketDetail getServerSocket() {
			return serverSocketDetail;
		}
	}

	class GTPPrimeServiceResponseImpl implements GTPPrimeServiceResponse {

		// private static final String MODULE = "GTP'-Service-Response";
		private int gtpPrimeVersion;
		private int protocolType;
		private int spareBits;
		private int headerType;
		private int messageType;
		private int payloadLength;
		private int seqNumber;
		private BaseGTPPrimePacket gtpPrimeResponsePacket;
		private List<BaseGTPPrimeElement> IEList;
		private boolean isMarkedForDrop;
		private boolean isFailure = false;
		private GTPPrimeClientData clientData;

		public GTPPrimeServiceResponseImpl(ServiceRequest serviceRequest) {
			isMarkedForDrop = false;
			IEList = new ArrayList<BaseGTPPrimeElement>();

			GTPPrimeServiceRequest gtpServiceRequst = (GTPPrimeServiceRequest) serviceRequest;

			if (gtpServiceRequst.isMalformed()){
				isMarkedForDrop = true;
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE,"Request marked for drop due to malformed packet");
				}
			}

		}

		@Override
		public void setFailure(){
			isFailure = true;
		}

		@Override
		public boolean isFailure(){
			return isFailure;
		}

		@Override
		public Object getParameter(String str) {
			return null;
		}

		@Override
		public byte[] getResponseBytes() {
			gtpPrimeResponsePacket = generatePacket();
			return gtpPrimeResponsePacket.getBytes();
		}

		private BaseGTPPrimePacket generatePacket() {
			BaseGTPPrimePacket responsePacket = null;
			if (gtpPrimeVersion == 1 || gtpPrimeVersion == 2) {
				responsePacket = new GTPPrimePacketV1();
			} else if (gtpPrimeVersion == 0) {
				responsePacket = new GTPPrimePacketV0();
				responsePacket.setFlowLabel();
				responsePacket.setSNDCPN();
				responsePacket.setSpares();
				responsePacket.setTID();
				responsePacket.setHeaderLength(gtpPrimeVersion);
			}

			responsePacket.setFirstByte(createFirstByte());
			responsePacket.setMessageType(messageType);
			responsePacket.setPayloadLength(payloadLength);
			responsePacket.setSeqNumber(seqNumber);
			responsePacket.setElementList(IEList);
			return responsePacket;
		}

		private byte createFirstByte() {
			int gtpPrimeVersion = this.gtpPrimeVersion;
			int protocolType = this.protocolType;
			int spareBits = this.spareBits;
			int headerType = this.headerType;
			gtpPrimeVersion = gtpPrimeVersion << 5;
			protocolType = protocolType << 4;
			spareBits = spareBits << 1;
			return new Byte(new Integer(gtpPrimeVersion | protocolType	| spareBits | headerType).byteValue());
		}

		@Override
		public boolean isFurtherProcessingRequired() {
			return false;
		}

		@Override
		public boolean isMarkedForDropRequest() {
			return isMarkedForDrop;
		}

		@Override
		public boolean isProcessingCompleted() {
			return true;
		}

		@Override
		public void markForDropRequest() {
			isMarkedForDrop = true;
		}

		@Override
		public void setFurtherProcessingRequired(boolean value) {
		}

		@Override
		public void setParameter(String key, Object parameterValue) {
		}

		@Override
		public void setProcessingCompleted(boolean value) {
		}

		@Override
		public void setMessageType(int type) {
			messageType = type;
		}

		@Override
		public void setPayloadLength(int length) {
			payloadLength = length;
		}

		@Override
		public void setSeqNumber(int number) {
			seqNumber = number;
		}

		@Override
		public void setHeaderType(byte headerType) {
			this.headerType = headerType;
		}

		@Override
		public void setProtocolType(byte protocolType) {
			this.protocolType = protocolType;
		}

		@Override
		public void setSparebits(byte spareBits) {
			this.spareBits = spareBits;
		}

		@Override
		public void setVersion(byte version) {
			this.gtpPrimeVersion = version;
		}

		@Override
		public void setElementList(List<BaseGTPPrimeElement> toSendIEList) {
			IEList = toSendIEList;
		}

		@Override
		public int getMessageType() {
			return messageType;
		}

		@Override
		public String toString(){
			StringWriter stringBuffer = new StringWriter();
			PrintWriter out = new PrintWriter( stringBuffer);

			out.println("\n\t\t--Packet Header-- " );
			out.print("{Protocol, " + ((gtpPrimeVersion == 2)? "V2" : ((gtpPrimeVersion==1)?"V1" : "V0")));
			out.print((( protocolType == 0 ) ? "P" : "G"));
			out.print("...");
			out.println(((headerType)==0 ? "0" : "1")+ "}");
			out.println("{Message Type, " + GTPPrimeConstants.fromTypeID(messageType) +"}");
			out.println("{Payload length, " + payloadLength + "}");
			out.println("{Sequence no, " + seqNumber+ "}");
			if ( gtpPrimeVersion == 0){
				out.println("{Flow label, " + 0+ "}");
				out.println("{SNDCP, " + 0+ "}");
				out.println("{Spare octet 1, " + 0 + "; Spare octet 2, " + 0 + "; Spare octet 3, " + 0 + "}");
				out.println("{Tunnel ID, " + 0+ "}");
			}

			out.println("\t\t--Elements--");
			if (IEList != null){
				for (int i=0 ; i<IEList.size() ; i++){
					out.println(IEList.get(i).toString());
				}
			}

			return stringBuffer.toString();
		}

		@Override
		public GTPPrimeClientData getClientData() {
			return clientData;	
		}

		@Override
		public void setClientData(GTPPrimeClientData client) {
			clientData = client;

		}
	}

	@Override
	public void handleAsyncServiceRequest(GTPPrimeServiceRequest request,
			GTPPrimeServiceResponse response) {
		// TODO Auto-generated method stub
		
	}
	
	public class IdleCommunicationAlertGeneratorTask extends BaseIntervalBasedTask{

		int requestCounter;
		long interval;
		
		public IdleCommunicationAlertGeneratorTask(long interval){
			requestCounter = getRequestCounter();
			this.interval = interval;
		}
		
		@Override
		public long getInterval() {
			return interval;
		}

		@Override
		public long getInitialDelay() {
			return interval;
		}
		
		@Override
		public void execute(AsyncTaskContext context) {
			if (requestCounter == getRequestCounter()){
				gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.IDLE_COMMUNICATION, getServiceIdentifier(), "No request received in last " + interval + " Seconds.");
			}
			
			requestCounter = getRequestCounter();
		}

				
	}

	@Override
	public String getServiceName() {
		return "GTP Prime Service";
	}

	@Override
	protected void applyMonitoryLogLevel(GTPPrimeServiceRequest request, GTPPrimeServiceResponse response) {
		
	}
	
	@Override
	protected void removeMonitoryLogLevel(GTPPrimeServiceRequest request, GTPPrimeServiceResponse response) {
		
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return gtpPrimeConfiguration.getSocketDetails();
	}
	
	@Override
	protected final void shutdownLogger() {
		Closeables.closeQuietly(serviceLogger);
	}
	@Override
	public int getDefaultServicePort() {
		return AAAServerConstants.DEFAULT_GTP_PRIME_SERVICE_PORT;
	}
	
}