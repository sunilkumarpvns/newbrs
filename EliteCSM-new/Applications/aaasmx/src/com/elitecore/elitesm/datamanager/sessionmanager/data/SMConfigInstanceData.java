package com.elitecore.elitesm.datamanager.sessionmanager.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import com.elitecore.aaa.util.constants.SessionManagerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.DatabaseDatasourceNameAdapter;
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.adapter.sessionmanager.BehaviorAdapter;
import com.elitecore.elitesm.ws.rest.adapter.sessionmanager.SessionOverrideActionAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.IsNumeric;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@XmlType(propOrder = {"name","description","databaseDatasourceId","tablename","startTimeField","lastUpdatedTimeField","dbQueryTimeOut","idSequenceName",
		"behaviour","dbfailureaction","sessionStopAction","batchUpdateEnabled","batchSize","batchUpdateInterval","autoSessionCloser","sessiontimeout",
		"closeBatchCount","sessionThreadSleepTime","sessionCloseAction","sessionOverrideAction","sessionOverrideColumn","concurrencyIdentityField","searchAttribute",
		"identityField","sessionManagerESIServerData","lstMandatoryFieldMapData","dbFieldMapDataList"})
@XmlRootElement(name="session-manager")
@ValidObject
public class SMConfigInstanceData extends BaseData implements ISMConfigInstanceData,Differentiable,Validator {
	
	private static final String ACCOUNTING_SERVER = "Accounting Server";
	private static final String NAS_CLIENT = "NAS Client";

	public SMConfigInstanceData() {
		description = RestUtitlity.getDefaultDescription();
		this.autoSessionCloser = SessionManagerConstants.AUTO_SESSION_CLOSE_ENABLED;
		this.sessionIdRefEntity = SessionManagerConstants.SESSION_ID_REF_ENTITY;
		this.groupNameField = SessionManagerConstants.GROUPNAME_FIELD;
		this.serviceTypeField = SessionManagerConstants.SERVICE_TYPE_FIELD;
		this.concurrencyIdentityField = SessionManagerConstants.CONCURRENCY_IDENTITY_FIELD;
		this.sessionOverrideColumn = SessionManagerConstants.SESSION_OVERRIDE_FIELD;
	}

	private String smConfigId;
	private String smInstanceId;

	@Expose
	@SerializedName("Datasource")
	private String databaseDatasourceId;

	@Expose
	@SerializedName("Table Name")
	@NotEmpty(message="Table name must be specified.")
	private String tablename;

	@Expose
	@SerializedName("Start Time Field")
	@NotEmpty(message="Start Time Field must be specified")
	private String startTimeField;

	@Expose
	@SerializedName("Last Update Time Field")
	@NotEmpty(message="Last Update Time Field must be specified")
	private String lastUpdatedTimeField;

	@Expose
	@SerializedName("Sequence Name")
	@NotEmpty(message="Sequence Name must be specified")
	private String idSequenceName;

	@Expose
	@SerializedName("Behavior")
	@NotNull(message = "Behaviour name must be specified")
	private Integer behaviour;

	@Expose
	@SerializedName("DB Failure Action")
	@NotEmpty(message = "DB Failure Action must be specified")
	@Pattern(regexp = "IGNORE|REJECT|DROP", message = "Invalid DB Failure Action. It can be IGNORE, REJECT or DROP.")
	private String dbfailureaction;

	@Expose
	@SerializedName("Session Stop Action")
	@NotEmpty(message = "Session Stop Action must be specified")
	@Pattern(regexp = "DELETE|UPDATE", message = "Invalid Session Stop Action. It can be DELETE or UPDATE.")
	private String sessionStopAction;

	@NotEmpty(message = "Auto Session Closer must be specified")
	@Pattern(regexp = "true|false", message = "Invalid value of Auto Session Closer. Value could be 'true' or 'false'.")
	private String autoSessionCloser;

	private Long sessiontimeout;

	private Long closeBatchCount;

	private Long sessionThreadSleepTime;

	private Integer sessionCloseAction;
	private String identityField;

	private String sessionIdField;
	private String sessionIdRefEntity;
	private String groupNameField;
	private String serviceTypeField;
	
	@NotEmpty(message = "Concurrency Identity Field must be specified.")
	private String concurrencyIdentityField;
	private String searchAttribute;

	@NotEmpty(message = "Batch Update Enabled must be specified")
	@Pattern(regexp = "true|false", message = "Invalid value of Batch Update Enabled. Value could be 'true' or 'false'.")
	private String batchUpdateEnabled;
	private Integer batchUpdateInterval;
	private Integer batchSize;

	@NotNull(message = "DB Query TimeOut must be specified.")
	private Integer dbQueryTimeOut;

	@NotNull(message = "Session Override Action must be specified")
	private Integer sessionOverrideAction;
	private String sessionOverrideColumn;
	
	@Valid
	private List<SMDBFieldMapData> dbFieldMapDataList;
	
	@Valid
	private List<SMDBFieldMapData> lstMandatoryFieldMapData = new ArrayList<SMDBFieldMapData>();

	@Valid
	private List<SMSessionCloserESIRelData> smSessionCloserESIRelDataList = new ArrayList<SMSessionCloserESIRelData>();
	private SessionManagerESIServerData sessionManagerESIServerData;

	// for rest web service use only
	private String name;
	private String description;

