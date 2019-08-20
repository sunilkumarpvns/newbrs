/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyData.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.diameter.sessionmanager.data;

import java.util.HashSet;
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

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.util.constants.DiameterSessionManagerConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.DatabaseDatasourceNameAdapter;
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
  
@XmlType(propOrder = {"name","description","databaseDatasourceId","tableName","sequenceName","startTimeField","lastUpdatedTimeField","dbQueryTimeout","delimeter",
		"dbFailureAction","batchEnabled","batchSize","batchInterval","batchQueryTimeout","batchedInsert","batchedUpdate","batchedDelete",
		"viewableColumns","diameterSessionManagerMappingData","scenarioMappingDataSet","sessionOverideActionDataSet"})
@XmlRootElement(name = "diameter-session-manager")
@ValidObject
public class DiameterSessionManagerData extends BaseData implements Differentiable, Validator{

	public DiameterSessionManagerData() {
		this.delimeter = DiameterSessionManagerConstants.MULTIVALUE_DELIMETER;
		this.batchSize = DiameterSessionManagerConstants.BATCH_SIZE;
		this.batchInterval = DiameterSessionManagerConstants.BATCH_UPDATE_INTERVAL;
		this.batchQueryTimeout = DiameterSessionManagerConstants.BATCH_QUERY_TIMEOUT;
	}

	private String sessionManagerId;

