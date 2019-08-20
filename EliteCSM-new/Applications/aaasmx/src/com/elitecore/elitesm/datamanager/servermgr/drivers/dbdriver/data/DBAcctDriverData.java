package com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data;


import java.io.Serializable;
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

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.aaa.util.constants.RadiusDBAcctDriverConstant;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.DatabaseDatasourceNameAdapter;
import com.elitecore.elitesm.ws.rest.adapter.FieldTrimmerAdapter;
import com.elitecore.elitesm.ws.rest.adapter.InitCapCaseAdapter;
import com.elitecore.elitesm.ws.rest.adapter.LowerCaseConvertAdapter;
import com.elitecore.elitesm.ws.rest.adapter.NumericAdapter;
import com.elitecore.elitesm.ws.rest.security.AuthenticationDetails;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name = "db-acct-driver")
@XmlType(propOrder = {"databaseId", "datasourceType", "dbQueryTimeout", "maxQueryTimeoutCount", "multivalDelimeter", "cdrTablename"
		, "cdrIdDbField", "cdrIdSeqName", "storeStopRec", "interimTablename", "storeInterimRec", "removeInterimOnStop", "dbDateField",
		"enabled", "interimCdrIdDbField", "interimCdrIdSeqName", "callStartFieldName", "callEndFieldName", "createDateFieldName",
		"lastModifiedDateFieldName", "storeAllCdr", "dbAcctFieldMapList",})
@ValidObject
public class DBAcctDriverData  extends BaseData implements Differentiable,Serializable,Validator{
	
	private static final String INVALID_DB = "-1L";
	private static final long serialVersionUID = 1L;
	private String openDbAcctId;	
	
	@Expose
	@SerializedName("Database Datasource")
	@NotEmpty(message = "Database Datasource must be specified")
	private String databaseId;
	
	@Expose
	@SerializedName("Datasource Type")
	@Pattern(regexp = "(?i)(Oracle)", message = "Invalid value for Data Source Type. Value could be 'Oracle'.")
	private String datasourceType;
	
	@Expose
	@SerializedName("DB Query Timeout(Sec.)")
	@NotNull(message = "DB Query Timeout must be specified.")
	@Min(value = 0, message = "DB Query Timeout must be zero or positive number.")
	private Long dbQueryTimeout;
	
	@Expose
	@SerializedName("Maximum Query Timeout Count")
	@NotNull(message = "Max Query timeout count must be specified.")
	@Min(value = 0, message = "Maximum Query Timeout Count must be zero or positive number.")
	private Long maxQueryTimeoutCount;
	
	@Expose
	@SerializedName("Multiple Value Delimiter")
	private String multivalDelimeter;
	
	//private Long datasourceScantime;
	
	@Expose
	@SerializedName("Table Name")
	@NotEmpty(message = "CDR Table Name must be specified.")
	private String cdrTablename;
	
	@Expose
	@SerializedName("Identity Field")
	@NotEmpty(message = "CDR Identify DB Field name must be specified.")
	private String cdrIdDbField;
	
	@Expose
	@SerializedName("Sequence Name")
	@NotEmpty(message = "CDR Sequence name must be specified.")
	private String cdrIdSeqName;
	
	@Expose
	@SerializedName("Store Start Record")
	private String storeTunnelStartRec;
	
	@Expose
	@SerializedName("Store Stop Record")
	@Pattern(regexp = "(?i)(true|false)", message = "Invalid value for Store Stop Record. Value could be 'true' or 'false'.")
	private String storeStopRec;
	
	@NotEmpty(message = "Interim CDR Table Name must be specified.")
	private String interimTablename;
	@Pattern(regexp = "(?i)(true|false)", message = "Invalid value for Store All Interim Record. Value could be 'true' or 'false'.")
	private String storeInterimRec;
	@Pattern(regexp = "(?i)(true|false)", message = "Invalid value for Remove Interim On Stop. Value could be 'true' or 'false'.")
	private String removeInterimOnStop;
	private String storeTunnelStopRec;
	private String removeTunnelStopRec;
	private String storeTunnelLinkStartRec;
	private String storeTunnelLinkStopRec;
	private String removeTunnelLinkStopRec;
	private String storeTunnelRejectRec;
	private String storeTunnelLinkRejectRec;
	private String dbDateField;
	@Pattern(regexp = "(?i)(true|false)", message = "Invalid value for Timestamp Enabled. Value could be 'true' or 'false'.")
	private String enabled;
	@NotEmpty(message = "Interim CDR Id Db Field must be specified.")
	private String interimCdrIdDbField;
	@NotEmpty(message = "Interim CDR Id Sequence name must be specified.")
	private String interimCdrIdSeqName;
	@NotEmpty(message ="Call Start Field name must be specified.")
	private String callStartFieldName;
	@NotEmpty(message ="Call End Field name must be specified.")
	private String callEndFieldName;
	@NotEmpty(message ="Create Date Field name must be specified.")
	private String createDateFieldName;
	@NotEmpty(message ="Last Modified Date Field name must be specified.")
	private String lastModifiedDateFieldName;
	@Valid
	@NotEmpty(message = "At least one mapping must be specified.")	
	private List<DBAcctFeildMapData> dbAcctFieldMapList;
	private String driverInstanceId;
	private String storeAllCdr;
	
