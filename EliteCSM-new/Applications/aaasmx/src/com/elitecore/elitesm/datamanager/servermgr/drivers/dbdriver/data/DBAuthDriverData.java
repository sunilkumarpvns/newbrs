package com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sf.json.JSONObject;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.aaa.util.constants.DriverConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverResult;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.util.DriverUtility;
import com.elitecore.elitesm.util.FieldWithLogicalNameValidateUtility;
import com.elitecore.elitesm.util.ResultObject;
import com.elitecore.elitesm.util.constants.DriverTypeConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.DatabaseDatasourceNameAdapter;
import com.elitecore.elitesm.ws.rest.adapter.NumericAdapter;
import com.elitecore.elitesm.ws.rest.security.AuthenticationDetails;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name = "db-auth-driver")
@XmlType(propOrder = {"databaseId" , "tableName", "dbQueryTimeout", "maxQueryTimeoutCount", "profileLookupColumn", "userIdentityAttributes","cacheable", "primaryKeyColumn"
		,"sequenceName", "dbFieldMapList" })
@ValidObject
public class DBAuthDriverData extends BaseData implements IDBAuthDriverData,Serializable,Differentiable,Validator{
	
	public DBAuthDriverData() {
		this.cacheable = DriverConstants.CACHEABLE;
	}
	
	private static final long serialVersionUID = 1L;
	
	private String dbAuthId;
	
	@Expose
	@SerializedName("Datasource")
	@NotEmpty(message = "Database Datasource must be specified")
	private String databaseId;
	
	@Expose
	@SerializedName("Table Name")
	@NotEmpty(message = "Table Name must be specified")
	private String tableName;
	
	@Expose
	@SerializedName("DB Query Timeout Sec")
	@NotNull(message = "Query Timeout must be specified")
	@Min(value = 0, message = "DB Query Timeout must be numeric")
	private Long dbQueryTimeout;
	
	@Expose
	@SerializedName("Maximum Query Timeout Count")
	@NotNull(message = "Max Query timeout count must be specified")
	@Min(value = 0, message = "Maximum Query Timeout must be numeric")
	private Long maxQueryTimeoutCount;
	
	@Expose
	@SerializedName("Profile Lookup Column")
	@NotEmpty(message = "Profile Lookup Column must be specified")
	private String profileLookupColumn;
	
	@Expose
	@SerializedName("User Identity Attributes")
	private String userIdentityAttributes;
	
	@Expose
	@SerializedName("Primary Key Column")
	private String primaryKeyColumn;
	
	@Expose
	@SerializedName("Sequence Name")
	private String sequenceName;
	
	@Expose
	@SerializedName("Cacheable")
	private String cacheable;
	
	private boolean checkValidate;
	
	private String driverInstanceId;
	
	@NotEmpty(message = "At least one mapping must be there.")
	private List<DBAuthFieldMapData> dbFieldMapList;

	@XmlTransient
	private Set<IDatasourceSchemaData> datasourceSchemaSet;
	
	@XmlElement(name = "primary-key-column")
	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	public void setPrimaryKeyColumn(String primaryKeyColumn) {
		this.primaryKeyColumn = primaryKeyColumn;
	}
	
	@XmlElement(name = "sequence-name")
	public String getSequenceName() {
		return sequenceName;
	}
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
	@XmlTransient
	public String getDbAuthId() {
		return dbAuthId;
	}
	
	public void setDbAuthId(String dbAuthId) {
		this.dbAuthId = dbAuthId;
	}
	
	@XmlElement(name = "database-datasource")
	@XmlJavaTypeAdapter(value = DatabaseDatasourceNameAdapter.class)
	public String getDatabaseId() {
		return databaseId;
	}
	
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	/*public long getDbScanTime() {
		return dbScanTime;
	}
	public void setDbScanTime(long dbScanTime) {
		this.dbScanTime = dbScanTime;
	}*/
	@XmlElement(name = "table-name")
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@XmlElement(name = "db-query-timeout")
	@XmlJavaTypeAdapter(value = NumericAdapter.class)
	public Long getDbQueryTimeout() {
		return dbQueryTimeout;
	}
	public void setDbQueryTimeout(Long dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}
	
