package com.elitecore.aaa.radius.sessionx;
import static com.elitecore.aaa.radius.sessionx.conf.LocalSessionManagerData.GROUPNAME_FIELD;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.conf.impl.MiscellaneousConfigurable;
import com.elitecore.aaa.core.data.ClientTypeConstant;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.service.RadServiceContext;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.sessionx.conf.ConcurrentLoginPolicyConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.LocalSessionManagerData;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerData;
import com.elitecore.aaa.radius.sessionx.conf.impl.ConcurrentLoginPolicyData;
import com.elitecore.aaa.radius.sessionx.conf.impl.LocalSessionManagerConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.impl.LocalSessionManagerConfiguration.BehaviourType;
import com.elitecore.aaa.radius.sessionx.conf.impl.LocalSessionManagerConfiguration.DBFailureActions;
import com.elitecore.aaa.radius.sessionx.conf.impl.LocalSessionManagerConfiguration.SessionClosureAndOverrideActions;
import com.elitecore.aaa.radius.sessionx.conf.impl.ServceWiseLogin;
import com.elitecore.aaa.radius.sessionx.data.FieldMappingParser;
import com.elitecore.aaa.radius.sessionx.data.ImproperSearchCriteriaException;
import com.elitecore.aaa.radius.sessionx.data.PropertyType;
import com.elitecore.aaa.radius.sessionx.data.RadiusSessionDataAndCriteriaBuilder;
import com.elitecore.aaa.radius.systemx.esix.udp.DefaultExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.DynamicNasExternalSystemData;
import com.elitecore.aaa.radius.systemx.esix.udp.RadResponseListener;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPCommGroup;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPResponse;
import com.elitecore.aaa.radius.systemx.esix.udp.impl.RadUDPCommGroupImpl;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.aaa.util.constants.AAAServerConstants.ProtocolType;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.commons.util.ConfigurationUtil;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;
import com.elitecore.core.serverx.sessionx.AutoSessionCloserListner;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SchemaMapping;
import com.elitecore.core.serverx.sessionx.Session;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionFactory;
import com.elitecore.core.serverx.sessionx.SessionResultCode;
import com.elitecore.core.serverx.sessionx.SystemPropertiesProvider;
import com.elitecore.core.serverx.sessionx.conf.SessionConfiguration;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.core.serverx.sessionx.impl.FieldMappingImpl;
import com.elitecore.core.serverx.sessionx.impl.SchemaMappingImpl;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.util.url.URLData;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * Session Manager are of two types <b>1) Local 2) Remote</b><br>
 * <br>
 * 1) <b>Local</b> - Local session managers do two tasks and they are they do session management
 *                   and also check for concurrency, hence the name ConcurrencySessionManager.
 *                  <br>Local session managers have two behavior types <b>a) Accounting (Classic) b) Authentication</b>
 * 		<br><b>a) Accounting (Classic)</b> - In this type of behavior the session is created at the time of
 * 								  Accounting Start request and concurrency is checked at the time
 *                                of next authentication request
 *      <br><b>b) Authentication</b>       - In this type of behavior session is created at the authentication
 *      						  request and the session is just stale at that time and it is marked
 *      						  as ACTIVE when the accounting start is received for that session. 
 *      						  Concurrency is checked at the next authentication request. This was
 *      						  introduced for the WIFI OFFLOAD project in which DHCP server sent a 
 *                                request and required the session to be present after the authentication
 *                                request.
 * <br> 
 * @author narendra.pathai<br>
 */
//FIXME have to look up at commenting the logic for service type that is extracted from class attribute in future
//FIXME as same attribute has to be configured in concurrent logic policy and "PDP Type" of session manager
/** FIXME Right now in the auth behavior when the accounting request comes we are not including the user identity attribute for search
 *  criteria. And so because of that there will be problems. We are relying on search column for user identity also. So during fetch inactive
 *  sessions the inactive session of some other guy will get deleted as the criteria will not have user identity. This is critical. 
 */

public class ConcurrencySessionManager implements ReInitializable {
	public static final String MODULE="CONCURRENCY_SESSION_MANAGER";
	private static final String NO_SHARED_SECRET = "no-shared-secret";
	private static final int NO_ROW_UPDATED = 0;
	
	private static final short INDEX_OF_CONCURRENCY_IDENTITY = 0;
	private static final short INDEX_OF_SERVICE_TYPE = 1;
	public static final  String SESSION_ID = "Session ID";
	public static final  String PDP_TYPE = "PDP Type";
	private static final String NAS_ID = "NAS ID";
	public static final  String SESSION_TIMEOUT = "Session Timeout";
	public static final  String USER_IDENTITY = "User Identity";
	public static final String AAA_ID = "AAA ID";
	
	protected final AAAServerContext serverContext;
	private String smInstanceId;
	private String smInstanceName;
	private SessionFactory sessionFactory;	
	private String tableName;
	
	
	/*
	 * The Search Columns are used for searching the existing sessions. The values can be 
	 * , or ; separated. This property is converted to Attributes using the mapping for the 
	 * column.
	 */
	private String searchColumns;
	
	private int sessionCloseAction;
	private RadUDPCommGroup udpCommunicationGrp ;
	private RadUDPCommGroup udpCommAcctStopGrp;
	private List<String> esiList;
	private List<String> esiListForDMAcctStop;
	private FieldMapping userIdentityFieldMapping;
	private FieldMapping aaaIdentityFieldMapping;
	private String smType;
	private int localSMBehaviourType;
	
	private RadiusSessionDataAndCriteriaBuilder sessionDataBuilder;
	private FieldMappingParser fieldMappingParser;
	/*
	 * This will store the comma separated property names based on the search columns specified
	 * The property names are search
	 */
	private LocalSessionManagerData sessionManagerConfig;


	private List<FieldMappingImpl> additionalFieldMappingList ;
	private Map<String,FieldMapping> mandatoryFieldMappingMap;

	private SessionOverrideEventHandler sessionOverrideEventHandler;
	
	/**
	 * @see LocalSessionManagerConfiguration
	 */
	private int sessionOverrideAction;
	
	/**
	 * @see LocalSessionManagerConfiguration for details on this configuration
	 */
	private String[] sessionOverrideColumns;
	
	private DBFailureActions dbFailureAction;
	private String actionOnStop = LocalSessionManagerData.SESSION_STOP_ACTION_DELETE;
	
	private String concurrencyIdentityField;
	private LocalSessionManagerData localSessionManagerData;
	private ConcurrentLoginPolicyConfiguration concurrentLoginPolicyConfiguration;

	public ConcurrencySessionManager(AAAServerContext serverContext,LocalSessionManagerData localSessionManagerData) {
		this.serverContext = serverContext;
		this.localSessionManagerData = localSessionManagerData;
		this.sessionManagerConfig = localSessionManagerData;
		this.smInstanceId = localSessionManagerData.getInstanceId();
		this.smInstanceName = localSessionManagerData.getInstanceName();
		this.tableName = localSessionManagerData.getTableName();
		this.searchColumns = 	localSessionManagerData.getSearchCols();
		this.smType = localSessionManagerData.getType();
		this.localSMBehaviourType = localSessionManagerData.getBehaviourType();
		this.sessionOverrideAction = localSessionManagerData.getSessionOverrideAction();
		this.dbFailureAction = localSessionManagerData.getDBFailureAction();
		this.concurrencyIdentityField = localSessionManagerData.getConcurrencyIdentityField();
		
		this.additionalFieldMappingList = new ArrayList<FieldMappingImpl>(0);
		this.mandatoryFieldMappingMap = new HashMap<String, FieldMapping>();
		this.udpCommunicationGrp = new RadUDPCommGroupImpl(serverContext);		
		this.esiList = new ArrayList<String>(0);
		this.esiListForDMAcctStop = new ArrayList<String>(0);
		this.sessionOverrideColumns = new String[0];
		this.concurrentLoginPolicyConfiguration = serverContext.getServerConfiguration().getConcurrentLoginPolicyConfiguration();
	}

