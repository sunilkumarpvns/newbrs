package com.elitecore.diameterapi.diameter.common.peers;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.core.commons.tls.EliteSSLContextFactory;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionData;
import com.elitecore.diameterapi.core.common.fsm.IStateTransitionDataCode;
import com.elitecore.diameterapi.core.common.fsm.enums.IEventEnum;
import com.elitecore.diameterapi.core.common.fsm.enums.IStateEnum;
import com.elitecore.diameterapi.core.common.fsm.exception.UnhandledTransitionException;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.peer.IPeerListener;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.transport.NetworkConnectionHandler;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectionEvents;
import com.elitecore.diameterapi.core.common.transport.tcp.exception.HandShakeFailException;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.stack.Stack;
import com.elitecore.diameterapi.core.stack.alert.StackAlertSeverity;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;
import com.elitecore.diameterapi.diameter.common.DiameterNWConnectionEventListener;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.explicitrouting.ExplicitRoutingHandler;
import com.elitecore.diameterapi.diameter.common.fsm.peer.DiameterPeerStateMachine;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerEvent;
import com.elitecore.diameterapi.diameter.common.fsm.peer.enums.DiameterPeerState;
import com.elitecore.diameterapi.diameter.common.fsm.peer.pcb.PCBStateMachine;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.peers.capabilityexchange.PeerApplicationProvider;
import com.elitecore.diameterapi.diameter.common.routerx.DiameterRouter;
import com.elitecore.diameterapi.diameter.common.session.DiameterAppMessageHandler;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterURIParser;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.InbandSecurityId;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.URIData;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.PeerDataCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RedirectHostAVPFormat;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.EndToEndPool;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.HopByHopPool;
import com.elitecore.diameterapi.diameter.stack.DuplicateDetectionHandler;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;
import com.elitecore.diameterapi.mibs.config.DiameterPeerConfig;
import com.elitecore.diameterapi.mibs.constants.IpAddressTypes;
import com.elitecore.diameterapi.mibs.constants.RowStatus;
import com.elitecore.diameterapi.mibs.constants.SecurityProtocol;
import com.elitecore.diameterapi.mibs.constants.StorageTypes;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerIpAddressTable;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerVendorTable;
import com.elitecore.diameterapi.mibs.statistics.GroupedStatistics;

public class DiameterPeer implements IPeerListener{

	private static final String MODULE = "PEER" ;
	public static final int DUPLICATE_CONNECTION_ALLOWED_INTERVAL = 3000;  	// in ms
	private List<String> hostIPAddresses = null;
	private int intVendorId = 0;
	private String strProductName = null;
	private int intFirmwareRevision;
	private long lExpirationTime;
	private Set<InbandSecurityId>localInbandSecurityIds;
	private Set<InbandSecurityId>remoteInbandSecurityIds; 
	private List<String>supportedVendors;
	private DiameterPeerStateMachine peerStateMachine;
	private int iSessionIdHigherValue;
	private int iSessionIdLowerValue;
	
	private AtomicBoolean isAlive;
	private int loadBalanceCount=1;
	private int loadBalnceCounter;
	private boolean initConnection=true;
	private boolean localPeer=false;
	private volatile PeerData peerData;
	private DiameterPCBStateMachine pcbStateMachine;
	private IDiameterStackContext stackContext;
	private NetworkConnectionHandler connectionHandler;
	@Nonnull private RetransmissionHandler retransmissionHandler;
	private List<DiameterPeerStatusListener> peerStatusListeners;
	private AtomicLong tranlationSeq;
	private ScheduledThreadPoolExecutor scheduleThreadPoolExecutor;
	private DiameterPeerConfig peerConfig;
	private PeerApplicationProvider peerApplicationProvider;
	private long lastConnectionAttemptedTimestamp;
	private AtomicLong messageTxCount;
	@Nonnull private TimeSource timesource;
	
	public DiameterPeer(PeerData peerData, IDiameterStackContext stackContext,DiameterRouter diameterRouter,SessionFactoryManager sessionFactoryManager, DiameterAppMessageHandler  appMessageHandler, ExplicitRoutingHandler explicitRoutingHandler, DuplicateDetectionHandler duplicateMessageHandler) {
		this(peerData, stackContext, diameterRouter, sessionFactoryManager, appMessageHandler, explicitRoutingHandler, duplicateMessageHandler, TimeSource.systemTimeSource(), new PeerApplicationProvider(stackContext, peerData));
	}
	
	@VisibleForTesting
	DiameterPeer(PeerData peerData, IDiameterStackContext stackContext, DiameterRouter diameterRouter,
			SessionFactoryManager sessionFactoryManager, DiameterAppMessageHandler  appMessageHandler, 
			ExplicitRoutingHandler explicitRoutingHandler, 
			DuplicateDetectionHandler duplicateMessageHandler, final TimeSource timeSource,
			PeerApplicationProvider peerApplicationProvider) {
		this.stackContext = stackContext;
		this.peerData = peerData;
		this.peerStateMachine = new DiameterPeerStateMachine(this, stackContext, diameterRouter, 
				sessionFactoryManager, 
				appMessageHandler,
				DiameterPeerState.Closed, 
				explicitRoutingHandler,
				new OverloadHandler(),
				duplicateMessageHandler, 
				timeSource){

			@Override
			protected String getPeerName() {
				return DiameterPeer.this.getPeerName();
			}

			@Override
			public void addAdditionalAVPs(List<IDiameterAVP> additionalAvps,
					DiameterPacket diameterPacket) {
				DiameterPeer.this.addAdditionalAVPs(additionalAvps, diameterPacket);
			}

			@Override
			protected void setPeerInitConnection(boolean peerInitConnection) {
				DiameterPeer.this.setInitConnection(peerInitConnection);
			
			}

			@Override
			protected void triggerDWR() {
				pcbStateMachine.onTimerElapsed();
			}

			@Override
			public void sendRequest(DiameterRequest request, ResponseListener listener) throws IOException {
				if (CommandCode.DEVICE_WATCHDOG.code != request.getCommandCode()) {
					retransmissionHandler.addPacket(request, listener);
				}
				try {
					writeToStream(request);
				} catch(IOException ex) {
					retransmissionHandler.removePacket(request.getHop_by_hopIdentifier());
					throw ex;
				}
			}

			@Override
			public void sendAnswer(DiameterAnswer answer) throws IOException {
				

				if (answer.getRequestReceivedTime() > 0) {
					long requestProcessingTime = timeSource.currentTimeInMillis() - answer.getRequestReceivedTime();
					if (requestProcessingTime > CommonConstants.MAX_RESPONSE_TIME_MS) {

						if (getLogger().isWarnLogLevel()) {
							getLogger().warn(MODULE, new StringBuilder(200)
									.append("Peer: ")
									.append(getPeerName())
									.append(", Diameter Request Processing Time: ")
									.append(requestProcessingTime)
									.append("ms for Session-ID=")
									.append(answer.getAVPValue(DiameterAVPConstants.SESSION_ID))
									.append(", Packet-Type: ")
									.append(answer.getCommandCode())
									.append(", HbH-ID=")
									.append(answer.getHop_by_hopIdentifier())
									.append(", E2E-ID=")
									.append(answer.getEnd_to_endIdentifier())
									.toString());
						}
						Stack.generateAlert(StackAlertSeverity.INFO, 
								DiameterStackAlerts.DIAMETER_HIGH_RESPONSE_TIME, MODULE, 
								new StringBuilder(200)
										.append("Diameter High Response Time : ")
										.append(requestProcessingTime)
										.append("ms to Peer: ")
										.append(getPeerName())
										.append(", for HbH-ID=")
										.append(answer.getHop_by_hopIdentifier())
										.append(", E2E-ID=")
										.append(answer.getEnd_to_endIdentifier()).toString(),
								(int)requestProcessingTime, getHostIdentity());

					} else {
						if(getLogger().isInfoLogLevel()){
							getLogger().info(MODULE, new StringBuilder(200)
									.append("Peer: ")
									.append(getPeerName())
									.append(", Diameter Request Processing Time: ")
									.append(requestProcessingTime)
									.append("ms")
									.append(" for Session-ID=")
									.append(answer.getAVPValue(DiameterAVPConstants.SESSION_ID))
									.append(", Packet-Type: ")
									.append(answer.getCommandCode()).toString());
						}
					}

				} else {
					if (getLogger().isDebugLogLevel()) {
						getLogger().debug(MODULE, new StringBuilder()
                                .append("Peer: ")
                                .append(getPeerName())
                                .append(" Diameter Request Processing Time can not be calculated.")
                                .append(" Reason: Request received time not available for sessionID=")
                                .append(answer.getAVPValue(DiameterAVPConstants.SESSION_ID))
                                .append(", Packet-Type: ")
                                .append(answer.getCommandCode())
                                .toString());
					}
				}
			
				
				writeToStream(answer);
			}

			@Override
			public void sendBasePacket(DiameterPacket basePacket) throws IOException {
				if (basePacket.getCommandCode() == CommandCode.DEVICE_WATCHDOG.getCode()) {
					if (basePacket.isRequest()) {
						if(getLogger().isInfoLogLevel()) {
							getLogger().info(MODULE, "Sending DWR to " + getPeerName() + 
									" " + getHostIPAddresses());
						}
					} else {
						if(getLogger().isInfoLogLevel()) {
							getLogger().info(MODULE, "Sending DWA to " + getPeerName() + 
									" " + getHostIPAddresses());
						}
					}
				}
				
				writeToStream(basePacket);
			}
		};
		this.tranlationSeq = new AtomicLong(1);
		
		hostIPAddresses = new ArrayList<String>();
		localInbandSecurityIds = new TreeSet<InbandSecurityId>();
		remoteInbandSecurityIds = new TreeSet<InbandSecurityId>();
		
		supportedVendors = new ArrayList<String>();
		iSessionIdHigherValue = (int)new Date().getTime();
		iSessionIdLowerValue = 0;
		this.pcbStateMachine = new DiameterPCBStateMachine(peerData.getWatchdogInterval(),peerData.getInitiateConnectionDuration(),stackContext);
		this.isAlive = new AtomicBoolean(false);
		this.peerStatusListeners = new ArrayList<DiameterPeerStatusListener>();
		this.peerApplicationProvider = peerApplicationProvider;
		this.scheduleThreadPoolExecutor =  new ScheduledThreadPoolExecutor(5, new EliteThreadFactory(com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants.DIAMETER_STACK_IDENTIFIER,peerData.getPeerName()+"-PEER-SCH", Thread.MAX_PRIORITY));
		this.scheduleThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		this.retransmissionHandler = new RetransmissionHandler();
		this.messageTxCount = new AtomicLong();
		this.timesource = timeSource;
		setLocalSecurity();
	}
	
