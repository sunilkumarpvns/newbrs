package com.elitecore.aaa.radius.sessionx.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elitecore.aaa.radius.sessionx.conf.LocalSessionManagerConfig;
import com.elitecore.aaa.radius.sessionx.conf.LocalSessionManagerData;
import com.elitecore.aaa.radius.sessionx.conf.SessionManagerData;
import com.elitecore.aaa.radius.sessionx.conf.impl.LocalSessionManagerConfiguration.DBFailureActions;
import com.elitecore.aaa.radius.sessionx.conf.impl.LocalSessionManagerConfiguration.DBFailureActions.DBFailureActionsAdapter;
import com.elitecore.core.commons.config.core.UserDefined;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.serverx.sessionx.FieldMapping;
import com.elitecore.core.serverx.sessionx.impl.FieldMappingImpl;


@XmlType(propOrder = {})
public class LocalSessionManager implements LocalSessionManagerData, UserDefined {
	
	private String instanceId;
	private String type = SessionManagerData.TYPE_LOCAL;
	private String instanceName;
	private String instanceDesc;

	//session manager batch update properties
	private boolean isBatchUpdateEnable=true;
	private int batchSize=1000;
	private int batchUpdateInterval = 100;
	private int batchUpdateDBQueryTimeout = 2;
	
	// smconfig related properties (for remote)
	private String smConfigId;	
	private String databaseDsId;
	private String tableName ="tblmconcurrentusers";
	private boolean counterEnable;	
	private boolean autoSessionCloser=false;
	private long sessionTimeout=2;
	private long closeBatchCount=50;
	private long sessionThreadSleepTime=10;		
	private long statusDuration=2;
	private long expiryReqLimitCount=10;
	private int sessionCloseAction =3;
	private String identityFeild ="USER_IDENTITY";
	private String idSequenceName ="SEQ_TBLMCONCURRENTUSERS";
	private String startTimeFeild ="START_TIME";
	private String lastUpdateTimeFeild ="LAST_UPDATED_TIME";
	
	/*
	 * This variable is for search columns based on which the session is to be searched.
	 * The values can be (,) comma separated.
	 */
	private String searchCols;	
	/*
	 * This variable is for the session manager behavior mode
	 * 1 - Classic (Accounting) - session is created during the accounting request
	 * 2 - Authentication - the session is created during the auth request 
	 */
	/*narendra.pathai
	 *these attributes have been added by me for the new configuration changes as
	 *for creating the session during the Auth Request
	 */ 
	
	private int behaviourType =1;
	/*narendra.pathai 
	 *JIRA-1597, introducing the concept of Session Override action which is helpful in the 
	 *case when the Accounting Stop for some session is lost and not received at the AAA
	 *side so the session would be existing on the AAA side. So when other request will come
	 *for the same user then due to concurrency check the ACCESS_REJECT will be sent.
	 *So if this property is set to any of Action for session close action. So that action will be performed 
	 *while checking for concurrency.
	 *Suppose Action configured is DM and STOP so when the condition for concurrency failure is reached
	 *then as per the configuration the DM message is sent to the configured NAS external systems.
	 *If ACK is received which means that the session is existing at the NAS and it itself will generate STOP
	 *so we send a ACCESS_REJECT. But if NAK with SessionContextNotFound Error cause is found then this means 
	 *that no session exists at NAS side and the session is stale, so we delete the session and allow the new 
	 *request and send ACCESS_ACCEPT. For further information on the behavior see class ConcurrencySessionManager 
	 */
	
	
	
	private int sessionOverrideAction;

	/*narendra.pathai
	 * JIRA-491, introduced the configuration for Session Override COLUMNS.
	 * The use of this configuration is that when the session override action is provided
	 * , we check the concurrency based on Concurrent login policy and if concurrency fails
	 * and we need to send DM or STOP to the ESI, this will be used to disconnect only the 
	 * particular session with value of this column.
	 * 
	 */
	private String sessionOverrideColumns;
	private List<String> esiRelations = new ArrayList<String>();
	private List<String> nakEsiList = new ArrayList<String>();
	private List<FieldMappingImpl> fieldMappings = new ArrayList<FieldMappingImpl>();
	private DBFailureActions dbFailureAction;
	private String operation;
	private String concurrencyIdentityField = LocalSessionManagerData.GROUPNAME_FIELD;
	
