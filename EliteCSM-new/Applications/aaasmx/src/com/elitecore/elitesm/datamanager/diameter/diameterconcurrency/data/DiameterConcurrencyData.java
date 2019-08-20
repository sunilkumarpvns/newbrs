package com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.adapter.DatabaseDatasourceNameAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder = {"name","description","databaseDsId","tableName","startTimeField","lastUpdateTimeField",
		"concurrencyIdentityField","dbFailureAction","sessionOverrideAction","sessionOverrideFields","diameterConcurrencyMandatoryFieldMappingsList",
		"diameterConcurrencyAdditonalFieldMappingList"})
@XmlRootElement(name="diameter-concurrency")
@ValidObject
public class DiameterConcurrencyData extends BaseData implements Differentiable,Validator {

	private String diaConConfigId;

	@NotEmpty(message = "Diameter Concurrecny name must be specified")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	private String name;
	private String description;
	private String databaseDsId;

	@NotEmpty(message = "Table Name must be specified")
	private String tableName;

	@NotEmpty(message="Start time field must be specified")
	private String startTimeField;

	@NotEmpty(message="Last update time field must be specified")
	private String lastUpdateTimeField;

	@NotEmpty(message= "Concurrency identity field must be specified")
	private String concurrencyIdentityField;
	
	@NotEmpty(message = "DB Failure Action must be specified")
	@Pattern(regexp = "IGNORE|REJECT|DROP", message = "Invalid DB Failure Action. It can be IGNORE, REJECT or DROP.")
	private String dbFailureAction;
	@NotEmpty(message = "Session Override Action must be specified")
	@Pattern(regexp = "None||Generate ASR", message = "Invalid SessionOverride Action. It can be None or Generate ASR.")
	private String sessionOverrideAction;
	private String sessionOverrideFields;
	private String auditUId;
	@Valid
	private List<DiameterConcurrencyFieldMapping> diameterConcurrencyFieldMappingList;
	@Valid
	private List<DiameterConcurrencyFieldMapping> diameterConcurrencyMandatoryFieldMappingsList;
	@Valid
	private List<DiameterConcurrencyFieldMapping> diameterConcurrencyAdditonalFieldMappingList;

	public DiameterConcurrencyData() {
		description = RestUtitlity.getDefaultDescription();
	}

	@XmlTransient
	public String getDiaConConfigId() {
		return diaConConfigId;
	}

	public void setDiaConConfigId(String diaConConfigId) {
		this.diaConConfigId = diaConConfigId;
	}

	@XmlElement(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "database-datasource")
	@NotEmpty(message = "Database Name must be specified")
	@XmlJavaTypeAdapter(value = DatabaseDatasourceNameAdapter.class)
	public String getDatabaseDsId() {
		return databaseDsId;
	}

	public void setDatabaseDsId(String databaseDsId) {
		this.databaseDsId = databaseDsId;
	}

	@XmlElement(name="table-name")
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@XmlElement(name="start-time-field")
	public String getStartTimeField() {
		return startTimeField;
	}

	public void setStartTimeField(String startTimeField) {
		this.startTimeField = startTimeField;
	}

	@XmlElement(name="db-failure-action")
	public String getDbFailureAction() {
		return dbFailureAction;
	}

	public void setDbFailureAction(String dbFailureAction) {
		this.dbFailureAction = dbFailureAction;
	}

	@XmlElement(name="session-override-action")
	public String getSessionOverrideAction() {
		return sessionOverrideAction;
	}

	public void setSessionOverrideAction(String sessionOverrideAction) {
		this.sessionOverrideAction = sessionOverrideAction;
	}

	@XmlElement(name="last-update-time-field")
	public String getLastUpdateTimeField() {
		return lastUpdateTimeField;
	}

	public void setLastUpdateTimeField(String lastUpdateTimeField) {
		this.lastUpdateTimeField = lastUpdateTimeField;
	}

	@XmlElement(name="concurrency-identity-field")
	public String getConcurrencyIdentityField() {
		return concurrencyIdentityField;
	}

