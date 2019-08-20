package com.elitecore.aaa.diameter.sessionmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.core.conf.impl.MiscellaneousConfigurable;
import com.elitecore.aaa.core.plugins.transactionlogger.KeywordValueProvider;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.sessionmanager.BatchParameters;
import com.elitecore.aaa.diameter.conf.sessionmanager.DiameterSessionManagerConfigurable;
import com.elitecore.aaa.diameter.conf.sessionmanager.FieldMappingImpl;
import com.elitecore.aaa.diameter.conf.sessionmanager.OperationOverrideDetail;
import com.elitecore.aaa.diameter.conf.sessionmanager.SessionDataMapping;
import com.elitecore.aaa.diameter.conf.sessionmanager.SessionScenarioDetails;
import com.elitecore.aaa.radius.sessionx.data.FieldMappingParser;
import com.elitecore.aaa.radius.sessionx.data.ImproperSearchCriteriaException;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.aaa.util.constants.AAAServerConstants.ProtocolType;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.exprlib.AttributeValueProvider;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionFactory;
import com.elitecore.core.serverx.sessionx.SystemPropertiesProvider;
import com.elitecore.core.serverx.sessionx.conf.SessionConfiguration;
import com.elitecore.core.serverx.sessionx.conf.impl.SessionConfigurationImpl;
import com.elitecore.core.serverx.sessionx.impl.SchemaMappingImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

/**
 * 
 * @author malav.desai
 */
public class DiameterSessionManager implements IDiameterSessionManager {
	
	private static final String MODULE = "DIAMETER-SESSION-MANAGER";
	private static final int OPERATION_FAILURE = -1;
	private static final String HBH_AND_ETE_FIELD_SEPARATOR = ":";
	private static final FieldMappingImpl SESSION_STATUS_MAPPING = new FieldMappingImpl("SESSION_STATUS", 
				DiameterAVPConstants.EC_SESSION_STATUS, FieldMapping.STRING_TYPE, AAAServerConstants.SESSION_STATUS_ACTIVE);
	private static final FieldMappingImpl PROTOCOL_TYPE_MAPPING = new FieldMappingImpl(AAAServerConstants.PROTOCOL_TYPE_FIELD, 
			AAAServerConstants.PROTOCOL_TYPE_FIELD, FieldMapping.STRING_TYPE, ProtocolType.DIAMETER.name());
	
	private final List<SessionScenario> scenarios;
	private final Map<String, SessionScenario> identifierToSessionScenarioMap;
	
	private List<DBOperationAndExpressionPair> operationActionDetails;

	private final AAAServerContext serverContext;
	private final DiameterSessionManagerConfigurable sessionManagerConfigurable;
	private String dbDataSourceName;
	
	public DiameterSessionManager(AAAServerContext serverContext, DiameterSessionManagerConfigurable sessionManagerConfigurable) {
		this.serverContext = serverContext;
		this.sessionManagerConfigurable = sessionManagerConfigurable;
		this.scenarios = new ArrayList<SessionScenario>();
		this.identifierToSessionScenarioMap = new LinkedHashMap<String, SessionScenario>();
	}
	
