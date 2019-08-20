package com.elitecore.aaa.rm.service.prepaidchargingservice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.core.conf.ClassicCSVAcctDriverConfiguration;
import com.elitecore.aaa.core.conf.RMServerConfiguration;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.drivers.DriverFactory;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.policies.accesspolicy.AccessPolicyManager;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.core.util.cli.SetCommand;
import com.elitecore.aaa.core.util.cli.SetCommand.ConfigurationSetter;
import com.elitecore.aaa.radius.drivers.RadClassicCSVAcctDriver;
import com.elitecore.aaa.radius.drivers.conf.CrestelAttributePolicyMappingDictator;
import com.elitecore.aaa.radius.drivers.conf.CrestelAttributePolicyMappingWrapper;
import com.elitecore.aaa.radius.drivers.conf.CrestelAttributePolicyMappingWrapper.CheckedMapping;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.plugins.core.RadPluginRequestHandler;
import com.elitecore.aaa.radius.policies.RadiusRuleSet;
import com.elitecore.aaa.radius.policies.radiuspolicy.data.RadiusPolicyTreeData;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.base.BaseRadiusService;
import com.elitecore.aaa.radius.service.base.RadServiceRequestImpl;
import com.elitecore.aaa.radius.service.base.RadServiceResponseImpl;
import com.elitecore.aaa.rm.conf.RMPrepaidChargingServiceConfiguration;
import com.elitecore.aaa.rm.conf.impl.RMCrestelAttributeMappingConfigurable;
import com.elitecore.aaa.rm.conf.impl.RmCrestelAttributeMappingDriverConfigurable;
import com.elitecore.aaa.rm.drivers.CrestelRatingResponseObject;
import com.elitecore.aaa.rm.drivers.DummyRatingResponseObject;
import com.elitecore.aaa.rm.drivers.IRatingResponseObject;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.api.rating.ServiceLocatorEnvironment;
import com.elitecore.api.rating.factory.RatingDelegateFactory;
import com.elitecore.api.rating.interfaces.IRatingDelegate;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.DriverProcessTimeoutException;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.driverx.IEliteDriver;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.core.serverx.policies.data.IPolicyData;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.AsyncRequestExecutor;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.util.logger.EliteRollingFileLogger;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.ratingapi.data.RequestPacketFactory;
import com.elitecore.ratingapi.util.IRequestPacketFactory;
import com.elitecore.ratingapi.util.IRequestParameters;
import com.elitecore.ratingapi.util.IResponseObject;

public class RMPrepaidChargingService extends BaseRadiusService<RMPrepaidChargingServiceRequest, RMPrepaidChargingServiceResponse>{
	
	private static final String MODULE = "RM_PREPAID_CHARGING_SERVICE";
	private static final String SERVICE_ID = "RM_PREPAID_CHARGING";			
	
	private RMPrepaidChargingServiceConfiguration prepaidChargingServiceConfig;
	private RMPrepaidChargingServiceContext prepaidChargingServiceContext;
	private Map driverConfigMap;
	
	// driver related parameters..
	private Hashtable apiHashtable = new Hashtable();
	private ServiceLocatorEnvironment serviceLocatorEnvironement = null;
	private IRatingDelegate ratingDelegate = null;
	private CrestelAttributePolicyMappingDictator attributeMappingPolicyDictator;
	private boolean bDummyDriver = false;
	private List ratingFieldMappingList = null;
	private Map<String,String> ratingResponseMap = null;
	private IRatingResponseObject responseObjForDummy;
	private static int DRIVER_TIMEOUT = 3000;
	public static final String REPLY_MESSAGE="REPLY_MESSAGE";
	public static final String FAILURE="FAILURE";
	public static final String AUTH_METHOD="AUTH_METHOD";
	public static final String REAUTH_METHOD="REAUTH_METHOD";
	public static final String ACCT_METHOD="ACCT_METHOD";
	private static String OPERATION_FAILURE = "operation_failure";

	private Map<String,RadiusRuleSet> ruleSetMap ;
	
	private DriverFactory driverFactory;
	private String driverInstanceId;
	private EliteRollingFileLogger serviceLogger;
	private DriverManager driverManager;
	