	public void setConcurrencyIdentityField(String concurrencyIdentityField) {
		this.concurrencyIdentityField = concurrencyIdentityField;
	}

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}

	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElementWrapper(name="additional-db-field-mappings")
	@XmlElement(name="db-field-mapping",type=DiameterConcurrencyFieldMapping.class)
	public List<DiameterConcurrencyFieldMapping> getDiameterConcurrencyAdditonalFieldMappingList() {
		return diameterConcurrencyAdditonalFieldMappingList;
	}

	public void setDiameterConcurrencyAdditonalFieldMappingList(
			List<DiameterConcurrencyFieldMapping> diameterConcurrencyAdditonalFieldMappingList) {
		this.diameterConcurrencyAdditonalFieldMappingList = diameterConcurrencyAdditonalFieldMappingList;
	}

	@XmlElementWrapper(name="mandatory-db-field-mappings")
	@XmlElement(name="db-field-mapping",type=DiameterConcurrencyFieldMapping.class)
	public List<DiameterConcurrencyFieldMapping> getDiameterConcurrencyMandatoryFieldMappingsList() {
		return diameterConcurrencyMandatoryFieldMappingsList;
	}

	public void setDiameterConcurrencyMandatoryFieldMappingsList(
			List<DiameterConcurrencyFieldMapping> diameterConcurrencyMandatoryFieldMappingsList) {
		this.diameterConcurrencyMandatoryFieldMappingsList = diameterConcurrencyMandatoryFieldMappingsList;
	}

	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();

		writer.println("Name :" + name);
		writer.println("Description :" + description);
		writer.println("Table Name :" + tableName);
		writer.println("Start Time Field :" + startTimeField);
		writer.println("Last Update Time :" + lastUpdateTimeField);
		writer.println("DB Failure Action :" + dbFailureAction);
		writer.println("Session Overide Action :" + sessionOverrideAction);
		writer.println("Concurrency Identity Field :" + concurrencyIdentityField);

		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
		writer.close();
		return out.toString();
	}

	@XmlTransient
	public List<DiameterConcurrencyFieldMapping> getDiameterConcurrencyFieldMappingList() {
		return diameterConcurrencyFieldMappingList;
	}

	public void setDiameterConcurrencyFieldMappingList(
			List<DiameterConcurrencyFieldMapping> diameterConcurrencyFieldMappingList) {
		this.diameterConcurrencyFieldMappingList = diameterConcurrencyFieldMappingList;
	}

	@XmlElement(name="session-override-fields")
	public String getSessionOverrideFields() {
		return sessionOverrideFields;
	}

	public void setSessionOverrideFields(String sessionOverrideFields) {
		this.sessionOverrideFields = sessionOverrideFields;
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("Table Name", tableName);
		object.put("Start Time Field", startTimeField);
		object.put("Last Update Time ", lastUpdateTimeField);
		object.put("Concurrency Identity Field", concurrencyIdentityField);
		object.put("DB Failure Action", dbFailureAction);
		object.put("Session Overide Action", sessionOverrideAction);
		object.put("Session Overide Fields", sessionOverrideFields);

		if (diameterConcurrencyFieldMappingList != null) {
			JSONArray array = new JSONArray();
			for (DiameterConcurrencyFieldMapping element : diameterConcurrencyFieldMappingList) {
				if(element.getLogicalField() == null){
					array.add(element.toJson());
				}
			}
			if(array.size() > 0){
				object.put("Additional DB Field Mappings ", array);
			}
		}

		if (diameterConcurrencyFieldMappingList != null) {
			JSONArray array = new JSONArray();
			for (DiameterConcurrencyFieldMapping element : diameterConcurrencyFieldMappingList) {
				if(element.getLogicalField() != null){
					array.add(element.toJson());
				}
			}
			if(array.size() > 0){
				object.put("Mandatory DB Field Mappings", array); 
			}
		}
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;

		Set<String> mandatoryFields = new HashSet<String>();
		Set<String> mandatoryDBFieldNames = new HashSet<String>();
		Set<String> mandatoryDuplicateFields = new HashSet<String>();
		Set<String> additionalDuplicateFields = new HashSet<String>();
		
		if(RestValidationMessages.INVALID.equals(this.databaseDsId)){
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Database Name must be valid");
		}

		// validate mandatory DB Field Mappings
		if(Collectionz.isNullOrEmpty(diameterConcurrencyMandatoryFieldMappingsList) == false){
			
			for (DiameterConcurrencyFieldMapping field : diameterConcurrencyMandatoryFieldMappingsList) {
				mandatoryFields.add(field.getLogicalField());
			}
			
			for (DiameterConcurrencyFieldMapping mandatoryField : diameterConcurrencyMandatoryFieldMappingsList) {
				for (MandatoryDBFieldMapping field : MandatoryDBFieldMapping.values()) {
					if(mandatoryFields.contains(field.getDBFieldMapping()) == false){
						RestUtitlity.setValidationMessage(context, " Mandatory DB Field " + field.getDBFieldMapping() + " must be specified");
						isValid = false;
					}
				}
				boolean isAdded = mandatoryDuplicateFields.add(mandatoryField.getLogicalField());
				if(isAdded == false){
					RestUtitlity.setValidationMessage(context, " Duplicate field " + mandatoryField.getLogicalField() + " found in mandatory field mappings");
					isValid = false;
				}
				
				if(Strings.isNullOrEmpty(mandatoryField.getDbFieldName()) == false){
					if(Strings.isNullOrEmpty(mandatoryField.getReferringAttribute())){
						RestUtitlity.setValidationMessage(context, " Referring Attribute for " + mandatoryField.getDbFieldName() + " field must be specified");
						isValid = false;
					}
				}
				
				if(Strings.isNullOrEmpty(mandatoryField.getDbFieldName()) == false){
					if(mandatoryField.getDataType() == null){
						RestUtitlity.setValidationMessage(context, " Data Type for " + mandatoryField.getDbFieldName() + " field must be specified");
						isValid = false;
					} else if (-1 == mandatoryField.getDataType().intValue()){
						RestUtitlity.setValidationMessage(context, " Data Type for " + mandatoryField.getDbFieldName() + " field must be 'String' only");
						isValid = false;
					}
				}
				mandatoryDBFieldNames.add(mandatoryField.getDbFieldName());
			}		
		}

		// validate additional DB Field mappings
		if(Collectionz.isNullOrEmpty(diameterConcurrencyAdditonalFieldMappingList) == false){
			for (DiameterConcurrencyFieldMapping additionalField : diameterConcurrencyAdditonalFieldMappingList) {
				if(additionalField.getLogicalField() == null){
					if(mandatoryDuplicateFields.contains(additionalField.getDbFieldName())){
						RestUtitlity.setValidationMessage(context, " Mandatory field " + additionalField.getDbFieldName() + " not allowed in additional field mappings");
						isValid = false;
					}
					
					boolean isAdded = additionalDuplicateFields.add(additionalField.getDbFieldName());
					if(isAdded == false){
						RestUtitlity.setValidationMessage(context, " Duplicate field " + additionalField.getDbFieldName() + " found in additional field mappings");
						isValid = false;
					}
				} else {
					RestUtitlity.setValidationMessage(context, " Invalid entry of " + additionalField.getDbFieldName() + " field in addtional mappings");
					isValid = false;
				}
				
				if(Strings.isNullOrEmpty(additionalField.getDbFieldName()) == false){
					if(Strings.isNullOrEmpty(additionalField.getReferringAttribute())){
						RestUtitlity.setValidationMessage(context, " Referring Attribute for " + additionalField.getDbFieldName() + " field must be specified");
						isValid = false;
					}
				}
				
				if(Strings.isNullOrEmpty(additionalField.getDbFieldName()) == false){
					if(additionalField.getDataType() == null){
						RestUtitlity.setValidationMessage(context, " Data Type for " + additionalField.getDbFieldName() + " field must be specified");
						isValid = false;
					} else if (-1 == additionalField.getDataType().intValue()){
						RestUtitlity.setValidationMessage(context, " Data Type for " + additionalField.getDbFieldName() + " field must be 'String' only");
						isValid = false;
					}
				}
			}
		}
		
		// validate Session Override Action
		if((Collectionz.isNullOrEmpty(diameterConcurrencyMandatoryFieldMappingsList) == false) && (Collectionz.isNullOrEmpty(diameterConcurrencyAdditonalFieldMappingList) == false)){
			if(Strings.isNullOrEmpty(sessionOverrideAction) == false){
				if(sessionOverrideAction.equalsIgnoreCase("Generate ASR") && Strings.isNullOrEmpty(sessionOverrideFields)){
					RestUtitlity.setValidationMessage(context," Session Override Fields must be specified");
					isValid = false;
				} else if(sessionOverrideAction.equalsIgnoreCase("Generate ASR")){
					String[] sesOveFields = sessionOverrideFields.split(",");
					for (String sessionOverrideField : sesOveFields) {
						if(mandatoryDBFieldNames.contains(sessionOverrideField) == false && (additionalDuplicateFields.contains(sessionOverrideField)) == false){
							RestUtitlity.setValidationMessage(context," Configured Session Override Fields " + sessionOverrideField + " must be mapped with either Mandatory Field Mappings or Additional DB Field Mappings");
							isValid = false;
						} 
					}
				}
			}
		}
		
		// validate All mandatory DB Fields
		if(validateDBFieldNames(context,mandatoryDBFieldNames) == false){
			isValid = false;
		}
		
		// validate All additional DB Fields
		if(validateDBFieldNames(context,additionalDuplicateFields) == false){
			isValid = false;
		}
		return isValid;
	}

	public boolean validateDBFieldNames(ConstraintValidatorContext context, Set<String> fieldsToValidate){
		boolean isValid = true;
		Set<String> tableFieldList = null;
		DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();

		tableFieldList = databaseDSBLManager.getTableFieldList(databaseDsId,tableName);

		if(Collectionz.isNullOrEmpty(tableFieldList) == false){
			if(Collectionz.isNullOrEmpty(fieldsToValidate) == false){
				for (String dbField : fieldsToValidate) {
					if(tableFieldList.contains(dbField.toUpperCase()) == false){
						RestUtitlity.setValidationMessage(context," DB Field "+ dbField +" must be valid");
						isValid = false;
					}
				}
			}
		}
		return isValid;
	}
}