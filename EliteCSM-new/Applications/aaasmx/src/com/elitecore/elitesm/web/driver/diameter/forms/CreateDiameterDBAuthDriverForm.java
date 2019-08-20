package com.elitecore.elitesm.web.driver.diameter.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthFieldMapData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class CreateDiameterDBAuthDriverForm extends BaseWebForm{
		
	private long dbAuthId;
	private String databaseId;
	//private long dbScanTime;
	private String tableName = "TBLRADIUSCUSTOMER";
	private long dbQueryTimeout = 2;
	private long maxQueryTimeoutCount = 200;
	private long driverInstanceId;
	private List databaseDSList;
	private String name;
	private String logicalName;
	private String dbFiled;
	private List<LogicalNameValuePoolData> logicalNameList;
	private String action;
	private int itemIndex;
	private int count;
	
	///request Parameters
	private String defaultValue;
	private String valueMapping;
	private String driverInstanceName;
	private String driverDesp;
	
	private String userIdentityAttributes;
	private String profileLookupColumn = "USER_IDENTITY";
	
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getValueMapping() {
		return valueMapping;
	}
	public void setValueMapping(String valueMapping) {
		this.valueMapping = valueMapping;
	}
	private String driverRelatedId;
	
	
	public long getDbAuthId() {
		return dbAuthId;
	}
	public void setDbAuthId(long dbAuthId) {
		this.dbAuthId = dbAuthId;
	}
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
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public long getDbQueryTimeout() {
		return dbQueryTimeout;
	}
	public void setDbQueryTimeout(long dbQueryTimeout) {
		this.dbQueryTimeout = dbQueryTimeout;
	}
	public long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	public long getDriverInstanceId() {
		return driverInstanceId;
	}
	public void setDriverInstanceId(long driverInstanceId) {
		this.driverInstanceId = driverInstanceId;
	}
	public List getDatabaseDSList() {
		return databaseDSList;
	}
	public void setDatabaseDSList(List databaseDSList) {
		this.databaseDSList = databaseDSList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogicalName() {
		return logicalName;
	}
	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}
	public String getDbFiled() {
		return dbFiled;
	}
	public void setDbFiled(String dbFiled) {
		this.dbFiled = dbFiled;
	}
	public List<LogicalNameValuePoolData> getLogicalNameList() {
		return logicalNameList;
	}
	public void setLogicalNameList(List<LogicalNameValuePoolData> logicalNameList) {
		this.logicalNameList = logicalNameList;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDriverInstanceName() {
		return driverInstanceName;
	}
	public void setDriverInstanceName(String driverInstanceName) {
		this.driverInstanceName = driverInstanceName;
	}
	public String getDriverDesp() {
		return driverDesp;
	}
	public void setDriverDesp(String driverDesp) {
		this.driverDesp = driverDesp;
	}
	public String getDriverRelatedId() {
		return driverRelatedId;
	}
	public void setDriverRelatedId(String driverRelatedId) {
		this.driverRelatedId = driverRelatedId;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getUserIdentityAttributes() {
		return userIdentityAttributes;
	}
	public void setUserIdentityAttributes(String userIdentityAttributes) {
		this.userIdentityAttributes = userIdentityAttributes;
	}
	
	public List<DBAuthFieldMapData> getDefaultmapping(){
		List<DBAuthFieldMapData> defaultMappingList = new ArrayList<DBAuthFieldMapData>();
		String[] logicalnm = {"User Name","CUI","User Password","Password Check","Encryption Type","Customer Status","Calling Station ID"
                  		      ,"Authorization Policy","Customer Check Items","Customer Reply Items","Expiry Date","Credit Limit"};
		String[] fieldMap = {"USERNAME","CUI","PASSWORD","PASSWORDCHECK","ENCRYPTIONTYPE","CUSTOMERSTATUS","CALLINGSTATIONID","DIAMETERPOLICY"
                		    ,"CUSTOMERCHECKITEM","CUSTOMERREPLYITEM","EXPIRYDATE","CREDITLIMIT"};
		String[] logicalv = {"User-Name","CUI","User-Password","PasswordCheck","EncryptionType","CustomerStatus","Calling-Station-ID","AuthorizationPolicy"
        					,"CustomerCheckItems","CustomerReplyItems","ExpiryDate","CreditLimit"};
		for(int index = 0 ; index < logicalnm.length ; index++){
			DBAuthFieldMapData dbAuthFieldMapData = new DBAuthFieldMapData();
			LogicalNameValuePoolData nameValuePoolData = new LogicalNameValuePoolData();
			nameValuePoolData.setName(logicalnm[index]);
			nameValuePoolData.setValue(logicalv[index]);
			dbAuthFieldMapData.setNameValuePoolData(nameValuePoolData);
			dbAuthFieldMapData.setDbField(fieldMap[index]);
			defaultMappingList.add(dbAuthFieldMapData);
		}
		return defaultMappingList;
	}
	public String getProfileLookupColumn() {
		return profileLookupColumn;
	}
	public void setProfileLookupColumn(String profileLookupColumn) {
		this.profileLookupColumn = profileLookupColumn;
	}
	
	
}