	@XmlElement(name="behaviour")
	@Min(value = 1 , message = "Behaviour name must be valid")
	@XmlJavaTypeAdapter(value = BehaviorAdapter.class)
	public Integer getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(Integer behaviour) {
		this.behaviour = behaviour;
	}

	@XmlElement(name="batch-update-enabled")
	@XmlJavaTypeAdapter(value = LowerCaseConvertAdapter.class)
	public String getBatchUpdateEnabled() {
		return batchUpdateEnabled;
	}

	public void setBatchUpdateEnabled(String batchUpdateEnabled) {
		this.batchUpdateEnabled = batchUpdateEnabled;
	}

	@XmlElement(name="batch-size")
	@IsNumeric(message = "Batch Size must be Numeric.", isAllowBlank = true)
	@Range(min = 1, max = 3000 , message = "Batch Size must be between 1 and 3000")
	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	@XmlElement(name="batch-update-interval")
	@Min(value = 0 ,message = "Batch Update Interval value must be zero or positive number.")
	public Integer getBatchUpdateInterval() {
		return batchUpdateInterval;
	}

	public void setBatchUpdateInterval(Integer batchUpdateInterval) {
		this.batchUpdateInterval = batchUpdateInterval;
	}

	@XmlElement(name="db-query-timeout")
	@Min(value = 0 , message = "DB Query Timeout value must be zero or positive number.")
	public Integer getDbQueryTimeOut() {
		return dbQueryTimeOut;
	}

	public void setDbQueryTimeOut(Integer dbQueryTimeOut) {
		this.dbQueryTimeOut = dbQueryTimeOut;
	}

	@XmlElement(name="discrete-search-fields")
	public String getSearchAttribute() {
		return searchAttribute;
	}

	public void setSearchAttribute(String searchAttributeField) {
		this.searchAttribute = searchAttributeField;
	}

	@XmlTransient
	public String getSmConfigId() {
		return smConfigId;
	}
	public void setSmConfigId(String smConfigId) {
		this.smConfigId = smConfigId;
	}

	@XmlTransient
	public String getSmInstanceId() {
		return smInstanceId;
	}
	public void setSmInstanceId(String smInstanceId) {
		this.smInstanceId = smInstanceId;
	}

	@XmlElement(name = "database-datasource")
	@NotEmpty(message = "Database Name must be specified")
	@XmlJavaTypeAdapter(value = DatabaseDatasourceNameAdapter.class)
	public String getDatabaseDatasourceId() {
		return databaseDatasourceId;
	}
	public void setDatabaseDatasourceId(String databaseDatasourceId) {
		this.databaseDatasourceId = databaseDatasourceId;
	}

	@XmlElement(name="table-name")
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	@XmlElement(name="auto-session-closer-enabled")
	public String getAutoSessionCloser() {
		return autoSessionCloser;
	}
	public void setAutoSessionCloser(String autoSessionCloser) {
		this.autoSessionCloser = autoSessionCloser;
	}

	@XmlElement(name="session-timeout")
	@Min(value = 0 ,message = "session timeout value must be zero or positive number.")
	public Long getSessiontimeout() {
		return sessiontimeout;
	}
	public void setSessiontimeout(Long sessiontimeout) {
		this.sessiontimeout = sessiontimeout;
	}

	@XmlElement(name="session-close-batch-count")
	@Min(value = 0 ,message = "session close batch count value must be zero or positive number.")
	public Long getCloseBatchCount() {
		return closeBatchCount;
	}
	public void setCloseBatchCount(Long closeBatchCount) {
		this.closeBatchCount = closeBatchCount;
	}

	@XmlElement(name="session-thread-sleep-time")
	@Min(value = 0 ,message = "session Thread Sleep Time value must be zero or positive number")
	public Long getSessionThreadSleepTime() {
		return sessionThreadSleepTime;
	}
	public void setSessionThreadSleepTime(Long sessionThreadSleepTime) {
		this.sessionThreadSleepTime = sessionThreadSleepTime;
	}

	@XmlElement(name="session-close-action")
	@XmlJavaTypeAdapter(value = SessionOverrideActionAdapter.class)
	public Integer getSessionCloseAction() {
		return sessionCloseAction;
	}
	public void setSessionCloseAction(Integer sessionCloseAction) {
		this.sessionCloseAction = sessionCloseAction;
	}

	@XmlElement(name="identity-field")
	public String getIdentityField() {
		return identityField;
	}
	public void setIdentityField(String IdentityField) {
		this.identityField = IdentityField;
	}
	
	@XmlElement(name="sequence-name")
	public String getIdSequenceName() {
		return idSequenceName;
	}
	public void setIdSequenceName(String IdSequenceName) {
		this.idSequenceName = IdSequenceName;
	}

	@XmlElement(name="start-time-field")
	public String getStartTimeField() {
		return startTimeField;
	}
	public void setStartTimeField(String startTimeField) {
		this.startTimeField = startTimeField;	
	}

	@XmlElement(name="last-updated-time-field")
	public String getLastUpdatedTimeField() {
		return lastUpdatedTimeField;
	}
	public void setLastUpdatedTimeField(String lastUpdatedTimeField) {
		this.lastUpdatedTimeField = lastUpdatedTimeField;
	}

	@XmlTransient
	public String getSessionIdField() {
		return sessionIdField;
	}
	public void setSessionIdField(String sessionIdField) {
		this.sessionIdField = sessionIdField;
	}