	 public DBAcctDriverData() {
		 this.multivalDelimeter = RadiusDBAcctDriverConstant.MULTI_VALUE_DELIMETER;
		 this.datasourceType = RadiusDBAcctDriverConstant.DATASOURCE_TYPE;
		 this.storeStopRec = RadiusDBAcctDriverConstant.STORE_STOP_RECORD;
		 this.storeStopRec = RadiusDBAcctDriverConstant.STOP_ALL_INTERIM_RECORD;
		 this.removeInterimOnStop = RadiusDBAcctDriverConstant.REMOVE_INTERIM_ON_STOP;
		 this.dbDateField = RadiusDBAcctDriverConstant.TIMESTAMP_FIELD;
		 this.enabled = RadiusDBAcctDriverConstant.TIMESTAMP_ENABLED;
	}
	
	@XmlElement(name = "store-start-record")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getStoreAllCdr() {
		return storeAllCdr;
	}
	public void setStoreAllCdr(String storeAllCdr) {
		this.storeAllCdr = storeAllCdr;
	}
	
	@XmlTransient
	public String getOpenDbAcctId() {
		return openDbAcctId;
	}
	public void setOpenDbAcctId(String openDbAcctId) {
		this.openDbAcctId = openDbAcctId;
	}
	
	@XmlElement(name = "database-datasource")
	@XmlJavaTypeAdapter(value = DatabaseDatasourceNameAdapter.class)
	public String getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	
	@XmlElement(name = "datasource-type")
	@XmlJavaTypeAdapter(value = InitCapCaseAdapter.class)
	public String getDatasourceType() {
		return datasourceType;
	}
	public void setDatasourceType(String datasourceType) {
		this.datasourceType = datasourceType;
	}
	/*public Long getDatasourceScantime() {
		return datasourceScantime;
	}
	public void setDatasourceScantime(Long datasourceScantime) {
		this.datasourceScantime = datasourceScantime;
	}*/
	
	@XmlElement(name = "cdr-table-name")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getCdrTablename() {
		return cdrTablename;
	}
	public void setCdrTablename(String cdrTablename) {
		this.cdrTablename = cdrTablename;
	}
	
	@XmlElement(name = "interim-table-name")
	public String getInterimTablename() {
		return interimTablename;
	}
	public void setInterimTablename(String interimTablename) {
		this.interimTablename = interimTablename;
	}
	
	@XmlElement(name = "store-stop-record")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getStoreStopRec() {
		return storeStopRec;
	}
	public void setStoreStopRec(String storeStopRec) {
		this.storeStopRec = storeStopRec;
	}
	
	@XmlElement(name = "store-all-interim-record")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getStoreInterimRec() {
		return storeInterimRec;
	}
	public void setStoreInterimRec(String storeInterimRec) {
		this.storeInterimRec = storeInterimRec;
	}
	
	@XmlElement(name = "remove-interim-on-stop")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getRemoveInterimOnStop() {
		return removeInterimOnStop;
	}
	public void setRemoveInterimOnStop(String removeInterimOnStop) {
		this.removeInterimOnStop = removeInterimOnStop;
	}
	
	@XmlTransient
	public String getStoreTunnelStartRec() {
		return storeTunnelStartRec;
	}
	public void setStoreTunnelStartRec(String storeTunnelStartRec) {
		this.storeTunnelStartRec = storeTunnelStartRec;
	}
	
	@XmlTransient
	public String getStoreTunnelStopRec() {
		return storeTunnelStopRec;
	}
	public void setStoreTunnelStopRec(String storeTunnelStopRec) {
		this.storeTunnelStopRec = storeTunnelStopRec;
	}
	
