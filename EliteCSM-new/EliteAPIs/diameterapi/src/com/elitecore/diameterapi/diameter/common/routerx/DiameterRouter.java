package com.elitecore.diameterapi.diameter.common.routerx;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverNotFoundException;
import com.elitecore.core.commons.drivers.TypeNotSupportedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.core.common.peer.DiameterPeerCommunicator;
import com.elitecore.diameterapi.core.common.router.exception.RoutingFailedException;
import com.elitecore.diameterapi.core.common.transport.VirtualInputStream;
import com.elitecore.diameterapi.core.common.transport.VirtualOutputStream;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.ElementRegistrationFailedException;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslationAgent;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.routerx.agent.DiameterAgent;
import com.elitecore.diameterapi.diameter.common.routerx.agent.ProxyAgent;
import com.elitecore.diameterapi.diameter.common.routerx.agent.RedirectAgent;
import com.elitecore.diameterapi.diameter.common.routerx.agent.RelayAgent;
import com.elitecore.diameterapi.diameter.common.routerx.agent.VirtualAgent;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;
import com.elitecore.diameterapi.diameter.stack.IDiameterStackContext;
import com.elitecore.diameterapi.script.DiameterRouterScript;
import com.elitecore.diameterapi.script.manager.DiameterScriptsManager;

// TODO test dummy translation mapping flow.
public class DiameterRouter {
	
	private static final String MODULE = "DIA-RTR";
	private RouterContext routerContext;
	private IDiameterStackContext stackContext;
	private List<RoutingEntry> routingEntryList;
	private Map<String, RoutingEntry> routingEntryMap;
	private List<RoutingEntryData> routingEntryDataList;
	private ProxyAgent proxyAgent;
	private RedirectAgent redirectAgent;
	private RelayAgent relayAgent;
	private VirtualAgent virtualAgent;
	private VirtualInputStream virtualInputStream;
	private final String virtualPeerName = "VirtualRoutingPeer";
	private Map<String, List<IDiameterAVP>> translationDummyMappings;
	private Set<ApplicationEnum> supportedRemoteApplications;
	private String groovy;

	private String routingTableName;
	private ITranslationAgent translationAgent;
	private IDiameterSessionManager diameterSessionManager = null;
	
	public DiameterRouter(IDiameterStackContext stackContext, List<RoutingEntryData> routingEntryDataList) {
		this(stackContext, routingEntryDataList, TranslationAgent.getInstance());
	}
	
	public DiameterRouter(IDiameterStackContext stackContext,
			List<RoutingEntryData> routingEntryDataList, ITranslationAgent translationAgent) {
		this.stackContext = stackContext;
		this.routingEntryList = new ArrayList<RoutingEntry>();
		this.routingEntryMap = new HashMap<String, RoutingEntry>();
		this.routingEntryDataList = routingEntryDataList;
		this.translationDummyMappings = new HashMap<String, List<IDiameterAVP>>();
		this.translationAgent = translationAgent;
		createRouterContext();
	}

	/**
	 * This method initializes Diameter Router.
	 */
	public void init(){
	
		initRoutingEntries();
		initAgents();
		
		registerVirtualRoutingPeer();
		initGroovy();
	}

