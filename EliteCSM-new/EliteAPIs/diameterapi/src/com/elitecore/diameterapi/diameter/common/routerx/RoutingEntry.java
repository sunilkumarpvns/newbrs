package com.elitecore.diameterapi.diameter.common.routerx;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.translator.ITranslationAgent;
import com.elitecore.diameterapi.diameter.common.data.DiameterFailoverConfiguration;
import com.elitecore.diameterapi.diameter.common.data.RoutingEntryData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.peers.DiameterPeerCommunicatorGroup;
import com.elitecore.diameterapi.diameter.common.peers.PeerSelector;
import com.elitecore.diameterapi.diameter.common.routerx.failure.DropFailureAction;
import com.elitecore.diameterapi.diameter.common.routerx.failure.FailoverFailureAction;
import com.elitecore.diameterapi.diameter.common.routerx.failure.PassthroughFailureAction;
import com.elitecore.diameterapi.diameter.common.routerx.failure.RecordFailureAction;
import com.elitecore.diameterapi.diameter.common.routerx.failure.RedirectFailureAction;
import com.elitecore.diameterapi.diameter.common.routerx.failure.RoutingFailureAction;
import com.elitecore.diameterapi.diameter.common.routerx.failure.TranslateFailureAction;
import com.elitecore.diameterapi.diameter.common.routerx.selector.ChainPeerGroupSelector;
import com.elitecore.diameterapi.diameter.common.routerx.selector.RuleBasedPeerGroupSelector;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

/**
 * Routing Entry contains all necessary data that is required for processing from Routing Entry Data.
 * This data includes:<br />
 * 
 * 1. Set of  <code>DiameterApplicationIdentifier</code>s<br />
 * 2. <code>RoutingActions</code> <br />
 * 3. <code>LogicalExpression</code><br />
 * 4. Map of <code> Integer, DiameterRoutingFailureHandler</code> i.e. errorCode --> failureHandler <br />
 * 5. List of <code> DiameterPeerCommunicatorGroup</code>s i.e. List of Peer Communicators <br />
 * 6. <code>RoutingEntryData</code> <br />
 * 7. <code>DiameterRouterContext</code> <br />
 * 
 * <br />Note: It is responsibility of Diameter Router to initialize RoutingEntry.
 * @author monica.lulla
 *
 *See also<br />
 *{@link DiameterApplicationIdentifier}<br />
 *{@link RoutingActions}<br />
 *{@link LogicalExpression}<br />
 *{@link RoutingFailureAction}<br />
 *{@link DiameterPeerCommunicatorGroup}<br />
 *{@link RoutingEntryData}<br />
 *{@link RouterContext}<br />
 *
 *
 */
public class RoutingEntry {
	
	
	private static final String MODULE = "ROUTING-ENTRY";
	private ApplicationEnum[] applicationIds;
	private RoutingActions routingAction;	
	private LogicalExpression advancedCondition;
	private Map<Integer, RoutingFailureAction> failureHandlerMap;
	// contains list of Peer Comm Groups. 
	private RoutingEntryData routingEntryData;
	private RouterContext routerContext;
	private String warning;
	private PeerSelector peerSelector;
	private String[] destRealms;
	private String[] originRealms;
	private String[] originHostIps;
	private String[] destExpRealms;
	private String[] originExpRealms;
	private static final String ASTERISK_STR = "*";
	private static final String DEFAULT_APP_ID = "0";
	private static final long DEFAULT_VENDOR_ID_LONG = 0;
	private boolean ALLOW_ALL_DEST_REALMS = false;
	private boolean ALLOW_ALL_ORIGIN_REALMS = false;
	private boolean ALLOW_ALL_ORIGIN_IPS = false;
	private ITranslationAgent translationAgent;

	public RoutingEntry(RoutingEntryData routingEntryData, RouterContext diameterRouterContext, ITranslationAgent translationAgent) {
		this.routingEntryData = routingEntryData;
		this.routerContext = diameterRouterContext;
		this.translationAgent = translationAgent;
		this.failureHandlerMap = new HashMap<Integer, RoutingFailureAction>();
	}

