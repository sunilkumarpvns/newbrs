package com.elitecore.aaa.diameter.conf.sessionmanager;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.diameter.conf.DiameterStackConfigurable;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.CaseInsensitiveEnumAdapter;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.IDiameterSessionManager;

/***
 * 
 * @author malav.desai
 *
 */
@XmlType(propOrder = {})
@XmlRootElement(name = "session-manager")
@ConfigurationProperties(moduleName ="SESSION-MANAGER-CONFIGURABLE",readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class, synchronizeKey = "SESSION-MANAGER-CONFIGURABLE")
@XMLProperties(name = "session-manager", schemaDirectories = { "system","schema" }, configDirectories = { "conf" })
public class DiameterSessionManagerConfigurable extends Configurable {
	
	private static final String MODULE = "SESSION-MANAGER-CONFIGURABLE";
	private String sessionManagerId;
	private String name;
	private String datasource;
	private String tableName;
	private String sequenceName;
	private String startTimeField;
	private String lastUpdateTimeField;
	private int dbQueryTimeoutInSec;
	private String multiValueDelimeter;
	private BatchParameters batchParameters;
	private List<SessionDataMapping> sessionDataList;
	private List<SessionScenarioDetails> scenarioDetails;
	private List<OperationOverrideDetail> operationOverrideDetails;
	private DBFailureActions dBFailureActions;
	
	@XmlJavaTypeAdapter(value = DBFailureActions.DBFailureActionsAdapter.class)
	@XmlElement(name = "failure-action", defaultValue ="IGNORE", type = String.class)
	public DBFailureActions getDBFailureActions() {
		return dBFailureActions;
	}

	public void setDBFailureActions(DBFailureActions dBFailureActions) {
		this.dBFailureActions = dBFailureActions;
	}

	public DiameterSessionManagerConfigurable() {
		sessionDataList = new ArrayList<SessionDataMapping>();
		scenarioDetails = new ArrayList<SessionScenarioDetails>();
		operationOverrideDetails = new ArrayList<OperationOverrideDetail>();
	}
	
