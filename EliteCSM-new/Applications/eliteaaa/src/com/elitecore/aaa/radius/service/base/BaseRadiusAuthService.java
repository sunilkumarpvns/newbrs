/*
 *  EliteAAA Server
 *
 *  Elitecore Technologies Ltd., 904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 3rd August 2010 by Ezhava Baiju Dhanpal
 *  
 */


package com.elitecore.aaa.radius.service.base;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.aaa.core.conf.VSAInClassConfiguration;
import com.elitecore.aaa.core.conf.impl.KeyValuePair;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.eap.session.EAPSessionId;
import com.elitecore.aaa.core.policies.accesspolicy.AccessPolicyManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.core.util.cli.SetCommand.ConfigurationSetter;
import com.elitecore.aaa.mibs.radius.authentication.client.RadiusAuthClientMIB;
import com.elitecore.aaa.mibs.radius.authentication.client.autogen.RADIUS_AUTH_CLIENT_MIB;
import com.elitecore.aaa.mibs.radius.authentication.client.autogen.TableRadiusAuthServerTable;
import com.elitecore.aaa.mibs.radius.authentication.client.extended.RADIUS_AUTH_CLIENT_MIBImpl;
import com.elitecore.aaa.mibs.radius.authentication.client.extended.RadiusAuthServerEntryMBeanImpl;
import com.elitecore.aaa.mibs.radius.authentication.client.extended.TableRadiusAuthServerTableImpl;
import com.elitecore.aaa.mibs.radius.authentication.server.RadiusAuthServiceMIBListener;
import com.elitecore.aaa.mibs.radius.authentication.server.autogen.RADIUS_AUTH_SERVER_MIB;
import com.elitecore.aaa.mibs.radius.authentication.server.autogen.TableRadiusAuthClientTable;
import com.elitecore.aaa.mibs.radius.authentication.server.extended.RADIUS_AUTH_SERVER_MIBImpl;
import com.elitecore.aaa.mibs.radius.authentication.server.extended.RadiusAuthClientEntryMBeanImpl;
import com.elitecore.aaa.mibs.radius.authentication.server.extended.TableRadiusAuthClientTableImpl;
import com.elitecore.aaa.radius.conf.RadAuthConfiguration;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusPolicyManager;
import com.elitecore.aaa.radius.policies.servicepolicy.RadiusServicePolicy;
import com.elitecore.aaa.radius.policies.servicepolicy.VendorSpecificInternalServicePolicy;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyConfigurable;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.RadiusServicePolicyData;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceSummaryWriter;
import com.elitecore.aaa.radius.service.auth.RadiusAuthServiceMIBCounters;
import com.elitecore.aaa.radius.service.auth.policy.AuthServicePolicyFactory;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.aaa.radius.sessionx.conf.impl.LocalSessionManagerConfiguration.BehaviourType;
import com.elitecore.aaa.radius.util.cli.AuthServCommand;
import com.elitecore.aaa.radius.util.converters.IPrepaidConverter;
import com.elitecore.aaa.radius.util.converters.PrepaidConverterFactory;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.util.cli.cmd.ICommand;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreeap.fsm.eap.IEapStateMachine;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.sun.management.snmp.SnmpStatusException;


/**
 * 
 * @author baiju
 *
 */
public abstract class BaseRadiusAuthService extends BaseRadiusService<RadAuthRequest, RadAuthResponse> {

	private static final String MODULE = "BS-RAD-AUTH";
	
    private ArrayList<RadiusServicePolicy<RadAuthRequest, RadAuthResponse>> authServicePolicies;
    private RadAuthConfiguration radAuthConfiguration ;
    private RadiusAuthServiceMIBListener authServiceMIBListener;
	private EliteRollingFileLogger serviceLogger;

	//FIXME ELITEAAA-2767 statistic for request related counter was not updated properly when net-mask client is configured
	// so need to add radclient here but remove when proper solution is given.
	private RadClientConfiguration radClientConfiguration;
    