	private void createRouterContext() {
		this.routerContext = new RouterContext(){

			@Override
			public PeerData getPeerData(String hostIdentity) {
				return DiameterRouter.this.getPeerData(hostIdentity);
			}

			@Override
			public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity) {
				return DiameterRouter.this.getPeerCommunicator(hostIdentity);
			}

			public String getVirtualRoutingPeerName() {
				return DiameterRouter.this.virtualPeerName;
			}

			@Override
			public void updateUnknownH2HDropStatistics(DiameterAnswer answer,
					String hostIdentity, String realmName, RoutingActions routeAction) {
				DiameterRouter.this.stackContext.updateUnknownH2HDropStatistics(answer, hostIdentity, realmName, routeAction);
			}

			@Override
			public void updateDiameterStatsPacketDroppedStatistics(
					DiameterPacket packet, String hostIdentity,
					String realmName, RoutingActions routeAction) {
				DiameterRouter.this.stackContext.updateDiameterStatsPacketDroppedStatistics(packet, hostIdentity, realmName, routeAction);
			}

			@Override
			public void updateRealmInputStatistics(DiameterPacket packet,
					String realmName, RoutingActions routeAction) {
				DiameterRouter.this.stackContext.updateRealmInputStatistics(packet, realmName, routeAction);
			}

			@Override
			public void updateRealmOutputStatistics(DiameterPacket packet,
					String realmName, RoutingActions routeAction) {
				DiameterRouter.this.stackContext.updateRealmOutputStatistics(packet, realmName, routeAction);
			}

			@Override
			public void postRequestRouting(DiameterRequest originRequest,
					DiameterRequest destinationRequest, String originPeerId,
					String destPeerId, String routingEntryName) {
				DiameterRouter.this.postRequestRouting(originRequest, destinationRequest, 
						originPeerId, destPeerId, routingEntryName);
				
			}

			@Override
			public void preAnswerRouting(DiameterRequest originRequest,
					DiameterRequest destinationRequest,
					DiameterAnswer originAnswer, String originPeerId,
					String routingEntryName) {
				DiameterRouter.this.preAnswerRouting(originRequest, destinationRequest, 
						originAnswer, originPeerId, routingEntryName);
				
			}

			@Override
			public void postAnswerRouting(DiameterRequest originRequest,
					DiameterRequest destinationRequest,
					DiameterAnswer originAnswer,
					DiameterAnswer destinationAnswer, String originPeerId,
					String destPeerId, String routingEntryName) {
				DiameterRouter.this.postAnswerRouting(originRequest, destinationRequest, 
						originAnswer, destinationAnswer, 
						originPeerId, destPeerId, routingEntryName);
				
			}

			@Override
			public CDRDriver<DiameterPacket> getDiameterCDRDriver(String name) throws DriverInitializationFailedException, DriverNotFoundException, TypeNotSupportedException {
				return DiameterRouter.this.stackContext.getDiameterCDRDriver(name);
			}

			@Override
			public void updateRealmTimeoutRequestStatistics(DiameterRequest destinationRequest, String realmName,
					RoutingActions routingAction) {
				DiameterRouter.this.stackContext.updateRealmTimeoutRequestStatistics(destinationRequest, realmName, routingAction);
			}
			
			@Override
			public RoutingEntry getRoutingEntry(String routingEntryName) {
				return routingEntryMap.get(routingEntryName);
			}

		};
	}
	
	private void initGroovy() {
		if(groovy == null || groovy.trim().length() == 0){
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "No Groovy registered for Diameter Router.");
			groovy = null;
			return;
		}
		
		groovy = groovy.trim();
		try {
			DiameterScriptsManager.getInstance().execute(groovy, DiameterRouterScript.class, "init", null, null);
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Groovy: " + groovy + " initialized.");
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Error occured while initializing Groovy: " + groovy +
				", Reason: " + e.getMessage());
			groovy = null;
		}
	
	}

	/**
	 * Registers a virtual Routing peer to the DiameterStack
	 * Virtual Routing Peer is used for dummy translations
	 */
	private void registerVirtualRoutingPeer() {
		PeerDataImpl peerData = new PeerDataImpl();
		peerData.setPeerName(virtualPeerName);
		peerData.setHostIdentity("virtual.routing.diameter.peer");
		peerData.setRemoteIPAddress("localhost");
		peerData.setInitiateConnectionDuration(0);
		peerData.setWatchdogInterval(0);

		VirtualOutputStream outpurStream = new VirtualOutputStream() {

			@Override
			public void send(Packet diaPacket) {
				DiameterRequest diameterRequest = (DiameterRequest) diaPacket;

				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Request received by " + virtualPeerName + diameterRequest.toString());

				DiameterAnswer diameterAnswer = new DiameterAnswer(diameterRequest);
				String translatorName = (String) diameterRequest.getParameter(TranslatorConstants.SELECTED_TRANSLATION_POLICY);

				if (translatorName != null) {
					List<IDiameterAVP> dummyResponseAVPs = translationDummyMappings.get(translatorName);
					if (dummyResponseAVPs == null) {
						dummyResponseAVPs = formDummyAnswerAVPs(translatorName);
					}
					
					if (dummyResponseAVPs != null) {
						diameterAnswer.addAvps(dummyResponseAVPs);
					} 
				} else {
					// TranslatorConstants.SELECTED_TRANSLATION_POLICY must be set by proxy agent while sending request 
					// to dummy peer. If flow reached here its a coding error
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
						LogManager.getLogger().info(MODULE, "Translation mapping not provided for Dummy Answer with Session-ID: " + 
						diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
				}
				diameterRequest.setParameter(TranslatorConstants.DUMMY_MAPPING, null);
				sendVirtualAnswer(diameterAnswer);
			}
		};
		
		try {
			virtualInputStream = stackContext.registerVirtualPeer(peerData, outpurStream).getInputStream();
		} catch (ElementRegistrationFailedException e) { 
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Registration of Diameter Routing virtual peer failed. Reason: " + e.getMessage());
			ignoreTrace(e);
		}
	}
	
	/**
	 * This method sends Dummy Answer to Stack 
	 * @param diameterAnswer Answer to send back to DiameterStack
	 */
	private void sendVirtualAnswer(DiameterAnswer diameterAnswer) {
		if (virtualInputStream != null) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Sending Answer to Diameter stack " + diameterAnswer.toString());
			virtualInputStream.received(diameterAnswer);
		}
		else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Diameter Virtual Routing Inputstream unavailable"); 
			}
		}
	}
	
	/**
	 * 
	 */
	private List<IDiameterAVP> formDummyAnswerAVPs(String transMapName) {
		
		Map<String,String> dummyMappings = translationAgent.getDummyResponseMap(transMapName);
		if (dummyMappings == null || dummyMappings.isEmpty()) {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "No Dummy Mappings defined for translation policy: " + transMapName);
			return null;
		}
		List<IDiameterAVP> dummyAVPs = new ArrayList<IDiameterAVP>();
		for(Entry<String,String>  mapping: dummyMappings.entrySet()){
			IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(mapping.getKey());
			if (diameterAVP != null) {
				diameterAVP.setStringValue(mapping.getValue());
				dummyAVPs.add(diameterAVP);
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Dummy attribute: " + mapping.getKey() + "defined for translation policy: " + 
					transMapName + " is not available in Dictionary");
			}
		}
		return dummyAVPs;
	}
	/**
	 * Initialize Diameter Agents
	 */
	private void initAgents(){
		proxyAgent = new ProxyAgent(routerContext, translationAgent, diameterSessionManager);
		relayAgent = new RelayAgent(routerContext, diameterSessionManager);
		redirectAgent = new RedirectAgent(routerContext);
		virtualAgent = new VirtualAgent(routerContext, diameterSessionManager);
	}
	/**
	 * Initialize Routing Entries.
	 */
	private void initRoutingEntries() {
		routingTableName = Parameter.getInstance().getRoutingTableName();

		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Routing Entry initialization started for Routing Table: " + routingTableName);
		}
		
		ArrayList<RoutingEntry> routingEntryList = new ArrayList<RoutingEntry>();
		HashMap<String, RoutingEntry> routingEntryMap = new HashMap<String, RoutingEntry>();
		
		for(RoutingEntryData routingEntryData  : routingEntryDataList){

			if(routingEntryData == null){
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Routing Entry Initialization failed. Reason: Routing Entry Data not Found.");
				continue;
			}
			try{
				RoutingEntry routingEntry = new RoutingEntry(routingEntryData, routerContext, translationAgent);
				routingEntry.init();
				routingEntryList.add(routingEntry);
				routingEntryMap.put(routingEntryData.getRoutingName(), routingEntry);
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Routing Entry: " + routingEntry.getRoutingEntryName() + 
					" initialized.");
			}  catch (InitializationFailedException e) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Routing Entry: " + routingEntryData.getRoutingName() +
					" will not be considered, as it failed to initalize. Reason: " + e.getMessage());
				}
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		this.routingEntryList = routingEntryList; 
		this.routingEntryMap = routingEntryMap;
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "All Routing Entry initialization completed.");
	}

	/**
	 * Returns all Remote Applications supported by this router 
	 */
	public Set<ApplicationEnum> getSupportedRemoteApplications() {
		if (this.supportedRemoteApplications == null) {
			Set<ApplicationEnum> remoteApplications = new HashSet<ApplicationEnum>();

			for (int i=0 ; i<routingEntryList.size() ; i++) {
				ApplicationEnum[] apps = routingEntryList.get(i).getSupportedApplications();
				if ( apps != null ) {
					for (int j=0 ; j<apps.length ; j++) { 
						remoteApplications.add(apps[j]);
					}
				}
			}
			this.supportedRemoteApplications = remoteApplications;
		}
		return supportedRemoteApplications;
	}
	/**
	 * 
	 * @param diameterPacket
	 * @param diameterSession
	 * @return
	 * @throws RoutingFailedException if unable to route Diameter Packet
	 */
	public RoutingActions processDiameterRequest(DiameterRequest diameterRequest, DiameterSession diameterSession) throws RoutingFailedException{

		if (diameterRequest.isServerInitiated()) {
			return processServerInitiatedRequest(diameterRequest, diameterSession);
		} else {
			return processClientInitiatedDiameterRequest(diameterRequest, diameterSession);
		}
	}
	
	/**
	 * 
	 * @param diameterRequest
	 * @param diameterSession
	 * @return
	 * @throws RoutingFailedException if unable to route Diameter Packet
	 */
	private RoutingActions processClientInitiatedDiameterRequest(DiameterRequest diameterRequest, DiameterSession diameterSession) throws RoutingFailedException{

		String sessionID = diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID);
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Diameter Packet received for Session-Id=" + sessionID);

		RoutingActions action = RoutingActions.PROXY;

		
		action = processRequest((DiameterRequest)diameterRequest, diameterSession);
		return action;
	}
	
	/**
	 * 
	 * @param diameterRequest
	 * @param diameterSession
	 * @return
	 * @throws RoutingFailedException if unable to route Diameter Request
	 */
	private RoutingActions processRequest(DiameterRequest diameterRequest, DiameterSession diameterSession) throws RoutingFailedException{

		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Processing Diameter Request for Session-Id=" + 
			diameterSession.getSessionId());

		preRequestRouting(diameterRequest, diameterRequest.getRequestingHost());
		
		RoutingActions action = RoutingActions.PROXY;

		RoutingEntry routingEntry = selectRoutingEntry(diameterRequest);
		
		if(routingEntry != null){
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Routing Entry " + routingEntry.getRoutingEntryName() + 
				" Selected for Session-ID=" + diameterSession.getSessionId());
			action = submitRequestToAgent(routingEntry, diameterSession, diameterRequest);
		}else{
			//Routing Entry not found => Check for Local Process
			if(isEligibleForLocalProcess(diameterRequest)){

				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Locally Processing Diameter Request for Session-Id=" + 
					diameterSession.getSessionId());
				action = RoutingActions.LOCAL;
			}else{
				//Packet not Eligible for Local --> UNABLE_TO_DELIVER
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_DELIVER + 
					", Reason: Routing entry not found for Diameter Request with Session-ID=" + 
					diameterSession.getSessionId());
				throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_DELIVER,
						RoutingActions.LOCAL,
						DiameterErrorMessageConstants.ROUTE_NOT_FOUND);
			}
		}
		return action;
	}
	
	/**
	 * Select Routing Entry based on Diameter Request.
	 * 
	 * @param diameterRequest
	 * @return respective {@link RoutingEntry}
	 */
	private RoutingEntry selectRoutingEntry (DiameterRequest diameterRequest) {

		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Selecting Routing Entry for Diameter Request with Session-ID=" +
			diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));

		for (int i=0 ; i < routingEntryList.size() ; i++) {

			if (routingEntryList.get(i).isApplicable(diameterRequest)) {
				return routingEntryList.get(i);	
			}
		}
		return null;
	}
	
	private RoutingActions submitRequestToAgent(RoutingEntry routingEntry, DiameterSession diameterSession, 
			DiameterRequest diameterRequest) throws RoutingFailedException {

		RoutingActions action = routingEntry.getRoutingAction();
		
		if (routingEntry.isRoutingEntryExecutable() == false) {
			throw new RoutingFailedException(ResultCode.DIAMETER_UNABLE_TO_COMPLY, action, diameterRequest, DiameterErrorMessageConstants.routingFailed(routingEntry.getRoutingEntryName()));
		}
		
		if (action == RoutingActions.LOCAL) {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Locally Processing Diameter Request for Session-Id=" + 
				diameterSession.getSessionId());
			return RoutingActions.LOCAL;
		}
		PeerData originPeer = stackContext.getPeerData(diameterRequest.getRequestingHost());
		if(originPeer != null){
			stackContext.updateRealmInputStatistics(diameterRequest, 
					originPeer.getRealmName(), 
					routingEntry.getRoutingAction());
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Submitting Diameter Request to Diameter Agent for Session-Id=" + 
			diameterSession.getSessionId());
		switch (action) {
		case PROXY:
			proxyAgent.routeRequest(diameterRequest, diameterSession, routingEntry);
			break;
		case REDIRECT:
			redirectAgent.routeRequest(diameterRequest, diameterSession, routingEntry);
			break;
		case RELAY:
			relayAgent.routeRequest(diameterRequest, diameterSession, routingEntry);
			break;		
		case VIRTUAL:
			virtualAgent.routeRequest(diameterRequest, diameterSession, routingEntry);
			break;	
		default:
			LogManager.getLogger().error(MODULE, "Invalid routing action selected for Session-ID=" + 
			diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			break;
		}
		return action;
	}
	
	private boolean isEligibleForLocalProcess (DiameterRequest diameterRequest) {
		
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Checking for Local processing Eligibility of Diameter Request with Session-Id=" + 
			diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
		
		String destRealm = diameterRequest.getAVPValue(DiameterAVPConstants.DESTINATION_REALM);
		if (destRealm != null) {
			if (destRealm.equalsIgnoreCase(Parameter.getInstance().getOwnDiameterRealm()) == false) {
				return false;
			}
		}
		
		String destHost = diameterRequest.getAVPValue(DiameterAVPConstants.DESTINATION_HOST);
		if (destHost != null) {
			if (destHost.equalsIgnoreCase(Parameter.getInstance().getOwnDiameterIdentity()) == false) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param hostIdentity can be Peer Name, Host Identity or IP of Peer
	 * @return Peer Data for Peer
	 */
	public PeerData getPeerData(String hostIdentity) {
		return this.stackContext.getPeerData(hostIdentity);
	}

	/**
	 * This method creates if not and returns {@link PeerCommunicator} of the Peer. 
	 * Also maintains a Map of Peer Communicator for Diameter Router.
	 *  
	 * @param hostIdentity hostIdentity, IP or Name of the Peer for which you need Peer Communicator
	 * @return Peer Communicator for the peer
	 */
	public DiameterPeerCommunicator getPeerCommunicator(String hostIdentity) {
		PeerData peerdata = stackContext.getPeerData(hostIdentity);
		if(peerdata == null){
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Peer " + hostIdentity + " is not available.");
			return null;
		}
		DiameterPeerCommunicator peerCommunicator = stackContext.getPeerCommunicator(hostIdentity);
		return peerCommunicator;
	}
	
	private void preRequestRouting (DiameterRequest originRequest, String originPeerId){

		if(groovy == null){
			return;
		}
		try {
			DiameterScriptsManager.getInstance().execute(
					groovy, DiameterRouterScript.class, "preRequest", 
					new Class<?>[]{String.class, DiameterRequest.class, String.class}, 
					new Object[]{routingTableName, originRequest, originPeerId});
			
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error in Pre-Request Routing Process while executing Groovy: " + groovy +
				", Reason: " +  e.getMessage());
			}
		}
	}
	
	private void postRequestRouting (DiameterRequest originRequest, DiameterRequest destinationRequest, 
			String originPeerId, String destPeerId, String routingEntryName){
		if(groovy == null){
			return;
		}
		try {
			DiameterScriptsManager.getInstance().execute(
					groovy, DiameterRouterScript.class, "postRequest", 
					new Class<?>[]{String.class, String.class, DiameterRequest.class, String.class, DiameterRequest.class,  String.class}, 
					new Object[]{routingTableName, routingEntryName, originRequest, originPeerId, destinationRequest, destPeerId });

		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error in Post-Request Routing Process while executing Groovy: " + groovy +
				", Reason: " +  e.getMessage());
			}
		}
	}
	
	private void preAnswerRouting (DiameterRequest originRequest, DiameterRequest destinationRequest, 
			DiameterAnswer originAnswer, String originPeerId, String routingEntryName){
		if(groovy == null){
			return;
		}
		try {
			DiameterScriptsManager.getInstance().execute(
					groovy, DiameterRouterScript.class, "preAnswer", 
					new Class<?>[]{String.class, String.class, DiameterRequest.class, String.class, DiameterRequest.class, DiameterAnswer.class}, 
					new Object[]{routingTableName, routingEntryName, originRequest, originPeerId, destinationRequest, originAnswer});

		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error in Pre-Answer Routing Process while executing Groovy: " + groovy +
				", Reason: " +  e.getMessage());
			}
		}
	}
	
	private void postAnswerRouting (DiameterRequest originRequest, DiameterRequest destinationRequest, 
			DiameterAnswer originAnswer, DiameterAnswer destinationAnswer, 
			String originPeerId, String destPeerId, String routingEntryName){
		if(groovy == null){
			return;
		}
		try {
			DiameterScriptsManager.getInstance().execute(
					groovy, DiameterRouterScript.class, "postAnswer", 
					new Class<?>[]{String.class, String.class, DiameterRequest.class, String.class, DiameterRequest.class, DiameterAnswer.class, DiameterAnswer.class, String.class}, 
					new Object[]{routingTableName, routingEntryName, originRequest, originPeerId, destinationRequest, originAnswer, destinationAnswer, destPeerId});

		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error in Post-Answer Routing Process while executing Groovy: " + groovy +
				", Reason: " +  e.getMessage());
			}
		}
	}
	
	public void setGroovy(String groovy) {
		this.groovy = groovy;
	}
	
	public void registerDiameterSessionManager(IDiameterSessionManager diameterSessionManager){
		this.diameterSessionManager = diameterSessionManager;
	}

	public void reInit(List<RoutingEntryData> routingEntryDataList) {
		this.routingEntryDataList = routingEntryDataList;
		initRoutingEntries();
	}

	public void registerPriorityRoutingEntry(RoutingEntryData routingEntryData) {
		try{
			RoutingEntry routingEntry = new RoutingEntry(routingEntryData, routerContext, translationAgent);
			routingEntry.init();
			this.routingEntryDataList.add(0, routingEntryData);
			this.routingEntryList.add(0, routingEntry);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Routing Entry: " + routingEntry.getRoutingEntryName() + 
				" initialized.");
			}
		}catch(Exception e){
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Error occured while initializing Routing Entry: " + routingEntryData.getRoutingName() +
				", it will not be added, Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	/**
	 * Process Server Initiated Request
	 * --> Appropriate routing Session Fetching
	 * --> Loop Detection
	 * --> Submit to Respective Agent
	 * @param diameterRequest
	 * @return
	 * @throws RoutingFailedException
	 */
	private RoutingActions processServerInitiatedRequest(DiameterRequest diameterRequest, DiameterSession session) 
			throws RoutingFailedException {

		String sessionID = diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID);
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Processing Server Initiated Request for Session-Id=" + 
					sessionID);
		}

		String routingEntryName = (String) session.getParameter(DiameterAgent.ROUTING_ENTRY);
		RoutingEntry routingEntry = routerContext.getRoutingEntry(routingEntryName);
		if(routingEntry == null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Locally Processing Server Initiated Request with Session-ID=" + 
						sessionID + ". Reason, Routing Session not found.");
			}
			return RoutingActions.LOCAL;
		}
		preRequestRouting(diameterRequest, diameterRequest.getRequestingHost());
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Routing Entry " + routingEntry.getRoutingEntryName() + 
					" Selected for Session-ID=" + diameterRequest.getSessionID());
		}
		return submitServerInitiatedRequestToAgent(routingEntry, session, diameterRequest);
	}
	
	private RoutingActions submitServerInitiatedRequestToAgent(
			RoutingEntry routingEntry, DiameterSession routingSession,
			DiameterRequest diameterRequest) throws RoutingFailedException {

		RoutingActions action = routingEntry.getRoutingAction();
		if (action == RoutingActions.LOCAL) {
			if (LogManager.getLogger().isInfoLogLevel())
				LogManager.getLogger().info(MODULE, "Locally Processing Diameter Request for Session-Id=" + 
						routingSession.getSessionId());
			return RoutingActions.LOCAL;
		}
		PeerData originPeer = stackContext.getPeerData(diameterRequest.getRequestingHost());
		if (originPeer != null) {
			stackContext.updateRealmInputStatistics(diameterRequest, 
					originPeer.getRealmName(), 
					routingEntry.getRoutingAction());
		}
		if (LogManager.getLogger().isDebugLogLevel())
			LogManager.getLogger().debug(MODULE, "Submitting Diameter Request to Diameter Agent for Session-Id=" + 
					routingSession.getSessionId());
		/*
		 * As this Submits Server Initiated Requests, REDIRECT Action is -NA-
		 */
		switch (action) {
		case PROXY:
			proxyAgent.routeServerInitiatedRequest(diameterRequest, routingSession);
			break;
		case RELAY:
			relayAgent.routeServerInitiatedRequest(diameterRequest, routingSession);
			break;	
		case VIRTUAL:
			virtualAgent.routeServerInitiatedRequest(diameterRequest, routingSession);
			break;	
		default:
			LogManager.getLogger().error(MODULE, "Invalid routing action selected for Session-ID=" + 
					diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
			break;
		}
		return action;
	}
}