	@XmlTransient
	public String getSessionIdRefEntity() {
		return sessionIdRefEntity;
	}
	public void setSessionIdRefEntity(String sessionIdRefEntity) {
		this.sessionIdRefEntity = sessionIdRefEntity;
	}

	@XmlElementWrapper(name = "additional-db-field-mappings")
	@XmlElement(name="additional-db-field-mapping-data")
	public List<SMDBFieldMapData> getDbFieldMapDataList() {
		return dbFieldMapDataList;
	}
	public void setDbFieldMapDataList(List<SMDBFieldMapData> dbFieldMapDataList) {
		this.dbFieldMapDataList = dbFieldMapDataList;
	}

	@XmlTransient
	public String getGroupNameField() {
		return groupNameField;
	}
	public void setGroupNameField(String groupNameField) {
		this.groupNameField = groupNameField;
	}

	@XmlTransient
	public String getServiceTypeField() {
		return serviceTypeField;
	}
	public void setServiceTypeField(String serviceTypeField) {
		this.serviceTypeField = serviceTypeField;
	}

	@XmlTransient
	public List<SMSessionCloserESIRelData> getSmSessionCloserESIRelDataList() {
		return smSessionCloserESIRelDataList;
	}

	public void setSmSessionCloserESIRelDataList(List<SMSessionCloserESIRelData> smSessionCloserESIRelDataList) {
		this.smSessionCloserESIRelDataList = smSessionCloserESIRelDataList;
	}

	@XmlElement(name="session-override-action")
	@Min(value = 0, message = "Session Override Action must be valid")
	@XmlJavaTypeAdapter(value = SessionOverrideActionAdapter.class)
	public Integer getSessionOverrideAction() {
		return sessionOverrideAction;
	}

	public void setSessionOverrideAction(Integer sessionOverrideAction) {
		this.sessionOverrideAction = sessionOverrideAction;
	}

	@XmlElement(name="session-override-fields")
	public String getSessionOverrideColumn() {
		return sessionOverrideColumn;
	}

	public void setSessionOverrideColumn(String sessionOverrideColumn) {
		this.sessionOverrideColumn = sessionOverrideColumn;
	}

	@Override
	@XmlElementWrapper(name = "mandatory-db-field-mappings")
	@XmlElement(name="mandatory-field-mapping-data")
	public List<SMDBFieldMapData> getLstMandatoryFieldMapData() {
		return lstMandatoryFieldMapData;
	}

	@Override
	public void setLstMandatoryFieldMapData(List<SMDBFieldMapData> lstMandatoryFieldMapData) {
		this.lstMandatoryFieldMapData=lstMandatoryFieldMapData;
	}

	@Override
	@XmlElement(name="db-failure-action")
	public String getDbfailureaction() {
		return dbfailureaction;
	}

	@Override
	public void setDbfailureaction(String dbfailureaction) {
		this.dbfailureaction = dbfailureaction;
	}

	@XmlElement(name="session-stop-action")
	public String getSessionStopAction() {
		return sessionStopAction;
	}

	public void setSessionStopAction(String sessionStopAction) {
		this.sessionStopAction = sessionStopAction;
	}

	@XmlElement(name="concurrency-identity-field")
	public String getConcurrencyIdentityField() {
		return concurrencyIdentityField;
	}

	public void setConcurrencyIdentityField(String concurrencyIdentityField) {
		this.concurrencyIdentityField = concurrencyIdentityField;
	}

	@XmlElement(name = "esi")
	public SessionManagerESIServerData getSessionManagerESIServerData() {
		return sessionManagerESIServerData;
	}

	public void setSessionManagerESIServerData(
			SessionManagerESIServerData sessionManagerESIServerData) {
		this.sessionManagerESIServerData = sessionManagerESIServerData;
	}

	// properties for rest web service purpose
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Datasource", EliteSMReferencialDAO.fetchDatabaseDatasourceData(databaseDatasourceId));
		object.put("Table Name", tablename);
		object.put("Auto Sessoin Closer Enabled ", autoSessionCloser);
		object.put("Session Timeout(sec)", sessiontimeout);
		object.put("Session Close Batch Count", closeBatchCount);
		object.put("Session Thread Sleep Time(sec)", sessionThreadSleepTime);
		object.put("Session Stop Action", (sessionStopAction.equals(ConfigConstant.DELETE) ? "Delete(Default)" : "Update"));

		if(sessionCloseAction != null){
			if (sessionCloseAction.equals(RadiusConstants.SESSION_CLOSE_ACTION_NONE)) {
				object.put("Session Close Action", "None");
			} else if (sessionCloseAction.equals(RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_DISCONNECT)) {
				object.put("Session Close Action", "Generate Disconnect");
			} else if (sessionCloseAction.equals(RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_STOP)) {
				object.put("Session Close Action", "Generate Stop");
			} else if (sessionCloseAction.equals(RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_DM_AND_STOP)) {
				object.put("Session Close Action", "Generate Disconnect and Stop");
			}
		}

		object.put("Sequence Name", idSequenceName);
		object.put("User Identity ", identityField);
		object.put("Start Time Field", startTimeField);
		object.put("Session ID", sessionIdField);
		object.put("Service Type", serviceTypeField);
		object.put("Concurrency Identity Field", concurrencyIdentityField);
		object.put("Discrete Search Fields", searchAttribute);
		object.put("Update Interval(sec)", batchUpdateInterval);
		object.put("Enabled", batchUpdateEnabled);
		object.put("Batch Size", batchSize);
		object.put("DB Query TimeOut(sec)", dbQueryTimeOut);
		object.put("Behaviour", ((behaviour == 1) ? "Acct" : "Auth"));