	@XmlElement(name = "maximum-query-timeout-count")
	@XmlJavaTypeAdapter(value = NumericAdapter.class)
	public Long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(Long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	@XmlTransient
	public String getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(String driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	
	@Valid
	@XmlElementWrapper(name = "db-field-mappings")
	@XmlElement(name = "db-field-mapping")
	public List<DBAuthFieldMapData> getDbFieldMapList() {
		return dbFieldMapList;
	}
	public void setDbFieldMapList(List<DBAuthFieldMapData> dbFieldMapList) {
		this.dbFieldMapList = dbFieldMapList;
	}
	
	@XmlTransient
	public Set<IDatasourceSchemaData> getDatasourceSchemaSet() {
		return datasourceSchemaSet;
	}
	
	public void setDatasourceSchemaSet(Set<IDatasourceSchemaData> datasourceSchemaSet) {
		this.datasourceSchemaSet = datasourceSchemaSet;
	}
	
	@XmlElement(name = "profile-lookup-column")
	public String getProfileLookupColumn() {
		return profileLookupColumn;
	}
	
	public void setProfileLookupColumn(String profileLookupColumn) {
		this.profileLookupColumn = profileLookupColumn;
	}
	
	@XmlElement(name = "user-identity-attributes")
	public String getUserIdentityAttributes() {
		return userIdentityAttributes;
	}
	
	public void setUserIdentityAttributes(String userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}
	
	@XmlElement(name = "cacheable")
	public String getCacheable() {
		return cacheable;
	}
	public void setCacheable(String cacheable) {
		this.cacheable = cacheable;
	}
	
	@XmlTransient
	public boolean isCheckValidate() {
		return checkValidate;
	}
	public void setCheckValidate(boolean checkValidate) {
		this.checkValidate = checkValidate;
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Database Datasource", EliteSMReferencialDAO.fetchDatabaseDatasourceData(databaseId));
		object.put("Table Name", tableName);
		object.put("DB Query Timeout (Sec.)", dbQueryTimeout);
		object.put("Maximum Query Timeout Count", maxQueryTimeoutCount);
		object.put("Profile Lookup Column", profileLookupColumn);
		object.put("User Identity Attributes", userIdentityAttributes);
		object.put("Primary Key Column", primaryKeyColumn);
		object.put("Sequence Name", sequenceName);
		if (dbFieldMapList != null) {
			JSONObject fields = new JSONObject();
			for (DBAuthFieldMapData element : dbFieldMapList) {
				fields.putAll(element.toJson());
			}
			object.put("DB Field Mapping", fields);
		}
		return object;
	}
	
	/**
	*Contains code that will check 
	*weather the logical name does exist and unique (except in Multiple allowed logical name) with default logical name.
	*It also validate if the combination of multiple allowed logical name and DB field value twice then response with proper message.
	*@author Tejas.p.Shah
	*/
	
	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;

		AuthenticationDetails customAuthenticationDetails = null;
		List<String> logicalNameMultipleAllowList = null;
		List<LogicalNameValuePoolData> multipleLogicalNameRelList = null;
		DriverBLManager driverBLManager = new DriverBLManager();
			
		try {
			
			if(RestValidationMessages.INVALID.equals(this.databaseId)){
				isValid = false;
				RestUtitlity.setValidationMessage(context, "Invalid Database Datasource ");	
			}
			
			if(Collectionz.isNullOrEmpty(dbFieldMapList) == false && checkValidate == false){
				if(SecurityContextHolder.getContext().getAuthentication() != null) {
					Object obj = SecurityContextHolder.getContext().getAuthentication().getDetails();
					
					if(SecurityContextHolder.getContext().getAuthentication() !=null) {
						if(obj instanceof AuthenticationDetails){
							customAuthenticationDetails = (AuthenticationDetails) obj;
						}
						
						List<LogicalNameValuePoolData> logicalNameList = driverBLManager.getLogicalNameValuePoolList();
						
						if(customAuthenticationDetails.isDiameter()){
							logicalNameMultipleAllowList = driverBLManager.getLogicalNameDriverRelList(DriverTypeConstants.DIAMETER_DB_AUTH_DRIVER);
							multipleLogicalNameRelList = driverBLManager.getMultipleLogicalNameRelList(DriverTypeConstants.DIAMETER_DB_AUTH_DRIVER);
						}else{
							logicalNameMultipleAllowList = driverBLManager.getLogicalNameDriverRelList(DriverTypeConstants.RADIUS_DB_AUTH_DRIVER);
							multipleLogicalNameRelList = driverBLManager.getMultipleLogicalNameRelList(DriverTypeConstants.RADIUS_DB_AUTH_DRIVER);
						}
						Set<String> uniqueLogicalNameSet = new HashSet<String>();
						DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
						
						StringBuilder duplicateLogicalNames = new StringBuilder();
						StringBuilder invalidLogicalNames = new StringBuilder();
						StringBuilder invalidFieldNames = new StringBuilder();
						StringBuilder combinationLogicalName =new StringBuilder();
						
						
						Map<String,String> validateMultipleAllowMap = new HashMap<String, String>();
						
						Set<String> dbFieldList = databaseDSBLManager.getTableFieldList(this.databaseId, this.tableName);
						for(DBAuthFieldMapData dbAuthFieldMapData : dbFieldMapList){
							String logicalName = null , dbField = null;
							if(Strings.isNullOrBlank(dbAuthFieldMapData.getLogicalName()) == false && Strings.isNullOrBlank(dbAuthFieldMapData.getDbField()) == false) {
								logicalName = dbAuthFieldMapData.getLogicalName().trim();
								dbField = dbAuthFieldMapData.getDbField().trim();
							}
							if(Collectionz.isNullOrEmpty(logicalNameMultipleAllowList) == false && Collectionz.isNullOrEmpty(logicalNameList) ==false) {
								List<String> uniqueLogicalNameList = DriverUtility.getUniqueLogicalMultipleAllowList(multipleLogicalNameRelList, logicalNameList);
								if(Collectionz.isNullOrEmpty(uniqueLogicalNameList) == false){
									ResultObject resObj = FieldWithLogicalNameValidateUtility.checkFieldWithLogicalNameValidate(validateMultipleAllowMap, uniqueLogicalNameSet,uniqueLogicalNameList, logicalNameMultipleAllowList,
											duplicateLogicalNames, invalidLogicalNames, logicalName, dbField,  context);
									if(resObj.isError()){
										combinationLogicalName.append(resObj.getErrorMsg() + ", ");
									}
								}
							}
							if(Collectionz.isNullOrEmpty(dbFieldList) == false) {
								if(dbFieldList.contains(dbField) == false && Strings.isNullOrEmpty(dbField) == false){
									isValid = false;
									invalidFieldNames.append(dbField.trim() + ", ");
								}
							}
						}
						DriverResult driverResult = new DriverResult();
						List<DBAuthFieldMapData> convertedList = getConversionOfValueWithLogicalName(context, dbFieldMapList,driverResult, logicalNameList);
						
						if(driverResult.isError()){
							isValid = false;
						}
						
						if(Collectionz.isNullOrEmpty(convertedList) == false) {
							dbFieldMapList.clear();
							dbFieldMapList.addAll(convertedList);
							
						}
						
						if(Strings.isNullOrEmpty(duplicateLogicalNames.toString()) == false ){
							duplicateLogicalNames.insert(0, "Duplicate Logical Name(s) : ");
							duplicateLogicalNames.setLength(duplicateLogicalNames.length() - 2);
							isValid = false;
							RestUtitlity.setValidationMessage(context,duplicateLogicalNames.toString());
						}
						if(Strings.isNullOrEmpty(invalidLogicalNames.toString()) == false){
							invalidLogicalNames.insert(0, "Invalid Logical Name(s) : ");
							invalidLogicalNames.setLength(invalidLogicalNames.length() - 2);
							isValid = false;
							RestUtitlity.setValidationMessage(context,invalidLogicalNames.toString());
						}
						if(Strings.isNullOrEmpty(invalidFieldNames.toString()) == false){
							invalidFieldNames.insert(0, "Invalid DB Fields Name(s) : ");
							invalidFieldNames.setLength(invalidFieldNames.length() - 2);
							isValid = false;
							RestUtitlity.setValidationMessage(context,invalidFieldNames.toString());
						}
						if(Strings.isNullOrEmpty(combinationLogicalName.toString()) == false){
							isValid = false;
							RestUtitlity.setValidationMessage(context, combinationLogicalName.toString());
						}
					}
				}
			}
		}catch (DataManagerException e) {
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		return isValid;
	}
	
	
	private List<DBAuthFieldMapData> getConversionOfValueWithLogicalName(ConstraintValidatorContext context, List<DBAuthFieldMapData> dbFieldMapList, DriverResult driverResult, List<LogicalNameValuePoolData> logicalNameList) {
		
		List<DBAuthFieldMapData> convertedList = new ArrayList<DBAuthFieldMapData>();
		for(DBAuthFieldMapData logicalNameValuePoolData : dbFieldMapList ){
			
			 if (Strings.isNullOrEmpty(logicalNameValuePoolData.getLogicalName()) && Strings.isNullOrEmpty(logicalNameValuePoolData.getDbField())) {
			      RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name and db-field must be specified.");
			      driverResult.setError(true);
			  } else if (Strings.isNullOrEmpty(logicalNameValuePoolData.getDbField()) && Strings.isNullOrEmpty(logicalNameValuePoolData.getLogicalName()) == false) {
			      RestUtitlity.setValidationMessage(context, "In the Mapping List, the db-field must be specified for logical-name:[" + logicalNameValuePoolData.getLogicalName()+ "].");
			      driverResult.setError(true);

			  } else if (Strings.isNullOrEmpty(logicalNameValuePoolData.getLogicalName()) && Strings.isNullOrEmpty(logicalNameValuePoolData.getDbField()) == false) {
			      RestUtitlity.setValidationMessage(context, "In the Mapping List, the logical-name must be specified for db-field:[" +logicalNameValuePoolData.getDbField()+ "].");
			      driverResult.setError(true);
			  }
			
			 if(driverResult.isError() == false) {
				 DBAuthFieldMapData dbAuthData = new DBAuthFieldMapData();
				 for(LogicalNameValuePoolData data: logicalNameList) {
					 if(data.getName().equals(logicalNameValuePoolData.getLogicalName())) {
						 dbAuthData.setLogicalName(data.getValue());
						 dbAuthData.setDbAuthId(logicalNameValuePoolData.getDbAuthId());
						 dbAuthData.setDbField(logicalNameValuePoolData.getDbField());
						 dbAuthData.setDbFieldMapId(logicalNameValuePoolData.getDbFieldMapId());
						 dbAuthData.setDefaultValue(logicalNameValuePoolData.getDefaultValue());
						 dbAuthData.setNameValuePoolData(logicalNameValuePoolData.getNameValuePoolData());
						 dbAuthData.setValueMapping(logicalNameValuePoolData.getValueMapping());
						 convertedList.add(dbAuthData);
						 break;
					 }
				 }
			 }
			 
		}
		return convertedList;
	}
	
}