	@Override
	public void init() throws InitializationFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE,"Initializing Diameter Session Manager");
		}
		operationActionDetails = new ArrayList<DBOperationAndExpressionPair>();
		for (OperationOverrideDetail detail : sessionManagerConfigurable.getOperationOverrideDetails()) {
			operationActionDetails.add(new DBOperationAndExpressionPair(detail.getName(), detail.getExpression(), detail.getOverrideAction()));
		}
		
		SessionConfigurationImpl sessionConfig = createSessionConfiguration();
		SystemPropertiesProvider systemPropertiesProvider = new SystemPropertiesProviderImpl();
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "SQL Batch update is " + systemPropertiesProvider.isBatchEnabled() 
					+ " and SQL No-wait is " + systemPropertiesProvider.isNoWaitEnabled());
		}
		sessionConfig.setSystemPropertiesProvider(systemPropertiesProvider);

		if(sessionManagerConfigurable.getBatchParameters().isEnabled()) {
			sessionConfig.setSessionFactoryType(SessionConfiguration.DB_SESSION_WITH_BATCH_UPDATE);
			BatchParameters batchParameters = sessionManagerConfigurable.getBatchParameters();
			sessionConfig.setBatchUpdateInterval(TimeUnit.SECONDS.toMillis(batchParameters.getIntervalInSec()));
			sessionConfig.setMaxBatchSize(batchParameters.getSize());
			sessionConfig.setSaveBatched(batchParameters.isInsertBatched());
			sessionConfig.setUpdateBatched(batchParameters.isUpdateBatched());
			sessionConfig.setDeleteBatched(batchParameters.isDeleteBatched());
		}else {
			sessionConfig.setSessionFactoryType(SessionConfiguration.DB_SESSION);
		}
		DBDataSource dbDataSource = serverContext.getServerConfiguration().getDatabaseDSConfiguration().
			getDataSource(sessionManagerConfigurable.getDatasource());
		if (dbDataSource == null) {
			throw new InitializationFailedException("Database datasource unavailable");
		}
		this.dbDataSourceName = dbDataSource.getDataSourceName();
		sessionConfig.addDataSource(dbDataSource);
		
		createSchemaMappings(sessionManagerConfigurable, sessionConfig);
		
		createScenarios(sessionConfig);
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE,"Diameter Session Manager initialized successfully");
		}
		
	}

	private void createSchemaMappings(DiameterSessionManagerConfigurable sessionManagerConfigurable,
			SessionConfigurationImpl sessionConfig) {
		for(SessionDataMapping sessionDataMapping : sessionManagerConfigurable.getSessionDatas()) {
			SchemaMappingImpl schemaMapping = new SchemaMappingImpl(AAAServerConstants.CONCURRENCY_ID_FIELD, 
					sessionManagerConfigurable.getStartTimeField(), sessionManagerConfigurable.getLastUpdateTimeField());
			List<FieldMappingImpl> fieldMappings = sessionDataMapping.getFeildMappings();
			fieldMappings.add(SESSION_STATUS_MAPPING);
			fieldMappings.add(PROTOCOL_TYPE_MAPPING);
			schemaMapping.setFieldMappings(new ArrayList<FieldMapping>(fieldMappings));
			schemaMapping.setSchemaName(sessionDataMapping.getName());
			schemaMapping.setTableName(sessionManagerConfigurable.getTableName());
			schemaMapping.setIdGenerator(SchemaMappingImpl.DB_SEQUENCE_GENERATOR);
			schemaMapping.setSequenceName(sessionManagerConfigurable.getSequenceName());
			sessionConfig.addSchema(schemaMapping);
		}
	}

	@VisibleForTesting
	protected SessionConfigurationImpl createSessionConfiguration() {
		return new SessionConfigurationImpl(serverContext);
	}
	
	private void createScenarios(SessionConfigurationImpl sessionConfig) throws InitializationFailedException{
		
		SessionFactory sessionFactory = null;
		try {
			sessionFactory = sessionConfig.createSessionFactory();
		} catch (InitializationFailedException e) {
			throw new InitializationFailedException("Initialization failed for diameter session manager, Reason : " + e.getMessage(), e);
		}
		for (SessionScenarioDetails sessionScenarioDetails : sessionManagerConfigurable.getScenarioDetails()) {
			SessionDataMapping sessionDataMapping = sessionManagerConfigurable.getSessionDataMapping(
					sessionScenarioDetails.getFieldMappingName());
			SessionScenario sessionScenario = new SessionScenario(
					sessionScenarioDetails.getName(),
					new FieldMappingParser(MODULE, new ArrayList<FieldMapping>(
							sessionDataMapping.getFeildMappings())),
					sessionFactory,
					sessionScenarioDetails.getExpression(),
					sessionManagerConfigurable.getTableName(),
					sessionScenarioDetails.getFieldMappingName(),
					sessionScenarioDetails.getCriteriaParams(),
					sessionManagerConfigurable.getMultiValueDelimeter()
					);
			try {
				sessionScenario.init();
				scenarios.add(sessionScenario);
			} catch (InvalidExpressionException e) {
				throw new InitializationFailedException("Diameter session manager initialization failed for Scenario with name: " + sessionScenarioDetails.getName() + 
						", Reason : " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 
	 * @return list of sessions located ,empty list in case of no scenario is selected & null in case of failure  
	 * 
	 */
	@Override
	public List<SessionData> locate(@Nonnull DiameterRequest request, @Nullable DiameterAnswer answer) {
		AttributeValueProvider requestValueProvider = new DiameterAVPValueProvider(request);
		if(answer == null) {
			answer = new DiameterAnswer();
		}
		AttributeValueProvider responseValueProvider = new DiameterAVPValueProvider(answer);
		KeywordValueProvider keywordValueProvider = new KeywordValueProvider(requestValueProvider, responseValueProvider);
		SessionScenario scenario = null;
		
		try {
			scenario = getApplicableSessionScenario(keywordValueProvider);
		} catch (InvalidTypeCastException e) {
			LogManager.getLogger().debug(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (MissingIdentifierException e) {
			LogManager.getLogger().debug(MODULE, e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		
		if(scenario == null) {
			return Collections.emptyList();
		}
		
		identifierToSessionScenarioMap.put(getDiameterKeyForIdentifier(request), scenario);
		List<SessionData> sessionDatas = null;
		try {
			sessionDatas = scenario.locate(new DiameterAVPValueProvider(request));
			if(sessionDatas == null) {
				sessionManagerConfigurable.getDBFailureActions().apply(answer, sessionManagerConfigurable.getName());
			}

		} catch (ImproperSearchCriteriaException ex) {
			answer.setParameter(IDiameterSessionManager.FURTHER_PROCESSING_REQUIRED, Boolean.valueOf(false));
			answer.setParameter(IDiameterSessionManager.PROCESSING_COMPLETED, Boolean.valueOf(true));
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, answer, String.valueOf(ResultCode.DIAMETER_MISSING_AVP));
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, answer, "Session Manager Operation Failed due to Missing AVP/s.");
			addFailedAVPs(ex.getMissingAttributes(), answer);
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Session Manager Operation Failed due to Missing AVP/s.");
				LogManager.getLogger().trace(MODULE, ex);
			}
		}
		return sessionDatas;
	}

	private void addFailedAVPs(List<String> missingAttributes, DiameterAnswer answer) {
		List<IDiameterAVP> missinAVPList = new ArrayList<IDiameterAVP>();
		for (String avp : missingAttributes) {
			missinAVPList.add(DiameterUtility.createAvp(avp));
		}
		DiameterUtility.addFailedAVPList(answer,missinAVPList);
	}

	@Override
	public int save(DiameterRequest diameterRequest,DiameterAnswer diameterAnswer) {
		return process(diameterRequest, diameterAnswer, DBOperationAction.INSERT);
	}
	
	@Override
	public int update(DiameterRequest diameterRequest,	DiameterAnswer diameterAnswer) {
		return process(diameterRequest, diameterAnswer, DBOperationAction.UPDATE);
	}
	
	@Override
	public int delete(DiameterRequest diameterRequest,	DiameterAnswer diameterAnswer) {
		if(diameterRequest.getLocatedSessionData() == null) {
			LogManager.getLogger().info(MODULE, "Session location operation did not perform, Reason db unavaibility");
			return OPERATION_FAILURE;
		}
		return process(diameterRequest, diameterAnswer, DBOperationAction.DELETE);
	}
	
	@Override
	public int updateOrSave(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer, List<SessionData> sessionDatas) {
		if(sessionDatas == null) {
			LogManager.getLogger().info(MODULE, "Session location operation did not perform, Reason db unavaibility");
			return OPERATION_FAILURE;
		}
		if(sessionDatas.isEmpty()) {
			return process(diameterRequest, diameterAnswer, DBOperationAction.INSERT);
		} else {
			return process(diameterRequest, diameterAnswer, DBOperationAction.UPDATE);
		}
	}
	
	@Override
	public int delete(List<SessionData> sessionData) {
		if(scenarios.isEmpty()) {
			return OPERATION_FAILURE;
		}
		return scenarios.get(0).delete(sessionData);
	}

	@Override
	public int truncate() {
		if(scenarios.isEmpty()) {
			return OPERATION_FAILURE;
		}
		return scenarios.get(0).truncate();
	}
	
	private int process(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer, DBOperationAction action) {
		DiameterAVPValueProvider requestValueProvider = new DiameterAVPValueProvider(diameterRequest);
		DiameterAVPValueProvider responseValueProvider = new DiameterAVPValueProvider(diameterAnswer);
		KeywordValueProvider keywordValueProvider = new KeywordValueProvider(requestValueProvider,responseValueProvider);
		SessionScenario scenario = identifierToSessionScenarioMap.remove(getDiameterKeyForIdentifier(diameterAnswer));
		if(scenario == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "No scenario has been saved by Request Packet, so checking all scenarios for applicability.");
			}
			try {
			scenario = getApplicableSessionScenario(keywordValueProvider);
			} catch (InvalidTypeCastException e) {
				LogManager.getLogger().debug(MODULE, e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} catch (MissingIdentifierException e) {
				LogManager.getLogger().debug(MODULE, e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		if(scenario == null) {
			return OPERATION_FAILURE;
		}
		
		IDiameterAVP avp = diameterRequest.getAVP(DiameterAVPConstants.EC_SESSION_ACTION);
		boolean checkOperationActions = true;
		if(avp != null) {
			DBOperationAction overrideAction = DBOperationAction.getDBOperationForOverrideActionFromOpearationValue(avp.getInteger());
			if(overrideAction != null) {
				if(LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Received " + DiameterAVPConstants.EC_SESSION_ACTION_STR + " AVP with value: " 
							+ overrideAction.getName() + ", So overriding default behaviour: " + action.getName() + ".");
				}
				action = overrideAction;
				checkOperationActions = false;
			} else {
				if(LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Received " + DiameterAVPConstants.EC_SESSION_ACTION_STR + " AVP with unknown value: " 
							+ avp.getInteger() + ", So skipping the override behaviour.");
				}
			}
		}
		if (checkOperationActions) {
			for (DBOperationAndExpressionPair operationActionDetail : operationActionDetails) {
				if (operationActionDetail.getLogicalExpression().evaluate(keywordValueProvider)) {
					action = operationActionDetail.getAction();
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Session override action with name: " + operationActionDetail.getName() 
								+ " is selected");
					}
					break;
				}
			}
		}
		int value = action.applyAction(scenario, requestValueProvider, responseValueProvider);
		if(value < 0) {
			sessionManagerConfigurable.getDBFailureActions().apply(diameterAnswer, sessionManagerConfigurable.getName());
		}
		return value; 
	}
	
	private SessionScenario getApplicableSessionScenario(AttributeValueProvider valueProvider)
			throws InvalidTypeCastException, MissingIdentifierException {
		for(SessionScenario sessionScenario : scenarios) {
			if(sessionScenario.isApplicable(valueProvider)) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Scenario selected with name: " + sessionScenario.getScenarioName()
							+ " for Diameter Packet with Session Id=" + valueProvider.getStringValue(DiameterAVPConstants.SESSION_ID));
				}
				return sessionScenario;
			}
		}
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "No Scenario has been selected for Diameter Packet with Session Id=" 
					+ valueProvider.getStringValue(DiameterAVPConstants.SESSION_ID));
		}
		return null;
	}
	
	private String getDiameterKeyForIdentifier(DiameterPacket diameterPacket) {
		return diameterPacket.getHop_by_hopIdentifier() + HBH_AND_ETE_FIELD_SEPARATOR + diameterPacket.getEnd_to_endIdentifier();
	}
	
	private final class SystemPropertiesProviderImpl implements SystemPropertiesProvider {
		@Override
		public boolean isNoWaitEnabled() {
			return Strings.toBoolean(System.getProperty(MiscellaneousConfigurable.NOWAIT_SYSTEM_PROPERTY));
		}

		@Override
		public boolean isBatchEnabled() {
			return Strings.toBoolean(System.getProperty(MiscellaneousConfigurable.BATCH_SYSTEM_PROPERTY));
		}

		@Override
		public int getQueryTimeout() {
			return sessionManagerConfigurable.getDbQueryTimeout();
		}

		@Override
		public int getBatchQueryTimeout() {
			if(sessionManagerConfigurable.getBatchParameters() != null){
				return sessionManagerConfigurable.getBatchParameters().getQueryTimeout();
			} else {
				return CommonConstants.NO_QUERY_TIMEOUT;
			}
			
		}
	}

	private class DBOperationAndExpressionPair {
		private String name;
		private LogicalExpression logicalExpression;
		private DBOperationAction dbOperationAction;
		
		public DBOperationAndExpressionPair(String actionname, String expression, String dbOperationAction) throws InitializationFailedException {
			this.name = actionname;
			try {
				this.logicalExpression = Compiler.getDefaultCompiler().parseLogicalExpression(expression.replaceAll("\\$", "\\\\\\$"));
			} catch (InvalidExpressionException e) {
				throw new InitializationFailedException("Operation action: " + name + " not working, Reason: " + e.getMessage(), e);
			}
			this.dbOperationAction = DBOperationAction.fromName(dbOperationAction);
			if (this.dbOperationAction == null) {
				throw new InitializationFailedException("Operation action: " + name + 
						" not working, Reason: configured override action is nor appropriate.");
			}
		}
		
		public String getName() {
			return name;
		}
		
		public LogicalExpression getLogicalExpression() {
			return logicalExpression;
		}
		
		public DBOperationAction getAction() {
			return dbOperationAction;
		}
	}
	
	public boolean isAlive() {
		return DBConnectionManager.getInstance(this.dbDataSourceName).isAlive();
	}
		

}