	public LocalSessionManager(){
		//required by Jaxb.
	}
	
	public LocalSessionManager(String instanceId) {
		this.instanceId = instanceId;
		this.type = SessionManagerData.TYPE_LOCAL;
	}
	
	@XmlElement(name = "instance-id",type = String.class)
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	@XmlElement(name = "type", defaultValue =SessionManagerData.TYPE_LOCAL, type = String.class)
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	@XmlElement(name = "instance-name", type = String.class)
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	@XmlTransient
	public String getInstanceDesc() {
		return instanceDesc;
	}
	public void setInstanceDesc(String instanceDesc) {
		this.instanceDesc = instanceDesc;
	}
	
	@XmlElement(name = "batchupdate-enable", defaultValue ="true", type = boolean.class)
	public boolean getIsBatchUpdateEnable() {
		return isBatchUpdateEnable;
	}
	public void setIsBatchUpdateEnable(boolean isBatchUpdateEnable) {
		this.isBatchUpdateEnable = isBatchUpdateEnable;
	}
	@XmlElement(name = "batch-size", defaultValue ="1000", type = int.class)
	public int getBatchSize() {
		return this.batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
		
	}	
	@XmlElement(name = "batch-interval", defaultValue ="100", type = int.class)
	public int getBatchUpdateInterval() {
		return this.batchUpdateInterval;
	}
	public void setBatchUpdateInterval(int batchUpdateInterval) {
		this.batchUpdateInterval = batchUpdateInterval;
	}
	@XmlElement(name = "query-timeout-in-secs", defaultValue ="2", type = int.class)
	public int getQueryTimeoutInSecs() {
		return this.batchUpdateDBQueryTimeout;
	}
	public void setQueryTimeoutInSecs(int batchUpdateDBQueryTimeout) {
		this.batchUpdateDBQueryTimeout = batchUpdateDBQueryTimeout;
	}
	@XmlElement(name = "config-id", type = String.class)
	public String getSmConfigId() {
		return smConfigId;
	}
	public void setSmConfigId(String smConfigId) {
		this.smConfigId = smConfigId;
	}
	@XmlElement(name = "datasource-id", type = String.class)
	public String getDatabaseDsId() {
		return databaseDsId;
	}
	public void setDatabaseDsId(String databaseDsId) {
		this.databaseDsId = databaseDsId;
	}
	@XmlElement(name = "table-name", defaultValue ="tblmconcurrentusers", type = String.class)
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	@XmlElement(name = "counter-enable",type = boolean.class)
	public boolean getCounterEnable() {
		return counterEnable;
	}
	public void setCounterEnable(boolean counterEnable) {
		this.counterEnable = counterEnable;
	}
	
	
	@XmlElement(name = "autosession-closure", defaultValue = "false", type = boolean.class)
	public boolean getAutoSessionCloser() {
		return this.autoSessionCloser;
	}
	public void setAutoSessionCloser(boolean autoSessionCloser) {
		this.autoSessionCloser = autoSessionCloser;
	}
	
	@XmlElement(name = "session-timeout", defaultValue ="2", type = long.class)
	public long getSessionTimeout() {
		return this.sessionTimeout;
	}
	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	
	@XmlElement(name = "session-close-batch-count", defaultValue ="50", type = long.class)
	public long getCloseBatchCount() {
		return this.closeBatchCount;
	}
	public void setCloseBatchCount(long closeBatchCount) {
		this.closeBatchCount = closeBatchCount;
	}
	
	@XmlElement(name = "session-thread-sleep-time", defaultValue ="10", type = long.class)
	public long getSessionThreadSleepTime() {
		return this.sessionThreadSleepTime;
	}
	public void setSessionThreadSleepTime(long sessionThreadSleepTime) {
		this.sessionThreadSleepTime = sessionThreadSleepTime;
	}
	
	@XmlElement(name = "status-duration", defaultValue ="2", type = long.class)
	public long getStatusDuration() {
		return this.statusDuration;
	}
	public void setStatusDuration(long statusDuration) {
		this.statusDuration = statusDuration;
	}
	@XmlElement(name = "expiry-count", defaultValue ="10", type = long.class)
	public long getExpiryReqLimitCount() {
		return expiryReqLimitCount;
	}
	public void setExpiryReqLimitCount(long expiryReqLimitCount) {
		this.expiryReqLimitCount = expiryReqLimitCount;
	}
	
