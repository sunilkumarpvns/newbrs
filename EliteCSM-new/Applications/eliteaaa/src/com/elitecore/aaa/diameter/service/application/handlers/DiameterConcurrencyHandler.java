package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.elitecore.aaa.core.conf.impl.MiscellaneousConfigurable;
import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager.CommunicationException;
import com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.DiameterConcurrencyConfigurationData;
import com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.MandateryFieldConstants;
import com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.SessionOverrideActions;
import com.elitecore.aaa.diameter.conf.sessionmanager.FieldMappingImpl;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.radius.sessionx.ConcurrentPolicyConstants;
import com.elitecore.aaa.radius.sessionx.conf.ConcurrentLoginPolicyConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.impl.ConcurrentLoginPolicyData;
import com.elitecore.aaa.radius.sessionx.conf.impl.ServceWiseLogin;
import com.elitecore.aaa.radius.sessionx.data.FieldMappingParser;
import com.elitecore.aaa.radius.sessionx.data.PropertyType;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.aaa.util.constants.AAAServerConstants.ProtocolType;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.Session;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionFactory;
import com.elitecore.core.serverx.sessionx.SystemPropertiesProvider;
import com.elitecore.core.serverx.sessionx.conf.SessionConfiguration;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.core.serverx.sessionx.impl.SchemaMappingImpl;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.EndToEndPool;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.HopByHopPool;

public class DiameterConcurrencyHandler<T extends ApplicationRequest, V extends ApplicationResponse> implements DiameterApplicationHandler<T, V> {

	private static final String MODULE = "DIA-CONCURRENCY";
	
	private static final FieldMapping protocolTypeMapping = new FieldMappingImpl(AAAServerConstants.PROTOCOL_TYPE_FIELD, 
			AAAServerConstants.PROTOCOL_TYPE_FIELD, FieldMapping.STRING_TYPE, ProtocolType.DIAMETER.name());;

	private Map<MandateryFieldConstants, FieldMapping> mandatoryMapping;

	private String diameterConfigId;
	private DiameterServiceContext serviceContext;
	private DiameterConcurrencyConfigurationData data;
	private FieldMappingParser fieldMappingParser;
	private DiameterSessionDataAndCriteriaBuilder sessionBuilder;
	private SessionFactory sessionFactory;
	private List<String> sessionOverrideColumns;
	
	private ConcurrentLoginPolicyConfiguration concurrentLoginPolicyConfiguration;
	private EliteAAAServiceExposerManager eliteAAAServiceExposerManager = EliteAAAServiceExposerManager.getInstance();

	public DiameterConcurrencyHandler(DiameterServiceContext serviceContext, String diameterConfigId) {
		this.serviceContext = serviceContext;
		this.sessionOverrideColumns = new ArrayList<String>(0);
		this.diameterConfigId = diameterConfigId;
		this.concurrentLoginPolicyConfiguration = serviceContext.getServerContext().getServerConfiguration().getConcurrentLoginPolicyConfiguration();
	}

	public DiameterConcurrencyHandler(DiameterServiceContext serviceContext, String diameterConfigId,
			EliteAAAServiceExposerManager eliteAAAServiceExpoiserManager) {
		this(serviceContext, diameterConfigId);
		this.eliteAAAServiceExposerManager = eliteAAAServiceExpoiserManager;
	}

	@Override
	public void init() throws InitializationFailedException {
		data = serviceContext.getServerContext().getServerConfiguration().
				getDiameterConcurrencyConfigurable().getDiameterConcurrencyConfigurationDataByName(diameterConfigId);
		if (data == null) {
			throw new InitializationFailedException("Initialization failed, Reason: diameter concurrency configuration for id: " 
					+ diameterConfigId + " not found");
		
		}
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing diameter concurrency with name: " + data.getName());
		}
		
		String sessionOverrideFields = data.getSessionOverrideFields();
		if (Strings.isNullOrBlank(sessionOverrideFields) == false) {
			this.sessionOverrideColumns = Arrays.asList(sessionOverrideFields.trim().split("[,;]"));
		}
		
		sessionFactory = createSessionFactory();
		