		if(dbfailureaction.equals(ConfigConstant.IGNORE)){
			object.put("DB Failure Action", "Ignore(Default)");
		} else if (dbfailureaction.equals(ConfigConstant.REJECT)) {
			object.put("DB Failure Action", "Reject");
		} else if (dbfailureaction.equals(ConfigConstant.DROP)){
			object.put("DB Failure Action", "Drop");
		}

		if (sessionOverrideAction != null) {
			if (sessionOverrideAction.equals(RadiusConstants.SESSION_OVERRIDE_ACTION_NONE)) {
				object.put("Session Override Action", "None");
			} else if (sessionOverrideAction.equals(RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_DISCONNECT)) {
				object.put("Session Override Action", "Generate Disconnect");
			} else if (sessionOverrideAction.equals(RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_STOP)) {
				object.put("Session Override Action", "Generate Stop");
			} else if (sessionOverrideAction.equals(RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_DM_AND_STOP)) {
				object.put("Session Override Action","Generate Disconnect and Stop");
			}
		}

		object.put("Session Override Fields", sessionOverrideColumn);
		object.put("Last Update Time Field", lastUpdatedTimeField);

		if (Collectionz.isNullOrEmpty(dbFieldMapDataList) == false) {
			JSONArray array = new JSONArray();
			for (SMDBFieldMapData element : dbFieldMapDataList) {
				if(element.getField() == null){
					array.add(element.toJson());
				}
			}
			object.put("Additional DB Field Mappings ", array);
		}

		if (Collectionz.isNullOrEmpty(lstMandatoryFieldMapData) == false) {
			JSONArray array = new JSONArray();
			for (SMDBFieldMapData element : lstMandatoryFieldMapData) {
				if(element.getField() != null){
					array.add(element.toJson());
				}
			}
			object.put("Mandatory DB Field Mappings", array); 
		}

		if (Collectionz.isNullOrEmpty(smSessionCloserESIRelDataList) == false) {
			JSONArray array = new JSONArray();
			for (SMSessionCloserESIRelData element : smSessionCloserESIRelDataList) {
				array.add(element.toJson());
			}
			object.put("ESI", array);
		}
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		boolean isRestCall = false;
		Set<String> dbFieldList = new TreeSet<String>();
		Set<String> validateMandatoryalFieldList = new TreeSet<String>();
		Set<String> validateAdditionalFieldList = new TreeSet<String>();
		