	public void init() throws InitializationFailedException {
		List<String> warnings = new ArrayList<String>();
		
		
		initDestRealms();
		initOriginRealms();
		initOriginHostIps();
		
		//init Routing Action..
		
		this.routingAction = RoutingActions.fromRoutingAction(routingEntryData.getRoutingAction());

		// Check configured translation policy existence, if configured
		if (routingEntryData.getTransMapName() != null && routingEntryData.getTransMapName().trim().length() > 0) {
			if (translationAgent.isExists(routingEntryData.getTransMapName()) == false) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Configured translation policy: " + routingEntryData.getTransMapName() + 
					" for Routing Entry: " + routingEntryData.getRoutingName() + " is not registered");
				warnings.add("Configured translation policy: " + routingEntryData.getTransMapName() + " is not registered");
			}
		}
		
		//init Application IDs
		initApplicationIds(warnings);

		//init Advanced Condition i.e. Ruleset
		initAdvancedCondition(warnings);

		//init Peer Selector
		initPeerSelector(warnings);
		
		//init Failure Handlers
		initFailureActions(warnings);
		
		warning = buildWarningMsg(warnings);
	}
	
	private void initDestRealms() {
		String [] tempDestRealms = ParserUtility.splitString(routingEntryData.getDestRealm(), ',', ';');
		List<String> staticDestRealms = new ArrayList<String>();
		List<String> expDestRealms = new ArrayList<String>();
		if(tempDestRealms == null 
				|| containsString(tempDestRealms, ASTERISK_STR) 
				|| tempDestRealms.length == 0){
			ALLOW_ALL_DEST_REALMS = true;
		}else{
			for(int i=0; i< tempDestRealms.length; i++){
				if(ParserUtility.containsChar(tempDestRealms[i].toCharArray(), '*'))
					expDestRealms.add(tempDestRealms[i].trim());
				else
					staticDestRealms.add(tempDestRealms[i].trim());
			}
			this.destRealms = staticDestRealms.toArray(new String[staticDestRealms.size()]);
			this.destExpRealms = expDestRealms.toArray(new String[expDestRealms.size()]);
		}
	}
	
	private void initOriginRealms() {
		String [] tempOriginRealms = ParserUtility.splitString(routingEntryData.getOriginRealm(), ',', ';');
		List<String> staticOriginRealms = new ArrayList<String>();
		List<String> expOriginRealms = new ArrayList<String>();
		if(tempOriginRealms == null 
				|| containsString(tempOriginRealms, ASTERISK_STR) 
				|| tempOriginRealms.length == 0){
			ALLOW_ALL_ORIGIN_REALMS = true;
		}else{
			for(int i=0; i< tempOriginRealms.length; i++){
				if(ParserUtility.containsChar(tempOriginRealms[i].toCharArray(), '*'))
					expOriginRealms.add(tempOriginRealms[i].trim());
				else
					staticOriginRealms.add(tempOriginRealms[i].trim());
			}
		}
		this.originRealms = staticOriginRealms.toArray(new String[staticOriginRealms.size()]);
		this.originExpRealms = expOriginRealms.toArray(new String[expOriginRealms.size()]);
	}
	
	private void initOriginHostIps() {
		
		originHostIps = ParserUtility.splitString(routingEntryData.getOriginHostIp(), ',', ';');
		if(originHostIps == null 
				|| containsString(originHostIps, ASTERISK_STR) 
				|| originHostIps.length == 0){
			ALLOW_ALL_ORIGIN_IPS = true;
		}else{
			for(int i=0; i< originHostIps.length; i++){
				originHostIps[i] = originHostIps[i].trim();
			}
		}
	}

	private void initPeerSelector(List<String> warnings) {
		
		ChainPeerGroupSelector chainPeerGroupSelector = 
				new ChainPeerGroupSelector();
		if(routingEntryData.getSubscriberBasedRoutingTableDataList() != null) {
			for(SubscriberBasedRoutingTableData subscriberBasedRoutingTableData : routingEntryData.getSubscriberBasedRoutingTableDataList()) {
				chainPeerGroupSelector.add(subscriberBasedRoutingTableData.createSelector(routerContext));
			}
		}
		chainPeerGroupSelector.add(new RuleBasedPeerGroupSelector(
				routingEntryData.getPeerGroupList(), routerContext));
		
		/*A Boolean Arg is Sent to Peer selector 
		because if Routing Action = REDIRECT AND Attached Redirection = false/disabled,
		we do not add Status Listener for peer communicator group*/
		
		boolean addlistener = true;
		if(routingAction == RoutingActions.REDIRECT){
			addlistener = routingEntryData.getAttachedRedirection();
		}
		try {
			chainPeerGroupSelector.init(addlistener);
		} catch (InitializationFailedException e) { 
			ignoreTrace(e);
			warnings.add(e.getMessage());
		}
		peerSelector = new PeerSelector(chainPeerGroupSelector, routerContext);
	}
	
	/**
	 * Initialization of Failure Actions for Routing Entry.
	 * @param warnings 
	 */
	private void initFailureActions(List<String> warnings){
		if(routingEntryData.getFailoverDataList() == null){
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "No Failure Configuartion defined for RoutingEntry: " 
				+ getRoutingEntryName());
			return;
		}
		for(DiameterFailoverConfiguration failureConfiguration: routingEntryData.getFailoverDataList()){
			String errorCode = failureConfiguration.getErrorCodes();
			RoutingFailureAction failureAction = null;
			switch (failureConfiguration.getFailoverAction()) {
			case FAILOVER:
				failureAction = new FailoverFailureAction(routerContext, failureConfiguration.getFailoverArguments(), 
						routingEntryData.getTransActionTimeOut(), peerSelector);
				break;
			case DROP:
				failureAction = new DropFailureAction();
				break;
			case PASSTHROUGH:
				failureAction = new PassthroughFailureAction();
				break;
			case REDIRECT:
				failureAction = new RedirectFailureAction(routerContext, failureConfiguration.getFailoverArguments(), 
						routingEntryData.getAttachedRedirection(), peerSelector);
				break;
			case TRANSLATE:
				String failureArgs = failureConfiguration.getFailoverArguments(); 
				if(failureArgs == null)
					failureArgs = routingEntryData.getTransMapName();
				failureAction = new TranslateFailureAction(failureArgs, translationAgent);
				break;
			case RECORD:
				failureArgs = failureConfiguration.getFailoverArguments(); 
				failureAction = new RecordFailureAction(routerContext,failureArgs);
				break;
			default:
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid Failure Action: " + failureConfiguration.getFailoverAction() + 
					" configured for Error Code(s): "+ errorCode +" in Routing Entry: "+ routingEntryData.getRoutingName());
				warnings.add("Invalid Failure Action: " + failureConfiguration.getFailoverAction() + 
						" for Error Code(s): " + errorCode);
				break;
			}
			if(failureAction != null){
				failureAction.init();
				if(failureAction.getWarnings() != null)
					warnings.addAll(failureAction.getWarnings());
			}
			if(errorCode != null && errorCode.trim().length() >0){
				String[] errorCodesArray;
				errorCodesArray = errorCode.split(",");
				for(int s = 0 ; s < errorCodesArray.length; s++){
					try{
						int	errorCodeValue = Integer.parseInt(errorCodesArray[s].trim());	
						if(errorCodeValue < 0){
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
								LogManager.getLogger().warn(MODULE, "Invalid Error Code: "+ errorCodesArray[s] + 
								" in Routing Entry: "+ routingEntryData.getRoutingName());
							warnings.add("Invalid Error code: "+ errorCodesArray[s]);
						}else{
							failureHandlerMap.put(errorCodeValue, failureAction);
						}
					}catch(NumberFormatException e){
						if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "Invalid Error Code: "+ errorCodesArray[s] + " in Routing Entry: "+ routingEntryData.getRoutingName());
						//this is invalid error code so not adding failure handler for this..
						warnings.add("Invalid Error code: "+ errorCodesArray[s]);
					}
				}
			} else {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "No Error code configured for " + failureConfiguration.getFailoverAction() + 
							" in Routing Entry: "+ routingEntryData.getRoutingName());
				}
				warnings.add("No Error code configured for " + failureConfiguration.getFailoverAction());
			}
		}
	}
	
	/**
	 * Initialization of Application IDs of Routing Entry
	 * @param warnings 
	 */
	private void initApplicationIds(List<String> warnings) {
		Set<ApplicationEnum> diameterApps = new HashSet<ApplicationEnum>();
		String applicationIdValue = routingEntryData.getApplicationIds();
		if(applicationIdValue == null || applicationIdValue.trim().length() == 0){
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Routing Entry: " + routingEntryData.getRoutingName() + 
				" will work for any Application, Reason: Application ID is not configured");
			return;
		}
		String[] strApplicationIds = applicationIdValue.split(",");
		for(int k = 0; k <strApplicationIds.length; k++){

			strApplicationIds[k] = strApplicationIds[k].trim();
			if(DEFAULT_APP_ID.equals(strApplicationIds[k])){
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Routing Entry: " + routingEntryData.getRoutingName() + 
					" will work for any Application, Reason: Application ID: " + DEFAULT_APP_ID + " is configured.");
				return;
			}
			
			long lAppId = -1;
			try{
				lAppId = Long.parseLong(strApplicationIds[k]);
				if(lAppId < 0){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid Application ID: " + strApplicationIds[k] + 
						" is configured for Routing Entry: " + routingEntryData.getRoutingName());
					warnings.add("Invalid Application ID: " + strApplicationIds[k]);
					continue;
				}
			}catch(NumberFormatException e){
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid Application ID: " + strApplicationIds[k] + 
					" is configured for Routing Entry: " + routingEntryData.getRoutingName());
				warnings.add("Invalid Application ID: " + strApplicationIds[k]);
				continue;
			}
			ApplicationEnum appEnum = ApplicationIdentifier.fromApplicationIdentifiers(lAppId);
			if(appEnum == null){
				final long applicationID = lAppId;
				appEnum = new ApplicationEnum() {

					@Override
					public long getVendorId() {
						return DEFAULT_VENDOR_ID_LONG;
					}

					@Override
					public long getApplicationId() {
						return applicationID;
					}

					@Override
					public Application getApplication() {
						return Application.UNKNOWN;
					}

					@Override
					public ServiceTypes getApplicationType() {
						return ServiceTypes.BOTH;
					}
					
					@Override
					public String toString() {
						return new StringBuilder()
						.append(getVendorId())
						.append(":")
						.append(getApplicationId())
						.append(" [").append(getApplication().getDisplayName()).append("]").toString();
					}
				};
			}
			diameterApps.add(appEnum);	
		}
		applicationIds = new ApplicationEnum[diameterApps.size()];
		applicationIds = diameterApps.toArray(applicationIds);
	}

	/**
	 * Initialization of Advanced String for Routing Entry 
	 * could not be parsed. 
	 * @param warnings
	 * @throws InitializationFailedException 
	 */
	private void initAdvancedCondition(List<String> warnings) throws InitializationFailedException {
		String advancedCondition = routingEntryData.getAdvancedCondition();
		if(advancedCondition == null || advancedCondition.trim().length() == 0 ){
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "No Advanced Condition configured for Routing Entry: " + routingEntryData.getRoutingName());
			return;
		}
		try {
			Compiler compiler = Compiler.getDefaultCompiler();
			LogicalExpression logicalExpression;
			logicalExpression = compiler.parseLogicalExpression(advancedCondition);
			this.advancedCondition = logicalExpression;
		} catch (InvalidExpressionException e) { 				
			throw new InitializationFailedException("Incorrect Advanced Condition: " + advancedCondition +
					" configured. Reason:  " + e.getMessage(), e);
		}
	}
	
	/**
	 * This method states that whether Routing Entry is applicable to this Diameter Packet
	 * based on AppId, originatorIp, AdvanceCondition
	 * 
	 * @param diameterPacket
	 * 
	 * @return true if Routing Entry is applicable to this Packet.
	 */
	public boolean isApplicable(DiameterPacket diameterPacket) {

		if(ALLOW_ALL_DEST_REALMS == false){
			String packetDestRealm = diameterPacket.getAVPValue(DiameterAVPConstants.DESTINATION_REALM);
			if(packetDestRealm != null){
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Comparing Destination Realm(Packet): " + packetDestRealm + 
					" with Destination Realms(Routing Entry): " + routingEntryData.getDestRealm());
				if (containsString(destRealms, packetDestRealm.trim()) == false){
					if(containsPattern(destExpRealms, packetDestRealm.trim()) == false)
						return false;
				}
			}
		}
		//Checking Application identifier if configured in routing entry
		if (applicationIds != null) {
			
			boolean isApplicationSatisfied = false;
			for(ApplicationEnum applicationIdentifier : applicationIds){
				if (diameterPacket.getApplicationID() == applicationIdentifier.getApplicationId()) {
					isApplicationSatisfied = true;
					break;
				}
			}
			
			if (isApplicationSatisfied == false){
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Application: " + diameterPacket.getApplicationID() + " not served by Routing Entry: " + 
					routingEntryData.getRoutingName()+ " for Diameter Request with Session-ID=" + 
					diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
				return false;
			}
		}
		
		if(ALLOW_ALL_ORIGIN_REALMS == false){
			String packetOriginRealm = diameterPacket.getAVPValue(DiameterAVPConstants.ORIGIN_REALM);
			if(packetOriginRealm != null){
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Comparing Origin Realm(Packet): " + packetOriginRealm + 
					" with Origin Realms(Routing Entry): " + routingEntryData.getOriginRealm());
				if (containsString(originRealms, packetOriginRealm) == false){
					if(containsPattern(originExpRealms, packetOriginRealm) == false)
						return false;
				}
			}
		}
		// Checking Origin IP (IF diameterPacket contains info AVP EC_SOURCE_IP_ADDRESS 
		//and realmData contains Origin IP)
		if(ALLOW_ALL_ORIGIN_IPS == false){
			String packetOriginHostIp = diameterPacket.getAVPValue(DiameterAVPConstants.EC_SOURCE_IP_ADDRESS, true);
			if ((packetOriginHostIp != null && packetOriginHostIp.trim().length() > 0)){
				if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Comparing Originator Host IP(Packet): " + packetOriginHostIp + 
					" with Originator Host IPs (Routing Entry): " + routingEntryData.getOriginHostIp());
				if (containsString(originHostIps, packetOriginHostIp) == false){
					return false;
				}
			}
		}
		//Checking Rule Set
		if(advancedCondition != null && (advancedCondition.evaluate(new DiameterAVPValueProvider(diameterPacket)) == false)){
			if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Ruleset not satisfied of Routing Entry: " + 
				routingEntryData.getRoutingName() + " for Diameter Request with Session-ID=" + 
				diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID));
			return false;
		}
		
		String destHostIdentity = diameterPacket.getAVPValue(DiameterAVPConstants.DESTINATION_HOST);
		if (destHostIdentity != null && peerSelector.isKnown(destHostIdentity) == false){
			
			if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace(MODULE, "Required Destination-Host(Packet): " + destHostIdentity + 
						" is not available in Routing Entry: " + routingEntryData.getRoutingName());
			}
			return false;
		}
		return true;
	}
	
	private String buildWarningMsg(List<String> warnings) {
		if (warnings.size() == 0) 
			return null; 

		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		int count = 1;

		printWriter.println();
		for(String warning : warnings){
			printWriter.println("	" + (count++) + ". " + warning);
		}
		return stringWriter.toString();
	}
	
	public String getRoutingEntryName() {
		return this.routingEntryData.getRoutingName();
	}
	
	/**
	 * 
	 * @return Routing Action
	 */
	public RoutingActions getRoutingAction() {
		return routingAction;
	}

	/**
	 * 
	 * @return Translation Mapping Name of Routing Entry
	 */
	public String getTranslationMapping() {
		return routingEntryData.getTransMapName();
	}
	
	/**
	 * 
	 * @return Peer Selector of Routing Entry
	 */
	public PeerSelector getPeerSelector() {
		return peerSelector;
	}

	/**
	 * 
	 * @return true if Stateful Routing Enabled
	 */
	public boolean isStatefulRoutingEnabled() {
		return routingEntryData.getStatefulRouting();
	}
	
	/**
	 * Gives Failure Action for Result Code
	 * if Failure Action for given result code is not available, 
	 * it will give Failure Action based on Result Code Category.
	 * 
	 * <br />Note: for DIAMETER_REDIRECT_INDICATION i.e Result Code 3006, 
	 * it will not fetch Failure Action based on Result Code Category 
	 * 
	 * @param resultCode
	 * @return Failure Action 
	 */
	public RoutingFailureAction getFailureAction(int resultCode) {
		
		RoutingFailureAction failureAction = failureHandlerMap.get(resultCode);
		if(failureAction == null){
			ResultCodeCategory category = ResultCodeCategory.getResultCodeCategory(resultCode);
			failureAction = failureHandlerMap.get(category.value);
		}
		return failureAction;
	}
	

	/**
	 * Supports Pattern in Lookup String<br />
	 * 
	 * Ex:   he*lo --> helllo, helo, hellllllo, heghfhgfhglo<br />
	 * 
	 * @param strings
	 * @param lookupString
	 * @return true if lookupString is available in strings
	 */
	private boolean containsPattern (String[] strings, String lookupString) {
		if(lookupString != null){
			for(int i=0 ; i<strings.length ; i++){
				if(strings[i] != null && DiameterUtility.matches(lookupString, strings[i])){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @return true if lookupString is available in strings
	 */
	private boolean containsString (String[] strings, String lookupString) {
		if(lookupString != null){
			for(int i=0 ; i< strings.length ; i++){
				if(strings[i] != null && strings[i].equals(lookupString)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns remote applications supported by this routing entry 
	 */
	public ApplicationEnum[] getSupportedApplications() {
		return applicationIds;
	}
	
	/**
	 * @return false if there were any configuration error(s) while initializing routing entry, otherwise true. 
	 */
	public boolean isRoutingEntryExecutable() {
		if (warning == null) {
			return true;
		}

		if (LogManager.getLogger().isWarnLogLevel()) {
			LogManager.getLogger().warn(MODULE, "Routing Entry: " + routingEntryData.getRoutingName() +  
					" is not being applied due to following configuration Error(s): " + warning);
		}
		return false;
	}
}