		createMandatoryFieldMappings();
		
		fieldMappingParser = new FieldMappingParser(MODULE, new ArrayList<FieldMapping>(data.getSessionData().getFeildMappings()));
		sessionBuilder = new DiameterSessionDataAndCriteriaBuilder(fieldMappingParser, new ArrayList<String>(), 
				data.getIncludeInASR());
		
	}

	protected SessionFactory createSessionFactory() throws InitializationFailedException {
		// creating basic session configuration impl
		SessionConfigurationImpl sessionConfig = new SessionConfigurationImpl(serviceContext.getServerContext());
		SystemPropertiesProvider systemPropertiesProvider = new SystemPropertiesProvider() {

			@Override
			public boolean isNoWaitEnabled() {
				return Strings.toBoolean(System.getProperty(MiscellaneousConfigurable.NOWAIT_SYSTEM_PROPERTY, "true"));
			}

			@Override
			public boolean isBatchEnabled() {
				return Strings.toBoolean(System.getProperty(MiscellaneousConfigurable.BATCH_SYSTEM_PROPERTY, "true"));
			}

			@Override
			public int getQueryTimeout() {
				return CommonConstants.NO_QUERY_TIMEOUT;
			}

			@Override
			public int getBatchQueryTimeout() {
				return CommonConstants.NO_QUERY_TIMEOUT;
			}
		};
		if(LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "SQL Batch update is " + systemPropertiesProvider.isBatchEnabled() 
					+ " and SQL No-wait is " + systemPropertiesProvider.isNoWaitEnabled());
		}
		sessionConfig.setSystemPropertiesProvider(systemPropertiesProvider);

		sessionConfig.setSessionFactoryType(SessionConfiguration.DB_SESSION);
		sessionConfig.addDataSource(serviceContext.getServerContext().getServerConfiguration().getDatabaseDSConfiguration().
				getDataSource(data.getDatasourceId()));
		
		//creating schema mapping and adding it to session config
		SchemaMappingImpl schemaMapping = new SchemaMappingImpl(AAAServerConstants.CONCURRENCY_ID_FIELD, 
				data.getStartTimeField(), data.getLastUpdateTimeField());
		
		schemaMapping.setFieldMappings(new ArrayList<FieldMapping>(data.getSessionData().getFeildMappings()));
		
		//add SESSION_STATUS field mapping
		schemaMapping.addFieldMapping( new FieldMappingImpl(AAAServerConstants.SESSION_STATUS, DiameterAVPConstants.EC_SESSION_STATUS, 
				FieldMapping.STRING_TYPE, AAAServerConstants.SESSION_STATUS));
		schemaMapping.addFieldMapping(new FieldMappingImpl(data.getConcurrencyIdentityField(), data.getConcurrencyIdentityField(),
				FieldMapping.STRING_TYPE, ""));
		schemaMapping.addFieldMapping(protocolTypeMapping);
		
		schemaMapping.setSchemaName(data.getSessionData().getName());
		schemaMapping.setTableName(data.getTableName());
		sessionConfig.addSchema(schemaMapping);
		
		//generating session factory using session config
		try {
			return sessionConfig.createSessionFactory();
		} catch (InitializationFailedException e) {
			throw new InitializationFailedException("Initialization failed for diameter concurrency with name: " 
					+ data.getName() + ", Reason : " + e.getMessage(), e);
		}
	}
	
	private void createMandatoryFieldMappings() {
		 mandatoryMapping = new HashMap<MandateryFieldConstants, FieldMapping>();
		 for (FieldMapping mapping : data.getSessionData().getFeildMappings()) {
			 MandateryFieldConstants constant = MandateryFieldConstants.getMandateryFieldConstant(mapping.getField());
			 if (constant != null) {
				 mandatoryMapping.put(constant, mapping);
			 }
		 }
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	public boolean isEligible(T request, V response) {
		return true;
	}

	@Override
	public void handleRequest(ApplicationRequest request,ApplicationResponse response,
			ISession session) {
		
		IDiameterAVP concurrentLoginPolicyAvp = findConcurrentLoginPolicyAttribute(request, response);
		if (concurrentLoginPolicyAvp == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, data.getName() + " - No Concurrent Login Policy AVP" 
						+ " found in request or response so skipping concurrency.");
			}
			return;
		}
		
		String policyName = concurrentLoginPolicyAvp.getStringValue(false);
		ConcurrentLoginPolicyData policyData = this.concurrentLoginPolicyConfiguration.getConcurrentLoginPolicy(policyName);
		if (policyData == null) {
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				String individualIdentity = sessionBuilder.getValueBasedOnFieldMapping(mandatoryMapping.get(MandateryFieldConstants.INDIVIDUAL_ID), request, response);
				LogManager.getLogger().debug(MODULE, data.getName() + " - Configured Concurrent Login Policy: " + policyName + 
						" not found for user identity: " + individualIdentity);
			}
			
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, response.getDiameterAnswer(), 
					ResultCode.DIAMETER_AUTHORIZATION_REJECTED.code + "");
			
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, response.getDiameterAnswer(), 
					DiameterErrorMessageConstants.CONCURRENT_LOGIN_POLICY_NOT_FOUND);
			
			response.setFurtherProcessingRequired(false);
			return;
		}
		
		if (isRequestValidForPolicy(policyData, request, response) == false) {
			DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_MISSING_AVP, DiameterErrorMessageConstants.CONCURRENCY_FAILED);
			return;
		}
		
		try { 
			checkConcurrency(policyData, request, response);
		} catch (DBFailuerException e) {
			data.getDBFailureAction().apply(response, data.getName());
		}
		
	}
	
	private IDiameterAVP findConcurrentLoginPolicyAttribute(ApplicationRequest request,
			ApplicationResponse response) {

		IDiameterAVP concurrentLoginPolicyAvp = request.getAVP(DiameterAVPConstants.EC_CONCURRENT_LOGIN_POLICY_NAME, true);
		
		if (concurrentLoginPolicyAvp == null) {
			concurrentLoginPolicyAvp = response.getAVP(DiameterAVPConstants.EC_CONCURRENT_LOGIN_POLICY_NAME, true);
		}
		
		return concurrentLoginPolicyAvp;
	}
	
	/*
	 * This method checks whether the request contains all the required AVPs
	 */
	private boolean isRequestValidForPolicy(ConcurrentLoginPolicyData policyData, ApplicationRequest request, ApplicationResponse response){
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, data.getName() + " - Validating packet for concurrency");
		}
		String policyMode = policyData.getPolicyMode();
		String policyType = policyData.getPolicyType();
		String serviceTypeAttribute = policyData.getServiceType();

		if (policyMode.equals(ConcurrentPolicyConstants.GENERAL_POLICY)) {
			if (policyType.equals(ConcurrentPolicyConstants.INDIVIDUAL_POLICY)) {
				if (sessionBuilder.getValueBasedOnFieldMapping(mandatoryMapping.get(MandateryFieldConstants.INDIVIDUAL_ID), request, response) == null) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, data.getName() + " - User found in individual policy: " + policyData.getName() 
								+ ". The configured user-identity AVP(s): " 
								+ mandatoryMapping.get(MandateryFieldConstants.INDIVIDUAL_ID).getPropertyName() 
								+ " are not present in the packet. Sending failure answer.");
					}
					return false;
				}
			} else if (policyType.equals(ConcurrentPolicyConstants.GROUP_POLICY)) {
				if (request.getAVP(DiameterAVPConstants.EC_PROFILE_USER_GROUP, true) == null) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, data.getName() + " - User found in group policy: " 
								+ policyData.getName() + " but no group name assigned to user.");
					}
					return false;
				}
			}
		} else if (policyMode.equals(ConcurrentPolicyConstants.SERVICE_WISE_POLICY)) {
			if (request.getAVP(serviceTypeAttribute, true) == null) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, data.getName() + " - The service-type AVP: [" + serviceTypeAttribute 
							+ "] confiured in the concurrent policy " + policyData.getName() 
							+ " is not present in request, so sending failure answer.");
				}
				return false;
			}
			if (policyType.equals(ConcurrentPolicyConstants.INDIVIDUAL_POLICY)) {
				if (sessionBuilder.getValueBasedOnFieldMapping(mandatoryMapping.get(MandateryFieldConstants.INDIVIDUAL_ID), request, response) == null) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, data.getName() + " - User found in service wise policy of individual type: " + policyData.getName()
								+ " but configured user-identity AVP(s): " 
								+  mandatoryMapping.get(MandateryFieldConstants.INDIVIDUAL_ID).getPropertyName()
								+ " are not present in the request.");
					}
					return false;
				}
			} else if (policyType.equals(ConcurrentPolicyConstants.GROUP_POLICY)) {
				if (request.getAVP(DiameterAVPConstants.EC_PROFILE_USER_GROUP, true) == null) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, data.getName() + " - User found in service wise policy of group type: " 
								+ policyData.getName() + " but no group name assigned to user.");
					}
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
	
	private void checkConcurrency(ConcurrentLoginPolicyData policyData, ApplicationRequest request, 
			ApplicationResponse response) {
		Session session = this.sessionFactory.getSession();
		Criteria criteria = session.createCriteria(data.getSessionData().getName());
		String policyMode = policyData.getPolicyMode();
		String policyType = policyData.getPolicyType();
		String concurrencyIdentity = null;

		if(policyMode.equals(ConcurrentPolicyConstants.GENERAL_POLICY)){
			if(policyType.equals(ConcurrentPolicyConstants.INDIVIDUAL_POLICY)){
				concurrencyIdentity = sessionBuilder.getValueBasedOnFieldMapping(mandatoryMapping.get(MandateryFieldConstants.INDIVIDUAL_ID), request, response);
				
				if(LogManager.getLogger().isDebugLogLevel()){
					LogManager.getLogger().debug(MODULE, data.getName() + " - User: " + concurrencyIdentity 
								+ " found in INDIVIDUAL policy : " + policyData.getName());
				}
				
				if(policyData.getMaxLogins() != ConcurrentPolicyConstants.UNLIMITED_SESSIONS){
					criteria.add(Restrictions.eq(data.getConcurrencyIdentityField(), concurrencyIdentity));
					applyGeneralPolicy(criteria, policyData.getMaxLogins(), request, response);
				}else{
					if(LogManager.getLogger().isDebugLogLevel()){
						LogManager.getLogger().debug(MODULE, data.getName() + " - " + concurrencyIdentity + " is found in policy: " 
								+ policyData.getName() + " and is allowed unlimited sessions.");
					}
				}
			}else if(policyType.equals(ConcurrentPolicyConstants.GROUP_POLICY)){
				concurrencyIdentity = request.getAVP(DiameterAVPConstants.EC_PROFILE_USER_GROUP, true).getStringValue(false);
				
				if(LogManager.getLogger().isDebugLogLevel()){
					LogManager.getLogger().debug(MODULE, data.getName() + " - Using groupname: " + concurrencyIdentity 
							+ " for GROUP policy : " + policyData.getName());
				}
				
				if(policyData.getMaxLogins() != ConcurrentPolicyConstants.UNLIMITED_SESSIONS){
					addClassAttribute(response, ("concr_grpname=" + concurrencyIdentity));
					criteria.add(Restrictions.eq(data.getConcurrencyIdentityField(), concurrencyIdentity));
					applyGeneralPolicy(criteria, policyData.getMaxLogins(), request, response);
				}else{
					if(LogManager.getLogger().isDebugLogLevel()){
						LogManager.getLogger().debug(MODULE, data.getName() + " - " + concurrencyIdentity + " group is allowed unlimited sessions in policy: " + policyData.getName() + ".");
					}
				}
			}
		}else if(policyMode.equals(ConcurrentPolicyConstants.SERVICE_WISE_POLICY)){
			/* The flow for concurrent login service type policy is that
			 * 1) First the concurrency based on the individual and group level is checked for 
			 * total no of sessions. If the no of sessions allowed are unlimited but still the check
			 * for service type is also required as there may be need to restrict some particular service sessions
			 * to a limited no
			 * 2) When the check for general concurrency is valid then the concurrency based on service type is also
			 * checked if the policy is of type Service Wise. Now it checks the service wise login count
			 * 
			 * So the flow is General Check ------> [ServiceWise Check if policy is service 
			 */

			
			if(policyType.equals(ConcurrentPolicyConstants.INDIVIDUAL_POLICY)){
				concurrencyIdentity = sessionBuilder.getValueBasedOnFieldMapping(mandatoryMapping.get(MandateryFieldConstants.INDIVIDUAL_ID), request, response);
				criteria.add(Restrictions.eq(data.getConcurrencyIdentityField(), concurrencyIdentity));
				addClassAttribute(response, ("concr_grpname=" + concurrencyIdentity + ";" + "loginpolicy=" + policyData.getName()));

				if(LogManager.getLogger().isDebugLogLevel()){
					LogManager.getLogger().debug(MODULE, data.getName() + " - User : " + concurrencyIdentity 
							+ " found in service-wise policy of individual type : " + policyData.getName());
				}
				
			}else if(policyType.equals(ConcurrentPolicyConstants.GROUP_POLICY)){
				concurrencyIdentity = request.getAVP(DiameterAVPConstants.EC_PROFILE_USER_GROUP, true).getStringValue(false);
				
				criteria.add(Restrictions.eq(data.getConcurrencyIdentityField(), concurrencyIdentity));
				addClassAttribute(response, ("concr_grpname=" + concurrencyIdentity + ";" + "loginpolicy=" + policyData.getName()));
				
				if(LogManager.getLogger().isDebugLogLevel()){
					LogManager.getLogger().debug(MODULE, data.getName() + " - User found in service-wise policy of group type : " + policyData.getName());
				}
			}

			applyServiceWisePolicy(criteria, request, response, policyData);
		}
	}
	
	
	private void sessionOverride(ApplicationRequest request, ApplicationResponse response,
			List<SessionData> sessionDataList) {
		
		//condition check for session override is enabled and session found for current request
		if (sessionOverrideColumns.size() > 0 && sessionDataList.isEmpty() == false) {
			SessionData overridableSession = getOverridableSession(request, sessionDataList);
			if(overridableSession != null && shouldOverrideSession()){
				//call for Submission of ASYNC request
				generateASR(overridableSession, request, response);
			}
		}
		
	}
	
	private boolean applyConcurrency(ApplicationRequest request, ApplicationResponse response,
			List<SessionData> sessionDataList,  long maxLogin) {
		//condition check for concurrency only
		if (sessionDataList.size() >= maxLogin) {
			setFailureAnswer(request, response, "Max login limit reached. Max sessions allowed: " + 
					maxLogin, DiameterErrorMessageConstants.MAX_LOGIN_LIMIT_REACHED);
			return false;
		}
		return true;
	}

	/*
	 * This method searches for ACTIVE sessions based on the criteria.
	 * ACTIVE LOGINS = MAX LOGINS then it checks whether the session override action
	 * is specified and does ESI communication to send CoA or STOP.
	 */
	private boolean applyGeneralPolicy(Criteria criteria, Long maxLoginSessions, 
			ApplicationRequest request, ApplicationResponse response){
		List<SessionData> sessionDataList = getSessionDataList(criteria);
		sessionOverride(request, response, sessionDataList);
		return applyConcurrency(request, response, sessionDataList, maxLoginSessions);
	}
	
	private boolean applyServiceWisePolicy(Criteria criteria, ApplicationRequest request, 
			ApplicationResponse response, ConcurrentLoginPolicyData policyData){
		
		Long maxLoginSessionsForOtherServices = null;
		List<SessionData> sessionDataList = getSessionDataList(criteria);
		sessionOverride(request, response, sessionDataList);
		
		//condition check if maxLogin limit is Unlimited
		if (policyData.getMaxLogins() != ConcurrentPolicyConstants.UNLIMITED_SESSIONS) {
			if (applyConcurrency(request, response, sessionDataList, policyData.getMaxLogins()) == false) {
				return false;
			}
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, data.getName() + "- User is allowed unlimited sessions for Service Type Attribute: " 
						+ " in policy: " + policyData.getName());
			}
		}
		
		List<SessionData> serviceDataList = new ArrayList<SessionData>();

		String serviceTypeValue = request.getAVP(policyData.getServiceType(), true).getStringValue(false);
		
		for (int i=0; i<sessionDataList.size(); i++) {
			SessionData sessionData = sessionDataList.get(i);
			if (serviceTypeValue.equals(sessionData.getValue(mandatoryMapping.get(MandateryFieldConstants.PDP_TYPE).getPropertyName()))) {
				serviceDataList.add(sessionData);
			}
		}
		
		List<ServceWiseLogin> serviceWiseLoginList = policyData.getServiceWiseLoginList();

		if (serviceWiseLoginList != null) {			
			for (int i=0;i<serviceWiseLoginList.size();i++) {
				ServceWiseLogin serviceWiseLogin = serviceWiseLoginList.get(i);				
				if (serviceWiseLogin.getServiceTypeValue().equals(serviceTypeValue)) {
					/*
					 * when service type received in request than concurrency should be checked 
					 * only for that service type no other processes are required after that. 
					 * 
					 */
					return applyConcurrency(request, response, serviceDataList, serviceWiseLogin.getMaxServiceWiseSessions());
				} else if (serviceWiseLogin.getServiceTypeValue().equals(String.valueOf(ConcurrentPolicyConstants.OTHERS_SERVICETYPE))) {
					maxLoginSessionsForOtherServices = serviceWiseLogin.getMaxServiceWiseSessions();
				}
			}
		}
		
		/*
		 * when request false into other category only than below if block will execute
		 */
		if (maxLoginSessionsForOtherServices != null) {
			return applyConcurrency(request, response, serviceDataList, maxLoginSessionsForOtherServices);
		}
		
		return true;
		
	}
	
	private List<SessionData> getSessionDataList(Criteria criteria) {
		Session session = this.sessionFactory.getSession();

		//adding the criteria for looking only sessions that are active
		criteria.add(Restrictions.eq(AAAServerConstants.SESSION_STATUS, DiameterAttributeValueConstants.EC_SESSON_STATUS_ACTIVE));
		List<SessionData> list = session.list(criteria);
		if (list == null) {
			throw new DBFailuerException("Db failure noticed while getting session data");
		}
		return list;
	}
	
	private SessionData getOverridableSession(ApplicationRequest radServiceRequest,List<SessionData> sessionList){
		Map<String,String> columnToAttributeValue = new HashMap<String,String>();
		for(String column : this.sessionOverrideColumns){
			String properties = fieldMappingParser.getPropertiesByColumn(column);
			for(PropertyType propertyType : fieldMappingParser.getPropertyListByColumn(column)){
				IDiameterAVP avp = radServiceRequest.getAVP(propertyType.getPropertyName(), true);
				if(avp != null){
					columnToAttributeValue.put(properties, avp.getStringValue());
					break;
				}
			}
		}	
		/*
		 * If session override attribute is not present into the request packet, then by default first record will be considered as overridable session
		 */
		if(columnToAttributeValue.size() == 0)
			return sessionList.get(0);
		
		boolean sessionFound = false;
		SessionData sessionData = null;
		for(int index=0; index< sessionList.size(); index++){
			sessionData = sessionList.get(index);
			for(String key : columnToAttributeValue.keySet()){
				if(columnToAttributeValue.get(key).equals(sessionData.getValue(key))){
					sessionFound = true;
				}else{
					/*
					 * If any of the session override column value and attribute value does not match then no need to further comparision of rest columns.
					 */
					sessionFound = false;
					break;
				}
			}
			/*
			 * If sessionFound is true after execution of above for loop, then it means the current session is overridable session and after
			 * this no need to compare rest session, as session override feature required only one session
			 */
			if(sessionFound)
				return sessionData;
		}
		/*
		 * If sessionFound is false then this method will return null
		 */
		return null;
	}
	
	private boolean shouldOverrideSession() {
		return data.getSessionOverrideAction() == SessionOverrideActions.GENREATE_ASR;
	}

	private void generateASR(SessionData sessionData, ApplicationRequest request,
			ApplicationResponse response){
		
		DiameterRequest asrRequest = new DiameterRequest();

		String applicationId = sessionData.getValue(mandatoryMapping.get(MandateryFieldConstants.APPLICATION_ID).getPropertyName());
		if (applicationId == null) {
			applicationId = mandatoryMapping.get(MandateryFieldConstants.APPLICATION_ID).getDefaultValue();
			if (applicationId == null) {
				LogManager.getLogger().warn(MODULE, "ASR generation failed, Reason: Application Id not available");
				return;
			}
		}
		
		asrRequest.setApplicationID(Long.parseLong(applicationId));
		asrRequest.setCommandCode(CommandCode.ABORT_SESSION.code);
		asrRequest.setEnd_to_endIdentifier(EndToEndPool.get());
		asrRequest.setHop_by_hopIdentifier(HopByHopPool.get());
		asrRequest.setProxiableBit();

		sessionBuilder.prepareDiameterPacket(asrRequest, sessionData);
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Generated ASR: " + asrRequest);
		}

		SettableFuture<DiameterAnswer> diameterFuture;
		try {
			ProtocolType sessionTypeConstant = ProtocolType.from(sessionData.getValue(protocolTypeMapping.getColumnName()));
			diameterFuture = eliteAAAServiceExposerManager.sendSessionDisconnect(
					sessionTypeConstant, System.getProperty(data.getName() + ".translation-mapping"), asrRequest);
			diameterFuture.addListener(new ConcurrencyResponseListener(diameterFuture), serviceContext.getServerContext().getTaskScheduler());
		} catch (CommunicationException e) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Not removing local session, Reason: error while disconnecting overridden session, Reason: " 
						+ e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	private void setFailureAnswer(ApplicationRequest request, ApplicationResponse response, String logMessage, String errorMessage){
		DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_AUTHORIZATION_REJECTED, errorMessage);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Sending failure answer, Reason: " + logMessage);
		}
	}
	
	private void addClassAttribute(ApplicationResponse response, String classAttributeValue){
		IDiameterAVP classAttribute = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.CLASS);
		if(classAttribute == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Class attribute not found in dictionary. Will not be added. Check dictionary.");
			}
			return;
		}
		classAttribute.setStringValue(classAttributeValue);
		response.addAVP(classAttribute);
	}
	
	public class ConcurrencyResponseListener implements Runnable {
		
		private final SettableFuture<DiameterAnswer> diameterFuture;

		public ConcurrencyResponseListener(SettableFuture<DiameterAnswer> diaFuture) {
			this.diameterFuture = diaFuture;
		}

		@Override
		public void run() {
			try {
				DiameterAnswer diameterAnswer = diameterFuture.get();
				IDiameterAVP resultCodeAVP = diameterAnswer.getAVP(DiameterAVPConstants.RESULT_CODE, true);
				if (resultCodeAVP == null) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Not removing local session, Reason: Result code AVP not found");
					}
					return;
				}
				IDiameterAVP sessionID = diameterAnswer.getAVP(DiameterAVPConstants.SESSION_ID, true);
				ResultCode resultCode = ResultCode.fromCode((int) resultCodeAVP.getInteger());
				if (resultCode.category.isFailureCategory) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Removing local session, Reason: Failure result code: " 
								+ resultCode + " found in ASA.");
					}
					removeDBSessionWith(sessionID.getStringValue());
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Not removing local session, Reason: Success result code: " 
								+ resultCode + " found in ASA.");
					}
				}

			} catch (InterruptedException e) {
				LogManager.getLogger().trace(MODULE, e);
			} catch (ExecutionException e) {
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		
	}
	
	private void removeDBSessionWith(String sessionId) {
		Session session = sessionFactory.getSession();
		Criteria criteria = session.createCriteria(data.getSessionData().getName());
		criteria.add(Restrictions.eq(mandatoryMapping.get(MandateryFieldConstants.SESSION_ID).getColumnName(), sessionId));
		session.delete(criteria);
	}
	
	private static class DBFailuerException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public DBFailuerException(String string) {
			super(string);
		}
		
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
}