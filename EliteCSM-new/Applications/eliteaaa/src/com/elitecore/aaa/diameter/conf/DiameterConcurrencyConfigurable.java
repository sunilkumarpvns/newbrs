package com.elitecore.aaa.diameter.conf;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.diameter.conf.sessionmanager.FieldMappingImpl;
import com.elitecore.aaa.diameter.conf.sessionmanager.SessionDataMapping;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.radius.sessionx.ConcurrencySessionManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.CaseInsensitiveEnumAdapter;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

/**
 * 
 * @author malav.desai
 *
 */
@XmlRootElement(name = "diameter-concurrency-configuration-list")
@ConfigurationProperties(moduleName ="DIAMETER_CONCURRENCY_CONFIGURABLE",synchronizeKey ="", reloadWith = XMLReader.class, readWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db"},name = "diameter-concurrency-conf")
public class DiameterConcurrencyConfigurable extends Configurable{
	
	private static final String MODULE = "DIAMETER-CONCURRENCY-CONFIGURABLE";
	private List<DiameterConcurrencyConfigurationData> configurationList;
	
	private Map<String, DiameterConcurrencyConfigurationData> configurationMap;
	
	public DiameterConcurrencyConfigurable() {
		configurationList = new ArrayList<DiameterConcurrencyConfigurationData>();
		configurationMap = new HashMap<String, DiameterConcurrencyConfigurationData>();
	}
	
	public DiameterConcurrencyConfigurationData getDiameterConcurrencyConfigurationDataByName(String name) {
		return configurationMap.get(name);
	}
	
	@XmlElement(name = "diameter-concurrency-configuration")
	public List<DiameterConcurrencyConfigurationData> getConfigurationList() {
		return configurationList;
	}

	@DBRead
	public void readFromDatabase() throws Exception {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			String diaConcurrencyQuery = "select * from TBLMDIACONCURRENCYCONFIG";
			preparedStatement = connection.prepareStatement(diaConcurrencyQuery);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				DiameterConcurrencyConfigurationData readDiameterConcurrencyConfigurationData = readDiameterConcurrencyConfigurationData(connection, resultSet);
				configurationList.add(readDiameterConcurrencyConfigurationData);
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@PostRead
	public void postReadProcessing() {
		for (DiameterConcurrencyConfigurationData data : configurationList) {
			configurationMap.put(data.getDiaConcurrencyId(), data);
			LogManager.getLogger().info(MODULE, "Diameter Concurrency: \n" + data);
		}
	}
	
	@PostReload
	public void postReloadProcessing() {
		
	}
	
	@PostWrite
	public void postWriteProcessing() {
		
	}
	
	private DiameterConcurrencyConfigurationData readDiameterConcurrencyConfigurationData(Connection connection, ResultSet resultSet) throws SQLException {
		DiameterConcurrencyConfigurationData diameterConcurrencyConfigurationData = new DiameterConcurrencyConfigurationData();
		diameterConcurrencyConfigurationData.setDiaConcurrencyId(resultSet.getString("DIACONCONFIGID"));
		diameterConcurrencyConfigurationData.name = resultSet.getString("NAME");
		diameterConcurrencyConfigurationData.datasourceId = resultSet.getString("DATABASESID");
		diameterConcurrencyConfigurationData.tableName = resultSet.getString("TABLENAME");
		diameterConcurrencyConfigurationData.startTimeField = resultSet.getString("STARTTIMEFIELD");
		diameterConcurrencyConfigurationData.lastUpdateTimeField = resultSet.getString("LASTUPDATETIMEFIELD");
		diameterConcurrencyConfigurationData.concurrencyIdentityField = resultSet.getString("CONCURRENCYIDENTITY");
		diameterConcurrencyConfigurationData.dbFailureAction = DBFailureActions.fromActionName(resultSet.getString("DBFAILUREACTION"), "");
		diameterConcurrencyConfigurationData.sessionOverrideAction = SessionOverrideActions.get(resultSet.getString("SESSIONOVERRIDEACTION"));
		diameterConcurrencyConfigurationData.sessionOverrideFields = resultSet.getString("SESSIONOVERRIDEFIELDS");
		diameterConcurrencyConfigurationData.sessionData = readSessionData(connection, diameterConcurrencyConfigurationData);
		diameterConcurrencyConfigurationData.sessionData.setName(diameterConcurrencyConfigurationData.name + " Mapping");
		return diameterConcurrencyConfigurationData;
	}
	
	private SessionDataMapping readSessionData(Connection connection, 
			DiameterConcurrencyConfigurationData data) throws SQLException {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		SessionDataMapping sessionDataMapping = new SessionDataMapping();
		List<FieldMappingImpl> fieldMappings = new ArrayList<FieldMappingImpl>();
		String fieldMappingQuery = "select * from TBLMDIACONFIELDMAPPINGS where DIACONCONFIGID = '" + data.getDiaConcurrencyId() + "' order by ORDERNUMBER";
		try {
			preparedStatement = connection.prepareStatement(fieldMappingQuery);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				FieldMappingImpl fieldMapping = new FieldMappingImpl();
				fieldMapping.setColumnName(resultSet.getString("DBFIELDNAME"));
				fieldMapping.setPropertyName(resultSet.getString("REFERRINGATTR"));
				fieldMapping.setType(resultSet.getInt("DATATYPE"));
				fieldMapping.setDefaultValue(resultSet.getString("DEFAULTVALUE"));
				fieldMapping.setField(resultSet.getString("FIELD"));
				if (Strings.toBoolean(resultSet.getString("INCLUDEINASR"))) {
					data.addInASR(fieldMapping.getPropertyName());
				}
				fieldMappings.add(fieldMapping);
			}
			sessionDataMapping.setFeildMappings(fieldMappings);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
		}
		return sessionDataMapping;
	}
	
	public static class DiameterConcurrencyConfigurationData {
		private String diaConcurrencyId;
		private String name;
		private String datasourceId;
		private String tableName;
		private String startTimeField;
		private String lastUpdateTimeField;
		private String concurrencyIdentityField;
		private DBFailureActions dbFailureAction;
		private SessionOverrideActions sessionOverrideAction;
		private String sessionOverrideFields;
		private SessionDataMapping sessionData;
		private Set<String> includeInASR = new HashSet<String>();
		
		@XmlElement(name = "diameter-concurrency-id", type = String.class)
		public String getDiaConcurrencyId() {
			return diaConcurrencyId;
		}
		public void setDiaConcurrencyId(String diaConcurrencyId) {
			this.diaConcurrencyId = diaConcurrencyId;
		}
		
		@XmlElement(name = "name", type = String.class)
		public String getName() {
			return this.name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		@XmlElement(name = "datasource", type = String.class)
		public String getDatasourceId() {
			return datasourceId;
		}
		public void setDatasourceId(String datasourceId) {
			this.datasourceId = datasourceId;
		}
		
		@XmlElement(name = "tablename", type = String.class)
		public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
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
		
		@XmlElement(name = "concurrency-id-field", type = String.class)
		public String getConcurrencyIdentityField() {
			return concurrencyIdentityField;
		}
		public void setConcurrencyIdentityField(String concurrencyIdentityField) {
			this.concurrencyIdentityField = concurrencyIdentityField;
		}
		
		@XmlElement(name = "session-data")
		public SessionDataMapping getSessionData() {
			return sessionData;
		}
		public void setSessionData(SessionDataMapping sessionData) {
			this.sessionData = sessionData;
		}
		
		@XmlJavaTypeAdapter(value = DBFailureActions.DBFailureActionsAdapter.class)
		@XmlElement(name = "failure-action", defaultValue ="IGNORE", type = String.class)
		public DBFailureActions getDBFailureAction() {
			return this.dbFailureAction;
		}
		
		public void setDBFailureAction(DBFailureActions dbFailureAction) {
			this.dbFailureAction = dbFailureAction;
		}
		
		@XmlJavaTypeAdapter(value = SessionOverrideActions.SessionClosureAndOverrideActionsAdapter.class)
		@XmlElement(name = "session-override-action", defaultValue ="NONE", type = String.class)
		public SessionOverrideActions getSessionOverrideAction() {
			return sessionOverrideAction;
		}
		public void setSessionOverrideAction(SessionOverrideActions sessionOverrideAction) {
			this.sessionOverrideAction = sessionOverrideAction;
		}
		
		@XmlElement(name = "session-override-fields", type = String.class)
		public String getSessionOverrideFields() {
			return sessionOverrideFields;
		}
		public void setSessionOverrideFields(String sessionOverrideFields) {
			this.sessionOverrideFields = sessionOverrideFields;
		}
		
		@XmlElementWrapper(name = "include-in-abort-session")
		@XmlElement(name = "property-name")
		public Set<String> getIncludeInASR() {
			return includeInASR;
		}
		public void setIncludeInASR(Set<String> includeInASR) {
			this.includeInASR = includeInASR;
		}
		
		private void addInASR(String key) {
			this.includeInASR.add(key);
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
			writer.println("Datasource ID : " + datasourceId);
			writer.println("SM Table Name : " + tableName);
			writer.println("Start Time Field: " + startTimeField);
			writer.println("Last Update Time Field: " + lastUpdateTimeField);
			writer.println("DB Failure Action : " + dbFailureAction.name());
			writer.println("Session Override Action : " + sessionOverrideAction.value);
			writer.println("Session Override Fields : " + sessionOverrideFields);
			writer.println("Session Data Mapping :");
			sessionData.format(writer);
			writer.println("Attributes included in ASR : " + includeInASR);
			return out.toString();
		}
		
		
	}
	
	public static enum DBFailureActions {
		IGNORE {
			
			@Override
			public void apply(ApplicationResponse response, String name) {
				if(LogManager.getLogger().isInfoLogLevel()){
					LogManager.getLogger().info(MODULE, "Applying failure behavior: IGNORE for session manager : " + name + ".");
				}
			}
			
		},
		REJECT {
			@Override
			public void apply(ApplicationResponse response, String name) {
				if(LogManager.getLogger().isInfoLogLevel()){
					LogManager.getLogger().info(MODULE, "Applying failure behavior: REJECT for session manager : " + name + ".");
				}
			
				DiameterProcessHelper.rejectResponse(response, ResultCode.DIAMETER_AUTHENTICATION_REJECTED, DiameterErrorMessageConstants.CONCURRENCY_FAILED_DUE_TO_DBOPERATION_FAILURE);
			}

		},
		DROP {
			@Override
			public void apply(ApplicationResponse response, String name) {
				if(LogManager.getLogger().isInfoLogLevel()){
					LogManager.getLogger().info(MODULE, "Applying failure behavior: DROP for session manager : " + name + ".");
				}
				
				DiameterProcessHelper.dropResponse(response);
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
		
		public abstract void apply(ApplicationResponse response, String name);
		
		/** This adapter will make case-insensitive conversion from String --> DBFailureActions **/
		public static class DBFailureActionsAdapter extends CaseInsensitiveEnumAdapter<DBFailureActions> {

			public DBFailureActionsAdapter() {
				super(DBFailureActions.class, DBFailureActions.IGNORE);
			}
		}
	}
	
	public static enum SessionOverrideActions {
		NONE("None"),
		GENREATE_ASR("Generate ASR"),
		;

		public String value;
		private static final Map<String, SessionOverrideActions> map;
		
		private SessionOverrideActions(String value) {
			this.value = value;
		}
		
		static{
			map =  new HashMap<String, SessionOverrideActions>();
			for(SessionOverrideActions sessionClosureAndOverrideAction : values()){
				map.put(sessionClosureAndOverrideAction.value, sessionClosureAndOverrideAction);
			}
		}
		
		public static boolean isValid(int value){
			return map.containsKey(value);	
		}
		
		public static SessionOverrideActions get(String key){
			return map.get(key);
		}
		
		public static class SessionClosureAndOverrideActionsAdapter extends CaseInsensitiveEnumAdapter<SessionOverrideActions> {

			public SessionClosureAndOverrideActionsAdapter() {
				super(SessionOverrideActions.class, SessionOverrideActions.NONE);
			}
		}
	}
	
	public static enum MandateryFieldConstants {
		APPLICATION_ID("Application ID"),
		SESSION_ID("Session Identity"),
		PDP_TYPE("PDP Type"),
		INDIVIDUAL_ID("Individual Identity"),
		PEER_ID("Peer Identity"),
		GROUP_ID("Group Identity"),
		;
		
		public String value;
		private static Map<String, MandateryFieldConstants> nameToConstantMap;
		
		static {
			nameToConstantMap = new HashMap<String, DiameterConcurrencyConfigurable.MandateryFieldConstants>();
			for (MandateryFieldConstants mandateryFieldConstant : values()) {
				nameToConstantMap.put(mandateryFieldConstant.value, mandateryFieldConstant);
			}
		}

		private MandateryFieldConstants(String value) {
			this.value = value;
		}
		
		public static MandateryFieldConstants getMandateryFieldConstant(String name) {
			return nameToConstantMap.get(name);
		}
	}
}