	@XmlTransient
	public String getRemoveTunnelStopRec() {
		return removeTunnelStopRec;
	}
	public void setRemoveTunnelStopRec(String removeTunnelStopRec) {
		this.removeTunnelStopRec = removeTunnelStopRec;
	}
	
	@XmlTransient
	public String getStoreTunnelLinkStartRec() {
		return storeTunnelLinkStartRec;
	}
	public void setStoreTunnelLinkStartRec(String storeTunnelLinkStartRec) {
		this.storeTunnelLinkStartRec = storeTunnelLinkStartRec;
	}
	
	@XmlTransient
	public String getStoreTunnelLinkStopRec() {
		return storeTunnelLinkStopRec;
	}
	public void setStoreTunnelLinkStopRec(String storeTunnelLinkStopRec) {
		this.storeTunnelLinkStopRec = storeTunnelLinkStopRec;
	}
	
	@XmlTransient
	public String getRemoveTunnelLinkStopRec() {
		return removeTunnelLinkStopRec;
	}
	public void setRemoveTunnelLinkStopRec(String removeTunnelLinkStopRec) {
		this.removeTunnelLinkStopRec = removeTunnelLinkStopRec;
	}
	
	@XmlTransient
	public String getStoreTunnelRejectRec() {
		return storeTunnelRejectRec;
	}
	public void setStoreTunnelRejectRec(String storeTunnelRejectRec) {
		this.storeTunnelRejectRec = storeTunnelRejectRec;
	}
	
	@XmlTransient
	public String getStoreTunnelLinkRejectRec() {
		return storeTunnelLinkRejectRec;
	}
	public void setStoreTunnelLinkRejectRec(String storeTunnelLinkRejectRec) {
		this.storeTunnelLinkRejectRec = storeTunnelLinkRejectRec;
	}
	
	@XmlElement(name = "db-query-time-out")
	@XmlJavaTypeAdapter(value = NumericAdapter.class)
	public Long getDbQueryTimeout() {
		return dbQueryTimeout;
	}
	public void setDbQueryTimeout(Long dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}
	