	@XmlElement(name = "session-close-action", defaultValue ="3", type = int.class )
	public int getSessionCloseAction() {
		return this.sessionCloseAction;
	}
	public void setSessionCloseAction(int sessionCloseAction) {
		this.sessionCloseAction = sessionCloseAction;
	}
	@XmlTransient
	public String getIdentityFeild() {
		return identityFeild;
	}
	public void setIdentityFeild(String identityFeild) {
		this.identityFeild = identityFeild;
	}
	@XmlElement(name = "sequence-name", defaultValue ="SEQ_TBLMCONCURRENTUSERS", type = String.class)
	public String getIdSequenceName() {
		return idSequenceName;
	}
	public void setIdSequenceName(String idSequenceName) {
		this.idSequenceName = idSequenceName;
	}
	@XmlElement(name = "start-time-field", defaultValue ="START_TIME", type = String.class)
	public String getStartTimeFeild() {
		return startTimeFeild;
	}
	public void setStartTimeFeild(String startTimeFeild) {
		this.startTimeFeild = startTimeFeild;
	}
	@XmlElement(name = "last-update-time", defaultValue ="LAST_UPDATED_TIME", type = String.class)
	public String getLastUpdateTimeFeild() {
		return lastUpdateTimeFeild;
	}
	
	public void setLastUpdateTimeFeild(String lastUpdateTimeFeild) {
		this.lastUpdateTimeFeild = lastUpdateTimeFeild;
	}
	
	@XmlElement(name = "search-cols", type = String.class )
	public String getSearchCols() {
		return searchCols;
	}
	public void setSearchCols(String searchCols) {
		this.searchCols = searchCols;
	}
	@XmlElement(name = "behaviour-type", defaultValue ="1", type = int.class)
	public int getBehaviourType() {
		return behaviourType;
	}
	public void setBehaviourType(int behaviourType) {
		this.behaviourType = behaviourType;
	}
	
	@XmlElement(name = "session-overide-action", type = int.class)
	public int getSessionOverrideAction() {
		return this.sessionOverrideAction;
	}
	public void setSessionOverrideAction(int sessionOverrideAction) {
		this.sessionOverrideAction = sessionOverrideAction;
	}
	
	@XmlElement(name = "session-override-columns", type = String.class)
	public String getSessionOverrideColumns() {
		return this.sessionOverrideColumns;
	}
	public void setSessionOverrideColumns(String sessionOverrideColumns) {
		this.sessionOverrideColumns = sessionOverrideColumns;
	}
	
	public void setEsiRelations(List<String> esiRelations) {
		this.esiRelations = esiRelations;
	}
	
	public void setNakEsiList(List<String> nakEsiList) {
		this.nakEsiList = nakEsiList;
	}
	
	public void setFieldMappings(List<FieldMappingImpl> fieldMappings) {
		this.fieldMappings = fieldMappings;
	}
	
	@XmlElementWrapper(name = "esi-list")
	@XmlElement(name = "esi")
	public List<String> getEsiRelations() {
		return esiRelations;
	}
	