	public RMPrepaidChargingService(AAAServerContext context, RadPluginManager pluginManager, RadiusLogMonitor logMonitor) {
		super(context, pluginManager, logMonitor);
		this.prepaidChargingServiceConfig = ((RMServerConfiguration)context.getServerConfiguration()).getRMPrepaidChargingServiceConfiguration();
		this.driverConfigMap = ((RMServerConfiguration)((AAAServerContext)context).getServerConfiguration()).getRMPrepaidChargingServiceConfiguration().getDriverConfig();
		this.ruleSetMap = new HashMap<String, RadiusRuleSet>();
		this.driverInstanceId = ((RMServerConfiguration)((AAAServerContext)getServerContext()).getServerConfiguration()).getRMPrepaidChargingServiceConfiguration().getDriverInstanceId();
		
		driverFactory = new DriverFactory() {
			@Override
			public IEliteDriver createDriver(DriverTypes driverType, String driverInstanceId) {
				if (driverType == DriverTypes.RAD_CLASSIC_CSV_ACCT_DRIVER) {
					return new RadClassicCSVAcctDriver(prepaidChargingServiceContext.getServerContext(), (ClassicCSVAcctDriverConfiguration)prepaidChargingServiceConfig.getDriverConfiguration(driverInstanceId));
				}
				
				return null;		
			}			
		};		
	}
	
	
	@Override
	protected void initService() throws ServiceInitializationException{	

		prepaidChargingServiceContext = new RMPrepaidChargingServiceContext() {

			@Override
			public RMPrepaidChargingServiceConfiguration getRMPrepaidChargingServiceConfiguration() {
				return prepaidChargingServiceConfig;
			}

			@Override
			public ESCommunicator getDriver(String driverInstanceId) {
				return getRadiusDriver(driverInstanceId);
			}
			@Override
			public String getServiceIdentifier() {
				return  RMPrepaidChargingService.this.getServiceIdentifier();
			}

			@Override
			public AAAServerContext getServerContext() {
				return (AAAServerContext) RMPrepaidChargingService.this.getServerContext();
			}
			@Override
			public RadPluginRequestHandler createPluginRequestHandler(List<PluginEntryDetail> prePluginList,
					List<PluginEntryDetail> postPluginList) {
				return createRadPluginReqHandler(prePluginList, postPluginList);
			}

			@Override
			public void submitAsyncRequest(RMPrepaidChargingServiceRequest serviceRequest,RMPrepaidChargingServiceResponse serviceResponse,
					AsyncRequestExecutor<RMPrepaidChargingServiceRequest, RMPrepaidChargingServiceResponse> requestExecutor) {
				RMPrepaidChargingService.this.submitAsyncRequest(serviceRequest, serviceResponse, requestExecutor);
			}			
		};
		
		driverManager = new DriverManager(
				((RMPrepaidChargingServiceContext)getServiceContext()).getRMPrepaidChargingServiceConfiguration(),
				this.driverFactory);
		setDriverManager(driverManager);
		
		super.initService();
		// initializing driver related details...
		initializeDriver();
		
		String fileName = "";		
		if (prepaidChargingServiceConfig.isServiceLevelLoggerEnabled()) {
			serviceLogger = 
				new EliteRollingFileLogger.Builder(getServerContext().getServerInstanceName(),
							prepaidChargingServiceConfig.getLogLocation())
				.rollingType(prepaidChargingServiceConfig.logRollingType())
				.rollingUnit(prepaidChargingServiceConfig.logRollingUnit())
				.maxRolledUnits(prepaidChargingServiceConfig.logMaxRolledUnits())
				.compressRolledUnits(prepaidChargingServiceConfig.isCompressLogRolledUnits())
				.sysLogParameters(prepaidChargingServiceConfig.getSysLogConfiguration().getHostIp(),
						prepaidChargingServiceConfig.getSysLogConfiguration().getFacility())
				.build();
			serviceLogger.setLogLevel(prepaidChargingServiceConfig.logLevel());
			LogManager.setLogger(getServiceIdentifier(), serviceLogger);
		}

		
		try {
			AccessPolicyManager.getInstance().initCache(getServiceContext().getServerContext().getServerHome());
			registerCacheable(AccessPolicyManager.getInstance());
		} catch (ManagerInitialzationException e) {
			LogManager.getLogger().error(MODULE,"Error Caching Access-Policies. Reason :" + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		RMPrepaidChargingConfigurationSetter prepaidChargingConfigurationSetter = new RMPrepaidChargingConfigurationSetter(prepaidChargingServiceContext);
		SetCommand.registerConfigurationSetter(prepaidChargingConfigurationSetter);
		LogManager.getLogger().info(MODULE,"RM Prepaid Charging service initilized successfully.");
	}
	
	private void initializeDriver() throws ServiceInitializationException{
		// TODO Auto-generated method stub

		if(this.driverConfigMap == null || this.driverConfigMap.isEmpty()){
			throw new ServiceInitializationException("Invalid configuration for driver", ServiceRemarks.UNKNOWN_PROBLEM);
		}
		try{			
			if(this.driverConfigMap.get(RmCrestelAttributeMappingDriverConfigurable.DUMMY_DRIVER)!= null){
				bDummyDriver = Boolean.parseBoolean(this.driverConfigMap.get(RmCrestelAttributeMappingDriverConfigurable.DUMMY_DRIVER).toString());
			}			    							
			if(bDummyDriver){
				
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, " Dummy Response option selected for prepaid service.");
				}
				ratingResponseMap = (Map)this.driverConfigMap.get(RmCrestelAttributeMappingDriverConfigurable.RATING_RESPONSE);
				
				///  building response object from map .... 						
				if(ratingResponseMap != null){
					responseObjForDummy = new DummyRatingResponseObject();
					if(!ratingResponseMap.isEmpty()){
						for(Map.Entry<String,String> entry : ratingResponseMap.entrySet()){
							responseObjForDummy.put(entry.getKey(), entry.getValue());
						}
					}					
				}		
			}else{
				apiHashtable.putAll((HashMap)this.driverConfigMap.get(RMCrestelAttributeMappingConfigurable.API_DETAIL));
				serviceLocatorEnvironement = ServiceLocatorEnvironment.getInstance();
				serviceLocatorEnvironement.setServiceLocatorEnv(apiHashtable);

				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Getting Rating delegate object.");

				int numberOfInstances = 5;
				if(apiHashtable.containsKey(RmCrestelAttributeMappingDriverConfigurable.INSTANCE_NUMBER)){
					numberOfInstances = Integer.parseInt((String)apiHashtable.get(RmCrestelAttributeMappingDriverConfigurable.INSTANCE_NUMBER));
				}
				
				ratingDelegate = RatingDelegateFactory.getInstance().getRatingDelegate(numberOfInstances);
				LogManager.getLogger().trace(MODULE,"Rating Delegate Object : "+ratingDelegate);
			}

			ratingFieldMappingList = (List)this.driverConfigMap.get(RMCrestelAttributeMappingConfigurable.RATING_FIELD);
							
			attributeMappingPolicyDictator = (CrestelAttributePolicyMappingDictator)this.driverConfigMap.get(RMCrestelAttributeMappingConfigurable.ATTRIBUTES_POLICY_MAPPING_CONFIGURATION);
			if(attributeMappingPolicyDictator==null)throw new ServiceInitializationException("No Configuration Found Mapping Policies", ServiceRemarks.UNKNOWN_PROBLEM);

			// it is in driver that call method and all that block;
			Map<String,Object> attributePolicyMappingDriverConfigurationMap = null;
			attributePolicyMappingDriverConfigurationMap = (HashMap)this.driverConfigMap.get(RmCrestelAttributeMappingDriverConfigurable.RATING_FIELD_MAPPING);
			
			if(attributePolicyMappingDriverConfigurationMap!=null && attributeMappingPolicyDictator!=null){
				
				//Manual PM initialization stuff 
				Map<String,IPolicyData> policyDataMap = new HashMap<String, IPolicyData>();
				
				for(Map.Entry<String, Object> entry : attributePolicyMappingDriverConfigurationMap.entrySet()){
					String requestType  = entry.getKey();
					List<Map<String,String>> configList = (List<Map<String, String>>) entry.getValue();

					if(configList!=null){
						for(Map<String, String> config : configList){
							String policy = config.get(RmCrestelAttributeMappingDriverConfigurable.CHECK_ITEM_RE);
							String strMapping = config.get(RmCrestelAttributeMappingDriverConfigurable.MAPPING);
							String callMethod = config.get(RmCrestelAttributeMappingDriverConfigurable.CALL_METHOD);
							String policyName = strMapping + callMethod;
							attributeMappingPolicyDictator.addConfiguration(requestType, strMapping,policyName,callMethod,policy);
							
							if(!policyDataMap.containsKey(policy)){
								RadiusRuleSet ruleSet = new RadiusRuleSet(policyName,policy,null);
								ruleSet.parseRuleSet();
								ruleSetMap.put(policy,ruleSet);								
							}
						}
					}

				}								
			}else{
				throw new ServiceInitializationException("Error in Loading Configuration from ATTRIBUTE_POLICY_MAPPING or ATTRIBUTE_POLICY_MAPPING_DICTATOR", ServiceRemarks.UNKNOWN_PROBLEM);
			}
			LogManager.getLogger().info(MODULE, "Rating field Mapping : "+ratingFieldMappingList);
			LogManager.getLogger().info(MODULE,"Dummy Driver : "+bDummyDriver);
		}catch(ServiceInitializationException e){
			throw e;
		}catch(Exception e){			
			LogManager.getLogger().warn(MODULE, "Error while initialization "+MODULE+" Driver. REASON : "+e.getMessage());
		}
		
	}