		try {
			
			if(RestValidationMessages.INVALID.equals(this.databaseDatasourceId)){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Database Name must be valid");
			}

			if(batchUpdateEnabled.equalsIgnoreCase("true")){
				//validate Batch Update Interval Enabled/Disabled
				if( batchSize == null ){
					RestUtitlity.setValidationMessage(context, "Batch Update Size must be specified");
					isValid = false;
				} 
				
				if( batchUpdateInterval == null ){
					RestUtitlity.setValidationMessage(context, "Batch Update Interval must be specified");
					isValid = false;
				}
			} else {
				if( batchSize == null ){
					this.batchSize = SessionManagerConstants.BATCH_SIZE;
				}
				if( batchUpdateInterval == null ){
					this.batchUpdateInterval = SessionManagerConstants.BATCH_UPDATE_INTERVAL;
				}
			}
			if(autoSessionCloser.equalsIgnoreCase("true")){
				//validate Auto Session Closer Enabled/Disabled
				if ( sessiontimeout == null ){
					RestUtitlity.setValidationMessage(context, "Session Timeout must be specified with value greater then 0");
					isValid = false;
				} 
				if( closeBatchCount == null ){
					RestUtitlity.setValidationMessage(context, "Session Close Batch Count  must be specified with value greater then 0");
					isValid = false;
				}
				if( sessionThreadSleepTime == null ){
					RestUtitlity.setValidationMessage(context, "Session Thread Sleep Time must be specified with value greater then 0");
					isValid = false;
				}
				if( sessionCloseAction == null ){
					RestUtitlity.setValidationMessage(context, "Session Close Action must be specified");
					isValid = false;
				} else if( sessionCloseAction != null && sessionCloseAction == -1 ){
					RestUtitlity.setValidationMessage(context, "Session Close Action must be valid");
					isValid = false;
				}
			} else {
				if( sessiontimeout == null ){
					this.sessiontimeout = SessionManagerConstants.SESSION_TIMEOUT;
				}
				if( closeBatchCount == null ){
					this.closeBatchCount = SessionManagerConstants.SESSION_CLOSE_BATCH_COUNT;
				}
				if( sessionThreadSleepTime == null ){
					this.sessionThreadSleepTime = SessionManagerConstants.SESSION_THREAD_SLEEP_TIME;
				}
				if( sessionCloseAction == null || sessionCloseAction == -1 ){
					this.sessionCloseAction = SessionManagerConstants.SESSION_CLOSE_ACTION;
				}
			}
			
			//validate DB Field mapping lists
			if(Collectionz.isNullOrEmpty(lstMandatoryFieldMapData) == false){
				
				if( validateMandatoryDBFieldMappingFieldValues(context) == false){
					isValid = false; 
				}
				
				if( validateMandatoryDBFieldMappingFieldValues(context) == false){
					isValid = false;
				}
				
				if( validateDBFieldIsValidOrNot(context,lstMandatoryFieldMapData) == false){
					isValid = false;
				}

				for (SMDBFieldMapData smdbFieldMapData : lstMandatoryFieldMapData) {
					if(Strings.isNullOrEmpty(smdbFieldMapData.getDbFieldName()) == false){
						dbFieldList.add(smdbFieldMapData.getDbFieldName());
						validateMandatoryalFieldList.add(smdbFieldMapData.getDbFieldName());
					}
				}
			} else {
				if(validateMandatoryDBFieldMappingFieldValues(context) == false){
					isValid = false;
				}
				if(Collectionz.isNullOrEmpty(lstMandatoryFieldMapData)) {
					RestUtitlity.setValidationMessage(context, "Mandatory DB Field mapping must be required");
					isValid = false;
				}
			}

			if(Collectionz.isNullOrEmpty(dbFieldMapDataList) == false){

				if( validateDBFieldIsValidOrNot(context,dbFieldMapDataList) == false){
					isValid = false;
				}
				
				for (SMDBFieldMapData smdbFieldMapData : dbFieldMapDataList) {
					if(Strings.isNullOrEmpty(smdbFieldMapData.getDbFieldName()) == false){
						dbFieldList.add(smdbFieldMapData.getDbFieldName());
						validateAdditionalFieldList.add(smdbFieldMapData.getDbFieldName());
					}
				}
			}

			// validate AdditionalDBField not contain mandatory field values
			for (String fieldValue : validateMandatoryalFieldList) {
				if(validateAdditionalFieldList.contains(fieldValue)){
					RestUtitlity.setValidationMessage(context, "DB Field mapping " +fieldValue + " alerady configured");
					isValid = false;
				}
			}

			if(Collectionz.isNullOrEmpty(lstMandatoryFieldMapData) == false){
				for (SMDBFieldMapData mandatoryField : lstMandatoryFieldMapData) {
					if(Strings.isNullOrEmpty(mandatoryField.getDbFieldName()) == false){
						if(Strings.isNullOrEmpty(mandatoryField.getReferringEntity())){
							RestUtitlity.setValidationMessage(context, " Referring Attribute for " + mandatoryField.getDbFieldName() + " field must be specified");
							isValid = false;
						}
					}
					
					if(Strings.isNullOrEmpty(mandatoryField.getDbFieldName()) == false){
						if(mandatoryField.getDataType() == null){
							RestUtitlity.setValidationMessage(context, " Data Type for " + mandatoryField.getDbFieldName() + " field must be specified");
							isValid = false;
						} else if (-1 == mandatoryField.getDataType().intValue()){
							RestUtitlity.setValidationMessage(context, " Data Type for " + mandatoryField.getDbFieldName() + " field must be valid");
							isValid = false;
						}
					}
				}
			}
			
			if(Collectionz.isNullOrEmpty(dbFieldMapDataList) == false){
				for (SMDBFieldMapData additionalField : dbFieldMapDataList) {
					if(Strings.isNullOrEmpty(additionalField.getDbFieldName()) == false){
						if(Strings.isNullOrEmpty(additionalField.getReferringEntity())){
							RestUtitlity.setValidationMessage(context, " Referring Attribute for " + additionalField.getDbFieldName() + " field must be specified");
							isValid = false;
						}
					}
					
					if(Strings.isNullOrEmpty(additionalField.getDbFieldName()) == false){
						if(additionalField.getDataType() == null){
							RestUtitlity.setValidationMessage(context, " Data Type for " + additionalField.getDbFieldName() + " field must be specified");
							isValid = false;
						} else if (-1 == additionalField.getDataType().intValue()){
							RestUtitlity.setValidationMessage(context, " Data Type for " + additionalField.getDbFieldName() + " field must be valid");
							isValid = false;
						}
					}
				}
			}

			// validate Session Override Field 
			if(Strings.isNullOrEmpty(sessionOverrideColumn) == false){
				String fieldName = "Session Override Field ";
				if( validateFields(context,sessionOverrideColumn,dbFieldList,fieldName) == false){
					isValid = false;
				}
			}

			// validate Discrete Search Field
			if(Strings.isNullOrEmpty(searchAttribute) == false){
				String fieldName = "Discrete Search Field ";
				if( validateFields(context,searchAttribute,dbFieldList,fieldName) == false){
					isValid = false;
				}
			}
			
			// validate Concurrency Identity Field
			if(Strings.isNullOrEmpty(concurrencyIdentityField) == false){
				String fieldName = "Concurrency Identity Field ";
				
				String descreteSearchField [] = concurrencyIdentityField.split(",");
				for (String searchField : descreteSearchField) {
				 if("CONCUSERID".equalsIgnoreCase(searchField)){
						RestUtitlity.setValidationMessage(context, "Configured " + fieldName + searchField + " is not allowed.");
						isValid = false;
					} else if(dbFieldList.contains(searchField) == false && "GROUPNAME".equalsIgnoreCase(searchField)== false){
						RestUtitlity.setValidationMessage(context, "Configured " + fieldName + searchField + " must be mapped in DB Field Mapping List");
						isValid =  false;
					}
				}
			}

			//validate Start Time Field
			if(Strings.isNullOrEmpty(startTimeField)==false){
				if(dbFieldList.contains(startTimeField)){
					RestUtitlity.setValidationMessage(context, "Duplicate entries for value " + startTimeField + " in Start Time Field and DB Field Mapping's DBField Name");
					isValid = false;
				}
			}

			//validate Last Updated Time Field
			if(Strings.isNullOrEmpty(lastUpdatedTimeField)==false){
				if(dbFieldList.contains(lastUpdatedTimeField)){
					RestUtitlity.setValidationMessage(context, "Duplicate entries for value " + lastUpdatedTimeField + " in Last Update Time Field and DB Field Mapping's DBField Name");
					isValid = false;
				}
			}

			// validate Session Override Action
			List<SMSessionCloserESIRelData> nasEsiData= new ArrayList<SMSessionCloserESIRelData>();
			List<SMSessionCloserESIRelData> radAcctEsiData= new ArrayList<SMSessionCloserESIRelData>();
			if(Collectionz.isNullOrEmpty(smSessionCloserESIRelDataList)){
				List<List<SMSessionCloserESIRelData>> esiServerDatas = new ArrayList<List<SMSessionCloserESIRelData>>();
				if(sessionManagerESIServerData != null){
					isRestCall = true;
						radAcctEsiData = sessionManagerESIServerData.getAccountServerDataList();
						nasEsiData = sessionManagerESIServerData.getNasServerDataList();
						esiServerDatas.add(sessionManagerESIServerData.getAccountServerDataList());
						esiServerDatas.add(sessionManagerESIServerData.getNasServerDataList());

					for (List<SMSessionCloserESIRelData> esiServerDatalist : esiServerDatas) {
						for(SMSessionCloserESIRelData closerESIRelData : esiServerDatalist){
							smSessionCloserESIRelDataList.add(closerESIRelData);
						}
					}
				} 
			}
			
			if(sessionOverrideAction != null){
				boolean sessionOverrideActionVal = false;
				sessionOverrideActionVal = validateSessionActionns(sessionOverrideAction,isRestCall,radAcctEsiData,nasEsiData,context);
				if(sessionOverrideActionVal == false){
					isValid = false;
				}
			}
			
			if(sessionCloseAction != null){
				boolean sessionCloseActionVal = false;
				sessionCloseActionVal = validateSessionActionns(sessionCloseAction,isRestCall,radAcctEsiData,nasEsiData,context);
				if(sessionCloseActionVal == false){
					isValid = false;
				}
			}
			
			
			
		} catch (Exception e) {
			isValid = false;
		}
		return isValid;
	}

	private boolean validateFields(ConstraintValidatorContext context, String fieldValues, Set<String> dbFieldList, String fieldName){
		boolean isValid = true;
		String descreteSearchField [] = fieldValues.split(",");
		for (String searchField : descreteSearchField) {
			if((searchField.equalsIgnoreCase("GROUPNAME") || searchField.equalsIgnoreCase("CONCUSERID"))){
				RestUtitlity.setValidationMessage(context, "Configured " + fieldName + searchField + " is not allowed.");
				isValid = false;
			} else if(dbFieldList.contains(searchField) == false){
				RestUtitlity.setValidationMessage(context, "Configured " + fieldName + searchField + " must be mapped in DB Field Mapping List");
				isValid =  false;
			}
		}
		return isValid;
	}

	private boolean validateMandatoryDBFieldMappingFieldValues(ConstraintValidatorContext context) {
		boolean isValid = true;
		Set<String> mandatoryFields = new TreeSet<String>();
		List<String> mandatoryDBFieldMapping = new ArrayList<String>();
		List<SMDBFieldMapData> additionalDBFields = new ArrayList<SMDBFieldMapData>();

		if(Collectionz.isNullOrEmpty(dbFieldMapDataList) ==  false && Collectionz.isNullOrEmpty(lstMandatoryFieldMapData)){
			for (SMDBFieldMapData smdbFieldMapData : dbFieldMapDataList) {
				if(Strings.isNullOrEmpty(smdbFieldMapData.getField())== false){
					for(MandatoryDBFieldMapping value : MandatoryDBFieldMapping.values()) {
						if(smdbFieldMapData.getField().equalsIgnoreCase(value.getDBFieldMapping())){
							lstMandatoryFieldMapData.add(smdbFieldMapData);
						}
					}
				} else {
					additionalDBFields.add(smdbFieldMapData);
				}
			}
			dbFieldMapDataList = null;
			dbFieldMapDataList = new ArrayList<SMDBFieldMapData>();
			for (SMDBFieldMapData smdbFieldMapData : additionalDBFields) {
				dbFieldMapDataList.add(smdbFieldMapData);
			}
			if(Collectionz.isNullOrEmpty(lstMandatoryFieldMapData)){
				return false;
			}
		}
		
		for (SMDBFieldMapData smdbFieldMapData : lstMandatoryFieldMapData) {
			if(Strings.isNullOrEmpty(smdbFieldMapData.getField()) == false){
				mandatoryFields.add(smdbFieldMapData.getField());
			}
		}

		for(MandatoryDBFieldMapping value : MandatoryDBFieldMapping.values()) {
			if(mandatoryFields.contains(value.getDBFieldMapping()) == false){
				mandatoryDBFieldMapping.add(value.getDBFieldMapping());
				isValid = false;
			}
		}

		if(isValid == false){
			RestUtitlity.setValidationMessage(context, "Mandatory DB Field " + mandatoryDBFieldMapping + " required");
		}
		return isValid;
	}

	private boolean validateDBFieldIsValidOrNot(ConstraintValidatorContext context, List<SMDBFieldMapData> dbFieldMappingList) {
		boolean isValid = true;

		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
		Set<String> tableFieldList = null;
		Set<String> dbFieldMappingsNameSet = new TreeSet<String>();
		Set<String> duplicateDbFieldMappingsNameSet = new TreeSet<String>();
		try {
			tableFieldList = databaseDSBLManager.getTableFieldList(databaseDatasourceId,tablename);
			if(Collectionz.isNullOrEmpty(tableFieldList) == false){
				for (SMDBFieldMapData smdbFieldMapData : dbFieldMappingList) {
					if(Strings.isNullOrEmpty(smdbFieldMapData.getDbFieldName()) == false){
						if(tableFieldList.contains(smdbFieldMapData.getDbFieldName().toUpperCase()) == false){
							RestUtitlity.setValidationMessage(context," DB Field "+ smdbFieldMapData.getDbFieldName() +" must be valid");
							isValid = false;
						}

						if(smdbFieldMapData.getDbFieldName().toUpperCase().equalsIgnoreCase("GROUPNAME")){
							RestUtitlity.setValidationMessage(context," Configured attribute in DB Field Mappings [GROUPNAME] is not allowed");
							isValid = false;
						}

						if(smdbFieldMapData.getDbFieldName().toUpperCase().equalsIgnoreCase("CONCUSERID")){
							RestUtitlity.setValidationMessage(context," Configured attribute in DB Field Mappings [CONCUSERID] is not allowed");
							isValid = false;
						} 

						boolean isAdded =  dbFieldMappingsNameSet.add(smdbFieldMapData.getDbFieldName());

						if(isAdded ==  false){
							duplicateDbFieldMappingsNameSet.add(smdbFieldMapData.getDbFieldName());
						}
					}
				}
			}
			if(Collectionz.isNullOrEmpty(duplicateDbFieldMappingsNameSet) == false){
				RestUtitlity.setValidationMessage(context, " DB Field " + duplicateDbFieldMappingsNameSet + " duplicate ");
				isValid = false;
			}

		} catch (Exception e) {
			RestUtitlity.setValidationMessage(context, " Failed to validate DB Fields ");
			isValid = false;
		}
		return isValid;
	}
	
	private boolean validateSessionActionns(Integer sessionAction, boolean isRestCall, List<SMSessionCloserESIRelData> radAcctEsiData, List<SMSessionCloserESIRelData> nasEsiData, ConstraintValidatorContext context){
		boolean isValid =  true;
		 
		if (RadiusConstants.SESSION_OVERRIDE_ACTION_NONE == sessionAction.intValue()) {
			isValid = true;
		}else if (RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_DISCONNECT == sessionAction.intValue()) {
			if(Collectionz.isNullOrEmpty(smSessionCloserESIRelDataList) == false ){
				if(isRestCall){
					if(Collectionz.isNullOrEmpty(nasEsiData)){
						RestUtitlity.setValidationMessage(context, "NAS Client must be specified");
						return isValid = false;
					} 
				}
			} else {
				RestUtitlity.setValidationMessage(context, "NAS Client must be specified");
				return isValid = false;
			}
			
			if(validateConfiguredSessionOvverideAction(context,NAS_CLIENT,nasEsiData)){
				if(validateConfiguredSessionOvverideAction(context,ACCOUNTING_SERVER,radAcctEsiData)){
					return isValid = getESIServerData(context,NAS_CLIENT,nasEsiData);
				}else{
					isValid = false;
				}
			} else {
				isValid = false;
			}
			
			
		} else if (RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_STOP == sessionAction.intValue()) {
			if(Collectionz.isNullOrEmpty(smSessionCloserESIRelDataList) == false){
				if(isRestCall){
					if(Collectionz.isNullOrEmpty(radAcctEsiData)){
						RestUtitlity.setValidationMessage(context, "Accounting Server must be specified");
						return isValid = false;
					} 
				}
			} else {
				RestUtitlity.setValidationMessage(context, "Accounting Server must be specified");
				return isValid = false;
			}
			
			if(validateConfiguredSessionOvverideAction(context,ACCOUNTING_SERVER,radAcctEsiData)){
				if(validateConfiguredSessionOvverideAction(context,NAS_CLIENT,nasEsiData)){
					return isValid = getESIServerData(context,ACCOUNTING_SERVER,radAcctEsiData);
				}else{
					isValid = false;
				}
			} else {
				isValid = false;
			}
			
		} else if (RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_DM_AND_STOP == sessionAction.intValue()) {
			if(Collectionz.isNullOrEmpty(smSessionCloserESIRelDataList)){
				RestUtitlity.setValidationMessage(context, "Accounting Server and NAS Client not found");
				return isValid = false;
			}
			if(isRestCall){
				if(Collectionz.isNullOrEmpty(nasEsiData)){
					RestUtitlity.setValidationMessage(context, "NAS Client must be specified");
					return isValid = false;
				} else if(Collectionz.isNullOrEmpty(radAcctEsiData)){
					RestUtitlity.setValidationMessage(context, "Accounting Server must be specified");
					return isValid = false;
				}
				
				for (SMSessionCloserESIRelData smSessionCloserESIRelData : nasEsiData) {
					if(RestValidationMessages.INVALID.equals(smSessionCloserESIRelData.getEsiInstanceId())){
						RestUtitlity.setValidationMessage(context, "NAS Client must be valid");
						return isValid = false;
					}
				}

				for (SMSessionCloserESIRelData smSessionCloserESIRelData : radAcctEsiData) {
					if(RestValidationMessages.INVALID.equals(smSessionCloserESIRelData.getEsiInstanceId())){
						RestUtitlity.setValidationMessage(context, "Accounting Server must be valid");
						return isValid = false;
					}
				}
				
				boolean isValidaNAS = true;
				if(Collectionz.isNullOrEmpty(nasEsiData) == false){
					isValidaNAS = getESIServerData(context,NAS_CLIENT,nasEsiData);
				}
				
				if(Collectionz.isNullOrEmpty(radAcctEsiData) == false){
					isValid = getESIServerData(context,ACCOUNTING_SERVER,radAcctEsiData);
				}
				if(isValidaNAS == false){
					isValid = false;
				}
			}
		}
		return isValid;
	}

	private boolean validateConfiguredSessionOvverideAction(ConstraintValidatorContext context, String esi, List<SMSessionCloserESIRelData> esiData) {
		boolean isValid = true;
		Long INVALID_ID = -1l;
		ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager = new ExternalSystemInterfaceBLManager();
		Set<String> externalSystemList = new TreeSet<String>();

		try {
			List<ExternalSystemInterfaceInstanceData> nasInstanceList = externalSystemInterfaceBLManager.getExternalSystemInstanceDataList(ExternalSystemConstants.NAS);
			List<ExternalSystemInterfaceInstanceData> rad_AcctList = externalSystemInterfaceBLManager.getExternalSystemInstanceDataList(ExternalSystemConstants.ACCT_PROXY);

			for (ExternalSystemInterfaceInstanceData externalSystemInterfaceInstanceData : nasInstanceList) {
				externalSystemList.add(externalSystemInterfaceInstanceData.getName());
			}

			for (ExternalSystemInterfaceInstanceData externalSystemInterfaceInstanceData : rad_AcctList) {
				externalSystemList.add(externalSystemInterfaceInstanceData.getName());
			}

			if(Collectionz.isNullOrEmpty(esiData) == false){
				for (SMSessionCloserESIRelData smSessionCloserESIRelData : esiData) {
					String esiInstaceId = smSessionCloserESIRelData.getEsiInstanceId();
					if(RestValidationMessages.INVALID.equals(esiInstaceId)){
						RestUtitlity.setValidationMessage(context, esi + " must be valid");
						return false;
					} else {
						String externalSystemInterface = externalSystemInterfaceBLManager.getRadiusESINameById(esiInstaceId);
						if(Strings.isNullOrEmpty(externalSystemInterface) ==  false){
							if(externalSystemList.contains(externalSystemInterface) == false){
								RestUtitlity.setValidationMessage(context, esi + " must be valid");
								return false;
							}
						} 
					}
				}
			} 
		} catch (Exception e) {
			RestUtitlity.setValidationMessage(context, " Failed to retrive ESI Server data ");
			isValid=false;
		}
		return isValid;
	}
	
	private boolean getESIServerData(ConstraintValidatorContext context, String esiName, List<SMSessionCloserESIRelData> esiData) {
		boolean isValid = true;
		ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager = new ExternalSystemInterfaceBLManager();
		
		Set<String> externalSystemList = new TreeSet<String>();
		
		try {
			if(NAS_CLIENT.equalsIgnoreCase(esiName)){
				List<ExternalSystemInterfaceInstanceData> nasInstanceList = externalSystemInterfaceBLManager.getExternalSystemInstanceDataList(ExternalSystemConstants.NAS);
				for (ExternalSystemInterfaceInstanceData externalSystemInterfaceInstanceData : nasInstanceList) {
					externalSystemList.add(externalSystemInterfaceInstanceData.getEsiInstanceId());
				}
				
				for (SMSessionCloserESIRelData nasESIData : esiData) {
					if(externalSystemList.contains(nasESIData.getEsiInstanceId()) == false){
						RestUtitlity.setValidationMessage(context, "Only NAS type ESI is allowed in NAS ESI Server");
						isValid = false;
					}
				}
			} else if(ACCOUNTING_SERVER.equalsIgnoreCase(esiName)){
				List<ExternalSystemInterfaceInstanceData> rad_AcctList = externalSystemInterfaceBLManager.getExternalSystemInstanceDataList(ExternalSystemConstants.ACCT_PROXY);
				for (ExternalSystemInterfaceInstanceData externalSystemInterfaceInstanceData : rad_AcctList) {
					externalSystemList.add(externalSystemInterfaceInstanceData.getEsiInstanceId());
				}
				
				for (SMSessionCloserESIRelData nasESIData : esiData) {
					if(externalSystemList.contains(nasESIData.getEsiInstanceId()) == false){
						RestUtitlity.setValidationMessage(context, " Only Accounting type ESI is allowed in Accounting ESI Server ");
						isValid = false;
					}
				}
			}
		} catch (Exception e) {
			RestUtitlity.setValidationMessage(context, " Failed to retrive ESI Server data ");
			isValid = false;
		}
		return isValid;
	}
}