	public void init() throws InitializationFailedException{
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE,"Initializing Local Session Manager: " + localSessionManagerData);
		}
		
		SchemaMappingImpl schemaMapping;
		String databaseDsId = sessionManagerConfig.getDatabaseDsId();

		DBDataSource dbDatasource = serverContext.getServerConfiguration().getDatabaseDSConfiguration().getDataSource(String.valueOf(databaseDsId));

		deriveMandatoryAndAdditionalFieldMappings(sessionManagerConfig.getFieldMappings());
		deriverUserIdentityFieldAndRemoveMapping();
		deriveAAAId();

		String sessionOverrideCols = sessionManagerConfig.getSessionOverrideColumns();
		setSearchColumns(sessionManagerConfig.getSearchCols());
		if(sessionOverrideCols != null && sessionOverrideCols.trim().length() > 0){
			this.sessionOverrideColumns = sessionOverrideCols.trim().split("[,;]");
		}

		//initializing the external systems
		this.esiList = sessionManagerConfig.getEsiRelations();
		this.esiListForDMAcctStop = sessionManagerConfig.getNakEsiList();

		if(esiList !=null && esiList.isEmpty() == false) {									
			for(int i = 0;i<esiList.size();i++){
				String esiWeightage = esiList.get(i);
				String[] esiWghtArr = esiWeightage.split("_");							
				try {
					Optional<DefaultExternalSystemData> udpES = serverContext.getServerConfiguration().getRadESConfiguration().getESData(String.valueOf(esiWghtArr[0]));
					if (udpES.isPresent() == false) {
						LogManager.getLogger().warn(MODULE, this.smInstanceName 
								+ "- Failed to initialize the external system with esiId = "
								+ esiWghtArr[0] + " configured for auto-session-closure. Reason: "
								+ "Configuration not found");
						continue;
					}
					UDPCommunicator esCommunicator = serverContext.getRadUDPCommunicatorManager().findCommunicatorByIDOrCreate(esiWghtArr[0], this.serverContext, udpES.get());
					udpCommunicationGrp.addCommunicator(esCommunicator,Integer.parseInt(esiWghtArr[1]));
				} catch(Exception e) {
					LogManager.getLogger().warn(MODULE, this.smInstanceName 
							+ "- Failed to initialize the external system with esiId = "
							+ esiWghtArr[0] +" configured for auto-session-closure. " 
							+ "Reason : " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}	

		// adding udp communication grp for acct stop in case of NAK from NAS at the time of sending DM

		if(esiListForDMAcctStop != null && esiListForDMAcctStop.isEmpty() == false){	
			udpCommAcctStopGrp = new RadUDPCommGroupImpl(this.serverContext);
			for(int i = 0;i<esiListForDMAcctStop.size();i++){
				String esiWeightage = esiListForDMAcctStop.get(i);
				String[] esiWghtArr = esiWeightage.split("_");							
				try{
					Optional<DefaultExternalSystemData> udpES = serverContext.getServerConfiguration().getRadESConfiguration().getESData(String.valueOf(esiWghtArr[0]));
					if (udpES.isPresent() == false) {
						LogManager.getLogger().warn(MODULE, this.smInstanceName 
								+ "- Failed to initialize the external system with esiId = "
								+ esiWghtArr[0] + " configured for auto-session-closure. "
								+ "Reason: Configuration not found");
						continue;
					}
					UDPCommunicator esCommunicator = serverContext.getRadUDPCommunicatorManager().findCommunicatorByIDOrCreate(esiWghtArr[0], this.serverContext, udpES.get());
					udpCommAcctStopGrp.addCommunicator(esCommunicator,Integer.parseInt(esiWghtArr[1]));
				}catch(Exception e){
					LogManager.getLogger().warn(MODULE, this.smInstanceName 
							+ "- Failed to initialize the external system with esiId = "
							+ esiWghtArr[0] +" configured for auto-session-closure. "
							+ "Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}	

		if(!sessionManagerConfig.getAutoSessionCloser()){
			schemaMapping = new SchemaMappingImpl(AAAServerConstants.CONCURRENCY_ID_FIELD,sessionManagerConfig.getStartTimeFeild(),sessionManagerConfig.getLastUpdateTimeFeild());
		}else{
			this.sessionCloseAction = sessionManagerConfig.getSessionCloseAction();
			schemaMapping = new SchemaMappingImpl(AAAServerConstants.CONCURRENCY_ID_FIELD,sessionManagerConfig.getStartTimeFeild(),sessionManagerConfig.getLastUpdateTimeFeild(),
					new AutoSessionCloserListner() {
						@Override
						public void timedoutSession(SessionData session) {
							if (serverContext.getServerInstanceId()
									.equals(session.getValue(aaaIdentityFieldMapping.getPropertyName()))) {
								if(LogManager.getLogger().isInfoLogLevel()) {
									LogManager.getLogger().info(MODULE, "Closing session : " + session);
								}
								closeSession(session);
							}
						}

			});
			schemaMapping.setSessionIdleTime((int)sessionManagerConfig.getSessionTimeout());
			schemaMapping.setBatchCount((int)sessionManagerConfig.getCloseBatchCount());
			schemaMapping.setThreadSleepTime(sessionManagerConfig.getSessionThreadSleepTime());

		}


		//adding the fixed mapping for Concurrency identity
		FieldMapping concurrencyIdentityFieldMapping = new FieldMappingImpl(FieldMapping.STRING_TYPE, GROUPNAME_FIELD, GROUPNAME_FIELD);
		schemaMapping.addFieldMapping(concurrencyIdentityFieldMapping);

		FieldMappingImpl protocolTypeMapping = new FieldMappingImpl(FieldMapping.STRING_TYPE, AAAServerConstants.PROTOCOL_TYPE_FIELD, 
				AAAServerConstants.PROTOCOL_TYPE_FIELD, ProtocolType.RADIUS.name());
		schemaMapping.addFieldMapping(protocolTypeMapping);

		for(int i=0;i<additionalFieldMappingList.size();i++){
			schemaMapping.addFieldMapping(additionalFieldMappingList.get(i));
		}

		for(FieldMapping fieldMapping:mandatoryFieldMappingMap.values()){
			schemaMapping.addFieldMapping(fieldMapping);
		}

		fieldMappingParser = new FieldMappingParser(smInstanceName,schemaMapping.getFieldMappings());

		schemaMapping.setIdGenerator(SchemaMapping.DB_SEQUENCE_GENERATOR);
		schemaMapping.setSchemaName(sessionManagerConfig.getTableName()); 
		schemaMapping.setTableName(sessionManagerConfig.getTableName());
		schemaMapping.setSequenceName(sessionManagerConfig.getIdSequenceName());

		//field mapping for session status
		FieldMappingImpl sessionStatusFieldMapping = new FieldMappingImpl(FieldMapping.STRING_TYPE, 
				AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS);
		schemaMapping.addFieldMapping(sessionStatusFieldMapping);

		/*
		 * Creating list of mapping based on the search columns for searching the session
		 */
		List<FieldMapping> sessionSearchAttributesList = createSessionSearchAttrList(schemaMapping);
		this.sessionDataBuilder = new RadiusSessionDataAndCriteriaBuilder(fieldMappingParser, sessionSearchAttributesList);
			
		SessionConfigurationImpl sessionConfig = new SessionConfigurationImpl(serverContext);
		SystemPropertiesProvider systemPropertiesProvider = new SystemPropertiesProvider() {

			@Override
			public boolean isNoWaitEnabled() {
				return ConfigurationUtil.stringToBoolean(System.getProperty(MiscellaneousConfigurable.NOWAIT_SYSTEM_PROPERTY), true);
			}

			@Override
			public boolean isBatchEnabled() {
				return ConfigurationUtil.stringToBoolean(System.getProperty(MiscellaneousConfigurable.BATCH_SYSTEM_PROPERTY), true);
			}

			@Override
			public int getQueryTimeout() {
				return sessionManagerConfig.getQueryTimeoutInSecs();
			}

			@Override
			public int getBatchQueryTimeout() {
				return sessionManagerConfig.getQueryTimeoutInSecs();
			}
		};
		sessionConfig.setSystemPropertiesProvider(systemPropertiesProvider);
		
		LogManager.getLogger().info(MODULE, "For " + this.smInstanceName + "- SQL Batch update is " + systemPropertiesProvider.isBatchEnabled() 
				+ " and SQL No-wait is " + systemPropertiesProvider.isNoWaitEnabled());
			
		boolean enableBatchUpdate = sessionManagerConfig.getIsBatchUpdateEnable();
		if(enableBatchUpdate){
			sessionConfig.setSessionFactoryType(SessionConfiguration.DB_SESSION_WITH_BATCH_UPDATE);
			sessionConfig.setBatchUpdateInterval(sessionManagerConfig.getBatchUpdateInterval());
			sessionConfig.setMaxBatchSize(sessionManagerConfig.getBatchSize());
			sessionConfig.setDeleteBatched(true);
			sessionConfig.setUpdateBatched(true);
			sessionConfig.setSaveBatched(true);
		}else{
			sessionConfig.setSessionFactoryType(SessionConfiguration.DB_SESSION);
		}
		sessionConfig.setInterval(dbDatasource.getStatusCheckDuration());
		sessionConfig.addSchema(schemaMapping);					
		sessionConfig.addDataSource(dbDatasource);

		this.sessionFactory = sessionConfig.createSessionFactory();

		//this will create the object of SessionOverrideHandler only is the configured Session override action is 
		//DM or DM+STOP or STOP
		if(isAsyncDMorSTOPRequired(sessionOverrideAction)){
			sessionOverrideEventHandler = new SessionOverrideEventHandler();
		}

		actionOnStop = sessionManagerConfig.getActionOnStop();
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Session Manager :" + this.smInstanceName + " has been initialised");
		}
	}

	protected void closeSession(SessionData session) {
		SessionCloseEventHandler seCloseEvntHandler = new SessionCloseEventHandler(session);
		seCloseEvntHandler.handleSessionCloseAction();
	}
	
	protected final SessionManagerData getSessionManagerData(){
		return sessionManagerConfig;
	}
	
	/*
	 * sets search columns to USER_IDENTITY column name 
	 * if not configured
	 * else set to configured list
	 * JIRA-3079
	 */
	private void setSearchColumns(String searchCols) {
		if (searchCols == null || searchCols.trim().isEmpty()) {
			String userIdColumn = mandatoryFieldMappingMap.get(USER_IDENTITY).getColumnName();
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Discrete Search Fields not configured, so applying default value as User Identity column value: " + userIdColumn);
			}
			this.searchColumns = userIdColumn;
		} else {
			this.searchColumns = searchCols;
		}
	}

	private void deriverUserIdentityFieldAndRemoveMapping() {
		this.userIdentityFieldMapping = mandatoryFieldMappingMap.get(USER_IDENTITY); 
	}
	
	private void deriveAAAId(){
		this.aaaIdentityFieldMapping = mandatoryFieldMappingMap.get(AAA_ID);
	}

	private void deriveMandatoryAndAdditionalFieldMappings(List<FieldMappingImpl> fieldMappingList) {
		
		List<FieldMappingImpl> tempFieldMappingList = new ArrayList<FieldMappingImpl>();
		Map<String ,FieldMapping> tempMandatoryMap = new HashMap<String, FieldMapping>();
		if(fieldMappingList!=null && fieldMappingList.isEmpty() == false){
			for(int j=0;j<fieldMappingList.size();j++){
				if(fieldMappingList.get(j).getField()!=null && fieldMappingList.get(j).getField().trim().length()>0){
					tempMandatoryMap.put(fieldMappingList.get(j).getField(), fieldMappingList.get(j));
				}else {
					tempFieldMappingList.add(fieldMappingList.get(j));
				}
			}
		}
		this.additionalFieldMappingList = tempFieldMappingList;
		this.mandatoryFieldMappingMap = tempMandatoryMap;
		
	}

	/*
	 * This method takes as input two parameters 1 - Schema Mapping 2 - Search columns
	 * This method based on the search columns specified creates the list of search attributes
	 * fetching the field mapping for that column from schema mapping
	 * Search Columns can be (,) Comma separated
	 */
	private List<FieldMapping> createSessionSearchAttrList(SchemaMapping schemaMapping){
		List<FieldMapping> searchAttrs = new ArrayList<FieldMapping>();
		if(searchColumns != null && searchColumns.trim().length() > 0){
			String[] searchColsList = searchColumns.split("[,;]");
			for(int i = 0; i<searchColsList.length; i++){
				FieldMapping mapping = schemaMapping.getFieldMapping(searchColsList[i]);
				if(mapping != null){
					searchAttrs.add(mapping);
				}
			}
		}
		return searchAttrs;
	}

	public <T extends RadServiceRequest, V extends RadServiceResponse> void handleAccountingRequest(T request, V response, RadServiceContext<T, V> serviceContext) {
		IRadiusAttribute acctStatusTypeAttr = request.getRadiusAttribute(RadiusAttributeConstants.ACCT_STATUS_TYPE,true);
		if(acctStatusTypeAttr != null){
			try {
			int acctStatusTypeAttrValue = acctStatusTypeAttr.getIntValue();
			if(acctStatusTypeAttrValue == RadiusAttributeValuesConstants.START){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, this.smInstanceName + "- Session Management for Accounting Start Request with behavior type: " + BehaviourType.getName(this.localSMBehaviourType));
				}
				handleAccountingStartRequest(request, response,(RadServiceContext<T, V>)serviceContext);
			}else if(acctStatusTypeAttrValue == RadiusAttributeValuesConstants.INTERIM_UPDATE){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, this.smInstanceName + "- Session Management for Accounting Update Request with behavior type: " + BehaviourType.getName(this.localSMBehaviourType));
				}				
				handleAccountingUpdateRequest(request, response,(RadServiceContext<T, V>)serviceContext);
			}else if(acctStatusTypeAttrValue == RadiusAttributeValuesConstants.STOP){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, this.smInstanceName + "- Session Management for Accounting Stop Request with behavior type: " + BehaviourType.getName(this.localSMBehaviourType));
				}
				handleAccountingStopRequest(request, response,(RadServiceContext<T, V>)serviceContext);
			}else if(acctStatusTypeAttrValue == RadiusAttributeValuesConstants.ACCOUNTING_ON || acctStatusTypeAttrValue == RadiusAttributeValuesConstants.ACCOUNTING_OFF){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, this.smInstanceName + "- Session Management for Accounting On/Off Request with behavior type: " + BehaviourType.getName(this.localSMBehaviourType));
				}
				handleAccountingOnOffRequest(request, response,(RadServiceContext<T, V>)serviceContext);
			}
			} catch (ImproperSearchCriteriaException e) {
				dropAccountingRequest(request, response, e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
		}
	}
	}

	private <T extends RadServiceRequest, V extends RadServiceResponse> void handleAccountingStartRequest(
			T request, V response, RadServiceContext<T, V> serviceContext) throws ImproperSearchCriteriaException {

		if(this.localSMBehaviourType == BehaviourType.ACCOUNTING.type){
			handleClassicFlavorAccountingStartRequest(request, response,serviceContext);
		}else if(this.localSMBehaviourType == BehaviourType.AUTHENTICATION.type){
			handleAuthFlavorAccountingStartRequest(request,response,serviceContext);
		}
	}


	/*
	 * This method is for authentication behavior type of session manager
	 * Step 1 ---- If no session id reference attributes is present in the request then
	 * 			   NO UPDATE will occur.
	 * 
	 * Step 2 ---- Creating the new session data for updating the session values
	 * 
	 * Step 3 ---- Creating the session
	 */
	private <T extends RadServiceRequest, V extends RadServiceResponse> void handleClassicFlavorAccountingStartRequest(
			T request, V response,RadServiceContext<T, V> serviceContext) throws ImproperSearchCriteriaException{
		//Step 1
		FieldMapping sessionIdFieldMapping = mandatoryFieldMappingMap.get(SESSION_ID);
		String sessionIdValue = sessionDataBuilder.getValueBasedOnFieldMapping(sessionIdFieldMapping, request, response);
		if(sessionIdValue == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, this.smInstanceName + "- None of Session Id Attributes [" + sessionIdFieldMapping.getPropertyName() + "] is present in the request/response so no create will occur.");
				return;
			}
		}

		//Step 2
		String [] groupNameAndServiceType = extractGroupNameAndServiceType(request,response);
		String groupName = groupNameAndServiceType[INDEX_OF_CONCURRENCY_IDENTITY];


		Session session = this.sessionFactory.getSession();		
		SessionData sessionData = sessionFactory.createSessionData(this.tableName);
		sessionDataBuilder.setSessionDataForInsert(sessionData, request, response, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);

		// add the value of groupname in sessiondata 
		sessionData.addValue(GROUPNAME_FIELD, groupName);


		//add the value of session status to be active
		sessionData.addValue(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_ACTIVE);
		
		if (shouldReuseSession()) {
			Criteria criteria = session.createCriteria(this.tableName);
			criteria.add(Restrictions.eq(sessionIdFieldMapping.getColumnName(), sessionIdValue));

			int existingSessionsCounts = session.count(criteria);
			
			if (existingSessionsCounts < 0) {
				dbFailureAction.forAccounting(request, response, groupName);
			} else if(existingSessionsCounts == 0) {
				saveSession(request, response, serviceContext, sessionIdValue, session, sessionData);
			} else {
				updateSession(request, response, sessionIdValue, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
			}
		} else {
			saveSession(request, response, serviceContext, sessionIdValue, session, sessionData);
		}
	}

	private boolean shouldReuseSession() {
		return LocalSessionManagerData.SESSION_STOP_ACTION_UPDATE.equalsIgnoreCase(actionOnStop);
	}

	private <V extends RadServiceResponse, T extends RadServiceRequest> void saveSession(
			T request, V response, RadServiceContext<T, V> serviceContext,
			String sessionIdValue, Session session, SessionData sessionData) {
		
		int result = session.save(sessionData);
		if (result < 0) {
			onSessionCreationFailure(serviceContext.getServerContext(), sessionIdValue);
			dbFailureAction.forAccounting(request, response, smInstanceName);
		} else {
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, smInstanceName + "- Session creation successful for Session Id: " + sessionIdValue); 
			}		
		}
	}

	private <T extends RadServiceRequest, V extends RadServiceResponse> void updateSession(T request, V response, String sessionIdValue, 
			int requestType) throws ImproperSearchCriteriaException {
		FieldMapping sessionIdFieldMapping = mandatoryFieldMappingMap.get(SESSION_ID);
		if(sessionIdValue == null){
			LogManager.getLogger().warn(MODULE, this.smInstanceName + "- None of Session Id Attributes [" + sessionIdFieldMapping.getPropertyName() + "] is present in the request so no update will occur.");
			return;
		}
		Session session = this.sessionFactory.getSession();	
		
		Criteria criteria = session.createCriteria(tableName);
		criteria.add(Restrictions.eq(sessionIdFieldMapping.getPropertyName(), sessionIdValue));
		
		sessionDataBuilder.fillSessionSearchCriteria(criteria, request, response);

		SessionData sessionData = sessionFactory.createSessionData(this.tableName);
		sessionData.addValue(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_ACTIVE);
		sessionDataBuilder.setSessionDataForUpdate(sessionData, request, response, requestType);

		String [] groupNameAndServiceType = extractGroupNameAndServiceType(request,response);
		String groupName = groupNameAndServiceType[INDEX_OF_CONCURRENCY_IDENTITY];
		sessionData.addValue(GROUPNAME_FIELD,groupName);
		
		int update = session.update(sessionData, criteria);
		if (update < 0) {
			onSessionUpdationFailure(sessionIdValue);
			dbFailureAction.forAccounting(request, response, smInstanceName);
		}
	}
	
	protected void onSessionCreationFailure(ServerContext serverContext,String sessionIdValue) {
		// No sonar : Methods should not be empty 
	}

	/*
	 * This method is for authentication behavior type of session manager
	 * Step 1 ---- If no session id reference attributes is present in the request then
	 * 			   NO UPDATE will occur.
	 * 
	 * Step 2 ---- Creating and filling the criteria for locating the session based on 
	 * 			   SEARCH columns
	 * 
	 * Step 3 ---- Creating the new session data for updating the session values
	 * 
	 * Step 4 ---- Updating the session
	 */
	private <T extends RadServiceRequest, V extends RadServiceResponse> void handleAuthFlavorAccountingStartRequest(
			T request, V response, RadServiceContext<T, V> serviceContext) throws ImproperSearchCriteriaException {
		//Step 1
		FieldMapping sessionIdFieldMapping = mandatoryFieldMappingMap.get(SESSION_ID);
		String sessionIdValue = sessionDataBuilder.getValueBasedOnFieldMapping(sessionIdFieldMapping, request, response);
		if(sessionIdValue == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, this.smInstanceName + "- None of Session Id Attributes [" + sessionIdFieldMapping.getPropertyName() + "] is present in the request so no update will occur.");
			return;
		}


		//Step 2
		Session session = this.sessionFactory.getSession();
		Criteria criteria = session.createCriteria(tableName);
		sessionDataBuilder.fillSessionSearchCriteria(criteria, request, response);


		//Step 3
		SessionData sessionData = sessionFactory.createSessionData(this.tableName);
		sessionDataBuilder.setSessionDataForUpdate(sessionData, request, response, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);

		String [] groupNameAndServiceType = extractGroupNameAndServiceType(request,response);
		String groupName = groupNameAndServiceType[INDEX_OF_CONCURRENCY_IDENTITY];

		// add the value of groupname in sessiondata 
		sessionData.addValue(GROUPNAME_FIELD, groupName);

		// add the value of service-type in sessiondata

		//add the value of session status to be active
		sessionData.addValue(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_ACTIVE);


		//Step 4
		int result = session.update(sessionData,criteria);
		if(result < 0){
			onSessionUpdationFailure(sessionIdValue);
			dbFailureAction.forAccounting(request, response, smInstanceName);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, smInstanceName + "- Session updation successful."); 
			}

		}
	}

	protected void onSessionUpdationFailure(String sessionIdValue) {
		// No sonar : Methods should not be empty 
	}

	private <T extends RadServiceRequest, V extends RadServiceResponse> void handleAccountingUpdateRequest(
			T request, V response, RadServiceContext<T, V> serviceContext) throws ImproperSearchCriteriaException {

		if(this.localSMBehaviourType == BehaviourType.ACCOUNTING.type){
			handleClassicFlavorAccountingUpdateRequest(request, response,serviceContext);
		}else if(this.localSMBehaviourType == BehaviourType.AUTHENTICATION.type){
			handleAuthFlavorAccountingUpdateRequest(request,response,serviceContext);
		}
	}    

	
	/* This method handles Accounting Update request for the Classic/Accounting behavior type
	 * Step 1 --- If there are no session reference identities in request no update or create will occur
	 * 
	 * Step 2 --- Creating and filling the criteria for locating the session based on 
	 * 			   SEARCH columns
	 * 
	 * Step 3 --- Creating the new session data for updating the session values
	 * 
	 * Step 4 --- DB operation according to sync or async mode
	 * 
	 */
	private <T extends RadServiceRequest, V extends RadServiceResponse> void handleClassicFlavorAccountingUpdateRequest(
			T request, V response, RadServiceContext<T, V> serviceContext) throws ImproperSearchCriteriaException {
		//STEP 1
		FieldMapping sessionIdFieldMapping = mandatoryFieldMappingMap.get(SESSION_ID);
		String sessionIdValue = sessionDataBuilder.getValueBasedOnFieldMapping(sessionIdFieldMapping, request, response);
		if(sessionIdValue == null){
			LogManager.getLogger().warn(MODULE, this.smInstanceName + "- None of Session Id Attributes [" + sessionIdFieldMapping.getPropertyName() + "] is present in the request so no update will occur.");
			return;
		}

		//STEP 2
		Session session = this.sessionFactory.getSession();	
		Criteria criteria = session.createCriteria(tableName);
		criteria.add(Restrictions.eq(mandatoryFieldMappingMap.get(SESSION_ID).getPropertyName(), sessionIdValue));
		sessionDataBuilder.fillSessionSearchCriteria(criteria, request, response);

		//STEP 3
		SessionData sessionData = sessionFactory.createSessionData(this.tableName);


		String [] groupNameAndServiceType = extractGroupNameAndServiceType(request,response);
		String groupName = groupNameAndServiceType[INDEX_OF_CONCURRENCY_IDENTITY];

		// add the value of groupname in sessiondata 
		sessionData.addValue(GROUPNAME_FIELD,groupName);		

		// add the value of service-type in sessiondata
		// STEP 4
		if (sessionManagerConfig.getIsBatchUpdateEnable()) {
			asyncDBOperations(request, response, serviceContext,
					sessionIdValue, session, sessionData, sessionIdFieldMapping);
		} else {
			syncDBOperations(request, response, serviceContext,
					sessionIdValue, session, criteria, sessionData);
		}
		
	}
	
	/*
	 * Step 1 --- fetch existing sessions with criteria 
	 * 
	 * Step 2 --- if the session is found than update it 
	 * 				otherwise create new session 
	 */
	private <V extends RadServiceResponse, T extends RadServiceRequest> void asyncDBOperations(
			T request, V response, RadServiceContext<T, V> serviceContext, String sessionIdValue,
			Session session, SessionData sessionData, FieldMapping sessionIdFieldMapping) throws ImproperSearchCriteriaException {
		
		Criteria criteria = session.createCriteria(this.tableName);
		criteria.add(Restrictions.eq(sessionIdFieldMapping.getColumnName(), sessionIdValue));
		
		int existingSessionsCounts = session.count(criteria);
		
		if (existingSessionsCounts < 0) {
			dbFailureAction.forAccounting(request, response, this.smInstanceName);
		} else if(existingSessionsCounts == 0) {
			sessionDataBuilder.setSessionDataForInsert(sessionData, request, response, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
			sessionData.addValue(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_ACTIVE);
			saveSession(request, response, serviceContext, sessionIdValue, session, sessionData);
		} else {
			sessionDataBuilder.setSessionDataForUpdate(sessionData, request, response, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
			updateSession(request, response, sessionIdValue, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
		}
	}
	
	/*
	 * Step 1 --- Update the session
	 * 
	 * Step 2(OPTIONAL) --- if the session is not updated which means that maybe the
	 * 						Accounting START was missed so try to create the session
	 * 						on Accounting UPDATE request. 
	 */
	private <V extends RadServiceResponse, T extends RadServiceRequest> void syncDBOperations(T request, V response,
			RadServiceContext<T, V> serviceContext, String sessionIdValue,
			Session session, Criteria criteria, SessionData sessionData) {
		//STEP 1
		sessionDataBuilder.setSessionDataForUpdate(sessionData, request, response, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
		int result = session.update(sessionData,criteria);
		
		if (result == SessionResultCode.FAILURE.code) {
			onSessionUpdationFailure(sessionIdValue);
			dbFailureAction.forAccounting(request, response, smInstanceName);
		} else if(result == NO_ROW_UPDATED) {
			LogManager.getLogger().info(MODULE, "Creating session on update");
			//STEP 2
			sessionDataBuilder.setSessionDataForInsert(sessionData, request, response, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
			sessionData.addValue(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_ACTIVE);
			int bResult = session.save(sessionData);
			if(bResult < 0){
				onSessionCreationFailure(serviceContext.getServerContext(), sessionIdValue);
				dbFailureAction.forAccounting(request, response, smInstanceName);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, smInstanceName + "- Session creation successful during update request."); 
				}
			}
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, smInstanceName + "- Session updation successful."); 
			}
		}
	}

	/* This method handles Accounting Update request for the Authentication behavior type
	 * Step 1 --- If there are no session reference identities in request no update or create will occur
	 * 
	 * Step 2 --- Creating and filling the criteria for locating the session based on 
	 * 			   SEARCH columns
	 * 
	 * Step 3 --- Creating the new session data for updating the session values
	 * 
	 * Step 4 --- Update the session
	 * 
	 */
	private <T extends RadServiceRequest, V extends RadServiceResponse> void handleAuthFlavorAccountingUpdateRequest(
			T request, V response, RadServiceContext<T, V> serviceContext) throws ImproperSearchCriteriaException {
		//STEP 1
		FieldMapping sessionIdFieldMapping = mandatoryFieldMappingMap.get(SESSION_ID);
		String sessionIdValue = sessionDataBuilder.getValueBasedOnFieldMapping(sessionIdFieldMapping, request, response);
		if(sessionIdValue == null){
			LogManager.getLogger().warn(MODULE, this.smInstanceName + "- None of Session Id Attributes [" + sessionIdFieldMapping.getPropertyName() + "] is present in the request so no update will occur.");
			return;
		}
		
		
		//STEP 2
		Session session = this.sessionFactory.getSession();	
		Criteria criteria = session.createCriteria(tableName);
		
		criteria.add(Restrictions.eq(mandatoryFieldMappingMap.get(SESSION_ID).getPropertyName(), sessionIdValue));
		sessionDataBuilder.fillSessionSearchCriteria(criteria, request, response);


		//STEP 3
		SessionData sessionData = sessionFactory.createSessionData(this.tableName);
		sessionDataBuilder.setSessionDataForUpdate(sessionData, request, response, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);

		String [] groupNameAndServiceType = extractGroupNameAndServiceType(request,response);

		String groupName = groupNameAndServiceType[INDEX_OF_CONCURRENCY_IDENTITY];

		// add the value of groupname in sessiondata 
		sessionData.addValue(GROUPNAME_FIELD,groupName);
		
		/**
		 *  Reason:
		 *  	in case of accounting start is missed than 
		 *  		accounting-update message should update session-status to active created by 
		 *  		authentication request
		 */
		sessionData.addValue(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_ACTIVE);
		
		// add the value of service-type in sessiondata


		//STEP 4
		int result = session.update(sessionData,criteria);

		if(result < 0){
			onSessionUpdationFailure(sessionIdValue);
			dbFailureAction.forAccounting(request, response, smInstanceName);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, smInstanceName + "- Session updation successful."); 
			}
		}
	}

	private <T extends RadServiceRequest, V extends RadServiceResponse> void handleAccountingStopRequest(
			T request, V response, RadServiceContext<T, V> serviceContext) throws ImproperSearchCriteriaException {

		if(this.localSMBehaviourType == BehaviourType.ACCOUNTING.type){
			handleClassicFlavorAccountingStopRequest(request, response, serviceContext);
		}else if(this.localSMBehaviourType == BehaviourType.AUTHENTICATION.type){
			handleAuthFlavorAccountingStopRequest(request,response,serviceContext);
		}
	}	


	/* This method handles Accounting Stop request for the Classic/Accounting behavior type
	 * Step 1 --- If there are no session reference identities in request no update or create will occur
	 * 
	 * Step 2 --- Creating and filling the criteria for locating the session based on 
	 * 			   SEARCH columns
	 * 
	 * Step 3 --- Delete the session based on criteria
	 * 
	 */
	private <T extends RadServiceRequest, V extends RadServiceResponse> void handleClassicFlavorAccountingStopRequest(
			T request, V response, RadServiceContext<T, V> serviceContext) throws ImproperSearchCriteriaException {
		//STEP 1
		FieldMapping sessionIdFieldMapping = mandatoryFieldMappingMap.get(SESSION_ID);
		String sessionIdValue = sessionDataBuilder.getValueBasedOnFieldMapping(sessionIdFieldMapping, request, response);
		if(sessionIdValue == null){
			LogManager.getLogger().warn(MODULE, this.smInstanceName + "- None of Session Id Attributes [" + sessionIdFieldMapping.getPropertyName() + "] is present in the request so no delete will occur.");
			return;
		}


		//STEP 2
		Session session = this.sessionFactory.getSession();
		Criteria criteria = session.createCriteria(tableName);
		criteria.add(Restrictions.eq(mandatoryFieldMappingMap.get(SESSION_ID).getPropertyName(), sessionIdValue));			
		sessionDataBuilder.fillSessionSearchCriteria(criteria, request, response);


		//STEP 3
		if (LocalSessionManagerData.SESSION_STOP_ACTION_DELETE.equals(actionOnStop)) {
			deleteSesssion(request, response, sessionIdValue, session, criteria);
		} else if (LocalSessionManagerData.SESSION_STOP_ACTION_UPDATE.equals(actionOnStop)) {
			markSessionForReuse(request, response, serviceContext);
		}
	}

	private <V extends RadServiceResponse, T extends RadServiceRequest> void deleteSesssion(
			T request, V response, String sessionIdValue, Session session,
			Criteria criteria) {
		int status = session.delete(criteria);
		if(status < 0){
			onSessionDeletionFailure(sessionIdValue);
			dbFailureAction.forAccounting(request, response, smInstanceName);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, smInstanceName + "- Session deletion successful."); 
			}
		}
	}
	
	private <T extends RadServiceRequest, V extends RadServiceResponse> void markSessionForReuse(
			T request, V response, RadServiceContext<T, V> serviceContext) throws ImproperSearchCriteriaException {
		
		FieldMapping sessionIdFieldMapping = mandatoryFieldMappingMap.get(SESSION_ID);
		String sessionIdValue = sessionDataBuilder.getValueBasedOnFieldMapping(sessionIdFieldMapping, request, response);
		if(sessionIdValue == null){
			LogManager.getLogger().warn(MODULE, this.smInstanceName + "- None of Session Id Attributes [" + sessionIdFieldMapping.getPropertyName() + "] is present in the request so no update will occur.");
			return;
		}

		Session session = this.sessionFactory.getSession();	
		Criteria criteria = session.createCriteria(tableName);
		criteria.add(Restrictions.eq(mandatoryFieldMappingMap.get(SESSION_ID).getPropertyName(), sessionIdValue));
		sessionDataBuilder.fillSessionSearchCriteria(criteria, request, response);

		SessionData sessionData = sessionFactory.createSessionData(this.tableName);
		sessionDataBuilder.setSessionDataForUpdate(sessionData, request, response, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);


		String [] groupNameAndServiceType = extractGroupNameAndServiceType(request,response);
		String groupName = groupNameAndServiceType[INDEX_OF_CONCURRENCY_IDENTITY];

		sessionData.addValue(GROUPNAME_FIELD,groupName);		
		sessionData.addValue(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_DELETED);

		
		int result = session.update(sessionData,criteria);
		if(result < 0){
			onSessionUpdationFailure(sessionIdValue);
			dbFailureAction.forAccounting(request, response, smInstanceName);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, smInstanceName + "- Session updation successful."); 
			}
		}
	}
	
	protected void onSessionDeletionFailure(String sessionIdValue) {
		// No sonar : Methods should not be empty 
	}

	
	/* This method handles Accounting Stop request for the Authentication behavior type
	 * Step 1 --- If there are no session reference identities in request no update or create will occur
	 * 
	 * Step 2 --- Creating and filling the criteria for locating the session based on 
	 * 			   SEARCH columns
	 * 
	 * Step 3 --- Delete the session based on criteria
	 * 
	 */
	private <T extends RadServiceRequest, V extends RadServiceResponse> void handleAuthFlavorAccountingStopRequest(
			T request, V response, RadServiceContext<T, V> serviceContext) throws ImproperSearchCriteriaException {
		//STEP 1
		FieldMapping sessionIdFieldMapping = mandatoryFieldMappingMap.get(SESSION_ID);
		String sessionIdValue = sessionDataBuilder.getValueBasedOnFieldMapping(sessionIdFieldMapping, request, response);
		if(sessionIdValue == null){
			LogManager.getLogger().warn(MODULE, this.smInstanceName + "- None of Session Id Attributes [" + sessionIdFieldMapping.getPropertyName() + "] is present in the request so no delete will occur.");
			return;
		}


		//STEP 2
		Session session = this.sessionFactory.getSession();
		Criteria criteria = session.createCriteria(tableName);
		
		criteria.add(Restrictions.eq(mandatoryFieldMappingMap.get(SESSION_ID).getPropertyName(), sessionIdValue));			
		sessionDataBuilder.fillSessionSearchCriteria(criteria, request, response);
		

		//STEP 3
		if (LocalSessionManagerData.SESSION_STOP_ACTION_DELETE.equals(actionOnStop)) {
			deleteSesssion(request, response, sessionIdValue, session, criteria);
		} else if (LocalSessionManagerData.SESSION_STOP_ACTION_UPDATE.equals(actionOnStop)) {
			markSessionForReuse(request, response, serviceContext);
		}
	}

	/*
	 * This is the method which will handle the Authentication request for Authentication behavior type
	 * 
	 * Step 1 --- Create session data based on the field mappings for creating the session
	 * 
	 * Step 2 --- Fetch and Delete the inactive sessions with the same search criteria
	 * 
	 * Step 3 --- Save the session
	 */
	//FIXME have to look up at commenting the logic for service type that is extracted from class attribute in future
	//FIXME as same attribute has to be configuraed in concurrent logic policy and "PDP Type" of session manager
	public <T extends RadServiceRequest, V extends RadServiceResponse> void handleAuthFlavorAuthenticationRequest(
			T request, V response){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, smInstanceName + "- Creating session for authentication request");
		}
		//STEP 1
		String [] groupNameAndServiceType = extractGroupNameAndServiceType(request,response);

		String groupName = groupNameAndServiceType[INDEX_OF_CONCURRENCY_IDENTITY];

		Session session = this.sessionFactory.getSession();		
		SessionData sessionData = sessionFactory.createSessionData(this.tableName);
		sessionDataBuilder.setSessionDataForInsert(sessionData, request, response, RadiusConstants.ACCESS_REQUEST_MESSAGE);
		// add the value of groupname in sessiondata 
		sessionData.addValue(GROUPNAME_FIELD, groupName);
		
		
		//add the value of the session status
		sessionData.addValue(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_INACTIVE);

		//STEP 2
		try {
		if(fetchAndDeleteInactiveOrDeletedMarkedSessions(request,response) == false){
			dbFailureAction.forAuthentication(request, response, smInstanceName);
			return;
		}
		} catch (ImproperSearchCriteriaException e) {
			response.setFurtherProcessingRequired(false);
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			response.setResponseMessage(AuthReplyMessageConstant.SESSION_MANAGER_OPERATION_FAILED);

			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, this.smInstanceName + "- Sending ACCESS-REJECT, reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
			return;
		}

		//STEP 3
		int result = session.save(sessionData);
		if(result < 0){
			onSessionCreationFailureForAuthBehavior(serverContext);
			dbFailureAction.forAuthentication(request, response, smInstanceName);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, smInstanceName + "- Session creation successful."); 
			}		
		}
	}

	protected void onSessionCreationFailureForAuthBehavior(AAAServerContext serverContext) {
		// No sonar : Methods should not be empty 
	}

	/*
	 * This method does the task of creating the criteria for session lookup and delete 
	 * INACTIVE sessions.
	 * 
	 */
	private boolean fetchAndDeleteInactiveOrDeletedMarkedSessions(RadServiceRequest request,RadServiceResponse response) throws ImproperSearchCriteriaException{
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, smInstanceName + "- Deleting inactive sessions"); 
		}
		Session session = this.sessionFactory.getSession();
		Criteria criteria = session.createCriteria(tableName);

		sessionDataBuilder.fillSessionSearchCriteria(criteria, request, response);

		//adding the criteria to search the inactive sessions and deleted marked session
		criteria.add(Restrictions.or(Restrictions.eq(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_INACTIVE),Restrictions.eq(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_DELETED)));
		int result = session.delete(criteria);
		if(result < 0){
			LogManager.getLogger().error(MODULE, this.smInstanceName + ": Error in deleting inactive sessions");
			return false;
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, smInstanceName + "- Successfully deleted inactive sessions");
			}
			return true;
		}

	}

	public <T extends RadServiceRequest, V extends RadServiceResponse> void handleAuthenticationRequest(
			T request, V response, RadServiceContext<T, V> serviceContext) {

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){

			LogManager.getLogger().debug(MODULE, this.smInstanceName + "- Local Session Management for Authentication Request with behavior type: " + BehaviourType.getName(this.localSMBehaviourType));

		}
		handleClassicFlavorAuthenticationRequest(request, response, serviceContext);
	}

	//This is the restructure of the method for being able to decide criteria to search the sessions will have
	//to make the check for whether the policy type is General or Service wise as the first check
	/*
	 * This method will handle the Authentication request for Classic/Accounting behavior
	 * It does the task of checking the CONCURRENCY.
	 * 
	 * Step 1 --- Skip processing if NO CONCURRENT LOGIN POLICY found in request or response
	 * 
	 * Step 2 --- Skip processing if POLICY DATA is not found
	 * 
	 * Step 3 --- If concurrent login policy is of INDIVIDUAL/GROUP type but there is no
	 * 			  GROUP NAME found for the user then ACCESS REJECT
	 * 			  Similarly if concurrent login policy is SERVICE WISE but the request does not
	 * 			  contain SERVICE TYPE attribute then ACCESS REJECT
	 * 
	 * STEP 4 --- Check for concurrency	
	 * 
	 */
	private <T extends RadServiceRequest, V extends RadServiceResponse> void handleClassicFlavorAuthenticationRequest(
			T radServiceRequest, V radServiceResponse, RadServiceContext<T, V> serviceContext){
		//STEP 1
		IRadiusAttribute concurrentLoginPolicyAttr = findConcurrentLoginPolicyAttribute(radServiceRequest, radServiceResponse);
		if (concurrentLoginPolicyAttr == null) {			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, smInstanceName 
						+ "- No Concurrent Login Policy attribute (21067:114)" 
						+ " found in request or response so skipping further processing.");
			}
			return;
		}

		//STEP 2
		ConcurrentLoginPolicyData policyData = concurrentLoginPolicyConfiguration.getConcurrentLoginPolicy(concurrentLoginPolicyAttr.getStringValue(false));
		if (policyData == null) {
			LogManager.getLogger().debug(MODULE, smInstanceName + "- Configured Concurrent Login Policy: " + concurrentLoginPolicyAttr.getStringValue(false) + " not found.");
			return;
		}

		//STEP 3
		if (isRequestValidForPolicy(policyData, radServiceRequest, radServiceResponse) == false) {
			sendAccessReject(radServiceRequest, radServiceResponse, AuthReplyMessageConstant.CONCURRENCY_FAILED, "Concurrency Failed due to missing attributes.");
			return;
		}

		//STEP 4
		checkConcurrency(policyData,radServiceRequest, radServiceResponse, serviceContext);
	}

	/**
	 * <a href = "https://jira.eliteaaa.com:8443/browse/ELITEAAA-2663">ELITEAAA-2663</a>
	 * <p>
	 * Tries to find concurrent login policy attribute either from request or response
	 */
	private IRadiusAttribute findConcurrentLoginPolicyAttribute(
			RadServiceRequest radServiceRequest,
			RadServiceResponse radServiceResponse) {

		IRadiusAttribute concurrentLoginPolicyAttr = 
			radServiceRequest.getRadiusAttribute(true,
					RadiusConstants.ELITECORE_VENDOR_ID,
					RadiusAttributeConstants.ELITE_CONCURRENT_LOGIN_POLICY_NAME);
		
		if (concurrentLoginPolicyAttr == null) {
			concurrentLoginPolicyAttr = radServiceResponse.getRadiusAttribute(true, 
					RadiusConstants.ELITECORE_VENDOR_ID,
					RadiusAttributeConstants.ELITE_CONCURRENT_LOGIN_POLICY_NAME);
		}
		
		return concurrentLoginPolicyAttr;
	}

	/*
	 * This method checks whether the request contains all the required attributes
	 */
	private boolean isRequestValidForPolicy(ConcurrentLoginPolicyData policyData,RadServiceRequest radServiceRequest, RadServiceResponse radServiceResponse){
		String policyMode = policyData.getPolicyMode();
		String policyType = policyData.getPolicyType();
		String serviceTypeAttribute = policyData.getServiceType();

		if(policyMode.equals(ConcurrentPolicyConstants.GENERAL_POLICY)){
			if(policyType.equals(ConcurrentPolicyConstants.INDIVIDUAL_POLICY)){
				if(sessionDataBuilder.getValueBasedOnFieldMapping(userIdentityFieldMapping, radServiceRequest, radServiceResponse) == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, this.smInstanceName + "- User found in individual policy: " + policyData.getName() + ". The configured user-identity attribute for the session manager: " + this.userIdentityFieldMapping + " is not present in the packet. Sending access-reject.");
					}
					return false;
				}
			}else if(policyType.equals(ConcurrentPolicyConstants.GROUP_POLICY)){
				if(radServiceRequest.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_USER_GROUP) == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, this.smInstanceName + "- User is found in group policy: " + policyData.getName() + " but no group name assigned to user.");
					}
					return false;
				}
			}else{
				// to be not used
			}
		}else if(policyMode.equals(ConcurrentPolicyConstants.SERVICE_WISE_POLICY)){
			if(radServiceRequest.getRadiusAttribute(serviceTypeAttribute,true) == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE,this.smInstanceName + "- The service - type attribute: [" + serviceTypeAttribute + "] confiured in the concurrent policy" + policyData.getName() + " is not present in the request so sending access reject.");
				}
				return false;
			}
			if(policyType.equals(ConcurrentPolicyConstants.INDIVIDUAL_POLICY)){
				if(sessionDataBuilder.getValueBasedOnFieldMapping(userIdentityFieldMapping, radServiceRequest, radServiceResponse) == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, this.smInstanceName + "- The configured user-identity attribute for the session manager is not present in the packet.Sending access-reject.");
					}
					return false;
				}
			}else if(policyType.equals(ConcurrentPolicyConstants.GROUP_POLICY)){
				if(radServiceRequest.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_USER_GROUP) == null){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, this.smInstanceName + "- User is found in group policy: " + policyData.getName() + " but no group name assigned to user.");
					}
					return false;
				}
			}else{return false;}
		}else{return false;}

		return true;
	}

	private <T extends RadServiceRequest, V extends RadServiceResponse> void checkConcurrency(
			ConcurrentLoginPolicyData policyData, T radServiceRequest, V radServiceResponse,
			RadServiceContext<T, V> serviceContext){
		Session session = this.sessionFactory.getSession();
		Criteria criteria = session.createCriteria(this.tableName);
		String policyMode = policyData.getPolicyMode();
		String policyType = policyData.getPolicyType();
		String userIdentity;
		String groupName;

		if(policyMode.equals(ConcurrentPolicyConstants.GENERAL_POLICY)){
			if(policyType.equals(ConcurrentPolicyConstants.INDIVIDUAL_POLICY)){
				
				userIdentity = sessionDataBuilder.getValueBasedOnFieldMapping(userIdentityFieldMapping, radServiceRequest, radServiceResponse);
				
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, this.smInstanceName + "- User : " + userIdentity + " found in INDIVIDUAL policy : " + policyData.getName());
				}
				
				if(policyData.getMaxLogins() != ConcurrentPolicyConstants.UNLIMITED_SESSIONS){
					criteria.add(Restrictions.eq(this.concurrencyIdentityField, userIdentity));
					applyGeneralPolicy(criteria,policyData.getMaxLogins(),radServiceRequest,radServiceResponse,serviceContext);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, this.smInstanceName + "- " + userIdentity + " is found in policy: " + policyData.getName() + " and is allowed unlimited sessions.");
					}
				}
			}else if(policyType.equals(ConcurrentPolicyConstants.GROUP_POLICY)){
				groupName = radServiceRequest.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_USER_GROUP).getStringValue(false);
				
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, this.smInstanceName + "- User found in GROUP policy : " + policyData.getName());
				}
				
				if(policyData.getMaxLogins() != ConcurrentPolicyConstants.UNLIMITED_SESSIONS){
					addClassAttribute(radServiceResponse, ("concr_grpname=" + groupName));
					criteria.add(Restrictions.eq(this.concurrencyIdentityField, groupName));
					applyGeneralPolicy(criteria,policyData.getMaxLogins(),radServiceRequest,radServiceResponse,serviceContext);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, this.smInstanceName + "- " + groupName + " group is allowed unlimited sessions in policy: " + policyData.getName() + ".");
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
				userIdentity = sessionDataBuilder.getValueBasedOnFieldMapping(userIdentityFieldMapping, radServiceRequest, radServiceResponse);
				criteria.add(Restrictions.eq(this.concurrencyIdentityField, userIdentity));
				addClassAttribute(radServiceResponse, ("concr_grpname=" + userIdentity + ";" + "loginpolicy=" + policyData.getName()));

				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, this.smInstanceName + "- User : " + userIdentity + " found in SERVICE WISE policy of INDIVIDUAL type : " + policyData.getName());
				}
				
			}else if(policyType.equals(ConcurrentPolicyConstants.GROUP_POLICY)){
				groupName = radServiceRequest.getRadiusAttribute(true,RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_USER_GROUP).getStringValue(false);
				criteria.add(Restrictions.eq(this.concurrencyIdentityField, groupName));
				addClassAttribute(radServiceResponse, "concr_grpname=" + groupName + ";" + "loginpolicy=" + policyData.getName());
				
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, this.smInstanceName + "- User found in SERVICE WISE policy of GROUP type : " + policyData.getName());
				}
			}

			applyServiceWisePolicy(criteria, radServiceRequest, radServiceResponse, serviceContext, policyData);
		}
	}
	
	private List<SessionData> getSessionDataList(Criteria criteria) {
		Session session = this.sessionFactory.getSession();

		//adding the criteria for looking only sessions that are active
		criteria.add(Restrictions.eq(AAAServerConstants.SESSION_STATUS, AAAServerConstants.SESSION_STATUS_ACTIVE));
		return session.list(criteria);
	}
	
	private <T extends RadServiceRequest, V extends RadServiceResponse> boolean applyServiceWisePolicy(Criteria criteria, T radServiceRequest, V radServiceResponse, 
			RadServiceContext<T,  V> serviceContext, ConcurrentLoginPolicyData policyData){
		
		Long maxLoginSessionsForOtherServices = null;
		List<SessionData> sessionDataList = getSessionDataList(criteria);
		sessionOverride(radServiceRequest, radServiceResponse, sessionDataList, serviceContext);
		
		//condition check if maxLogin limit is Unlimited
		if (policyData.getMaxLogins() != ConcurrentPolicyConstants.UNLIMITED_SESSIONS) {
			if (applyConcurrency(radServiceRequest, radServiceResponse, sessionDataList, policyData.getMaxLogins()) == false) {
				return false;
			}
		} else {
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, this.smInstanceName + "- User is allowed unlimited sessions for Service Type Attribute: " 
					+ " in policy: " + policyData.getName());
			}
		}
		
		List<SessionData> serviceDataList = new ArrayList<SessionData>();

		String serviceTypeValue = radServiceRequest.getRadiusAttribute(policyData.getServiceType(),true).getStringValue(false);
		
		for (int i=0; i<sessionDataList.size(); i++) {
			SessionData sessionData = sessionDataList.get(i);
			if (serviceTypeValue.equals(sessionData.getValue(mandatoryFieldMappingMap.get(PDP_TYPE).getPropertyName()))) {
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
					return applyConcurrency(radServiceRequest, radServiceResponse, serviceDataList, serviceWiseLogin.getMaxServiceWiseSessions());
				} else if (serviceWiseLogin.getServiceTypeValue().equals(String.valueOf(ConcurrentPolicyConstants.OTHERS_SERVICETYPE))) {
					maxLoginSessionsForOtherServices = serviceWiseLogin.getMaxServiceWiseSessions();
				}
			}
		}
		/*
		 * when request false into other category only than below if block will execute
		 */
		if (maxLoginSessionsForOtherServices != null) {
			return applyConcurrency(radServiceRequest, radServiceResponse, serviceDataList, maxLoginSessionsForOtherServices);
		}
		
		return true;
		
	}
	
	private <T extends RadServiceRequest, V extends RadServiceResponse> void sessionOverride(T radServiceRequest, 
			V radServiceResponse, List<SessionData> sessionDataList, RadServiceContext<T, V> serviceContext) {
		
		//condition check for session override is enabled and session found for current request
		if (sessionOverrideColumns.length > 0 && sessionDataList.isEmpty() == false) {
			SessionData overridableSession = getOverridableSession(radServiceRequest, sessionDataList);
			if(overridableSession != null && isAsyncDMorSTOPRequired(sessionOverrideAction)){
				//call for Submission of ASYNC request
				sessionOverrideEventHandler.handleSessionOverrideEvent(overridableSession, serviceContext, 
						radServiceRequest, radServiceResponse);
			}
		}
		
	}
	
	private <T extends RadServiceRequest, V extends RadServiceResponse> boolean applyConcurrency(T radServiceRequest, 
			V radServiceResponse, List<SessionData> sessionDataList,  long maxLogin) {
		
		//condition check for concurrency only
		if (sessionDataList.size() >= maxLogin) {
			sendAccessReject(radServiceRequest, radServiceResponse, AuthReplyMessageConstant.MAX_LOGIN_LIMIT_REACHED,
					"Max Login Limit reached. Max Sessions Allowed: " + maxLogin);
			return false;
		}
		return true;
	}

	/*
	 * This method searches for ACTIVE sessions based on the criteria.
	 * ACTIVE LOGINS = MAX LOGINS then it checks whether the session override action
	 * is specified and does ESI communication to send CoA or STOP.
	 */
	private <T extends RadServiceRequest, V extends RadServiceResponse> boolean applyGeneralPolicy(Criteria criteria, 
			Long maxLoginSessions, T radServiceRequest, V radServiceResponse, RadServiceContext<T, V> serviceContext){
		List<SessionData> sessionDataList = getSessionDataList(criteria);
		sessionOverride(radServiceRequest, radServiceResponse, sessionDataList, serviceContext);
		return applyConcurrency(radServiceRequest, radServiceResponse, sessionDataList, maxLoginSessions);
	}
	
	private <T extends RadServiceRequest> SessionData getOverridableSession(
			T radServiceRequest, List<SessionData> sessionList){
		Map<String,String> columnToAttributeValue = new HashMap<String,String>();
		for(String column : this.sessionOverrideColumns){
			String properties = fieldMappingParser.getPropertiesByColumn(column);
			for(PropertyType propertyType : fieldMappingParser.getPropertyListByColumn(column)){
				IRadiusAttribute radiusAttribute = radServiceRequest.getRadiusAttribute(propertyType.getPropertyName(),true);
				if(radiusAttribute != null){
					columnToAttributeValue.put(properties, radiusAttribute.getStringValue());
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
		SessionData nullSessionData = sessionData;
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
		return nullSessionData;
	}

	private void addClassAttribute(RadServiceResponse radServiceResponse, String classAttributeValue){
		IRadiusAttribute classAttribute = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.CLASS);
		if(classAttribute == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, smInstanceName + "- Class attribute not found in dictionary. Will not be added. Check dictionary.");
			}
			return;
		}
		classAttribute.setStringValue(classAttributeValue);
		radServiceResponse.addAttribute(classAttribute);
	}

	private boolean isAsyncDMorSTOPRequired(int sessionOverrideAction){
		return sessionOverrideAction == SessionClosureAndOverrideActions.DM_ACTION.type || sessionOverrideAction == SessionClosureAndOverrideActions.DM_AND_ACCT_STOP_ACTION.type || sessionOverrideAction == SessionClosureAndOverrideActions.ACCT_STOP_ACTION.type;
	}



	/*
	 * This method handles the ACCOUNTING ON/OFF request of both behavior type
	 * Step 1 --- If there are no session reference identities in request no update or delete will occur
	 * 
	 * Step 2 --- Creating and filling the criteria for locating the session based on 
	 * 			   SEARCH columns
	 * 
	 * Step 3 --- Delete the session based on criteria
	 */
	public <T extends RadServiceRequest, V extends RadServiceResponse> void handleAccountingOnOffRequest(
			T request, V response, RadServiceContext<T, V> serviceContext){
		handleNasRebootRequest(request, response);
	}

	/*
	 * This method handles NAS REBOOT request for both behavior types.
	 * NAS attribute values are based on the preference
	 * 1st preference -- NAS Identifier
	 * 2nd preference -- NAS IP Address 
	 * 3rt preference -- NAS IPv6 Address
	 */
	public void handleNasRebootRequest(RadServiceRequest radServiceRequest, RadServiceResponse radServiceResponse){
		String key;
		String value;
		
		Session session = this.sessionFactory.getSession();
		Criteria criteria = session.createCriteria(tableName);
		IRadiusAttribute nasIdentifierAttr = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER,true);
		if(nasIdentifierAttr != null && fieldMappingParser.getPropertyListByProperties("0:32").isEmpty() == false){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, this.smInstanceName + "- NAS-Identifier(0:32) attribute found in NAS Reboot request. Cleaning sessions based on this attribute.");
			}
			key = "0:32";
			value = nasIdentifierAttr.getStringValue(false);
			criteria.add(Restrictions.eq(key, value));
			deleteSessionBasedOnCriteria(criteria, session);
		}else{
			IRadiusAttribute nasIPAttr = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS,true);
			if(nasIPAttr != null && fieldMappingParser.getPropertyListByProperties("0:4").isEmpty() == false){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, this.smInstanceName + "- NAS-IP-Address(0:4) attribute found in NAS Reboot request. Cleaning sessions based on this attribute.");
				}
				key = "0:4";
				value = nasIPAttr.getStringValue(false);
				criteria.add(Restrictions.eq(key, value));
				deleteSessionBasedOnCriteria(criteria, session);
			}else{
				IRadiusAttribute nasIPv6Attr = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IPV6_ADDRESS,true);
				if(nasIPv6Attr != null && fieldMappingParser.getPropertyListByProperties("0:95").isEmpty() == false){
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, this.smInstanceName + "- NAS-IPv6-Address(0:95) attribute found in NAS Reboot request. Cleaning sessions based on this attribute.");
					}
					key = "0:95";
					value = nasIPv6Attr.getStringValue(false);
					criteria.add(Restrictions.eq(key, value));
					deleteSessionBasedOnCriteria(criteria, session);
				} else {
					dropAccountingRequest(radServiceRequest, radServiceResponse, "Session deletion failed, Reason: NAS-Identifier(0:32), " +
							"NAS-IP-Address(0:4) or NAS-IPv6-Address(0:95), either not present in request or fieldmapping reffering to attribute is missing");
				}
			}			
		}
		
	}
	
	private void deleteSessionBasedOnCriteria(Criteria criteria, Session session){
		int status = session.delete(criteria);
		if(status < 0){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, this.smInstanceName + "- Error in deleting session for NAS reboot request.");
			}
		}
	}
	
	private String[] extractGroupNameAndServiceType(RadServiceRequest radServiceRequest, RadServiceResponse radServiceResponse){
		String[] returnValues = {"default_user",null};
		
		
		String classAttributeValue = fetchClassAttributeValue(radServiceRequest);
		
		if(classAttributeValue == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "No suitable class attribute found in request. Using User-Name (0:1) as group name");
			}
			
			//Using the User Identity if Class attribute is not found
			String userIdentity = sessionDataBuilder.getValueBasedOnFieldMapping(userIdentityFieldMapping, radServiceRequest, radServiceResponse);
			if(userIdentity == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "None of attributes, configured for field mapping USER_IDENTITY, found in request. Using \"default_user\" as default value.");
				}
			}else{
				returnValues[INDEX_OF_CONCURRENCY_IDENTITY] = userIdentity;
			}
		}else{
			int indexOfGroupNameKey = classAttributeValue.indexOf("concr_grpname=");
			int indexOfPolicyNameKey = classAttributeValue.indexOf("loginpolicy=");
			int indexOfSemiColon = classAttributeValue.indexOf(';',indexOfGroupNameKey);
			
			if(indexOfGroupNameKey != -1){
				int indexOfGroupNameValue = indexOfGroupNameKey + "concr_grpname=".length();
				if(indexOfGroupNameValue != classAttributeValue.length()){
					if(indexOfSemiColon != -1){
						returnValues[INDEX_OF_CONCURRENCY_IDENTITY] = classAttributeValue.substring(indexOfGroupNameKey + "concr_grpname=".length(), indexOfSemiColon).trim();

					}else{
						returnValues[INDEX_OF_CONCURRENCY_IDENTITY] = classAttributeValue.substring(indexOfGroupNameKey + "concr_grpname=".length()).trim();
					}
				}
			}
			indexOfSemiColon = classAttributeValue.indexOf(';',indexOfPolicyNameKey);
			if(indexOfPolicyNameKey != -1){
				int indexOfPolicyNameValue = indexOfPolicyNameKey + "loginpolicy=".length();
				if(indexOfPolicyNameValue != classAttributeValue.length()){
					if(indexOfSemiColon != -1){
						String concLoginPolicyName = classAttributeValue.substring(indexOfPolicyNameKey + "loginpolicy=".length(),indexOfSemiColon).trim();
						returnValues[INDEX_OF_SERVICE_TYPE] = extractServiceType(concLoginPolicyName, radServiceRequest);
					}else{
						String concLoginPolicyName = classAttributeValue.substring(indexOfPolicyNameKey + "loginpolicy=".length()).trim();
						returnValues[INDEX_OF_SERVICE_TYPE] = extractServiceType(concLoginPolicyName, radServiceRequest);
					}
				}
			}
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Using value of group name : " + returnValues[INDEX_OF_CONCURRENCY_IDENTITY] + ", value of service type as : " + returnValues[INDEX_OF_SERVICE_TYPE]);
		}
		return returnValues;
	}

	private String fetchClassAttributeValue(RadServiceRequest radServiceRequest){
		String classAttributeValue = null;
		Collection<IRadiusAttribute> classAttributes = radServiceRequest.getRadiusAttributes(RadiusAttributeConstants.CLASS,true);
		
		if(classAttributes == null || classAttributes.isEmpty()){
			return classAttributeValue;
		}
		
		for(IRadiusAttribute classAttribute : classAttributes){
			String tempClassAttributeValue = classAttribute.getStringValue(false);
			int indexOfGroupNameKey = tempClassAttributeValue.indexOf("concr_grpname=");
			if(indexOfGroupNameKey != -1){
				classAttributeValue = tempClassAttributeValue;
				break;
			}
		}
		
		return classAttributeValue;
	}
	
	private String extractServiceType(String policyName, RadServiceRequest radServiceRequest){
		ConcurrentLoginPolicyData policyData = this.concurrentLoginPolicyConfiguration.getConcurrentLoginPolicy(policyName);
		if(policyData == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "No concurrent login policy found with name: " + policyName + ". Using value of Nas-Port-Type(0:5) as default value.");
					}
				}else{
			if(!policyData.getPolicyMode().equals(ConcurrentPolicyConstants.SERVICE_WISE_POLICY)){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Concurrent login policy: " + policyName + " in class attribute is not Service Wise");
					}
				return null;
			}
			
			String serviceTypeAttributeID = policyData.getServiceType();
			IRadiusAttribute serviceTypeAttribute = radServiceRequest.getRadiusAttribute(serviceTypeAttributeID,true);
			if(serviceTypeAttribute != null){
				return serviceTypeAttribute.getStringValue(false);
					}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Service Type attribute : " + serviceTypeAttributeID + " in concurrent login policy: " + policyName + " not found in request. Using value of Nas-Port-Type(0:5) as default value");
						}
					}
				}

		IRadiusAttribute nasPortType = radServiceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT_TYPE,true);
		if(nasPortType != null){
			return nasPortType.getStringValue(false);
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Nas-Port-Type(0:5) attribute not found in request");
			}			
			return null;
				}				
			}

	protected void sendAccessReject(RadServiceRequest request,RadServiceResponse response,String responseMessage, String logMessage){
		response.setFurtherProcessingRequired(false);
		response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
		response.setResponseMessage(responseMessage);
		
		if(logMessage != null && LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, this.smInstanceName + "- Sending ACCESS-REJECT reason: " + logMessage);
	}
	}

	private void dropAccountingRequest(RadServiceRequest request, RadServiceResponse response, String logMessage) {
		request.stopFurtherExecution();
		response.setFurtherProcessingRequired(false);
		response.markForDropRequest();
		
		if(logMessage != null && LogManager.getLogger().isLogLevel(LogLevel.WARN)){
			LogManager.getLogger().warn(MODULE, this.smInstanceName + "- Droping ACCOUNTING-REQUEST, Reason: " + logMessage);
		}
	}

	public String getSmInstanceId() {
		return this.smInstanceId;
	}

	public String getSmInstanceName() {
		return this.smInstanceName;
	}

	public String getSMType() {
		return this.smType;
	}

	public int flushAllSessions() {
		if(this.sessionFactory.getSession() == null)
			return -1;
		return this.sessionFactory.getSession().truncate(tableName);
	}

	public int getBehaviorType(){
		return this.localSMBehaviourType;
	}
	
	private UDPCommunicator findOrCreateDynamicNASCommunicator(SessionData sessionData) {
		UDPCommunicator communicator = null;
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Communicating via dynamic NAS");
		}
		
		String probableNasIPAddress = sessionData.getValue(mandatoryFieldMappingMap.get(NAS_ID).getPropertyName());
		
		if (probableNasIPAddress == null) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "NAS IP Address not found for dynamic Nas communication");
			}
			onDMRequestDropped(sessionData);
			return communicator;
		}
		
		communicator = createUdpCommunicatorFor(probableNasIPAddress);
		
		if (communicator == null) {
			onDMRequestDropped(sessionData);
		}

		return communicator;
	}
	
	// class for handling session closure action
	private class SessionCloseEventHandler {
		private SessionData sessionData;

		public SessionCloseEventHandler(SessionData sessionData) {			
			this.sessionData = sessionData;				
		}

		public void handleSessionCloseAction(){			
			RadiusPacket radiusPacket = new RadiusPacket();
			radiusPacket.setIdentifier(RadiusUtility.generateIdentifier());	
			radiusPacket.setLength(RadiusPacket.DEFAULT_RADIUS_PACKET_LENGTH);			


			sessionDataBuilder.prepareRadiusPacket(radiusPacket, sessionData);


			if (sessionCloseAction == SessionClosureAndOverrideActions.DM_ACTION.type || sessionCloseAction == SessionClosureAndOverrideActions.DM_AND_ACCT_STOP_ACTION.type){				
				// code for disconnect			
				
				
				IRadiusAttribute radiusAttribute = radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_VENDOR_TYPE);
				if(radiusAttribute != null && radiusAttribute.getIntValue()==ClientTypeConstant.WAG.typeId){
					radiusPacket.setPacketType(RadiusConstants.COA_REQUEST_MESSAGE);
				}else{
					radiusPacket.setPacketType(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE);	
				}
				
													
				LogManager.getLogger().info(MODULE, radiusPacket.toString());
				radiusPacket.refreshPacketHeader();

				/*
				 * Here group is alive is checked because internally it check for the size of the UDP communicator list.
				 * So, it will be true only when any communicator is added to the group. Now communicator will be added
				 * to the group only when any NAS type ESI is binded in concurrency session manager configuration.
				 * If any communicator is not bound to it then group will be not alive.
				 * 
				 * So, when group is not alive, we will dynamically create the NAS, the same way Dynamic NAS Communicator
				 * does for Radius Dynauth policy. Here, the NAS IP Address will be get from the mandatory mapping done 
				 * in the session manager configuration.
				 */
				if (udpCommunicationGrp.isAlive() == true) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Sending DM to configured NAS");
					}
					udpCommunicationGrp.handleRequest(radiusPacket.getBytes(),
							NO_SHARED_SECRET, 
							createDMResponseListener(sessionData), HazelcastRadiusSession.RAD_NO_SESSION);
				} else {
					UDPCommunicator communicator = findOrCreateDynamicNASCommunicator(sessionData);
					RadUDPCommGroup localGroup = new RadUDPCommGroupImpl(serverContext);
					localGroup.addCommunicator(communicator);
					localGroup.handleRequest(radiusPacket.getBytes(),
							NO_SHARED_SECRET,
							createDMResponseListener(sessionData), HazelcastRadiusSession.RAD_NO_SESSION);
				}

			}else if(sessionCloseAction == SessionClosureAndOverrideActions.ACCT_STOP_ACTION.type){
				radiusPacket.setPacketType(RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
				upgradeAccountStatusTypeToStop(radiusPacket);
				LogManager.getLogger().info(MODULE, radiusPacket.toString());
				udpCommAcctStopGrp.handleRequest(radiusPacket.getBytes(),
						NO_SHARED_SECRET, 
						createAccountingStopResponseListener(sessionData), HazelcastRadiusSession.RAD_NO_SESSION);			
			}else if(sessionCloseAction == SessionClosureAndOverrideActions.NONE.type){
				// code for deleting session
				serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(this.sessionData));				
			}			

		}
	}	

	private UDPCommunicator createUdpCommunicatorFor(String probableIpAddress) {

		UDPCommunicator communicator = null;
		
		RadClientData clientData = serverContext.getServerConfiguration()
												.getRadClientConfiguration()
												.getClientData(probableIpAddress);
		
		if (clientData == null) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Dynamic NAS Communication Failed, " +
						"Reason: Unknown client with IP Address: " + probableIpAddress);
			}
			return communicator;
		}
		
		if (clientData.getDynauthPort().isPresent() == false) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Dynamic NAS Communication failed, "
						+ "Reason: No Dynamic Authorization port configured for client IP: "
						+ probableIpAddress + " in client profile: " 
						+ clientData.getProfileName());
			}
			return communicator;
		}
		
		URLData url = new URLData();
		url.setHost(probableIpAddress);
		url.setPort(clientData.getDynauthPort().get());

		DynamicNasExternalSystemData radiusExternalSystemData = null;
		
		try {
			radiusExternalSystemData =  DynamicNasExternalSystemData.
					create(url, clientData.getTimeout(), clientData.getSharedSecret(RadiusConstants.COA_REQUEST_MESSAGE),  
							clientData.getSupportedAttributesStrCOA(), clientData.getUnsupportedAttributesStrCOA(), 
							clientData.getSupportedAttributesStrDM(), clientData.getUnsupportedAttributesStrDM());


			communicator = serverContext.getRadUDPCommunicatorManager()
					.findCommunicatorByURLOrCreate(url, 
							serverContext, 
							radiusExternalSystemData);
			
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Failed to create Dynamic NAS: " + radiusExternalSystemData.getName() + 
					" , Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);

		} catch (UnknownHostException e) {
			// will not occur. If the received IP address is unknown then it would have been detected in AddressAttribute itself
			LogManager.getLogger().trace(MODULE, e);
		}		
		
		return communicator;
	}

	protected RadResponseListener createDMResponseListener(SessionData sessionData){
		return new RadResponseListnerImplForDM(sessionData);
	}
	
	protected RadResponseListener createAccountingStopResponseListener(SessionData sessionData) {
		return new RadResponseImplForAcctStop(sessionData);
	}
	

	// class for deleting sessions from table 
	private class ConcurrencySessionCleanupTaskExecutor extends BaseSingleExecutionAsyncTask{

		private SessionData sessionData ;

		public ConcurrencySessionCleanupTaskExecutor(SessionData sessionData) {
			this.sessionData = sessionData;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			LogManager.getLogger().info(MODULE, "Clearing session: " + sessionData);
			Session session = sessionFactory.getSession();
			int result = session.delete(sessionData);
			if ( result < 0) {
				LogManager.getLogger().info(MODULE, "Delete operation failed for session " + sessionData);
			} else {
				LogManager.getLogger().info(MODULE, "Number of deleted sessions are " + result);
			}
		}
	}


	private void onDMRequestDropped(SessionData sessionData) {
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Unable to forward DM request to target system. Reason : request dropped");	
		}
		serverContext.generateSystemAlert(AlertSeverity.INFO,Alerts.RM_SESSION_GENERIC, 
				MODULE, "Disconnect Request dropped. Reason: No alive ESI found", 0,
				"Disconnect Request dropped. Reason: No alive ESI found");
		//if the action is DM and STOP then only to send ACCT STOP
		if (sessionCloseAction == SessionClosureAndOverrideActions.DM_ACTION.type) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, smInstanceName + "- Session Close Action is DM and request dropped, so deleting session locally.");
			}
			serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));
			return;
		}
		
		if (udpCommAcctStopGrp == null) {
			LogManager.getLogger().info(MODULE, "No esi added for accounting stop related implementation.");
			LogManager.getLogger().info(MODULE, "Deleting session locally.");
			serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));
			return;
		}
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Sending accounting stop to the configured servers.");
		}

		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.setIdentifier(RadiusUtility.generateIdentifier());	
		radiusPacket.setLength(RadiusPacket.DEFAULT_RADIUS_PACKET_LENGTH);			

		sessionDataBuilder.prepareRadiusPacket(radiusPacket, sessionData);
		radiusPacket.setPacketType(RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
		upgradeAccountStatusTypeToStop(radiusPacket);
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, radiusPacket.toString());
		}

		udpCommAcctStopGrp.handleRequest(radiusPacket.getBytes(),
				NO_SHARED_SECRET, createAccountingStopResponseListener(sessionData), HazelcastRadiusSession.RAD_NO_SESSION);
	}
	
	// response listener classes for both disconnect message and accounting stop 

	class RadResponseListnerImplForDM implements RadResponseListener{
		SessionData sessionData;

		public RadResponseListnerImplForDM(SessionData sessionData) {
			this.sessionData = sessionData;
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			onDMRequestDropped(sessionData);
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse, ISession session) {

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Response received from Proxy Server: " + radUDPResponse);

			if(radUDPResponse == null || radUDPResponse.getRadiusPacket() == null){
				LogManager.getLogger().info(MODULE,"Radius Packet is empty with no attributes present.");
				return;
			}

			if(radUDPResponse.getRadiusPacket().getPacketType() == RadiusConstants.DISCONNECTION_ACK_MESSAGE){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "ACK is received from NAS,so no further processing is required.");
				}
				return;
			}				

			LogManager.getLogger().debug(MODULE, "NAK received from NAS");

			if (sessionCloseAction == SessionClosureAndOverrideActions.DM_ACTION.type) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Skipping the Accounting request part, Reason: The selected session close action is: " + SessionClosureAndOverrideActions.get(sessionCloseAction));
				}
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Deleting sessions locally.");
				}
				serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));
				return;
			}
			
			if (udpCommAcctStopGrp == null) {
				LogManager.getLogger().info(MODULE, "No accounting servers added.");
				LogManager.getLogger().info(MODULE, "Deleting sessions locally.");
				serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));
				return;
			}										
			LogManager.getLogger().info(MODULE, "Sending accounting stop to the configured servers.");

			RadiusPacket radiusPacket = new RadiusPacket();
			radiusPacket.setIdentifier(RadiusUtility.generateIdentifier());	
			radiusPacket.setLength(RadiusPacket.DEFAULT_RADIUS_PACKET_LENGTH);			

			sessionDataBuilder.prepareRadiusPacket(radiusPacket, sessionData);
			radiusPacket.setPacketType(RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
			upgradeAccountStatusTypeToStop(radiusPacket);
			LogManager.getLogger().info(MODULE, radiusPacket.toString());

			udpCommAcctStopGrp.handleRequest(radiusPacket.getBytes(),
					NO_SHARED_SECRET,
					createAccountingStopResponseListener(sessionData), HazelcastRadiusSession.RAD_NO_SESSION);													

		}
		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Request Timeout Response Received");

			if (sessionCloseAction == SessionClosureAndOverrideActions.DM_ACTION.type) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Skipping the Accounting request part, Reason: The selected session close action is: " + SessionClosureAndOverrideActions.get(sessionCloseAction));
				}
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Deleting sessions locally.");
				}
				serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));
				return;
			}
			
			if(udpCommAcctStopGrp == null){
				LogManager.getLogger().info(MODULE, "No accounting servers added.");
				LogManager.getLogger().info(MODULE, "Deleting sessions locally.");
				serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));
				return;
			}

			LogManager.getLogger().info(MODULE, "Sending accounting stop to the configured servers.");

			RadiusPacket radiusPacket = new RadiusPacket();
			radiusPacket.setIdentifier(RadiusUtility.generateIdentifier());	
			radiusPacket.setLength(RadiusPacket.DEFAULT_RADIUS_PACKET_LENGTH);			

			sessionDataBuilder.prepareRadiusPacket(radiusPacket, sessionData);
			radiusPacket.setPacketType(RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
			upgradeAccountStatusTypeToStop(radiusPacket);
			LogManager.getLogger().info(MODULE, radiusPacket.toString());					

			udpCommAcctStopGrp.handleRequest(radiusPacket.getBytes(),
					NO_SHARED_SECRET,
					createAccountingStopResponseListener(sessionData), HazelcastRadiusSession.RAD_NO_SESSION);																				
		}

	}

	class RadResponseImplForAcctStop implements RadResponseListener{

		SessionData sessionData;

		public RadResponseImplForAcctStop(SessionData sessionData) {
			this.sessionData = sessionData;
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Unable to forward request to target system. Reason : request dropped.");
				LogManager.getLogger().info(MODULE, "Deleting the data locally");
			}
			serverContext.generateSystemAlert(AlertSeverity.INFO , Alerts.RM_SESSION_GENERIC, 
					MODULE, "Accounting request dropped. Reason: No alive ESI found", 0,
					"Accounting request dropped. Reason: No alive ESI found");
			serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(this.sessionData));
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse, ISession session) {

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Response received from Proxy Server: " + radUDPResponse);									
		}
		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Request Timeout Response Received for Accounting Stop ,deleting the session locally.");
			serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));																				
		}
	}

	private RadiusPacket prepareExternalPacket(SessionData sessionData,int packetType){
		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.setIdentifier(RadiusUtility.generateIdentifier());	
		radiusPacket.setLength(RadiusPacket.DEFAULT_RADIUS_PACKET_LENGTH);			
		sessionDataBuilder.prepareRadiusPacket(radiusPacket, sessionData);
		radiusPacket.setPacketType(packetType);
		radiusPacket.refreshPacketHeader();
		return radiusPacket;
	}

	class SessionOverrideEventHandler{

		<T extends RadServiceRequest, V extends RadServiceResponse> void handleSessionOverrideEvent(
				SessionData sessionData,
				RadServiceContext<T, V> serviceContext, 
				T originalRadServiceRequest,
				V originalRadServiceResponse){
			if(sessionOverrideAction == SessionClosureAndOverrideActions.DM_ACTION.type || sessionOverrideAction == SessionClosureAndOverrideActions.DM_AND_ACCT_STOP_ACTION.type){
				onSessionOverrideEventForDM(sessionData);
			}else if(sessionOverrideAction == SessionClosureAndOverrideActions.ACCT_STOP_ACTION.type){
				onSessionOverrideEventForAcctStop(sessionData);
			}
		}
	}

	protected <T extends RadServiceRequest, V extends RadServiceResponse> void onSessionOverrideEventForDM(
			SessionData sessionData) {

		RadiusPacket radiusPacket;
		radiusPacket = prepareExternalPacket(sessionData,RadiusConstants.DISCONNECTION_REQUEST_MESSAGE);

		if (udpCommunicationGrp.isAlive()) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending DM to configured NAS");
			}
			
			udpCommunicationGrp.handleRequest(radiusPacket.getBytes(),
					NO_SHARED_SECRET,
					createSessionOverrideResponseListenerForForDM(sessionData), HazelcastRadiusSession.RAD_NO_SESSION);
		} else {
			UDPCommunicator communicator = findOrCreateDynamicNASCommunicator(sessionData);
			RadUDPCommGroup localGroup = new RadUDPCommGroupImpl(serverContext);
			localGroup.addCommunicator(communicator);
			localGroup.handleRequest(radiusPacket.getBytes(),
					NO_SHARED_SECRET,
					createSessionOverrideResponseListenerForForDM(sessionData), HazelcastRadiusSession.RAD_NO_SESSION);
		}
	}

	protected <T extends RadServiceRequest, V extends RadServiceResponse> void onSessionOverrideEventForAcctStop(SessionData sessionData) {
		RadiusPacket radiusPacket;
		radiusPacket = prepareExternalPacket(sessionData, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
		upgradeAccountStatusTypeToStop(radiusPacket);			
		udpCommAcctStopGrp.handleRequest(radiusPacket.getBytes(),
				NO_SHARED_SECRET,
				createSessionOverrideResponseListenerForAccountingStop(
				sessionData), HazelcastRadiusSession.RAD_NO_SESSION);
	}

	protected <T extends RadServiceRequest, V extends RadServiceResponse> RadResponseListener
	createSessionOverrideResponseListenerForAccountingStop(
			SessionData sessionData) {
		return new SessionOverrideResponseListenerForAcctStop<T, V>(sessionData);
	}
	
	protected <T extends RadServiceRequest, V extends RadServiceResponse> RadResponseListener
	createSessionOverrideResponseListenerForForDM(
			SessionData sessionData) {
		return new SessionOverrideResponseListenerForDM<T, V>(sessionData);
	}

	/* Listener which will listen for the DM or STOP response from ESI in case when 
	 * session override is configured.
	 * BEHAVIOR:
	 * DM Response received:
	 * 		a) DM ACK --- ACCESS-REJECT
	 * 
	 * 		b) DM NAK
	 * 			A) ACTION -- DM & STOP -- generate STOP [which will eventually delete session ]
	 * 			B) ACTION -- DM		   -- LOCALLY delete session and ACCEPT
	 * 
	 * 		c) DM Dropped/Timeout
	 * 			A) ACTION -- DM & STOP -- generate STOP [which will eventually delete session ]
	 * 			B) ACTION -- DM		   -- LOCALLY delete session and ACCEPT
	 */
	class SessionOverrideResponseListenerForDM<T extends RadServiceRequest, V extends RadServiceResponse> implements RadResponseListener {
		final SessionData sessionData;

		public SessionOverrideResponseListenerForDM(SessionData sessionData) {
			this.sessionData = sessionData;
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,
				RadUDPResponse radUDPResponseFromDMEsi, ISession session) {
			//Here will have to take decision of whether to send to accounting external systems
			if (radUDPResponseFromDMEsi.getRadiusPacket().getPacketType() == RadiusConstants.DISCONNECTION_ACK_MESSAGE) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, smInstanceName + "- DM ACK received from external system, so session will not be deleted locally.");
				}
				return;
			}

			//received NAK from External System
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, smInstanceName + "- NAK received from external system.");
			}

			IRadiusAttribute errorCause = radUDPResponseFromDMEsi.getRadiusPacket().getRadiusAttribute(RadiusAttributeConstants.ERROR_CAUSE,true);
			if(errorCause == null){
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Error cause attribute(0:101) not received in DM NAK, so session will not be deleted locally.");
				}
				return;
			}

			if (RadiusAttributeValuesConstants.SESSION_CONTEXT_NOT_FOUND != errorCause.getIntValue()) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Error cause other than " + RadiusAttributeValuesConstants.SESSION_CONTEXT_NOT_FOUND_STR
							+ " received in DM NAK , so session will not be deleted locally.");
				}
				return;
			}

			if(sessionOverrideAction == SessionClosureAndOverrideActions.DM_AND_ACCT_STOP_ACTION.type){
				if(udpCommAcctStopGrp != null){
					RadiusPacket radiusPacket = prepareExternalPacket(sessionData, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
						upgradeAccountStatusTypeToStop(radiusPacket);			
					LogManager.getLogger().info(MODULE, radiusPacket.toString());
					udpCommAcctStopGrp.handleRequest(radiusPacket.getBytes(),
							NO_SHARED_SECRET,
							createAccountingStopResponseListener(sessionData), HazelcastRadiusSession.RAD_NO_SESSION);
				}else{
					//Override action is DM & STOP but no stop esi so delete session locally.
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE,smInstanceName + "- Session override action is DM_AND_ACCT_STOP but no Acct Stop ESI found. So deleting session locally.");
					}
					serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));
				}
			}else{
				//Override action is DM and session not found at NAS which means that the session is actually stale
				//so delete the session locally
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().debug(MODULE, smInstanceName + "- Session override action is DM and NAK received from NAS, so deleting session locally.");
				}
				serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));
			}
		}

		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Unable to forward DM request to target system");
			
			serverContext.generateSystemAlert(AlertSeverity.INFO,Alerts.RM_SESSION_GENERIC, 
					MODULE, "Disconnect Request dropped. Reason: No alive ESI found", 0,
					"Disconnect Request dropped. Reason: No alive ESI found");

			if(sessionOverrideAction == SessionClosureAndOverrideActions.DM_AND_ACCT_STOP_ACTION.type){
				if(udpCommAcctStopGrp != null){
					RadiusPacket radiusPacket = prepareExternalPacket(sessionData, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
					upgradeAccountStatusTypeToStop(radiusPacket);	
					LogManager.getLogger().info(MODULE, radiusPacket.toString());
					udpCommAcctStopGrp.handleRequest(radiusPacket.getBytes(),
							NO_SHARED_SECRET, 
							createAccountingStopResponseListener(sessionData), HazelcastRadiusSession.RAD_NO_SESSION);
				}else{
					//Override action is DM & STOP but no stop esi so delete session locally.
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE,smInstanceName + "- Session override action is DM_AND_ACCT_STOP but no Acct Stop ESI found. So deleting session locally.");
					}
					serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));
				}
			} else{
				//Override action is DM and request dropped so deleting sessions
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, smInstanceName + "- Session override action is DM and request to NAS dropped, so session will not be deleted locally.");
				}
			}
		}

		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Request Timeout Response Received for DM");

			if(sessionOverrideAction == SessionClosureAndOverrideActions.DM_AND_ACCT_STOP_ACTION.type){
				if(udpCommAcctStopGrp != null){
					RadiusPacket radiusPacket = prepareExternalPacket(sessionData, RadiusConstants.ACCOUNTING_REQUEST_MESSAGE);
					upgradeAccountStatusTypeToStop(radiusPacket);			
					LogManager.getLogger().info(MODULE, radiusPacket.toString());
					udpCommAcctStopGrp.handleRequest(radiusPacket.getBytes(),
							NO_SHARED_SECRET ,
							createAccountingStopResponseListener(sessionData), HazelcastRadiusSession.RAD_NO_SESSION);
				}else{
					//Override action is DM & STOP but no stop esi so delete session locally.
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE,smInstanceName + "- Session override action is DM_AND_ACCT_STOP but no Acct Stop ESI found. So deleting session locally.");
					}
					serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));
				}
			}else{
				//Override action is DM and request dropped so removing session locally
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, smInstanceName + "- Session override action is DM and request to NAS timedout, so session will not be deleted locally.");
				}
				serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));
			}
		}
	}

	
	/*
	 * This Listener is for ACCT-STOP as SESSION-OVERRIDE-ACTION & has following behavior:
	 * Response Received 		--- Dont delete session locally and positive FUTHER processing
	 * Response Timeout/Dropped --- Delete sessions LOCALLY and positive FURTHER processing
	 */
	class SessionOverrideResponseListenerForAcctStop<T extends RadServiceRequest, V extends RadServiceResponse> implements RadResponseListener{
		
		private SessionData sessionData;
		
		public SessionOverrideResponseListenerForAcctStop(
				SessionData sessionData) {
			this.sessionData = sessionData;
		}
		
		@Override
		public void requestDropped(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Unable to forward request to target system. Reason : request dropped.");
				LogManager.getLogger().info(MODULE, "Deleting the data locally");
			}
			
			serverContext.generateSystemAlert(AlertSeverity.INFO , Alerts.RM_SESSION_GENERIC,
					MODULE, "Accounting request dropped. Reason: No alive ESI found. So deleting session locally.", 0,
					"Accounting request dropped. Reason: No alive ESI found. So deleting session locally.");
			serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(this.sessionData));
		}

		@Override
		public void responseReceived(RadUDPRequest radUDPRequest,RadUDPResponse radUDPResponse, ISession session) {

			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Response received from Proxy Server: " + radUDPResponse);
			
		}
		
		@Override
		public void requestTimeout(RadUDPRequest radUDPRequest) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Request Timeout Response Received for Accounting Stop ,deleting the session locally.");

			serverContext.getTaskScheduler().scheduleSingleExecutionTask(new ConcurrencySessionCleanupTaskExecutor(sessionData));											
		}
		
	}
		@Override
	public void reInit() throws InitializationFailedException {
		sessionManagerConfig = (LocalSessionManagerData) serverContext.getServerConfiguration().getSessionManagerConfiguration().getSessionManagerConfigById(smInstanceId);
	}


	protected final SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	protected final String getTableName() {
		return tableName;
	}

	/**
	 * return the true or false based on the DB connection.
	 * true if DB is alive, false otherwise. 
	 * @return true if it is alive.
	 */
	public boolean isAlive() {
		return sessionFactory == null ? false : sessionFactory.isAlive();
	}
	
	private static void upgradeAccountStatusTypeToStop(RadiusPacket radiusPacket){
		IRadiusAttribute acctStatusType = radiusPacket.getRadiusAttribute(
				RadiusAttributeConstants.ACCT_STATUS_TYPE);
		if (acctStatusType == null){
			acctStatusType = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.ACCT_STATUS_TYPE);
			radiusPacket.addAttribute(acctStatusType);
		}
		acctStatusType.setIntValue(RadiusAttributeValuesConstants.STOP);
		radiusPacket.refreshPacketHeader();	
	}
}