	@XmlElementWrapper(name = "nak-esi-list")
	@XmlElement(name = "nak-esi")
	public List<String> getNakEsiList() {
		return nakEsiList;
	}

	
	@XmlElementWrapper(name = "field-mappings")
	@XmlElement(name = "field-mapping")
	public List<FieldMappingImpl> getFieldMappings() {
		return fieldMappings;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof LocalSessionManager))
			return false;
		
		LocalSessionManager other = (LocalSessionManager) obj;
		return this.instanceId == other.instanceId;
	}

	@Override
	@XmlJavaTypeAdapter(value = DBFailureActionsAdapter.class)
	@XmlElement(name = "failure-action", defaultValue ="IGNORE", type = String.class)
	public DBFailureActions getDBFailureAction() {
		return this.dbFailureAction;
	}
	
	public void setDBFailureAction(DBFailureActions dbFailureAction) {
		this.dbFailureAction = dbFailureAction;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println(StringUtility.fillChar("-", 80, '-'));
		out.println(StringUtility.fillChar(" ", 15) + "Basic Configuration : "
				+ getInstanceName());
		out.println(StringUtility.fillChar("-", 80, '-'));
		out.println(StringUtility.fillChar("SESSION MANAGER", 45) + " : "
				+ getInstanceName());
		out.println(StringUtility.fillChar("Type", 45) + " : " + getType());
		out.println(StringUtility.fillChar("Behaviour", 45)
				+ " : "
				+ LocalSessionManagerConfig.BehaviourType
						.getName(getBehaviourType()));
		out.println(StringUtility.fillChar("DB failure action", 45)
				+ " : "
				+ getDBFailureAction().name());
		out.println(StringUtility.fillChar("Batch Update Enabled", 45) + " : "
				+ getIsBatchUpdateEnable());
		out.println(StringUtility.fillChar("Batch Size", 45) + " : "
				+ getBatchSize());
		out.println(StringUtility.fillChar(
				"Batch Update Interval (in Seconds)", 45)
				+ " : "
				+ getBatchUpdateInterval());
		out.println(StringUtility.fillChar("DB Timeout (in Seconds)", 45)
				+ " : " + getQueryTimeoutInSecs());
		out.println(StringUtility.fillChar("AutoSession Closer Enabled ", 45)
				+ " : " + getAutoSessionCloser());
		out.println(StringUtility.fillChar("Closed Batch", 45) + " : "
				+ getCloseBatchCount());
		out.println(StringUtility.fillChar("Session Timeout (in minutes)", 45)
				+ " : " + getSessionTimeout());
		out.println(StringUtility.fillChar(
				"Session Thread Sleep Time (in Seconds)", 45)
				+ " : "
				+ getSessionThreadSleepTime());
		out.println(StringUtility.fillChar("Session Close Action", 45)
				+ " : " + getSessionCloseAction());
		out.println(StringUtility.fillChar("Session Override Action", 45) + " : "
				+ getSessionOverrideAction());
		out.println(StringUtility.fillChar("Session Override Column", 45) + " : "
				+ getSessionOverrideColumns());
		out.println(StringUtility.fillChar("Session Stop Action", 45) + " : "
				+ getActionOnStop());
		out.println(StringUtility.fillChar("Concurrency Identity Field", 45) + " : "
				+ getConcurrencyIdentityField());
		out.println(StringUtility.fillChar("Additional DB Field Mapping", 45)
				+ " : ");
		out.println(StringUtility.fillChar(".", 80, '.'));
		out.println(StringUtility.fillChar(" DBFIELD_NAME", 30) + "|"
				+ StringUtility.fillChar(" REFERRING_ATTRIBUTE", 25) + "|"
				+ StringUtility.fillChar(" DATA_TYPE", 5));
		out.println(StringUtility.fillChar(".", 80, '.'));
		List<FieldMappingImpl> fieldMappingList = getFieldMappings();
		if (fieldMappingList == null || fieldMappingList.isEmpty()) {
			out.println(" No Additional DB field Mappings");
		} else {
			for (FieldMapping field : fieldMappingList) {
				out.print(StringUtility.fillChar(" " + field.getColumnName(),
						30) + "|");
				out.print(StringUtility.fillChar(" " + field.getPropertyName(),
						25) + "|");
				out.println(StringUtility.fillChar(" " + field.getType(), 5));
			}
		}
		out.println(StringUtility.fillChar("-", 80, '-'));
		out.flush();
		out.close();
		return stringBuffer.toString();
	}

	@Override
	@XmlElement(name = "action-on-stop", defaultValue = LocalSessionManagerData.SESSION_STOP_ACTION_DELETE, type = String.class)
	public String getActionOnStop() {
		return this.operation;
	}
	
	public void setActionOnStop(String operation) {
		this.operation = operation;
	}

	@Override
	@XmlElement(name = "concurrency-identity-field", defaultValue = LocalSessionManagerData.GROUPNAME_FIELD, type = String.class)
	public String getConcurrencyIdentityField() {
		return this.concurrencyIdentityField;
	}
	
	public void setConcurrencyIdentityField(String concurrencyIdentityField) {
		this.concurrencyIdentityField = concurrencyIdentityField;
	}
}