	public void init() throws InitializationFailedException{
		peerConfig = createPeerConfig();
		peerApplicationProvider.init();
	}
	
	private void setLocalSecurity() {
		if(peerData.getSecurityStandard() == SecurityStandard.NONE){
			localInbandSecurityIds.add(InbandSecurityId.NO_INBAND_SECURITY);
		} else if(peerData.getSecurityStandard() == SecurityStandard.RFC_3588_TLS){
			localInbandSecurityIds.add(InbandSecurityId.TLS);
		} else if(peerData.getSecurityStandard() == SecurityStandard.RFC_3588_DYNAMIC){
			localInbandSecurityIds.add(InbandSecurityId.TLS);
			localInbandSecurityIds.add(InbandSecurityId.NO_INBAND_SECURITY);
		}
	}

	public void attemptConnection() {
		
		if(isInitiateConnection()) {
			if(getRemoteInetAddress() == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Unabled to attempt connection with Peer: " + peerData.getPeerName() + ". Reason: Invalid RemoteInetAddress");
				return;
			}

			try {
				IStateTransitionData stateTransitionData = getStateTransitionData();
				stateTransitionData.addObject(PeerDataCode.PEER_EVENT, DiameterPeerEvent.Start);
				peerStateMachine.onStateTransitionTrigger(stateTransitionData);
			} catch (UnhandledTransitionException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Connection Initialization failed with Peer: " + peerData.getPeerName() + ". Reason: " + e.getMessage());
			}
		}
	}
	
	

	/**
	 * @return the initiateConnection
	 */
	public boolean isInitiateConnection() {
		return initConnection && peerData.getInitiateConnectionDuration() > 0;
	}
	
	private void setInitConnection(boolean initConnection) {
		this.initConnection = initConnection;
	}

	public void addSupportedVendor(long intVendorId) {
		supportedVendors.add(String.valueOf(intVendorId));
	}

	public List<String> getSupportedVendors() {
		return supportedVendors;
	}

	public boolean isValidSupporedVendor(int intVendorId) {
		return supportedVendors.contains(String.valueOf(intVendorId));
	}

	public void setHostIPAddress(List<String> hostIPAddress) {
		if(hostIPAddress != null && hostIPAddress.size() > 0)
			hostIPAddresses = hostIPAddress;
	}

	public List<String>getHostIPAddresses() {
		return hostIPAddresses;
	}	
	public boolean isValidHostIPAddress(String hostAddress) {
		return hostIPAddresses.contains(hostAddress);
	}

	public void setVendorId(int intVendorId) {
		this.intVendorId = intVendorId;
	}

	public int getVendorId() {
		return intVendorId;
	}

	public void setProductName(String strProductName) {
		this.strProductName = strProductName;
	}

	public String getProductName() {
		return strProductName;
	}

	public void setFirmwareRevision(int intFirmwareRevision) {
		this.intFirmwareRevision = intFirmwareRevision;
	}

	public int getFirmwareRevision() {
		return intFirmwareRevision;
	}

	public String getRealmName() {
		return peerData.getRealmName();
	}
	
	public EliteSSLContextExt createEliteSSLContext(){
		EliteSSLContextExt eliteSSLContext = null;
		try{
			EliteSSLContextFactory sslContextFactory = stackContext.getEliteSSLContextFactory();
			if(sslContextFactory == null){
				LogManager.getLogger().warn(MODULE, "Could not create sslContext for Peer: "+ peerData.getPeerName() 
				+ ". Reason: SSLContext factory not provide");
				
				return null;
			}
			eliteSSLContext = sslContextFactory.createSSLContext(peerData.getSSLParameter());
			
			eliteSSLContext.getTrustManager().setCertificateSubjectCnChecker(new DiameterCertificateSubjectCnChecker(!peerData.getSSLParameter().isValidateSubjectCN(), peerData));
		}catch(Exception ex){
			LogManager.getLogger().warn(MODULE, "TLS connection not possible for diameterPeer: " + peerData.getPeerName() + ". Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			//TODO ALERT
		}
		
		return eliteSSLContext;
	}

	/**
	 * Add securityID value received in CER or CEA
	 * @param inbandSecurityId
	 */
	public void addRemoteSecurityId(InbandSecurityId inbandSecurityId) {
		remoteInbandSecurityIds.add(inbandSecurityId);
	}

	public Set<InbandSecurityId> getCommonSecurityIds() {
		Set<InbandSecurityId>commonInbandSecurityIds = new TreeSet<InbandSecurityId>();
		if(localInbandSecurityIds.contains(InbandSecurityId.TLS) && remoteInbandSecurityIds.contains(InbandSecurityId.TLS)){
			commonInbandSecurityIds.add(InbandSecurityId.TLS);
		} else if(localInbandSecurityIds.contains(InbandSecurityId.NO_INBAND_SECURITY) && remoteInbandSecurityIds.contains(InbandSecurityId.NO_INBAND_SECURITY)){
			commonInbandSecurityIds.add(InbandSecurityId.NO_INBAND_SECURITY);
		} 
		return commonInbandSecurityIds;
	}

	public String getNextSessionId(){
		String strSessionId = Parameter.getInstance().getOwnDiameterIdentity() + ";"+ iSessionIdHigherValue
		+ ";" + iSessionIdLowerValue;
		if(iSessionIdLowerValue == Integer.MAX_VALUE){
			iSessionIdLowerValue = 0;
			iSessionIdHigherValue ++;
		}else
			iSessionIdLowerValue++;
		return strSessionId;
	}

	public String getNextSessionId(String strOptionalValue){
		String strSessionId = Parameter.getInstance().getOwnDiameterIdentity() + ";"+ iSessionIdHigherValue
		+ ";" + iSessionIdLowerValue;
		if(iSessionIdLowerValue == Integer.MAX_VALUE){
			iSessionIdLowerValue = 0;
			iSessionIdHigherValue ++;
		}else
			iSessionIdLowerValue++;
		return strSessionId + ";" +strOptionalValue;
	}

	public long getExpirationTime() {
		return lExpirationTime;
	}
	public void setExpirationTime(long expirationTime) {
		//Expire time for request is converted to Mill Seconds from seconds as it is in seconds in configuration.
		lExpirationTime = expirationTime*1000;
	}

	public String getHostIdentity() {
		return peerData.getHostIdentity();
	}

	@Override
	public String getPeerName() {
		return peerData.getPeerName();
	}

	// For Session CleanUp On CER and DPR
	public boolean isSessionCleanUpOnCER(){
		return peerData.isSessionCleanUpOnCER();
	}
	public boolean isSessionCleanUpOnDPR(){
		return peerData.isSessionCleanUpOnDPR();
	}
	
	public IStateEnum currentState() {
		if(peerStateMachine != null)
			return (peerStateMachine.currentState());
		else
			return null;
	}

	public DiameterPeerStateMachine getPeerStateMachine() {
		return peerStateMachine;
	}
	public int getPCBState(){
		return pcbStateMachine.getCurrentState();
	}

	public int getPeerState(){
		return peerStateMachine.getCurrentState();
	}
	public long getPeerStateChangedDuration(){
		return peerStateMachine.getStateDuration();
	}
	public void setPeerStateMachine(DiameterPeerStateMachine peerStateMachine) {
		this.peerStateMachine = peerStateMachine;
	}
	
	protected void writeToStream(DiameterPacket packet) throws IOException{

		boolean destHostReplaced = false, destRealmReplaced = false;
		IDiameterAVP destHost = packet.getAVP(DiameterAVPConstants.DESTINATION_HOST);
		IDiameterAVP destRealm = packet.getAVP(DiameterAVPConstants.DESTINATION_REALM);
		if (destHost != null && DiameterConstants.VIRTUAL_IDENTIFIER.equals(destHost.getStringValue())) {
			destHost.setStringValue(this.peerData.getHostIdentity());
			destHostReplaced = true;
		}

		if (destRealm != null && DiameterConstants.VIRTUAL_IDENTIFIER.equals(destRealm.getStringValue())) {
			destRealm.setStringValue(this.peerData.getRealmName());
			destRealmReplaced = true;
		}
		
		this.stackContext.finalPreResponseProcess(packet);

		long currentTimeMillis = timesource.currentTimeInMillis(); 
		
		
		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Sending Packet to: " + getPeerName() + 
					" "+ getHostIPAddresses() + packet.toString());
		} 