    public BaseRadiusAuthService(AAAServerContext context, 
    		DriverManager driverManager,
    		RadPluginManager pluginManager,
    		RadiusLogMonitor logMonitor) {
		super(context, driverManager, pluginManager, logMonitor);
		authServiceMIBListener = new RadiusAuthServiceMIBListener(new RadiusAuthServiceMIBCounters(context));
		radAuthConfiguration = context.getServerConfiguration().getAuthConfiguration();
		radClientConfiguration =  context.getServerConfiguration().getRadClientConfiguration();
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {	
	}
	
	
	@Override
	protected void initService() throws ServiceInitializationException{
		super.initService();
		LogManager.getLogger().info(MODULE, String.valueOf(radAuthConfiguration));
		if (radAuthConfiguration.isServiceLevelLoggerEnabled()) {			
			serviceLogger = 
				new EliteRollingFileLogger.Builder(getServerContext().getServerInstanceName(),
						radAuthConfiguration.getLogLocation())
				.rollingType(radAuthConfiguration.logRollingType())
				.rollingUnit(radAuthConfiguration.logRollingUnit())
				.maxRolledUnits(radAuthConfiguration.logMaxRolledUnits())
				.compressRolledUnits(radAuthConfiguration.isCompressLogRolledUnits())
				.sysLogParameters(radAuthConfiguration.getSysLogConfiguration().getHostIp(),
						radAuthConfiguration.getSysLogConfiguration().getFacility())
				.build();
			serviceLogger.setLogLevel(radAuthConfiguration.logLevel());
			
			LogManager.setLogger(getServiceIdentifier(), serviceLogger);
		}

		try {
			RadiusPolicyManager.getInstance(RadiusConstants.RADIUS_AUTHORIZATION_POLICY).initCache(getServiceContext().getServerContext(), false);
		} catch (ManagerInitialzationException e) {
			LogManager.getLogger().error(MODULE,"Error Caching Radius-Policies. Reason :" + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		try {
			AccessPolicyManager.getInstance().initCache(getServiceContext().getServerContext().getServerHome());
			registerCacheable(AccessPolicyManager.getInstance());
		} catch (ManagerInitialzationException e) {
			LogManager.getLogger().error(MODULE,"Error Caching Access-Policies. Reason :" + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		LogManager.getLogger().info(MODULE,"Radius Authentication service initilized successfully.");
		initServicePolicy();
		LogManager.getLogger().info(MODULE, servicePolicyCacheToString());

		RadClientConfiguration radClientConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getRadClientConfiguration();
		if(radClientConfiguration != null){
			this.authServiceMIBListener.init();
		}else{
			LogManager.getLogger().info(MODULE, "Can not initialize MIB counters for clients. Client configuration is null");			
		}

		registerServiceSummaryWriter(new RadAuthServiceSummaryWriter((RadAuthServiceContext)getServiceContext()));

		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

		try {
			RADIUS_AUTH_CLIENT_MIB radius_AUTH_CLIENT_MIB = new RADIUS_AUTH_CLIENT_MIBImpl();
			radius_AUTH_CLIENT_MIB.populate(mbeanServer, null);
			TableRadiusAuthServerTable authClientTable = new TableRadiusAuthServerTableImpl(radius_AUTH_CLIENT_MIB,mbeanServer);
			Map<String, String> serverMap = RadiusAuthClientMIB.getServerMap();
			int serverIndex = 1;
			String authServerIdentity = "";
			try{

				for (Entry<String, String> entry : serverMap.entrySet()) {
					RadiusAuthServerEntryMBeanImpl radiusAuthServerEntry = new RadiusAuthServerEntryMBeanImpl(serverIndex++, entry.getValue());
					authServerIdentity = entry.getValue();
					authClientTable.addEntry(radiusAuthServerEntry,new ObjectName(radiusAuthServerEntry.getObjectName()));
				}
			}catch(SnmpStatusException sse){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Auth Server: "+authServerIdentity+" in Auth Server table. Reason: "+sse.getMessage());
				}
				LogManager.getLogger().trace(sse);
			}catch (MalformedObjectNameException ex) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Auth Server: "+authServerIdentity+" in Auth Server table. Reason: "+ex.getMessage());
				}
				LogManager.getLogger().trace(ex);
			}
			RadiusAuthClientMIB.setAuthClientTable(authClientTable);
			((AAAServerContext)getServerContext()).registerSnmpMib(radius_AUTH_CLIENT_MIB);
		} catch (Throwable t) {
			LogManager.getLogger().error(MODULE, "RADIUS Authentication clinet MIB registration failed. Reason: " + t.getMessage());
			LogManager.getLogger().trace(MODULE, t);
		}
	
		try {
			RADIUS_AUTH_SERVER_MIB radius_AUTH_SERVER_MIB = new RADIUS_AUTH_SERVER_MIBImpl();
			radius_AUTH_SERVER_MIB.populate(mbeanServer, null);
			TableRadiusAuthClientTable authClientTable = new TableRadiusAuthClientTableImpl(radius_AUTH_SERVER_MIB,mbeanServer);

			Map<String, String> clientMap = RadiusAuthServiceMIBListener.getClientMap();
			int clientIndex = 1;
			String authClientIdentity = "";
			try{
				for (Entry<String, String> entry : clientMap.entrySet()) {
					RadiusAuthClientEntryMBeanImpl authClientEntry = new RadiusAuthClientEntryMBeanImpl(clientIndex++, entry.getValue());
					authClientIdentity = entry.getValue();
					authClientTable.addEntry(authClientEntry,new ObjectName(authClientEntry.getObjectName()));
				}
			}catch(SnmpStatusException sse){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Auth Client: "+authClientIdentity+" in Auth Client table. Reason: "+sse.getMessage());
				}
				LogManager.getLogger().trace(sse);
			}catch (MalformedObjectNameException ex) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error while adding Entry for Auth Client: "+authClientIdentity+" in Auth Client table. Reason: "+ex.getMessage());
				}
				LogManager.getLogger().trace(ex);
			}
			RadiusAuthServiceMIBListener.setAuthClientTable(authClientTable);
			((AAAServerContext)getServerContext()).registerSnmpMib(radius_AUTH_SERVER_MIB);
		} catch (Throwable t) {
			LogManager.getLogger().error(MODULE, "RADIUS Authentication server MIB registration failed. Reason: " + t.getMessage());
			LogManager.getLogger().trace(MODULE, t);
		}
	}

	@Override
	public void reInit() {
		radAuthConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getAuthConfiguration();
		this.radClientConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getRadClientConfiguration();
		reInitLogLevel();
//		reInitServicePolicy();
		super.reInit();
	}
	
	//TODO reinitialization is temporarily out of service
	private void reInitServicePolicy() {
		ArrayList<RadiusServicePolicy<RadAuthRequest, RadAuthResponse>> tmpAuthServicePolicies= new ArrayList<RadiusServicePolicy<RadAuthRequest, RadAuthResponse>>();	
		try{
			RadiusServicePolicy<RadAuthRequest, RadAuthResponse> vendorSpecificServicePolicy  = getServicePolicy(VendorSpecificInternalServicePolicy.NAME);
			if(vendorSpecificServicePolicy!=null){
				tmpAuthServicePolicies.add(vendorSpecificServicePolicy);
			}
			
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Failed to Re-Initialize the Service Policy " + VendorSpecificInternalServicePolicy.NAME + ". Reason : " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
		
		authServicePolicies = tmpAuthServicePolicies;
	}

	private void reInitLogLevel() {
		if(radAuthConfiguration.isServiceLevelLoggerEnabled()){
			serviceLogger.setLogLevel(radAuthConfiguration.logLevel());
		}
	}

	private void initServicePolicy(){
		ArrayList<RadiusServicePolicy<RadAuthRequest, RadAuthResponse>> tmpAuthServicePolicies= new ArrayList<RadiusServicePolicy<RadAuthRequest, RadAuthResponse>>();	
		RadiusServicePolicy<RadAuthRequest, RadAuthResponse> servicePolicy = null;
		try{
			servicePolicy = new VendorSpecificInternalServicePolicy((RadAuthServiceContext)getServiceContext());
			servicePolicy.init();
			tmpAuthServicePolicies.add(servicePolicy);				
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Failed to Initialize the Service Policy " + servicePolicy.getPolicyName() + ". Reason : " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
		
		RadiusServicePolicyConfigurable policyConfigurable = ((AAAServerContext)getServerContext()).getServerConfiguration().getRadiusServicePolicyConfiguration();
	
		AuthServicePolicyFactory servicePolicyFactory = new AuthServicePolicyFactory((RadAuthServiceContext) getServiceContext());
		for(RadiusServicePolicyData policyData : policyConfigurable.getAuthenticationFlowPolicies()) {
			if(policyData.getSupportedMessages().isAuthenticationMessageEnabled()) {
				try {
					tmpAuthServicePolicies.add(servicePolicyFactory.create(policyData.getAuthenticationPolicyData()));
				} catch(Exception e) {
					LogManager.getLogger().error(MODULE, "Failed to Initialize the Service Policy " +  policyData.getName() + ". Reason : " + e.getMessage());
					LogManager.getLogger().trace(e);
				}
			} else {
				LogManager.getLogger().info(MODULE, "Authentication message is disabled for radius service policy: " +  policyData.getName());
			}
		}
	
		authServicePolicies = tmpAuthServicePolicies;		
	}
	
	@Override
	public void preRequestProcess(RadAuthRequest request, RadAuthResponse response){		
		super.preRequestProcess(request, response);
		addEAPAttributeAsInfoAttributeFrom(request);
	}
	
	
	private void addEAPAttributeAsInfoAttributeFrom(RadAuthRequest request){
		//adding the code for including the Elitecore-EAP-Attribute
		Collection<IRadiusAttribute> eapMessageAttributes = request.getRadiusAttributes(RadiusAttributeConstants.EAP_MESSAGE);

		if(eapMessageAttributes != null && eapMessageAttributes.size() > 0){
			byte[] eapMessageBytes = getBytesFromRadiusAtrCollection(eapMessageAttributes);
			try {
				EAPPacket eapPacket = new EAPPacket(eapMessageBytes);
				IRadiusAttribute eapCodeAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_EAP_CODE);
				if(eapCodeAttr != null){
					eapCodeAttr.setIntValue(eapPacket.getCode());
					request.addInfoAttribute(eapCodeAttr);
				}
				List<EAPType> eapTypes = eapPacket.getEAPTypes();
				if(eapTypes != null && eapTypes.size() > 0){
					IRadiusAttribute eapMethodAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_EAP_METHOD);
					if(eapMethodAttribute != null ){
						eapMethodAttribute.setIntValue(eapTypes.get(0).getType());
						request.addInfoAttribute(eapMethodAttribute);
					}
				}

			} catch (InvalidEAPPacketException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Invalid EAP Packet received, ELITE-EAP-CODE (21067:211) and ELITE-EAP-CODE (21067:212) will not be added.");
				}
			}
		}
	}
	
	private byte[] getBytesFromRadiusAtrCollection(Collection<IRadiusAttribute> radiusAttributeCollection) {
		
		int length = 0;
		Collection<byte[]> byteArrayCollection = new ArrayList<byte[]>();
		Iterator<IRadiusAttribute> attrItr = radiusAttributeCollection.iterator();
		while(attrItr.hasNext()){
			IRadiusAttribute temp = attrItr.next();
			byteArrayCollection.add(temp.getValueBytes());
			length+=temp.getValueBytes().length;
		}
		
		byte[] resultBytes = new byte[length];
		Iterator<byte[]> byteItr = byteArrayCollection.iterator();
		
		int count = 0;
		while(byteItr.hasNext()){
			byte[] tempBytes = byteItr.next();
			System.arraycopy(tempBytes, 0, resultBytes, count, tempBytes.length);
			count+=tempBytes.length;
		}
		return resultBytes;
	}
	
	@Override
	protected void handleStatusServerMessageRequest(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Status Server message received.");
		
		serviceResponse.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
		
		//as per RFC if request contains Message Authenticator then need to add in response too
		if(serviceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null){
			IRadiusAttribute msgAuthenticatorAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			//as generate packet does add valid bytes
			msgAuthenticatorAttr.setValueBytes(new byte[0]);
			serviceResponse.addAttribute(msgAuthenticatorAttr);
		}
		return;
	}
	
	@Override
	protected final void handleAsyncRadiusRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {		
		/* ELITEAAA-1856
		 * If EAP response is received from Proxy ESI so adding elitecore EAP Attribute
		 * before submitting it for further process.
		 * 
		 */
		addEAPAttributeAsInfoFrom(response);
		handleAsyncRadiusAuthRequest(request, response, session);
	}

	
	private void addEAPAttributeAsInfoFrom(RadAuthResponse response) {
		Collection<IRadiusAttribute> eapMessageAttributes = response.getRadiusAttributes(RadiusAttributeConstants.EAP_MESSAGE);
		if(eapMessageAttributes != null && eapMessageAttributes.size() > 0){
			byte[] eapMessageBytes = getBytesFromRadiusAtrCollection(eapMessageAttributes);
			try {
				EAPPacket eapPacket = new EAPPacket(eapMessageBytes);
				IRadiusAttribute eapCodeAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_EAP_CODE);
				if(eapCodeAttr != null){
					eapCodeAttr.setIntValue(eapPacket.getCode());
					response.addInfoAttribute(eapCodeAttr);
				}
				List<EAPType> eapTypes = eapPacket.getEAPTypes();
				if(eapTypes != null && eapTypes.size() > 0){
					IRadiusAttribute eapMethodAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_EAP_METHOD);
					if(eapMethodAttribute != null ){
						eapMethodAttribute.setIntValue(eapTypes.get(0).getType());
						response.addInfoAttribute(eapMethodAttribute);
					}
				}

			} catch (InvalidEAPPacketException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Invalid EAP Packet received, ELITE-EAP-CODE (21067:211) and ELITE-EAP-CODE (21067:212) will not be added.");
				}
			}
		}
	}

	@Override
	protected final void finalRadiusPreResponseProcess(RadAuthRequest request,
			RadAuthResponse response) {
		super.finalRadiusPreResponseProcess(request, response);
		finalAuthPreResponseProcess(request, response);
		
		if(response.getPacketType() == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
			//this can be the case when the Accept is being set through Universal Plug in
			if(request.getServicePolicy() != null){
				/*
				 * This is the code for handling the Authentication type Session manager where
				 * the session is to be created during Authentication service
				 */
				Optional<ConcurrencySessionManager> sessionManager = request.getServicePolicy().getSessionManager();
				if(sessionManager.isPresent() && sessionManager.get().getBehaviorType() == BehaviourType.AUTHENTICATION.type) {
					sessionManager.get().handleAuthFlavorAuthenticationRequest(request, response);
					if(response.isMarkedForDropRequest() || response.isFurtherProcessingRequired() == false){
						return;
					}
				}
			}
		}
		
		if(response.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PPAQ_AVPAIR) != null){
			String prepaidStandard = ((RadAuthServiceContext)getServiceContext()).getServerContext().getServerConfiguration().getRadClientConfiguration().getPrepaidStandard(request.getClientIp());
			if(prepaidStandard != null){
				IPrepaidConverter prepaidConverter = PrepaidConverterFactory.getInstance().createPrepaidConverter(prepaidStandard);
				if(prepaidConverter != null){
					try {
						prepaidConverter.convertToStandard(response);
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
							LogManager.getLogger().debug(MODULE,"Response packet after applying prepaid conversion: " );
						}
					} catch (Exception e) {
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
							LogManager.getLogger().trace(MODULE, "Not able to convert standard prepaid attribute, reason: " + e.getMessage());
						}
					}
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "No converter yet available for " + prepaidStandard + " prepaid standard.");
					}
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "No value for prepaid standard defined.");
				}
			}
		}
		
		filterUnsupportedVendorSpecificAttributes(response);
	}
	
	private void filterUnsupportedVendorSpecificAttributes(RadAuthResponse response) {

		if (response.getClientData().isFilterUnsupportedVSA() == false) {
			return;
		}
		
		if (response.getPacketType() == RadiusConstants.ACCESS_REJECT_MESSAGE) {
			return;
		}
		
		RadClientData clientData = response.getClientData();
		
		if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Filtering Unsupported VSAs from response for Client: " + clientData.getClientIp() + " Profile: " + clientData.getProfileName());
		}

		List<IRadiusAttribute> attributeList = (List<IRadiusAttribute>) response.getRadiusAttributes(false);

		for (int i = 0; i < attributeList.size(); i++) {
			if (clientData.isSupportedVendorId(attributeList.get(i).getVendorID()) == false) {
				response.removeAttribute(attributeList.get(i), true);
			}
		}
	}

	protected abstract void finalAuthPreResponseProcess(RadAuthRequest request, RadAuthResponse response);
	
	protected void convertVSAToClassAttribute(RadAuthRequest request, RadAuthResponse response) {
		
		List<IRadiusAttribute> classAttrList = getClassAttributes(request, response);
		if(classAttrList !=null && classAttrList.size()>0){
			int listSize= classAttrList.size();
			IRadiusAttribute radiusAttribute;
			for(int i=0;i<listSize;i++){
				radiusAttribute  = classAttrList.get(i);
				if(radiusAttribute!=null){
					response.addAttribute(radiusAttribute);
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						if(radiusAttribute.getVendorID() == RadiusConstants.STANDARD_VENDOR_ID)
							LogManager.getLogger().debug(MODULE, "Adding Class Attribute: " + Dictionary.getInstance().getAttributeName(radiusAttribute.getType()) + radiusAttribute);
						else
							LogManager.getLogger().debug(MODULE, "Adding Class Attribute: " + Dictionary.getInstance().getVendorName(radiusAttribute.getVendorID()) + ":" + Dictionary.getInstance().getAttributeName(radiusAttribute.getVendorID(), radiusAttribute.getType())  + radiusAttribute);
					}
				}
			}
		}
		
	}
	private List<IRadiusAttribute> getClassAttributes(RadAuthRequest request, RadAuthResponse response) {
		
		List<IRadiusAttribute> classAttributeList = null;
		
		VSAInClassConfiguration vsaInClassConfiguration = ((AAAServerContext)getServerContext()).getServerConfiguration().getVSAInClassConfiguration();
		if(vsaInClassConfiguration!=null && vsaInClassConfiguration.getIsEnabled()){
			
			int MAX_LEN_FOR_STRING_ATTR = 253;
			String[] classAttributeId =	vsaInClassConfiguration.getClassAttributeId();
			String strPrefix = vsaInClassConfiguration.getStrPrefix();
			int prefixLength =strPrefix.length();
			List<KeyValuePair>  attrbiutesFromReqeustPacket=vsaInClassConfiguration.getAttrbiutesFromReqeustPacket();
			
			List<KeyValuePair>  attributesFromResponsePacket = vsaInClassConfiguration.getAttributesFromResponsePacket();
			String strSeparator = String.valueOf(vsaInClassConfiguration.getSeparator());
			boolean isMultipleClassAttrSupported = response.getClientData().isMultipleClassAttributeSupported();
		
			if(classAttributeId!=null){
				classAttributeList = new ArrayList<IRadiusAttribute>();
				String strClassAttrValue;
				IRadiusAttribute classAttribute ;
				int iLength;
				for(int cntr=0;cntr<classAttributeId.length;cntr++){
					
					classAttribute = Dictionary.getInstance().getKnownAttribute(classAttributeId[cntr]);
					if(classAttribute !=null){
						iLength = prefixLength;
						strClassAttrValue = strPrefix;
						List<String> vsaList =response.getVSAAttributesList();
						if(vsaList != null){
							int vsaListSize = vsaList.size();
							String currentAttribute;
							for(int i=0;i<vsaListSize;i++){
								try{
									currentAttribute = vsaList.get(i);
									if(currentAttribute!=null && currentAttribute.length()>0){
										iLength += currentAttribute.length() + 1;
										if(iLength > MAX_LEN_FOR_STRING_ATTR){
											classAttribute.setStringValue(strClassAttrValue);
											classAttributeList.add(classAttribute);
											if(!isMultipleClassAttrSupported) {
												removeClassAttributesFromResponse(response);
												return classAttributeList;
											}
											strClassAttrValue = strPrefix;
											classAttribute = Dictionary.getInstance().getKnownAttribute(classAttributeId[cntr]);
											iLength = strClassAttrValue.length();
										}
										strClassAttrValue = strClassAttrValue + strSeparator + currentAttribute;
									}
									
								}catch (Exception e) {
									if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
										LogManager.getLogger().warn(MODULE, "Error occured while adding VSA in Class aattribute for Attribute-Id :"+classAttributeId[cntr]+ " Reason: " + e.getMessage());
									LogManager.getLogger().trace(MODULE, e);
								}
							}
						}
						if(attrbiutesFromReqeustPacket != null && attrbiutesFromReqeustPacket.size() > 0){
							int noOfRequestAttr=attrbiutesFromReqeustPacket.size();
							
							String currentAttribute  = null;
							KeyValuePair keyValuePair;
							IRadiusAttribute radiusAttribute=null;
							for(int i=0;i<noOfRequestAttr;i++){
								currentAttribute = null;
								keyValuePair = attrbiutesFromReqeustPacket.get(i);
								if(keyValuePair.getAttributeId() != null && keyValuePair.getAttributeId().length() > 0){
									radiusAttribute = request.getRadiusAttribute(keyValuePair.getAttributeId(),true);
									if(radiusAttribute!=null){
										currentAttribute = radiusAttribute.getStringValue(false); 
									}
								}else if(keyValuePair.getDefaultValue() != null && keyValuePair.getDefaultValue().length() > 0){
									currentAttribute = keyValuePair.getDefaultValue();
								}
								if(currentAttribute != null){
									try{
										currentAttribute = keyValuePair.getKey() + "=" + currentAttribute; 
										iLength += currentAttribute.length() + 1;
										if(iLength > MAX_LEN_FOR_STRING_ATTR){
											classAttribute.setStringValue(strClassAttrValue);
											classAttributeList.add(classAttribute);
											if(!isMultipleClassAttrSupported) {
												removeClassAttributesFromResponse(response);
												return classAttributeList;
											}
											strClassAttrValue = strPrefix;
											classAttribute = Dictionary.getInstance().getKnownAttribute(classAttributeId[cntr]);
											iLength = strClassAttrValue.length(); 
										}
										strClassAttrValue = strClassAttrValue + strSeparator + currentAttribute;
										
									}catch (Exception e) {
										if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
											LogManager.getLogger().warn(MODULE, "Error occured while adding VSA in Class attribute for Attribute from Request packet :"+attrbiutesFromReqeustPacket.get(i)+ " Reason: " + e.getMessage());
										LogManager.getLogger().trace(MODULE, e);
									}
								}
							}
						}
						
						if(attributesFromResponsePacket != null && attributesFromResponsePacket.size() > 0){
							int noOfResposeAttr =attributesFromResponsePacket.size();
							KeyValuePair keyValuePair;
							String currentAttribute;
							ArrayList<IRadiusAttribute> radiusAttributeList;
							IRadiusAttribute radiusAttribute;
							String strAttributeValue;
							int size;
							for(int i=0;i<noOfResposeAttr;i++){
								keyValuePair = attributesFromResponsePacket.get(i);
								if(keyValuePair.getAttributeId() != null && keyValuePair.getAttributeId().length() > 0){
									radiusAttributeList = (ArrayList<IRadiusAttribute>)response.getRadiusAttributes(true,keyValuePair.getAttributeId());
									if(radiusAttributeList!=null ){
										size =0;
										size = radiusAttributeList.size();
										for(int k=0;k<size;k++){
											radiusAttribute = radiusAttributeList.get(k);
											if(radiusAttribute!=null){
												try{
													strAttributeValue = radiusAttribute.getStringValue(false);
													response.removeAttribute(radiusAttribute, true);
											
													if(strAttributeValue.contains(strSeparator))
														currentAttribute = keyValuePair.getKey() + "=" + radiusAttribute.getStringValue(false).replaceAll(strSeparator, "\\\\"+strSeparator);
													else
														currentAttribute =  keyValuePair.getKey() + "="+strAttributeValue;
														
													iLength += currentAttribute.length() + 1;
													if(iLength > MAX_LEN_FOR_STRING_ATTR){
														classAttribute.setStringValue(strClassAttrValue);
														classAttributeList.add(classAttribute);
														if(!isMultipleClassAttrSupported) {
															removeClassAttributesFromResponse(response);
															return classAttributeList;
														}
														strClassAttrValue = strPrefix;
														classAttribute = Dictionary.getInstance().getKnownAttribute(classAttributeId[cntr]);
														iLength = strClassAttrValue.length(); 
													}
													strClassAttrValue = strClassAttrValue + strSeparator + currentAttribute;
													
												}catch (Exception e) {
													if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
														LogManager.getLogger().warn(MODULE, "Error occured while adding VSA in Class attribute for Attribute from Response packet: "+keyValuePair+" Reason: " + e.getMessage() );
													LogManager.getLogger().trace(MODULE, e);
												}
											}
										}
									}
								}else if(keyValuePair.getDefaultValue() != null && keyValuePair.getDefaultValue().length() > 0 ){
									try{
										currentAttribute = keyValuePair.getKey() + "=" + keyValuePair.getDefaultValue(); 
										iLength += currentAttribute.length() + 1;
										if(iLength > MAX_LEN_FOR_STRING_ATTR){
											classAttribute.setStringValue(strClassAttrValue);
											classAttributeList.add(classAttribute);
											if(!isMultipleClassAttrSupported)
												return classAttributeList;
											strClassAttrValue = strPrefix;
											classAttribute = Dictionary.getInstance().getKnownAttribute(classAttributeId[cntr]);
											iLength = strClassAttrValue.length(); 
										}
										strClassAttrValue = strClassAttrValue + strSeparator + currentAttribute;
										
									}catch (Exception e) {
										if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
											LogManager.getLogger().warn(MODULE, "Error occured while adding VSA in Class attribute for Attribute from Request packet :"+attrbiutesFromReqeustPacket.get(i)+ " Reason: " + e.getMessage());
										LogManager.getLogger().trace(MODULE, e);
									}
								}
							}
						}
						
						if(!strClassAttrValue.equals(strPrefix)){
							classAttribute.setStringValue(strClassAttrValue);
							classAttributeList.add(classAttribute);
						}
						
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "VSA attributes will not be added to Attribute for configured Attribute id :"+classAttributeId[cntr]+" Reason : Attribute not found in the dictionary");
					}
				}
			}
			
			
		}
		
		return classAttributeList;
	}
	
	private void removeClassAttributesFromResponse(RadAuthResponse response) {
		if(response.getRadiusAttributes(RadiusAttributeConstants.CLASS) == null){
			return;
		}
		List<IRadiusAttribute> classAttributes =  new ArrayList<IRadiusAttribute>(response.getRadiusAttributes(RadiusAttributeConstants.CLASS));
		if(classAttributes.isEmpty() == false) {
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,"Unable to accomodate all class attributes in VSA in class attribute, "
						+ classAttributes.size() + " class attributes will be removed, "
						+ "Reason: length of VSA in class attribute exceeded and multiple class attributes are not allowed. Removed attributes : "
						+  Strings.join(",", classAttributes, IRadiusAttribute.STRING_VALUE_FUNCTION));
			}
			response.removeAllAttributes(classAttributes, true);
		}
	}

	protected abstract void handleAsyncRadiusAuthRequest(RadAuthRequest request, RadAuthResponse response, ISession session);

	protected void setServicePolicy(RadAuthRequest request){		
		for(RadiusServicePolicy<RadAuthRequest, RadAuthResponse> authServicePolicy : authServicePolicies){
			if(authServicePolicy.assignRequest(request)){				
				((RadiusAuthRequestImpl)request).setServicePolicy(authServicePolicy);
				IRadiusAttribute infoAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_SATISFIED_SERVICE_POLICY);
				if(infoAttribute != null){
					infoAttribute.setStringValue(authServicePolicy.getPolicyName());
					request.addInfoAttribute(infoAttribute);
				}
				break;
			}
		}
	}
	public static class RadiusAuthRequestImpl extends RadServiceRequestImpl implements RadAuthRequest {
		private RadiusServicePolicy<RadAuthRequest, RadAuthResponse> servicePolicy;
		private IEapStateMachine eapSession;
		private AAAServerContext serverContext;
		private RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> executor;
		
		public RadiusAuthRequestImpl(byte[] requestBytes, InetAddress sourceAddress, int sourcePort, AAAServerContext serverContext, SocketDetail serverSocketDetail) {
			super(requestBytes, sourceAddress, sourcePort, serverSocketDetail);			
			this.serverContext = serverContext;
		}

		public RadiusServicePolicy<RadAuthRequest, RadAuthResponse> getServicePolicy() {
			return servicePolicy;
		}
		private void setServicePolicy(RadiusServicePolicy<RadAuthRequest, RadAuthResponse> servicePolicy){
			this.servicePolicy = servicePolicy;
		}
		@Override
		public IEapStateMachine getEapSession() {
			if(eapSession == null){
				 String eapSessionIdStr = EAPSessionId.createEapSessionId(this, null);
				 eapSession = serverContext.getEapSessionManager().getEAPStateMachine(eapSessionIdStr);
			}
			return eapSession;
		}
		
		public void setExecutor(RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> executor){
			this.executor = executor;
		}
		
		@Override
		public RadiusRequestExecutor<RadAuthRequest, RadAuthResponse> getExecutor() {
			return executor;
		}
		
		@Override
		public RadServiceRequest clone() {
			RadiusAuthRequestImpl clone =  (RadiusAuthRequestImpl)super.clone();
			clone.executor = null;
			return clone;
		}
	}
	
	public static class RadiusAuthResponseImpl extends RadServiceResponseImpl implements RadAuthResponse {
		private static final String MODULE = "RAD-AUTH-RESPONSE-IMPL";
		private AAAServerContext serverContext;
		
		public RadiusAuthResponseImpl(byte[] authenticator,int identifier, AAAServerContext aaaServerContext) {
			super(authenticator,identifier);
			setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
			this.serverContext = aaaServerContext;
		}
		
		@Override
		public RadiusPacket generatePacket() {
			RadiusPacket responsePacket = new RadiusPacket();			
			responsePacket.setPacketType(getPacketType());
			responsePacket.setIdentifier(getIdentifier());
			
			if(getPacketType() != RadiusConstants.ACCESS_REJECT_MESSAGE){
				responsePacket.addAttributes(getAttributeList());
			}else{
				if(getResponseMessege() != null){
					responsePacket.addAttribute(getRadiusAttribute(RadiusAttributeConstants.REPLY_MESSAGE));
				}
				/* for the case Access-Reject adding Eap-Failure packet in response packet*/
				String eapSessionId = (String)getParameter("EAP_SESSION_ID");
				try{
					if(eapSessionId!=null && eapSessionId.length()>0){
						IEapStateMachine eapStateMachine = serverContext.getEapSessionManager().getEAPStateMachine(eapSessionId);
						if(eapStateMachine!=null){
							EAPPacket eapFailurePacket = eapStateMachine.buildFailure(eapStateMachine.getCurrentIdentifier());
							IRadiusAttribute respEapMessageAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.EAP_MESSAGE);				
							respEapMessageAttr.setValueBytes(eapFailurePacket.getBytes());
							responsePacket.addAttribute(respEapMessageAttr);
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Problem in adding Eap-Message attribure ,Reason : Eap-Session doesn't exist");
						}
					}
				}catch (EAPException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Problem during adding Eap-Message attribute in response packet for Access-Reject");
				}
				
				Collection<IRadiusAttribute> proxyStateAttributes = getRadiusAttributes(RadiusAttributeConstants.PROXY_STATE);
				if (Collectionz.isNullOrEmpty(proxyStateAttributes) == false) {
					LogManager.getLogger().debug(MODULE, "Added count: " + proxyStateAttributes.size() + " count of proxy state attribute in response packet.");
					for (IRadiusAttribute proxyStateAttribute : proxyStateAttributes) {
						responsePacket.addAttribute(proxyStateAttribute);
			}
				}
			}
			
			//responsePacket.reencryptAttributes(null,null, getRequestAuthenticator()	, getClientData().getSharedSecret());			
			responsePacket.refreshPacketHeader();
			IRadiusAttribute msgAuthenticatorAttribute = responsePacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			if (responsePacket.getRadiusAttribute(RadiusAttributeConstants.EAP_MESSAGE)!=null || msgAuthenticatorAttribute != null) {
				
				if(msgAuthenticatorAttribute == null){
					msgAuthenticatorAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);				
					responsePacket.addAttribute(msgAuthenticatorAttribute);
					responsePacket.refreshPacketHeader();
				}
				
				msgAuthenticatorAttribute.setValueBytes(RadiusUtility.generateMessageAuthenticator(responsePacket.getBytes(), getRequestAuthenticator(), getClientData().getSharedSecret(getPacketType())));
			}
			
			responsePacket.refreshPacketHeader();
			responsePacket.refreshInfoPacketHeader();
			responsePacket.setAuthenticator(RadiusUtility.generateRFC2865ResponseAuthenticator((responsePacket),getRequestAuthenticator(),getClientData().getSharedSecret(getPacketType())));
			return responsePacket;
		
		}
		
		@Override
		public RadServiceResponse clone() {
			return super.clone();
		}
	}
	
	@Override
	public final RadAuthRequest formServiceSpecificRequest(InetAddress sourceAddress, int sourcePort, byte[] requestBytes, SocketDetail serverSocketDetail) {
		return new RadiusAuthRequestImpl(requestBytes, sourceAddress, sourcePort, (AAAServerContext) getServerContext(), serverSocketDetail);
	}

	@Override
	public final RadAuthResponse formServiceSpecificResposne(RadAuthRequest serviceRequest) {
		return new RadiusAuthResponseImpl(serviceRequest.getAuthenticator(),serviceRequest.getIdentifier(), (AAAServerContext) getServerContext());
	}
	
	@Override
	public List<ICommand> getCliCommands() {	
		List<ICommand> cmdList = new ArrayList<ICommand>();
		
		cmdList.add(new AuthServCommand(getServerContext()) {		
			@Override
			public String getServicepolicyList() {				
				return getServicePolicyNames();
			}

			@Override
			public String getServicePolicyDetail(String commandParameters) {
				
				StringWriter stringBuffer = new StringWriter();
				PrintWriter out = new PrintWriter(stringBuffer);
				RadiusServicePolicy<RadAuthRequest, RadAuthResponse> servicePolicy = 
					getServicePolicy(commandParameters);
				if(servicePolicy!=null){
					out.println(servicePolicy);
					out.close();
					return stringBuffer.toString();
				}
				out.println();
				out.println("Service Policy: "+ commandParameters+" is not available in the cache");		
				out.close();
				return stringBuffer.toString();
			}

			@Override
			public String getServiceThreadSummary() {				
				return getThreadSummary();
			}
		});	
		return cmdList;
	}
	
	private RadiusServicePolicy<RadAuthRequest, RadAuthResponse> getServicePolicy(String policyName){
				
		if (authServicePolicies != null && !authServicePolicies.isEmpty()) {
			for(RadiusServicePolicy<RadAuthRequest, RadAuthResponse> servicePolicy:authServicePolicies) {
				if(servicePolicy.getPolicyName().equals(policyName)){
					return servicePolicy;
				}
			}
		}
		return null;	

	}
	private String getServicePolicyNames(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();		
		out.println();
		out.println(" -- Rad Auth Service Policies -- ");
		out.println();
		if (authServicePolicies != null && !authServicePolicies.isEmpty()) {
			for (RadiusServicePolicy<RadAuthRequest, RadAuthResponse> servicePolicy : authServicePolicies) {
				out.println(servicePolicy.getPolicyName());
			}
		} else {
			out.println("No policy available in the cache");
		}
		out.close();
		return stringBuffer.toString();	
	}
	private String servicePolicyCacheToString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();		
		out.println();
		out.println(" -- Rad Auth Service Policies -- ");
		out.println();

		if (authServicePolicies != null && !authServicePolicies.isEmpty()) {
			for (RadiusServicePolicy<RadAuthRequest, RadAuthResponse> servicePolicy : authServicePolicies) {
				out.println(servicePolicy.toString());
			}
		} else {
			out.println("No policy available in the cache");
		}
		out.close();
		return stringBuffer.toString();
	}

	public RadAuthConfiguration getRadAuthConfiguration() {
		return radAuthConfiguration;
	}
	
	@Override
	protected synchronized boolean startService() {
		return super.startService();
	}
	
	@Override
	protected int getMainThreadPriority() {
		return radAuthConfiguration.mainThreadPriority();
	}

	@Override
	protected int getMaxRequestQueueSize() {
		return radAuthConfiguration.maxRequestQueueSize();
	}

	@Override
	protected int getMaxThreadPoolSize() {
		return radAuthConfiguration.maxThreadPoolSize();
	}

	@Override
	protected int getMinThreadPoolSize() {
		return radAuthConfiguration.minThreadPoolSize();
	}

	@Override
	protected int getSocketReceiveBufferSize() {
		return radAuthConfiguration.socketReceiveBufferSize();
	}

	@Override
	protected int getSocketSendBufferSize() {
		return radAuthConfiguration.socketSendBufferSize();
	}

	@Override
	protected int getThreadKeepAliveTime() {
		return radAuthConfiguration.threadKeepAliveTime();
	}

	@Override
	protected int getWorkerThreadPriority() {
		return radAuthConfiguration.workerThreadPriority();
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return radAuthConfiguration.getSocketDetails();
	}

	@Override
	public String getKey() {
		return this.radAuthConfiguration.getKey();
	}
	
	@Override
	public boolean isDuplicateDetectionEnabled() {
		return radAuthConfiguration.isDuplicateRequestDetectionEnabled();
	}

	@Override
	public int getDuplicateDetectionQueuePurgeInterval() {
		return radAuthConfiguration.getDupicateRequestQueuePurgeInterval();
	}
	
	@Override
	protected void incrementServTotalInvalidRequests(ServiceRequest request){
		authServiceMIBListener.listenRadiusAuthServTotalInvalidRequestsEvent();
	}
	@Override
	protected void incrementServTotalBadAuthenticators(String clientAddress){
		RadClientData clientData = radClientConfiguration.getClientData(clientAddress);
		if(clientData != null) {
			authServiceMIBListener.listenRadiusAuthServTotalBadAuthenticatorsEvent(clientData.getClientIp());
		}
	}
	
	@Override
	protected void incrementRequestReceivedCounter(String clientAddress){
		authServiceMIBListener.listenRadiusAuthServTotalAccessRequestsEvent();
		RadClientData clientData = radClientConfiguration.getClientData(clientAddress);
		if(clientData != null) {
			authServiceMIBListener.listenRadiusAuthServTotalAccessRequestsEvent(clientData.getClientIp());
		}
	}
	
	@Override
	protected void incrementRequestDroppedCounter(ServiceRequest request){
		RadClientData clientData = radClientConfiguration.getClientData(((RadAuthRequest)request).getClientIp());
		if(clientData != null) {
			authServiceMIBListener.listenRadiusAuthServTotalPacketsDroppedEvent(clientData.getClientIp());
		}
    }
	
	@Override
	protected void incrementResponseCounter(ServiceResponse response){
		RadAuthResponse authResponse = (RadAuthResponse)response;
		incrementResponseCounter(authResponse.getClientData().getClientIp(), 
				authResponse.getPacketType());
	}
	
	@Override
	protected void incrementResponseCounter(String sourceAddress,
			byte[] responseBytes) {
		RadClientData clientData = ((AAAServerContext)getServerContext()).getServerConfiguration()
			.getRadClientConfiguration().getClientData(sourceAddress);
		
		if (clientData == null) {
			return;
		}
		
		incrementResponseCounter(clientData.getClientIp(), 
				RadiusPacket.parsePacketType(responseBytes));
	}
	
	private void incrementResponseCounter(String clientIp, int packetType) {
		switch (packetType) {
		case RadiusConstants.ACCESS_ACCEPT_MESSAGE:
			authServiceMIBListener.listenRadiusAuthServTotalAccessAcceptsEvent(clientIp);			
			break;
		case RadiusConstants.ACCESS_REJECT_MESSAGE:
			authServiceMIBListener.listenRadiusAuthServTotalAccessRejectsEvent(clientIp);
			break;
		case RadiusConstants.ACCESS_CHALLENGE_MESSAGE:
			authServiceMIBListener.listenRadiusAuthServTotalAccessChallengesEvent(clientIp);
			break;
		}
	}

	private void incrementAuthServTotalUnknownTypes(String clientAddress){
		RadClientData clientData = radClientConfiguration.getClientData(clientAddress);
		if(clientData != null) {
			authServiceMIBListener.listenRadiusAuthServTotalUnknownTypesEvent(clientData.getClientIp());
		}
	}
	@Override
	protected void incrementServTotalMalformedRequest(RadServiceRequest request) {
		RadClientData clientData = radClientConfiguration.getClientData(request.getClientIp());
		if(clientData != null) {
			authServiceMIBListener.listenRadiusAuthServTotalMalformedAccessRequestsEvent(clientData.getClientIp());
		}
	}
	
	@Override
	protected void incrementDuplicateRequestReceivedCounter(ServiceRequest serviceRequest) {
		RadAuthRequest radAuthRequest = (RadAuthRequest)serviceRequest;
		RadClientData clientData = radClientConfiguration.getClientData(radAuthRequest.getClientIp());
		if(clientData != null) {
			authServiceMIBListener.listenRadiusAuthServTotalDupAccessRequestsEvent(clientData.getClientIp());
		}
	}
	
	@Override
	protected boolean isValidRequest(RadAuthRequest radServiceRequest,
			RadAuthResponse radServiceRsponse) {
		
		if(radServiceRequest.getPacketType() != RadiusConstants.ACCESS_REQUEST_MESSAGE && radServiceRequest.getPacketType() != RadiusConstants.STATUS_SERVER_MESSAGE){
			
			incrementAuthServTotalUnknownTypes(radServiceRsponse.getClientData().getClientIp());
			radServiceRsponse.markForDropRequest();
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Unknown request packet (packet type: "+ radServiceRequest.getPacketType() + ") received from " + radServiceRequest.getClientIp() + ":" + radServiceRequest.getClientPort());
			return false;
		}
		
		IRadiusAttribute msgAuthenticator = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		if(msgAuthenticator != null){
			if(msgAuthenticator.getValueBytes().length == 16 && validateMessageAuthenticator(radServiceRequest,msgAuthenticator.getValueBytes(),radServiceRsponse.getClientData().getSharedSecret(radServiceRequest.getPacketType()))){
					
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Valid message authenticator");	
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Packet Dropped. Invalid Message Authenticator.");
					
				radServiceRsponse.setFurtherProcessingRequired(false);
				radServiceRsponse.markForDropRequest();
				incrementServTotalBadAuthenticators(radServiceRequest.getClientIp());
				return false;
			}
		}
		return true;
		
	}
	

	public class RadAuthConfigurationSetter implements ConfigurationSetter{
		private ServiceContext serviceContext;
		private static final String REALTIME = "realtime";

		public RadAuthConfigurationSetter(ServiceContext serviceContext){
			this.serviceContext = serviceContext;
		}

		@Override
		public String execute(String... parameters) {
			if(parameters[2].equalsIgnoreCase("log")){
				if(parameters.length >= 4){
					if(((RadAuthServiceContext)serviceContext).getAuthConfiguration().isServiceLevelLoggerEnabled()){
						if (serviceLogger instanceof EliteRollingFileLogger) {
							EliteRollingFileLogger logger = (EliteRollingFileLogger)serviceLogger;
							if (logger.isValidLogLevel(parameters[3]) == false) {
								return "Invalid log level: " + parameters[3];
							}
							logger.setLogLevel(parameters[3]);
							return "Configuration Changed Successfully";

						}
					}else{
						return "Error : Authentication log are disabled";
					}
				}
			}
			return getHelpMsg();
		}

		@Override
		public boolean isEligible(String... parameters) {
			if(parameters.length == 0){
				return false;
			}
			if(!parameters[0].equalsIgnoreCase("service")){
				return false;
			}
			if(!parameters[1].equalsIgnoreCase("auth")){
				return false;
			}
			if(parameters[2].equalsIgnoreCase("log")){
				return true;
			}else if(parameters[2].equalsIgnoreCase("-help")){
				return true;
			}

			return false;
		}

		@Override
		public String getHelpMsg() {
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("Usage : set service auth [<options>]");
			out.println();
			out.println("where options include:");		
			out.println("     log { all | debug | error | info | off | trace | warn }");
			out.println("     		Set the log level of the Authentication Service. ");			
			out.close();
			return stringWriter.toString();
		}

		@Override
		public String getHotkeyHelp() {
			return "'auth':{'log':{'off':{},'error':{},'warn':{},'info':{},'debug':{},'trace':{},'all':{}}}";
		}

		@Override
		public int getConfigurationSetterType() {
			return SERVICE_TYPE;
		}
	}
	
	@Override
	public boolean validatePacketAsPerRFC(RadAuthRequest request){

		//  As per RFC 2865
		//  Either NAS-IP-Address or NAS-Identifier MUST be present in a
		//  RADIUS Auth-Request.
		/**
		 * 	As per RFC 3162
		 * 	NAS-IPv6-Address and/or NAS-IP-Address MAY be present in an Access-Request Packet
		 * 	if neither attribute is present then NAS-Identifier must be Present.
		 */

		if(request.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS) == null 
				&& request.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER) == null 
				&& request.getRadiusAttribute(RadiusAttributeConstants.NAS_IPV6_ADDRESS) == null) {

			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Marking packet invalid for request-id : " + request.getIdentifier() + 
				" Reason : none of the attribute NAS-IP-ADDRESS(0:4), NAS-IDENTIFIER(0:32) and NAS-IPV6-ADDRESS(0:95) was found in request");
			}

			return false;
		}
		return true;

	}

	@Override
	protected boolean validateMessageAuthenticator(RadAuthRequest radServiceRequest, byte[] msgAuthenticatorBytes,String strSecret) {
		return RadiusUtility.validateMessageAuthenticator(radServiceRequest.getRequestBytes(), radServiceRequest.getAuthenticator(),msgAuthenticatorBytes, strSecret);
	}

	@Override
	protected void incrementRequestDroppedCounter(String clientAddress , ServiceRequest request){
		RadClientData clientData = radClientConfiguration.getClientData(clientAddress);
		if(clientData != null) {
			authServiceMIBListener.listenRadiusAuthServTotalPacketsDroppedEvent(clientData.getClientIp());
		}
	}

	@Override
	protected final void shutdownLogger() {
		Closeables.closeQuietly(serviceLogger);
	}

	@Override
	public int getDefaultServicePort() {
		return AAAServerConstants.DEFAULT_RAD_AUTH_PORT;
	}

}