	@XmlElement(name = "sesssion-manager-id", type = String.class)
	public String getSessionManagerId() {
		return this.sessionManagerId;
	}
	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}
	
	@XmlElement(name = "name", type = String.class)
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "datasource", type = String.class)
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	
	@XmlElement(name = "tablename", type = String.class)
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@XmlElement(name = "sequence-name", type = String.class)
	public String getSequenceName() {
		return this.sequenceName;
	}
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
	@XmlElement(name = "start-time-field", type = String.class)
	public String getStartTimeField() {
		return this.startTimeField;
	}
	public void setStartTimeField(String startTimeField) {
		this.startTimeField = startTimeField;
	}

	@XmlElement(name = "last-update-time-field", type = String.class)
	public String getLastUpdateTimeField() {
		return this.lastUpdateTimeField;
	}
	public void setLastUpdateTimeField(String lastUpdateTimeField) {
		this.lastUpdateTimeField = lastUpdateTimeField;
	}
	
	@XmlElement(name = "db-query-timeout-sec", type = int.class)
	public int getDbQueryTimeout() {
		return dbQueryTimeoutInSec;
	}
	public void setDbQueryTimeout(int dbQueryTimeout) {
		this.dbQueryTimeoutInSec = dbQueryTimeout;
	}
	
	@XmlElement(name="delimeter", type = String.class)
	public String getMultiValueDelimeter() {
		return multiValueDelimeter;
	}
	public void setMultiValueDelimeter(String delimeter) {
		this.multiValueDelimeter = delimeter;
	}
	
	@XmlElement(name = "batch-parameters")
	public BatchParameters getBatchParameters() {
		return batchParameters;
	}
	public void setBatchParameters(BatchParameters batchParameters) {
		this.batchParameters=batchParameters;
	}
	
	@XmlElementWrapper(name = "session-data-mapping")
	@XmlElement(name = "field-mapping-list")
	public List<SessionDataMapping> getSessionDatas() {
		return sessionDataList;
	}
	public void setSessionDatas(List<SessionDataMapping> sessionDataList) {
		this.sessionDataList = sessionDataList;
	}
	
	@XmlElementWrapper(name = "scenarios")
	@XmlElement(name = "scenario")
	public List<SessionScenarioDetails> getScenarioDetails() {
		return scenarioDetails;
	}
	public void setScenarioDetails(List<SessionScenarioDetails> scenarioDetails) {
		this.scenarioDetails = scenarioDetails;
	}
	
	@XmlElementWrapper(name = "override-operations")
	@XmlElement(name = "override-operation")
	public List<OperationOverrideDetail> getOperationOverrideDetails() {
		return operationOverrideDetails;
	}
	public void setOperationOverrideDetails(List<OperationOverrideDetail> operationOverrideDetails) {
		this.operationOverrideDetails = operationOverrideDetails;
	}

	public SessionDataMapping getSessionDataMapping(String name) {
		if(name == null) {
			return null;
		}
		for (SessionDataMapping sessionDataMapping : sessionDataList) {
			if(name.equals(sessionDataMapping.getName())) {
				return sessionDataMapping;
			}
		}
		return null;
	}
	
	@PostRead
	public void postReadProcessing() {
		for (SessionScenarioDetails scenarioDetail : scenarioDetails) {
			List<String> criteriaParams = new ArrayList<String>();
			for (String string : scenarioDetail.getCriteria().split(multiValueDelimeter)) {
				criteriaParams.add(string.trim());
			}
			scenarioDetail.setCriteriaParams(criteriaParams);
		}
		LogManager.getLogger().info(MODULE, toString());
	}
	
	@PostWrite
	public void postWriteProcessing() {
		
	}
	
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		IndentingPrintWriter writer = new IndentingPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(MODULE);
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();
		
		writer.println("Session Manager Name : " + name);
		writer.println("Datasource ID : " + datasource);
		writer.println("SM Table Name : " + tableName);
		writer.println("Sequence Name: " + sequenceName);
		writer.println("DB Query Timeout (sec) : " + dbQueryTimeoutInSec);
		writer.println("Start Time Field: " + startTimeField);
		writer.println("Last Update Time Field: " + lastUpdateTimeField);
		writer.println("Multivalue Delimeter :" + multiValueDelimeter);
		writer.println("DB Failure Action : " + dBFailureActions.name());
		writer.println("Batch Parametres :");
		batchParameters.format(writer);
		writer.println("Session Data Mappings :");
		for (SessionDataMapping sessionDataMapping : sessionDataList) {
			sessionDataMapping.format(writer);
		}
		writer.println("Session Scenario Details :");
		for (SessionScenarioDetails sessionScenarioDetail : scenarioDetails) {
			sessionScenarioDetail.format(writer);
		}
		writer.println("Session Scenario Details :");
		for (OperationOverrideDetail operationOverrideScenarioDetail : operationOverrideDetails) {
			operationOverrideScenarioDetail.format(writer);
		}
		return out.toString();
	}
	
	@DBRead
	public void readDiameterSessionManagerFromDBUsingId() throws Exception {
		Connection connection = null;

		this.sessionManagerId = getConfigurationContext().get(DiameterStackConfigurable.class).getSessionManagerId().get();
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			
			readSessionManagerBasicDetails(connection);
			
			readSessionDataMappings(connection);
			
			readScenarios(connection);
			
			readOperationOverride(connection);
		} finally {
			DBUtility.closeQuietly(connection);
		}
	}
	
	private void readSessionManagerBasicDetails(Connection connection) throws SQLException, InitializationFailedException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
		String sessionManagerBasicDetailQuery = "select * from TBLMDIAMETERSESSIONMANAGER where SESSIONMANAGERID = '" + this.sessionManagerId + "'";
		preparedStatement = connection.prepareStatement(sessionManagerBasicDetailQuery); //NOSONAR - Reason: SQL binding mechanisms should be used
		resultSet = preparedStatement.executeQuery();
		if (resultSet.next() == false ) {
			throw new InitializationFailedException("No configuration is available for diameter session manager.");
		}
		this.name = resultSet.getString("NAME");
		this.tableName = resultSet.getString("TABLENAME");
		this.datasource = resultSet.getString("DATABASEDSID");
		this.dbQueryTimeoutInSec = resultSet.getInt("DBQUERYTIMEOUT");
		this.sequenceName = resultSet.getString("SEQUENCENAME");
		this.startTimeField = resultSet.getString("STARTTIMEFIELD");
		this.lastUpdateTimeField = resultSet.getString("LASTUPDATETIMEFIELD");
		this.multiValueDelimeter = resultSet.getString("DELIMETER");
		this.dBFailureActions = DBFailureActions.fromActionName(resultSet.getString("DBFAILUREACTION"), "");
		this.batchParameters = new BatchParameters();
			this.batchParameters.setEnabled(Boolean.parseBoolean(resultSet.getString("BATCHENABLED")));
			if ( this.batchParameters.isEnabled()) {
				this.batchParameters.setSize(resultSet.getInt("BATCHSIZE"));
				this.batchParameters.setInterval(resultSet.getInt("BATCHINTERVAL"));
				this.batchParameters.setQueryTimeout(resultSet.getInt("BATCHQUERYTIMEOUT"));
				this.batchParameters.setInsertBatched(Boolean.parseBoolean(resultSet.getString("BATCHEDINSERT")));
				this.batchParameters.setUpdateBatched(Boolean.parseBoolean(resultSet.getString("BATCHEDUPDATE")));
				this.batchParameters.setDeleteBatched(Boolean.parseBoolean(resultSet.getString("BATCHEDDELETE")));
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
		}
	}
	
	private void readSessionDataMappings(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		this.sessionDataList = new ArrayList<SessionDataMapping>();
		String sessionMappingQuery = "select * from TBLMDIASESSIONMAPCONF where SESSIONMANAGERID = '" + this.sessionManagerId + "' order by ORDERNUMBER";
		try {
			preparedStatement = connection.prepareStatement(sessionMappingQuery);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				SessionDataMapping sessionDataMapping = readSessionData(connection, resultSet.getString("MAPPINGID"));
				sessionDataMapping.setName(resultSet.getString("MAPPINGNAME"));
				this.sessionDataList.add(sessionDataMapping);
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
		}
	}
	
	private SessionDataMapping readSessionData(Connection connection, String mappingId) throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		SessionDataMapping sessionDataMapping = new SessionDataMapping();
		List<FieldMappingImpl> fieldMappings = new ArrayList<FieldMappingImpl>();
		String fieldMappingQuery = "select * from TBLMDIASESSIONDBFIELDMAP where MAPPINGID = '" + mappingId + "' order by ORDERNUMBER";
		try {
			preparedStatement = connection.prepareStatement(fieldMappingQuery);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				FieldMappingImpl fieldMapping = new FieldMappingImpl();
				fieldMapping.setColumnName(resultSet.getString("DBFIELDNAME"));
				fieldMapping.setPropertyName(resultSet.getString("REFERRINGATTR"));
				fieldMapping.setType(resultSet.getInt("DATATYPE"));
				fieldMapping.setDefaultValue(resultSet.getString("DEFAULTVALUE"));

				fieldMappings.add(fieldMapping);
			}
			sessionDataMapping.setFeildMappings(fieldMappings);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
		}
		return sessionDataMapping;
	}
	
	private void readScenarios(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			this.scenarioDetails = new ArrayList<SessionScenarioDetails>();
			String sessionScenarioDeatilQuery = "select * from TBLMDIASESSIONSCENARIOMAP where SESSIONMANAGERID = '" + this.sessionManagerId + "' order by ORDERNUMBER";
			preparedStatement = connection.prepareStatement(sessionScenarioDeatilQuery); //NOSONAR - Reason: SQL binding mechanisms should be used
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				SessionScenarioDetails sessionScenarioDetail = new SessionScenarioDetails();
				sessionScenarioDetail.setName(resultSet.getString("NAME"));
				sessionScenarioDetail.setExpression(resultSet.getString("RULESET"));
				sessionScenarioDetail.setCriteria(resultSet.getString("CRITERIA"));
				sessionScenarioDetail.setFieldMappingName(resultSet.getString("MAPPINGNAME"));

				this.scenarioDetails.add(sessionScenarioDetail);
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
		}
	}

	private void readOperationOverride(Connection connection) throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			this.operationOverrideDetails = new ArrayList<OperationOverrideDetail>();
			String sessionOverrideActionQuery = "select * from TBLMDIASESSIONOVERRIDEACTION where SESSIONMANAGERID = '" + this.sessionManagerId + "' order by ORDERNUMBER";
			preparedStatement = connection.prepareStatement(sessionOverrideActionQuery);
			resultSet = preparedStatement.executeQuery(); //NOSONAR - Reason: SQL binding mechanisms should be used
			while (resultSet.next()) {
				OperationOverrideDetail operationOverrideScenarioDetail = new OperationOverrideDetail();
				operationOverrideScenarioDetail.setName(resultSet.getString("NAME"));
				operationOverrideScenarioDetail.setExpression(resultSet.getString("RULESET"));
				operationOverrideScenarioDetail.setOverrideAction(resultSet.getString("ACTIONS"));

				this.operationOverrideDetails.add(operationOverrideScenarioDetail);
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
		}
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}
	
	@DBReload
	public void reload(){
		
	}
	
	public static enum DBFailureActions {
		IGNORE {
			
			@Override
			public void apply(DiameterPacket diameterPacket, String name) {
				if(LogManager.getLogger().isInfoLogLevel()){
					LogManager.getLogger().info(MODULE, "Applying failure behavior: IGNORE for session manager : " + name + ".");
				}
			}
			
		},
		REJECT {
			@Override
			public void apply(DiameterPacket diameterPacket, String name) {
				if(LogManager.getLogger().isInfoLogLevel()){
					LogManager.getLogger().info(MODULE, "Applying failure behavior: REJECT for session manager : " + name + ".");
				}
				
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, diameterPacket, String.valueOf(ResultCode.DIAMETER_AUTHENTICATION_REJECTED));
				diameterPacket.setParameter(IDiameterSessionManager.FURTHER_PROCESSING_REQUIRED, Boolean.valueOf(false));
				diameterPacket.setParameter(IDiameterSessionManager.PROCESSING_COMPLETED, Boolean.valueOf(true));
				DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, diameterPacket, DiameterErrorMessageConstants.SESSION_MANAGER_DB_OPERATION_FAILED);
			}

		},
		DROP {
			@Override
			public void apply(DiameterPacket diameterAnswer, String name) {
				if(LogManager.getLogger().isInfoLogLevel()){
					LogManager.getLogger().info(MODULE, "Applying failure behavior: DROP for session manager : " + name + ".");
				}
				diameterAnswer.setParameter(IDiameterSessionManager.MARK_FOR_DROP_REQUEST,Boolean.valueOf(true));
				diameterAnswer.setParameter(IDiameterSessionManager.FURTHER_PROCESSING_REQUIRED,Boolean.valueOf(false));
				diameterAnswer.setParameter(IDiameterSessionManager.PROCESSING_COMPLETED,Boolean.valueOf(true));
			}

		};
		
		private static final Map<String, DBFailureActions> nameToDBFailureActionMap;
		
		static {
			nameToDBFailureActionMap = new HashMap<String, DBFailureActions>();
			for(DBFailureActions failureAction: values()){
				nameToDBFailureActionMap.put(failureAction.name(), failureAction);
			}
		}
		
		public static DBFailureActions fromActionName(String actionName, String smInstanceName){
			DBFailureActions dbFailureAction = nameToDBFailureActionMap.get(actionName);
			if(LogManager.getLogger().isInfoLogLevel()){
				LogManager.getLogger().info(ConcurrencySessionManager.MODULE, "Db Failure Action: " + actionName + " is applied for session manager: " + smInstanceName);
			}
			if(dbFailureAction == null){
				if(LogManager.getLogger().isWarnLogLevel()){
					LogManager.getLogger().warn(ConcurrencySessionManager.MODULE, "Configured action: " + actionName + " for session manager: " + smInstanceName + " is not supported, so applying default IGNORE failure action.");
				}
				dbFailureAction = DBFailureActions.IGNORE;
			}
			return dbFailureAction;
		}
		
		public abstract void apply(DiameterPacket response, String name);
		
		/** This adapter will make case-insensitive conversion from String --> DBFailureActions **/
		public static class DBFailureActionsAdapter extends CaseInsensitiveEnumAdapter<DBFailureActions> {

			public DBFailureActionsAdapter() {
				super(DBFailureActions.class, DBFailureActions.IGNORE);
			}
		}
	}

}