	@Override
	public boolean stopService() {
		super.stopService();
		if (driverManager != null) {
			driverManager.stop();
		}
		return true;
	}

	@Override
	public List<PluginEntryDetail> getRadPostPluginList() {
		return this.prepaidChargingServiceConfig.getPostPluginList();
	}

	@Override
	public List<PluginEntryDetail> getRadPrePluginList() {
		return this.prepaidChargingServiceConfig.getPrePluginList();
	}

	@Override
	protected final void handleRadiusRequest(RMPrepaidChargingServiceRequest request,
			RMPrepaidChargingServiceResponse response, ISession session) {		
		
		boolean handleByFallback = false;
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Within handle request of prepaid charging service");						
		}		
		try {	
			handleMappingRequests(request,response);			
		}catch (DriverProcessFailedException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Driver Process Timeout Happend. Terminating further process.");
			}
			handleByFallback = true;
		} catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
		}
		if(handleByFallback){
			RadClassicCSVAcctDriver driver =  (RadClassicCSVAcctDriver)prepaidChargingServiceContext.getDriver(driverInstanceId);
			if(driver == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "The configured driver is incorrect.Please configure correct driver.");
				}
				return;
			}
			try {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Handling request using fallback driver.");
				}
				driver.handleAccountingRequest(request);
			} catch (DriverProcessFailedException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Classic CSV Driver processing failed. Reason: " + e.getMessage());				
			}
		}		
	}

	private void handleMappingRequests(RadServiceRequest request,RadServiceResponse response) throws DriverProcessFailedException {

		setResponseForEvent(request,response);		
		try{
			int requestType = request.getPacketType();
			String requestTypeStr = getRequestType(requestType);
			String policies  = null;
			boolean policyApplied = false;
			int i =0;
			RadiusPolicyTreeData policyTreeData = new RadiusPolicyTreeData(request,response);
			policyTreeData.setRejectOnCheckItemNotFound(true);
			List<CrestelAttributePolicyMappingWrapper> attrPolicyMappingWrapperList = attributeMappingPolicyDictator.getCommunicationConfiguration(requestTypeStr);
			if(attrPolicyMappingWrapperList!=null){
				for(CrestelAttributePolicyMappingWrapper attrPolicyMappingWrapper  : attrPolicyMappingWrapperList){					
						if(attrPolicyMappingWrapper!=null){
							policies = attrPolicyMappingWrapper.getPolicies();
							if(policies!=null){
								try{															
										RadiusRuleSet ruleSet = ruleSetMap.get(attrPolicyMappingWrapper.getCheckItem());
										ruleSet.applyRuleSet(policyTreeData);																			
										policyApplied = true;
										i++ ;
										if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
											LogManager.getLogger().debug(MODULE, "Request Packet after successfull appllication of policy : "+ request);
								}catch (PolicyFailedException e) {
									if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
										LogManager.getLogger().debug(MODULE, "Policy Application Failed for : "+policies);
									continue;
								}
							}
						}
					if(policyApplied){
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
							LogManager.getLogger().trace(MODULE,"Configuration Applied : "+attrPolicyMappingWrapper.getMappingId());
						}
						handleMappingRequest(request,response,attrPolicyMappingWrapper,requestTypeStr);
						break;
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "Policy Not Satisfied for "+attrPolicyMappingWrapper.getPolicies());
					}
				}
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "No Related Configuration found, hence sending Failure to client.");
				   getResponsePacket(request,response,null);
			}

		}catch (DriverProcessTimeoutException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().warn(MODULE, "Driver Process Timeout Happend. Terminating further process.");
			}
			throw e;
		}catch(Exception e){		
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "No Related Configuration found, hence sending Failure to client.");
				getResponsePacket(request,response,null);
			throw new DriverProcessFailedException("Sending failure to client",e);
		}				
	}

	@Override
	protected void handleStatusServerMessageRequest(RMPrepaidChargingServiceRequest serviceRequest, 
			RMPrepaidChargingServiceResponse serviceResponse) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Status Server message received.");

		serviceResponse.setPacketType(RadiusConstants.STATUS_SERVER_MESSAGE);
		
		//as per RFC if request contains Message Authenticator then need to add in response too
		if(serviceRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null){
			IRadiusAttribute msgAuthenticatorAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			//as generate packet does add valid bytes
			msgAuthenticatorAttr.setValueBytes(new byte[0]);
			serviceResponse.addAttribute(msgAuthenticatorAttr);
		}

	}
	
	private void handleMappingRequest(RadServiceRequest request,RadServiceResponse response,CrestelAttributePolicyMappingWrapper attrPolicyMappingWrapper,String requestTypeStr) throws DriverProcessFailedException {		
		IRatingResponseObject ratingResponseObject = null;
		try {

			int responseCode = -1;
			if(!bDummyDriver){
				IRequestParameters requestParameters = getRequestParameters(request,attrPolicyMappingWrapper);
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "RequestParameters : "+requestParameters);
				IResponseObject responseObject = null;
				double startTime = 0;
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					startTime = System.nanoTime();
				}			

				if(AUTH_METHOD.equalsIgnoreCase(attrPolicyMappingWrapper.getCallMethod())){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Calling authorizationRequest in Rating");
					responseObject = ratingDelegate.authorizationRequest(requestParameters);						
				}else if(ACCT_METHOD.equalsIgnoreCase(attrPolicyMappingWrapper.getCallMethod())){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Calling accountingRequest in Rating");
					responseObject = ratingDelegate.accountingRequest(requestParameters);						
				}else if(REAUTH_METHOD.equalsIgnoreCase(attrPolicyMappingWrapper.getCallMethod())){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Calling reauthorizationRequest in Rating");
					responseObject = ratingDelegate.reAuthorizationRequest(requestParameters);								
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "No Appropriate Calling Method Selected");
					
				}
				if(responseObject != null){
					ratingResponseObject = new CrestelRatingResponseObject(responseObject);						
				}
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
					double endTime = (System.nanoTime() - startTime);
					LogManager.getLogger().trace(MODULE,"Rating Response Time : " + endTime + " nenoseconds.");
					LogManager.getLogger().trace(MODULE,"Response from rating, responseObject : "+ratingResponseObject);									
				}					
			}else{
				ratingResponseObject = responseObjForDummy;
				ratingResponseObject.setResponseCode(1);
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Using Dummy Driver : "+ratingResponseObject);
			}

			if(ratingResponseObject!= null ){
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE,"Response from rating, Code : "+ratingResponseObject.getResponseCode());

				responseCode = ratingResponseObject.getResponseCode();
				List<Map<String,Object>> responseMappingList = null;

				if(responseCode>0){
					responseMappingList = attrPolicyMappingWrapper.getResponseParameterMappingList();
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE,"+ve Response from rating, hence Proper Request Mapping Selected.");
						getResponsePacket(response, responseMappingList, ratingResponseObject);
					if(attrPolicyMappingWrapper.getCheckedMapping()!=null){
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE, "Adding checked Attributes");
						addCheckedAttributes(attrPolicyMappingWrapper.getCheckedMapping(),ratingResponseObject,response);
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE, "No Checked Attributes to add");
					}
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE,"-ve Response from rating, hence Selecting Response Error Configuration.");
//					responseMappingList = attributeMappingPolicyDictator.getResponseErrorConfigList();
					getResponsePacket(request,response,ratingResponseObject);
				}



			}else{			
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Getting problem with rating api ,responseObject : "+ratingResponseObject);				
				//responsePacket =getResponsePacket(radiusPacket,responsePacket,ratingResponseObject);
				throw new DriverProcessFailedException("Getting problem with rating api ,responseObject :" +ratingResponseObject);
			}						

		} catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Getting problem with rating , reason : "+e.getMessage());
			 	getResponsePacket(request,response,ratingResponseObject);
			throw new DriverProcessFailedException("Getting problem with rating api." ,e);
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Final response packet of handleAuthorizaion request : "+response);		
	}

	private void addCheckedAttributes(List<CheckedMapping> checkMappings,IRatingResponseObject responseObject,RadServiceResponse response){
		for(CheckedMapping checkedMapping : checkMappings){
			List<Map<String,String>> checkValues = checkedMapping.getCheckValueMap();
			if(checkValues!=null){
				boolean addAttr = false;
				checkAttrLoop:	for(Map<String,String> checkValue : checkValues){
					for(Map.Entry<String, String> entry : checkValue.entrySet()){
						String value = null;
						if((value=responseObject.get(entry.getKey()))!=null){
							if(value!=null){
								if(value.equalsIgnoreCase(entry.getValue())){
									addAttr  = true;
									continue;
								}else{
									addAttr = false;
									break checkAttrLoop;
								}
							}else{
								addAttr = false;
								break checkAttrLoop;
							}
						}else{
							addAttr = false;
							break checkAttrLoop;
						}
					}
				}
				if(addAttr){
					List<Map<String,Object>> mapping = checkedMapping.getCheckAttributeMapping();
					getResponsePacket(response,mapping,responseObject);

				}
			}
		}
	}
	
	private void getResponsePacket(RadServiceResponse response,List<Map<String,Object>> responseMappingList,IRatingResponseObject responseObject){
		String strVendorId = "0";
		String strAttributeId = null;
		String strRatingField = null;
		String strDefaultValue = null;
		ArrayList subAttributeList = new ArrayList();
		Iterator<Map<String, Object>> attributeListItr = responseMappingList.iterator();
		while(attributeListItr.hasNext()){						
			IRadiusAttribute radiusAttr = null;
			Map authAttributeMap = attributeListItr.next();
			strVendorId = (String)authAttributeMap.get(RMCrestelAttributeMappingConfigurable.VENDOR_ID);
			strAttributeId = (String)authAttributeMap.get(RMCrestelAttributeMappingConfigurable.ATTRIBUTE_IDS);
			strRatingField = (String)authAttributeMap.get(RMCrestelAttributeMappingConfigurable.RATING_FIELD);
			strDefaultValue = (String)authAttributeMap.get(RMCrestelAttributeMappingConfigurable.DEFAULT_VALUE);

			strAttributeId = strVendorId+":"+strAttributeId;

			if(strRatingField != null){
				radiusAttr = Dictionary.getInstance().getKnownAttribute(strAttributeId);							
				if(radiusAttr != null ){
					if(responseObject.get(strRatingField) != null){
						radiusAttr.setStringValue(String.valueOf(responseObject.get(strRatingField)));
					}else if(strDefaultValue != null && strDefaultValue.length()>0){
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE, "Parameter is not found in rating response , so need to send : "+radiusAttr+" avp with default value : "+strDefaultValue+"  in response packet.");
						radiusAttr.setStringValue(strDefaultValue);
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE, "Parameter is not found in rating response , so no need to send : "+radiusAttr+" avp in response packet.");
						radiusAttr = null;
					}
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE, Dictionary.getInstance().getAttributeName((strAttributeId))+" attribute is not found in request packet, use default value : "+strDefaultValue);
				}
			}else{
				subAttributeList = (ArrayList)authAttributeMap.get(RMCrestelAttributeMappingConfigurable.ATTRIBUTE_LIST);

				radiusAttr = Dictionary.getInstance().getKnownAttribute(strAttributeId);
				if(radiusAttr!=null)
					getResponsePacket(radiusAttr, subAttributeList, responseObject);
				else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "Can't find Attribute for Attribute-Id :"+strAttributeId+" , so Skipping further process for this Attribute.");
				}
			}							

			if(radiusAttr != null && radiusAttr.getStringValue()!=null && !"".equalsIgnoreCase(radiusAttr.getStringValue())){
				response.addAttribute(radiusAttr);
			}
		}		
	}
	
	private IRadiusAttribute getResponsePacket(IRadiusAttribute rootRadiusAttr,ArrayList configSubAttributeList,IRatingResponseObject responseObject){

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Root Attribute is :: "+rootRadiusAttr);

		IRadiusAttribute radiusAttr = null;
		Iterator<Map> configurationSunAttributeIterator = configSubAttributeList.iterator();

		while(configurationSunAttributeIterator.hasNext()){
			Map attributeConfigurationMap = configurationSunAttributeIterator.next();
			String ratingField = null;
			String attrId;
			attrId       = (attributeConfigurationMap.get(RMCrestelAttributeMappingConfigurable.ATTRIBUTE_IDS).toString());

			radiusAttr = Dictionary.getInstance().getKnownAttribute(rootRadiusAttr.getParentId()+":"+ (attrId));
			if(radiusAttr==null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Can't find Attribute for Attribute-Id : "+rootRadiusAttr.getParentId()+":"+ (attrId));
				continue;
			}
			if((ratingField = (String) attributeConfigurationMap.get(RMCrestelAttributeMappingConfigurable.RATING_FIELD))!=null){
				String avpValue = responseObject.get(ratingField);
				if(avpValue==null && (avpValue=(String.valueOf(attributeConfigurationMap.get(RMCrestelAttributeMappingConfigurable.DEFAULT_VALUE))))!=null){}
				radiusAttr.setStringValue(avpValue);
				((BaseRadiusAttribute)rootRadiusAttr).addTLVAttribute(radiusAttr);
			}else{
				List subAttrConfigList =  (List) attributeConfigurationMap.get(RMCrestelAttributeMappingConfigurable.ATTRIBUTE_LIST);
				getResponsePacket(radiusAttr,  (ArrayList)subAttrConfigList, responseObject);
				String value  = radiusAttr.getStringValue();

				if(rootRadiusAttr!=null && value !=null && !"".equalsIgnoreCase(value)){
					((BaseRadiusAttribute)rootRadiusAttr).addTLVAttribute(radiusAttr);
				}
			}
		}
		return radiusAttr;
	}
	
	
	
	
	private void setResponseForEvent(RadServiceRequest request,RadServiceResponse response){
		if(request.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
			response.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
		}else if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE) {
			response.setPacketType(RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE);
		}else if(request.getPacketType() == RadiusConstants.RESOURCE_QUERY_REQUEST 
				|| request.getPacketType() == RadiusConstants.RESOURCE_REAUTHORIZE_REQUEST) {
			response.setPacketType(RadiusConstants.RESOURCE_QUERY_RESPONSE);
		}else if(request.getPacketType() == RadiusConstants.RESOURCE_FREE_REQUEST){
			response.setPacketType(RadiusConstants.RESOURCE_FREE_RESPONSE);
		}		
	}

	private String getRequestType(int requestType){

		if(RadiusConstants.RESOURCE_QUERY_REQUEST==requestType || RadiusConstants.ACCESS_REQUEST_MESSAGE==requestType){
			return RmCrestelAttributeMappingDriverConfigurable.AUTH_MAPPING;
		}else if(RadiusConstants.RESOURCE_REAUTHORIZE_REQUEST==requestType || RadiusConstants.ACCESS_REQUEST_MESSAGE==requestType){
			return RmCrestelAttributeMappingDriverConfigurable.REAUTH_MAPPING;
		}else if(RadiusConstants.RESOURCE_FREE_REQUEST==requestType || RadiusConstants.ACCOUNTING_REQUEST_MESSAGE==requestType){
			return RmCrestelAttributeMappingDriverConfigurable.ACCT_MAPPING;
		}

		return null;
	}
	
	private IRequestParameters getRequestParameters(RadServiceRequest request,CrestelAttributePolicyMappingWrapper attributePolicyMappingWrapper){
		
		IRequestPacketFactory iRequestPacketFactory = RequestPacketFactory.getInstance();
		IRequestParameters requestParameters = iRequestPacketFactory.getRequestPacket();
		String vendorId;
		String attributeId;
		String strRatingField = null;
		String strDefaultValue = null;
		String strAttributeValue = null;
		HashMap<String,String> radiusRatingValueMapping = new HashMap<String,String>();
		ArrayList subAttributeList = new ArrayList();

		if(attributePolicyMappingWrapper.getRequestParameterMappingList() != null){
			Iterator<Map<String, Object>> attributeListItr = attributePolicyMappingWrapper.getRequestParameterMappingList().iterator();
			while(attributeListItr.hasNext()){
				strAttributeValue = null;
				strDefaultValue = null;
				Map requestParameterMap = attributeListItr.next();

				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Configured Attributes Map :: "+requestParameterMap);
				}
				try{
					vendorId = (requestParameterMap.get(RMCrestelAttributeMappingConfigurable.VENDOR_ID).toString());
					attributeId = (requestParameterMap.get(RMCrestelAttributeMappingConfigurable.ATTRIBUTE_IDS).toString());
					strRatingField = (String)requestParameterMap.get(RMCrestelAttributeMappingConfigurable.RATING_FIELD);
					strDefaultValue = (String)requestParameterMap.get(RMCrestelAttributeMappingConfigurable.DEFAULT_VALUE);
					radiusRatingValueMapping = (HashMap<String, String>)requestParameterMap.get(RMCrestelAttributeMappingConfigurable.VALUE_MAPPING);

					attributeId = vendorId+":"+attributeId;
					if(strRatingField != null){
						IRadiusAttribute radiusAttribute = request.getRadiusAttribute(attributeId);
						if(radiusAttribute != null){
							strAttributeValue = radiusAttribute.getStringValue();
							if(radiusRatingValueMapping != null && radiusRatingValueMapping.get(strAttributeValue)!=null){
								strAttributeValue = radiusRatingValueMapping.get(strAttributeValue);
							}							
						}else if(strDefaultValue!=null){
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(MODULE, Dictionary.getInstance().getAttributeName(attributeId)+" attribute is not found in request packet, use default value : "+strDefaultValue);
							strAttributeValue = strDefaultValue;
						}
						requestParameters.put(strRatingField, strAttributeValue);

					}else{
						IRadiusAttribute radiusAttribute = null;
						subAttributeList = (ArrayList)requestParameterMap.get(RMCrestelAttributeMappingConfigurable.ATTRIBUTE_LIST);
						radiusAttribute = request.getRadiusAttribute(attributeId);
						if(radiusAttribute!=null){
						getRequestParameters(radiusAttribute, subAttributeList,request,requestParameters);
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
								LogManager.getLogger().debug(MODULE, "Parameter not found in request packet :: "+attributeId);
							}
						}
					}
				}catch (NullPointerException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Object Null :: "+requestParameterMap);
					}
					throw e;
				}
			}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Request Parameters :: "+requestParameters);
		}
		return requestParameters;
	}
	
	
	private IRequestParameters getRequestParameters(IRadiusAttribute rootAttribute,List attributePolicyMappingWrapper,RadServiceRequest request,IRequestParameters requestParameters){
		String vendorId;
		String attributeId;
		String strRatingField = null;
		String strDefaultValue = null;
		String strAttributeValue = null;
		HashMap<String,String> radiusRatingValueMapping = new HashMap<String,String>();
		ArrayList subAttributeList = new ArrayList();

		Iterator<Map<String, Object>> attributeListItr = attributePolicyMappingWrapper.iterator();
		while(attributeListItr.hasNext()){
			strAttributeValue = null;
			strDefaultValue = null;
			Map requestParameterMap = attributeListItr.next();

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Configured Attributes Map :: "+requestParameterMap);
			}
			try{
				vendorId = (requestParameterMap.get(RMCrestelAttributeMappingConfigurable.VENDOR_ID).toString());
				attributeId = (requestParameterMap.get(RMCrestelAttributeMappingConfigurable.ATTRIBUTE_IDS).toString());
				strRatingField = (String)requestParameterMap.get(RMCrestelAttributeMappingConfigurable.RATING_FIELD);
				strDefaultValue = (String)requestParameterMap.get(RMCrestelAttributeMappingConfigurable.DEFAULT_VALUE);
				radiusRatingValueMapping = (HashMap<String, String>)requestParameterMap.get(RMCrestelAttributeMappingConfigurable.VALUE_MAPPING);

				attributeId = rootAttribute.getParentId()+":"+attributeId;
				if(strRatingField != null){
					IRadiusAttribute radiusAttribute = request.getRadiusAttribute(attributeId);
					if(radiusAttribute != null){
						strAttributeValue = radiusAttribute.getStringValue();
						if(radiusRatingValueMapping != null && radiusRatingValueMapping.get(strAttributeValue)!=null){
							strAttributeValue = radiusRatingValueMapping.get(strAttributeValue);
						}							
					}else if(strDefaultValue!=null){
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(MODULE, Dictionary.getInstance().getAttributeName(attributeId)+" attribute is not found in request packet, use default value : "+strDefaultValue);
						strAttributeValue = strDefaultValue;
					}
					requestParameters.put(strRatingField, strAttributeValue);

				}else{
					IRadiusAttribute radiusAttribute = null;
					subAttributeList = (ArrayList)requestParameterMap.get(RMCrestelAttributeMappingConfigurable.ATTRIBUTE_LIST);
					radiusAttribute =  request.getRadiusAttribute(attributeId);
				}
			}catch (NullPointerException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Object Null :: "+requestParameterMap);
				}
				throw e;
			}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Request Parameters :: "+requestParameters);
		}
		return requestParameters;
	}
	private void getResponsePacket(RadServiceRequest request,RadServiceResponse response,IRatingResponseObject responseObject){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "responseObject from rating is "+responseObject+".");
		
		String message = null;
		if(responseObject != null){
			message = responseObject.getResponseMessage();
		}else {
			message = AuthReplyMessageConstant.RATING_PROCESS_FAILED;
		}
		if(request.getPacketType() == RadiusConstants.ACCESS_REQUEST_MESSAGE){
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			response.setResponseMessage(message);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, AuthReplyMessageConstant.AUTHENTICATION_FAILED);
		}else if(request.getPacketType() == RadiusConstants.ACCOUNTING_REQUEST_MESSAGE){
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Accounting Failed, hence dropping packet.");
			//responsePacket = null;
		}else if(request.getPacketType() == RadiusConstants.RESOURCE_QUERY_REQUEST){
			makeRequestQueryResponsePacket(response,OPERATION_FAILURE, message);
		}else if(request.getPacketType() == RadiusConstants.RESOURCE_FREE_REQUEST){
			IRadiusAttribute attr = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_RESOURCE_MANAGER_AVPAIR);
			attr.setStringValue("REPLAY_MESSAGE=FAILURE");
			response.addAttribute(attr);			
		}		
	}
	
	
	private void makeRequestQueryResponsePacket(RadServiceResponse response, String strStatus,String strMessage){
		
		try{
			IRadiusAttribute attr1 = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_RESOURCE_MANAGER_AVPAIR);
			attr1.setStringValue("status="+strStatus);
			response.addAttribute(attr1);			
			
			IRadiusAttribute attr2 = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_RESOURCE_MANAGER_AVPAIR);
			attr2.setStringValue("message="+strMessage);
			response.addAttribute(attr2);			
			
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Error while creating ResponcePacket for Resource_Query_Responce, reason :" 
					+ e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}	
	}
	
	@Override
	protected void incrementServTotalBadAuthenticators(String clientAddress) {
		// TODO Auto-generated method stub
		
	}

	
	protected void incrementServTotalInvalidRequests() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isValidRequest(RMPrepaidChargingServiceRequest radServiceRequest,
			RMPrepaidChargingServiceResponse radServiceRsponse) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int getMainThreadPriority() {
		return this.prepaidChargingServiceConfig.mainThreadPriority();
	}

	@Override
	protected int getMaxRequestQueueSize() {
		return this.prepaidChargingServiceConfig.maxRequestQueueSize();
	}

	@Override
	protected int getMaxThreadPoolSize() {
		return this.prepaidChargingServiceConfig.maxThreadPoolSize();
	}

	@Override
	protected int getMinThreadPoolSize() {
		return prepaidChargingServiceConfig.minThreadPoolSize();
	}

	@Override
	protected int getSocketReceiveBufferSize() {
		return prepaidChargingServiceConfig.socketReceiveBufferSize();
	}

	@Override
	protected int getSocketSendBufferSize() {
		return prepaidChargingServiceConfig.socketSendBufferSize();
	}

	@Override
	protected int getThreadKeepAliveTime() {
		return prepaidChargingServiceConfig.threadKeepAliveTime();
	}

	@Override
	protected int getWorkerThreadPriority() {
		return prepaidChargingServiceConfig.workerThreadPriority();
	}

	@Override
	public String getKey() {
		return prepaidChargingServiceConfig.getKey();
	}

	@Override
	protected ServiceContext getServiceContext() {
		return prepaidChargingServiceContext;
	}

	@Override
	public void readConfiguration() throws LoadConfigurationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getServiceIdentifier() {
		return SERVICE_ID;
	}


	@Override
	protected void incrementServTotalInvalidRequests(ServiceRequest request) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public RMPrepaidChargingServiceRequest formServiceSpecificRequest(InetAddress sourceAddress, int sourcePort, byte[] requestBytes, SocketDetail serverSocketDetail) {
		return new RMPrepaidChargingServiceRequestImpl(requestBytes, sourceAddress, sourcePort, serverSocketDetail);
	}


	@Override
	public RMPrepaidChargingServiceResponse formServiceSpecificResposne(RMPrepaidChargingServiceRequest serviceRequest) {
		return new RMPrepaidChargingServiceResponseImpl(serviceRequest.getAuthenticator(),serviceRequest.getIdentifier());
	}
	
	protected void incrementServTotalMalformedRequest(RadServiceRequest request) {
		// TODO Auto-generated method stub
		
	}

	public boolean validatePacketAsPerRFC(RMPrepaidChargingServiceRequest request) {
		// TODO Auto-generated method stub
		return false;
	}

	private class RMPrepaidChargingServiceRequestImpl extends RadServiceRequestImpl implements RMPrepaidChargingServiceRequest{		

		public RMPrepaidChargingServiceRequestImpl(byte[] requestBytes, InetAddress sourceAddress, int sourcePort, SocketDetail serverSocketDetail) {
			super(requestBytes, sourceAddress, sourcePort, serverSocketDetail);
		}
		
		@Override
		public RadServiceRequest clone() {
			return super.clone();
		}
	}
	
	private class RMPrepaidChargingServiceResponseImpl extends RadServiceResponseImpl implements RMPrepaidChargingServiceResponse {

		public RMPrepaidChargingServiceResponseImpl(byte[] authenticator,int identifier) {
			super(authenticator,identifier);
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
			}
			//responsePacket.reencryptAttributes(null,null, getRequestAuthenticator()	, getClientData().getSharedSecret());
			responsePacket.refreshPacketHeader();
			responsePacket.refreshInfoPacketHeader();
			
			IRadiusAttribute msgAuthenticatorAttribute = responsePacket.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			if (msgAuthenticatorAttribute != null) {
				msgAuthenticatorAttribute.setValueBytes(RadiusUtility.generateMessageAuthenticator(responsePacket.getBytes(), getRequestAuthenticator(), getClientData().getSharedSecret(getPacketType())));
			}
			
			responsePacket.refreshPacketHeader();
			responsePacket.refreshInfoPacketHeader();
			
			RadiusUtility.generateRFC2865ResponseAuthenticator(responsePacket, getRequestAuthenticator(),getClientData().getSharedSecret(getPacketType()));
			return responsePacket;
		}
		
		@Override
		public RadServiceResponse clone() {
			return super.clone();
		}
	}


	public class RMPrepaidChargingConfigurationSetter implements ConfigurationSetter{
		private ServiceContext serviceContext;
		private static final String REALTIME = "realtime";
		
		public RMPrepaidChargingConfigurationSetter(ServiceContext serviceContext){
			this.serviceContext = serviceContext;
		}
		
		@Override
		public String execute(String... parameters) {
			if(parameters[2].equalsIgnoreCase("log")){
				if(parameters.length >= 4){
					if(((RMPrepaidChargingServiceContext)serviceContext).getRMPrepaidChargingServiceConfiguration().isServiceLevelLoggerEnabled()){
						if (serviceLogger instanceof EliteRollingFileLogger) {
							EliteRollingFileLogger logger = (EliteRollingFileLogger)serviceLogger;
							if (logger.isValidLogLevel(parameters[3]) == false) {
								return "Invalid log level: " + parameters[3];
							}
							logger.setLogLevel(parameters[3]);
							return "Configuration Changed Successfully";
							
						}
					}else{
						return "Error : Prepaid Charging log are disabled";
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
			if(!parameters[1].equalsIgnoreCase("prepaid")){
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
			out.println("Usage : set service prepaid [<options>]");
			out.println();
			out.println("where options include:");		
			out.println("     log { all | debug | error | info | off | trace | warn }");
			out.println("     		Set the log level of the Prepaid Charging Service. ");			
			out.close();
			return stringWriter.toString();
		}

		@Override
		public String getHotkeyHelp() {
			return "'prepaid':{'log':{'off':{},'error':{},'warn':{},'info':{},'debug':{},'trace':{},'all':{}}}";
		}

		@Override
		public int getConfigurationSetterType() {
			return SERVICE_TYPE;
		}
	}


	@Override
	protected void handleAsyncRadiusRequest(RMPrepaidChargingServiceRequest request,
			RMPrepaidChargingServiceResponse response, ISession session) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isDuplicateDetectionEnabled() {
		return prepaidChargingServiceConfig.isDuplicateRequestDetectionEnabled();
	}

	@Override
	public int getDuplicateDetectionQueuePurgeInterval() {
		return prepaidChargingServiceConfig.getDupicateRequestQueuePurgeInterval();
	}


	@Override
	public String getServiceName() {
		return "RM Prepaid Charging Service";
		
	}
	
	@Override
	protected boolean validateMessageAuthenticator(RMPrepaidChargingServiceRequest radServiceRequest,
			byte[] msgAuthenticatorBytes,String strSecret) {
		//RM services do not validate the message authenticator received in request
		return true;
	}

	@Override
	public List<SocketDetail> getSocketDetails() {
		return prepaidChargingServiceConfig.getSocketDetails();
	}
	
	@Override
	protected final void shutdownLogger() {
		Closeables.closeQuietly(serviceLogger);
	}

	@Override
	public int getDefaultServicePort() {
		return AAAServerConstants.DEFAULT_RM_PREPAID_CHARGING_PORT;
	}

	@Override
	protected boolean isSessionRelease(RMPrepaidChargingServiceRequest request,
			RMPrepaidChargingServiceResponse response) {
		return false;
	}

}