		packet.setSendTime(currentTimeMillis);
		
		try {			
			connectionHandler.send(packet);
			this.stackContext.updateOutputStatistics(packet, getHostIdentity());
		} finally {
			if (destHostReplaced){
				destHost.setStringValue(DiameterConstants.VIRTUAL_IDENTIFIER);
			}
			
			if (destRealmReplaced){
				destRealm.setStringValue(DiameterConstants.VIRTUAL_IDENTIFIER);
			}
		}
		
	}

	public void closeConnection(ConnectionEvents event) {
		
		/*
		 * This event is generated whenever PCB state machine generate event "TimerExpired" and PCBState = "DOWN" OR "INITIAL"
		 */
		try{
			if(event == ConnectionEvents.TIMER_EXPIRED){
				handleEvent(DiameterPeerEvent.Timeout, event);
			}
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Error in processiong timeout event for peer = " + getPeerName());
			LogManager.getLogger().trace(MODULE, ex);
		}
		if(isPeerConnected()) {
			
			if(event == ConnectionEvents.FORCE_CLOSE)
				connectionHandler.terminateConnection();
			else
			connectionHandler.closeConnection(event);
		}
		peerApplicationProvider.clear();
		remoteInbandSecurityIds.clear();
		supportedVendors.clear();
		hostIPAddresses.clear();

	}

	public boolean isPeerConnected(){
		if(connectionHandler != null && connectionHandler.isConnected()) {
			return true;
		}
		return false;
	}

	@Override
	public void setConnectionListener(NetworkConnectionHandler connectionHandler) {

		if(connectionHandler == null || connectionHandler.isConnected() == false){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Connection-Handler not used for Peer: " + getPeerName()+
				", Reason: Connection-Handler is null");
			return;
	}

		if(this.connectionHandler != null && this.connectionHandler.isConnected()){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Connection-Handler(" + 
				connectionHandler.getSourceIpAddress() + ":" + connectionHandler.getSourcePort() + 
				") not used for Peer: " + getPeerName() + 
				", Reason: Connection-Handler (" + 
				this.connectionHandler.getSourceIpAddress() + ":" + this.connectionHandler.getSourcePort() + 
				") already exists");
			return;
			}
		this.connectionHandler = connectionHandler;
		recordMessageTxCountAndConnectionTime();
		DiameterNWConnectionEventListener connectionEventListener = new DiameterNWConnectionEventListener(this);
		this.connectionHandler.addNetworkConnectionEventListener(connectionEventListener);
		}
		

	public Set<ApplicationEnum> getCommonApplications() {
		return peerApplicationProvider.getCommonApplications();
	}
	
	public Set<ApplicationEnum> getRemoteApplications() {
		return peerApplicationProvider.getRemoteApplications();
	}
	
	public Set<ApplicationEnum> getApplications() {
		return peerApplicationProvider.getApplications();
	}

	
	
	/**
	 * 
	 * @author harsh
	 *
	 */
	private class RetransmissionHandler {
	
		private ConcurrentHashMap<Integer, DiameterRequestWrapper> requestMap;
		
		public RetransmissionHandler(){
			requestMap = new ConcurrentHashMap<Integer, DiameterRequestWrapper>(1024, com.elitecore.core.commons.util.constants.CommonConstants.DEFAULT_LOAD_FACTOR, stackContext.getMaxWorkerThreads());
		}
		
		public void addPacket(DiameterRequest diameterRequest, ResponseListener listener){
			long timeoutTimeMillis = timesource.currentTimeInMillis() + peerData.getRequestTimeout();
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "RequestTimeout will be on " +  simpleDateFormatPool.get().format(timeoutTimeMillis)
				+ " for diameter request with H2HId: " + diameterRequest.getHop_by_hopIdentifier()
					+ " to peer: " + peerData.getHostIdentity());
			DiameterRequestWrapper diameterRequestWrapper = new DiameterRequestWrapper(diameterRequest, listener);
			requestMap.put(diameterRequest.getHop_by_hopIdentifier(), diameterRequestWrapper);
			
			TimeoutTask timeoutTask = new TimeoutTask(peerData.getRequestTimeout(), diameterRequest.getHop_by_hopIdentifier(), diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			ScheduledFuture<?> scheduledFuture = scheduleThreadPoolExecutor.schedule(timeoutTask, timeoutTask.getInitialDelay(), timeoutTask.getTimeUnit());
			diameterRequestWrapper.setScheduleTask(scheduledFuture);
		}
		
		
		public DiameterRequestWrapper removePacket(int hopByHopIdentifier){
			
			DiameterRequestWrapper diameterRequestWrapper = requestMap.remove(hopByHopIdentifier);
			if(diameterRequestWrapper == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Diameter request with H2HId: " + hopByHopIdentifier
					+ " for peer: " + peerData.getHostIdentity() + "  already removed from pending requests");
				return null;
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Diameter request with H2HId: " + hopByHopIdentifier
				+" for  peer: " + peerData.getHostIdentity() + " removed from pending requests");
			if(diameterRequestWrapper.getScheduleTask() != null){
				diameterRequestWrapper.getScheduleTask().cancel(true);
			}
			
			
			return diameterRequestWrapper; 
		}
		
	
		

		private class DiameterRequestWrapper {
			
			private DiameterRequest diameterRequest;
			private ResponseListener listener;
			
			private int retryCount;
			private ScheduledFuture<?> scheduledTask;
			
			public DiameterRequestWrapper(DiameterRequest diameterRequest, ResponseListener listener) {
				this.diameterRequest = diameterRequest;
				this.listener = listener;
				this.retryCount = 0;
			}
			
			public ResponseListener getListener(){ return listener; }
			
			public DiameterRequest getDiameterRequest(){return diameterRequest;}
			
			public int getRetryCount(){ return retryCount;}
			
			public void increamentRetryCount(){ retryCount++; }
			
			public ScheduledFuture<?> getScheduleTask(){
				return scheduledTask;
			}
			
			public void setScheduleTask(ScheduledFuture<?> task){
				this.scheduledTask = task;
			}
			
		}
		
		private class TimeoutTask implements Runnable{
			private long timeoutInterval;
			private int hopByHopId;
			private String sessionID;
			public TimeoutTask(long retryInterval, int hopByHopId, String sessionID){
				this.timeoutInterval = retryInterval;
				this.hopByHopId = hopByHopId;
				this.sessionID = sessionID;
			}
			
			public long getInitialDelay() { return timeoutInterval;}
			
			public TimeUnit getTimeUnit() { return TimeUnit.MILLISECONDS; }

			@Override
			public void run() {
				
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "No response from server " + getHostIdentity() + " for request with H2HId: " 
					+ hopByHopId + " for sessionID: " + sessionID);
				
				DiameterRequestWrapper diameterRequestWrapper = requestMap.get(hopByHopId);
				
				if(diameterRequestWrapper == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Skipping execution of  Timeout Task for diameter reqeust with H2HId: "+ hopByHopId 
						+", sessionID: "+ sessionID +" for peer: " + peerData.getHostIdentity() 
						+ ". Reason: diameter request removed from pending requests");
					return;
				}
				
				
				DiameterRequest diameterRequest = diameterRequestWrapper.getDiameterRequest();
				if(diameterRequestWrapper.getRetryCount() >= peerData.getRetryCount()){
					handleTimeoutRequest(diameterRequestWrapper);
					return;
				}
			
				diameterRequestWrapper.increamentRetryCount();
				
				if(getPeerState() != DiameterPeerState.I_Open.stateOrdinal() && getPeerState() != DiameterPeerState.R_Open.stateOrdinal()){
					LogManager.getLogger().warn(MODULE, "Unable to retrasmite diameter request with H2HId: " + hopByHopId +", sessionID: "+ sessionID
					+ " to peer: " + getHostIdentity() + ". Reason: peer is not in OPEN state");
					
					stackContext.updateTimeoutRequestStatistics(diameterRequest, getHostIdentity());
					diameterRequestWrapper.setScheduleTask(scheduleThreadPoolExecutor.schedule(this, this.getInitialDelay(), this.getTimeUnit()));
					return;
				
				}
				
				retry(diameterRequestWrapper);
				diameterRequestWrapper.setScheduleTask(scheduleThreadPoolExecutor.schedule(this, this.getInitialDelay(), this.getTimeUnit()));
			}
			
			private void retry(DiameterRequestWrapper diameterRequestWrapper){
				DiameterRequest diameterRequest = diameterRequestWrapper.getDiameterRequest();
				try{
					diameterRequest.setReTransmittedBit();
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Retransmitting diameter request with H2HId: " + hopByHopId +", sessionID: "+ sessionID
						+ " to peer: " + getHostIdentity() + ". Attempt: "+ diameterRequestWrapper.getRetryCount() +". Remaining Attempts: " 
						+ (peerData.getRetryCount() - diameterRequestWrapper.getRetryCount()));
					}
					
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
						LogManager.getLogger().debug(MODULE, "Next request timeout will be on " +  simpleDateFormatPool.get().format(timesource.currentTimeInMillis() + peerData.getRequestTimeout())
						+ " for diameter request with H2HId: " + diameterRequest.getHop_by_hopIdentifier() + " to peer: " + peerData.getHostIdentity());
					}
					stackContext.updateTimeoutRequestStatistics(diameterRequest, getHostIdentity());
					writeToStream(diameterRequest);
				}catch(Exception ex){
					LogManager.getLogger().error(MODULE, "Error in retrasmitting diameter request with H2HId: " + hopByHopId +", sessionID: " 
					+ sessionID + " to Peer: " + getHostIdentity() + ". Reason: " + ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
				}
			}
			
			private void handleTimeoutRequest(DiameterRequestWrapper diameterRequestWrapper){
				DiameterRequest diameterRequest = diameterRequestWrapper.getDiameterRequest();
				
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Timeout occured for diameter request with H2HId: " + hopByHopId +", sessionID: "+ sessionID
					+ " for peer: " + peerData.getHostIdentity());
		
				ResponseListener listener = diameterRequestWrapper.getListener();
				if(requestMap.remove(hopByHopId) == null){
					return;
				}
				
				stackContext.updateTimeoutRequestStatistics(diameterRequest, getHostIdentity());
				
				
				if(listener != null){
					try{
						listener.requestTimedout(getHostIdentity(), (DiameterSession)stackContext.getOrCreateSession(sessionID, diameterRequest.getApplicationID()));
					}catch(Exception ex){
						LogManager.getLogger().error(MODULE, "Error in providing diameter request with H2HId: " + hopByHopId +", sessionID: " 
						+ sessionID + " for Peer: " + getHostIdentity() + " to timeoutlistener. Reason: " + ex.getMessage());
					LogManager.getLogger().trace(MODULE, ex);
					}
					
				} else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Drop diameter request with H2HId: " + hopByHopId +", sessionID: "+ sessionID
						+ " for peer: " + peerData.getHostIdentity() + ". Reason: timeoutlistener not found");
				}
			}
		}
		
		

	}
	


	/**
	 * returns remotePort
	 */
	public int getCommunicationPort() {
		return peerData.getRemotePort();
	}

	public boolean isAlive() {
		return isAlive.get();
	}
	
	public int getLoadBalanceCount() {
		return loadBalanceCount;
	}

	public void setLoadBalanceCount(int loadBalanceCount) {
		this.loadBalanceCount = loadBalanceCount;
		setLoadBalnceCounter(loadBalanceCount);
	}

	public int getLoadBalnceCounter() {
		return loadBalnceCounter;
	}

	public void setLoadBalnceCounter(int loadBalnceCounter) {
		this.loadBalnceCounter = loadBalnceCounter;
	}

	public boolean isLocalPeer() {
		return localPeer;
	}

	public void setLocalPeer(boolean localPeer) {
		this.localPeer = localPeer;
	}
	public long getWatchdogInterval(){
		return peerData.getWatchdogInterval();
	}
	public long getTimeoutConnectionAttempts(){
		return ((DiameterPeerStateMachine)this.peerStateMachine).getTimeoutConnectionAttempts();
	}
	
	public int getPeerTimeout() {
		if(pcbStateMachine != null)
			return pcbStateMachine.getTimeout();
		else{
			return 3000;
		}
	}

	@Override
	public void processReceivedDiameterPacket(Packet packet, NetworkConnectionHandler connectionHandler) throws UnhandledTransitionException{
		long currentTime = timesource.currentTimeInMillis();
		DiameterPacket diameterPacket = (DiameterPacket) packet;
		IStateTransitionData stateTransitionData = getStateTransitionData();
		stateTransitionData.addObject(PeerDataCode.DIAMETER_RECEIVED_PACKET, diameterPacket);
		stateTransitionData.addObject(PeerDataCode.CONNECTION, connectionHandler);
		this.stackContext.updateInputStatistics(diameterPacket, getHostIdentity());
		long idealTime = currentTime - diameterPacket.creationTimeMillis();
		if (DiameterUtility.isBaseProtocolPacket(diameterPacket.getCommandCode()) == false) {
			/*
			 * Case Description: 
			 * Traffic was observed from this Peer 
			 * few of the Packets are read and are in queue.
			 * Now, Network goes down, hence Peer is marked closed.
			 * So, the Packet already read are not processed and are dropped.
			 */
			if (isAlive() == false || isPeerConnected() == false) {
				LogManager.getLogger().warn(MODULE, "Dropping Packet with HbH-ID=" 
						+ diameterPacket.getHop_by_hopIdentifier() + "EtE-ID=" 
						+ diameterPacket.getEnd_to_endIdentifier() + ", Session-ID="
						+ diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID) + 
						" Reason, Peer: " + getHostIdentity() + " is Down.");
				stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
				return;
			}

			if(diameterPacket.isResponse()){
				
				convertFromRedirectHostFormat(stackContext, diameterPacket, getPeerData().getRedirectHostAVPFormat());

				RetransmissionHandler.DiameterRequestWrapper requestWrapper = retransmissionHandler.removePacket(diameterPacket.getHop_by_hopIdentifier());
				if (requestWrapper == null) {
					stackContext.updateUnknownH2HDropStatistics((DiameterAnswer) diameterPacket, getHostIdentity());
					LogManager.getLogger().warn(MODULE, "Dropping response. Reason: Request was timeout OR Possibly duplicate response for HbH: " 
							+ diameterPacket.getHop_by_hopIdentifier() + ", sessionID: "
							+ diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID)+" from peer " + getHostIdentity());
					return;
				}

				DiameterRequest diameterRequest = requestWrapper.getDiameterRequest();
				stateTransitionData.addObject(PeerDataCode.DIAMETER_PACKET_TO_SEND, diameterRequest);
				stateTransitionData.addObject(PeerDataCode.RESPONSE_LISTENER, requestWrapper.listener);

				long requestProcessingTime = timesource.currentTimeInMillis() - diameterRequest.getSendTime();
				if (requestProcessingTime > CommonConstants.MAX_RESPONSE_TIME_MS) {

					getLogger().warn(MODULE, new StringBuilder(200)
									.append("Diameter High Response Time: ")
									.append(requestProcessingTime)
									.append("ms from Peer: ")
									.append(getPeerName())
									.append(", for Session-ID=")
									.append(diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID))
									.append(", Packet-Type: ")
									.append(diameterPacket.getCommandCode())
									.append(", HbH-ID=")
									.append(diameterPacket.getHop_by_hopIdentifier())
									.append(", E2E-ID=")
									.append(diameterPacket.getEnd_to_endIdentifier()).toString()
							);

					Stack.generateAlert(StackAlertSeverity.INFO, 
							DiameterStackAlerts.DIAMETER_PEER_HIGH_RESPONSE_TIME, MODULE,
							new StringBuilder(200)
									.append("Diameter High Response Time: ")
									.append(requestProcessingTime)
									.append("ms from Peer: ")
									.append(getPeerName())
									.append(", for HbH-ID=")
									.append(diameterPacket.getHop_by_hopIdentifier())
									.append(", E2E-ID=")
									.append(diameterPacket.getEnd_to_endIdentifier())
									.toString(), (int)requestProcessingTime, getHostIdentity());

				} else {
					if(getLogger().isLogLevel(LogLevel.INFO)) {
						getLogger().info(MODULE, "Diameter Response Time: " + requestProcessingTime 
								+"ms from Peer: " + getPeerName()
								+", for Session-ID=" + diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID)
								+", Packet-Type: " + diameterPacket.getCommandCode());
					}
				}
			} else {
				DiameterRequest request = (DiameterRequest)diameterPacket;
				request.setPeerData(peerData);

				if(idealTime >= peerData.getRequestTimeout()){
					if(stackContext.getActionOnOverload() == OverloadAction.DROP){
						LogManager.getLogger().warn(MODULE, new StringBuilder(200)
								.append("Dropping request,")
								.append(" Package-Type:")
								.append(diameterPacket.getCommandCode())
								.append(". Reason: Request Timeout in queues for HbH: ")
								.append(diameterPacket.getHop_by_hopIdentifier())
								.append(", sessionID: ")
								.append(diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID))
								.append(" from peer ")
								.append(getHostIdentity())
								.append(" and Overload Action is drop")
								.toString());
						stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
						return;
					}

					int resultCodeOnOverload = stackContext.getOverloadResultCode();
					IDiameterAVP resultCode = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
					resultCode.setInteger(resultCodeOnOverload);
					DiameterPacket diameterAnswer = new DiameterAnswer((DiameterRequest)diameterPacket);
					diameterAnswer.addAvp(resultCode);

					try {
						LogManager.getLogger().warn(MODULE, new StringBuilder(200)
								.append("Sending ")
								.append(resultCodeOnOverload)
								.append(" response. Reason: Request Timeout in queues for HbH: ")
								.append(diameterPacket.getHop_by_hopIdentifier())
								.append(", sessionID: ")
								.append(diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID))
								.append(" from peer ")
								.append(getHostIdentity())
								.toString());

						writeToStream(diameterAnswer);
					} catch (Exception e) {
						if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
						stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
					}

					return;
				}

				if(isLoopDetected((DiameterRequest)diameterPacket)){
					handleLoopedRequest(diameterPacket);
					return;
					}
					}
				}
		diameterPacket.setQueueTime(idealTime);
		if (isSameConnection(connectionHandler)) {
		try {
			pcbStateMachine.onReceive(stateTransitionData);
		}catch(Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		}

		try {
			handleStateTransition(stateTransitionData);
		} catch (UnhandledTransitionException e) {

			LogManager.getLogger().trace(MODULE, e);
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, e.getMessage());
			if(diameterPacket.isRequest()) {
				DiameterAnswer diameterAnswer = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
				try {
					writeToStream(diameterAnswer);
					stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
				} catch (IOException io) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Unable to send" + CommandCode.fromCode(diameterAnswer.getCommandCode()) + " to " + getHostIdentity() + ". Reason: " + io.getMessage()); //TODO -ask Subhash
					LogManager.getLogger().trace(MODULE, io);
					stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
				}
			}
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Peer:" + getPeerName() + " Unknown Error: " + e.getMessage() + ". Sending DIAMETER_UNABLE_TO_COMPLY");
			LogManager.getLogger().trace(MODULE, e);
			if(diameterPacket.isRequest()) {
				DiameterAnswer diameterAnswer = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_UNABLE_TO_COMPLY);
				try {
					writeToStream(diameterAnswer);
					stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
				} catch (IOException io) { 
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, io.getMessage());
					LogManager.getLogger().trace(MODULE, io);
					stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
				}
			}
		}
	}
	
	private void handleLoopedRequest(DiameterPacket diameterPacket) {
		if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) { 
			LogManager.getLogger().warn(MODULE,"Reason: Loop detected for Peer: " + getPeerName() + 
					" for Diameter Request with Session-ID=" + 
					diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID) + 
					", Sending: " + ResultCode.DIAMETER_LOOP_DETECTED);
		}
		
		try {
			DiameterAnswer diameterAnswer = new DiameterAnswer((DiameterRequest)diameterPacket, ResultCode.DIAMETER_LOOP_DETECTED);
			writeToStream(diameterAnswer);
		} catch (Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			stackContext.updateDiameterStatsPacketDroppedStatistics(diameterPacket, getHostIdentity());
		}
	}
	
	/**
	 * checks loop by comparing value of route record with diameter stack's host identity
	 * @param diameterRequest
	 * @return boolean
	 */
	private boolean isLoopDetected(DiameterRequest diameterRequest){
		
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Checking Diameter Loop for Diameter Request with Session-Id=" + 
					diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
		
		List<IDiameterAVP> routeRecords = diameterRequest.getAVPList(DiameterAVPConstants.ROUTE_RECORD);

		if(routeRecords != null && routeRecords.size() > 0) {
			for(IDiameterAVP routeRecord : routeRecords){
				if(Parameter.getInstance().getOwnDiameterIdentity().equals(routeRecord.getStringValue())){
					return true;
				}
			}	
		}

		return false;
	}

	private IStateTransitionData getStateTransitionData() {
		IStateTransitionData stateTransitionData = new IStateTransitionData() {
			Map<IStateTransitionDataCode, Object> data = new HashMap<IStateTransitionDataCode, Object>();
			public Object getData(IStateTransitionDataCode key) {
				return data.get(key);
			}

			public void addObject(IStateTransitionDataCode key, Object value) {
				data.put(key, value);
			}
		};

		return stateTransitionData;
	}
	//	@Override
	//	public NetworkConnectionHandler getConnectionListener() {
	//
	//		return this.connectionListener;
	//	}

	public void handleStateTransition(IStateTransitionData stateTransitionData) throws UnhandledTransitionException{
		try {
			peerStateMachine.onStateTransitionTrigger(stateTransitionData);
		} catch (UnhandledTransitionException e) {
			throw e;
		}
	}

	@Override
	public void handleEvent(IEventEnum eventEnum, ConnectionEvents event, Map<PeerDataCode, String> eventParam) throws UnhandledTransitionException{
		try {
			IStateTransitionData stateTransitionData = getStateTransitionData();
			stateTransitionData.addObject(PeerDataCode.PEER_EVENT, eventEnum);
			if (eventParam != null){
				for (Map.Entry<PeerDataCode, String> paramEntry : eventParam.entrySet()){
					stateTransitionData.addObject(paramEntry.getKey(),paramEntry.getValue());
				}
			}
			handleStateTransition(stateTransitionData);
		} catch (UnhandledTransitionException e) {
			throw e;
		}
	}
	
	@Override
	public void handleEvent(IEventEnum eventEnum, ConnectionEvents event) throws UnhandledTransitionException{
		if(event == ConnectionEvents.HANDSHAKE_FAIL){
			if(isInitiateConnection()){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Initiate Connection with Peer :" + peerData.getHostIdentity() 
					+ ", IP : " + peerData.getRemoteIPAddress() + " disabled, Reason: Handshake Failed.");
			}
				setInitConnection(false);
		}
		}
			
		handleEvent(eventEnum, event, null);
	}

	@Override
	public String getRealm() {		
		return peerData.getRealmName();
	}

	private class DiameterPCBStateMachine extends PCBStateMachine {
		private volatile IStateTransitionData sendDwr;
		public DiameterPCBStateMachine(long watchDogTimerMs,int isInitiateConnection,IDiameterStackContext stackContext) {
			super(watchDogTimerMs,isInitiateConnection,stackContext);
		}
		
		@Override
		public void start() {
			super.start();
			createDWR();
		}

		private void createDWR(){
			DiameterRequest dwr = new DiameterRequest();
			dwr.setCommandCode(CommandCode.DEVICE_WATCHDOG.code);
			addAdditionalAVPs(peerData.getAdditionalDWRAvps(), dwr);
			IStateTransitionData sendDwr = getStateTransitionData();
			sendDwr.addObject(PeerDataCode.DIAMETER_PACKET_TO_SEND,dwr); 
			sendDwr.addObject(PeerDataCode.PEER_EVENT, DiameterPeerEvent.SendMessage);
			this.sendDwr = sendDwr;
		}
		
		@Override
		public void attemptOpen() {

			if(isInitiateConnection()){
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Trying to attempt connection open for peer: " + getPeerName());
				}
				attemptConnection();
			}
		}

		@Override
		public void closeConnection(ConnectionEvents event) {
			DiameterPeer.this.closeConnection(event);
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Connection closed with peer:" + getPeerName());
			}
			
		}

		@Override
		public void sendDPR() {
			DiameterPeer.this.handleEvent(DiameterPeerEvent.Stop, ConnectionEvents.CONNECTION_DPR);
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn (MODULE, "PCB event for connection close detected, DPR sent to peer: " + getPeerName());
			}
		}

		@Override
		public void onConnectionUp() {
			super.onConnectionUp();
			markOpen();
		}
		@Override
		public void sendWatchdog() {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Generating SendMessage DWR to peer:"+ getPeerName());
			}
			DiameterPacket dwr = ((DiameterPacket) sendDwr.getData(PeerDataCode.DIAMETER_PACKET_TO_SEND));
			dwr.setHop_by_hopIdentifier(HopByHopPool.get());
			dwr.setEnd_to_endIdentifier(EndToEndPool.get());
			handleStateTransition(sendDwr);
			}	

		@Override
		public void failback() {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Failback is called for peer:"+ getPeerName());
			}
			// isAlive.set(true);
			markOpen();
			
		}

		@Override
		public void failover() {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "Failover is called for peer:"+ getPeerName());
			}
			//isAlive.set(false);
			markClosed();

		}

		@Override
		public void throwaway() {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Throwaway is called for peer:"+ getPeerName());
			}

		}

		@Override
		protected String getPeerName() {
			return DiameterPeer.this.getPeerName();
		}

		@Override
		protected boolean isInitiateConnection() {
			return DiameterPeer.this.isInitiateConnection();

	}
	}

	@Override
	public void onConnectionDown() {
		this.pcbStateMachine.onConnectionDown();
	}

	@Override	
	public void onConnectionUp() {
		if((peerData.getSecurityStandard() == SecurityStandard.RFC_3588_TLS) || 
				(peerData.getSecurityStandard() == SecurityStandard.RFC_3588_DYNAMIC && remoteInbandSecurityIds.contains(InbandSecurityId.TLS))){
			try {
				this.connectionHandler.secureConnection(peerData, createEliteSSLContext());
				setInitConnection(true);
			}catch (HandShakeFailException e) {
				LogManager.getLogger().error(MODULE, "Error in creating TLS Socket. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Initiate Connection with Peer :" + peerData.getHostIdentity() 
					+ ", IP : " + peerData.getRemoteIPAddress() + " disabled, Reason: Handshake Failed.");
				}
				setInitConnection(false);
				closeConnection(ConnectionEvents.HANDSHAKE_FAIL);
				return;
			}
		}
		
		this.pcbStateMachine.onConnectionUp();
	}

	public boolean stop() {
		if(peerStateMachine.getCurrentState() == DiameterPeerState.I_Open.ordinal() || peerStateMachine.getCurrentState() == DiameterPeerState.R_Open.ordinal()){
			
			try {
				peerStateMachine.stop();
			} catch(Exception ex){
				getLogger().error(MODULE, "Error while stopping peer state machine. Reason: " + ex.getMessage());
				getLogger().trace(MODULE, ex);
			} 
			// Here no need to call stop of PCB StateMachine as onConnectionDown of peer will be called 
			// once peerStateMachine.stop(); is called 
		}
		
		if(scheduleThreadPoolExecutor != null){
			try{
				scheduleThreadPoolExecutor.shutdown();
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Waiting for DiameterPeer: " + peerData.getPeerName() +" level scheduled async task executor to complete execution");
				if(!scheduleThreadPoolExecutor.awaitTermination(2, TimeUnit.SECONDS)){
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Shutting down DiameterPeer: " + peerData.getPeerName() +" level scheduled async task executor forcefully. " +
						"Reason: Async task taking more than 2 second to complete");
					scheduleThreadPoolExecutor.shutdownNow();
				}
			}catch(Exception ex){ 
				ignoreTrace(ex);
				try{scheduleThreadPoolExecutor.shutdownNow();}catch(Exception e){ignoreTrace(e);} 
			}
		}
		
		return true;
	}
	
	@Override
	public boolean isSameConnection(NetworkConnectionHandler connectionHandler) {
		return (connectionHandler != null && connectionHandler.equals(this.connectionHandler));
	}
	
	@Override
	public void sendDiameterAnswer(DiameterAnswer diameterAnswer)throws UnhandledTransitionException{
		if ( isAlive() == false || isPeerConnected() == false ) {
			throw new UnhandledTransitionException("Diameter Peer: " + getHostIdentity() + " is Closed.");
		}
		
		IDiameterAVP originHost = diameterAnswer.getAVP(DiameterAVPConstants.ORIGIN_HOST);
		IDiameterAVP originRealm = diameterAnswer.getAVP(DiameterAVPConstants.ORIGIN_REALM);
		
		if (originHost != null && DiameterConstants.VIRTUAL_IDENTIFIER.equals(originHost.getStringValue())){
			originHost.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		}
		
		if (originRealm != null && DiameterConstants.VIRTUAL_IDENTIFIER.equals(originRealm.getStringValue())){
			originRealm.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		}

		convertToRedirectHostFormat(stackContext, diameterAnswer, this.peerData.getRedirectHostAVPFormat());
		

		IStateTransitionData stateTransitionData = createStateTransitionData();							
		stateTransitionData.addObject(PeerDataCode.DIAMETER_PACKET_TO_SEND, diameterAnswer);
		stateTransitionData.addObject(PeerDataCode.PEER_EVENT, DiameterPeerEvent.SendMessage);
		
		handleStateTransition(stateTransitionData);
	
	}
	
	@Override
	public void sendDiameterRequest(DiameterRequest diameterRequest, @Nonnull ResponseListener listener)throws UnhandledTransitionException{
		
		if ( isAlive() == false || isPeerConnected() == false ) {
			throw new UnhandledTransitionException("Diameter Peer: " + getHostIdentity() + " is Closed.");
		}
		
		IDiameterAVP originHost = diameterRequest.getAVP(DiameterAVPConstants.ORIGIN_HOST);
		IDiameterAVP originRealm = diameterRequest.getAVP(DiameterAVPConstants.ORIGIN_REALM);
		
		if (originHost != null && DiameterConstants.VIRTUAL_IDENTIFIER.equals(originHost.getStringValue())){
			originHost.setStringValue(Parameter.getInstance().getOwnDiameterIdentity());
		}
		
		if (originRealm != null && DiameterConstants.VIRTUAL_IDENTIFIER.equals(originRealm.getStringValue())){
			originRealm.setStringValue(Parameter.getInstance().getOwnDiameterRealm());
		}

		convertToRedirectHostFormat(stackContext, diameterRequest, this.peerData.getRedirectHostAVPFormat());
		

		IStateTransitionData stateTransitionData = createStateTransitionData();							
		stateTransitionData.addObject(PeerDataCode.DIAMETER_PACKET_TO_SEND, diameterRequest);
		
		stateTransitionData.addObject(PeerDataCode.PEER_EVENT, DiameterPeerEvent.SendMessage);
		
		stateTransitionData.addObject(PeerDataCode.RESPONSE_LISTENER, listener);
		handleStateTransition(stateTransitionData);
	
		
	}

	private IStateTransitionData createStateTransitionData() {
		return new IStateTransitionData() {
			Map<IStateTransitionDataCode, Object> data = new HashMap<IStateTransitionDataCode, Object>();
			@Override
			public Object getData(IStateTransitionDataCode code) {
				return data.get(code);
			}

			@Override
			public void addObject(IStateTransitionDataCode code, Object value) {
				data.put(code, value);
			}
		};
	}
	
	public List<IDiameterAVP> getAdditionalCERAvps() {
		return peerData.getAdditionalCERAvps();
	}
	
	public List<IDiameterAVP> getAdditionalDPRAvps() {
		return peerData.getAdditionalDPRAvps();
	}

	public InetAddress getLocalInetAddress() {
		return peerData.getLocalInetAddress();
	}

	public InetAddress getRemoteInetAddress() {
		return peerData.getRemoteInetAddress();
	}
	
	@Override
	public DiameterPeerState registerStatusListener(DiameterPeerStatusListener listener){
		if (listener != null){
			this.peerStatusListeners.add(listener);
		}
		
		
		
		return DiameterPeerState.fromStateOrdinal(peerStateMachine.currentState().stateOrdinal());
	}
	
	private void markOpen(){
		isAlive.set(true);
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Marking Peer: " + this.getPeerName() + " - " + this.getHostIdentity() + " Open");
		for(DiameterPeerStatusListener listener : peerStatusListeners){
			listener.markOpen();
		}
	}
	
	private void markClosed(){
		isAlive.set(false);
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Marking Peer: " + this.getPeerName() + " - " + this.getHostIdentity() + " Closed");
		for(DiameterPeerStatusListener listener : peerStatusListeners){
			listener.markClosed();
		}
	}

	public int getLocalPort() {
		return peerData.getLocalPort();
	}
	
	public TransportProtocols getTransportProtocol() {
		return peerData.getTransportProtocol();
	}

	
	public long getNextSequence() {
		return tranlationSeq.getAndAdd(1);
	}
	
	public void setRemoteIpAddress(String remoteIp) {
		peerData.setRemoteIPAddress(remoteIp);
	}

	public void setRemoteInetAddress(InetAddress remoteInetAddress) {
		peerData.setRemoteInetAddress(remoteInetAddress);
	}

	public void setRemotePort(int remotePort) {
		peerData.setRemotePort(remotePort);
	}

	public void setHostIdentity(String hostIdentity) {
		peerData.setHostIdentity(hostIdentity);
	}
	
	public String getLocalBoundAddress(){
		return connectionHandler.getLocalAddress();
	}
	@Override
	public boolean isSendDPRonCloseEvent() {
		return peerData.isSendDPRonCloseEvent();
	}
	
	private void addAdditionalAVPs(List<IDiameterAVP> additionalAVPs, DiameterPacket diameterPacket){
		if(additionalAVPs!=null){
			int noOfDPRAvps = additionalAVPs.size();
			for (int i=0 ; i<noOfDPRAvps ; i++){
				
				IDiameterAVP additionalAVP = additionalAVPs.get(i); 
				if (additionalAVP.getAVPId().equals(DiameterAVPConstants.DESTINATION_REALM) || 
						additionalAVP.getAVPId().equals(DiameterAVPConstants.DESTINATION_HOST) ||
						additionalAVP.getAVPId().equals(DiameterAVPConstants.ORIGIN_REALM) ||
						additionalAVP.getAVPId().equals(DiameterAVPConstants.ORIGIN_HOST)) {
					
					IDiameterAVP avp = diameterPacket.getAVP(additionalAVP.getAVPId());
					if (avp != null){
						avp.setStringValue(additionalAVP.getStringValue());
					} else {
						diameterPacket.addAvp(additionalAVP);
					}
				} else {
					diameterPacket.addAvp(additionalAVP);
				}
			}
		}
	}

	public List<IDiameterAVP> getAdditionalDWRAvps() {
		return peerData.getAdditionalDWRAvps();
	}
	
	public PeerData getPeerData(){
		return this.peerData;
	}
	
	public void addRemoteApplication(DiameterPacket diameterPacket){
		peerApplicationProvider.addRemoteApplication(diameterPacket);
	}

	public boolean addSecurityAVP(DiameterPacket diameterPacket){
		
		if(peerData.getSecurityStandard() == SecurityStandard.RFC_6733){
			return true;
		}
		
		for(InbandSecurityId securityId : localInbandSecurityIds){
			IDiameterAVP inbandSecurityAVP = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.INBAND_SECURITY_ID);
			if(inbandSecurityAVP == null){
				return false;
			}
			
			inbandSecurityAVP.setInteger(securityId.getCode());
			diameterPacket.addAvp(inbandSecurityAVP);
		}
		
		return true;
	}
	
	/**
	 * Redirect Host AVP Format --> DiameterURI Format
	 * 
	 * @param fromRedirectHostAVPFormat format is the one received in Redirect Host AVP value.
	 * @param stackContext stack Context
	 * 
	 */
	private void convertFromRedirectHostFormat(IStackContext stackContext, DiameterPacket packet, RedirectHostAVPFormat fromRedirectHostAVPFormat) {
		
		if(packet.isRequest()){
			return;
		}
		
		if(fromRedirectHostAVPFormat == RedirectHostAVPFormat.DIAMETERURI){
			return;
		}
		
		List<IDiameterAVP> redirectHostAVPs = packet.getAVPList(DiameterAVPConstants.REDIRECT_HOST);
		if(redirectHostAVPs == null) {
			return;
		}

		for(IDiameterAVP redirectHostAVP : redirectHostAVPs){
			String redirectHostAVPVal = redirectHostAVP.getStringValue();
			PeerData redirectHostData = null;
			redirectHostData = stackContext.getPeerData(redirectHostAVPVal);
			if(redirectHostData != null){
				redirectHostAVPVal = redirectHostData.getURI();
				redirectHostAVP.setStringValue(redirectHostAVPVal);
			}else{
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Peer Data could not be fetched for Redirect Host: " + redirectHostAVPVal + ". Sending Redirect-Host AVP value : " + redirectHostAVPVal);
				}
				continue;
			}
		}	
	}

	/**
	 * DiameterURI format --> Redirect Host AVp Format
	 * 
	 * @param toRedirectHostAVPFormat format is the one in which we want convert Redirect Host AVP value.
	 * @param stackContext stack Context
	 * 
	 */
	private void convertToRedirectHostFormat(IStackContext stackContext, DiameterPacket packet, RedirectHostAVPFormat toRedirectHostAVPFormat) {
		
		if(packet.isRequest()){
			return;
		}
		
		if(RedirectHostAVPFormat.DIAMETERURI == toRedirectHostAVPFormat){
			return;
		}
		
		List<IDiameterAVP> redirectHostAVPs = packet.getAVPList(DiameterAVPConstants.REDIRECT_HOST);
		if(redirectHostAVPs == null) {
			return;
		}

		for(IDiameterAVP redirectHostAVP : redirectHostAVPs){
			String redirectHostAVPVal = redirectHostAVP.getStringValue();
			PeerData redirectHostData = null;
			URIData uriData = DiameterURIParser.parse(redirectHostAVPVal);
			if(uriData == null){
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
					LogManager.getLogger().info(MODULE, "Unable to parse DiameterURI . Sending Redirect-Host AVP value : " + redirectHostAVPVal);
				}
				continue;
			}
			switch (toRedirectHostAVPFormat) {
			case HOSTIDENTITY:
				redirectHostAVPVal = uriData.getHost();				
				break;
			case IP:
				redirectHostData = stackContext.getPeerData(uriData.getHost());
				if(redirectHostData != null){
					redirectHostAVPVal = redirectHostData.getRemoteIPAddress();	
				}		
				break;
			default:
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Unable to form " + toRedirectHostAVPFormat + " for Redirect-Host value: " + redirectHostAVPVal);
				}
				break;
			}
			redirectHostAVP.setStringValue(redirectHostAVPVal);
		}	
	}
	private DiameterPeerConfig createPeerConfig() {

		return new DiameterPeerConfig() {

			@Override
			public String getPeerId() {
				return DiameterPeer.this.getHostIdentity(); 
			}

			@Override
			public int getPeerFirmwareRevison() {
				return DiameterPeer.this.getFirmwareRevision();
			}

			@Override
			public StorageTypes getPeerStorageType() {
				return StorageTypes.NON_VOLATILE;
			}

			@Override
			public RowStatus getPeerRowStatus() {

				RowStatus status = RowStatus.NOT_READY;
				if((DiameterPeer.this.getPeerStateMachine().currentState() == DiameterPeerState.I_Open) 
						|| DiameterPeer.this.getPeerStateMachine().currentState().stateOrdinal() == DiameterPeerState.R_Open.stateOrdinal())
					status = RowStatus.ACTIVE;
				return status;
			}

			@Override
			public int getDbpPeerPortListen() {
				return DiameterPeer.this.getCommunicationPort();
			}

			@Override
			public TransportProtocols getDbpPeerTransportProtocol() {

				TransportProtocols transportProtocol = TransportProtocols.TCP;
				TransportProtocols protocol = DiameterPeer.this.getTransportProtocol();
				if(protocol != null){
					transportProtocol = protocol;
				}
				return transportProtocol;
			}

			@Override
			public SecurityProtocol getDbpPeerSecurity() {
				SecurityProtocol security = SecurityProtocol.NONE; 

				NetworkConnectionHandler connectionHandler = DiameterPeer.this.connectionHandler;
				if(connectionHandler != null){
					security = connectionHandler.getSecurityProtocol();
				}
				return security;
			}

			@Override
			public int getDbpPeerPortConnect() {

				int connectPort = 0;

				NetworkConnectionHandler connectionHandler = DiameterPeer.this.connectionHandler;
				if(connectionHandler != null){
					connectPort = connectionHandler.getLocalPort();
				}
				return connectPort;
			}

			@Override
			public Set<ApplicationEnum> getDbpAppAdvFromPeer() {
				return DiameterPeer.this.getRemoteApplications();
			}

			@Override
			public DiameterBasePeerVendorTable[] getDbpPeerVendorTable() {
				List<DiameterBasePeerVendorTable> vendorTables = new ArrayList<DiameterBasePeerVendorTable>();
				List<String> supportedStrVendorList = DiameterPeer.this.getSupportedVendors();

				int vendorListIndex = 0;
				if(supportedStrVendorList!=null){
					for(int j=0 ; j< supportedStrVendorList.size() ; j++){

						if(supportedStrVendorList.get(j)!=null){
							vendorListIndex++;
							vendorTables.add(new DiameterBasePeerVendorTable(vendorListIndex,
									supportedStrVendorList.get(j),StorageTypes.NON_VOLATILE,RowStatus.ACTIVE));
						}
					}
				}
				return vendorTables.toArray(new DiameterBasePeerVendorTable[vendorTables.size()]);
			}

			@Override
			public DiameterBasePeerIpAddressTable[] getPeerIpAddressIndex() {
				List<DiameterBasePeerIpAddressTable> dbPeerIpList = new ArrayList<DiameterBasePeerIpAddressTable>();

				//TODO -- to be updated when we will support multiple IP Addr for single peer.
				NetworkConnectionHandler connectionHandler = DiameterPeer.this.connectionHandler;
				if(connectionHandler != null){
					String remoteIP = connectionHandler.getSourceIpAddress();
					if(remoteIP.length() > 0){
						int ipListIndex = 0;
						dbPeerIpList.add(new DiameterBasePeerIpAddressTable(ipListIndex++,IpAddressTypes.IPV4,remoteIP));
					}
				}
				return dbPeerIpList.toArray(new DiameterBasePeerIpAddressTable[dbPeerIpList.size()]);
			}

			@Override
			public String getPeerIpAddresses() {
				//TODO -- to be updated when we will support multiple IP Addr for single peer.
				NetworkConnectionHandler connectionHandler = DiameterPeer.this.connectionHandler;
				int port = DiameterPeer.this.getCommunicationPort();
				String ip = "";
				if(connectionHandler != null){
					ip = connectionHandler.getSourceIpAddress();
					if(ip.trim().length() == 0){
						ip = DiameterPeer.this.getPeerData().getRemoteIPAddress();
					}
				}else{
					ip = DiameterPeer.this.getPeerData().getRemoteIPAddress();
					if(Strings.isNullOrBlank(ip)){
						ip = DiameterPeer.this.getHostIdentity();
					}
				}
				return ip + "-" + port;
			}

			@Override
			public long getDbpPerPeerStatsTimeoutConnAtmpts() {
				return DiameterPeer.this.getTimeoutConnectionAttempts();
			}

			@Override
			public int getPCBState() {
				return DiameterPeer.this.getPCBState();
			}

			@Override
			public long getDbpPerPeerInfoStateDuration() {
				return DiameterPeer.this.getPeerStateChangedDuration();
			}

			@Override
			public int getPeerState() {
				return DiameterPeer.this.getPeerState();
			}

			@Override
			public long getPeerWatchDogInterval() {
				return DiameterPeer.this.getWatchdogInterval();
			}

			@Override
			public long getDbpPeerIndex() {
				return DiameterPeer.this.peerData.getPeerIndex();
			}

			@Override
			public Set<ApplicationEnum> getDbpAppAdvToPeer() {
				return DiameterPeer.this.getApplications();
			}

			@Override
			public boolean isConnectionInitiationEnabled() {
				return DiameterPeer.this.isInitiateConnection();
			}
			
			@Override
			public String getPeerLocalIpAddresses() {
				return DiameterPeer.this.getPeerData().getLocalIPAddress() + "-" + getLocalPort();
			}
		};
	}
	
	public class OverloadHandler{
		
		public void handle(DiameterRequest diameterRequest){

			if(stackContext.getActionOnOverload() == OverloadAction.DROP){
				LogManager.getLogger().warn(MODULE, "Dropping request," 
						+ " Package-Type:" + diameterRequest.getCommandCode() 
						+ ", HbH-ID: " + diameterRequest.getHop_by_hopIdentifier() 
						+ ", Session-ID: " + diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
						+ " from peer " + getHostIdentity() +". Reason: Overload Action is DROP");
				
				stackContext.updateDiameterStatsPacketDroppedStatistics(diameterRequest, getHostIdentity());
				return;
			}

			DiameterPacket diameterAnswer = new DiameterAnswer(diameterRequest);
			IDiameterAVP resultCode = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
			int resultCodeOnOverload = stackContext.getOverloadResultCode();
			resultCode.setInteger(resultCodeOnOverload);
			diameterAnswer.addAvp(resultCode);
			

			if (resultCode.getInteger() != ResultCode.DIAMETER_SUCCESS.code) {
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, 
						diameterAnswer, DiameterErrorMessageConstants.TPS_EXCEEDED);
			}
			
			try {
				LogManager.getLogger().warn(MODULE, "Sending "+ resultCodeOnOverload 
						+ " response for HbH: " + diameterRequest.getHop_by_hopIdentifier() 
						+ ", Session-ID: " + diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
						+ " from peer " + getHostIdentity()  +". Reason: Overload Action is REJECT");
				writeToStream(diameterAnswer);
			} catch (Exception e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
			
			return;
		}
	}

	public DiameterPeerConfig getPeerConfig() {
		return this.peerConfig;
	}
	
	public void reloadDiameterPeer(){
		this.pcbStateMachine.createDWR();
		this.peerStateMachine.reload();
	}
	
	/**
	 * 
	 * @return time In Millis when connection was attempted by this Peer.
	 */
	public long getPeerLastConnectionRequestTime() {
		return lastConnectionAttemptedTimestamp;
	}

	private static ThreadLocal<SimpleDateFormat> simpleDateFormatPool = new ThreadLocal<SimpleDateFormat>(){
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss S");
		};
	};

	@Override
	public String getLocalIpAddress() {
		return peerData.getLocalIPAddress();
	}
	
	public void recordMessageTxCountAndConnectionTime() {
		lastConnectionAttemptedTimestamp = timesource.currentTimeInMillis();
		messageTxCount.set(getCurrentMessageTxCount());
	}


	private long getCurrentMessageTxCount() {
		GroupedStatistics peerStatistics = stackContext.getDiameterStatisticsProvider() == null ?
				null : stackContext.getDiameterStatisticsProvider().getPeerStatsMap().get(getHostIdentity()) ;
		if (peerStatistics == null) {
			return 0;
		}
		return peerStatistics.getTotalRequestOutCount() + peerStatistics.getTotalAnswerOutCount();
	}
	
	public boolean isTrafficObservedOnCurrentConnection() {
		return getCurrentMessageTxCount() > messageTxCount.get();
	}


	public void start() {
		pcbStateMachine.start();
	}

	
}