	@Expose
	@SerializedName("Name")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX,message = RestValidationMessages.NAME_INVALID)
	@NotEmpty(message = "Diameter Session Manager name must be specified")
	private String name;

	@Expose
	@SerializedName("Description")
	private String description;

	@Expose
	@SerializedName("Datasource")
	private String databaseDatasourceId;

	@Expose
	@SerializedName("Table Name")
	@NotEmpty(message = "Table name must be specified")
	private String tableName;

	@Expose
	@SerializedName("Sequence Name")
	@NotEmpty(message = "Sequence Name must be specified")
	private String sequenceName;

	@Expose
	@SerializedName("Start Time Field")
	@NotEmpty(message = "Start Time Field Name must be specified")
	private String startTimeField;

	@Expose
	@SerializedName("DB Query Timeout(sec)")
	@NotNull(message = "DB Query Timeout Field Name must be specified")
	@Min(value = 0 , message = "DB Query Timeout value must be zero or positive number.")
	private Integer dbQueryTimeout;

	@Expose
	@SerializedName("Multivalue Delimiter")
	@Length(min = 0,max = 5 , message = "Delimeter length must be between 0 to 5")
	private String delimeter;

	@Expose
	@SerializedName("DB Failure Action")
	@Pattern(regexp = "IGNORE|REJECT|DROP", message = "Invalid DB Failure Action. It can be IGNORE, REJECT or DROP.")
	private String dbFailureAction;

	@Expose
	@SerializedName("Batch Mode")
	@NotEmpty(message = "Batch Mode field must be specified")
	@Pattern(regexp = "true|false", message = "Invalid value of Batch Mode Enabled. Value could be 'true' or 'false'.")
	private String batchEnabled;

	@Expose
	@SerializedName("Batch Size")
	private Integer batchSize;

	@Expose
	@SerializedName("Update Interval(sec)")
	private Integer batchInterval;

	@Expose
	@SerializedName("Batch Query TimeOut(sec)")
	private Integer batchQueryTimeout;

	@Pattern(regexp = "true|false", message = "Invalid value of Batch Operation Insert. Value could be 'true' or 'false'.")
	private String batchedInsert;
	@Pattern(regexp = "true|false", message = "Invalid value of Batch Operation Update. Value could be 'true' or 'false'.")
	private String batchedUpdate;
	@Pattern(regexp = "true|false", message = "Invalid value of Batch Operation Delete. Value could be 'true' or 'false'.")
	private String batchedDelete;

	@NotEmpty(message = "Last Updated Time Field name must be specified")
	private String lastUpdatedTimeField;
	private String auditUId;
	private String viewableColumns;

	@Valid
	private Set<DiameterSessionManagerMappingData> diameterSessionManagerMappingData;
	@Valid
	private Set<ScenarioMappingData> scenarioMappingDataSet;
	@Valid
	private Set<SessionOverideActionData> sessionOverideActionDataSet;

	@XmlTransient
	public String getSessionManagerId() {
		return sessionManagerId;
	}
	public void setSessionManagerId(String sessionManagerId) {
		this.sessionManagerId = sessionManagerId;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	@XmlElement(name = "db-query-timeout")
	public Integer getDbQueryTimeout() {
		return dbQueryTimeout;
	}
	public void setDbQueryTimeout(Integer dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}

	@XmlElement(name = "multivalue-delimeter")
	public String getDelimeter() {
		return delimeter;
	}
	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}

	@XmlElement(name = "batch-mode")
	@XmlJavaTypeAdapter(value = LowerCaseConvertAdapter.class)
	public String getBatchEnabled() {
		return batchEnabled;
	}
	public void setBatchEnabled(String batchEnabled) {
		this.batchEnabled = batchEnabled;
	}

	@XmlElement(name = "batch-size")
	public Integer getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	@XmlElement(name = "update-interval")
	public Integer getBatchInterval() {
		return batchInterval;
	}
	public void setBatchInterval(Integer batchInterval) {
		this.batchInterval = batchInterval;
	}

	@XmlElement(name = "batch-query-timeout")
	public Integer getBatchQueryTimeout() {
		return batchQueryTimeout;
	}
	public void setBatchQueryTimeout(Integer batchQueryTimeout) {
		this.batchQueryTimeout = batchQueryTimeout;
	}

	@XmlElement(name = "batch-operations-insert")
	public String getBatchedInsert() {
		return batchedInsert;
	}
	public void setBatchedInsert(String batchedInsert) {
		this.batchedInsert = batchedInsert;
	}

	@XmlElement(name = "batch-operations-update")
	public String getBatchedUpdate() {
		return batchedUpdate;
	}
	public void setBatchedUpdate(String batchedUpdate) {
		this.batchedUpdate = batchedUpdate;
	}

	@XmlElement(name = "batch-operations-delete")
	public String getBatchedDelete() {
		return batchedDelete;
	}
	public void setBatchedDelete(String batchedDelete) {
		this.batchedDelete = batchedDelete;
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

	@XmlElement(name = "table-name")
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@XmlElement(name = "sequence-name")
	public String getSequenceName() {
		return sequenceName;
	}
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	@XmlElement(name = "start-time-field")
	public String getStartTimeField() {
		return startTimeField;
	}
	public void setStartTimeField(String startTimeField) {
		this.startTimeField = startTimeField;
	}

	@XmlElement(name = "last-updated-time-field")
	public String getLastUpdatedTimeField() {
		return lastUpdatedTimeField;
	}
	public void setLastUpdatedTimeField(String lastUpdatedTimeField) {
		this.lastUpdatedTimeField = lastUpdatedTimeField;
	}

	@XmlElementWrapper(name = "table-field-mappings")
	@XmlElement(name = "table-field-mapping-data")
	public Set<DiameterSessionManagerMappingData> getDiameterSessionManagerMappingData() {
		return diameterSessionManagerMappingData;
	}
	public void setDiameterSessionManagerMappingData(
			Set<DiameterSessionManagerMappingData> diameterSessionManagerMappingData) {
		this.diameterSessionManagerMappingData = diameterSessionManagerMappingData;
	}

	@XmlElementWrapper(name = "scenario-mappings")
	@XmlElement(name = "scenario-mapping-data")
	public Set<ScenarioMappingData> getScenarioMappingDataSet() {
		return scenarioMappingDataSet;
	}
	public void setScenarioMappingDataSet(Set<ScenarioMappingData> scenarioMappingDataSet) {
		this.scenarioMappingDataSet = scenarioMappingDataSet;
	}

	@XmlElementWrapper(name = "session-overide-action-mappings")
	@XmlElement(name = "session-overide-action-mapping-data")
	public Set<SessionOverideActionData> getSessionOverideActionDataSet() {
		return sessionOverideActionDataSet;
	}
	public void setSessionOverideActionDataSet(
			Set<SessionOverideActionData> sessionOverideActionDataSet) {
		this.sessionOverideActionDataSet = sessionOverideActionDataSet;
	}

	@XmlElement(name = "viewable-columns")
	public String getViewableColumns() {
		return viewableColumns;
	}
	public void setViewableColumns(String viewableColumns) {
		this.viewableColumns = viewableColumns;
	}

	@XmlElement(name = "db-failure-action")
	public String getDbFailureAction() {
		return dbFailureAction;
	}
	public void setDbFailureAction(String dbFailureAction) {
		this.dbFailureAction = dbFailureAction;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("Datasource ", EliteSMReferencialDAO.fetchDatabaseDatasourceData(databaseDatasourceId));
		object.put("Table Name", tableName);
		object.put("Sequence Name",sequenceName);
		object.put("Start Time Field", startTimeField);
		object.put("Last Update Time Field", lastUpdatedTimeField);
		object.put("DB Query Timeout(sec)" , dbQueryTimeout);
		object.put("Multivalue Delimeter", delimeter);
		object.put("DB Failure Action", dbFailureAction);
		if(batchEnabled.equals("true")){
			object.put("Batch Mode", "Enabled");
		}else{
			object.put("Batch Mode", "Disabled");
		}
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {

		boolean isValid = true;

		if(RestValidationMessages.INVALID.equals(this.databaseDatasourceId)){
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Database Name must be valid");
		}
		
		if(batchedUpdate.equalsIgnoreCase("false") && batchedDelete.equalsIgnoreCase("false") && batchedInsert.equalsIgnoreCase("false")){
			RestUtitlity.setValidationMessage(context, "At least one Batch Operations must be true");
			return isValid = false;
		}
		isValid = validateMappings(context);

		return isValid;
	}

	private boolean validateMappings(ConstraintValidatorContext context){
		boolean isValid = true;

		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();

		Set<String> dbFieldNameList = databaseDSBLManager.getTableFieldList(databaseDatasourceId, tableName);

		Set<String> diameterSessionManagerMappingNameList = new TreeSet<String>();
		Set<String> diameterSessionManagerMappingDBFieldNameList = new TreeSet<String>();

		if(Collectionz.isNullOrEmpty(diameterSessionManagerMappingData) == false){
			Set<String> duplicateName = new HashSet<String>(); 
			for (DiameterSessionManagerMappingData diameterSMMappingData : diameterSessionManagerMappingData) {

				String mappingName = diameterSMMappingData.getMappingName();
				diameterSessionManagerMappingNameList.add(mappingName);

				for (SessionManagerFieldMappingData sessionManagerFieldMappingData : diameterSMMappingData.getSessionManagerFieldMappingData()) {
					if(Collectionz.isNullOrEmpty(dbFieldNameList) == false){
						if(Strings.isNullOrBlank(sessionManagerFieldMappingData.getDbFieldName()) == false){
							if(dbFieldNameList.contains(sessionManagerFieldMappingData.getDbFieldName()) == false){
								RestUtitlity.setValidationMessage(context, "DB Field Name " + sessionManagerFieldMappingData.getDbFieldName() + " Must be valid");
								isValid = false;
							}
						}
					}
					String dbFieldName = sessionManagerFieldMappingData.getDbFieldName();
					diameterSessionManagerMappingDBFieldNameList.add(dbFieldName);
				}
				boolean isAdded = duplicateName.add(diameterSMMappingData.getMappingName());
				if(isAdded == false){
					RestUtitlity.setValidationMessage(context,  "Duplicate Mapping name " + diameterSMMappingData.getMappingName() + " found");
					isValid = false;
				}
			}
		}

		if(Collectionz.isNullOrEmpty(scenarioMappingDataSet) == false){
			Set<String> duplicateName = new HashSet<String>(); 
			for (ScenarioMappingData scenarioData : scenarioMappingDataSet) {
				if(Strings.isNullOrBlank(scenarioData.getMappingName()) == false){
					if(diameterSessionManagerMappingNameList.contains(scenarioData.getMappingName()) ==  false){
						RestUtitlity.setValidationMessage(context, "Mapping Name " + scenarioData.getMappingName() + " must be valid");
						isValid = false;
					}
				}

				if(Strings.isNullOrEmpty(scenarioData.getCriteria()) == false){
					String[] criteriaNames = scenarioData.getCriteria().split(",");
					for (String criteriaName : criteriaNames) {
						if(Strings.isNullOrBlank(criteriaName) == false){
							if(diameterSessionManagerMappingDBFieldNameList.contains(criteriaName) == false){
								if(Strings.isNullOrBlank(scenarioData.getMappingName()) == false){
									RestUtitlity.setValidationMessage(context,  criteriaName + " not exist in " + scenarioData.getMappingName());
									isValid = false;
								}
							}
						}
					}
				}
				
				boolean isAdded = duplicateName.add(scenarioData.getName());
				if(isAdded == false){
					RestUtitlity.setValidationMessage(context,  "Duplicate Scenario Mapping name " + scenarioData.getName() + " found");
					isValid = false;
				}
			}
		}

		//Validate Table Field Mapping Data
		if(Collectionz.isNullOrEmpty(diameterSessionManagerMappingData) == false){
			for (DiameterSessionManagerMappingData tableFieldMappingData : diameterSessionManagerMappingData) {
				for (SessionManagerFieldMappingData mandatoryField : tableFieldMappingData.getSessionManagerFieldMappingData()) {
					if(Strings.isNullOrBlank(mandatoryField.getDbFieldName()) == false){
						if(Strings.isNullOrBlank(mandatoryField.getReferringAttr())){
							RestUtitlity.setValidationMessage(context, "Referring Attribute for " + mandatoryField.getDbFieldName() + " field must be specified");
							isValid = false;
						}
					}

					if(Strings.isNullOrBlank(mandatoryField.getDbFieldName()) == false){
						if(mandatoryField.getDataType() == null){
							RestUtitlity.setValidationMessage(context, "Data Type for " + mandatoryField.getDbFieldName() + " field must be specified");
							isValid = false;
						} else if (-1 == mandatoryField.getDataType().intValue()){
							RestUtitlity.setValidationMessage(context, "Data Type for " + mandatoryField.getDbFieldName() + " field must be valid");
							isValid = false;
						}
					}
				}

			}

			//Validate Scenario Mapping Data
			if(Collectionz.isNullOrEmpty(scenarioMappingDataSet) == false){
				for (ScenarioMappingData mandatoryField : scenarioMappingDataSet) {
					if(Strings.isNullOrBlank(mandatoryField.getName()) == false){
						if(Strings.isNullOrBlank(mandatoryField.getRuleset())){
							RestUtitlity.setValidationMessage(context, "Ruleset for " + mandatoryField.getName() + " field must be specified");
							isValid = false;
						}
					}

					if(Strings.isNullOrEmpty(mandatoryField.getName()) == false){
						if(Strings.isNullOrBlank(mandatoryField.getCriteria())){
							RestUtitlity.setValidationMessage(context, "Criteria for " + mandatoryField.getName() + " field must be specified");
							isValid = false;
						} else if (Strings.isNullOrBlank(mandatoryField.getMappingName())){
							RestUtitlity.setValidationMessage(context, "Mapping name for " + mandatoryField.getName() + " field must be valid");
							isValid = false;
						}
					}
				}
			}

			//Validate Session Override Action Data
			if(Collectionz.isNullOrEmpty(sessionOverideActionDataSet) == false){
				Set<String> duplicateName = new HashSet<String>(); 
				for (SessionOverideActionData mandatoryField : sessionOverideActionDataSet) {
					if(Strings.isNullOrBlank(mandatoryField.getName()) == false){
						if(Strings.isNullOrBlank(mandatoryField.getRuleset())){
							RestUtitlity.setValidationMessage(context, "Ruleset for " + mandatoryField.getName() + " field must be specified");
							isValid = false;
						}
						
						boolean isAdded = duplicateName.add(mandatoryField.getName());
						if(isAdded == false){
							RestUtitlity.setValidationMessage(context,  "Duplicate Session Override Action name " + mandatoryField.getName() + " found");
							isValid = false;
						}
					}

					if(Strings.isNullOrBlank(mandatoryField.getName()) == false){
						if(Strings.isNullOrBlank(mandatoryField.getActions())){
							RestUtitlity.setValidationMessage(context, "Actions for " + mandatoryField.getName() + " field must be specified");
							isValid = false;
						}
					}
				}
			}
		}
		return isValid;
	}
}