	@XmlElement(name = "max-query-timeout-count")
	@XmlJavaTypeAdapter(value = NumericAdapter.class)
	public Long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(Long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	
	@XmlElement(name = "multiple-value-delimiter")
	public String getMultivalDelimeter() {
		return multivalDelimeter;
	}
	public void setMultivalDelimeter(String multivalDelimeter) {
		this.multivalDelimeter = multivalDelimeter;
	}
	
	@XmlElement(name = "timestamp-field")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getDbDateField() {
		return dbDateField;
	}
	public void setDbDateField(String dbDateField) {
		this.dbDateField = dbDateField;
	}
	
	@XmlElement(name = "timestamp-enabled")
	@XmlJavaTypeAdapter(LowerCaseConvertAdapter.class)
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	
	@XmlElement(name = "cdr-identify-field")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getCdrIdDbField() {
		return cdrIdDbField;
	}
	public void setCdrIdDbField(String cdrIdDbField) {
		this.cdrIdDbField = cdrIdDbField;
	}
	
	@XmlElement(name = "cdr-sequence-name")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getCdrIdSeqName() {
		return cdrIdSeqName;
	}
	
	public void setCdrIdSeqName(String cdrIdSeqName) {
		this.cdrIdSeqName = cdrIdSeqName;
	}
	
	@XmlElement(name = "interim-identify-field")
	public String getInterimCdrIdDbField() {
		return interimCdrIdDbField;
	}
	public void setInterimCdrIdDbField(String interimCdrIdDbField) {
		this.interimCdrIdDbField = interimCdrIdDbField;
	}
	
	@XmlElement(name = "interim-sequence-name")
	public String getInterimCdrIdSeqName() {
		return interimCdrIdSeqName;
	}
	public void setInterimCdrIdSeqName(String interimCdrIdSeqName) {
		this.interimCdrIdSeqName = interimCdrIdSeqName;
	}
	
	@XmlElement(name = "call-start-field-name")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getCallStartFieldName() {
		return callStartFieldName;
	}
	public void setCallStartFieldName(String callStartFieldName) {
		this.callStartFieldName = callStartFieldName;
	}
	
	@XmlElement(name = "call-end-field-name")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getCallEndFieldName() {
		return callEndFieldName;
	}
	public void setCallEndFieldName(String callEndFieldName) {
		this.callEndFieldName = callEndFieldName;
	}
	
	@XmlElement(name = "create-date-field-name")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getCreateDateFieldName() {
		return createDateFieldName;
	}
	public void setCreateDateFieldName(String createDateFieldName) {
		this.createDateFieldName = createDateFieldName;
	}
	
	@XmlElement(name = "last-modified-date-field-name")
	@XmlJavaTypeAdapter(value = FieldTrimmerAdapter.class)
	public String getLastModifiedDateFieldName() {
		return lastModifiedDateFieldName;
	}
	public void setLastModifiedDateFieldName(String lastModifiedDateFieldName) {
		this.lastModifiedDateFieldName = lastModifiedDateFieldName;
	}
	
	@XmlElementWrapper(name = "db-acct-driver-mapping-details")
	@XmlElement(name = "db-acct-driver-mapping-detail")
	public List<DBAcctFeildMapData> getDbAcctFieldMapList() {
		return dbAcctFieldMapList;
	}

	public void setDbAcctFieldMapList(List<DBAcctFeildMapData> dbAcctFieldMapList) {
		this.dbAcctFieldMapList = dbAcctFieldMapList;
	}
	
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}

	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Database Datasource",EliteSMReferencialDAO.fetchDatabaseDatasourceData(databaseId));
		object.put("Datasource Type", datasourceType);
		object.put("DB Query Timeout (Sec.)", dbQueryTimeout);
		object.put("Maximum Query Timeout Count", maxQueryTimeoutCount);
		object.put("Multiple Value Delimiter", multivalDelimeter);
		object.put("Table Name", cdrTablename);
		object.put("Identity Field", cdrIdDbField);
		object.put("Sequence Name", cdrIdSeqName);
		object.put("Store Start Record", storeTunnelStartRec);
		object.put("Store Stop Record", storeStopRec);
		object.put("Interim CDR Table Name", interimTablename);
		object.put("Interim CDR Identity Field", interimCdrIdDbField);
		object.put("Interim CDR Sequence Name", interimCdrIdSeqName);
		object.put("Store All Interim Record", storeInterimRec);
		object.put("Remove Interim On Stop", removeInterimOnStop);
		object.put("Call Start Field Name", callStartFieldName);
		object.put("Call End Field Name", callEndFieldName);
		object.put("Create Date Field Name", createDateFieldName);
		object.put("Last Modified Date Field Name", lastModifiedDateFieldName);
		object.put("Timestamp Field", dbDateField);
		object.put("Timestamp Enabled", enabled);
		object.put("Store Start Record", storeAllCdr);
		if(dbAcctFieldMapList!=null){
			JSONObject fields = new JSONObject();
			for (DBAcctFeildMapData element : dbAcctFieldMapList) {
				fields.putAll(element.toJson());
			}
			object.put("DB Acct Driver Mapping Detail", fields);
		}
		return object;
	}
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		try {
			
			if(RestValidationMessages.INVALID.equals(this.databaseId)){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Invalid Database Datasource ");		
			}
			
			AuthenticationDetails customAuthenticationDetails = null;
			if(SecurityContextHolder.getContext().getAuthentication() != null) {
				Object obj = SecurityContextHolder.getContext().getAuthentication().getDetails();
					if(obj instanceof AuthenticationDetails){
						customAuthenticationDetails = (AuthenticationDetails) obj;
					}
					
					if(customAuthenticationDetails.isDiameter() == false ) {
						if (Strings.isNullOrBlank(this.storeAllCdr)) {
							this.storeAllCdr = "true";
						} else if (this.storeAllCdr.equalsIgnoreCase("true")) {
							this.storeAllCdr = "true";
							
						} else if (this.storeAllCdr.equalsIgnoreCase("false")) {
							this.storeAllCdr = "false";
						} else {
							RestUtitlity.setValidationMessage(context, "Invalid value for Store Start Record. Value could be 'true' or 'false'.");
							return false;
						}
					}else if (customAuthenticationDetails.isDiameter()){
						this.storeAllCdr = "true";
					}
					
			}
		
			if(Strings.isNullOrBlank(this.databaseId) == false && Strings.isNullOrEmpty(this.cdrTablename) == false && (INVALID_DB.equals(this.databaseId)) == false) {
				StringBuilder invalidFieldNames = new StringBuilder();
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				
				Set<String> dbFieldList = databaseDSBLManager.getTableFieldList(this.databaseId, this.cdrTablename);
				
				if(Collectionz.isNullOrEmpty(dbFieldList) == false) {
					
					if(Collectionz.isNullOrEmpty(this.dbAcctFieldMapList) == false){
						
						for(DBAcctFeildMapData dbAcctFeildMapData : dbAcctFieldMapList){
							
							String dbField = dbAcctFeildMapData.getDbfield();
							if(dbFieldList.contains(dbField) == false){
								
								if(dbField.isEmpty() == false){
									
									isValid = false;
									invalidFieldNames.append(dbField.trim() + ", ");
								}
							}
						}
						
						if(Strings.isNullOrEmpty(invalidFieldNames.toString()) == false){
							isValid = false;
							invalidFieldNames.insert(0, "Invalid DB Fields Name(s) : ");
							invalidFieldNames.setLength(invalidFieldNames.length() - 2);
							
							RestUtitlity.setValidationMessage(context,invalidFieldNames.toString());
							return isValid;
						}
						
						Set<String> checkDuplicateAttributeIdSet = getDuplicateAttributeIdsSet(dbAcctFieldMapList);
						if(checkDuplicateAttributeIdSet.isEmpty() == false){
							isValid = false;
							RestUtitlity.setValidationMessage(context, "Mapping with Attribute Ids "+checkDuplicateAttributeIdSet+" exits multiple times");
						}
						
						Set<String> checkDuplicateDBFieldSet = getDuplicateDBFieldSet(dbAcctFieldMapList);
						if(checkDuplicateDBFieldSet.isEmpty() == false){
							isValid = false;
							RestUtitlity.setValidationMessage(context, "Mapping with DB Fields "+checkDuplicateDBFieldSet+" exits multiple times");
							return isValid;
						}
					}

					if(Strings.isNullOrEmpty(invalidFieldNames.toString()) == false){
						isValid = false;
						invalidFieldNames.insert(0, "Invalid DB Fields Name(s) : ");
						invalidFieldNames.setLength(invalidFieldNames.length() - 2);
						RestUtitlity.setValidationMessage(context,invalidFieldNames.toString());
						return isValid;
					}
					
					Set<String> checkDuplicateAttributeIdSet = getDuplicateAttributeIdsSet(dbAcctFieldMapList);
					if(checkDuplicateAttributeIdSet.isEmpty() == false){
						isValid = false;
						RestUtitlity.setValidationMessage(context, "The mapping configured as attribute-id:"+checkDuplicateAttributeIdSet+" exists multiple times.");
					}
					
					Set<String> checkDuplicateDBFieldSet = getDuplicateDBFieldSet(dbAcctFieldMapList);
					if(checkDuplicateDBFieldSet.isEmpty() == false){
						isValid = false;
						RestUtitlity.setValidationMessage(context, "The mapping configured as db-field:"+checkDuplicateDBFieldSet+" exists multiple times.");
						return isValid;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
	}
		return isValid;
	}
	
	private Set<String> getDuplicateAttributeIdsSet(List<DBAcctFeildMapData> dbAcctFieldMapDataSet){
		Set<String> validAttributeIdSet = new HashSet<String>();
		Set<String> invalidAttributeIdSet = new HashSet<String>();

		for(DBAcctFeildMapData dbAcctFeildMapData : dbAcctFieldMapDataSet){
			String attributeid = dbAcctFeildMapData.getAttributeids();
			if (Strings.isNullOrEmpty(attributeid) == false) {
			boolean	flag = validAttributeIdSet.add(attributeid);
			if(flag == false){
				invalidAttributeIdSet.add(attributeid);
			}
			}
		}
		return invalidAttributeIdSet;
	}
	
	private Set<String> getDuplicateDBFieldSet(List<DBAcctFeildMapData> dbAcctFieldMapDataSet){
		Set<String> validDBFieldSet = new HashSet<String>();
		Set<String> invalidDBFieldSet = new HashSet<String>();

		for(DBAcctFeildMapData dbAcctFeildMapData : dbAcctFieldMapDataSet){
			String dbField = dbAcctFeildMapData.getDbfield();
			if (Strings.isNullOrEmpty(dbField) == false) {
			boolean	flag = validDBFieldSet.add(dbField);
			if(flag == false){
				invalidDBFieldSet.add(dbField);
			}
			}
		}
		return invalidDBFieldSet;
	}
}